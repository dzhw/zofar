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
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.components.composite.comparison.UIComparisonItem;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IMultipleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.multiplechoice.options.ZofarMultipleChoiceMatrixOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.options.ZofarComparisonCheckboxOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.options.ZofarHorizontalMultipleOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.options.ZofarVerticalMultipleOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author dick
 * 
 */
@FacesComponent(value = "org.zofar.MultipleOption")
public class MultipleOption extends UIInput implements Identificational,
		Visible, NamingContainer {
	private enum PropertyKeys {
		value, label, missing, exclusive
	}
	private String label = "";
	private UIComponent labels;
	private UIComponent parentResponseDomain;
	public static final String COMPONENT_FAMILY = "org.zofar.MultipleOption";
	public MultipleOption() {
		super();
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	public String getRendererType() {
		final UIComponent parent = getParentResponseDomain();
		String rendererType = null;
		if (parent != null) {
			final UIComponent context = parent.getParent().getParent();
			final boolean comparsionFlag = ((context != null)&&((UIComparisonItem.class).isAssignableFrom(context.getClass())));
			if(comparsionFlag)rendererType =  ZofarComparisonCheckboxOptionRenderer.RENDERER_TYPE;
			else if (parent instanceof IMultipleChoiceResponseDomain) {
				String direction = (String) parent.getAttributes().get(
						"direction");
				Boolean isHorizontal = false;
				if (direction != null) {
					isHorizontal = direction.equals("horizontal");
				}
				if (isHorizontal) {
					rendererType = ZofarHorizontalMultipleOptionRenderer.RENDERER_TYPE;
				} else {
					rendererType = ZofarVerticalMultipleOptionRenderer.RENDERER_TYPE;
				}
			} else {
				rendererType = ZofarMultipleChoiceMatrixOptionRenderer.RENDERER_TYPE;
			}
		}
		return rendererType;
	}
	public UIComponent getParentResponseDomain() {
		if (parentResponseDomain == null) {
			parentResponseDomain = JsfUtility.getInstance()
					.getParentResponseDomain(getParent());
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
	public void setItemValue(final boolean value) {
		getStateHelper().put(PropertyKeys.value, value);
	}
	public String getItemValue() {
		return String.valueOf(getStateHelper().eval(PropertyKeys.value));
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
	public void setExclusive(final boolean exclusive) {
		getStateHelper().put(PropertyKeys.exclusive, exclusive);
	}
	public boolean isExclusive() {
		return Boolean.valueOf(getStateHelper().eval(PropertyKeys.exclusive,
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
