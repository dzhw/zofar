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
package eu.dzhw.zofar.management.utils.string;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ConvertClient {
	/** The Constant INSTANCE. */
	private static final ConvertClient INSTANCE = new ConvertClient();
	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(ConvertClient.class);
	/**
	 * Instantiates a new string utils.
	 */
	private ConvertClient() {
		super();
	}
	/**
	 * Gets the single instance of StringUtils.
	 * 
	 * @return single instance of StringUtils
	 */
	public static synchronized ConvertClient getInstance() {
		return INSTANCE;
	}
	public String convert(String source,Charset fromCharset,Charset toCharset) throws Exception{
		if(source == null)return null;
		return new String(source.getBytes(fromCharset),toCharset);
	}
	public List<String> availableCharacterSets(){
		return new ArrayList<String>(Charset.availableCharsets().keySet());
	}
	public Charset getCharacterSet(final String charsetName){
		return Charset.forName(charsetName);
	}
}
