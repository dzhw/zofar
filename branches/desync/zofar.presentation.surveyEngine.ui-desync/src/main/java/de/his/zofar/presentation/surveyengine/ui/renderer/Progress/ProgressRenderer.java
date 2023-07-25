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
package de.his.zofar.presentation.surveyengine.ui.renderer.Progress;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.common.PictureBar;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = PictureBar.COMPONENT_FAMILY, rendererType = ProgressRenderer.RENDERER_TYPE)
public class ProgressRenderer extends Renderer{
	public static final String RENDERER_TYPE = "org.zofar.picturebar";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProgressRenderer.class);
	public ProgressRenderer() {
		super();
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered())
			return;
		if((PictureBar.class).isAssignableFrom(component.getClass())){
			PictureBar tmp = (PictureBar)component;
			final Stack<String> movements = tmp.getNavigatorAttribute().getMovements();
			final Map milestones = tmp.getMilestonesAttribute();
			if(movements != null){
				HtmlGraphicImage dotImage = new HtmlGraphicImage();
				dotImage.setValue(tmp.getDotAttribute());
				HtmlGraphicImage arrowImage = new HtmlGraphicImage();
				arrowImage.setValue(tmp.getArrowAttribute());			
				for(String movement : movements){
					if((milestones != null)&&(milestones.containsKey(movement))){
						arrowImage.encodeAll(context);
						HtmlGraphicImage milestoneImage = new HtmlGraphicImage();
						milestoneImage.setValue(milestones.get(movement)+"");
						milestoneImage.encodeAll(context);
					}
					else dotImage.encodeAll(context);					
				}
			}
		}
	}
    @Override
    public void encodeChildren(final FacesContext context,
            final UIComponent component) throws IOException {
    }
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered())
			return;
		final ResponseWriter writer = context.getResponseWriter();
	}
	@Override
	public boolean getRendersChildren() {
		return false;
	}
}
