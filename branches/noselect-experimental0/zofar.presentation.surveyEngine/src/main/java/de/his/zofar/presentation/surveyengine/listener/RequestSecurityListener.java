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
package de.his.zofar.presentation.surveyengine.listener;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import de.his.zofar.presentation.surveyengine.security.concurrentSession.ConcurrentSessionManager;
import de.his.zofar.service.surveyengine.model.ParticipantPrincipal;
public class RequestSecurityListener implements PhaseListener {
	private static final long serialVersionUID = 1553996346970195722L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RequestSecurityListener.class);
	public RequestSecurityListener() {
		super();
		LOGGER.info("created "+this.hashCode());
	}
	private ConcurrentSessionManager getManager() {
		final FacesContext context = FacesContext.getCurrentInstance();
		final ConcurrentSessionManager manager = context
				.getApplication().evaluateExpressionGet(context,
						"#{concurrentSessionManager}",
						ConcurrentSessionManager.class);
		return manager;
	}
	@Override
	public void afterPhase(final PhaseEvent event) {
	}
	@Override
	public void beforePhase(final PhaseEvent event) {
		final HttpServletRequest request = (HttpServletRequest)event.getFacesContext().getExternalContext().getRequest();
		if(request.getMethod().equals("POST")){
			final HttpSession session = (HttpSession) event.getFacesContext().getExternalContext().getSession(false);
			if (LOGGER.isDebugEnabled())LOGGER.debug("check post {} ({})", request.getHeader("Location"),session.getId());
			if(session != null){
				final SecurityContext context = SecurityContextHolder.getContext();
				if (context != null) {
					final Authentication authentication = context.getAuthentication();
					if (authentication != null) {
						final ParticipantPrincipal participant = (ParticipantPrincipal) context.getAuthentication().getPrincipal();
						if (!getManager().check(participant.getUsername(), session.getId())) {
							final HttpServletResponse response = (HttpServletResponse)event.getFacesContext().getExternalContext().getResponse();
							final String location = response.getHeader("Location");
							if (LOGGER.isDebugEnabled())LOGGER.debug(" response => {} for {}",location,authentication.getName());
							boolean loop = false;
							if(location != null){
								if(location.endsWith("/j_spring_security_logout"))loop = true;
							}
							if(!loop){
								getManager().logout(session,authentication,request,response);
								return;
							}
							else if (LOGGER.isDebugEnabled())LOGGER.debug(" response target loop => {} for {}",location,authentication.getName());
						}
					}
				}
			}
		}
	}
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}
