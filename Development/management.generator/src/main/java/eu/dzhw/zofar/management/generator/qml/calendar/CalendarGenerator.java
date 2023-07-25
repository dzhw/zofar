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
package eu.dzhw.zofar.management.generator.qml.calendar;
import java.io.File;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class CalendarGenerator {
	private static final CalendarGenerator INSTANCE = new CalendarGenerator();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CalendarGenerator.class);
	final FileClient calendarFile= FileClient.getInstance();
	private CalendarGenerator() {
		super();
	}
	public static CalendarGenerator getInstance() {
		return INSTANCE;
	}
	public void generate(final String prefix, final String[] colors,
			final String[] slots, final String[] columns, final String[] rows)
			throws Exception {
		final int rowCount = rows.length;
		final int columnCount = columns.length;
		final int slotCount = slots.length;
		int lft = 1;
		final StringBuffer calendarBuffer = new StringBuffer();
		final StringBuffer variableBuffer = new StringBuffer();
		calendarBuffer.append("<calendar:calendar legendLayout=\"lineDirection\" behaviour=\"drag\" labelAll=\"false\"\n");
		calendarBuffer.append("rows=\"");
		calendarBuffer.append("\n");
		for (int a = 0; a < rowCount; a++) {
			calendarBuffer.append(rows[a]);
			if (a < rowCount - 1){
				calendarBuffer.append(",");
			}
		}
		calendarBuffer.append("\" ");
		calendarBuffer.append(" columns=\"");
		for (int b = 0; b < columnCount; b++) {
			calendarBuffer.append(columns[b]);
			if (b < columnCount - 1){
				calendarBuffer.append(",");
			}
		}
		calendarBuffer.append("\" ");
		for (int c = 1; c <= slotCount; c++) {
			calendarBuffer.append("slot" + c + "Label=\""
					+ slots[((c - 1) % slots.length)] + "\" slot" + c
					+ "Color=\"" + colors[((c - 1) % colors.length)] + "\"");
			if (c < slotCount){
				calendarBuffer.append(" ");
			}
		}
		calendarBuffer.append(">\n");
		for (int a = 1; a <= rowCount; a++) {
			for (int b = 1; b <= columnCount; b++) {
				calendarBuffer.append("<calendar:calendarItem label=\"" + lft
						+ "\" rendered=\"#{true}\" ");
				for (int c = 1; c <= slotCount; c++) {
					final String varname = prefix + "X" + a + "X" + b + "X" + c;
					variableBuffer.append("<zofar:variable name=\""+varname+"\" type=\"boolean\" />\n");
					calendarBuffer.append("slot" + c + "=\"#{" + varname + "}\"");
					if (c < slotCount){
						calendarBuffer.append(" ");
					}
				}
				calendarBuffer.append("/>\n");
				lft++;
			}
		}
		calendarBuffer.append("</calendar:calendar>\n");
	}
	public void generateQML(final String uid, final String[] prefixes,
			final String[] colors, final String[] pattern,
			final String[] slots, final String[] columns, final String[] rows, final File workDir)
			throws Exception {
		final int rowCount = rows.length;
		final int columnCount = columns.length;
		final int slotCount = slots.length;
		final int prefixesCount = prefixes.length;
		int[] counters = new int[prefixesCount];
		for(int a=0;a<prefixesCount;a++)counters[a] = 1;
		if(prefixesCount < slotCount) throw new Exception("less prefixes than slots");
		final StringBuffer calendarBuffer = new StringBuffer();
		final StringBuffer variableBuffer = new StringBuffer();
		calendarBuffer.append("<zofar:calendar uid=\""
				+ uid
				+ "\" behaviour=\"move\" legendLayout=\"pageDirection\" legendPosition=\"left\" showIndicator=\"false\" showLegendIcon=\"true\"");
		calendarBuffer.append(" rows=\"");
		for (int a = 0; a < rowCount; a++) {
			calendarBuffer.append(rows[a]);
			if (a < rowCount - 1){
				calendarBuffer.append(",");
			}
		}
		calendarBuffer.append("\" ");
		calendarBuffer.append(" columns=\"");
		for (int b = 0; b < columnCount; b++) {
			calendarBuffer.append(columns[b]);
			if (b < columnCount - 1){
				calendarBuffer.append(",");
			}
		}
		calendarBuffer.append("\" ");
		calendarBuffer.append(">\n");
		calendarBuffer.append("<zofar:configuration>\n");
		for (int c = 1; c <= slotCount; c++) {
			String tmpPattern = ""; 
			if((pattern != null)&&(pattern.length > ((c-1) % pattern.length))) tmpPattern = "pattern=\""+pattern[((c-1) % pattern.length)]+"\" ";
			String tmpColor = ""; 
			if((colors != null)&&(colors.length > ((c-1) % colors.length))) tmpColor = "color=\""+colors[((c-1) % colors.length)]+"\" ";
			calendarBuffer.append("<zofar:SlotConfiguration slot=\"Slot"+c+ "\" label=\""+slots[((c-1) % slots.length)]+"\""+tmpColor+""+tmpPattern+"/>\n");
		}
		calendarBuffer.append("</zofar:configuration>\n");
		for (int a = 1; a <= rowCount; a++) {
			for (int b = 1; b <= columnCount; b++) {
				calendarBuffer.append("<zofar:item visible=\"true\">\n");
				for (int c = 1; c <= slotCount; c++) {
					final int currentLft = counters[c-1]; 
					String currentStr = currentLft+"";
					if(currentLft < 10) currentStr = "0"+currentStr;
					counters[c-1] = counters[c-1] + 1;
					final String prefix = prefixes[c-1];
					final String varname = prefix.replaceAll(Pattern.quote("*"), currentStr+"");
					variableBuffer.append("<zofar:variable name=\""+varname+"\" type=\"boolean\" />\n");
					calendarBuffer.append("<zofar:SlotItem variable=\""+varname+"\" slot=\"Slot" + c + "\" />\n");
				}
				calendarBuffer.append("</zofar:item>\n");
			}
		}
		calendarBuffer.append("</zofar:calendar>\n");
		calendarFile.createTextFile("CalendarVariables.txt", variableBuffer.toString(), workDir);
		calendarFile.createTextFile("CalendarQML.txt", calendarBuffer.toString(), workDir);
	}
}
