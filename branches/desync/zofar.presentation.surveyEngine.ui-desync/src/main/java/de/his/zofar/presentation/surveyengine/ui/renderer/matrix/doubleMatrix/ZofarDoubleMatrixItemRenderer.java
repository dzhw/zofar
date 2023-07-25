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
package de.his.zofar.presentation.surveyengine.ui.renderer.matrix.doubleMatrix;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.composite.doublematrix.UIDoubleMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice.UISingleChoiceMatrixItemResponseDomain;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIDoubleMatrixItem.COMPONENT_FAMILY, rendererType = ZofarDoubleMatrixItemRenderer.RENDERER_TYPE)
public class ZofarDoubleMatrixItemRenderer extends ZofarAbstractDoubleMatrixRenderer {
	public static final String RENDERER_TYPE = "org.zofar.DoubleMatrixItem";
    private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarDoubleMatrixItemRenderer.class);
	public ZofarDoubleMatrixItemRenderer() {
		super();
	}
    @Override
    public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
        this.renderAnswerOptions(context,component, Side.LEFT);
        this.renderQuestionColumn(context,component);
        this.renderAnswerOptions(context,component, Side.RIGHT);
    }
    /**
     * @param context
     * @param right
     * @throws IOException
     */
    private void renderAnswerOptions(final FacesContext context, final UIComponent component, final Side side)
            throws IOException {
        String facetName = null;
        switch (side) {
        case LEFT:
            facetName = LEFT_QUESTION;
            break;
        case RIGHT:
            facetName = RIGHT_QUESTION;
            break;
        default:
            throw new IllegalStateException();
        }
        final UIComponent question = component.getFacet(facetName);
        if (!UISingleChoiceMatrixItemResponseDomain.class
                .isAssignableFrom(question.getClass())) {
            throw new IllegalStateException(
                    "The left and right side of an double matrix item must be of type UISingleChoiceMatrixItemResponseDomain. But it is: "
                            + question.getClass());
        }
        question.encodeAll(context);
    }
    /**
     * @param context
     */
    private void renderQuestionColumn(final FacesContext context, final UIComponent component)
            throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final UIComponent questionFacet = component.getFacet(QUESTION);
        writer.startElement("td", component);
        writer.writeAttribute("class", "zo-db-item-question", null);
        questionFacet.encodeAll(context);
        writer.endElement("td");
    }
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
