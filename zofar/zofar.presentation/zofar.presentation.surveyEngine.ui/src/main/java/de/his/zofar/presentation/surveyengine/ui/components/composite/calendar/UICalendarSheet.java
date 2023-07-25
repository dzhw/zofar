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
package de.his.zofar.presentation.surveyengine.ui.components.composite.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.renderer.calendar.SheetRenderer;

/**
 * @author meisner
 *
 */
@FacesComponent(value = "de.his.zofar.Calendar.Sheet")
public class UICalendarSheet extends UINamingContainer implements Identificational,Visible{

	public static final String COMPONENT_FAMILY = "de.his.zofar.Calendar.Sheet";

	public UICalendarSheet() {
		super();
	}
	
    @Override
    public String getFamily() {
        return UICalendarSheet.COMPONENT_FAMILY;
    }

    @Override
    public String getRendererType() {
        return SheetRenderer.RENDERER_TYPE; 
    }

	public int getColumnCount(){
		return getColumnList().size();
	}
	
	public List<String> getColumnList(){
		final List<String> back = new ArrayList<String>();
		back.addAll(Arrays.asList(getColumnsAttribute().split(",")));
		return back;
	}
	
    public String getColumnsAttribute() {
        final String value = (String) getAttributes().get("columns");
        return value;
    }
    
	public int getRowCount(){
		return getRowList().size();
	}
	
	public List<String> getRowList(){
		final List<String> back = new ArrayList<String>();
		back.addAll(Arrays.asList(getRowsAttribute().split(",")));
		return back;
	}
	
    public String getRowsAttribute() {
        final String value = (String) getAttributes().get("rows");
        return value;
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
}
