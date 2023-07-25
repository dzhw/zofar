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
package de.his.zofar.service.questionnaire.model.trigger;

import java.io.Serializable;

/**
 * @author meisner
 * 
 */

public abstract class Trigger implements Serializable {

	private static final long serialVersionUID = -3326888381422769905L;
	
	private boolean forward = false;
	private boolean backward = false;
	private String condition;

	protected Trigger() {
		super();
	}

	public boolean isForward() {
		return forward;
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public boolean isBackward() {
		return backward;
	}

	public void setBackward(boolean backward) {
		this.backward = backward;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public abstract Object proceed();

}
