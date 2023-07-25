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
package de.his.zofar.presentation.surveyengine.ui.components.answer.options;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.UIDropDownResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.components.composite.comparison.UIComparisonItem;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.options.ZofarComparisonRadioSingleChoiceOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.options.ZofarDropDownSingleOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.options.ZofarHorizontalRadioSingleOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.options.ZofarVerticalRadioSingleOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.SingleOption")
public class SingleOption extends UINamingContainer implements Identificational,Visible {
    private enum PropertyKeys {
        value, label, missing
    }
    private String label = "";
    private UIComponent labels;
    private UIComponent parentResponseDomain;
    public static final String COMPONENT_FAMILY = "org.zofar.SingleOption";
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SingleOption.class);
    public SingleOption() {
        super();
    }
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    @Override
    public String getRendererType() {
    	UIComponent parent = this.getParentResponseDomain();
        String rendererType = null;
        if (parent != null) {
			final UIComponent context = parent.getParent().getParent();
			final boolean comparsionFlag = ((context != null)&&((UIComparisonItem.class).isAssignableFrom(context.getClass())));
			if(comparsionFlag)rendererType =  ZofarComparisonRadioSingleChoiceOptionRenderer.RENDERER_TYPE;
			else if (parent instanceof UIDropDownResponseDomain) {
                rendererType = ZofarDropDownSingleOptionRenderer.RENDERER_TYPE;
            } else {
            	String direction = (String)parent.getAttributes().get("direction");
                Boolean isHorizontal = false;
                if (direction != null) {
                	isHorizontal = direction.equals("horizontal");
                }
                if (isHorizontal) {
                    rendererType = ZofarHorizontalRadioSingleOptionRenderer.RENDERER_TYPE;
                } else {
                    rendererType = ZofarVerticalRadioSingleOptionRenderer.RENDERER_TYPE;
                }
            }
        }
        return rendererType;
    }
    public UIComponent getParentResponseDomain() {
        if (parentResponseDomain == null) {
            parentResponseDomain = JsfUtility.getInstance().getParentResponseDomain(getParent());
        }
        return parentResponseDomain;
    }
	@Override
	@Deprecated
	public Boolean visibleCondition() {
		return this.isRendered();
	}
	@Override
	@Deprecated
	public String getUID() {
		return this.getId();
	}
    public void setItemValue(final String value) {
        getStateHelper().put(PropertyKeys.value, value);
    }
    public String getItemValue() {
        return (String) getStateHelper().eval(PropertyKeys.value);
    }
    public String getValue() {
        return getItemValue();
    }
    public String getLabel() {
        if ((label == null) || (label.equals(""))) {
            final Object itemLabel = getAttributes().get("itemLabel");
            if (itemLabel != null && !((String) itemLabel).isEmpty()) {
                label = (String) itemLabel + " ";
            }
            final String labels = getLabelStringFromFacet();
            if (!labels.isEmpty()) {
                label += labels + " ";
            }
        }
        return label;
    }
    private UIComponent getLabels() {
        labels = getFacet("labels");
        return labels;
    }
    private String getLabelStringFromFacet() {
        final String delimiter = " ";
        final StringBuilder labels = new StringBuilder();
        final UIComponent facet = getLabels();
        if (facet != null) {
            if (facet.getChildren().isEmpty()) {
                if (UIText.class.isAssignableFrom(facet.getClass())) {
                    if (!((UIText) facet).getContent().isEmpty()) {
                        labels.append(((UIText) facet).getContent()).append(
                                delimiter);
                    }
                }
            } else {
                if (UIText.class.isAssignableFrom(facet.getClass())) {
                    for (final UIComponent child : facet.getChildren()) {
                        labels.append(child).append(delimiter);
                    }
                }
            }
        }
        return labels.toString();
    }
    public void setMissing(final boolean missing) {
        getStateHelper().put(PropertyKeys.missing, missing);
    }
    public boolean isMissing() {
        return Boolean.valueOf(getStateHelper().eval(PropertyKeys.missing,
                false).toString());
    }
    public Boolean hasAttachedOpenQuestion() {
        for (final UIComponent child : getChildren()) {
            if (UIAttachedOpenQuestion.class.isAssignableFrom(child.getClass())) {
                return true;
            }
        }
        return false;
    }
    public UIAttachedOpenQuestion getAttachedOpenQuestion() {
        for (final UIComponent child : getChildren()) {
            if (UIAttachedOpenQuestion.class.isAssignableFrom(child.getClass())) {
                return (UIAttachedOpenQuestion) child;
            }
        }
        return null;
    }
}
