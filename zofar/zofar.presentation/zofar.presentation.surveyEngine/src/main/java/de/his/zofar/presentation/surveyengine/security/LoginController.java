/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package de.his.zofar.presentation.surveyengine.security;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import de.his.zofar.presentation.surveyengine.security.concurrentSession.ConcurrentSessionManager;
import de.his.zofar.presentation.surveyengine.util.SystemConfiguration;
/**
 * @author meisner
 * 
 */
@Controller("loginController")
@Scope("request")
public class LoginController implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6275198735859938836L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(LoginController.class);
    @Inject
    private ProviderManager participantAuthenticationManager = null;
    @Inject
    private SessionRegistry sessionRegistry = null;
    @Inject
    private AnonymousCreator anonymousCreator = null;
	public LoginController() {
		super();
	}
	public String loginMode(){
		final SystemConfiguration system = SystemConfiguration.getInstance();
		return system.loginMode();
	}
	public ProviderManager getParticipantAuthenticationManager() {
		return participantAuthenticationManager;
	}
	public void setParticipantAuthenticationManager(
			ProviderManager participantAuthenticationManager) {
		this.participantAuthenticationManager = participantAuthenticationManager;
	}
	public SessionRegistry getSessionRegistry() {
		return sessionRegistry;
	}
	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
	public AnonymousCreator getAnonymousCreator() {
		return anonymousCreator;
	}
	public void setAnonymousCreator(AnonymousCreator anonymousCreator) {
		this.anonymousCreator = anonymousCreator;
	}
	private ConcurrentSessionManager getConcurrentSessionManager(){
        FacesContext context = FacesContext.getCurrentInstance();
        ConcurrentSessionManager manager = (ConcurrentSessionManager) context.getApplication().evaluateExpressionGet(context, "#{concurrentSessionManager}", ConcurrentSessionManager.class);
		return manager;
	}
	public void directLogin(final String token) {
		this.directLogin(token,false);
	}
	public void reLogin() {
		final SystemConfiguration system = SystemConfiguration.getInstance();
		if (!system.cluster())
			return;
		final FacesContext context = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = context.getExternalContext();
		final HttpServletRequest httpRequest = (HttpServletRequest) externalContext.getRequest();
		if (httpRequest != null) {
			final Cookie[] cookies = httpRequest.getCookies();
			int count = 0;
			String oldId = null;
			if (cookies != null) {
				for (final Cookie cookie : cookies) {
					if (cookie.getName().equals("relocation_sid")) {
						if ((cookie.getValue() != null) && (!cookie.getValue().equals(""))) {
							oldId = cookie.getValue();
							count = count + 1;
						}
					}
				}
			} 
			if (count == 1) {
				LOGGER.info("relogin {}", oldId);
				if ((oldId != null) && (!oldId.equals(""))) {
					try{
						final String token = getConcurrentSessionManager().get(oldId);
						LOGGER.info("found token in SessionMap {}", token);
						if (token != null)
							this.directLogin(token, true);
					}
					catch (Exception e) {
			        	LOGGER.error("Catched ReLogin Exception:", e);
					}
				}
			} 
			else if(count > 1){
				LOGGER.error("More than one relocation_sid cookie found ==> relogin skipped");
			}
		}
	}
	public boolean isReLogin() {
		final FacesContext context = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = context.getExternalContext();
		final HttpServletRequest httpRequest = (HttpServletRequest) externalContext.getRequest();
		final HttpServletResponse httpResponse = (HttpServletResponse) externalContext.getResponse();
		if (httpRequest != null) {
			final Cookie[] cookies = httpRequest.getCookies();
			int count = 0;
			String oldId = null;
			if (cookies != null) {
				for (final Cookie cookie : cookies) {
					if (cookie.getName().equals("relocation_sid")) {
						if ((cookie.getValue() != null) && (!cookie.getValue().equals(""))) {
							oldId = cookie.getValue();
							count = count + 1;
							cookie.setMaxAge(-1);
							cookie.setHttpOnly(true);
							cookie.setValue("");
							httpResponse.addCookie(cookie);
						}
					}
				}
			} else {
			}
			if (count == 1) {
				if ((oldId != null) && (!oldId.equals(""))) {
					return true;
				}
			} else if (count > 1) {
			}
		} else {
		}
		return false;
	}
	public void directLogin(String token,final boolean relogin) {
		final FacesContext context = FacesContext.getCurrentInstance();
		if (token == null) {
			token = "";
		}
		token = token.trim();
		if (!token.equals("")) {
        	final ExternalContext externalContext = context	.getExternalContext();
        	final RedirectStrategy redirectHandler = new DefaultRedirectStrategy();
			final HttpServletRequest httpRequest = (HttpServletRequest) externalContext.getRequest();
        	httpRequest.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    		int timeoutIntervall = -1;
        	Object session = externalContext.getSession(false);
    		if(session != null){
    			timeoutIntervall = ((HttpSession)session).getMaxInactiveInterval();
    			((HttpSession)session).setMaxInactiveInterval(30);
    		}
			final HttpServletResponse httpResponse = (HttpServletResponse) externalContext.getResponse();
			try {
	            Authentication request = new UsernamePasswordAuthenticationToken(token, token);
	            Authentication result = participantAuthenticationManager.authenticate(request);
	            SecurityContextHolder.getContext().setAuthentication(result);
	            if(result.isAuthenticated()){
	            	final List<SessionInformation> knownSessions = sessionRegistry.getAllSessions(result.getPrincipal(), false);
	            	if((knownSessions != null)&&(!knownSessions.isEmpty())){
	            		Iterator<SessionInformation> it = knownSessions.iterator();
	            		while(it.hasNext()){
	            			final SessionInformation knownSession= it.next();
	            			LOGGER.info("kill known Session : {}", knownSession.getSessionId());
	            			knownSession.expireNow();
	            		}
	            	}
	            	if(timeoutIntervall > 0)((HttpSession)session).setMaxInactiveInterval(timeoutIntervall);
	            	sessionRegistry.registerNewSession(RequestContextHolder.currentRequestAttributes().getSessionId(), result.getPrincipal());
	            	redirectHandler.sendRedirect(httpRequest, httpResponse, "/index.html");
	            	getConcurrentSessionManager().set(token, RequestContextHolder.currentRequestAttributes().getSessionId(),relogin);
	            }
	        } catch (AuthenticationException e) {
            		LOGGER.info("Authentication failed: {}", token);
	            	try {
						redirectHandler.sendRedirect(httpRequest, httpResponse, "/special/login.html?message=AuthentificationFailed&zofar_token="+token);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	        } catch (Exception e) {
	        	LOGGER.error("DirectLogin Exception ({}) : {}", token,e.getMessage());
			}
		}
	}
	public void anonymLogin(final String captchaId,final String captchaText) {
		final AnonymousCreator anonymous=  this.getAnonymousCreator();
		LOGGER.info("anonymous: {}", anonymous);
		if(anonymous != null){
			if(anonymous.checkCaptcha(captchaId, captchaText)){
				String token = anonymous.createAnonymousAccount();
				this.directLogin(token);
			}
		}
	}
	public void anonymLogin(final String captchaId,final String captchaText, final String injectedPID) {
		final AnonymousCreator anonymous= this.getAnonymousCreator();
		LOGGER.info("anonymous: {}", anonymous);
		if(anonymous != null){
			if(anonymous.checkCaptcha(captchaId, captchaText)){
				String token = anonymous.createAnonymousAccount();
				this.directLogin(token);
		    	if(injectedPID != null) {
			    	final String cleanedPID = injectedPID.strip();
			    	if(!cleanedPID.contentEquals("")) {
						final FacesContext fc = FacesContext.getCurrentInstance();
				    	final ExternalContext externalContext = fc.getExternalContext();
				    	Object session = externalContext.getSession(false);
				    	if(session != null) {
				    		HttpSession tmpSession = (HttpSession)session;
				    		System.out.println("Session ID "+tmpSession.getId());
				    		tmpSession.setAttribute("zofar_pid", injectedPID);
				    	}
			    	}
		    	}
			}
		}
	}
	public String url() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext
				.getRequest();
		final String referer = httpServletRequest.getHeader("referer");
		final String forwarded = httpServletRequest.getHeader("X-Forwarded-Host");
		String back = referer;
		if(back == null)back = forwarded;
		if(back == null)back = "UNKOWN";
		return back;
	}
	public boolean startwith(final String haystack,final String pattern){
		if(haystack == null)return false;
		if(pattern == null)return false;
		if(haystack.startsWith(pattern))return true;
		return false;
	}
	public boolean contains(final String haystack,final String pattern){
		if(haystack == null)return false;
		if(pattern == null)return false;
		if(haystack.contains(pattern))return true;
		return false;
	}
}
