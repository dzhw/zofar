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
package de.his.zofar.generator.maven.plugin.generator.page;

import com.sun.java.jsf.composite.trigger.ActionType;
import com.sun.java.jsf.composite.trigger.ConsistencyType;
import com.sun.java.jsf.composite.trigger.DirectionType;
import com.sun.java.jsf.composite.trigger.JscheckType;
import com.sun.java.jsf.composite.trigger.ScriptItemType;
import com.sun.java.jsf.composite.trigger.SessionType;
import com.sun.java.jsf.composite.trigger.TriggerType;
import com.sun.java.jsf.composite.trigger.VariableType;
import com.sun.java.jsf.facelets.DefineType;

import de.his.zofar.xml.questionnaire.TriggerActionType;
import de.his.zofar.xml.questionnaire.TriggerConsistencyType;
import de.his.zofar.xml.questionnaire.TriggerJSCheckType;
import de.his.zofar.xml.questionnaire.TriggerScriptItemType;
import de.his.zofar.xml.questionnaire.TriggerSessionType;
import de.his.zofar.xml.questionnaire.TriggerVariableType;

/**
 * creator for triggers.
 * 
 * @author le
 * 
 */
class TriggerCreator {

	/**
	 * adds a consistency trigger.
	 * 
	 * @param source
	 * @param target
	 */
	void addTriggerConsistency(final TriggerConsistencyType source,
			final DefineType target) {
		final ConsistencyType trigger = target.addNewConsistency();

		trigger.setMessage(source.getMessage());
		trigger.setConstraints((source.getConstraints()).replaceAll("<", "&lt;"));

		addDefaultAttributes(source, trigger);
	}

	/**
	 * adds a variable trigger.
	 * 
	 * @param source
	 * @param target
	 */
	void addTriggerVariable(final TriggerVariableType source,
			final DefineType target) {
		final VariableType trigger = target.addNewVariable();

		trigger.setVar(String.format("#{%s}", source.getVariable()));
		trigger.setValue(source.getValue());
		addDefaultAttributes(source, trigger);
	}
	
	/**
	 * adds a JS Check trigger.
	 * 
	 * @param source
	 * @param target
	 */
	void addTriggerJS(final TriggerJSCheckType source,
			final DefineType target) {
		final JscheckType trigger = target.addNewJscheck();

		trigger.setVar(String.format("#{%s}", source.getVariable()));
		if(source.isSetXvar())trigger.setXvar(String.format("#{%s}", source.getXvar()));
		if(source.isSetYvar())trigger.setYvar(String.format("#{%s}", source.getYvar()));
	}

	/**
	 * adds a session trigger.
	 * 
	 * @param source
	 * @param target
	 */
	void addTriggerSession(final TriggerSessionType source,
			final DefineType target) {
		final SessionType trigger = target.addNewSession();

		trigger.setTimeout(source.getTimeout());

		addDefaultAttributes(source, trigger);
	}
	
	/**
	 * adds a action trigger.
	 * 
	 * @param source
	 * @param target
	 */
	void addTriggerAction(final TriggerActionType source,
			final DefineType target) {
		final ActionType trigger = target.addNewAction();
		trigger.setCommand(source.getCommand());
		addDefaultAttributes(source, trigger);
	}

	/**
	 * adds attributes to the target trigger which all triggers have in common.
	 * 
	 * @param source
	 *            the trigger to add
	 * @param target
	 */
	private void addDefaultAttributes(
			final de.his.zofar.xml.questionnaire.TriggerType source,
			final TriggerType target) {
		if (source.isSetDirection()) {
			target.setDirection(getDirection(source.getDirection()));
		}

		if (source.isSetCondition()) {
			target.setCondition(source.getCondition());
		}

		if (source.isSetOnExit()) {
			target.setOnExit(source.getOnExit());
		}
		
		target.setNavigator("#{navigatorBean}");
		
		TriggerScriptItemType[] scriptItems = source.getScriptItemArray();
		if((scriptItems != null)&&(scriptItems.length > 0)){
			final int count = scriptItems.length;
			for(int a = 0; a < count; a++){
				ScriptItemType scriptItem = target.addNewScriptItem();
				scriptItem.setValue(scriptItems[a].getValue());
			}
		}
	}

	/**
	 * @param directionValue
	 * @return
	 */
	private DirectionType.Enum getDirection(
			final de.his.zofar.xml.questionnaire.TriggerType.Direction.Enum sourceDirection) {
		DirectionType.Enum direction = null;

		switch (sourceDirection.intValue()) {
		case de.his.zofar.xml.questionnaire.TriggerType.Direction.INT_BACKWARD:
			direction = DirectionType.BACKWARD;
			break;
		case de.his.zofar.xml.questionnaire.TriggerType.Direction.INT_FORWARD:
			direction = DirectionType.FORWARD;
			break;
		case de.his.zofar.xml.questionnaire.TriggerType.Direction.INT_SAME:
			direction = DirectionType.SAME;
			break;
		case de.his.zofar.xml.questionnaire.TriggerType.Direction.INT_UNKNOWN:
			direction = DirectionType.UNKNOWN;
			break;
		default:
			throw new IllegalStateException("direction type with value "
					+ sourceDirection.intValue()
					+ " is unknown or is not yet implemented");
		}
		return direction;
	}
}
