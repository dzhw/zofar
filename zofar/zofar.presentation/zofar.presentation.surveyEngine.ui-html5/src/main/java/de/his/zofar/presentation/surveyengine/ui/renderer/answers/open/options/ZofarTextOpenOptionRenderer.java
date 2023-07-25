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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.open.options;
import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import com.sun.faces.facelets.compiler.UIInstructions;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.OpenOption;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
@FacesRenderer(componentFamily = OpenOption.COMPONENT_FAMILY, rendererType = ZofarTextOpenOptionRenderer.RENDERER_TYPE)
public class ZofarTextOpenOptionRenderer extends Renderer {
	public static final String RENDERER_TYPE = "org.zofar.TextOpenOption";
	public ZofarTextOpenOptionRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		writer.writeAttribute("id", component.getClientId(context), null);
		String forId= null;
		final UIComponent tmp = JsfUtility.getInstance().getComposite(component);
		if (tmp != null) {
			for (final UIComponent child : tmp.getChildren()) {
				if ((javax.faces.component.html.HtmlInputText.class).isAssignableFrom(child.getClass())) {
					forId = child.getClientId(context);
					break;
				}
			}
		}
		if(forId != null)writer.writeAttribute("for", forId, null);
		final UIComponent prefix = component.getFacet("prefix");
		if ((prefix != null)&&(prefix.isRendered())) {
			writer.startElement("span", component);
			writer.writeAttribute("id", prefix.getClientId(context), null);
			writer.writeAttribute("class", "prefix", null);
			if (UIInstructions.class.isAssignableFrom(prefix.getClass())) {
				writer.write(" " + JsfUtility.getInstance().evaluateValueExpression(context, String.valueOf(prefix), String.class) + " ");
			} else {
				prefix.encodeAll(context);
			}
			writer.endElement("span");
		}
		if(component.getAttributes().containsKey("zofar-column")) {
			final UIComponent column = (UIComponent) component.getAttributes().get("zofar-column");
			writer.startElement("span", component);
			writer.writeAttribute("id", forId+"_column", null);
			writer.writeAttribute("class", "column", null);
			column.encodeAll(context);
			writer.endElement("span");		
		}
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final UIComponent tmp = JsfUtility.getInstance().getComposite(component);
		if (tmp != null) {
			for (final UIComponent child : tmp.getChildren()) {
				if ((javax.faces.component.html.HtmlInputText.class).isAssignableFrom(child.getClass())) {
					child.encodeAll(context);
				} else if((javax.faces.component.html.HtmlInputTextarea.class).isAssignableFrom(child.getClass())){
					child.encodeAll(context);
				}
			}
		}
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent postfix = component.getFacet("postfix");
		if ((postfix != null)&&(postfix.isRendered())) {
			writer.startElement("span", component);
			writer.writeAttribute("id", postfix.getClientId(context), null);
			writer.writeAttribute("class", "postfix", null);
			if (UIInstructions.class.isAssignableFrom(postfix.getClass())) {
				writer.write(" " + JsfUtility.getInstance().evaluateValueExpression(context, String.valueOf(postfix), String.class));
			} else {
				postfix.encodeAll(context);
			}
			writer.endElement("span");
		}
		final UIComponent tmp = JsfUtility.getInstance().getComposite(component);
		if (tmp != null) {
			for (final UIComponent child : tmp.getChildren()) {
				if ((javax.faces.component.html.HtmlMessage.class).isAssignableFrom(child.getClass())) {
					child.encodeAll(context);
				}
			}
		}
		writer.endElement("label");
	}
}
