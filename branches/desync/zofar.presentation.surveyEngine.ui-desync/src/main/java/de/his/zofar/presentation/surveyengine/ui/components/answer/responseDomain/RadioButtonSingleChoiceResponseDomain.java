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
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.common.Alignable;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain.ZofarHorizontalRadioResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain.ZofarVerticalRadioResponseDomainRenderer;
/**
 * Renders a panel for the answer options of a single choice question. Each
 * answer option will be rendered as a radio button.
 *
 * @author Reitmann
 */
@FacesComponent(value = "org.zofar.RadioButtonSingleChoiceResponseDomain")
public class RadioButtonSingleChoiceResponseDomain extends UIInput implements
        ISingleChoiceResponseDomain, Identificational, Alignable {
    private enum PropertyKeys {
        showValues, itemClasses, missingSeparated, horizontal, labelPosition, alignAttached, direction
    }
	public static final String CSS_CLASS_DELIM = ",";
    public static final String COMPONENT_FAMILY = "org.zofar.RadioButtonSingleChoiceResponseDomain";
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory
			.getLogger(RadioButtonSingleChoiceResponseDomain.class);
	public RadioButtonSingleChoiceResponseDomain() {
		super();
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	public String getRendererType() {
		Boolean isHorizontal = ((String)getAttributes().get("direction")).equals("horizontal");
		if(isHorizontal == null)isHorizontal = false;
        if (isHorizontal) {
        	return ZofarHorizontalRadioResponseDomainRenderer.RENDERER_TYPE;
        }
        else return ZofarVerticalRadioResponseDomainRenderer.RENDERER_TYPE;
	}
    @Override
	public void processDecodes(FacesContext context) {
		super.processDecodes(context);
	}
	/*
     * (non-Javadoc)
     *
     * @see de.his.zofar.presentation.surveyengine.ui.interfaces.
     * ISingleChoiceResponseDomain#setShowValues(boolean)
     */
    @Override
    public void setShowValues(final boolean showValues) {
        getStateHelper().put(PropertyKeys.showValues, showValues);
    }
    @Override
    public boolean isShowValues() {
        return Boolean.valueOf(getStateHelper().eval(PropertyKeys.showValues,
                false).toString());
	}
	@Override
	public String getUID() {
		return this.getId();
	}
    @Override
    public Boolean isHorizontalAligned() {
    	return this.getDirection().equals("horizontal");
    }
    @Override
    public String getDirection() {
    	return (String) getStateHelper().eval(PropertyKeys.direction);
    }
    public void setDirection(final String direction) {
        getStateHelper().put(PropertyKeys.direction, direction);
    }
	/**
	 * We do not want to participate in state saving.
	 */
	@Override
	public boolean isTransient() {
		return true;
	}
    public void setLabelPosition(final String labelPosition) {
        getStateHelper().put(PropertyKeys.labelPosition, labelPosition);
    }
    public String getLabelPosition() {
        return (String) getStateHelper().eval(PropertyKeys.labelPosition);
    }
	public String labelPosition() {
        return getLabelPosition();
	}
    public void setMissingSeparated(final boolean missingSeparated) {
        getStateHelper().put(PropertyKeys.missingSeparated, missingSeparated);
    }
    public boolean isMissingSeparated() {
        return Boolean.valueOf(getStateHelper().eval(
                PropertyKeys.missingSeparated, true).toString());
	}
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain#
     * setItemClasses(java.lang.String)
     */
    @Override
    public void setItemClasses(final String itemClasses) {
        getStateHelper().put(PropertyKeys.itemClasses, itemClasses);
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain#
     * getItemClasses()
     */
    @Override
    public String getItemClasses() {
        return (String) getStateHelper().eval(PropertyKeys.itemClasses);
    }
    public void setAlignAttached(final boolean alignAttached) {
        getStateHelper().put(PropertyKeys.alignAttached, alignAttached);
    }
    public boolean isAlignAttached() {
        return Boolean.valueOf(getStateHelper().eval(
                PropertyKeys.alignAttached, false).toString());
    }
}
