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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
import eu.dzhw.zofar.management.generator.qml.tokens.components.preloads.PreloadFuntions;
import eu.dzhw.zofar.management.security.text.TextCipherClient;
public class MitarbeiterGenerator {
	private static final MitarbeiterGenerator INSTANCE = new MitarbeiterGenerator();
	private static final Logger LOGGER = LoggerFactory.getLogger(MitarbeiterGenerator.class);
	private MitarbeiterGenerator() {
		super();
	}
	public static MitarbeiterGenerator getInstance() {
		return INSTANCE;
	}
	public Map<String, String> generate(final String url,final Map<Integer, Map<String, Map<String, String>>> preloadRules) throws Exception {
		final HTTPClient spider = HTTPClient.getInstance();
		final TextCipherClient encoder = TextCipherClient.getInstance();
		final PreloadFuntions preloadFunctions = PreloadFuntions.getInstance();
		List<String> detailAnchors = new ArrayList<String>();
		detailAnchors.addAll(getLinks(spider, "http://www.####/gmbh/orga", ""));
		detailAnchors.addAll(getLinks(spider, "http://www.####/gmbh/orga", "mitarbeiter_II"));
		detailAnchors.addAll(getLinks(spider, "http://www.####/gmbh/orga", "verwaltung"));
		if (detailAnchors != null) {
			Iterator<String> it = detailAnchors.iterator();
			List<String> preloads = new ArrayList<String>();
			List<String> csvHeader = new ArrayList<String>();
			csvHeader.add("token");
			csvHeader.add("url");
			csvHeader.add("name");
			csvHeader.add("mail");
			List<Map<String, String>> tocsv = new ArrayList<Map<String, String>>();
			List<String> sql = new ArrayList<String>();
			Integer lft = 1;
			while (it.hasNext()) {
				final String link = it.next();
				final Page detailPage = spider.loadPage(link);
				if (detailPage != null) {
					Map<String, String> details = getDetails(detailPage);
					Map<String, Map<String, String>> individualPreloadRules = null;
					if(preloadRules.containsKey(lft))individualPreloadRules = preloadRules.get(lft);
					final Map<String,String> individualPreloads = new LinkedHashMap<String,String>();
					if(individualPreloadRules != null){
						for(final Map.Entry<String, Map<String, String>> item:individualPreloadRules.entrySet()){
							individualPreloads.put(item.getKey(), preloadFunctions.execute(lft,item.getValue().get("function"),item.getValue().get("value")));
						}
					}
					final StringBuffer buffer = new StringBuffer();
					buffer.append("<zofar:preload password=\"" + details.get("tel") + "\" name=\"" + details.get("tel") + "\">");
					buffer.append("<zofar:preloadItem variable=\"email\" value=\"" + details.get("mail") + "\"></zofar:preloadItem>");
					buffer.append("<zofar:preloadItem variable=\"fullname\" value=\"" + details.get("name") + "\"></zofar:preloadItem>");
					for(final Map.Entry<String,String> preloadItem : individualPreloads.entrySet()){
						buffer.append("<zofar:preloadItem variable=\""+preloadItem.getKey()+"\" value=\"" + preloadItem.getValue() + "\"></zofar:preloadItem>");
					}
					buffer.append("</zofar:preload>");
					preloads.add(buffer.toString());
					final Map<String, String> csvRow = new HashMap<String, String>();
					csvRow.put("token", details.get("tel"));
					csvRow.put("url", url + details.get("tel"));
					csvRow.put("name", details.get("name"));
					csvRow.put("mail", details.get("mail"));
					for (final Map.Entry<String, String> preloadItem : individualPreloads.entrySet()) {
						if(!csvHeader.contains(preloadItem.getKey()))csvHeader.add(preloadItem.getKey());
						csvRow.put(preloadItem.getKey(), preloadItem.getValue());
					}
					tocsv.add(csvRow);
					sql.add("INSERT INTO participant(id, version, password, token) VALUES (nextval('seq_participant_id'),0,'"+ encoder.encodeSHA(details.get("tel")) + "','" + details.get("tel") + "');");
					sql.add("INSERT INTO surveydata(id, version, value, variablename, participant_id) VALUES (nextval('seq_surveydata_id'), 0, '"+ details.get("mail")+ "', 'email', (select DISTINCT id from participant where token = '" + details.get("tel") + "'));");
					sql.add("INSERT INTO surveydata(id, version, value, variablename, participant_id) VALUES (nextval('seq_surveydata_id'), 0, '"+ details.get("name")+ "', 'fullname', (select DISTINCT id from participant where token = '" + details.get("tel") + "'));");
					for(final Map.Entry<String,String> preloadItem : individualPreloads.entrySet()){
						sql.add("INSERT INTO surveydata(id, version, value, variablename, participant_id) VALUES (nextval('seq_surveydata_id'), 0, '"+ preloadItem.getValue()+ "', '"+ preloadItem.getKey()+ "', (select DISTINCT id from participant where token = '" + details.get("tel") + "'));");
					}
					lft = lft + 1;
				}
			}
			it = preloads.iterator();
			StringBuffer qmlBuffer = new StringBuffer();
			while (it.hasNext()) {
				final String preload = it.next();
				qmlBuffer.append(preload + "\n");
			}
			StringBuffer csvBuffer = new StringBuffer();
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
			it = sql.iterator();
			StringBuffer sqlBuffer = new StringBuffer();
			while (it.hasNext()) {
				final String sqlItem = it.next();
				sqlBuffer.append(sqlItem + "\n");
			}
			Map<String, String> back = new HashMap<String, String>();
			back.put("qml", qmlBuffer.toString());
			back.put("csv", csvBuffer.toString());
			back.put("sql", sqlBuffer.toString());
			return back;
		}
		return null;
	}
	private List<String> getLinks(final HTTPClient spider, final String baseURL, final String relUrl) throws Exception {
		final Page page = spider.loadPage(baseURL + "/" + relUrl);
		final List<String> detailAnchors = new ArrayList<String>();
		if (page != null) {
			if ((HtmlPage.class).isAssignableFrom(page.getClass())) {
				final HtmlPage tmp = (HtmlPage) page;
				List<HtmlAnchor> links = spider.getLinks(tmp, "starts-with(@href, 'mitarbeiter?auskunft')");
				if (links != null) {
					Iterator<HtmlAnchor> it = links.iterator();
					while (it.hasNext()) {
						final HtmlAnchor link = it.next();
						final String absoluteHref = baseURL + "/" + link.getHrefAttribute();
						if (!detailAnchors.contains(absoluteHref))
							detailAnchors.add(absoluteHref);
					}
				}
			}
		}
		return detailAnchors;
	}
	final static Pattern emailPattern = Pattern.compile("E-Mail: (([^ ]*)####.eu)");
	final static Pattern telPattern = Pattern.compile("Tel.: ((0511 1220)|(0341 962765))-?([^ ]{1,3})");
	private Map<String, String> getDetails(final Page detailPage) {
		final Map<String, String> details = new HashMap<String, String>();
		if (detailPage != null) {
			if ((HtmlPage.class).isAssignableFrom(detailPage.getClass())) {
				final HtmlPage tmp = (HtmlPage) detailPage;
				List<HtmlSpan> name = (List<HtmlSpan>) tmp.getByXPath("//span[starts-with(@class, 'h3')]");
				if ((name != null) && (name.size() > 0))
					details.put("name", name.get(0).asText());
				String content = tmp.asText();
				content = content.replace('\n', ' ');
				Matcher matcher = emailPattern.matcher(content);
				if (matcher.find()) {
					details.put("mail", matcher.group(1));
				}
				matcher = telPattern.matcher(content);
				if (matcher.find()) {
					details.put("tel", matcher.group(4));
				}
			}
		}
		return details;
	}
}
