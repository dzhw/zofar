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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.responsedomain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.singlechoice.UISingleChoiceMatrixItemResponseDomain;
/**
 * @author meisner
 *
 */
@Deprecated
@FacesRenderer(componentFamily = UISingleChoiceMatrixItemResponseDomain.COMPONENT_FAMILY, rendererType = ZofarDifferentialMatrixItemResponseDomainRenderer.RENDERER_TYPE)
public class ZofarDifferentialMatrixItemResponseDomainRenderer extends ZofarGeneralRadioMatrixItemResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.DifferentialMatrixItemResponseDomain";
	public ZofarDifferentialMatrixItemResponseDomainRenderer() {
		super();
	}
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		final String clientId = component.getClientId(context);
		final Object currentValue = component.getAttributes().get("value");
		final List<SingleOption> missings = new ArrayList<SingleOption>();
		final List<SingleOption> answerOptions = new ArrayList<SingleOption>();
		for (final UIComponent child : component.getChildren()) {
			if (SingleOption.class.isAssignableFrom(child.getClass())) {
				final SingleOption answerOption = (SingleOption) child;
				if (answerOption.isMissing()) {
					missings.add((SingleOption) child);
				} else {
					answerOptions.add(answerOption);
				}
			}
		}
		int itemNum = 0;
	}
}
