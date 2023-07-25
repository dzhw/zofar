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
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ComboCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private static final long serialVersionUID = -3385002328871076567L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ComboCellEditor.class);
	private List<Object> selectable;
	private Map<Point2D,Object> selected;
	private Object current;
	private Map<Point2D,JComboBox<Object>> map;
	@Override
	public Object getCellEditorValue() {
		return current;
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		final Class columnClass = table.getColumnClass(column);
		if ((List.class).isAssignableFrom(value.getClass())) {
			if (selectable == null)
				selectable = new ArrayList<Object>();
			selectable.clear();
			selectable.addAll((List) value);
		}
		if(map == null)map = new HashMap<Point2D,JComboBox<Object>>();
		JComboBox<Object> combo = null;
		Point2D key = new Point(row,column);
		if(map.containsKey(key))combo = map.get(key);
		if(combo == null){
			combo = new JComboBox<Object>();
			combo.addActionListener(this);
			combo.setActionCommand(row + "#" + column);
			combo.removeAllItems();
			for (Object item : selectable) {
				combo.addItem(item);
			}
			map.put(key, combo);
		}
		if((selected != null)&&(selected.containsKey(key))){
			combo.setSelectedItem(selected.get(key));
			current = selected.get(key);
		}
		return combo;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();
		final Object source = e.getSource();
		final String[] coords = cmd.split("#");
		Point2D key = new Point(Integer.parseInt(coords[0]),Integer.parseInt(coords[1]));
		if(selected == null)selected = new HashMap<Point2D,Object>();
		if (source != null) {
			selected.put(key, ((JComboBox<Object>) source).getSelectedItem());
			current = ((JComboBox<Object>) source).getSelectedItem();
		} 
	}
}
