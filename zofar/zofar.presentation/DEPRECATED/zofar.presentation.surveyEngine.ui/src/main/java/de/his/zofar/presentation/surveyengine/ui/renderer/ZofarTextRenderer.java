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
package de.his.zofar.presentation.surveyengine.ui.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import com.sun.faces.facelets.compiler.UIInstructions;

import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;

/**
 * renderer class for all text components.
 *
 * @author le
 *
 */
@FacesRenderer(componentFamily = UIText.COMPONENT_FAMILY, rendererType = ZofarTextRenderer.RENDERER_TYPE)
public class ZofarTextRenderer extends Renderer {

    public static final String RENDERER_TYPE = "org.zofar.Text";

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.render.Renderer#getRendersChildren()
     */
    @Override
    public boolean getRendersChildren() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
     * javax.faces.component.UIComponent)
     */
    @Override
    public synchronized void encodeBegin(final FacesContext context,
            final UIComponent component) throws IOException {

        if (!component.isRendered()) {
            return;
        }

        final ResponseWriter writer = context.getResponseWriter();
        boolean flag = false;

        if ((boolean) component.getAttributes().get("block")) {
            writer.startElement("div", component);
            flag = true;
        } 
        else if((de.his.zofar.presentation.surveyengine.ui.components.text.UIResponseOptionText.class).isAssignableFrom(component.getClass())){
    		
    	}
    	else {
            writer.startElement("span", component);
            flag = true;
        }
        
        if(flag){
            writer.writeAttribute("id", component.getClientId(context), null);

            final String styleClass = (String) component.getAttributes().get(
                    "styleClass");

            if (styleClass != null) {
                writer.writeAttribute("class", styleClass, null);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext
     * , javax.faces.component.UIComponent)
     */
    @Override
    public synchronized void encodeChildren(final FacesContext context,
            final UIComponent component) throws IOException {

        if (!component.isRendered()) {
            return;
        }

        final ResponseWriter writer = context.getResponseWriter();

        for (final UIComponent child : component.getChildren()) {
            if (UIInstructions.class.isAssignableFrom(child.getClass())) {
                writer.write(JsfUtility.getInstance().evaluateValueExpression(
                        context, String.valueOf(child), String.class));
            } else {
                child.encodeAll(context);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
     * javax.faces.component.UIComponent)
     */
    @Override
    public synchronized void encodeEnd(final FacesContext context,
            final UIComponent component) throws IOException {

        if (!component.isRendered()) {
            return;
        }

        final ResponseWriter writer = context.getResponseWriter();

        if (UIText.class.isAssignableFrom(component.getClass())
                && ((UIText) component).isBlock()) {
            writer.endElement("div");
        } 
        else if((de.his.zofar.presentation.surveyengine.ui.components.text.UIResponseOptionText.class).isAssignableFrom(component.getClass())){
    		
    	}
        else {
            writer.endElement("span");
        }

    }

}
