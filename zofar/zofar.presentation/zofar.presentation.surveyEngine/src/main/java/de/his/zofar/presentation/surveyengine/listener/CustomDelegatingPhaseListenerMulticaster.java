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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.jsf.DelegatingPhaseListenerMulticaster;
public class CustomDelegatingPhaseListenerMulticaster extends
		DelegatingPhaseListenerMulticaster {
	private static final long serialVersionUID = 551284545920774560L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomDelegatingPhaseListenerMulticaster.class);
	private List<PhaseListener> ownListeners;
	public CustomDelegatingPhaseListenerMulticaster() {
		super();
		ownListeners = new ArrayList<PhaseListener>();
		ownListeners.add(new CacheListener());
	}
	@Override
	protected Collection<PhaseListener> getDelegates(FacesContext facesContext) {
		return ownListeners;
	}
}
