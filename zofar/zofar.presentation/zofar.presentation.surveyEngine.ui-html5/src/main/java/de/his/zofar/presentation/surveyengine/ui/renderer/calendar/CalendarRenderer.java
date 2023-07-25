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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendar;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarColumnItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarLegendItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarRowItem;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UICalendar.COMPONENT_FAMILY, rendererType = CalendarRenderer.RENDERER_TYPE)
public class CalendarRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarRenderer.class);
	public static final String RENDERER_TYPE = "de.his.zofar.Calendar.calendar";
	public CalendarRenderer() {
		super();
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("article", component);
		writer.writeAttribute("id", component.getClientId(context) + "_main", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-orientation form-responsive", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), null);
		writer.writeAttribute("class", "calendar", null);
		final String behaviour = (String) component.getAttributes().get("behaviour");
		writer.writeAttribute("data-behaviour", behaviour, null);
		UICalendar calendar = (UICalendar) component;
		final UIComponent legend = calendar.getFacet("legend");
		if (legend != null) {
			final List<UIComponent> legendItems = legend.getChildren();
			if (legendItems != null) {
				writer.startElement("div", component);
				writer.writeAttribute("id", component.getClientId(context) + "_legend", null);
				writer.writeAttribute("class", "calendar-legend", null);
				writer.startElement("button", component);
				writer.writeAttribute("type", "button", null);
				writer.writeAttribute("data-toggle", "modal", null);
				final String modalId = component.getClientId(context).replace(':', '_') + "_legend_modal";
				writer.writeAttribute("data-target", "#" + modalId, null);
				writer.writeAttribute("class", "btn btn-secondary calendar-legend-selected", null);
				writer.endElement("button");
				writer.startElement("div", component);
				writer.writeAttribute("id", modalId, null);
				writer.writeAttribute("class", "modal fade", null);
				writer.writeAttribute("role", "dialog", null);
				writer.startElement("div", component);
				writer.writeAttribute("class", "modal-dialog", null);
				writer.startElement("div", component);
				writer.writeAttribute("class", "modal-content", null);
				writer.startElement("div", component);
				writer.writeAttribute("class", "modal-header", null);
				writer.startElement("button", component);
				writer.writeAttribute("type", "button", null);
				writer.writeAttribute("class", "close", null);
				writer.writeAttribute("data-dismiss", "modal", null);
				writer.write("x");
				writer.endElement("button");
				writer.startElement("h4", component);
				writer.writeAttribute("class", "modal-title", null);
				writer.write("Aktivität auswählen");
				writer.endElement("h4");
				writer.endElement("div");
				writer.startElement("div", component);
				writer.writeAttribute("class", "modal-body", null);
				writer.writeAttribute("id", component.getClientId(context) + "_legend_modal_legends", null);
				for (final UIComponent legendItem : legendItems) {
					if (!(UICalendarLegendItem.class).isAssignableFrom(legendItem.getClass()))
						continue;
					if (!legendItem.isRendered())
						continue;
					final UICalendarLegendItem tmp = (UICalendarLegendItem) legendItem;
					final String id = tmp.getIdAttribute();
					final String label = tmp.getLabelAttribute();
					final String color = tmp.getColorAttribute();
					final String pattern = tmp.getPatternAttribute();
					writer.startElement("div", component);
					writer.writeAttribute("class", "form-check", null);
					writer.startElement("label", component);
					writer.writeAttribute("class", "form-check-label", null);
					writer.startElement("input", component);
					writer.writeAttribute("name", component.getClientId(context) + "_legend_modal_legends_legend",
							null);
					writer.writeAttribute("id", component.getClientId(context) + "_legend_modal_legends_legend_" + id,
							null);
					writer.writeAttribute("value", id, null);
					writer.writeAttribute("class", "form-check-input", null);
					writer.writeAttribute("type", "radio", null);
					writer.writeAttribute("data-color", color, null);
					writer.writeAttribute("data-label", label, null);
					writer.writeAttribute("data-pattern", pattern, null);
					writer.endElement("input");
					writer.endElement("label");
					writer.endElement("div");
				}
				writer.endElement("div");
				writer.startElement("div", component);
				writer.writeAttribute("class", "modal-footer", null);
				writer.startElement("button", component);
				writer.writeAttribute("type", "button", null);
				writer.writeAttribute("class", "btn btn-default", null);
				writer.writeAttribute("data-dismiss", "modal", null);
				writer.write("OK");
				writer.endElement("button");
				writer.endElement("div");
				writer.endElement("div");
				writer.endElement("div");
				writer.endElement("div");
				writer.endElement("div");
			}
		}
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-inner form-3-col pt-2 pt-sm-5", null);
		writer.startElement("span", component);
		writer.writeAttribute("class", "calendar-header-title", null);
		writer.endElement("span");
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "calendar-container", null);
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		UICalendar calendar = (UICalendar) component;
		final String carouselId = component.getClientId(context).replace(':', '_') + "_carousel";
		writer.startElement("div", component);
		writer.writeAttribute("id", carouselId, null);
		writer.writeAttribute("class", "carousel slide", null);
		writer.writeAttribute("data-interval", "false", null);
		writer.startElement("ol", component);
		writer.writeAttribute("class", "carousel-indicators carousel-indicators-numbers hidden-md-up", null);
		final UIComponent rows = calendar.getFacet("rows");
		if (rows != null) {
			final List<UIComponent> rowItems = rows.getChildren();
			if (rowItems != null) {
				int slideIndex = -1;
				for (final UIComponent rowItem : rowItems) {
					if (!(UICalendarRowItem.class).isAssignableFrom(rowItem.getClass()))
						continue;
					slideIndex = slideIndex + 1;
					if (!rowItem.isRendered())
						continue;
					final UICalendarRowItem tmpRow = (UICalendarRowItem) rowItem;
					final String rowLabel = tmpRow.getLabelAttribute();
					writer.startElement("li", component);
					writer.writeAttribute("data-target", "#" + carouselId, null);
					writer.writeAttribute("data-slide-to", slideIndex, null);
					if (slideIndex == 0)
						writer.writeAttribute("class", "active", null);
					writer.write(rowLabel);
					writer.endElement("li");
				}
			}
		}
		writer.endElement("ol");
		writer.startElement("div", component);
		writer.writeAttribute("role", "listbox", null);
		writer.writeAttribute("class", "carousel-inner", null);
		final UIComponent columns = calendar.getFacet("columns");
		if (columns != null) {
			final List<UIComponent> columnItems = columns.getChildren();
			if (columnItems != null) {
				writer.startElement("div", component);
				writer.writeAttribute("class", "calendar-head", null);
				writer.startElement("div", component);
				writer.writeAttribute("class", "calendar-header-cell calendar-header-cell-first unselectable", null);
				writer.writeAttribute("data-full-title", "", null);
				writer.writeAttribute("aria-hidden", "true", null);
				writer.endElement("div");
				for (final UIComponent columnItem : columnItems) {
					if (!(UICalendarColumnItem.class).isAssignableFrom(columnItem.getClass()))
						continue;
					if (!columnItem.isRendered())
						continue;
					final UICalendarColumnItem tmpColumn = (UICalendarColumnItem) columnItem;
					final String columnLabel = tmpColumn.getLabelAttribute();
					writer.startElement("div", component);
					writer.writeAttribute("class", "calendar-header-cell unselectable", null);
					writer.writeAttribute("data-full-title", columnLabel, null);
					writer.writeAttribute("aria-hidden", "true", null);
					writer.endElement("div");
				}
				writer.endElement("div");
			}
		}
		List<UIComponent> items = null;
		final UIComponent itemsFacet = calendar.getFacet("items");
		if (itemsFacet != null) {
			items = itemsFacet.getChildren();
		}
		if (rows != null) {
			final List<UIComponent> rowItems = rows.getChildren();
			if (rowItems != null) {
				int totalItemIndex = 0;
				int rowIndex = -1;
				for (final UIComponent rowItem : rowItems) {
					if (!(UICalendarRowItem.class).isAssignableFrom(rowItem.getClass()))
						continue;
					rowIndex = rowIndex + 1;
					if (!rowItem.isRendered())
						continue;
					String rowClasses = "row carousel-item";
					if (rowIndex == 0) {
						rowClasses = rowClasses + " active";
					}
					writer.startElement("div", component);
					writer.writeAttribute("class", rowClasses, null);
					writer.startElement("div", component);
					writer.writeAttribute("class", "calendar-row-container", null);
					writer.writeAttribute("data-legend", component.getClientId(context) + "_legend_modal_legends",
							null);
					final UICalendarRowItem tmpRow = (UICalendarRowItem) rowItem;
					final String rowLabel = tmpRow.getLabelAttribute();
					writer.startElement("div", component);
					writer.writeAttribute("class", "calendar-row", null);
					writer.writeAttribute("name", rowLabel.replace(' ', '_'), null);
					writer.writeAttribute("id", rowLabel.replace(' ', '_'), null);
					writer.writeAttribute("data-rowindex", rowIndex, null);
					writer.startElement("div", component);
					writer.writeAttribute("class", "calendar-cell row-title", null);
					writer.writeAttribute("data-title", rowLabel, null);
					writer.writeAttribute("aria-hidden", "true", null);
					writer.endElement("div");
					if (columns != null) {
						final List<UIComponent> columnItems = columns.getChildren();
						if (columnItems != null) {
							int colIndex = -1;
							for (final UIComponent columnItem : columnItems) {
								if (!(UICalendarColumnItem.class).isAssignableFrom(columnItem.getClass()))
									continue;
								colIndex = colIndex + 1;
								final Map<String, UIComponent> slotMap = new HashMap<String, UIComponent>();
								UICalendarItem item = null;
								if ((items != null) && (totalItemIndex < items.size())) {
									item = (UICalendarItem) items.get(totalItemIndex);
									if (item != null) {
										final List<UIComponent> slots = JsfUtility.getInstance().getComposite(item)
												.getChildren();
										for (final UIComponent slot : slots) {
											slotMap.put(slot.getId(), slot);
										}
									}
								}
								boolean itemExists = false;
								boolean itemIsRendered = true;
								if (item != null) {
									itemExists = true;
									itemIsRendered = item.isRendered();
								}
								totalItemIndex = totalItemIndex + 1;
								if (!columnItem.isRendered())
									continue;
								final UICalendarColumnItem tmpColumn = (UICalendarColumnItem) columnItem;
								final String columnLabel = tmpColumn.getLabelAttribute();
								writer.startElement("div", component);
								if (itemIsRendered)
									writer.writeAttribute("class", "calendar-cell", null);
								else
									writer.writeAttribute("class", "calendar-cell calendar-cell-blocked", null);
								final String cellId = rowIndex + "_" + colIndex;
								writer.writeAttribute("name", cellId, null);
								writer.writeAttribute("id", cellId, null);
								writer.writeAttribute("data-cellindex", totalItemIndex, null);
								if (itemExists) {
									writer.writeAttribute("data-item", item.getClientId(), null);
								}
								writer.startElement("div", component);
								writer.writeAttribute("class", "cover", null);
								writer.endElement("div");
								final UIComponent legend = calendar.getFacet("legend");
								if (legend != null) {
									if (itemIsRendered) {
										final List<UIComponent> legendItems = legend.getChildren();
										if (legendItems != null) {
											int slotId = 0;
											for (final UIComponent legendItem : legendItems) {
												if (!(UICalendarLegendItem.class)
														.isAssignableFrom(legendItem.getClass()))
													continue;
												slotId = slotId + 1;
												if (!legendItem.isRendered())
													continue;
												final UICalendarLegendItem tmp = (UICalendarLegendItem) legendItem;
												final String label = tmp.getLabelAttribute();
												writer.startElement("label", component);
												writer.writeAttribute("class", "calendar-slot", null);
												if (slotMap.containsKey("slot" + slotId)) {
													slotMap.get("slot" + slotId).setRendered(true);
													slotMap.get("slot" + slotId).encodeAll(context);
												}
												writer.startElement("span", component);
												writer.writeAttribute("class", "sr-only", null);
												writer.write(label + " - " + columnLabel + " " + rowLabel);
												writer.endElement("span");
												writer.endElement("label");
											}
										}
									}
								}
								writer.endElement("div");
							}
						}
					}
					writer.endElement("div");
					writer.endElement("div");
					writer.endElement("div");
				}
			}
		}
		writer.endElement("div");
		writer.startElement("div", component);
		writer.writeAttribute("class", "calendar-control", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "carousel-control-outer hidden-md-up", null);
		writer.startElement("a", component);
		writer.writeAttribute("class", "btn btn-secondary carousel-control-prev", null);
		writer.writeAttribute("href", "#" + carouselId, null);
		writer.writeAttribute("role", "button", null);
		writer.writeAttribute("data-slide", "prev", null);
		writer.startElement("span", component);
		writer.writeAttribute("class", "label", null);
		writer.endElement("span");
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
		writer.writeAttribute("href", "#" + carouselId, null);
		writer.writeAttribute("role", "button", null);
		writer.writeAttribute("data-slide", "next", null);
		writer.startElement("i", component);
		writer.writeAttribute("class", "fa fa-angle-right", null);
		writer.writeAttribute("aria-hidden", "true", null);
		writer.endElement("i");
		writer.startElement("span", component);
		writer.writeAttribute("class", "label", null);
		writer.endElement("span");
		writer.startElement("span", component);
		writer.writeAttribute("class", "sr-only", null);
		writer.write("Next");
		writer.endElement("span");
		writer.endElement("a");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("article");
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
