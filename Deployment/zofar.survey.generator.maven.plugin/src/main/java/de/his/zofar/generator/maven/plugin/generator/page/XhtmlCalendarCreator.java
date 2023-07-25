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

import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;

import de.his.zofar.xml.questionnaire.CalendarConfigurationItemType;
import de.his.zofar.xml.questionnaire.CalendarConfigurationType;
import de.his.zofar.xml.questionnaire.CalendarItemType;
import de.his.zofar.xml.questionnaire.CalendarSlotType;
import de.his.zofar.xml.questionnaire.CalendarType;
import de.his.zofar.xml.questionnaire.IdentificationalType;

public class XhtmlCalendarCreator extends AbstractXhtmlElementCreator implements IXhtmlCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(XhtmlCalendarCreator.class);

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

	private void createElement(IdentificationalType source, com.sun.java.jsf.composite.composite.CalendarType target) throws XmlException {
		if (!(CalendarType.class.isAssignableFrom(source.getClass())))
			return;
		CalendarType calendar = (CalendarType) source;

		setIdentifier(source, target);

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
		
		target.setLegendLayout(com.sun.java.jsf.composite.composite.CalendarType.LegendLayout.Enum.forString(calendar.getLegendLayout().toString()));
		target.setLegendPosition(com.sun.java.jsf.composite.composite.CalendarType.LegendPosition.Enum.forString(calendar.getLegendPosition().toString()));
		target.setBehaviour(com.sun.java.jsf.composite.composite.CalendarType.Behaviour.Enum.forString(calendar.getBehaviour().toString()));
		
		String rows = "";
		final List<String> rowList = new ArrayList<String>();
		rowList.addAll(Arrays.asList(calendar.getRows().split(",")));
		final Iterator<String> rowIt = rowList.iterator();
		int rowLft = 0;
		while(rowIt.hasNext()){
			final String text = rowIt.next();
			String key = null;
			if (PageManager.getInstance().getMojo() != null) {
				key = PageManager
						.getInstance()
						.getMojo()
						.addTextToBundle(
								generateUid(source) + "_Row_" + rowLft, text);
			}

			final String textValue = (key == null) ? text
					: createElExpression(key);
			rows = rows+textValue;
			if(rowIt.hasNext())rows = rows+",";
			rowLft = rowLft + 1;
		}
		
		target.setRows(rows);
		
		String columns = "";
		final List<String> columnList = new ArrayList<String>();
		columnList.addAll(Arrays.asList(calendar.getColumns().split(",")));
		final Iterator<String> columnIt = columnList.iterator();
		int columnLft = 0;
		while(columnIt.hasNext()){
			final String text = columnIt.next();
			String key = null;
			if (PageManager.getInstance().getMojo() != null) {
				key = PageManager
						.getInstance()
						.getMojo()
						.addTextToBundle(
								generateUid(source) + "_Column_" + columnLft, text);
			}

			final String textValue = (key == null) ? text
					: createElExpression(key);
			columns = columns+textValue;
			if(columnIt.hasNext())columns = columns+",";
			columnLft = columnLft + 1;
		}
		
		target.setColumns(columns);

		final CalendarConfigurationType configuration = calendar.getConfiguration();
		if (configuration != null) {
			final CalendarConfigurationItemType[] slots = configuration.getSlotConfigurationArray();
			if (slots != null) {
				for (final CalendarConfigurationItemType item : slots) {
					final String slotId = item.getSlot().toString();
					try {
						Method labelMethod = target.getClass().getMethod("set" + slotId + "Label", String.class);
						final String text = item.getLabel();
						if (labelMethod != null) {
							String key = null;
							if (PageManager.getInstance().getMojo() != null) {
								key = PageManager
										.getInstance()
										.getMojo()
										.addTextToBundle(
												generateUid(source) + "_Label_" + slotId, text);
							}

							final String textValue = (key == null) ? text
									: createElExpression(key);
							labelMethod.invoke(target, textValue);
						}
						if (item.isSetColor()) {
							Method colorMethod = target.getClass().getMethod("set" + slotId + "Color", String.class);
							if (colorMethod != null) {
								colorMethod.invoke(target, createElExpression(item.getColor()));
							}
						}
						if (item.isSetPattern()) {
							Method patternMethod = target.getClass().getMethod("set" + slotId + "Pattern", String.class);
							if (patternMethod != null)
								patternMethod.invoke(target, createElExpression(item.getPattern().toString()));
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
		}

		final CalendarItemType[] items = calendar.getItemArray();
		if (items != null) {
			int lft = 1;
			for (final CalendarItemType item : items) {
				com.sun.java.jsf.composite.composite.CalendarItemType targetItem = target.addNewCalendarItem();
				targetItem.setId("item"+lft);
				if (item.isSetVisible()) {
					targetItem.setRendered(createElExpression(item.getVisible()));
				}
				CalendarSlotType[] slots = item.getSlotItemArray();
				if (slots != null) {
					for (final CalendarSlotType slot : slots) {
						final String slotId = slot.getSlot().toString();

						try {
							Method slotMethod = targetItem.getClass().getMethod("set" + slotId, String.class);
							if (slotMethod != null){
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
