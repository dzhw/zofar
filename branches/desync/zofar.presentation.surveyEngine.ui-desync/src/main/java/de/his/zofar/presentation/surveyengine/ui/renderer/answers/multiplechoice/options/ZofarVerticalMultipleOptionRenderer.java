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
/*
 * 
 */
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.options;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;

/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = MultipleOption.COMPONENT_FAMILY, rendererType = ZofarVerticalMultipleOptionRenderer.RENDERER_TYPE)
public class ZofarVerticalMultipleOptionRenderer extends
		ZofarCheckboxOptionRenderer {

	public static final String RENDERER_TYPE = "org.zofar.VerticalMultipleOption";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarVerticalMultipleOptionRenderer.class);

	private final static String INPUTCLASSES = "zo-ao-input zo-ao-input-vertical ";
	private final static String INPUTCLASSESMISSING = "zo-ao-input-missing ";
	private final static String VALUECLASSES = "zo-ao-value zo-ao-value-vertical ";
	private final static String LABELCLASSES = "zo-ao-label zo-ao-label-vertical ";
	private final static String OPENCLASSES = "zo-ao-attached zo-ao-attached-vertical ";
	private final static String UNITCLASSES = "zo-ao-label-vertical-unit ";

	public ZofarVerticalMultipleOptionRenderer() {
		super();
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeBegin(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		final UIComponent parent = (UIComponent) component.getAttributes().get(
				"parentResponseDomain");
		if (parent == null) {
			return;
		}

		Boolean alignAttached = (Boolean) parent.getAttributes().get(
				"alignAttached");
		if (alignAttached == null)
			alignAttached = false;

		Boolean isMissing = (Boolean) component.getAttributes().get("missing");
		if (isMissing == null)
			isMissing = false;

		final String labelPosition = (String) parent.getAttributes().get(
				"labelPosition");

		final ResponseWriter writer = context.getResponseWriter();
		if ((labelPosition != null)
				&& labelPosition
						.equals(ZofarResponseDomainRenderer.LABEL_POSITION_LEFT)) {
		} else {
			
			StringBuffer classes = new StringBuffer();
			writer.startElement("td", component);
			classes.append(ZofarVerticalMultipleOptionRenderer.INPUTCLASSES);
			
			if (isMissing){
				writer.writeAttribute("class",ZofarVerticalMultipleOptionRenderer.INPUTCLASSESMISSING, null);
			} else{
				writer.writeAttribute("class",ZofarVerticalMultipleOptionRenderer.INPUTCLASSES, null);
			}			
			this.encodeInput(context, component);
			writer.endElement("td");

			classes = new StringBuffer();
			writer.startElement("td", component);
			classes.append(ZofarVerticalMultipleOptionRenderer.VALUECLASSES);
			
			if(!this.hasValueLabel(context, component)){
				writer.writeAttribute("class", "zo-ao-value-empty", null);
			}
			else{
				writer.writeAttribute("class", classes, null);
			}
			this.encodeValueLabel(context, component);
			writer.endElement("td");
		}

	}

	@Override
	public void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		final UIComponent parent = (UIComponent) component.getAttributes().get(
				"parentResponseDomain");
		if (parent == null) {
			return;
		}

		Boolean alignAttached = (Boolean) parent.getAttributes().get(
				"alignAttached");
		if (alignAttached == null)
			alignAttached = false;
		final boolean openFlag = this.hasOpenQuestion(context, component);
		
		final ResponseWriter writer = context.getResponseWriter();
		final StringBuffer classes = new StringBuffer();
		writer.startElement("td", component);
		classes.append(ZofarVerticalMultipleOptionRenderer.LABELCLASSES);
		
		if (Section.class.isAssignableFrom(component.getParent().getClass())){
			classes.append(ZofarVerticalMultipleOptionRenderer.UNITCLASSES);
		}
		
		if(!alignAttached){
			if(openFlag)classes.append(ZofarVerticalMultipleOptionRenderer.OPENCLASSES);
			writer.writeAttribute("colspan", 2, null);
		}
		writer.writeAttribute("class", classes, null);
		this.encodeLabel(context, component);		
		if(alignAttached){
			writer.endElement("td");
			writer.startElement("td", component);
			writer.writeAttribute("class", ZofarVerticalMultipleOptionRenderer.OPENCLASSES, null);
		}
		this.encodeOpenQuestion(context, component);
		writer.endElement("td");
	}

	@Override
	public void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		final UIComponent parent = (UIComponent) component.getAttributes().get(
				"parentResponseDomain");
		if (parent == null) {
			return;
		}

		final String labelPosition = (String) parent.getAttributes().get(
				"labelPosition");

		if ((labelPosition != null)
				&& labelPosition
						.equals(ZofarResponseDomainRenderer.LABEL_POSITION_LEFT)) {

		} else {
		}
	}
}
