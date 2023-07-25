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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.options;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
public abstract class ZofarRadioSingleOptionRenderer extends Renderer{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarRadioSingleOptionRenderer.class);
	protected ZofarRadioSingleOptionRenderer() {
		super();
	}
	@Override
	public synchronized boolean getRendersChildren() {
		return false;
	}
    protected synchronized boolean hasInput(final FacesContext context,
            final UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return false;
        }
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return false;
        }
        return true;
    }
	protected synchronized void encodeInput(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return;
        }
		final String selectedValue = (String) parent.getAttributes().get("value");
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("input", component);
		writer.writeAttribute("type", "radio", null);
		writer.writeAttribute("name", parent.getClientId(), null);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("value", component.getId(), "value");
		writer.writeAttribute("onchange", "javascript:zofar_singleChoice_triggerRadio('"+parent.getClientId()+"','"+component.getClientId()+"');",null);
		if (component.getId() != null && !component.getId().isEmpty()
				&& component.getId().equals(selectedValue)) {
			writer.writeAttribute("checked", "checked", null);
		}
		writer.endElement("input");
		return;
	}
	protected synchronized boolean hasLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return false;
        }
		final String itemLabel = (String) component.getAttributes()
				.get("label");
		if(itemLabel.equals(""))return false;
		return true;
	}
	protected synchronized void encodeLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return ;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return;
        }
		final String itemLabel = (String) component.getAttributes()
				.get("label");
		final String label = JsfUtility.getInstance().evaluateValueExpression(
				context, itemLabel, String.class);
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		writer.writeAttribute("for", component.getClientId(), null);
		writer.write(label);
		writer.endElement("label");
		return ;
	}
	protected synchronized boolean hasValueLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return false;
        }
		final Boolean showValues = (Boolean) parent.getAttributes().get(
				"showValues");
		if((showValues == null)||(!showValues))return false;
		final String itemValue = (String) component.getAttributes().get("value");
		if(itemValue.equals(""))return false;
		return true;
	}
	protected synchronized void encodeValueLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
            return;
        }
		final Boolean showValues = (Boolean) parent.getAttributes().get(
				"showValues");
		if((showValues == null)||(!showValues))return;
		final String itemValue = (String) component.getAttributes().get("value");
		final String label = JsfUtility.getInstance().evaluateValueExpression(
				context, itemValue, String.class);
		if(label.equals(""))return;
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		writer.writeAttribute("for", component.getClientId(), null);
		writer.write(""+label+"");
		writer.endElement("label");
		return;
	}
	protected synchronized boolean hasOpenQuestion(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
		for (@SuppressWarnings("unused") final UIComponent child : component.getChildren()) {
			return true;
		}
		return false;
	}
	protected synchronized boolean encodeOpenQuestion(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return false;
		}
		boolean flag = false;
		for (final UIComponent child : component.getChildren()) {
			child.encodeAll(context);
			insertJavaScript(context,component,child.getClientId()+":"+child.getAttributes().get("inputId"));
			flag = true;
		}
		return flag;
	}
	private synchronized void insertJavaScript(final FacesContext context,final UIComponent component,final String oid) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", null);
		UIComponent responsedomain = JsfUtility.getInstance().getParentResponseDomain(component);
		String script = "zofar_singleChoice_register_open('"+ responsedomain.getClientId() + "', '" + oid+"');\n";
		writer.write(script);
		writer.endElement("script");
	}
}
