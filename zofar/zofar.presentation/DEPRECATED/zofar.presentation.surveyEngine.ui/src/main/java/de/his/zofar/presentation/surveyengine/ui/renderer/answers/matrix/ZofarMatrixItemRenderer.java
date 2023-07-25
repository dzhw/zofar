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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.UIAttachedOpenQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
public abstract class ZofarMatrixItemRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarMatrixItemRenderer.class);
    protected static final String TABLE_CELL = "td";
    public static final String HEADER_FACET = "header";
    protected static final String QUESTION_COLUMN = "questionColumn";
	public ZofarMatrixItemRenderer() {
		super();
	}
	/**
	 * does the response domain have a question column.
	 * 
	 * @return
	 */
	protected synchronized Boolean isHasQuestionColumn(final FacesContext context,
			final UIComponent component) {
		Boolean hasQuestionColumn = false;
		UIComponent parent = component;
		while(!((AbstractTableResponseDomain.class).isAssignableFrom(parent.getClass())))parent = parent.getParent();
		if (parent.getAttributes().get(QUESTION_COLUMN) != null) {
			hasQuestionColumn = Boolean.valueOf(parent.getAttributes().get(
					QUESTION_COLUMN)+"");
		}
		return hasQuestionColumn;
	}
	public abstract void encodeBegin(FacesContext context, UIComponent component)
			throws IOException;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.component.UIComponentBase#encodeChildren(javax.faces.context
	 * .FacesContext)
	 */
	@Override
	public synchronized void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		for (final UIComponent child : component.getChildren()) {
			if((UIAttachedOpenQuestion.class).isAssignableFrom(child.getClass())){
				continue;
			}
			child.encodeAll(context);
		}
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
