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
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.comparison.UIComparisonItem;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerOption;
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
public class MultipleOption extends UIInput implements Identificational, Visible, NamingContainer,IAnswerOption {
	private enum PropertyKeys {
		value, label, missing, exclusive, tooltipText
	}
	private String label = "";
	private UIComponent labels;
	private Object sequenceId;
	private boolean showLabelFlag = true;
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
		final UIComponent parent = JsfUtility.getInstance().getParentResponseDomain(this.getParent());
		String rendererType = null;
		if (parent != null) {
			final UIComponent context = parent.getParent().getParent();
			final boolean comparsionFlag = ((context != null) && ((UIComparisonItem.class).isAssignableFrom(context.getClass())));
			if (comparsionFlag) {
				rendererType = ZofarComparisonCheckboxOptionRenderer.RENDERER_TYPE;
			} else if (parent instanceof IMultipleChoiceResponseDomain) {
				final String direction = (String) parent.getAttributes().get("direction");
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
	public void setItemValue(final boolean value) {
		this.getStateHelper().put(PropertyKeys.value, value);
	}
	public String getItemValue() {
		return String.valueOf(this.getStateHelper().eval(PropertyKeys.value));
	}
	@Override
	public String getValue() {
		return this.getItemValue();
	}
	public void setTooltipText(final String tooltip) {
		this.getStateHelper().put(PropertyKeys.tooltipText, tooltip);
	}
	public String getTooltipText() {
		return (String) this.getStateHelper().eval(PropertyKeys.tooltipText);
	}
	public String getLabel() {
		if ((this.label == null) || (this.label.equals(""))) {
			final Object itemLabel = this.getAttributes().get("itemLabel");
			if ((itemLabel != null) && !((String) itemLabel).isEmpty()) {
				this.label = (String) itemLabel + " ";
			}
			final String labels = this.getLabelStringFromFacet();
			if (!labels.isEmpty()) {
				this.label += labels + " ";
			}
		}
		return this.label;
	}
	private UIComponent getLabels() {
		this.labels = this.getFacet("labels");
		return this.labels;
	}
	private String getLabelStringFromFacet() {
		final String delimiter = " ";
		final StringBuilder labels = new StringBuilder();
		final UIComponent facet = this.getLabels();
		if (facet != null) {
			if (facet.getChildren().isEmpty()) {
				if (UIText.class.isAssignableFrom(facet.getClass())) {
					if (!((UIText) facet).getContent().isEmpty()) {
						labels.append(((UIText) facet).getContent()).append(delimiter);
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
		this.getStateHelper().put(PropertyKeys.missing, missing);
	}
	public boolean isMissing() {
		return Boolean.valueOf(this.getStateHelper().eval(PropertyKeys.missing, false).toString());
	}
	public void setExclusive(final boolean exclusive) {
		this.getStateHelper().put(PropertyKeys.exclusive, exclusive);
	}
	public boolean isExclusive() {
		return Boolean.valueOf(this.getStateHelper().eval(PropertyKeys.exclusive, false).toString());
	}
	public boolean isShowLabelFlag() {
		return showLabelFlag;
	}
	public void setShowLabelFlag(boolean showLabelFlag) {
		this.showLabelFlag = showLabelFlag;
	}
	public Boolean hasAttachedOpenQuestion() {
		for (final UIComponent child : this.getChildren()) {
			if (UIAttachedOpenQuestion.class.isAssignableFrom(child.getClass())) {
				return true;
			}
		}
		return false;
	}
	public UIAttachedOpenQuestion getAttachedOpenQuestion() {
		for (final UIComponent child : this.getChildren()) {
			if (UIAttachedOpenQuestion.class.isAssignableFrom(child.getClass())) {
				return (UIAttachedOpenQuestion) child;
			}
		}
		return null;
	}
	@Override
	public void setSequenceId(Object id) {
		this.sequenceId = id;
	}
	@Override
	public Object getSequenceId() {
		return sequenceId;
	}
}
