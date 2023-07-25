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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.UIDropDownMissingResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.singlechoice.UISingleChoiceMatrixResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;
/**
 * @author meisner
 *
 */
public abstract class ZofarMatrixResponseDomainRenderer extends ZofarResponseDomainRenderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarMatrixResponseDomainRenderer.class);
	public ZofarMatrixResponseDomainRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		boolean dropdown=false;
		for (final UIComponent child : component.getChildren()) {
			for (final UIComponent childD : child.getChildren()) {
				if((UIDropDownMissingResponseDomain.class).isAssignableFrom(childD.getClass())){
					dropdown=true;
				}
			}
		}
		String itemClassesStr = " ";
		if((UISingleChoiceMatrixResponseDomain.class).isAssignableFrom(component.getClass())){
			final List<String> itemClasses = ((UISingleChoiceMatrixResponseDomain)component).getAdditonalClasses();
			if(itemClasses != null) {
				for(final String itemClass : itemClasses) {
					if(itemClass == null)continue;
					final String stripped = itemClass.strip();
					if(stripped.contentEquals(""))continue;
					itemClassesStr += " "+stripped;
				}
			}
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), null);
		writer.writeAttribute("class", "form-orientation form-responsive"+itemClassesStr, null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "question-sub", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		writer.startElement("p", component);
		writer.writeAttribute("class", "text-head hidden-md-up", null);
		writer.writeAttribute("data-carousel-head-id", component.getClientId(context).replace(':', '_')+"_carousel", null);
		writer.endElement("p");
		writer.endElement("div");
		writer.endElement("div");
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-inner form-2-col pt-2 pt-sm-4", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		if (!dropdown) {
			writer.startElement("div", component);
			writer.writeAttribute("id", component.getClientId(context).replace(':', '_')+"_carousel", null);
			writer.writeAttribute("class", "carousel slide", null);
			writer.writeAttribute("data-interval","false", null);
			writer.startElement("div", component);
			writer.writeAttribute("class", "zofar-ol-custom hidden-md-up", null);
			writer.startElement("ol", component);
			writer.writeAttribute("class", "carousel-indicators carousel-indicators-numbers hidden-md-up", null);
			int lft = 0;
			final List<UIMatrixItem> items = indicatorHelper(component.getChildren());
			for (final UIComponent child : items) {
				if((UIMatrixItem.class).isAssignableFrom(child.getClass())) {
					if(!child.isRendered())continue;
					writer.startElement("li", component);
					writer.writeAttribute("data-target", "#"+component.getClientId(context).replace(':', '_')+"_carousel", null);
					writer.writeAttribute("data-slide-to", lft, null);
					if(lft == 0)writer.writeAttribute("class", "active", null);
					writer.endElement("li");
					lft = lft + 1;
				}
			}
			writer.endElement("ol");
			writer.endElement("div");
			writer.startElement("div", component);
			writer.writeAttribute("class", "carousel-inner", null);
			writer.writeAttribute("role", "listbox", null);
		}
	}
	private List<UIMatrixItem> indicatorHelper(final  List<UIComponent> compChilds) {
		final List<UIMatrixItem> items = new ArrayList<UIMatrixItem>();
		for (final UIComponent child : compChilds) {
			if((UIMatrixItem.class).isAssignableFrom(child.getClass())) {
				items.add((UIMatrixItem)child);
			}
			else if((UISort.class).isAssignableFrom(child.getClass())) {
				items.addAll(indicatorHelper(((UISort)child).sortChildren()));
			}
			else if((Section.class).isAssignableFrom(child.getClass())) {
				items.addAll(indicatorHelper(((Section)child).getChildren()));
			}
		}
		return items;
	}
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeChildren(context, component);
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		boolean dropdown=false;
		for (final UIComponent child : component.getChildren()) {
			for (final UIComponent childD : child.getChildren()) {
				if((UIDropDownMissingResponseDomain.class).isAssignableFrom(childD.getClass())){
					dropdown=true;
					break;
				}
			}
		}		
		if (!dropdown) {
			writer.endElement("div");
			writer.startElement("div", component);
			writer.writeAttribute("class", "carousel-control-outer hidden-md-up", null);
			writer.startElement("a", component);
			writer.writeAttribute("class", "btn btn-secondary carousel-control-prev", null);
			writer.writeAttribute("hRef", "#"+component.getClientId(context).replace(':', '_')+"_carousel", null);
			writer.writeAttribute("role", "button", null);
			writer.writeAttribute("data-slide", "prev", null);
			writer.startElement("i", component);
			writer.writeAttribute("class", "fa fa-angle-left", null);
			writer.writeAttribute("aria-hidden", "true", null);
			writer.endElement("i");
			writer.startElement("span", component);
			writer.writeAttribute("class", "sr-only", null);
			writer.write("Previous");
			writer.endElement("span");
			writer.endElement("a");
			writer.startElement("a", component);
			writer.writeAttribute("class", "btn btn-secondary carousel-control-next", null);
			writer.writeAttribute("hRef", "#"+component.getClientId(context).replace(':', '_')+"_carousel", null);
			writer.writeAttribute("role", "button", null);
			writer.writeAttribute("data-slide", "next", null);
			writer.startElement("i", component);
			writer.writeAttribute("class", "fa fa-angle-right", null);
			writer.writeAttribute("aria-hidden", "true", null);
			writer.endElement("i");
			writer.startElement("span", component);
			writer.writeAttribute("class", "sr-only", null);
			writer.write("Next");
			writer.endElement("span");
			writer.endElement("a");
			writer.endElement("div");
		}
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
	}
}
