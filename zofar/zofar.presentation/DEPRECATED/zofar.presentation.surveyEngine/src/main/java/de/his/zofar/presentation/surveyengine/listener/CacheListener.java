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
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Used to suspress Browser-Caching
 * 
 * @author meisner
 *
 */
public class CacheListener implements PhaseListener {
	private static final long serialVersionUID = 598946879776647822L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CacheListener.class);
   /* (non-Javadoc)
 * @see javax.faces.event.PhaseListener#getPhaseId()
 */
@Override
	public PhaseId getPhaseId()
    {
        return PhaseId.RENDER_RESPONSE;
    }
   @Override
    public void afterPhase(final PhaseEvent event)
    {
    }
   /* 
* Suppress use of Browser-Cache
 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
 */
@Override
    public void beforePhase(final PhaseEvent event)
    {
        final FacesContext facesContext = event.getFacesContext();
        final HttpServletResponse response = (HttpServletResponse) facesContext
                .getExternalContext().getResponse();
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.addHeader("Expires", "0");
    }
}
