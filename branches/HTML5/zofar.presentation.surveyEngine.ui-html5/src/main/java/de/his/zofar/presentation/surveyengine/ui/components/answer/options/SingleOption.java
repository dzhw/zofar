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
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.comparison.UIComparisonItem;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.singlechoice.UISingleChoiceMatrixItemResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerOption;
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
public class SingleOption extends UINamingContainer implements Identificational, Visible, IAnswerOption {
	private enum PropertyKeys {
		value, label, missing, tooltipText
	}
	private String label = "";
	private UIComponent labels;
	private boolean showLabelFlag = true;
	private Object sequenceId;
	public static final String COMPONENT_FAMILY = "org.zofar.SingleOption";
	private static final Logger LOGGER = LoggerFactory.getLogger(SingleOption.class);
	public SingleOption() {
		super();
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	public String getRendererType() {
		final UIComponent parentRdc = JsfUtility.getInstance().getParentResponseDomain(this.getParent());
		String rendererType = null;
		if (parentRdc != null) {
			final UIComponent context = parentRdc.getParent().getParent();
			final boolean comparsionFlag = ((context != null) && ((UIComparisonItem.class).isAssignableFrom(context.getClass())));
			if (comparsionFlag) {
				rendererType = ZofarComparisonRadioSingleChoiceOptionRenderer.RENDERER_TYPE;
			} else if (parentRdc instanceof UIDropDownResponseDomain) {
				rendererType = ZofarDropDownSingleOptionRenderer.RENDERER_TYPE;
			} else if (parentRdc instanceof UISingleChoiceMatrixItemResponseDomain) {
				rendererType = ZofarHorizontalRadioSingleOptionRenderer.RENDERER_TYPE;
			} else {
				final String direction = (String) parentRdc.getAttributes().get("direction");
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
	public void setItemValue(final String value) {
		this.getStateHelper().put(PropertyKeys.value, value);
	}
	public String getItemValue() {
		return (String) this.getStateHelper().eval(PropertyKeys.value);
	}
	public String getValue() {
		return this.getItemValue();
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
	public void setTooltipText(final String tooltip) {
		this.getStateHelper().put(PropertyKeys.tooltipText, tooltip);
	}
	public String getTooltipText() {
		return (String) this.getStateHelper().eval(PropertyKeys.tooltipText);
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
