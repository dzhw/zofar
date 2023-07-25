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
package eu.dzhw.zofar.management.generator.qml.tokens.components.ui;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
public class CustomTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -4967208485256641526L;
	private Class[] colClasses;
	public CustomTableModel(String[] columns,Class[] colClasses,Object[][] rows) {
		super(rows,(Object[])columns);
		this.colClasses = colClasses;
	}
    public Object getValueAt(int row, int col) {
        if((this.getDataVector().size() >= (row+1))&&(((Vector)this.getDataVector().get(row)).size() >= (col+1)))return ((Vector)this.getDataVector().get(row)).get(col);
        return null;
    }
    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
    	return colClasses[c];
    }
}
