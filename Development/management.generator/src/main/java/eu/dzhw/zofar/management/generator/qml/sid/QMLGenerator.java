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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.files.FileClient;
/**
 * Generator for QML Code based on Templates specialized to SLC
 * @author meisner
 *
 */
public class QMLGenerator {
	private static final QMLGenerator INSTANCE = new QMLGenerator();
	private static final Logger LOGGER = LoggerFactory.getLogger(QMLGenerator.class);
	private QMLGenerator() {
		super();
	}
	public static QMLGenerator getInstance() {
		return INSTANCE;
	}
	public String generate(final File template, final Map<String,String> replacements)
			throws Exception {
		final FileClient fileClient = FileClient.getInstance();
		final GenericGenerator deckGenerator = DeckGenerator.getInstance();
		final String deckTemplate = fileClient.readAsString(template);
		final StringBuilder deckContent = new StringBuilder();
		for(int deckNr=1;deckNr<=200;deckNr++) {
			final List<Map<String, String>> deckReplacements = new ArrayList<Map<String, String>>();
			final Map<String, String> replacement = new HashMap<String, String>();
			replacement.put("deck", deckNr+"");
			deckReplacements.add(replacement);
			deckReplacements.add(replacements);
			final String deckResult = deckGenerator.process(deckTemplate, deckReplacements);
			deckContent.append(deckResult + "\n");
		} 
		return deckContent.toString();
	}
}
