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
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarTile;
@FacesRenderer(componentFamily = UICalendarTile.COMPONENT_FAMILY, rendererType = TileRenderer.RENDERER_TYPE)
public class TileRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TileRenderer.class);
	public static final String RENDERER_TYPE = "de.his.zofar.Calendar.Tile";
	final HtmlGraphicImage clearImage;
	public TileRenderer() {
		super();
		clearImage = new HtmlGraphicImage();
		clearImage.setValue("/images/Clear.gif");
		clearImage.setWidth("1px");
		clearImage.setHeight("1px");
	}
	private synchronized void slotHelper(final FacesContext context, final String name,
			final String label, final boolean labelAll, final String style,
			UIComponent component, ResponseWriter writer) throws IOException {
		writer.startElement("tr", component);
		writer.writeAttribute("id", name + component.getClientId(), null);
		writer.writeAttribute("style", style, null);
		writer.writeAttribute("class", "unselectable zo-calendar-tile-table-"
				+ name.toLowerCase() + " zo-calendar-tile-table-slot", null);
		writer.startElement("td ", component);
		writer.writeAttribute("id", name + component.getClientId()+"_cell", null);
		writer.writeAttribute("align", "center", null);
		writer.writeAttribute("valign", "middle", null);
		writer.writeAttribute("class", "unselectable", null);
		writer.writeAttribute("height", "5px", null);
		writer.writeAttribute("colspan", "3", null);
		if (labelAll)
			writer.write(label);
		else {
		}
		writer.endElement("td");
		writer.endElement("tr");
	}
	@Override
	public synchronized void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered())
			return;
		String slot1Label = (String) component.getAttributes()
				.get("slot1Label");
		String slot2Label = (String) component.getAttributes()
				.get("slot2Label");
		String slot3Label = (String) component.getAttributes()
				.get("slot3Label");
		String slot4Label = (String) component.getAttributes()
				.get("slot4Label");
		String slot5Label = (String) component.getAttributes()
				.get("slot5Label");
		String slot6Label = (String) component.getAttributes()
				.get("slot6Label");
		String slot7Label = (String) component.getAttributes()
				.get("slot7Label");
		String slot8Label = (String) component.getAttributes()
				.get("slot8Label");
		String slot9Label = (String) component.getAttributes()
				.get("slot9Label");
		String slot10Label = (String) component.getAttributes().get(
				"slot10Label");
		String slot11Label = (String) component.getAttributes().get(
				"slot11Label");
		String slot12Label = (String) component.getAttributes().get(
				"slot12Label");
		String slot13Label = (String) component.getAttributes().get(
				"slot13Label");
		String slot14Label = (String) component.getAttributes().get(
				"slot14Label");
		String slot15Label = (String) component.getAttributes().get(
				"slot15Label");
		String slot16Label = (String) component.getAttributes().get(
				"slot16Label");
		String slot17Label = (String) component.getAttributes().get(
				"slot17Label");
		String slot18Label = (String) component.getAttributes().get(
				"slot18Label");
		String slot19Label = (String) component.getAttributes().get(
				"slot19Label");
		String slot20Label = (String) component.getAttributes().get(
				"slot20Label");
		String behaviour = (String) component.getAttributes().get("behaviour");
		if ((behaviour == null) || ((behaviour.trim()).equals("")))
			behaviour = "default";
		boolean labelAll = (boolean) component.getParent().getParent()
				.getParent().getAttributes().get("labelAll");
		final String hiddenStyle = "position: absolute !important;top: -9999px !important;left: -9999px !important;";
		String slot1Style = "";
		if (slot1Label == null)
			slot1Style = hiddenStyle;
		else if (slot1Label.equals("null"))
			slot1Style = hiddenStyle;
		else if ((slot1Label.trim()).equals(""))
			slot1Style = hiddenStyle;
		String slot2Style = "";
		if (slot2Label == null)
			slot2Style = hiddenStyle;
		else if (slot2Label.equals("null"))
			slot2Style = hiddenStyle;
		else if ((slot2Label.trim()).equals(""))
			slot2Style = hiddenStyle;
		String slot3Style = "";
		if (slot3Label == null)
			slot3Style = hiddenStyle;
		else if (slot3Label.equals("null"))
			slot3Style = hiddenStyle;
		else if ((slot3Label.trim()).equals(""))
			slot3Style = hiddenStyle;
		String slot4Style = "";
		if (slot4Label == null)
			slot4Style = hiddenStyle;
		else if (slot4Label.equals("null"))
			slot4Style = hiddenStyle;
		else if ((slot4Label.trim()).equals(""))
			slot4Style = hiddenStyle;
		String slot5Style = "";
		if (slot5Label == null)
			slot5Style = hiddenStyle;
		else if (slot5Label.equals("null"))
			slot5Style = hiddenStyle;
		else if ((slot5Label.trim()).equals(""))
			slot5Style = hiddenStyle;
		String slot6Style = "";
		if (slot6Label == null)
			slot6Style = hiddenStyle;
		else if (slot6Label.equals("null"))
			slot6Style = hiddenStyle;
		else if ((slot6Label.trim()).equals(""))
			slot6Style = hiddenStyle;
		String slot7Style = "";
		if (slot7Label == null)
			slot7Style = hiddenStyle;
		else if (slot7Label.equals("null"))
			slot7Style = hiddenStyle;
		else if ((slot7Label.trim()).equals(""))
			slot7Style = hiddenStyle;
		String slot8Style = "";
		if (slot8Label == null)
			slot8Style = hiddenStyle;
		else if (slot8Label.equals("null"))
			slot8Style = hiddenStyle;
		else if ((slot8Label.trim()).equals(""))
			slot8Style = hiddenStyle;
		String slot9Style = "";
		if (slot9Label == null)
			slot9Style = hiddenStyle;
		else if (slot9Label.equals("null"))
			slot9Style = hiddenStyle;
		else if ((slot9Label.trim()).equals(""))
			slot9Style = hiddenStyle;
		String slot10Style = "";
		if (slot10Label == null)
			slot10Style = hiddenStyle;
		else if (slot10Label.equals("null"))
			slot10Style = hiddenStyle;
		else if ((slot10Label.trim()).equals(""))
			slot10Style = hiddenStyle;
		String slot11Style = "";
		if (slot11Label == null)
			slot11Style = hiddenStyle;
		else if (slot11Label.equals("null"))
			slot11Style = hiddenStyle;
		else if ((slot11Label.trim()).equals(""))
			slot11Style = hiddenStyle;
		String slot12Style = "";
		if (slot12Label == null)
			slot12Style = hiddenStyle;
		else if (slot12Label.equals("null"))
			slot12Style = hiddenStyle;
		else if ((slot12Label.trim()).equals(""))
			slot12Style = hiddenStyle;
		String slot13Style = "";
		if (slot13Label == null)
			slot13Style = hiddenStyle;
		else if (slot13Label.equals("null"))
			slot13Style = hiddenStyle;
		else if ((slot13Label.trim()).equals(""))
			slot13Style = hiddenStyle;
		String slot14Style = "";
		if (slot14Label == null)
			slot14Style = hiddenStyle;
		else if (slot14Label.equals("null"))
			slot14Style = hiddenStyle;
		else if ((slot14Label.trim()).equals(""))
			slot14Style = hiddenStyle;
		String slot15Style = "";
		if (slot15Label == null)
			slot15Style = hiddenStyle;
		else if (slot15Label.equals("null"))
			slot15Style = hiddenStyle;
		else if ((slot15Label.trim()).equals(""))
			slot15Style = hiddenStyle;
		String slot16Style = "";
		if (slot16Label == null)
			slot16Style = hiddenStyle;
		else if (slot16Label.equals("null"))
			slot16Style = hiddenStyle;
		else if ((slot16Label.trim()).equals(""))
			slot16Style = hiddenStyle;
		String slot17Style = "";
		if (slot17Label == null)
			slot17Style = hiddenStyle;
		else if (slot17Label.equals("null"))
			slot17Style = hiddenStyle;
		else if ((slot17Label.trim()).equals(""))
			slot17Style = hiddenStyle;
		String slot18Style = "";
		if (slot18Label == null)
			slot18Style = hiddenStyle;
		else if (slot18Label.equals("null"))
			slot18Style = hiddenStyle;
		else if ((slot18Label.trim()).equals(""))
			slot18Style = hiddenStyle;
		String slot19Style = "";
		if (slot19Label == null)
			slot19Style = hiddenStyle;
		else if (slot19Label.equals("null"))
			slot19Style = hiddenStyle;
		else if ((slot19Label.trim()).equals(""))
			slot19Style = hiddenStyle;
		String slot20Style = "";
		if (slot20Label == null)
			slot20Style = hiddenStyle;
		else if (slot20Label.equals("null"))
			slot20Style = hiddenStyle;
		else if ((slot20Label.trim()).equals(""))
			slot20Style = hiddenStyle;
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("table", component);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("width", "100%", null);
		writer.writeAttribute("height", "100%", null);
		writer.writeAttribute("border", "0", null);
		writer.writeAttribute("cellpadding", "0", null);
		writer.writeAttribute("cellspacing", "0", null);
		writer.startElement("tr", component);
		writer.startElement("td ", component);
		writer.writeAttribute("id", "FrameCell" + component.getClientId(), null);
		final UIComponent slotCheck = getSlotCheck(component);
		if (slotCheck != null)
			slotCheck.encodeAll(context);
		writer.startElement("table", component);
		writer.writeAttribute("id", "TileTable" + component.getClientId(), null);
		writer.writeAttribute("width", "100%", null);
		writer.writeAttribute("height", "100%", null);
		writer.writeAttribute("style",
				"border-width:1px;border-style: solid;", null);
		writer.writeAttribute("class", "zo-calendar-tile-table", null);
		writer.writeAttribute("cellpadding", "0", null);
		writer.writeAttribute("cellspacing", "0", null);
		if (behaviour.equals("drag")) {
			writer.writeAttribute("onmousedown", "javascript:startMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId() + "')", null);
			writer.writeAttribute("onmouseup", "javascript:stopMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId() + "')", null);
		} else if (behaviour.equals("click")) {
			writer.writeAttribute("onclick", "javascript:startMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId()
					+ "');javascript:stopMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId() + "');", null);
		} else if (behaviour.equals("move")) {
			writer.writeAttribute("onmousedown", "javascript:startMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId()
					+ "');javascript:stopMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId() + "');flagDown_"
					+ component.getParent().getParent().getParent().getId()
					+ "();", null);
			writer.writeAttribute("onmouseover", "javascript:overMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId() + "')", null);
			writer.writeAttribute("onmouseup", "javascript:flagUp_"
					+ component.getParent().getParent().getParent().getId()
					+ "();", null);
		} else if (behaviour.equals("default")) {
			writer.writeAttribute("onmousedown", "javascript:startMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId() + "')", null);
			writer.writeAttribute("onmouseup", "javascript:stopMark_"
					+ component.getParent().getParent().getParent().getId()
					+ "('" + component.getClientId() + "')", null);
		}
		final String labelStr = (String) component.getAttributes().get("label");
		if ((labelStr != null) && (!labelStr.equals(""))) {
			writer.startElement("tr", component);
			writer.writeAttribute("id", "Slot0" + component.getClientId(), null);
			writer.writeAttribute("style",
					"border-width:1px;border-bottom-style: dotted;", null);
			writer.writeAttribute(
					"class",
					"unselectable zo-calendar-tile-table-header zo-calendar-tile-table-slot",
					null);
			writer.startElement("td ", component);
			writer.writeAttribute("class", "unselectable", null);
			clearImage.encodeAll(context);
			writer.endElement("td");
			writer.startElement("td ", component);
			writer.writeAttribute("class", "unselectable", null);
			clearImage.encodeAll(context);
			writer.endElement("td");
			writer.startElement("td ", component);
			writer.writeAttribute("align", "right", null);
			writer.writeAttribute("valign", "top", null);
			writer.writeAttribute("class", "unselectable", null);
			writer.writeAttribute("style",
					"padding-right:20px;padding-top:10px", null);
			writer.write((String) component.getAttributes().get("label"));
			writer.endElement("td");
			writer.endElement("tr");
		}
		slotHelper(context, "Slot1", slot1Label, labelAll, slot1Style,
				component, writer);
		slotHelper(context, "Slot2", slot2Label, labelAll, slot2Style,
				component, writer);
		slotHelper(context, "Slot3", slot3Label, labelAll, slot3Style,
				component, writer);
		slotHelper(context, "Slot4", slot4Label, labelAll, slot4Style,
				component, writer);
		slotHelper(context, "Slot5", slot5Label, labelAll, slot5Style,
				component, writer);
		slotHelper(context, "Slot6", slot6Label, labelAll, slot6Style,
				component, writer);
		slotHelper(context, "Slot7", slot7Label, labelAll, slot7Style,
				component, writer);
		slotHelper(context, "Slot8", slot8Label, labelAll, slot8Style,
				component, writer);
		slotHelper(context, "Slot9", slot9Label, labelAll, slot9Style,
				component, writer);
		slotHelper(context, "Slot10", slot10Label, labelAll, slot10Style,
				component, writer);
		slotHelper(context, "Slot11", slot11Label, labelAll, slot11Style,
				component, writer);
		slotHelper(context, "Slot12", slot12Label, labelAll, slot12Style,
				component, writer);
		slotHelper(context, "Slot13", slot13Label, labelAll, slot13Style,
				component, writer);
		slotHelper(context, "Slot14", slot14Label, labelAll, slot14Style,
				component, writer);
		slotHelper(context, "Slot15", slot15Label, labelAll, slot15Style,
				component, writer);
		slotHelper(context, "Slot16", slot16Label, labelAll, slot16Style,
				component, writer);
		slotHelper(context, "Slot17", slot17Label, labelAll, slot17Style,
				component, writer);
		slotHelper(context, "Slot18", slot18Label, labelAll, slot18Style,
				component, writer);
		slotHelper(context, "Slot19", slot19Label, labelAll, slot19Style,
				component, writer);
		slotHelper(context, "Slot20", slot20Label, labelAll, slot20Style,
				component, writer);
		writer.endElement("table");
		insertJavaScript(context, component);
		writer.endElement("td");
		writer.endElement("tr");
		writer.endElement("table");
	}
	@Override
	public synchronized void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered())
			return;
	}
	@Override
	public synchronized void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered())
			return;
		super.encodeEnd(context, component);
	}
	@Override
	public synchronized boolean getRendersChildren() {
		return true;
	}
	private synchronized UIComponent getSlotCheck(UIComponent component) {
		if (component == null)
			return null;
		if (!UIComponent.isCompositeComponent(component))
			return null;
		final UIComponent slotCheck = ((UINamingContainer) component)
				.getFacet("slotCheck");
		return slotCheck;
	}
	private synchronized void insertJavaScript(final FacesContext context,
			final UIComponent component) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", null);
		String script = "init_"
				+ component.getParent().getParent().getParent().getId() + "('"
				+ component.getClientId() + "', '" + component.getClientId()
				+ ":slot1Check','" + component.getClientId()
				+ ":slot2Check', '" + component.getClientId()
				+ ":slot3Check','" + component.getClientId() + ":slot4Check','"
				+ component.getClientId() + ":slot5Check','"
				+ component.getClientId() + ":slot6Check','"
				+ component.getClientId() + ":slot7Check','"
				+ component.getClientId() + ":slot8Check','"
				+ component.getClientId() + ":slot9Check','"
				+ component.getClientId() + ":slot10Check','"
				+ component.getClientId() + ":slot11Check','"
				+ component.getClientId() + ":slot12Check','"
				+ component.getClientId() + ":slot13Check','"
				+ component.getClientId() + ":slot14Check','"
				+ component.getClientId() + ":slot15Check','"
				+ component.getClientId() + ":slot16Check','"
				+ component.getClientId() + ":slot17Check','"
				+ component.getClientId() + ":slot18Check','"
				+ component.getClientId() + ":slot19Check','"
				+ component.getClientId() + ":slot20Check');\n";
		writer.write(script);
		writer.endElement("script");
	}
}
