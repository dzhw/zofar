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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.RadioButtonSingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;

/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = RadioButtonSingleChoiceResponseDomain.COMPONENT_FAMILY, rendererType = ZofarVerticalRadioResponseDomainRenderer.RENDERER_TYPE)
public class ZofarVerticalRadioResponseDomainRenderer extends
        ZofarSingleChoiceResponseDomainRenderer {

	public static final String RENDERER_TYPE = "org.zofar.VerticalRadioResponseDomain";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarVerticalRadioResponseDomainRenderer.class);

	public ZofarVerticalRadioResponseDomainRenderer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeBegin(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		String additonalClasses = null;
		final boolean separateMissings = (Boolean) component.getAttributes().get("missingSeparated");
		if(separateMissings)additonalClasses = "zo-rd-singlechoice-vertical	zo-missingSeparated zo-missingSeparated-vertical";
        this.startTable(context.getResponseWriter(), component,additonalClasses);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {

		Point2D rowPair = new Point(1,0);

		if (!component.isRendered()) {
			return;
		}

		for (final UIComponent child : component.getChildren()) {
			if (!child.isRendered()) {
				continue;
			}
			rowPair = this.encodeChildrenHelper(context,component,child,rowPair);
		}
	}

	public Point2D encodeChildrenHelper(final FacesContext context,final UIComponent component,
			final UIComponent child,Point2D rowPair) throws IOException {

		if (!child.isRendered()) {
			return rowPair;
		}
		final String[] rowClasses = this.itemClassesToArray((String) component.getAttributes().get(
				"itemClasses"));
		final boolean hasRowClasses = rowClasses.length > 0;

		final ResponseWriter writer = context.getResponseWriter();

		if((Section.class).isAssignableFrom(child.getClass())){
			writer.startElement("tr", component);

			writer.startElement("td", component);
			writer.writeAttribute("colspan", "4", null);
			writer.writeAttribute("class","zo-unit zo-sc-unit zo-vertical-sc-unit",null);
			writer.writeAttribute("style","padding-top:10px;padding-bottom:10px;",null);
			child.encodeAll(context);
			writer.endElement("td");
			writer.endElement("tr");

			for (final UIComponent sectionChild : ((Section)child).getChildren()) {
				if (!sectionChild.isRendered()) {
					continue;
				}
				rowPair = this.encodeChildrenHelper(context,component,sectionChild,rowPair);
			}
		}
		else if((UISort.class).isAssignableFrom(child.getClass())){
			for (final UIComponent sortChild : ((UISort)child).sortChildren()) {
				if (!sortChild.isRendered()) {
					continue;
				}
				rowPair = this.encodeChildrenHelper(context,component,sortChild,rowPair);
			}
		}
		else{
			
	        Boolean isMissing = (Boolean) child.getAttributes().get("missing");
			if (isMissing == null){
				isMissing = false;
			}
			writer.startElement("tr", component);
			String classes = "";
			if (hasRowClasses) {
				int index = (int)rowPair.getX();
				index++;
				rowPair.setLocation(index,rowPair.getY());
				classes += rowClasses[index % rowClasses.length]+" ";
			}
			if (isMissing){
				int index = (int)rowPair.getY();
				index++;
				rowPair.setLocation(rowPair.getX(), index);
				classes += "zo-ao-missing zo-ao-missing-vertical"+"-"+(index);
			}
			if(!classes.equals(""))writer.writeAttribute("class", classes.trim(), null);
			child.encodeAll(context);
			writer.endElement("tr");
		}
		return rowPair;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		final ResponseWriter writer = context.getResponseWriter();
		this.endTable(writer);

	}

}
