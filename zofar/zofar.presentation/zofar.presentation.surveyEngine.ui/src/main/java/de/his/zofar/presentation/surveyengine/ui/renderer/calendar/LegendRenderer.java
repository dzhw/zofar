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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarLegend;

@FacesRenderer(componentFamily = UICalendarLegend.COMPONENT_FAMILY, rendererType = LegendRenderer.RENDERER_TYPE)
public class LegendRenderer extends Renderer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LegendRenderer.class);

	public static final String RENDERER_TYPE = "de.his.zofar.Calendar.Legend";
	final HtmlGraphicImage clearImage;

	public LegendRenderer() {
		super();
		clearImage = new HtmlGraphicImage();
		clearImage.setValue("/images/Clear.gif");
		clearImage.setWidth("3px");
		clearImage.setHeight("3px");
	}

	@Override
	public synchronized void encodeBegin(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered())
			return;
		if (!(UICalendarLegend.class).isAssignableFrom(component.getClass()))
			return;
		final boolean legendIconFlag = ((UICalendarLegend) component)
				.getLegendIconAttribute();
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		String css = "calendar-legende-radio";
		if (!legendIconFlag)
			css += " calendar-legende-radio-no-pointer";
		writer.writeAttribute("class", css, null);

	}

	@Override
	public synchronized void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered())
			return;
		if (!(UICalendarLegend.class).isAssignableFrom(component.getClass()))
			return;
		final ResponseWriter writer = context.getResponseWriter();

		final String layout = ((UICalendarLegend) component)
				.getLayoutAttribute();
		final boolean indicatorFlag = ((UICalendarLegend) component)
				.getIndicatorAttribute();
		final boolean legendIconFlag = ((UICalendarLegend) component)
				.getLegendIconAttribute();

		if (layout != null) {
			if (layout.equals("flowDirection")) {
				for (final UIComponent item : component.getChildren()) {
					renderLegendItem(context, writer, item, component, layout,
							indicatorFlag, legendIconFlag);
				}
			} else {
				final Set<Set<UIComponent>> items = new LinkedHashSet<Set<UIComponent>>();

				if (layout.equals("lineDirection")) {
					final Set<UIComponent> row = new LinkedHashSet<UIComponent>();
					for (final UIComponent child : component.getChildren()) {
						row.add(child);
					}
					items.add(row);
				} else if (layout.equals("pageDirection")) {
					for (final UIComponent child : component.getChildren()) {
						final Set<UIComponent> row = new LinkedHashSet<UIComponent>();
						row.add(child);
						items.add(row);
					}
				} else if (layout.equals("2Columns")) {
					items.addAll(createColumnLayout(2, component));
				} else if (layout.equals("3Columns")) {
					items.addAll(createColumnLayout(3, component));
				} else if (layout.equals("4Columns")) {
					items.addAll(createColumnLayout(4, component));
				}
				writer.startElement("table", component);
				writer.writeAttribute("border", "0", null);

				for (final Set<UIComponent> row : items) {
					if (row.isEmpty())
						continue;
					writer.startElement("tr", component);
					if ((layout != null) && (layout.equals("flowDirection"))) {
						writer.startElement("td", component);
					}
					for (final UIComponent item : row) {
						renderLegendItem(context, writer, item, component,
								layout, indicatorFlag, legendIconFlag);
					}
					if ((layout != null) && (layout.equals("flowDirection"))) {
						writer.endElement("td");
					}
					writer.endElement("tr");
				}
				writer.endElement("table");
			}
		}
	}

	private Set<Set<UIComponent>> createColumnLayout(final int columnCount,
			final UIComponent component) {
		int count = 0;

		for (final UIComponent child : component.getChildren()) {
			if ((UISelectItem.class).isAssignableFrom(child.getClass())) {
				final UISelectItem tmp = (UISelectItem) child;
				final String itemLabel = tmp.getItemLabel();
				if (itemLabel == null)
					continue;
				if (itemLabel.equals("null"))
					continue;
				count = count + 1;
			}
		}

		final float tmpGroupCount = ((float) count / (float) columnCount);
		final int groupCount = (int) Math.ceil(tmpGroupCount);

		final UIComponent[][] matrix = new UIComponent[groupCount][columnCount];
		int groupIndex = 0;
		int columnIndex = 0;
		for (final UIComponent child : component.getChildren()) {
			if ((UISelectItem.class).isAssignableFrom(child.getClass())) {
				final UISelectItem tmp = (UISelectItem) child;
				final String itemLabel = tmp.getItemLabel();
				if (itemLabel == null)
					continue;
				if (itemLabel.equals("null"))
					continue;
				matrix[groupIndex][columnIndex] = child;
				groupIndex = groupIndex + 1;
				if (groupIndex >= groupCount) {
					groupIndex = 0;
					columnIndex = columnIndex + 1;
				}
			}
		}

		final Set<Set<UIComponent>> rows = new LinkedHashSet<Set<UIComponent>>();
		for (final UIComponent[] matrixRow : matrix) {
			final Set<UIComponent> row = new LinkedHashSet<UIComponent>();
			for (final UIComponent rowItem : matrixRow) {
				row.add(rowItem);
			}
			rows.add(row);
		}

		return rows;
	}

	final String hiddenStyle = "position: absolute !important;top: -9999px !important;left: -9999px !important;";

	private synchronized void renderLegendItem(final FacesContext context,
			final ResponseWriter writer, final UIComponent item,
			final UIComponent parent, final String layout,
			final boolean showIndicator, final boolean showLegendIcon)
			throws IOException {
		if (item == null)
			return;
		if ((UISelectItem.class).isAssignableFrom(item.getClass())) {
			final UISelectItem tmp = (UISelectItem) item;
			final String itemLabel = tmp.getItemLabel();

			if (itemLabel == null)
				return;
			if (itemLabel.equals("null"))
				return;
			if ((layout != null) && (!layout.equals("flowDirection"))) {
				writer.startElement("td", parent);
			}

			final Object color = tmp.getAttributes().get("color");
			final Object pattern = tmp.getAttributes().get("pattern");

			writer.startElement("table", item);
			writer.writeAttribute("cellpadding", "0", null);
			writer.writeAttribute("cellspacing", "5px", null);
			writer.writeAttribute("border", "0", null);
			writer.writeAttribute("height", "100%", null);

			if ((layout != null) && (layout.equals("flowDirection"))) {
				writer.writeAttribute("width", "0%", null);
				writer.writeAttribute(
						"style",
						"margin-bottom:2px;margin-right:10px;position:relative;float:left;",
						null);
			} else {
				writer.writeAttribute("width", "100%", null);
			}
			writer.startElement("tr", item);
			writer.startElement("td", item);
			writer.writeAttribute("width", "12px", null);
			String indicatorCellStyle = "min-width:12px;";
			writer.writeAttribute("style", indicatorCellStyle, null);

			String jsTrigger = "javascript:";
			if (showIndicator)
				jsTrigger += "toggleIndicator_" + parent.getParent().getId()
						+ "('" + tmp.getItemValue() + "');";
			if (showLegendIcon) {
				final String uuid = UUID.randomUUID().toString()
						.replaceAll("-", "");
				final String arrowId = "arrow" + uuid;
				final HtmlGraphicImage arrowRightImage = new HtmlGraphicImage();
				arrowRightImage
						.setValue(context
								.getApplication()
								.evaluateExpressionGet(
										context,
										"#{resource['images/calendar/arrowRight.png']}",
										String.class));

				arrowRightImage.setWidth("12px");
				arrowRightImage.setHeight("12px");
				arrowRightImage.setId(arrowId);
				arrowRightImage.setStyle("display:none;");
				arrowRightImage.encodeAll(context);

				writer.startElement("script", item);
				writer.writeAttribute("type", "text/javascript", null);
				String script = "registerPointer_"
						+ item.getParent().getParent().getId() + "('"
						+ arrowRightImage.getId() + "');\n";
				writer.write(script);
				writer.endElement("script");
				jsTrigger += "setPointer_" + parent.getParent().getId() + "('"
						+ arrowId + "');";
			}
			writer.endElement("td");

			writer.startElement("td", item);

			if ((layout != null) && (layout.equals("flowDirection"))) {
				writer.writeAttribute("width", "5px", null);
				writer.writeAttribute("style", "min-width:5px;", null);
			} else {
				writer.writeAttribute("width", "10px", null);
				writer.writeAttribute("style", "min-width:10px;", null);
			}
			writer.endElement("td");

			writer.startElement("td", item);
			writer.writeAttribute("width", "16px", null);
			String colorCellStyle = "min-width:16px;";
			if ((color != null) && (!color.equals("")))
				colorCellStyle = colorCellStyle + "background-color:" + color
						+ ";";

			if ((pattern != null) && (!pattern.equals("")))
				colorCellStyle = colorCellStyle + "background-image:url('"
						+ pattern + "');";
			writer.writeAttribute("style", colorCellStyle, null);
			writer.writeAttribute(
					"onclick",
					jsTrigger + "setRadioWert_" + parent.getParent().getId()
							+ "('" + parent.getClientId(context) + "','"
							+ tmp.getItemValue() + "');", null);
			writer.endElement("td");

			writer.startElement("td", item);
			writer.writeAttribute("width", "10px", null);
			writer.writeAttribute("style", "min-width:10px;", null);
			writer.writeAttribute(
					"onclick",
					jsTrigger + "setRadioWert_" + parent.getParent().getId()
							+ "('" + parent.getClientId(context) + "','"
							+ tmp.getItemValue() + "');", null);
			writer.endElement("td");

			writer.startElement("td", item);
			
			if (!showLegendIcon) {
				final String uuid = UUID.randomUUID().toString()
						.replaceAll("-", "");
				writer.writeAttribute("id", "cell_" + uuid, null);
				writer.writeAttribute(
						"onclick",
						jsTrigger + "setNotPointer_"
								+ parent.getParent().getId() + "('cell_" + uuid
								+ "');setRadioWert_"
								+ parent.getParent().getId() + "('"
								+ parent.getClientId(context) + "','"
								+ tmp.getItemValue() + "');", null);
				writer.writeAttribute(
						"style",
						"border-top-color: white;border-top-width: 1px;border-top-style: solid;border-bottom-color: white;border-bottom-width: 1px;border-bottom-style: solid;",
						null);
				writer.startElement("script", item);
				writer.writeAttribute("type", "text/javascript", null);
				String script = "registerNotPointer_"
						+ item.getParent().getParent().getId() + "('cell_"
						+ uuid + "');\n";
				writer.write(script);
				writer.endElement("script");
			}
			else{
				writer.writeAttribute(
						"onclick",
						jsTrigger + "setRadioWert_"
								+ parent.getParent().getId() + "('"
								+ parent.getClientId(context) + "','"
								+ tmp.getItemValue() + "');", null);
			}

			writer.startElement("input", item);
			writer.writeAttribute("name", parent.getClientId(context), null);
			writer.writeAttribute("id", tmp.getClientId(), null);
			writer.writeAttribute("type", "radio", null);
			writer.writeAttribute("value", tmp.getItemValue(), null);
			writer.writeAttribute("style", hiddenStyle, null);
			writer.endElement("input");
			writer.writeText(tmp.getItemLabel(), null);
			writer.endElement("td");

			writer.startElement("td", item);
			writer.writeAttribute("width", clearImage.getWidth(), null);
			clearImage.encodeAll(context);
			writer.endElement("td");
			writer.endElement("tr");

			writer.endElement("table");

			if ((layout != null) && (!layout.equals("flowDirection"))) {
				writer.endElement("td");
			}
		}
	}

	@Override
	public synchronized void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered())
			return;
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
	}

	@Override
	public synchronized boolean getRendersChildren() {
		return true;
	}
}
