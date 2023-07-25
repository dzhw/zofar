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
@FacesRenderer(componentFamily = UIDisplayTable.COMPONENT_FAMILY, rendererType = TableRenderer.RENDERER_TYPE)
public class TableRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TableRenderer.class);
	public static final String RENDERER_TYPE = "org.zofar.display.table";
	private int rowCounter;
	public TableRenderer() {
		super();
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		if(component == null)return;
		if (!component.isRendered())return;
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("table", component);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("style","vertical-align:middle;text-align:center;", null);
		writer.writeAttribute("class","zo-display-table", null);
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		if(component == null)return;
		if (!component.isRendered())return;
		this.encodeChildrenHelper(context, component, component.getChildren(),retrieveColumnCount(component));
	}
	private int retrieveColumnCount(
			final UIComponent component) throws IOException {
		int count = 0;
		for (final UIComponent child : component.getChildren()) {
			if((UIDisplayTableHeader.class).isAssignableFrom(child.getClass())){
				int headerCount = child.getChildCount();
				count = Math.max(count, headerCount);
			}
			if((UIDisplayTableBody.class).isAssignableFrom(child.getClass())){
				int maxItemCount = 0;
				for (final UIComponent row : child.getChildren()) {
					maxItemCount = Math.max(row.getChildCount(), maxItemCount);
				}
				count = Math.max(count, maxItemCount);
			}
		}
		return count;
	}
	private void encodeChildrenHelper(final FacesContext context,
			final UIComponent component, final List<UIComponent> children,final int colCount) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		for (final UIComponent child : children) {
			if((UIDisplayTableHeader.class).isAssignableFrom(child.getClass()))addHeader(context,(UIDisplayTableHeader)child,writer,colCount);
			if((UIDisplayTableBody.class).isAssignableFrom(child.getClass()))addBody(context,(UIDisplayTableBody)child,writer,colCount);
		}
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		if(component == null)return;
		if (!component.isRendered())return;
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("table");
	}
	private void addHeader(final FacesContext context,final UIDisplayTableHeader component,final ResponseWriter writer,final int colCount) throws IOException{
		if(component == null)return;
		if (!component.isRendered())return;
		writer.startElement("tr", component);
		writer.writeAttribute("id", component.getClientId(), null);
		int lft = 0;
		for (final UIComponent child : component.getChildren()) {
			writer.startElement("th", component);
			writer.writeAttribute("style","padding:4px;vertical-align:middle;text-align:center;", null);
			writer.writeAttribute("class","zo-display-table-header-item", null);
			writer.writeAttribute("id", child.getClientId(), null);
			child.encodeAll(context);
			writer.endElement("th");
			lft = lft + 1;
		}
		while(lft < colCount){
			writer.startElement("th", component);
			writer.writeAttribute("style","padding:4px;vertical-align:middle;text-align:center;", null);
			writer.writeAttribute("class","zo-display-table-header-item zo-display-table-header-item-empty", null);
			writer.endElement("th");
			lft = lft + 1;
		}
		writer.endElement("tr");
	}
	private void addBody(final FacesContext context,final UIDisplayTableBody component,final ResponseWriter writer,final int colCount) throws IOException{
		if(component == null)return;
		if (!component.isRendered())return;
		rowCounter=0;
		for (final UIComponent child : component.getChildren()) {
			if((UIDisplayTableRow.class).isAssignableFrom(child.getClass()))addRow(context,(UIDisplayTableRow)child,writer,colCount);
		}
	}
	private void addRow(final FacesContext context,final UIDisplayTableRow component,final ResponseWriter writer,final int colCount) throws IOException{
		if(component == null)return;
		if (!component.isRendered())return;
		writer.startElement("tr", component);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("class", "zo-display-row-"+rowCounter, null);
		int lft = 0;
		for (final UIComponent child : component.getChildren()) {
			if((UIDisplayTableItem.class).isAssignableFrom(child.getClass())){
				addItem(context,(UIDisplayTableItem)child,writer,colCount);
				lft = lft + 1;
			}
		}
		while(lft < colCount){
			writer.startElement("td", component);
			writer.writeAttribute("style","padding:4px;vertical-align:middle;text-align:center;", null);
			writer.writeAttribute("class","zo-display-table-body-item zo-display-table-body-item-empty", null);
			writer.endElement("td");
			lft = lft + 1;
		}
		writer.endElement("tr");
		rowCounter++;
	}
	private void addItem(final FacesContext context,final UIDisplayTableItem component,final ResponseWriter writer,final int colCount) throws IOException{
		if(component == null)return;
		if (!component.isRendered())return;
		writer.startElement("td", component);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("style","padding:4px;vertical-align:middle;text-align:center;", null);
		writer.writeAttribute("class","zo-display-table-body-item", null);
		for (final UIComponent child : component.getChildren()) {
			child.encodeAll(context);
		}
		writer.endElement("td");
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
