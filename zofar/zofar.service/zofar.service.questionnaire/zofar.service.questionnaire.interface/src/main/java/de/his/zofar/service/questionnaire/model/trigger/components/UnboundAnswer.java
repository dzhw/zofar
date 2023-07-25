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
/**
 *
 */
package de.his.zofar.service.questionnaire.model.trigger.components;

import de.his.zofar.service.question.model.interfaces.Answer;
import de.his.zofar.service.valuetype.model.Variable;


/**
 * @author meisner
 *
 */
public class UnboundAnswer implements Answer {

	private static final long serialVersionUID = -6404758851471974763L;
	private final Variable variable;
	private Object value;
	private final String label;

	public UnboundAnswer(final Variable variable,final String label) {
		super();
		this.variable = variable;
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see de.his.hiob.model.question.models.interfaces.Answer#setAnswerValue(java.lang.Object)
	 */
	@Override
	public void setAnswerValue(final Object value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see de.his.hiob.model.question.models.interfaces.Answer#getAnswerValue()
	 */
	@Override
	public Object getAnswerValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see de.his.hiob.model.question.models.interfaces.Answer#getAnswerLabel()
	 */
	@Override
	public String getAnswerLabel() {
		return this.label;
	}

	/* (non-Javadoc)
	 * @see de.his.hiob.model.question.models.interfaces.Answer#getAnswerVariable()
	 */
	@Override
	public Variable getAnswerVariable() {
		return this.variable;
	}

}
