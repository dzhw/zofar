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
/*
 * 
 */
package de.his.zofar.presentation.surveyengine.ui.renderer.mixed;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.composite.mixed.UIMixedMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.mixed.UIMixedMatrixResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixResponseDomainRenderer;
/**
* @author meisner
*
*/
@FacesRenderer(componentFamily = UIMixedMatrixResponseDomain.COMPONENT_FAMILY, rendererType = ZofarMixedMatrixResponseDomainRenderer.RENDERER_TYPE)
public class ZofarMixedMatrixResponseDomainRenderer extends ZofarMatrixResponseDomainRenderer{
	public static final String RENDERER_TYPE = "org.zofar.MixedMatrixResponseDomain";
    private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarMixedMatrixResponseDomainRenderer.class);
	public ZofarMixedMatrixResponseDomainRenderer() {
		super();
	}
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
	public void encodeBegin(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		if (component.getChildren().size()>1){
			super.encodeBegin(context, component,"zo-mixed-matrix-response-domain zo-mixed-matrix-response-domain mixed_");
		}
		else {
			super.encodeBegin(context, component,"zo-mixed-matrix-response-domain zo-mixed-matrix-response-domain mixed_"+(component.getId()));
		}
	}
	@Override
	protected Boolean isHasQuestionColumn(final FacesContext context,
			final UIComponent component) {
		Boolean hasQuestionColumn = true;
		if (component.getAttributes().get(ZofarMatrixResponseDomainRenderer.QUESTION_COLUMN) != null) {
			hasQuestionColumn = Boolean.valueOf(component.getAttributes().get(
					ZofarMatrixResponseDomainRenderer.QUESTION_COLUMN)+"");
		}
		if(hasQuestionColumn){
			Boolean tmpFlag = false;
	        for (final UIComponent child : component.getChildren()) {
	        	if((UIMixedMatrixItem.class).isAssignableFrom(child.getClass())){
	        		final UIMixedMatrixItem tmp = (UIMixedMatrixItem)child;
	        		final UIComponent header = tmp.getFacet(de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer.HEADER_FACET);
	        		if((header != null)&&(header.getChildren() != null)&&(!header.getChildren().isEmpty()))tmpFlag = true;
	        	}
	        }
	        if(!tmpFlag)hasQuestionColumn=false;
		}
		return hasQuestionColumn;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;     
		}
        for (final UIComponent child : component.getChildren()) {
        	child.encodeAll(context);
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
	public void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeEnd(context, component);
	}
}
