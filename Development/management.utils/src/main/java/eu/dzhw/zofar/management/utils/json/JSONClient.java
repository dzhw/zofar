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
package eu.dzhw.zofar.management.utils.json;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
public class JSONClient {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(JSONClient.class);
	/** The instance. */
	private static JSONClient INSTANCE;
	/**
	 * Instantiates a new xml client.
	 */
	private JSONClient() {
		super();
	}
	/**
	 * Gets the single instance of XmlClient.
	 * 
	 * @return single instance of XmlClient
	 */
	public static JSONClient getInstance() {
		if (INSTANCE == null)
			INSTANCE = new JSONClient();
		return INSTANCE;
	}
	public final JsonArray episodesCronSort(final JsonArray data) {
		return this.episodesCronSort(data, "startDate");
	}
	public final JsonArray episodesCronSort(final JsonArray data,final String sortProperty) {
		if(data == null)return null;
		if(data.size() == 0) return data;
		final Map<Date,List<JsonElement>> startMap = new LinkedHashMap<Date,List<JsonElement>>();
		Iterator<JsonElement> it = data.iterator();
		while(it.hasNext()) {
			final JsonElement elem = it.next();
			try {
				final Date startDate = this.parseDate((String)this.getJsonProperty(elem, "startDate"));
				List<JsonElement> startList = null;
				if(startMap.containsKey(startDate))startList = startMap.get(startDate);
				if(startList == null)startList = new ArrayList<JsonElement>();
				startList.add(elem);
				startMap.put(startDate,startList);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		List<Date> sortedKeys = new ArrayList<Date>(startMap.keySet());
		Collections.sort(sortedKeys);
		final JsonArray sorted = new JsonArray();
		for(final Date startDate : sortedKeys) {
			List<JsonElement> episodes = startMap.get(startDate);
			if(episodes.isEmpty()) continue;
			if(episodes.size() == 1) {
				sorted.add(episodes.get(0));
			}
			else {
				final Map<Date,List<JsonElement>> endMap = new LinkedHashMap<Date,List<JsonElement>>();
				Iterator<JsonElement> endIt = episodes.iterator();
				while(endIt.hasNext()) {
					final JsonElement elem = endIt.next();
					try {
						final Date endDate = this.parseDate((String)this.getJsonProperty(elem, "endDate"));
						List<JsonElement> endList = null;
						if(endMap.containsKey(endDate))endList = endMap.get(endDate);
						if(endList == null)endList = new ArrayList<JsonElement>();
						endList.add(elem);
						endMap.put(endDate,endList);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				List<Date> sortedEndKeys = new ArrayList<Date>(endMap.keySet());
				Collections.sort(sortedEndKeys);
				for(final Date endDate : sortedEndKeys) {
					List<JsonElement> endEpisodes = endMap.get(endDate);
					if(endEpisodes.isEmpty()) continue;
					if(endEpisodes.size() == 1) {
						sorted.add(endEpisodes.get(0));
					}
					else {
						for(final JsonElement endEpisode : endEpisodes) {
							sorted.add(endEpisode);
						}
					}
				}
			}
		}
		return sorted;
	}
	private final Date parseDate(final String date) throws ParseException{
		final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return inputFormat.parse(date);
	}
	public final int beforeEpisodeIndex(final JsonArray data, final int stopIndex, final String property, final String value)throws Exception {
		if(data == null)return -1;
		if(data.size() == 0) return -1;
		for(int index = Math.min(data.size()-1, stopIndex); index >= 0;index--) {
			final JsonElement tmp = data.get(index);
			if(!tmp.isJsonObject())continue;
			final JsonObject episode = (JsonObject)tmp;
			if(!episode.has(property))continue;
			final JsonElement propertyElement = episode.get(property);
			if(!propertyElement.isJsonPrimitive()) continue;
			if(propertyElement.getAsString().contentEquals(value)) return index;
		}
		return -1;
	}
	public final int nextEpisodeIndex(final JsonArray data, final int startIndex, final String property, final String value)throws Exception {
		if(data == null)return -1;
		if(data.size() == 0) return -1;
		if(data.size() < startIndex) return -1;
		int count = data.size();
		for(int index = Math.max(0, startIndex); index < count;index++) {
			final JsonElement tmp = data.get(index);
			if(!tmp.isJsonObject())continue;
			final JsonObject episode = (JsonObject)tmp;
			if(!episode.has(property))continue;
			final JsonElement propertyElement = episode.get(property);
			if(!propertyElement.isJsonPrimitive()) continue;
			if(propertyElement.getAsString().contentEquals(value)) return index;
		}
		return -1;
	}
	public final JsonArray hasEpisodes(final JsonArray data, final String rangeStart, final String rangeStop, final String property, final String value)throws Exception {
		final JsonArray back = new JsonArray();
		if(data == null)return null;
		if(data.size() == 0) return back;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if(rangeStart != null) rangeStartDate = format.parse(rangeStart);
		if(rangeStop != null)  rangeEndDate = format.parse(rangeStop);
		final Iterator<JsonElement> it = data.iterator();
		while(it.hasNext()) {
			final JsonElement tmp = it.next();
			if(!tmp.isJsonObject())continue;
			final JsonObject episode = (JsonObject)tmp;
			if(rangeStartDate != null) {
				if(!episode.has("startDate"))continue;
				Date episodeStartDate = format.parse(episode.get("startDate").getAsString());
				if(episodeStartDate.before(rangeStartDate))continue;
			}
			if(rangeEndDate != null) {
				if(!episode.has("endDate"))continue;
				Date episodeEndDate = format.parse(episode.get("endDate").getAsString());
				if(episodeEndDate.after(rangeEndDate))continue;
			}
			if(!episode.has(property))continue;
			final JsonElement propertyElement = episode.get(property);
			if(!propertyElement.isJsonPrimitive()) continue;
			if(propertyElement.getAsString().contentEquals(value)) {
				back.add(episode);
			}
		}
		return back;
	}
	public final JsonArray str2jsonArr(final String serializedData) {
		if(serializedData == null)return null;
		if(serializedData.contentEquals(""))return new JsonArray();
        try {
            final String decoded = URLDecoder.decode(serializedData, StandardCharsets.UTF_8.toString());
            JsonArray jsonArray = new JsonParser().parse(decoded).getAsJsonArray();
            return jsonArray;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
	}
	public final String jsonArr2str(final JsonArray data) {
		if(data == null)return "";
		return data.toString();
	}
	public final JsonElement createJson() {
		return new JsonArray();
	}
	public final JsonElement getJson(JsonArray arr,final int index) {
		if(arr == null)return JsonNull.INSTANCE;
		if(index < 0 )return JsonNull.INSTANCE;
		if(index >= arr.size())return JsonNull.INSTANCE;
		return arr.get(index);
	}
	public final JsonElement getOrCreateJson(JsonArray arr,final int index) {
		if(arr == null)return JsonNull.INSTANCE;
		if(index < 0 )return JsonNull.INSTANCE;
		if(index >= arr.size())return new JsonObject();
		return arr.get(index);
	}
	public final JsonArray insertJson(JsonArray arr,final JsonElement data,final Integer index) {
		if(arr == null)arr = new JsonArray();
		if(data == null)return arr;
		if(index < 0)arr.add(data);
		else if(arr.size() <= index)arr.add(data);
		else {
			JsonArray newArr = new JsonArray();
			final int size = arr.size();
			for(Integer a=0;a<size;a++) {
				final JsonElement tmp = arr.get(a);
				if(a == index)newArr.add(data);
				newArr.add(tmp);
			}
			return newArr;
		}
		return arr;
	}
	public final JsonArray addJson(JsonArray arr,final JsonElement data) {
		if(arr == null)arr = new JsonArray();
		if(data == null)return arr;
		arr.add(data);
		return arr;
	}
	public final JsonArray addOrReplaceJson(JsonArray arr,final JsonElement data,final int index) {
		if(arr == null)arr = new JsonArray();
		if(data == null)return arr;
		if(index < 0)arr.add(data);
		else if(arr.size() <= index)arr.add(data);
		else arr.set(index, data);
		return arr;
	}
	public final JsonArray delJson(JsonArray arr,final JsonElement data) {
		if(arr == null)arr = new JsonArray();
		if(data == null)return arr;
		if(arr.contains(data))arr.remove(data);
		return arr;
	}
	public final int indexOfJson(JsonArray arr,final JsonElement element) {
		if(arr == null)return -1;
		if(element == null)return -1;
		final int size = arr.size();
		for(int a=0;a<size;a++) {
			final JsonElement tmp = arr.get(a);
			if(tmp.equals(element)) {
				return a;
			}
		}
		return -1;
	}
	public final JsonArray replaceJson(JsonArray arr,final JsonElement oldData,final JsonElement newData) {
		if(arr == null)arr = new JsonArray();
		if(oldData == null)return arr;
		int index = -1;
		final int size = arr.size();
		for(int a=0;a<size;a++) {
			final JsonElement tmp = arr.get(a);
			if(tmp.equals(oldData)) {
				index = a;
				break;
			}
		}
		if(index >= 0)arr.set(index, newData);
		return arr;
	}
	public final Object getJsonProperty(final JsonElement data,final String property) {
		if(data == null)return JsonNull.INSTANCE;
		if(!data.isJsonObject())return JsonNull.INSTANCE;
		final JsonObject jsonObj = data.getAsJsonObject();
		if(!jsonObj.has(property))return "";
		final JsonElement back = jsonObj.get(property);
		if(back.isJsonPrimitive())return back.getAsString();
		return back;
	}
	public final JsonElement setJsonProperty(final JsonElement data,final String property, final Object value) {
		JsonElement back = JsonNull.INSTANCE;
		if(data == null)back = new JsonObject();
		else if(data.isJsonNull())back = new JsonObject();
		else back = data;
		if(back.isJsonObject()) {
			if((data != null)&&(data.isJsonObject())&&(data.getAsJsonObject().has(property)))((JsonObject)back).remove(property);
			if((JsonElement.class).isAssignableFrom(value.getClass()))((JsonObject)back).add(property, (JsonElement)value);
			else if((String.class).isAssignableFrom(value.getClass())) {
				if(!((String)value).contentEquals(""))((JsonObject)back).add(property, new JsonPrimitive((String)value));
			}
			else if((Boolean.class).isAssignableFrom(value.getClass()))((JsonObject)back).add(property, new JsonPrimitive((Boolean)value));
			else if((Number.class).isAssignableFrom(value.getClass()))((JsonObject)back).add(property, new JsonPrimitive((Number)value));
			else {
				System.out.println("unhandled json property type found : "+value.getClass()+" !! converted to String");
				((JsonObject)back).add(property, new JsonPrimitive(value+""));
			}
		}
		return back;
	}
}
