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
package de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.common.Alignable;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.likert.responsedomain.ZofarHorizontalLikertResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.likert.responsedomain.ZofarVerticalLikertResponseDomainRenderer;
/**
 * Renders a Likertscale. Each answer option will be rendered as a radio
 * button.
 *
 * @author Reitmann
 */
@FacesComponent(value = "org.zofar.LikertResponseDomain")
public class LikertResponseDomain extends UIInput implements IResponseDomain,Identificational,
        Alignable {
	public static final String COMPONENT_FAMILY = "org.zofar.LikertResponseDomain";
    public static final String CSS_CLASS_DELIM = ",";
    private enum PropertyKeys {
        showValues, itemClasses, missingSeparated, horizontal, labelPosition, alignAttached, direction
    }
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LikertResponseDomain.class);
    private String[] rowClasses;
    private int cellCounter;
    public LikertResponseDomain() {
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
        	return ZofarHorizontalLikertResponseDomainRenderer.RENDERER_TYPE;
        }
        else return ZofarVerticalLikertResponseDomainRenderer.RENDERER_TYPE;
	}
    @Deprecated
    @Override
    public String getUID() {
    	return this.getId();
    }
    @Deprecated
    @Override
    public Boolean isHorizontalAligned() {
    	return this.getDirection().equals("horizontal");
    }
    @Override
    public String getDirection() {
    	return (String) getStateHelper().eval("direction");
    }
    public void setDirection(final String direction) {
        getStateHelper().put("direction", direction);
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
