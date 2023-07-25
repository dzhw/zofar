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
package de.his.zofar.presentation.surveyengine.ui.renderer.display;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.display.UIDisplayTable;
import de.his.zofar.presentation.surveyengine.ui.components.display.UIDisplayTableBody;
import de.his.zofar.presentation.surveyengine.ui.components.display.UIDisplayTableHeader;
import de.his.zofar.presentation.surveyengine.ui.components.display.UIDisplayTableItem;
import de.his.zofar.presentation.surveyengine.ui.components.display.UIDisplayTableRow;

/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIDisplayTable.COMPONENT_FAMILY, rendererType = TableRenderer2.RENDERER_TYPE)
public class TableRenderer2 extends Renderer {

	private static final Logger LOGGER = LoggerFactory.getLogger(TableRenderer2.class);

	public static final String RENDERER_TYPE = "org.zofar.display.table.responsive";

	public TableRenderer2() {
		super();
	}

	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (component == null) {
			return;
		}
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();

		writer.startElement("article", component);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);

	}

	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (component == null) {
			return;
		}
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId().replace(':', '_') + "carousel", null);
		writer.writeAttribute("class", "zo-display-table carousel slide", null);
		writer.writeAttribute("data-interval", "false", null);

		this.encodeChildrenHelper(context, component, component.getChildren(), this.retrieveColumnCount(component));

		writer.endElement("div");
	}

	private void encodeChildrenHelper(final FacesContext context, final UIComponent component,
			final List<UIComponent> children, final int colCount) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		for (final UIComponent child : children) {
			if ((UIDisplayTableHeader.class).isAssignableFrom(child.getClass())) {
				this.addHeader(context, (UIDisplayTableHeader) child, writer, colCount);
			}
			if ((UIDisplayTableBody.class).isAssignableFrom(child.getClass())) {
				this.addBody(context, (UIDisplayTableBody) child, writer, colCount);
			}
		}
	}

	private void addHeader(FacesContext context, UIDisplayTableHeader component, ResponseWriter writer, int colCount)
			throws IOException {
		if (component == null) {
			return;
		}
		if (!component.isRendered()) {
			return;
		}
		writer.startElement("div", component);
		writer.writeAttribute("class", "zo-display-table-header", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "zo-display-table-header-row hidden-sm-down", null);
		int lft = 0;
		for (final UIComponent child : component.getChildren()) {
			writer.startElement("div", component);
			writer.writeAttribute("id", lft, null);
			writer.writeAttribute("class", "zo-display-table-header-item col-md-4", null);
			child.encodeAll(context);
			writer.endElement("div");
			lft = lft + 1;
		}
		while (lft < colCount) {
			writer.startElement("div", component);
			writer.writeAttribute("class", "zo-display-table-header-item col-md-4", null);
			writer.endElement("div");
			lft = lft + 1;
		}
		writer.endElement("div");
		writer.endElement("div");
	}

	private void addBody(FacesContext context, UIDisplayTableBody component, ResponseWriter writer, int colCount)
			throws IOException {
		if (component == null) {
			return;
		}
		if (!component.isRendered()) {
			return;
		}

		writer.startElement("div", component);
		writer.writeAttribute("class", "zo-display-table-body carousel-inner", null);
		writer.writeAttribute("role", "listbox", null);

		boolean firstFlag = true;
		for (final UIComponent child : component.getChildren()) {
			if ((UIDisplayTableRow.class).isAssignableFrom(child.getClass())) {
				this.addRow(context, (UIDisplayTableRow) child, writer, colCount, firstFlag);
				firstFlag = false;
			}
		}
		writer.endElement("div");
	}

	private void addRow(final FacesContext context, final UIDisplayTableRow component, final ResponseWriter writer,
			final int colCount, final boolean active) throws IOException {
		if (component == null) {
			return;
		}
		if (!component.isRendered()) {
			return;
		}
		writer.startElement("div", component);
		String classes = "zo-display-table-body-row row carousel-item";
		if (active)
			classes += " active";
		writer.writeAttribute("class", classes, null);

		int lft = 0;
		for (final UIComponent child : component.getChildren()) {
			if ((UIDisplayTableItem.class).isAssignableFrom(child.getClass())) {
				this.addItem(context, (UIDisplayTableItem) child, writer, lft);
				lft = lft + 1;
			}
		}
		while (lft < colCount) {
			this.addItem(context, null, writer, -1);
			lft = lft + 1;
		}
		writer.endElement("div");
	}

	private void addItem(final FacesContext context, final UIDisplayTableItem component, final ResponseWriter writer,
			final int col) throws IOException {
		writer.startElement("span", component);
		writer.writeAttribute("class", "zo-display-table-body-item-label hidden-md-up", null);
		writer.writeAttribute("data-label", col+"", null);
		writer.endElement("span");

		writer.startElement("div", component);
		writer.writeAttribute("class", "zo-display-table-body-item col-md-4", null);

		if (component != null) {
			if (component.isRendered()) {
				for (final UIComponent child : component.getChildren()) {
					child.encodeAll(context);
				}
			}


		}
		writer.endElement("div");
	}

	private int retrieveColumnCount(final UIComponent component) throws IOException {
		int count = 0;
		for (final UIComponent child : component.getChildren()) {
			if ((UIDisplayTableHeader.class).isAssignableFrom(child.getClass())) {
				final int headerCount = child.getChildCount();
				count = Math.max(count, headerCount);
			}
			if ((UIDisplayTableBody.class).isAssignableFrom(child.getClass())) {
				int maxItemCount = 0;
				for (final UIComponent row : child.getChildren()) {
					maxItemCount = Math.max(row.getChildCount(), maxItemCount);
				}
				count = Math.max(count, maxItemCount);
			}
		}
		return count;
	}

	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (component == null) {
			return;
		}
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("div", component);
		writer.writeAttribute("class", "carousel-control-outer hidden-md-up", null);
		
		writer.startElement("a", component);
		writer.writeAttribute("class", "btn btn-secondary carousel-control-prev", null);
		writer.writeAttribute("href", "#"+component.getClientId().replace(':', '_') + "carousel", null);
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
		writer.writeAttribute("href", "#"+component.getClientId().replace(':', '_') + "carousel", null);
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

		writer.endElement("div");
		writer.endElement("article");
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
