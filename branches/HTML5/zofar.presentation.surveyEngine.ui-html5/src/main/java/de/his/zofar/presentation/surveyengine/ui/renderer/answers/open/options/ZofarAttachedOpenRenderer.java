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
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.UIAttachedOpenQuestion;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
@FacesRenderer(componentFamily = UIAttachedOpenQuestion.COMPONENT_FAMILY, rendererType = ZofarAttachedOpenRenderer.RENDERER_TYPE)
public class ZofarAttachedOpenRenderer extends Renderer {
	public static final String RENDERER_TYPE = "org.zofar.AttachedOpenQuestion";
	public ZofarAttachedOpenRenderer() {
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
		writer.startElement("span", component);
		writer.writeAttribute("class", "attached", null);
		writer.writeAttribute("id", component.getClientId(context), null);
		final UIComponent prefix = component.getFacet("prefix");
		if (prefix != null) {
			writer.startElement("span", component);
			writer.writeAttribute("class", "prefix", null);
			if (UIInstructions.class.isAssignableFrom(prefix.getClass())) {
				writer.write(" " + JsfUtility.getInstance().evaluateValueExpression(context, String.valueOf(prefix), String.class) + " ");
			} else {
				prefix.encodeAll(context);
			}
			writer.endElement("span");
		}
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		for(Map.Entry<String,Object> tmpAttr : component.getAttributes().entrySet()) {
		}
		final UIComponent tmp = JsfUtility.getInstance().getComposite(component);
		if (tmp != null) {
			for(Map.Entry<String,Object> tmpAttr : tmp.getAttributes().entrySet()) {
			}
		}
		if (tmp != null) {
			for (final UIComponent child : tmp.getChildren()) {
				if ((javax.faces.component.html.HtmlInputText.class).isAssignableFrom(child.getClass())) {
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
		if (postfix != null) {
			writer.startElement("span", component);
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
		writer.endElement("span");
	}
}
