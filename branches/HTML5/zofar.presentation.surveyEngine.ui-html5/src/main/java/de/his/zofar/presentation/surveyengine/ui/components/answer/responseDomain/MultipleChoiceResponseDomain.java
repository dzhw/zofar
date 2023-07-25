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
package de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain;

import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.components.common.Alignable;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IMultipleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISequence;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.responsedomain.ZofarHorizontalCheckboxResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.responsedomain.ZofarVerticalCheckboxResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;

/**
 * Renders a panel for the answer options of a single choice question. Each
 * answer option will be rendered as a radio button.
 *
 * @author dick
 */
@FacesComponent(value = "org.zofar.MultipleChoiceResponseDomain")
public class MultipleChoiceResponseDomain extends UIComponentBase implements IMultipleChoiceResponseDomain, Identificational, Alignable {

	private enum PropertyKeys {
		showValues, itemClasses, missingSeparated, horizontal, labelPosition, alignAttached, direction
	}

	public static final String CSS_CLASS_DELIM = ",";

	public static final String COMPONENT_FAMILY = "org.zofar.MultipleChoiceResponseDomain";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MultipleChoiceResponseDomain.class);

	public MultipleChoiceResponseDomain() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public String getRendererType() {
		final Boolean isHorizontal = ((String) this.getAttributes().get("direction")).equals("horizontal");
		if (isHorizontal) {
			return ZofarHorizontalCheckboxResponseDomainRenderer.RENDERER_TYPE;
		} else {
			return ZofarVerticalCheckboxResponseDomainRenderer.RENDERER_TYPE;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.his.zofar.presentation.surveyengine.ui.interfaces.
	 * ISingleChoiceResponseDomain#setShowValues(boolean)
	 */
	@Override
	public void setShowValues(final boolean showValues) {
		this.getStateHelper().put(PropertyKeys.showValues, showValues);
	}

	@Override
	public boolean isShowValues() {
		return Boolean.valueOf(this.getStateHelper().eval(PropertyKeys.showValues, false).toString());
	}

	@Override
	public String getDirection() {
		return (String) this.getStateHelper().eval(PropertyKeys.direction);
	}

	@Override
	public void setDirection(final String direction) {
		this.getStateHelper().put(PropertyKeys.direction, direction);
	}

	public void setLabelPosition(final String labelPosition) {
		this.getStateHelper().put(PropertyKeys.labelPosition, labelPosition);
	}

	public String getLabelPosition() {
		return (String) this.getStateHelper().eval(PropertyKeys.labelPosition);
	}

	public String labelPosition() {
		return this.getLabelPosition();
	}

	public void setMissingSeparated(final boolean missingSeparated) {
		this.getStateHelper().put(PropertyKeys.missingSeparated, missingSeparated);
	}

	public boolean isMissingSeparated() {
		return Boolean.valueOf(this.getStateHelper().eval(PropertyKeys.missingSeparated, true).toString());
	}

	public void setAlignAttached(final boolean alignAttached) {
		this.getStateHelper().put(PropertyKeys.alignAttached, alignAttached);
	}

	public boolean isAlignAttached() {
		return Boolean.valueOf(this.getStateHelper().eval(PropertyKeys.alignAttached, false).toString());
	}
	
	public void sequenceAnswerOptions(){
		List<UIComponent> visibleAnswerOptions = JsfUtility.getInstance().getVisibleChildrensOfType(this, MultipleOption.class);
		if(visibleAnswerOptions != null){
			int index = 0;
			for(final UIComponent visibleAnswerOption : visibleAnswerOptions){
				if((ISequence.class).isAssignableFrom(visibleAnswerOption.getClass())){
					((ISequence)visibleAnswerOption).setSequenceId(index);
					index = index + 1;
				}
			}
		}
	}

}
