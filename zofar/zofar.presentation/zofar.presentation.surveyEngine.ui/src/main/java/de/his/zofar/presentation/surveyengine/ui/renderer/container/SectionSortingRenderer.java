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
package de.his.zofar.presentation.surveyengine.ui.renderer.container;
import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import com.sun.faces.facelets.compiler.UIInstructions;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author dick
 *
 */
@FacesRenderer(componentFamily = Section.COMPONENT_FAMILY, rendererType = SectionSortingRenderer.RENDERER_TYPE)
public class SectionSortingRenderer extends Renderer {
	public static final String RENDERER_TYPE = "org.zofar.SectionSorting";
	public ResponseWriter writer;
	public boolean flag=false;
	public SectionSortingRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	@Override
	public synchronized void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		writer = context.getResponseWriter();
		UIComponent header = component.getFacet("header");
		if((header!=null)&&(header.isRendered())){
			 writer.startElement("optgroup", component);
             writer.writeAttribute("label", retrieveText(header), null);
			header.encodeAll(context);
			 flag=true;
		}
	}
	@Override
	public synchronized void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {
		this.encodeChildrenHelper(context, component, component.getChildren());
	}
	private synchronized void encodeChildrenHelper(final FacesContext context,
			final UIComponent component, final List<UIComponent> children) throws IOException {
		for (final UIComponent child : children) {
			if (UISort.class.isAssignableFrom(child.getClass())) {
				this.encodeChildrenHelper(context, component,
						((UISort) child).sortChildren());
			} else {
				child.encodeAll(context);
			}
		}
	}
	@Override
	public synchronized void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		UIComponent footer = component.getFacet("footer");
		if((footer!=null)&&(footer.isRendered()))footer.encodeAll(context);
		if (flag) writer.endElement("optgroup");
	}
    /**
     * @param header
     * @return
     */
    private synchronized String retrieveText(final UIComponent header) {
        final StringBuffer text = new StringBuffer();
        if (header.isRendered()) {
            if (header instanceof UIText) {
            	final Object value = header.getAttributes().get("value");
                if(value != null)text.append(value);
                for (final UIComponent child : header.getChildren()) {
                    if (child.isRendered()) {
                    	text.append(retrieveText(child));
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
