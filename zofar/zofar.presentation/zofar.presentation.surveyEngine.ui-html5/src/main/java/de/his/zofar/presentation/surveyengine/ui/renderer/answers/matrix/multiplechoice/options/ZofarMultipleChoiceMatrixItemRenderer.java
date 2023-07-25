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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.multiplechoice.options;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.UIAttachedOpenQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISequence;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;

/**
 * @author dick
 *
 */
@FacesRenderer(componentFamily = UIMatrixItem.COMPONENT_FAMILY, rendererType = ZofarMultipleChoiceMatrixItemRenderer.RENDERER_TYPE)
public class ZofarMultipleChoiceMatrixItemRenderer extends ZofarMatrixItemRenderer {

	public static final String RENDERER_TYPE = "org.zofar.MultipleChoiceCompositeItem";

	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarMultipleChoiceMatrixItemRenderer.class);

	public ZofarMultipleChoiceMatrixItemRenderer() {
		super();
	}

	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}

		super.encodeBegin(context, component);
	}

	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}

		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("class", "col-12 col-md-8", null);

		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId()+":rdc", null);
		writer.writeAttribute("class", "custom-form custom-form-checkbox", null);

		writer.startElement("div", component);
		writer.writeAttribute("class", "flex-wrapper", null);
		
		boolean isFirst = true;
		
		if (((ISequence.class).isAssignableFrom(component.getClass()))) {
			final Object sequenceId = ((ISequence) component).getSequenceId();
			if((sequenceId != null)&&(((Integer)sequenceId)>0))isFirst = false;
		}
		
		UIMatrixResponseDomain matrixRDC = (UIMatrixResponseDomain) JsfUtility.getInstance().getParentResponseDomain(component);
		if(matrixRDC != null){
			component.getAttributes().put("showValues",matrixRDC.isShowValues());
		}

		List<UIComponent> multipleOptions = JsfUtility.getInstance().getVisibleChildrensOfType(component, MultipleOption.class);	
		if ((multipleOptions != null) && (!isFirst)) {
			for (final UIComponent child : multipleOptions) {
				if (((MultipleOption.class).isAssignableFrom(child.getClass()))) {
					((MultipleOption) child).setShowLabelFlag(false);
				}
			}
		}

		final List<UIComponent> childs = component.getChildren();
		if (childs != null) {
			for (UIComponent child : childs) {
				if((UIAttachedOpenQuestion.class).isAssignableFrom(child.getClass()))continue;
				child.encodeAll(context);
			}
		}

		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
	}

	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeEnd(context, component);
	}

}
