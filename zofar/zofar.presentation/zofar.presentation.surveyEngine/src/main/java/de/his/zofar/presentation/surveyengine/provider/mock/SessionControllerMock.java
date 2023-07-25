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
package de.his.zofar.presentation.surveyengine.provider.mock;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import de.his.zofar.presentation.surveyengine.controller.SessionController;
import de.his.zofar.service.surveyengine.model.Participant;
public class SessionControllerMock extends SessionController {
	private static final long serialVersionUID = -6500362184815458551L;
	private Locale locale;
	private Participant participant;
	private Map<String,String> valueMap;
	public SessionControllerMock() {
		super(false);
		locale = new Locale("de");
		participant = new Participant();
		valueMap = new HashMap<String,String>();
	}
	@Override
	public Locale getCurrentLocale() {
		return this.locale;
	}
	@Override
	public Participant getParticipant() {
		return this.participant;
	}
	@Override
	public String getValueForVariablename(String variableName) {
		return valueMap.get(variableName);
	}
	@Override
	public void processAction(ActionEvent actionEvent)
			throws AbortProcessingException {
	}
	@Override
	public void setValueForVariablename(String variableName, String value) {
		valueMap.put(variableName,value);
	}
	@Override
	public String switchCurrentLocale(String locale) {
		return "";
	}
}
