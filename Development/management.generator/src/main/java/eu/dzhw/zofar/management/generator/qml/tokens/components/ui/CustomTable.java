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

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomTable extends JTable {

	private static final long serialVersionUID = -7138013539032357460L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomTable.class);

	public CustomTable(String[] columns,Class[] colClasses,Object[][] rows) {
		this(new CustomTableModel(columns,colClasses,rows));
	}	
	
	private CustomTable(TableModel dm) {
		super(dm);
		final int count = dm.getColumnCount();
		for(int a=0;a<count;a++){
			final Class clazz = dm.getColumnClass(a);
			if((List.class).isAssignableFrom(clazz)){
				this.getColumnModel().getColumn(a).setCellEditor(new ComboCellEditor());
			}
		}
	}
}
