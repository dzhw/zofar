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
package de.his.zofar.presentation.surveyengine.ui.components.composite.comparison;
import javax.faces.component.FacesComponent;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.comparison.ZofarComparisonResponseDomainRenderer;
/**
 * @author meisner
 * 
 */
@FacesComponent(value = "org.zofar.ComparisonResponseDomain")
public class UIComparisonResponseDomain extends AbstractTableResponseDomain
		implements Identificational {
	private enum PropertyKeys {
		itemClasses
	}
	public static final String COMPONENT_FAMILY = "org.zofar.ComparisonResponseDomain";
	public UIComparisonResponseDomain() {
		super();
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	public String getRendererType() {
		return ZofarComparisonResponseDomainRenderer.RENDERER_TYPE;
	}
	@Override
	@Deprecated
	public String getUID() {
		return this.getId();
	}
    private static final String ITEM_CLASSES = "";
	@Override
	public void setItemClasses(String itemClasses) {
		getStateHelper().put(PropertyKeys.itemClasses, itemClasses);
	}
	@Override
	public String getItemClasses() {
		return ITEM_CLASSES;
	}
}
