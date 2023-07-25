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
package de.his.zofar.presentation.surveyengine.util;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author meisner
 * 
 */
public class StringUtility implements Serializable {
	private static final long serialVersionUID = 5401177052616219646L;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(StringUtility.class);
	private static final StringUtility INSTANCE = new StringUtility();
    private static final Map<String, String> DEESCAPE_CHARS;
    static
    {
    	DEESCAPE_CHARS = new HashMap<String, String>();
    	DEESCAPE_CHARS.put("&auml;","ä");
    	DEESCAPE_CHARS.put("&Auml;","Ä");
    	DEESCAPE_CHARS.put("&ouml;","ö");
    	DEESCAPE_CHARS.put("&Ouml;","Ö");
    	DEESCAPE_CHARS.put("&uuml;","ü");
    	DEESCAPE_CHARS.put("&Uuml;","Ü");
    	DEESCAPE_CHARS.put("&szlig;","ß");
    	DEESCAPE_CHARS.put("&euro;","€");
    	DEESCAPE_CHARS.put("&copy;","©");
    	DEESCAPE_CHARS.put("&bull;","•");
    	DEESCAPE_CHARS.put("&trade;","™");
    	DEESCAPE_CHARS.put("&reg;","®");
    	DEESCAPE_CHARS.put("&sect;","§");
    	DEESCAPE_CHARS.put("&#124;","|");
    }
    private static final Map<String, String> REPLACE_CHARS;
    static
    {
    	REPLACE_CHARS = new HashMap<String, String>();
    	REPLACE_CHARS.put("&amp;","#");
    	REPLACE_CHARS.put("&lt;","#");
    	REPLACE_CHARS.put("&gt;","#");
    	REPLACE_CHARS.put("&quot;","#");
    }
	private StringUtility() {
		super();
	}
	public synchronized static final StringUtility getInstance() {
		return INSTANCE;
	}
	public String escapeHtml(final String dirtyStr){
		String back = StringEscapeUtils.escapeHtml4(StringEscapeUtils.unescapeHtml4(dirtyStr));
		for(Entry<String, String> entry : REPLACE_CHARS.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    back = back.replaceAll(Pattern.quote(key), value);
		}
		for(Entry<String, String> entry : DEESCAPE_CHARS.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    back = back.replaceAll(Pattern.quote(key), value);
		}
		return back;
	}
}
