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
package eu.dzhw.zofar.management.comm.network.webservices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gargoylesoftware.htmlunit.Page;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
public class GenderService {
	/** The Constant INSTANCE. */
	private static final GenderService INSTANCE = new GenderService();
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(GenderService.class);
	private static final String USER_KEY = "DTWRNysaTsPdocFFke";
	/**
	 * Instantiates a new HTTP client.
	 */
	private GenderService() {
		super();
	}
	public static GenderService getInstance() {
		return INSTANCE;
	}
	public String getGenderBySurname(final String surname, final String country){
		try {
			Page answer = HTTPClient.getInstance().loadPage("https://gender-api.com/get?key="+USER_KEY+"&name="+surname+"&country="+country);
			if(answer != null){
				final String content = answer.getWebResponse().getContentAsString();
			    Gson gson = new Gson();
			    JsonObject json = gson.fromJson(content, JsonObject.class);
			    String gender = json.get("gender").getAsString(); 
			    return gender;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
