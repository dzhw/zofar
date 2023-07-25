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
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import de.his.zofar.presentation.surveyengine.security.concurrentSession.ConcurrentSessionManager;
public class SessionSecurityListener implements HttpSessionListener {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SessionSecurityListener.class);
	public SessionSecurityListener() {
		super();
		LOGGER.info("start SessionSecurityListener {}",this.hashCode());
	}
	private ConcurrentSessionManager getManager(final HttpSession session){
		final ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		final ConcurrentSessionManager manager = (ConcurrentSessionManager) ctx.getBean("concurrentSessionManager");
		return manager;
	}
	@Override
	public void sessionCreated(final HttpSessionEvent se) {
	}
	@Override
	public void sessionDestroyed(final HttpSessionEvent se) {
		if(LOGGER.isDebugEnabled())LOGGER.debug("Session destroyed {} (Manager : {})", se.getSession().getId(),getManager(se.getSession()));
		getManager(se.getSession()).remove(se.getSession().getId());
	}
}
