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
/**
 *
 */
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.faces.facelets.compiler.UIInstructions;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author le
 *
 */
@FacesRenderer(componentFamily = IResponseDomain.COMPONENT_FAMILY, rendererType = ZofarDropDownResponseDomainRenderer.RENDERER_TYPE)
public class ZofarDropDownResponseDomainRenderer extends
        ZofarSingleChoiceResponseDomainRenderer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarDropDownResponseDomainRenderer.class);
    public static final String RENDERER_TYPE = "org.zofar.DropDownResponseDomain";
    /*
     * (non-Javadoc)
     *
     * @see de.his.zofar.presentation.surveyengine.ui.renderer.
     * ZofarResponseDomainRenderer#encodeBegin(javax.faces.context.FacesContext,
     * javax.faces.component.UIComponent)
     */
    @Override
    public void encodeBegin(final FacesContext context,
            final UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }
        String additonalClasses = null;
		additonalClasses = "zo-rd-dropdown";
        this.startTable(context.getResponseWriter(), component,additonalClasses);
    }
    /*
     * (non-Javadoc)
     *
     * @see de.his.zofar.presentation.surveyengine.ui.renderer.
     * ZofarResponseDomainRenderer
     * #encodeChildren(javax.faces.context.FacesContext,
     * javax.faces.component.UIComponent)
     */
    @Override
    public void encodeChildren(final FacesContext context,
            final UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }
        final Object currentValue = ((UIInput) component).getValue();
        final ResponseWriter writer = context.getResponseWriter();
        writer.startElement("tr", component);
        writer.writeAttribute("class", "zo-dropdown-row", null);
        writer.startElement("td", component);
        writer.writeAttribute("class", "zo-dropdown-column", null);
        writer.startElement("select", component);
        writer.writeAttribute("class", "zo-dropdown", null);
        writer.writeAttribute("name", component.getClientId(), null);
        writer.writeAttribute("id", component.getClientId()+":select", null);
        final boolean isShowValues = (boolean) component.getAttributes().get(
                "showValues");
        for (final UIComponent child : component.getChildren()) {
            if (!child.isRendered()) {            	
                continue;
            }  
            if (child instanceof Section) {
                this.encodeUnit(context, child, currentValue, isShowValues);
            } 
            else {
                child.encodeAll(context);
            }
        }
        writer.endElement("select");
        writer.endElement("td");
        writer.endElement("tr");
    }
    /*
     * (non-Javadoc)
     *
     * @see de.his.zofar.presentation.surveyengine.ui.renderer.
     * ZofarResponseDomainRenderer#encodeEnd(javax.faces.context.FacesContext,
     * javax.faces.component.UIComponent)
     */
    @Override
    public void encodeEnd(final FacesContext context,
            final UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }
        final ResponseWriter writer = context.getResponseWriter();
        this.endTable(writer);
    }
    /**
     * renders the unit AKA section with the <optgroup /> tag. as HTML specs
     * states, nested <optgroup /> are not allowed
     * (http://www.w3.org/TR/html401/interact/forms.html#edef-OPTGROUP). if this
     * changes in the future one must implement nested units.
     *
     * @param context
     * @param component
     * @param currentValue
     * @param isShowValues
     * @throws IOException
     */
    private void encodeUnit(final FacesContext context,
            final UIComponent component, final Object currentValue,
            final boolean isShowValues)
            throws IOException {
        if (!component.isRendered()) {
            return;
        }
        final ResponseWriter writer = context.getResponseWriter();
        boolean renderUnitContainer = true;
        final UIComponent header = component.getFacet("header");
        if (header != null && header.isRendered()) {
            final String title = this.retrieveText(header);
            renderUnitContainer = !title.isEmpty();
            if (renderUnitContainer) {
                writer.startElement("optgroup", component);
                writer.writeAttribute("label", title, null);
            }
        }
        for (final UIComponent child : component.getChildren()) {
            child.encodeAll(context);
        }
        if (renderUnitContainer) {
            writer.endElement("optgroup");
        }
    }
    /**
     * @param header
     * @return
     */
    private String retrieveText(final UIComponent header) {
        final StringBuffer text = new StringBuffer();
        if (header.isRendered()) {
            if (header instanceof UIText) {
            	final Object value = header.getAttributes().get("value");
                if(value != null)text.append(value);
                for (final UIComponent child : header.getChildren()) {
                    if (child.isRendered()) {
                    	text.append(this.retrieveText(child));
                    }
                }
            } 
            if (UIInstructions.class.isAssignableFrom(header.getClass())) {
                text.append(JsfUtility.getInstance().evaluateValueExpression(
                        FacesContext.getCurrentInstance(), String.valueOf(header), String.class));
            }
        }
        return text.toString();
    }
}
