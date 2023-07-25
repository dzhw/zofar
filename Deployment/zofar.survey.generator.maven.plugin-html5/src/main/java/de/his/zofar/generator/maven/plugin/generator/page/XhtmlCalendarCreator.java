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
package de.his.zofar.generator.maven.plugin.generator.page;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlException;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.composite.CalendarColumnItemType;
import com.sun.java.jsf.composite.composite.CalendarItemsType;
import com.sun.java.jsf.composite.composite.CalendarLegendItemType;
import com.sun.java.jsf.composite.composite.CalendarRowItemType;
import com.sun.java.jsf.composite.container.SectionType;
import de.his.zofar.xml.questionnaire.CalendarConfigurationItemType;
import de.his.zofar.xml.questionnaire.CalendarConfigurationType;
import de.his.zofar.xml.questionnaire.CalendarItemType;
import de.his.zofar.xml.questionnaire.CalendarSlotType;
import de.his.zofar.xml.questionnaire.CalendarType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
public class XhtmlCalendarCreator extends AbstractXhtmlElementCreator implements IXhtmlCreator {
	public XhtmlCalendarCreator() {
		super();
	}
	@Override
	public void addToSection(IdentificationalType source, SectionType target, boolean root) throws XmlException {
		createElement(source, target.addNewCalendar());
	}
	@Override
	public void addToSort(IdentificationalType source, SortType target) throws XmlException {
		createElement(source, target.addNewCalendar());
	}
	private void createElement(IdentificationalType source, com.sun.java.jsf.composite.composite.CalendarType target)
			throws XmlException {
		if (!(CalendarType.class.isAssignableFrom(source.getClass())))
			return;
		CalendarType calendar = (CalendarType) source;
		setIdentifier(source, target);
		if (calendar.isSetVisible()) {
			target.setRendered(createElExpression(calendar.getVisible()));
		}
		if (calendar.isSetLabelAll()) {
			target.setLabelAll(createElExpression(calendar.getLabelAll()));
		}
		if (calendar.isSetShowIndicator()) {
			target.setShowIndicator(createElExpression(calendar.getShowIndicator()));
		}
		if (calendar.isSetShowLegendIcon()) {
			target.setShowLegendIcon(createElExpression(calendar.getShowLegendIcon()));
		}
		if (calendar.isSetVisible()) {
			target.setRendered(createElExpression(calendar.getVisible()));
		}
		target.setLegendLayout(com.sun.java.jsf.composite.composite.CalendarType.LegendLayout.Enum
				.forString(calendar.getLegendLayout().toString()));
		target.setBehaviour(calendar.getBehaviour().toString());
		final com.sun.java.jsf.core.FacetType targetRows = target.addNewFacet();
		targetRows.setName(com.sun.java.jsf.core.FacetType.Name.ROWS);
		final List<String> rowList = new ArrayList<String>();
		rowList.addAll(Arrays.asList(calendar.getRows().split(",")));
		final Iterator<String> rowIt = rowList.iterator();
		int rowLft = 0;
		while (rowIt.hasNext()) {
			final String text = rowIt.next();
			String key = null;
			if (PageManager.getInstance().getMojo() != null) {
				key = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source) + "_Row_" + rowLft, text);
			}
			final String textValue = (key == null) ? text : createElExpression(key);
			final CalendarRowItemType targetRow = targetRows.addNewCalendarRowItem();
			targetRow.setLabel(textValue);
			rowLft = rowLft + 1;
		}
		final com.sun.java.jsf.core.FacetType targetColumns = target.addNewFacet();
		targetColumns.setName(com.sun.java.jsf.core.FacetType.Name.COLUMNS);
		final List<String> columnList = new ArrayList<String>();
		columnList.addAll(Arrays.asList(calendar.getColumns().split(",")));
		final Iterator<String> columnIt = columnList.iterator();
		int columnLft = 0;
		while (columnIt.hasNext()) {
			final String text = columnIt.next();
			String key = null;
			if (PageManager.getInstance().getMojo() != null) {
				key = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source) + "_Column_" + columnLft,
						text);
			}
			final String textValue = (key == null) ? text : createElExpression(key);
			final CalendarColumnItemType targetColumn = targetColumns.addNewCalendarColumnItem();
			targetColumn.setLabel(textValue);
			columnLft = columnLft + 1;
		}
		final com.sun.java.jsf.core.FacetType targetLegend = target.addNewFacet();
		targetLegend.setName(com.sun.java.jsf.core.FacetType.Name.LEGEND);
		final CalendarConfigurationType configuration = calendar.getConfiguration();
		if (configuration != null) {
			final CalendarConfigurationItemType[] slots = configuration.getSlotConfigurationArray();
			if (slots != null) {
				int legendLft = 0;
				for (final CalendarConfigurationItemType item : slots) {
					final CalendarLegendItemType legendItem = targetLegend.addNewCalendarLegendItem();
					final String slotId = item.getSlot().toString().toLowerCase();
					legendItem.setId(slotId);
					final String text = item.getLabel();
					String key = null;
					if (PageManager.getInstance().getMojo() != null) {
						key = PageManager.getInstance().getMojo()
								.addTextToBundle(generateUid(source) + "_Legend_" + legendLft, text);
					}
					final String textValue = (key == null) ? text : createElExpression(key);
					legendItem.setLabel(textValue);
					if (item.isSetColor()) {
						final String slotColor = item.getColor();
						legendItem.setColor(slotColor);
					}
					if (item.isSetPattern()) {
						final String slotPattern = item.getPattern();
						legendItem.setPattern(slotPattern);
					}
					legendLft = legendLft + 1;
				}
			}
		}
		final CalendarItemType[] items = calendar.getItemArray();
		if (items != null) {
			final com.sun.java.jsf.core.FacetType targetItems = target.addNewFacet();
			targetItems.setName(com.sun.java.jsf.core.FacetType.Name.ITEMS);
			int lft = 1;
			for (final CalendarItemType item : items) {
				com.sun.java.jsf.composite.composite.CalendarItemType targetItem = targetItems.addNewCalendarItem();
				targetItem.setId("item" + lft);
				if (item.isSetVisible()) {
					targetItem.setRendered(createElExpression(item.getVisible()));
				}
				CalendarSlotType[] slots = item.getSlotItemArray();
				if (slots != null) {
					for (final CalendarSlotType slot : slots) {
						final String slotId = slot.getSlot().toString().toLowerCase();
						try {
							Method slotMethod = targetItem.getClass().getMethod("set" + StringUtils.capitalize(slotId),
									String.class);
							if (slotMethod != null) {
								final String variable = slot.getVariable();
								slotMethod.invoke(targetItem, createElExpression(variable));
							}
						} catch (NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
				lft = lft + 1;
			}
		}
	}
}
