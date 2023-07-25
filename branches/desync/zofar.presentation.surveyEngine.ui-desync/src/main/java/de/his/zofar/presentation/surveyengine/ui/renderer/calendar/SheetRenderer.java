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
package de.his.zofar.presentation.surveyengine.ui.renderer.calendar;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarLegend;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarSheet;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = UICalendarSheet.COMPONENT_FAMILY, rendererType = SheetRenderer.RENDERER_TYPE)
public class SheetRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SheetRenderer.class);
	public static final String RENDERER_TYPE = "de.his.zofar.Calendar.Sheet";
	final HtmlGraphicImage clearImage;
	final HtmlGraphicImage rowClearImage;
	public SheetRenderer() {
		super();
		clearImage = new HtmlGraphicImage();
		clearImage.setValue("/images/Clear.gif");
		clearImage.setWidth("1px");
		clearImage.setHeight("1px");
		rowClearImage = new HtmlGraphicImage();
		rowClearImage.setValue("/images/Clear.gif");
		rowClearImage.setWidth("1px");
		rowClearImage.setHeight("5px");
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		final boolean rendered = (boolean) component.isRendered();
		if (!rendered)
			return;
		int columnCount = 0;
		@SuppressWarnings("unchecked")
		final List<String> columnList = (List<String>) component.getAttributes().get("columnList");
		if (columnList != null) {
			columnCount = columnList.size();
		} else {
			columnCount = (int) component.getAttributes().get("columnCount");
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UICalendarLegend legend = getLegend(component);
		if (legend != null) {
			final String position = legend.getPositionAttribute().toLowerCase();
			if (position.equals("left")) {
				writer.startElement("table", component);
				writer.startElement("tr", component);
				writer.startElement("td", component);
				writer.writeAttribute("class", "unselectable", null);
				writer.writeAttribute("style", "text-align: left; vertical-align: middle;", null);
				legend.encodeAll(context);
				writer.endElement("td");
				writer.startElement("td", component);
			} else if (position.equals("right")) {
				writer.startElement("table", component);
				writer.startElement("tr", component);
				writer.startElement("td", component);
			}
		}
		writer.startElement("table", component);
		writer.writeAttribute("class", "zo-calendar-sheet", null);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("align", "center", null);
		writer.writeAttribute("border", "0", null);
		writer.writeAttribute("cellpadding", "0", null);
		writer.writeAttribute("cellspacing", "0", null);
		if ((legend != null) && (legend.getPositionAttribute().toLowerCase().equals("top"))) {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", "unselectable", null);
			writer.writeAttribute("colspan", columnCount + 3, null);
			writer.writeAttribute("style", "text-align: left; vertical-align: middle;", null);
			legend.encodeAll(context);
			writer.endElement("td");
			writer.endElement("tr");
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", "unselectable", null);
			writer.writeAttribute("width", "1px", null);
			writer.writeAttribute("height", "1px", null);
			writer.writeAttribute("colspan", columnCount + 3, null);
			clearImage.encodeAll(context);
			writer.endElement("td");
			writer.endElement("tr");
		}
		writer.startElement("tr", component);
		writer.startElement("td", component);
		clearImage.encodeAll(context);
		writer.endElement("td");
		writer.startElement("td", component);
		clearImage.encodeAll(context);
		writer.endElement("td");
		if (columnList != null) {
			for (final String column : columnList) {
				writer.startElement("td", component);
				writer.writeAttribute("class", "unselectable", null);
				writer.writeAttribute("onmouseover", "javascript:flagUp_" + component.getParent().getParent().getId() + "();", null);
				writer.writeAttribute("style", "text-align: left; vertical-align: middle;padding-left:5px;padding-right:5px;padding-bottom:3px;min-width:40px", null);
				writer.write(column);
				writer.endElement("td");
			}
		}
		writer.startElement("td", component);
		writer.writeAttribute("class", "unselectable", null);
		writer.writeAttribute("onmouseover", "javascript:flagUp_" + component.getParent().getParent().getId() + "();", null);
		clearImage.encodeAll(context);
		writer.endElement("td");
		writer.endElement("tr");
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered())
			return;
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("tr", component);
		int columnCount = 0;
		@SuppressWarnings("unchecked")
		final List<String> columnList = (List<String>) component.getAttributes().get("columnList");
		if (columnList != null) {
			columnCount = columnList.size();
		} else {
			columnCount = (int) component.getAttributes().get("columnCount");
		}
		@SuppressWarnings("unchecked")
		final List<String> rowList = (List<String>) component.getAttributes().get("rowList");
		Iterator<String> rowIt = null;
		if (rowList != null)
			rowIt = rowList.iterator();
		int index = 0;
		for (final UIComponent child : component.getChildren()) {
			if (index % columnCount == 0) {
				writer.startElement("td", component);
				writer.writeAttribute("align", "center", null);
				writer.writeAttribute("class", "unselectable zo-calendar-sheet-slotlabels", null);
				writer.writeAttribute("style", "padding-right:5px", null);
				writer.writeAttribute("onmouseover", "javascript:flagUp_" + component.getParent().getParent().getId() + "();", null);
				if ((rowIt != null) && (rowIt.hasNext())) {
					writer.write(rowIt.next());
				} else {
					clearImage.encodeAll(context);
				}
				writer.endElement("td");
				writer.startElement("td", component);
				writer.startElement("table", component);
				writer.writeAttribute("width", "5px", null);
				writer.writeAttribute("height", "100%", null);
				writer.writeAttribute("border", "0", null);
				writer.writeAttribute("cellpadding", "0", null);
				writer.writeAttribute("cellspacing", "0", null);
				final UICalendarLegend legend = getLegend(component);
				if (legend != null) {
					for (final UIComponent legendItem : legend.getChildren()) {
						if ((UISelectItem.class).isAssignableFrom(legendItem.getClass())) {
							final UISelectItem tmp = (UISelectItem) legendItem;
							final String itemLabel = tmp.getItemLabel();
							boolean hidden = false;
							if (itemLabel == null)
								hidden = true;
							if (itemLabel.equals("null"))
								hidden = true;
							if (hidden)
								continue;
							writer.startElement("tr", component);
							writer.startElement("td", component);
							final String uuid = UUID.randomUUID().toString();
							writer.writeAttribute("id", uuid, null);
							writer.writeAttribute("onmouseover", "javascript:flagUp_" + component.getParent().getParent().getId() + "();", null);
							writer.writeAttribute("class", "unselectable", null);
							writer.writeAttribute("height", "5px", null);
							writer.writeAttribute("style", "background-color:white;", null);
							writer.startElement("script", component);
							writer.writeAttribute("type", "text/javascript", null);
							String script = "registerIndicator_" + component.getParent().getParent().getId() + "('" + tmp.getItemValue() + "','" + uuid + "');\n";
							writer.write(script);
							writer.endElement("script");
							writer.endElement("td");
							writer.endElement("tr");
						}
					}
				}
				writer.endElement("table");
				writer.endElement("td");
			}
			writer.startElement("td", component);
			writer.writeAttribute("class", "zo-calendar-tile", null);
			child.encodeAll(context);
			writer.endElement("td");
			index++;
			if (index % columnCount == 0) {
				writer.startElement("td", component);
				writer.writeAttribute("class", "unselectable", null);
				writer.writeAttribute("onmouseover", "javascript:flagUp_" + component.getParent().getParent().getId() + "();", null);
				clearImage.encodeAll(context);
				writer.endElement("td");
				writer.endElement("tr");
				writer.startElement("tr", component);
				writer.startElement("td", component);
				writer.writeAttribute("class", "unselectable", null);
				writer.writeAttribute("style", "max-height:" + rowClearImage.getHeight() + ";height:" + rowClearImage.getHeight() + "; overflow:hidden;", null);
				writer.writeAttribute("colspan", columnCount + 3, null);
				writer.writeAttribute("onmouseover", "javascript:flagUp_" + component.getParent().getParent().getId() + "();", null);
				rowClearImage.encodeAll(context);
				writer.endElement("td");
				writer.startElement("tr", component);
			}
		}
		writer.endElement("tr");
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered())
			return;
		final ResponseWriter writer = context.getResponseWriter();
		final int columnCount = (int) component.getAttributes().get("columnCount");
		writer.startElement("tr", component);
		writer.startElement("td", component);
		writer.writeAttribute("class", "unselectable", null);
		writer.writeAttribute("onmouseover", "javascript:flagUp_" + component.getParent().getParent().getId() + "();", null);
		writer.writeAttribute("width", "1px", null);
		writer.writeAttribute("height", "1px", null);
		writer.writeAttribute("colspan", columnCount + 3, null);
		clearImage.encodeAll(context);
		writer.endElement("td");
		writer.endElement("tr");
		final UICalendarLegend legend = getLegend(component);
		if ((legend != null) && (legend.getPositionAttribute().toLowerCase().equals("bottom"))) {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", "unselectable", null);
			writer.writeAttribute("colspan", columnCount + 3, null);
			writer.writeAttribute("style", "text-align: left; vertical-align: middle;", null);
			legend.encodeAll(context);
			writer.endElement("td");
			writer.endElement("tr");
		}
		writer.endElement("table");
		if (legend != null) {
			final String position = legend.getPositionAttribute().toLowerCase();
			if (position.equals("left")) {
				writer.endElement("td");
				writer.endElement("tr");
				writer.endElement("table");
			} else if (position.equals("right")) {
				writer.endElement("td");
				writer.startElement("td", component);
				writer.writeAttribute("class", "unselectable", null);
				writer.writeAttribute("style", "text-align: left; vertical-align: middle;", null);
				legend.encodeAll(context);
				writer.endElement("td");
				writer.endElement("tr");
				writer.endElement("table");
			}
		}
	}
	private UICalendarLegend getLegend(UIComponent component) {
		if (component == null)
			return null;
		if (!UIComponent.isCompositeComponent(component))
			return null;
		final UIComponent legendFacet = ((UINamingContainer) component).getFacet("legend");
		if (legendFacet != null) {
			if ((UICalendarLegend.class).isAssignableFrom(legendFacet.getClass()))
				return ((UICalendarLegend) legendFacet);
		}
		return null;
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
