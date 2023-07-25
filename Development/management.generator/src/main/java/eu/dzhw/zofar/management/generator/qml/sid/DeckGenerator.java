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
package eu.dzhw.zofar.management.generator.qml.sid;
import java.util.List;
import java.util.Map;
public class DeckGenerator extends GenericGenerator {
	private static DeckGenerator INSTANCE;
	private DeckGenerator() {
		super();
	}
	public static DeckGenerator getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DeckGenerator();
		return INSTANCE;
	}
	@Override
	public String process(String template, List<Map<String, String>> replacements) {
		return super.replacePlaceholder(template, replacements);
	}
}
