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
package de.his.zofar.service.questionnaire.model.trigger.concrete;

import de.his.zofar.service.question.model.interfaces.Answer;
import de.his.zofar.service.questionnaire.model.trigger.Trigger;
import de.his.zofar.service.questionnaire.model.trigger.components.UnboundAnswer;
import de.his.zofar.service.valuetype.model.Variable;

/**
 * @author meisner
 *
 */
public class AutomatedAssignment extends Trigger {

	private static final long serialVersionUID = -1034044098987759911L;

	private final Variable assignVariable;
	private final String assignLabel;
	private final Object assignValue;
	private Answer assignAnswer;

	public AutomatedAssignment(final Variable assignVariable, final String assignLabel,
			final Object assignValue) {
		super();
		this.assignVariable = assignVariable;
		this.assignLabel = assignLabel;
		this.assignValue = assignValue;
	}

	@Override
	public Answer proceed() {
		if (this.assignAnswer == null) {
			this.assignAnswer = new UnboundAnswer(this.assignVariable,
					this.assignLabel);
		}
		this.assignAnswer.setAnswerValue(assignValue);
		return this.assignAnswer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AutomatedAssignment [assignVariable=" + assignVariable
				+ ", assignLabel=" + assignLabel + ", assignValue="
				+ assignValue + ", assignAnswer=" + assignAnswer
				+ ", isForward()=" + isForward() + ", isBackward()="
				+ isBackward() + ", getCondition()=" + getCondition() + "]";
	}



}
