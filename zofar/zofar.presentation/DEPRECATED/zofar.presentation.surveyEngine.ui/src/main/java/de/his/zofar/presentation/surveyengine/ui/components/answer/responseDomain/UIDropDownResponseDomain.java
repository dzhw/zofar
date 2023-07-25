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
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.responsedomain.ZofarDropDownMatrixResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain.ZofarDropDownResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;

/**
 * @author le
 *
 */
@FacesComponent("org.zofar.responsedomain.DropDown")
public class UIDropDownResponseDomain extends UIInput implements
        ISingleChoiceResponseDomain, Identificational {

	private enum PropertyKeys {
        showValues, itemClasses
    }

	private UIComponent parentResponseDomain;

    
	@Override
	public String getUID() {
		return this.getId();
	}
    
    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIInput#getFamily()
     */
    @Override
    public String getFamily() {
        return IResponseDomain.COMPONENT_FAMILY;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIComponentBase#getRendererType()
     */
    @Override
    public String getRendererType() {
    	UIComponent parent = JsfUtility.getInstance().getParentResponseDomain(getParent());
        if (parent == null) {
        		return ZofarDropDownResponseDomainRenderer.RENDERER_TYPE;
        }
    	else{
    		return ZofarDropDownMatrixResponseDomainRenderer.RENDERER_TYPE;
    	}
    }
    
    public UIComponent getParentResponseDomain() {
        if (parentResponseDomain == null) {
            parentResponseDomain = JsfUtility.getInstance().getParentResponseDomain(getParent());
        }

        return parentResponseDomain;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIComponentBase#isTransient()
     */
    @Override
    public boolean isTransient() {
        return true;
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

    /*
     * (non-Javadoc)
     *
     * @see de.his.zofar.presentation.surveyengine.ui.interfaces.
     * ISingleChoiceResponseDomain#isShowValues()
     */
    @Override
    public boolean isShowValues() {
        return Boolean.valueOf(this.getStateHelper()
                .eval(PropertyKeys.showValues, Boolean.FALSE).toString());
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

}
