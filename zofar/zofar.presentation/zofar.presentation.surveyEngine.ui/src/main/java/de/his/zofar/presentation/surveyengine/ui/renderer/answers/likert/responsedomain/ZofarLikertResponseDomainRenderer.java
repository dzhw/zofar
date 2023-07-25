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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.likert.responsedomain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.LikertResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain.ZofarSingleChoiceResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = LikertResponseDomain.COMPONENT_FAMILY, rendererType = ZofarLikertResponseDomainRenderer.RENDERER_TYPE)
public class ZofarLikertResponseDomainRenderer extends
ZofarSingleChoiceResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.LikertResponseDomain";
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ZofarLikertResponseDomainRenderer.class);
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
		if (component.getId() != null && !component.getId().isEmpty()
				&& component.getId().equals(selectedValue)) {
			writer.writeAttribute("checked", "checked", null);
		}
		writer.endElement("input");
		return;
	}
	protected synchronized void encodeLabel(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final String itemLabel = (String) component.getAttributes().get("label");
		final String label = JsfUtility.getInstance().evaluateValueExpression(
				context, itemLabel, String.class);
		if(label.equals(""))return;
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		writer.writeAttribute("for", component.getClientId(), null);
		writer.write(""+label+"");
		writer.endElement("label");
		return;
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
	protected synchronized List<UIComponent> retrieveLabels(final FacesContext context,final UIComponent component,
			final UIComponent child) throws IOException {
		final List<UIComponent> back = new ArrayList<UIComponent>();
		if((Section.class).isAssignableFrom(child.getClass())){
			for (final UIComponent sectionChild : ((Section)child).getChildren()) {
				if (!sectionChild.isRendered()) {
					continue;
				}
				back.addAll(retrieveLabels(context,component,sectionChild));
			}
		}
		else if((UISort.class).isAssignableFrom(child.getClass())){
			for (final UIComponent sortChild : ((UISort)child).getChildren()) {
				if (!sortChild.isRendered()) {
					continue;
				}
				back.addAll(retrieveLabels(context,component,sortChild));
			}
		}
		else{
			final UIComponent labels = child.getFacet("labels");
			if(labels != null){
				back.add(labels);
			}
		}
		return back;
	}
}
