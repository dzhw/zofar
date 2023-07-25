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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.section;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = Section.COMPONENT_FAMILY, rendererType = RadioSectionRenderer.RENDERER_TYPE)
public class RadioSectionRenderer extends HtmlBasicRenderer{
	public static final String RENDERER_TYPE = "org.zofar.RadioSection";
	public RadioSectionRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	@Override
	public synchronized void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		if(!component.isRendered())return;
		UIComponent header = component.getFacet("header");
		if(header!=null)header.encodeAll(context);
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
    }
	@Override
	public synchronized void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
	}
}
