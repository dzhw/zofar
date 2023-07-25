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
package eu.dzhw.zofar.testing.condition.components;

import java.io.Serializable;

import eu.dzhw.zofar.testing.condition.term.elements.Element;

public class SessionController extends Element implements Serializable{
	
	private static final long serialVersionUID = -3085929420135475321L;
	private final static SessionController INSTANCE = new SessionController();
	
	public Object participant;
	public Object currentLocale;

	private SessionController() {
		super("SessionController Dummy");
	}

	public static SessionController getInstance() {
		return INSTANCE;
	}

	public Object getParticipant() {
		return participant;
	}

	public void setParticipant(Object participant) {
		this.participant = participant;
	}

	public Object getCurrentLocale() {
		return currentLocale;
	}

	public void setCurrentLocale(Object currentLocale) {
		this.currentLocale = currentLocale;
	}

	
	
	


}
