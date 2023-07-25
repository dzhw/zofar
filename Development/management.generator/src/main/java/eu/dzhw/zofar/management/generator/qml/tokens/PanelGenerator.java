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
package eu.dzhw.zofar.management.generator.qml.tokens;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.comm.db.postgresql.PostgresClient;
import eu.dzhw.zofar.management.generator.qml.tokens.components.PermutationGenerator;
import eu.dzhw.zofar.management.generator.qml.tokens.components.preloads.PreloadFuntions;
import eu.dzhw.zofar.management.security.text.TextCipherClient;
import eu.dzhw.zofar.management.utils.objects.CollectionClient;
public class PanelGenerator {
	private static final PanelGenerator INSTANCE = new PanelGenerator();
	private static final Logger LOGGER = LoggerFactory.getLogger(PanelGenerator.class);
	private PanelGenerator() {
		super();
	}
	public static PanelGenerator getInstance() {
		return INSTANCE;
	}
	public Map<String, String> generate(final String server, final String port, final String database, final String user, final String pass, String criteria, final String prefix, final String postfix, final String url, final String chars, final int minlength,
			final int maxlength, final Map<Integer, Map<String, Map<String, String>>> preloadRules) throws Exception {
		Map<String, String> back = new HashMap<String, String>();
		if (criteria == null)
			throw new Exception("No critera defined");
		if (criteria.trim().equals(""))
			throw new Exception("No critera defined");
		if (criteria.trim().toLowerCase().equals("false"))
			throw new Exception("Result will be empty");
		if (criteria.trim().toLowerCase().equals("true"))
			throw new Exception("Result will be all Entries");
		final TextCipherClient encoder = TextCipherClient.getInstance();
		final PreloadFuntions preloadFunctions = PreloadFuntions.getInstance();
		final List<Map<String, String>> panel = this.getData(server, port, database, user, pass, criteria);
		if (panel != null) {
			PermutationGenerator permutationGenerator = PermutationGenerator.getInstance();
			final List<String> permutations = permutationGenerator.calculatePermutations(chars, minlength, maxlength, panel.size(), true);
			List<String> csvHeader = new ArrayList<String>();
			csvHeader.add("token");
			csvHeader.add("url");
			List<Map<String, String>> tocsv = new ArrayList<Map<String, String>>();
			Map<String, String> mapping = new LinkedHashMap<String, String>();
			StringBuffer qmlBuffer = new StringBuffer();
			StringBuffer csvBuffer = new StringBuffer();
			StringBuffer mappingBuffer = new StringBuffer();
			StringBuffer sqlBuffer = new StringBuffer();
			Iterator<String> tokenIt = permutations.iterator();
			Integer lft = 1;
			for (final Map<String, String> item : panel) {
				String extendedToken = tokenIt.next();
				if (prefix != null)
					extendedToken = prefix + extendedToken;
				if (postfix != null)
					extendedToken = extendedToken + postfix;
				Map<String, Map<String, String>> individualPreloadRules = null;
				if (preloadRules.containsKey(lft))
					individualPreloadRules = preloadRules.get(lft);
				final Map<String, String> individualPreloads = new LinkedHashMap<String, String>();
				if (individualPreloadRules != null) {
					for (final Map.Entry<String, Map<String, String>> preloadItem : individualPreloadRules.entrySet()) {
						individualPreloads.put(preloadItem.getKey(), preloadFunctions.execute(lft, preloadItem.getValue().get("function"), preloadItem.getValue().get("value")));
					}
				}
				final StringBuffer buffer = new StringBuffer();
				buffer.append("<zofar:preload password=\"" + extendedToken + "\" name=\"" + extendedToken + "\">\n");
				sqlBuffer.append("INSERT INTO participant(id, version, password, token) VALUES (nextval('seq_participant_id'),0,'" + encoder.encodeSHA(extendedToken) + "','" + extendedToken + "');\n");
				for (final Map.Entry<String, String> property : item.entrySet()) {
					if (!property.getKey().startsWith("panel"))
						continue;
					buffer.append("<zofar:preloadItem variable=\"" + property.getKey() + "\" value=\"" + property.getValue() + "\"></zofar:preloadItem>\n");
					sqlBuffer.append("INSERT INTO surveydata(id, version, value, variablename, participant_id) VALUES (nextval('seq_surveydata_id'), 0, '" + property.getValue() + "', '" + property.getKey() + "', (select DISTINCT id from participant where token = '"
							+ extendedToken + "'));\n");
				}
				for (final Map.Entry<String, String> preloadItem : individualPreloads.entrySet()) {
					buffer.append("<zofar:preloadItem variable=\"" + preloadItem.getKey() + "\" value=\"" + preloadItem.getValue() + "\"></zofar:preloadItem>\n");
					sqlBuffer.append("INSERT INTO surveydata(id, version, value, variablename, participant_id) VALUES (nextval('seq_surveydata_id'), 0, '" + preloadItem.getValue() + "', '" + preloadItem.getKey() + "', (select DISTINCT id from participant where token = '"	+ extendedToken + "'));\n");
				}
				buffer.append("</zofar:preload>\n");
				final Map<String, String> csvRow = new HashMap<String, String>();
				csvRow.put("token", extendedToken);
				csvRow.put("url", url + extendedToken);
				for (final Map.Entry<String, String> preloadItem : individualPreloads.entrySet()) {
					if(!csvHeader.contains(preloadItem.getKey()))csvHeader.add(preloadItem.getKey());
					csvRow.put(preloadItem.getKey(), preloadItem.getValue());
				}
				tocsv.add(csvRow);
				mapping.put(item.get("id"), extendedToken);
				qmlBuffer.append(buffer.toString() + "\n");
				lft = lft + 1;
			}
			boolean first = true;
			for(final String header: csvHeader){
				if(!first)csvBuffer.append(";");
				csvBuffer.append("'"+header+"'");
				first = false;
			}
			if(!first)csvBuffer.append("\n");
			for(final Map<String, String> row: tocsv){
				first = true;
				for(final String header: csvHeader){
					if(!first)csvBuffer.append(";");
					String value = "";
					if(row.containsKey(header))value = row.get(header);
					csvBuffer.append("'"+value+"'");
					first = false;
				}
				if(!first)csvBuffer.append("\n");
			}
			mappingBuffer.append("'id';'token'\n");
			for (final Map.Entry<String, String> token : mapping.entrySet()) {
				mappingBuffer.append("'" + token.getKey() + "';'" + token.getValue() + "'\n");
			}
			back.put("qml", qmlBuffer.toString());
			back.put("csv", csvBuffer.toString());
			back.put("mapping", mappingBuffer.toString());
			back.put("sql", sqlBuffer.toString());
			return back;
		}
		return null;
	}
	private List<Map<String, String>> getData(final String url, final String port, final String database, final String user, final String pass, final String criteria) throws Exception {
		final PostgresClient client = PostgresClient.getInstance();
		final Connection con = client.getConnection(url, port, database, user, pass);
		if (con != null) {
			List<Object> ids = new ArrayList<Object>();
			String query = "Select distinct paneldata.participant_id from paneldata,answeroption WHERE paneldata.quid = CAST(answeroption.quid AS varchar) and paneldata.value = CAST(answeroption.answeroptionuid AS varchar) and (" + criteria + ")";
			List<Map<String, String>> idResult = client.queryDb(con, query);
			if (idResult != null) {
				for (final Map<String, String> item : idResult) {
					ids.add(item.get("participant_id"));
				}
			}
			if (!ids.isEmpty()) {
				query = "Select * from participant WHERE id in (" + CollectionClient.getInstance().implode(ids) + ")";
				List<Map<String, String>> panel = client.queryDb(con, query);
				Map<String, Map<String, String>> answerOptions = new HashMap<String, Map<String, String>>();
				if (panel != null) {
					for (final Map<String, String> item : panel) {
						List<Map<String, String>> paneldata = client.queryDb(con, "Select * from paneldata WHERE participant_id=" + item.get("id") + "");
						if (paneldata != null) {
							for (final Map<String, String> data : paneldata) {
								final String quid = data.get("quid");
								final String value = data.get("value");
								if (!answerOptions.containsKey(quid)) {
									Map<String, String> aos = new HashMap<String, String>();
									List<Map<String, String>> answerOption = client.queryDb(con, "Select * from answeroption WHERE quid='" + quid + "'");
									if (answerOption != null) {
										for (Map<String, String> tmp : answerOption) {
											String label = tmp.get("label");
											if (label != null)
												label = label.trim();
											aos.put(tmp.get("answeroptionuid"), label);
										}
									}
									answerOptions.put(quid, aos);
								}
								item.put("panel" + quid + "_value", value);
								item.put("panel" + quid + "_label", answerOptions.get(quid).get(value));
							}
						}
					}
					client.close(con);
					return panel;
				}
			}
			client.close(con);
		}
		return null;
	}
}
