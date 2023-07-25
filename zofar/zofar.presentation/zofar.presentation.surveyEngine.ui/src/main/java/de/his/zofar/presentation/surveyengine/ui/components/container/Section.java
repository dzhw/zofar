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
package de.his.zofar.presentation.surveyengine.ui.components.container;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.RadioButtonSingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.UIDropDownMissingResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.section.RadioSectionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.container.SectionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.container.SectionSortingRenderer;
/**
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.Section")
public class Section extends UINamingContainer implements Identificational,Visible {
    public static final String COMPONENT_FAMILY = "org.zofar.Section";
    public Section() {
        super();
    }
	@Override
	@Deprecated
	public String getUID() {
		return this.getId();
	}
	@Override
	@Deprecated
	public Boolean visibleCondition() {
		return this.isRendered();
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	public String getRendererType() {
		UIComponent parent = this.getParent();
		if((parent != null)&&((RadioButtonSingleChoiceResponseDomain.class).isAssignableFrom(parent.getClass()))){
			return RadioSectionRenderer.RENDERER_TYPE;
		}
		else if((parent != null)&&((UIDropDownMissingResponseDomain.class).isAssignableFrom(parent.getParent().getParent().getClass()))){
			return SectionSortingRenderer.RENDERER_TYPE;
		}
		else {
        	return SectionRenderer.RENDERER_TYPE;
        }
	}
    /**
     * this methods returns a list of composite components children, which are
     * the actual children. getChildren() returns an empty list.
     *
     * @return the actual children of the section
     */
    public List<UIComponent> getContent() {
        List<UIComponent> content;
        if (this.getFacet(COMPOSITE_FACET_NAME).getChildren().isEmpty()) {
            content = new ArrayList<UIComponent>();
            content.add(this);
        } else {
            content = this.getFacet(COMPOSITE_FACET_NAME).getChildren();
        }
        return content;
    }
}
