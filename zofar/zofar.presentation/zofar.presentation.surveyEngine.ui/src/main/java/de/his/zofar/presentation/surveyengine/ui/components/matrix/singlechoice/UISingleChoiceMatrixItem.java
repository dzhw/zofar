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
package de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableItem;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.options.ZofarDifferentialMatrixItemRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.options.ZofarSingleChoiceMatrixItemRenderer;
/**
 * @author le
 *
 */
@FacesComponent("org.zofar.SingleChoiceMatrixItem")
public class UISingleChoiceMatrixItem extends AbstractTableItem {
    public UISingleChoiceMatrixItem() {
        super();
    }
	@Override
	public String getRendererType() {
		if(isDifferential()){
			return ZofarDifferentialMatrixItemRenderer.RENDERER_TYPE;
		}
		else return ZofarSingleChoiceMatrixItemRenderer.RENDERER_TYPE;
	}
	private Boolean isDifferential() {
		Boolean differential = false;
		UIComponent parent = this;
		while(!((AbstractTableResponseDomain.class).isAssignableFrom(parent.getClass())))parent = parent.getParent();
		if (parent.getAttributes().get("isDifferential") != null) {
			differential = (Boolean) parent
					.getAttributes().get("isDifferential");
		}
		return differential;
	}
	@Override
	@Deprecated
	public String getUID() {
		return this.getId();
	}
	@Override
	@Deprecated
	public Boolean visibleCondition() {
		return this.isRendered();
	}
    /*
     * (non-Javadoc)
     *
     * @see
     * javax.faces.component.UIComponentBase#encodeChildren(javax.faces.context
     * .FacesContext)
     */
}
