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
package de.his.zofar.presentation.surveyengine.provider;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import de.his.zofar.presentation.surveyengine.AbstractAnswerBean;
import de.his.zofar.presentation.surveyengine.StringValueTypeBean;
import de.his.zofar.presentation.surveyengine.controller.NavigatorBean;
import de.his.zofar.presentation.surveyengine.controller.SessionController;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.util.JsfUtility;
/**
 * Bean to provide Episode specific EL - Functions
 * 
 * @author meisner dick friedrich
 * @version 0.0.2
 * 
 */
@ManagedBean(name = "episodes")
@ApplicationScoped
public class EpisodesProvider implements Serializable {
	protected static final long serialVersionUID = -5918995633008831644L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EpisodesProvider.class);
	public EpisodesProvider() {
		super();
	}
	@PostConstruct
	private void init() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("init");
		}
	}
	public String timestamp2monthpicker(final String input_timestamp) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (!zofar.isWellFormedTimestamp(input_timestamp) || zofar.isMissingDateValue(input_timestamp))
			return "1970,01,01";
		final String yearString = input_timestamp.substring(0, 4);
		final String monthString = input_timestamp.substring(5, 7);
		final String dayString = input_timestamp.substring(8, 10);
		return yearString + "," + monthString + "," + dayString;
	}
	public String monthpicker2timestamp(final String input_timestamp) {
		if (input_timestamp == null || input_timestamp.trim() == "")
			return "1970-01-01T01:00:00.000Z";
		final String[] timestamp_list = input_timestamp.split(",");
		if (timestamp_list.length != 3)
			return "1970-01-01T01:00:00.000Z";
		final String yearString = timestamp_list[0];
		String monthString = timestamp_list[1];
		if (monthString.length() == 1)
			monthString = "0" + monthString;
		String dayString = timestamp_list[2];
		if (dayString.length() == 1)
			dayString = "0" + dayString;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final String returnString = yearString + "-" + monthString + "-" + dayString + "T01:00:00.000Z";
		if (!zofar.isWellFormedTimestamp(returnString) || zofar.isMissingDateValue(returnString))
			return "1970-01-01T01:00:00.000Z";
		return returnString;
	}
	public String monthPickerIncreaseByMonths(final String inputMonthpickerTS, final int months) {
		if (months == 0)
			return inputMonthpickerTS;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final String timestamp = monthpicker2timestamp(inputMonthpickerTS);
		return timestamp2monthpicker(zofar.timestampTimeDelta(timestamp, 0, months, 0, 0, 0, 0, 0));
	}
	public String yearStr2monthpickerYearEnd(final String input_year) {
		return timestamp2monthpicker(input_year + "-12-31T01:00:00.000Z");
	}
	public String yearStr2monthpickerYearStart(final String input_year) {
		return timestamp2monthpicker(input_year + "-01-01T01:00:00.000Z");
	}
	public synchronized String getLowestDate(final JsonArray episodes, final String property) {
		return getLowestDate(episodes, property, "1970-12-31T23:59:59.999Z");
	}
	public synchronized String getLowestDate(final JsonArray episodes, final String property, final String limit) {
		if (episodes == null)
			return "0000-00-00T00:00:00.000Z";
		if (episodes.size() == 0)
			return "0000-00-00T00:00:00.000Z";
		if (property == null)
			return "0000-00-00T00:00:00.000Z";
		final List<Date> sortedList = new ArrayList<Date>();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (final JsonElement episode : episodes) {
			if (!episode.isJsonObject())
				continue;
			final JsonObject episodeObj = episode.getAsJsonObject();
			if (!episodeObj.has(property))
				continue;
			final JsonElement propertyValue = episodeObj.get(property);
			if (!propertyValue.isJsonPrimitive())
				continue;
			try {
				if (compareJsonTimestamps(propertyValue.getAsString(), limit) > 0) {
					sortedList.add(df.parse(propertyValue.getAsString()));
				}
			} catch (final Exception e) {
				continue;
			}
		}
		if (!sortedList.isEmpty()) {
			Collections.sort(sortedList);
			return df.format(sortedList.get(0));
		}
		return "0000-00-00T00:00:00.000Z";
	}
	public synchronized String getHighestDate(final JsonArray episodes, final String property) {
		return getHighestDate(episodes, property, "4000-01-01T00:00:00.000Z");
	}
	public synchronized String getHighestDate(final JsonArray episodes, final String property, final String limit) {
		if (episodes == null)
			return "0000-00-00T00:00:00.000Z";
		if (episodes.size() == 0)
			return "0000-00-00T00:00:00.000Z";
		if (property == null)
			return "0000-00-00T00:00:00.000Z";
		final List<Date> sortedList = new ArrayList<Date>();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (final JsonElement episode : episodes) {
			if (!episode.isJsonObject())
				continue;
			final JsonObject episodeObj = episode.getAsJsonObject();
			if (!episodeObj.has(property))
				continue;
			final JsonElement propertyValue = episodeObj.get(property);
			if (!propertyValue.isJsonPrimitive())
				continue;
			try {
				if (compareJsonTimestamps(propertyValue.getAsString(), limit) < 0) {
					sortedList.add(df.parse(propertyValue.getAsString()));
				}
			} catch (final Exception e) {
				continue;
			}
		}
		if (!sortedList.isEmpty()) {
			Collections.sort(sortedList);
			return df.format(sortedList.get(sortedList.size() - 1));
		}
		return "0000-00-00T00:00:00.000Z";
	}
	/**
	 * helper function for checking if a specified property value within the json
	 * episode is a missing value
	 * 
	 * @param arr      the whole json array
	 * @param index    the index of the episode in question
	 * @param property the key / name string of the property value in question
	 * @return true if the property name is present and a parseable date could be
	 *         found and the date is missing, false if else
	 */
	private boolean nonMissingDateProperty(final JsonArray arr, final int index, final String property) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		final JsonElement episode = arr.get(index);
		return nonMissingDateProperty(episode, property);
	}
	/**
	 * helper function for checking if a specified property value within the json
	 * episode is a missing value
	 * 
	 * @param episode  the index of the episode in question
	 * @param property the key / name string of the property value in question
	 * @return true if the property name is present and a parseable date could be
	 *         found and the date is missing, false if else
	 */
	private boolean nonMissingDateProperty(final JsonElement episode, final String property) {
		final JsonObject episodeObj = (JsonObject) episode;
		if (hasJsonProperty(episodeObj, property)) {
			final FunctionProvider zofar = JsfUtility.getInstance()
					.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
			final String testDate = (String) getJsonProperty(episodeObj, property);
			if (zofar.isTimeStampParseable(testDate))
				return !zofar.isMissingDateValueParseable(testDate);
		}
		return false;
	}
	/**
	 * function to determine whether endDate value of an episode within a json array
	 * is non-missing
	 * 
	 * @param arr   the whole json array
	 * @param index the index of the episode in question
	 * @return true if the property "endDate" is present and a parseable date could
	 *         be found and the date is non-missing, false if else
	 */
	public boolean nonMissingEndDate(final JsonArray arr, final int index) {
		return this.nonMissingDateProperty(arr, index, "endDate");
	}
	/**
	 * function to determine whether endDate value of a given episode is non-missing
	 * 
	 * @param episode in question
	 * @return true if the property "endDate" is present and a parseable date could
	 *         be found and the date is non-missing, false if else
	 */
	public boolean nonMissingEndDate(final JsonElement episode) {
		return this.nonMissingDateProperty(episode, "endDate");
	}
	/**
	 * function to determine whether startDate value of a given episode is
	 * non-missing
	 * 
	 * @param episode in question
	 * @return true if the property "startDate" is present and a parseable date
	 *         could be found and the date is non-missing, false if else
	 */
	public boolean nonMissingStartDate(final JsonElement episode) {
		return this.nonMissingDateProperty(episode, "startDate");
	}
	/**
	 * function to determine whether startDate value of an episode within a json
	 * array is non-missing
	 * 
	 * @param arr   the whole json array
	 * @param index the index of the episode in question
	 * @return true if the property "startDate" is present and a parseable date
	 *         could be found and the date is non-missing, false if else
	 */
	public boolean nonMissingStartDate(final JsonArray arr, final int index) {
		return this.nonMissingDateProperty(arr, index, "startDate");
	}
	/**
	 * compare two timestamps. Prior to that, that, it also checks via
	 * comparableTimestamps whether the two timestmap strings are parseable to
	 * defined Timestamp and have a non-missing value.
	 *
	 * @param timestamp1 first timestamp
	 * @param timestamp2 second timestamp
	 * @return -1 if timestamp1 is smaller than timestamp2; 0 if both timestamp
	 *         strings are identical; +1 if timestamp1 is larger than timestamp2;
	 *         for all other cases -2
	 */
	public int compareJsonTimestamps(final String timestamp1, final String timestamp2) {
		int compareResult = -2;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if ((zofar.isTimeStampParseable(timestamp1) && !zofar.isMissingDateValueParseable(timestamp1))
				&& (zofar.isTimeStampParseable(timestamp2) && !zofar.isMissingDateValueParseable(timestamp1))) {
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try {
				final Date d1 = simpleDateFormat.parse(timestamp1);
				final Date d2 = simpleDateFormat.parse(timestamp2);
				if (d1.before(d2)) {
					compareResult = -1;
				} else if (d1.after(d2)) {
					compareResult = 1;
				} else if (d1.compareTo(d2) == 0) {
					compareResult = 0;
				}
			} catch (final Exception e) {
			}
		}
		return compareResult;
	}
	public synchronized Float minFloatList(final List<Float> valueList) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		return zofar.minFloatList(valueList);
	}
	public synchronized String arrayDateMin(final JsonArray arr, final String propertyName, final String rangeMin,
			final String rangeMax, final List<Map<String, String>> filterMapArray) throws Exception {
		return arrayDateMinMax(arr, "min", propertyName, rangeMin, rangeMax, filterMapArray);
	}
	public synchronized String arrayDateMax(final JsonArray arr, final String propertyName, final String rangeMin,
			final String rangeMax, final List<Map<String, String>> filterMapArray) throws Exception {
		return arrayDateMinMax(arr, "max", propertyName, rangeMin, rangeMax, filterMapArray);
	}
	/**
	 *
	 * [REVIEWED CM]
	 *
	 * Returns the minimum or maximum value within the json array of all property
	 * field with the key of propertyName, over all episodes within the json array.
	 * rangeMin <= result <= rangeMax, if rangeMin and rangeMax are given; rangeMin
	 * <= result, if only rangeMin is given; result <= rangeMax, if only rangeMax is
	 * given;
	 *
	 * @param arr          the json array
	 * @param mode         "min" or "max"
	 * @param propertyName the key name of the property, e.g. "startDate" or
	 *                     "endDate"
	 * @param rangeMin     the minimum value
	 * @param rangeMax
	 * @param filter
	 * @return lexicographically smallest string in the property with the given
	 *         propertyName within the given range
	 */
	private synchronized String arrayDateMinMax(final JsonArray arr, final String mode, final String propertyName,
			final String rangeMin, final String rangeMax, final List<Map<String, String>> filter) throws Exception {
		if (arr == null)
			return null;
		if (mode == null)
			return null;
		if (!Objects.equals(mode, "min") && !Objects.equals(mode, "max"))
			return null;
		if (propertyName == null)
			return null;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		String rangeMinCopy = null;
		String rangeMaxCopy = null;
		if (zofar.isWellFormedTimestamp(rangeMin))
			rangeMinCopy = rangeMin;
		if (zofar.isWellFormedTimestamp(rangeMax))
			rangeMaxCopy = rangeMax;
		JsonArray copyJsonArray = null;
		if (filter != null) {
			copyJsonArray = this.filterArray(arr, filter);
		} else {
			copyJsonArray = arr.deepCopy();
		}
		final List<String> resultsList = new ArrayList<>();
		for (final JsonElement currentEpisodeElement : copyJsonArray) {
			final JsonObject currentEpisodeObject = (JsonObject) currentEpisodeElement;
			if (currentEpisodeObject.has(propertyName) && !currentEpisodeObject.get(propertyName).isJsonNull()) {
				final String resultString = currentEpisodeObject.get(propertyName).getAsString();
				boolean matchCandidate = true;
				if (!zofar.isWellFormedTimestamp(resultString))
					continue;
				if (rangeMinCopy != null && (resultString.compareTo(rangeMinCopy) < 0))
					matchCandidate = false;
				if (rangeMaxCopy != null && (resultString.compareTo(rangeMaxCopy) > 0))
					matchCandidate = false;
				if (matchCandidate) {
					resultsList.add(resultString);
				}
				if (!zofar.isWellFormedTimestamp(resultString))
					continue;
				if (rangeMinCopy != null && (resultString.compareTo(rangeMinCopy) < 0))
					matchCandidate = false;
				if (rangeMaxCopy != null && (resultString.compareTo(rangeMaxCopy) > 0))
					matchCandidate = false;
				if (matchCandidate) {
					resultsList.add(resultString);
				}
			}
		}
		if (resultsList.size() == 1) {
			return resultsList.get(0);
		}
		String resultString = null;
		for (final String timestamp : resultsList) {
			if (resultString == null) {
				resultString = timestamp;
			} else {
				if (((mode.equals("min")) && (resultString.compareTo(timestamp) > 0))
						|| (mode.equals("max")) && (resultString.compareTo(timestamp) < 0)) {
					resultString = timestamp;
				}
			}
		}
		return resultString;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param serializedData input string of a json array, e.g.: [{"key1":"val1"},
	 *                       {"key1":"val2"}]
	 * @return a parsed json array
	 */
	public synchronized JsonArray str2jsonArr(final String serializedData) {
		if (serializedData == null)
			return null;
		if (serializedData.contentEquals(""))
			return new JsonArray();
		try {
			String decoded = URLDecoder.decode(serializedData, StandardCharsets.UTF_8.toString());
			decoded = decoded.replaceAll("<prct>", "%");
			decoded = decoded.replaceAll("<pls>", "+");
			final JsonArray jsonArray = new JsonParser().parse(decoded).getAsJsonArray();
			return jsonArray;
		} catch (final UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}
	public synchronized String prettyPrintJson(final JsonElement json) {
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final String back = gson.toJson(json);
		return back;
	}
	public synchronized String prettyPrintJsonHtml(final JsonElement json) {
		final String prettyString = prettyPrintJson(json);
		return prettyString.replaceAll("\\n", "<br>").replaceAll("\\s", "&nbsp;");
	}
	public synchronized JsonObject parseJsonObj(final String toJson) {
		if (toJson == null)
			return null;
		if (toJson.contentEquals(""))
			return new JsonObject();
		final String decoded = toJson.replace('*', '\"');
		final JsonObject back = new JsonParser().parse(decoded).getAsJsonObject();
		return back;
	}
	private static final Map<String, String> QUOTE_CHARS;
	static {
		QUOTE_CHARS = new HashMap<String, String>();
		QUOTE_CHARS.put("'", "#27");
		QUOTE_CHARS.put("{", "#7B");
		QUOTE_CHARS.put("}", "#7D");
		QUOTE_CHARS.put("[", "#5B");
		QUOTE_CHARS.put("]", "#5D");
		QUOTE_CHARS.put(":", "#3A");
		QUOTE_CHARS.put(",", "#2c");
		QUOTE_CHARS.put("\\", "#5C");
	}
	public String quoteCharJson(final String str) {
		String jsonStr = str;
		for (final Entry<String, String> entry : QUOTE_CHARS.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			jsonStr = jsonStr.replaceAll(Pattern.quote(key), Matcher.quoteReplacement(value));
		}
		return jsonStr;
	}
	public String unQuoteCharJson(final String str) {
		String jsonStr = str;
		for (final Entry<String, String> entry : QUOTE_CHARS.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			jsonStr = jsonStr.replaceAll(Pattern.quote(value), Matcher.quoteReplacement(key));
		}
		return jsonStr;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @return
	 */
	public synchronized final String jsonArr2str(final JsonArray data) {
		if (data == null)
			return "";
		String serializedData = data.toString();
		serializedData = serializedData.replaceAll(Pattern.quote("%"), "<prct>");
		serializedData = serializedData.replaceAll(Pattern.quote("+"), "<pls>");
		return serializedData;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param index
	 * @return
	 */
	public synchronized final JsonElement getJson(final JsonArray arr, final int index) {
		if (arr == null)
			return JsonNull.INSTANCE;
		if (index < 0)
			return JsonNull.INSTANCE;
		if (index >= arr.size())
			return JsonNull.INSTANCE;
		return arr.get(index);
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param index
	 * @return
	 */
	public synchronized final JsonElement getOrCreateJson(final JsonArray arr, final int index) {
		if (arr == null)
			return JsonNull.INSTANCE;
		if (index < 0)
			return JsonNull.INSTANCE;
		if (index >= arr.size())
			return new JsonObject();
		return arr.get(index);
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param data
	 * @param index
	 * @return
	 */
	public synchronized final JsonArray insertJson(JsonArray arr, final JsonElement data, final Integer index) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		if (index < 0)
			arr.add(data);
		else if (arr.size() <= index)
			arr.add(data);
		else {
			final JsonArray newArr = new JsonArray();
			final int size = arr.size();
			for (Integer a = 0; a < size; a++) {
				final JsonElement tmp = arr.get(a);
				if (a == index)
					newArr.add(data);
				newArr.add(tmp);
			}
			return newArr;
		}
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param data
	 * @return
	 */
	public synchronized final JsonArray addJson(JsonArray arr, final JsonElement data) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		arr.add(data);
		return arr;
	}
	/**
	 * [REVIEW][TODO] PERFORMACE TEST
	 * 
	 * @param arr
	 * @param data
	 * @return
	 */
	public synchronized final JsonArray addJsonDeepCopy(JsonArray arr, final JsonElement data) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		final JsonArray back = arr.deepCopy();
		back.add(data);
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param index
	 * @return
	 */
	public synchronized final JsonArray delJson(final JsonArray arr, final int index) {
		if (arr == null)
			return new JsonArray();
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		arr.remove(index);
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param data
	 * @return
	 */
	public synchronized final JsonArray delJson(JsonArray arr, final JsonElement data) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		if (arr.contains(data))
			arr.remove(data);
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param oldData
	 * @param newData
	 * @return
	 */
	public synchronized final JsonArray replaceJson(JsonArray arr, final JsonElement oldData,
			final JsonElement newData) {
		if (arr == null)
			arr = new JsonArray();
		if (oldData == null)
			return arr;
		int index = -1;
		final int size = arr.size();
		for (int a = 0; a < size; a++) {
			final JsonElement tmp = arr.get(a);
			if (tmp.equals(oldData)) {
				index = a;
				break;
			}
		}
		if (index >= 0)
			arr.set(index, newData);
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray filterArray(final JsonArray data, final String rangeStart, final String rangeStop,
			final String property, final String value) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return null;
		if (data.size() == 0)
			return back;
		if (property == null)
			return back;
		if (value == null)
			return back;
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new LinkedHashMap<>();
		filter.put(property, value);
		filterList.add(filter);
		return filterArray(data, rangeStart, rangeStop, filterList);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray filterArray(final JsonArray data, final String rangeStart, final String rangeStop,
			final List<Map<String, String>> filter) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return back;
		if (data.size() == 0)
			return back;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (rangeStartDate != null) {
				if (!episode.has("startDate"))
					continue;
				final Date episodeStartDate = format.parse(episode.get("startDate").getAsString());
				if (episodeStartDate.before(rangeStartDate))
					continue;
			}
			if (rangeEndDate != null) {
				if (!episode.has("endDate"))
					continue;
				final Date episodeEndDate = format.parse(episode.get("endDate").getAsString());
				if (episodeEndDate.after(rangeEndDate))
					continue;
			}
			if (jsonObjectMatchesFilter(episode, filter))
				back.add(episode);
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray filterArray(final JsonArray data, final List<Map<String, String>> filter) {
		final JsonArray back = new JsonArray();
		if (data == null)
			return back;
		if (data.size() == 0)
			return back;
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (jsonObjectMatchesFilter(episode, filter))
				back.add(episode);
		}
		return back;
	}
	/**
	 * Helper function to ensure that the returned json array is free from empty
	 * json objects
	 *
	 * @param serializedData input string data
	 * @return a "cleaned" json array without empty json objects as elements
	 */
	public synchronized final JsonArray str2jsonArrNoEmpty(final String serializedData) throws Exception {
		final JsonArray jsonArray = str2jsonArr(serializedData);
		return removeBrokenJsonElements(removeEmptyJsonElementsFromArray(jsonArray));
	}
	private JsonArray removeBrokenJsonElements(final JsonArray jsonArray) throws Exception {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final List<Map<String, String>> filterList = new ArrayList<>();
		final Map<String, String> filter = new HashMap<>();
		filter.put("state", null);
		filter.put("startDate", null);
		filter.put("endDate", null);
		filter.put("id", null);
		filter.put("type", null);
		filterList.add(filter);
		final JsonArray jsonArrayCleaned = this.filterArray(jsonArray, filterList);
		final JsonArray jsonArrayCleanedSound = new JsonArray();
		if (jsonArrayCleaned.size() == 0)
			return jsonArrayCleaned;
		for (final JsonElement episodeElement : jsonArrayCleaned) {
			final JsonObject episodeObj = (JsonObject) episodeElement;
			if (episodeObj.has("startDate")) {
				final String startDate = episodeObj.get("startDate").getAsString();
				if (!zofar.isTimeStampParseable(startDate))
					continue;
			}
			if (episodeObj.has("endDate")) {
				final String endDate = episodeObj.get("endDate").getAsString();
				if (!zofar.isTimeStampParseable(endDate))
					continue;
			}
			if (episodeObj.has("state")) {
				final String state = episodeObj.get("state").getAsString();
				if (!state.equals("new") && !state.equals("done"))
					continue;
			}
			jsonArrayCleanedSound.add(episodeObj);
		}
		return jsonArrayCleanedSound;
	}
	/**
	 *
	 * @param episode
	 * @param filterList
	 * @return
	 */
	private boolean jsonObjectMatchesFilter(final JsonObject episode, final List<Map<String, String>> filterList) {
		if (episode == null)
			return false;
		if (filterList == null)
			return false;
		if (filterList.isEmpty())
			return false;
		boolean listMatch = false;
		for (final Map<String, String> filterEntry : filterList) {
			boolean entryMatch = true;
			for (final Map.Entry<String, String> filterItem : filterEntry.entrySet()) {
				if (!episode.has(filterItem.getKey())) {
					entryMatch = false;
					break;
				}
				if (filterItem.getValue() == null)
					continue;
				final JsonElement propertyElement = episode.get(filterItem.getKey());
				if (!(propertyElement.isJsonPrimitive() || propertyElement.isJsonArray())) {
					entryMatch = false;
					break;
				}
				if (propertyElement.isJsonPrimitive()) {
					if (!propertyElement.getAsString().contentEquals(filterItem.getValue())) {
						entryMatch = false;
						break;
					}
				}
				if (propertyElement.isJsonArray()) {
					if (!propertyElement.getAsJsonArray().contains(new JsonPrimitive(filterItem.getValue()))) {
						entryMatch = false;
						break;
					}
				}
			}
			if (entryMatch)
				listMatch = true;
		}
		return listMatch;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param properties
	 */
	public synchronized final void getJsonProperties(final JsonElement data, final List<String> properties) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		for (final String property : properties) {
			final AbstractAnswerBean variable = JsfUtility.getInstance().evaluateValueExpression(
					FacesContext.getCurrentInstance(), "#{" + property + "}", AbstractAnswerBean.class);
			String valueStr = getJsonProperty(data, property).toString();
			valueStr = unQuoteCharJson(valueStr);
			valueStr = valueStr.replaceAll(Pattern.quote("\\") + "{2,}", Matcher.quoteReplacement("\\"));
			valueStr = valueStr.replaceAll(Pattern.quote("\\"), Matcher.quoteReplacement("\\\\"));
			zofar.setVariableValue(variable, valueStr);
		}
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param objectName
	 * @param obj
	 * @param properties
	 */
	public synchronized final void setJsonProperties(final String objectName, final JsonElement obj,
			final Map<String, Object> properties) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		for (final Map.Entry<String, Object> property : properties.entrySet()) {
			zofar.assign(objectName, setJsonProperty(obj, property.getKey(), property.getValue()));
		}
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param property
	 * @param value
	 * @return
	 */
	public synchronized final JsonElement setJsonProperty(final JsonElement data, final String property,
			final Object value) {
		final Set<String> notQuoted = new HashSet<String>();
		notQuoted.add("startDate");
		notQuoted.add("endDate");
		return this.setJsonProperty(data, property, value, notQuoted);
	}
	public synchronized final JsonElement setJsonProperty(final JsonElement data, final String property,
			final Object value, final Set<String> notQuoted) {
		JsonElement back = JsonNull.INSTANCE;
		if (data == null)
			back = new JsonObject();
		else if (data.isJsonNull())
			back = new JsonObject();
		else
			back = data;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (back.isJsonObject()) {
			if ((data != null) && (data.isJsonObject()) && (data.getAsJsonObject().has(property)))
				((JsonObject) back).remove(property);
			if (value == null) {
				((JsonObject) back).remove(property);
			} else if ((JsonElement.class).isAssignableFrom(value.getClass()))
				((JsonObject) back).add(property, (JsonElement) value);
			else if ((String.class).isAssignableFrom(value.getClass())) {
				if (!((String) value).contentEquals("")) {
					if (!notQuoted.contains(property)) {
						if (zofar.isWellFormedTimestamp((String) value)) {
							((JsonObject) back).add(property, new JsonPrimitive((String) value));
						} else {
							((JsonObject) back).add(property, new JsonPrimitive(quoteCharJson((String) value)));
						}
					} else
						((JsonObject) back).add(property, new JsonPrimitive((String) value));
				}
			} else if ((Boolean.class).isAssignableFrom(value.getClass()))
				((JsonObject) back).add(property, new JsonPrimitive((Boolean) value));
			else if ((Number.class).isAssignableFrom(value.getClass()))
				((JsonObject) back).add(property, new JsonPrimitive((Number) value));
			else {
				LOGGER.warn("unhandled json property type found : {} !! converted to String", value.getClass());
				((JsonObject) back).add(property, new JsonPrimitive(value + ""));
			}
		}
		return back;
	}
	/**
	 * Convenient method to output a JsonElement as formatted String
	 * 
	 * @param data    JsonElement to be stringified
	 *                (example{'property1':'bla','complexProperty2':{'property1':'blub','property2':'bla'}})
	 * @param pattern String with containt placeholders for Json-Properties ("Thisis
	 *                Property1: [property1] or this :
	 *                [complexProperty2*property2]")
	 * @return formatted stringified JsonElement
	 */
	public synchronized final String printJson(final JsonElement data, final String pattern) {
		if (data == null)
			return "";
		if (!(JsonObject.class).isAssignableFrom(data.getClass()))
			return "";
		final Pattern placeholderPattern = Pattern.compile(
				"(" + Pattern.quote("[") + ")(((?!" + Pattern.quote("]") + ").)*)(" + Pattern.quote("]") + ")");
		final Matcher matcher = placeholderPattern.matcher(pattern);
		String back = pattern;
		final FacesContext fc = FacesContext.getCurrentInstance();
		while (matcher.find()) {
			final String found = matcher.group();
			String property = matcher.group(2);
			String subProperty = null;
			final int subIndex = property.indexOf('*');
			if (subIndex >= 0) {
				subProperty = property.substring(subIndex + 1);
				property = property.substring(0, subIndex);
			}
			String value = null;
			JsonElement valueObj = null;
			final Object tmp = getJsonProperty(data, property);
			if ((tmp != null) && ((JsonElement.class).isAssignableFrom(tmp.getClass()))) {
				valueObj = (JsonElement) tmp;
			}
			if (valueObj != null) {
				if (valueObj.isJsonPrimitive())
					value = ((JsonPrimitive) valueObj).getAsString();
				else if (valueObj.isJsonObject()) {
					if (subProperty != null) {
						value = printJson(valueObj, "[" + subProperty + "]");
					}
				}
			}
			if ((tmp != null) && (value == null))
				value = tmp + "";
			if (value == null)
				value = "";
			back = back.replace(found, value);
		}
		final String evaluatedBack = JsfUtility.getInstance().evaluateValueExpression(fc, "#{" + back + "}",
				String.class);
		return evaluatedBack;
	}
	public synchronized String labelOfType(final JsonArray types, final String type, final String language) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("episode labelOf : {} {}", types, type);
		}
		if (types == null)
			return "";
		if (types.isJsonNull())
			return "";
		if (type == null)
			return "";
		String back = "";
		final Iterator<JsonElement> it = types.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject tmpObj = tmp.getAsJsonObject();
			if (tmpObj.get("id").getAsString().contentEquals(type)) {
				back = tmpObj.get("label").getAsJsonObject().get(language).getAsString();
				break;
			}
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param property
	 * @return
	 */
	public synchronized final Object getJsonProperty(final JsonElement data, final String property) {
		if (data == null)
			return JsonNull.INSTANCE;
		if (!data.isJsonObject())
			return JsonNull.INSTANCE;
		final JsonObject jsonObj = data.getAsJsonObject();
		if (!jsonObj.has(property))
			return "";
		final JsonElement back = jsonObj.get(property);
		if (back.isJsonPrimitive())
			return back.getAsString();
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param episodes1
	 * @param episodes2
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonElement mergeEpisodes(final JsonElement episodes1, final JsonElement episodes2,
			final String key) throws Exception {
		final Map<String, JsonObject> backMap = new HashMap<String, JsonObject>();
		if (episodes1.isJsonArray()) {
			final JsonArray jsons = (JsonArray) episodes1;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					final JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (!backMap.containsKey(id))
						backMap.put(id, tmpJSON);
					else {
						final JsonObject loadedJSON = backMap.get(id);
						for (final Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey()))
								loadedJSON.add(property.getKey(), property.getValue());
						}
					}
				}
			}
		}
		if (episodes2.isJsonArray()) {
			final JsonArray jsons = (JsonArray) episodes2;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					final JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (!backMap.containsKey(id)) {
					} else {
						final JsonObject loadedJSON = backMap.get(id);
						boolean typeChanged = false;
						String loadedType = null;
						if (loadedJSON.has("type") && (loadedJSON.get("type").isJsonPrimitive()))
							loadedType = ((JsonPrimitive) loadedJSON.get("type")).getAsString();
						String tmpType = null;
						if (tmpJSON.has("type") && (tmpJSON.get("type").isJsonPrimitive()))
							tmpType = ((JsonPrimitive) tmpJSON.get("type")).getAsString();
						if ((loadedType != null) && (tmpType != null)) {
							typeChanged = (!(loadedType.contentEquals(tmpType)));
						}
						for (final Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey())) {
								if (!typeChanged)
									loadedJSON.add(property.getKey(), property.getValue());
							}
						}
					}
				}
			}
		}
		final JsonArray back = new JsonArray();
		for (final Map.Entry<String, JsonObject> item : backMap.entrySet()) {
			back.add(item.getValue());
		}
		return back;
	}
	/**
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String rangeStart, final String rangeStop,
			final Map<String, String> filter) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return back;
		if (data.size() == 0)
			return back;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final List<Map<String, String>> filterList = new ArrayList<>();
		filterList.add(filter);
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (rangeStartDate != null) {
				if (!episode.has("startDate"))
					continue;
				final Date episodeStartDate = format.parse(episode.get("startDate").getAsString());
				if (episodeStartDate.before(rangeStartDate))
					continue;
			}
			if (rangeEndDate != null) {
				if (!episode.has("endDate"))
					continue;
				final Date episodeEndDate = format.parse(episode.get("endDate").getAsString());
				if (episodeEndDate.after(rangeEndDate))
					continue;
			}
			final boolean match = jsonObjectMatchesFilter(episode, filterList);
			if (match)
				back.add(episode);
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String property, final String value)
			throws Exception {
		return hasEpisodes(data, null, null, property, value);
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String rangeStart, final String rangeStop,
			final String property, final String value) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return null;
		if (data.size() == 0)
			return back;
		if (property == null)
			return back;
		if (value == null)
			return back;
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		return hasEpisodes(data, rangeStart, rangeStop, filter);
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @return
	 * @throws ParseException
	 */
	public synchronized final boolean isEpisodesComplete(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		if (data == null)
			return false;
		if (data.size() == 0)
			return false;
		if (rangeStart == null)
			return false;
		if (rangeStop == null)
			return false;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		return isEpisodesCompleteHelper(data, rangeStartDate, rangeEndDate);
	}
	/**
	 * [REVIEW]
	 * 
	 * @param data
	 * @return
	 * @throws ParseException
	 */
	public synchronized final boolean isEpisodesComplete(final JsonArray data) throws ParseException {
		if (data == null)
			return false;
		if (data.size() == 0)
			return false;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date minStartDate = null;
		Date maxEndDate = null;
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject json = (JsonObject) tmp;
			if (!json.has("startDate"))
				continue;
			if (!json.has("endDate"))
				continue;
			String startDateStr = json.get("startDate").getAsString();
			startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentStartDate = format.parse(startDateStr);
			if (minStartDate == null)
				minStartDate = currentStartDate;
			else if (currentStartDate.before(minStartDate))
				minStartDate = currentStartDate;
			String endDateStr = json.get("endDate").getAsString();
			endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentEndDate = format.parse(endDateStr);
			if (maxEndDate == null)
				maxEndDate = currentEndDate;
			else if (currentEndDate.after(maxEndDate))
				maxEndDate = currentEndDate;
		}
		return isEpisodesCompleteHelper(data, minStartDate, maxEndDate);
	}
	/**
	 * [REVIEW][TODO] Helper method for EpisodesComplete
	 * 
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @return
	 * @throws ParseException
	 */
	private synchronized final boolean isEpisodesCompleteHelper(final JsonArray data, final Date rangeStart,
			final Date rangeStop) throws ParseException {
		if (data == null)
			return false;
		if (data.size() == 0)
			return false;
		if (rangeStart == null)
			return false;
		if (rangeStop == null)
			return false;
		final Set<Date> hasEpisodes = new HashSet<Date>();
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			if (tmp.isJsonNull())
				continue;
			final JsonObject json = (JsonObject) tmp;
			if (!json.has("startDate"))
				continue;
			if (!json.has("endDate"))
				continue;
			if (json.get("startDate").isJsonNull())
				continue;
			if (json.get("endDate").isJsonNull())
				continue;
			String startDateStr = json.get("startDate").getAsString();
			startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentStartDate = format.parse(startDateStr);
			String endDateStr = json.get("endDate").getAsString();
			endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentEndDate = format.parse(endDateStr);
			final Calendar episodeStartCal = Calendar.getInstance();
			episodeStartCal.setTime(currentStartDate);
			final Calendar episodeStopCal = Calendar.getInstance();
			episodeStopCal.setTime(currentEndDate);
			for (final Calendar lft = episodeStartCal; (lft.before(episodeStopCal) || lft.equals(episodeStopCal)); lft
					.add(Calendar.DAY_OF_YEAR, 1)) {
				final Date xx = lft.getTime();
				if (!hasEpisodes.contains(xx)) {
					hasEpisodes.add(xx);
				}
			}
		}
		final Calendar startCal = Calendar.getInstance();
		startCal.setTime(rangeStart);
		startCal.set(Calendar.HOUR_OF_DAY, 1);
		final Calendar endCal = Calendar.getInstance();
		endCal.setTime(rangeStop);
		endCal.set(Calendar.HOUR_OF_DAY, 1);
		for (final Calendar lft = startCal; (lft.before(endCal) || lft.equals(endCal)); lft.add(Calendar.DAY_OF_YEAR,
				1)) {
			final Date xx = lft.getTime();
			if (!hasEpisodes.contains(xx)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data            input JSON object with episodes
	 * @param rangeStartYear  start date of the time period that is being considered
	 * @param rangeStartMonth start date of the time period that is being considered
	 * @param rangeStopYear   end date of the time period that is being considered
	 * @param rangeStopMonth  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final float countEpisodesRatio(final JsonArray data, final String rangeStartYear,
			String rangeStartMonth, final String rangeStopYear, String rangeStopMonth) throws ParseException {
		if (data == null)
			return Float.valueOf(0);
		if (data.size() == 0)
			return Float.valueOf(0);
		if (rangeStartMonth == null)
			return Float.valueOf(0);
		if (rangeStopMonth == null)
			return Float.valueOf(0);
		if (rangeStartYear == null)
			return Float.valueOf(0);
		if (rangeStopYear == null)
			return Float.valueOf(0);
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (rangeStartMonth.length() == 1) {
			rangeStartMonth = "0".concat(rangeStartMonth);
		}
		if (rangeStopMonth.length() == 1) {
			rangeStopMonth = "0".concat(rangeStopMonth);
		}
		final String rangeStart = rangeStartYear.concat("-").concat(rangeStartMonth).concat("-")
				.concat("01T00:00:00.000Z");
		final String rangeStop = rangeStopYear.concat("-").concat(rangeStopMonth).concat("-")
				.concat("01T00:00:00.000Z");
		if (!zofar.isWellFormedTimestamp(rangeStart))
			return Float.valueOf(0);
		if (!zofar.isTimeStampParseable(rangeStart))
			return Float.valueOf(0);
		if (!zofar.isWellFormedTimestamp(rangeStop))
			return Float.valueOf(0);
		if (!zofar.isTimeStampParseable(rangeStop))
			return Float.valueOf(0);
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final Calendar tempCal = new GregorianCalendar();
		tempCal.clear();
		tempCal.setTime(rangeEndDate);
		tempCal.add(Calendar.MONTH, 1);
		tempCal.add(Calendar.DATE, -1);
		rangeEndDate = tempCal.getTime();
		return countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, false).get(0);
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 * 
	 * @param data input JSON object with episodes
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or
	 *         norangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final float countEpisodesRatio(final JsonArray data) throws ParseException {
		if (data == null)
			return 0;
		if (data.size() == 0)
			return 0;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date minStartDate = null;
		Date maxEndDate = null;
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			if (tmp.isJsonNull())
				continue;
			final JsonObject json = (JsonObject) tmp;
			if (!json.has("startDate"))
				continue;
			if (!json.has("endDate"))
				continue;
			String startDateStr = json.get("startDate").getAsString();
			startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T01:00:00.000Z";
			if (!zofar.isWellFormedTimestamp(startDateStr))
				return 0;
			if (!zofar.isTimeStampParseable(startDateStr))
				return 0;
			final Date currentStartDate = format.parse(startDateStr);
			if (minStartDate == null)
				minStartDate = currentStartDate;
			else if (currentStartDate.before(minStartDate))
				minStartDate = currentStartDate;
			String endDateStr = json.get("endDate").getAsString();
			endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T01:00:00.000Z";
			if (!zofar.isWellFormedTimestamp(endDateStr))
				return 0;
			if (!zofar.isTimeStampParseable(endDateStr))
				return 0;
			final Date currentEndDate = format.parse(endDateStr);
			if (maxEndDate == null)
				maxEndDate = currentEndDate;
			else if (currentEndDate.after(maxEndDate))
				maxEndDate = currentEndDate;
		}
		return countEpisodesRatioHelper(data, minStartDate, maxEndDate, format, false).get(0);
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data       input JSON object with episodes
	 * @param rangeStart start date of the time period that is being considered
	 * @param rangeStop  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final List<Float> countEpisodesRatioYearwise(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		final ArrayList<Float> returnList = new ArrayList<>();
		returnList.add((float) 0);
		if (data == null)
			return returnList;
		if (data.size() == 0)
			return returnList;
		if (rangeStart == null)
			return returnList;
		if (rangeStop == null)
			return returnList;
		if (rangeStart.contentEquals(""))
			return returnList;
		if (rangeStop.contentEquals(""))
			return returnList;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (!zofar.isWellFormedTimestamp(rangeStart))
			return returnList;
		if (!zofar.isTimeStampParseable(rangeStart))
			return returnList;
		if (!zofar.isWellFormedTimestamp(rangeStop))
			return returnList;
		if (!zofar.isTimeStampParseable(rangeStop))
			return returnList;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final List<Float> resultsList = countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, true);
		return resultsList;
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data            input JSON object with episodes
	 * @param rangeStartYear  start date of the time period that is being considered
	 * @param rangeStartMonth start date of the time period that is being considered
	 * @param rangeStopYear   end date of the time period that is being considered
	 * @param rangeStopMonth  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final List<Float> countEpisodesRatioYearwise(final JsonArray data, final String rangeStartYear,
			String rangeStartMonth, final String rangeStopYear, String rangeStopMonth) throws ParseException {
		final ArrayList<Float> returnList = new ArrayList<>();
		returnList.add((float) 0);
		if (data == null)
			return returnList;
		if (data.size() == 0)
			return returnList;
		if (rangeStartMonth == null)
			return returnList;
		if (rangeStopMonth == null)
			return returnList;
		if (rangeStartYear == null)
			return returnList;
		if (rangeStopYear == null)
			return returnList;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		if (rangeStartMonth.length() == 1) {
			rangeStartMonth = "0".concat(rangeStartMonth);
		}
		if (rangeStopMonth.length() == 1) {
			rangeStopMonth = "0".concat(rangeStopMonth);
		}
		final String rangeStart = rangeStartYear.concat("-").concat(rangeStartMonth).concat("-")
				.concat("01T00:00:00.000Z");
		final String rangeStop = rangeStopYear.concat("-").concat(rangeStopMonth).concat("-")
				.concat("01T00:00:00.000Z");
		if (!zofar.isWellFormedTimestamp(rangeStart))
			return returnList;
		if (!zofar.isTimeStampParseable(rangeStart))
			return returnList;
		if (!zofar.isWellFormedTimestamp(rangeStop))
			return returnList;
		if (!zofar.isTimeStampParseable(rangeStop))
			return returnList;
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final Calendar tempCal = new GregorianCalendar();
		tempCal.clear();
		tempCal.setTime(rangeEndDate);
		tempCal.add(Calendar.MONTH, 1);
		tempCal.add(Calendar.DATE, -1);
		rangeEndDate = tempCal.getTime();
		final List<Float> resultsList = countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, true);
		return resultsList;
	}
	/**
	 * [REVIEW][TODO] helper function, returns a float value representing the
	 * portion of a given time period that is filled with episode data (calculated
	 * on the level of days)
	 *
	 * @param data            input JSON object with episodes
	 * @param rangeStartInput start date of the time period that is being considered
	 * @param rangeStopInput  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	private synchronized List<Float> countEpisodesRatioHelper(final JsonArray data, final Date rangeStartInput,
			final Date rangeStopInput, final SimpleDateFormat format, final boolean yearwise) throws ParseException {
		final List<Float> returnList = new LinkedList<>();
		returnList.add((float) 0);
		if (data == null)
			return returnList;
		if (data.size() == 0)
			return returnList;
		if (rangeStartInput == null)
			return returnList;
		if (rangeStopInput == null)
			return returnList;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final Calendar timestampCal = new GregorianCalendar();
		final Calendar outputCal = new GregorianCalendar();
		timestampCal.setTime(rangeStartInput);
		outputCal.clear();
		outputCal.set(Calendar.YEAR, timestampCal.get(Calendar.YEAR));
		outputCal.set(Calendar.MONTH, timestampCal.get(Calendar.MONTH));
		outputCal.set(Calendar.DATE, timestampCal.get(Calendar.DATE));
		outputCal.set(Calendar.HOUR, 0);
		outputCal.set(Calendar.MINUTE, 0);
		outputCal.set(Calendar.SECOND, 0);
		outputCal.set(Calendar.MILLISECOND, 0);
		final Date rangeStart = outputCal.getTime();
		outputCal.clear();
		timestampCal.clear();
		timestampCal.setTime(rangeStopInput);
		outputCal.set(Calendar.YEAR, timestampCal.get(Calendar.YEAR));
		outputCal.set(Calendar.MONTH, timestampCal.get(Calendar.MONTH));
		outputCal.set(Calendar.DATE, timestampCal.get(Calendar.DATE));
		outputCal.set(Calendar.HOUR, 23);
		outputCal.set(Calendar.MINUTE, 59);
		outputCal.set(Calendar.SECOND, 59);
		outputCal.set(Calendar.MILLISECOND, 999);
		final Date rangeStop = outputCal.getTime();
		final List<Float> resultsList = new LinkedList<>();
		final Set<Date> hasEpisodes = new HashSet<Date>();
		if (yearwise) {
			final Calendar episodeStartTimeCal = Calendar.getInstance();
			episodeStartTimeCal.setTime(rangeStart);
			final int yearrangestart = episodeStartTimeCal.get(Calendar.YEAR);
			final Calendar episodeEndTimeCal = Calendar.getInstance();
			episodeEndTimeCal.setTime(rangeStop);
			final int yearrangeend = episodeEndTimeCal.get(Calendar.YEAR);
			final List<Integer> yearsList = new LinkedList<>();
			for (int i = yearrangestart; i <= yearrangeend; i++) {
				yearsList.add(i);
			}
			for (int i = 0; i < yearsList.size(); i++) {
				hasEpisodes.clear();
				final int currentYear = yearsList.get(i);
				Date currentRangeStart = new Date();
				Date currentRangeStop = new Date();
				if (i != 0) {
					final Calendar currentCalendarStart = new GregorianCalendar();
					currentCalendarStart.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0);
					currentCalendarStart.set(Calendar.MILLISECOND, 0);
					currentRangeStart = currentCalendarStart.getTime();
				} else {
					currentRangeStart = rangeStart;
				}
				if (i != yearsList.size() - 1) {
					final Calendar currentCalendarStop = new GregorianCalendar();
					currentCalendarStop.set(currentYear, Calendar.DECEMBER, 31, 0, 0, 0);
					currentCalendarStop.set(Calendar.MILLISECOND, 0);
					currentCalendarStop.add(Calendar.DATE, 1);
					currentRangeStop = currentCalendarStop.getTime();
				} else {
					currentRangeStop = rangeStop;
				}
				final Iterator<JsonElement> it = data.iterator();
				while (it.hasNext()) {
					final JsonElement tmp = it.next();
					if (!tmp.isJsonObject())
						continue;
					final JsonObject json = (JsonObject) tmp;
					if (!json.has("startDate"))
						continue;
					if (!json.has("endDate"))
						continue;
					if (json.get("startDate").isJsonNull())
						continue;
					if (json.get("endDate").isJsonNull())
						continue;
					String startDateStr = json.get("startDate").getAsString();
					startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T00:00:00.000Z";
					if (!zofar.isWellFormedTimestamp(startDateStr))
						continue;
					if (!zofar.isTimeStampParseable(startDateStr))
						continue;
					final Date currentStartDate = format.parse(startDateStr);
					String endDateStr = json.get("endDate").getAsString();
					endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T23:59:59.999Z";
					if (!zofar.isWellFormedTimestamp(endDateStr))
						continue;
					if (!zofar.isTimeStampParseable(endDateStr))
						continue;
					final Date currentEndDate = format.parse(endDateStr);
					final Calendar episodeStartCal = Calendar.getInstance();
					episodeStartCal.setTime(currentStartDate);
					final Calendar episodeStopCal = Calendar.getInstance();
					episodeStopCal.setTime(currentEndDate);
					for (final Calendar lft = episodeStartCal; (lft.before(episodeStopCal)
							|| lft.equals(episodeStopCal)); lft.add(Calendar.DAY_OF_YEAR, 1)) {
						if (lft.get(Calendar.YEAR) != currentYear) {
							continue;
						}
						final Date xx = lft.getTime();
						if (!hasEpisodes.contains(xx)) {
							if ((!(xx.before(currentRangeStart)) || xx.equals(currentRangeStart))
									&& !(xx.after(currentRangeStop))) {
								hasEpisodes.add(xx);
							}
						}
					}
				}
				final long diffInMillies = Math.abs(currentRangeStop.getTime() - currentRangeStart.getTime());
				final float diff = TimeUnit.DAYS.convert(diffInMillies + 1L, TimeUnit.MILLISECONDS);
				final float hasEpisodesFloat = hasEpisodes.size();
				final float result = (hasEpisodesFloat / diff);
				final TreeSet<Date> myTreeSet = new TreeSet<Date>();
				myTreeSet.addAll(hasEpisodes);
				resultsList.add(result);
			}
		} else {
			final Iterator<JsonElement> it = data.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (!tmp.isJsonObject())
					continue;
				final JsonObject json = (JsonObject) tmp;
				if (!json.has("startDate"))
					continue;
				if (!json.has("endDate"))
					continue;
				if (json.get("startDate").isJsonNull())
					continue;
				if (json.get("endDate").isJsonNull())
					continue;
				String startDateStr = json.get("startDate").getAsString();
				if (startDateStr.contentEquals(""))
					continue;
				startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T00:00:00.000Z";
				if (!zofar.isWellFormedTimestamp(startDateStr))
					continue;
				if (!zofar.isTimeStampParseable(startDateStr))
					continue;
				final Date currentStartDate = format.parse(startDateStr);
				String endDateStr = json.get("endDate").getAsString();
				if (endDateStr.contentEquals(""))
					continue;
				endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T23:59:59.999Z";
				if (!zofar.isWellFormedTimestamp(endDateStr))
					continue;
				if (!zofar.isTimeStampParseable(endDateStr))
					continue;
				final Date currentEndDate = format.parse(endDateStr);
				final Calendar episodeStartCal = Calendar.getInstance();
				episodeStartCal.setTime(currentStartDate);
				final Calendar episodeStopCal = Calendar.getInstance();
				episodeStopCal.setTime(currentEndDate);
				for (final Calendar lft = episodeStartCal; (lft.before(episodeStopCal)
						|| lft.equals(episodeStopCal)); lft.add(Calendar.DAY_OF_YEAR, 1)) {
					final Date xx = lft.getTime();
					if (!hasEpisodes.contains(xx)) {
						if (!(xx.before(rangeStart)) && !(xx.after(rangeStop))) {
							hasEpisodes.add(xx);
						}
					}
				}
			}
			final long diffInMillies = Math.abs(rangeStop.getTime() - rangeStart.getTime());
			final float diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1L;
			final float hasEpisodesFloat = hasEpisodes.size();
			final float result = (hasEpisodesFloat / diff);
			final TreeSet<Date> myTreeSet = new TreeSet<Date>();
			myTreeSet.descendingSet();
			myTreeSet.addAll(hasEpisodes);
			resultsList.add(result);
		}
		return resultsList;
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 * 
	 * @param data       input JSON object with episodes
	 * @param rangeStart start date of the time period that is being considered
	 * @param rangeStop  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or
	 *         norangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final float countEpisodesRatio(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		if (data == null)
			return 0;
		if (data.size() == 0)
			return 0;
		if (rangeStart == null)
			return 0;
		if (rangeStop == null)
			return 0;
		if (rangeStart.contentEquals(""))
			return 0;
		if (rangeStop.contentEquals(""))
			return 0;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (!zofar.isWellFormedTimestamp(rangeStart))
			return 0;
		if (!zofar.isTimeStampParseable(rangeStart))
			return 0;
		if (!zofar.isWellFormedTimestamp(rangeStop))
			return 0;
		if (!zofar.isTimeStampParseable(rangeStop))
			return 0;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		return countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, false).get(0);
	}
	private synchronized final boolean isSplittable(final JsonElement episode, final Date splitDate) {
		if (episode == null)
			return false;
		if (episode.isJsonNull())
			return false;
		if (!episode.isJsonObject())
			return false;
		if (splitDate == null)
			return false;
		final Calendar splitCal = new GregorianCalendar();
		splitCal.setTime(splitDate);
		splitCal.set(Calendar.DAY_OF_MONTH, 1);
		splitCal.set(Calendar.HOUR, 1);
		splitCal.set(Calendar.MINUTE, 0);
		splitCal.set(Calendar.SECOND, 0);
		splitCal.set(Calendar.MILLISECOND, 0);
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final JsonObject episodeObj = episode.getAsJsonObject();
		if (!episodeObj.has("startDate"))
			return false;
		Date startDate = null;
		try {
			startDate = stampFormat.parse(episodeObj.get("startDate").getAsString());
		} catch (final ParseException e1) {
			e1.printStackTrace();
		}
		if (startDate == null)
			return false;
		final Calendar startCal = new GregorianCalendar();
		startCal.setTime(startDate);
		if (!startCal.before(splitCal))
			return false;
		if (!episodeObj.has("endDate"))
			return false;
		Date endDate = null;
		try {
			endDate = stampFormat.parse(episodeObj.get("endDate").getAsString());
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		if (endDate == null)
			return false;
		final Calendar endCal = new GregorianCalendar();
		endCal.setTime(endDate);
		if (!splitCal.before(endCal))
			return false;
		return true;
	}
	/**
	 * method for splitting of episodes
	 *
	 * @param arr
	 * @param index
	 * @param split_type_dict
	 * @return
	 */
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict) {
		return this.splitEpisode(arr, index, split_type_dict, null, -1);
	}
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict, final int limit) {
		return this.splitEpisode(arr, index, split_type_dict, null, limit);
	}
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict, final List<String> blacklist) {
		return this.splitEpisode(arr, index, split_type_dict, blacklist, -1);
	}
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict, final List<String> blacklist, final int limit) {
		final Map<String, Object> back = new LinkedHashMap<String, Object>();
		back.put("index", -1);
		back.put("episodes", arr);
		if (arr == null) {
			back.put("episodes", new JsonArray());
			return back;
		}
		if (index < 0)
			return back;
		if (index >= arr.size())
			return back;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		JsonArray copy_arr = arr.deepCopy();
		final JsonObject episode = copy_arr.get(index).getAsJsonObject();
		episode.add("state", new JsonPrimitive("done"));
		copy_arr = addOrReplaceJson(copy_arr, episode, index);
		back.put("index", -1);
		back.put("episodes", copy_arr);
		copy_arr = removePropertyFromAllEpisodes(copy_arr, "splitStack");
		copy_arr = removePropertyFromAllEpisodes(copy_arr, "currentSplit");
		back.put("episodes", copy_arr);
		int currentCountOfType = -1;
		if (limit > -1) {
			if (hasJsonProperty(episode, "type")) {
				final Object typeObj = getJsonProperty(episode, "type");
				if ((typeObj != null) && ((String.class).isAssignableFrom(typeObj.getClass()))) {
					final String typeStr = (String) typeObj;
					final List<Map<String, String>> filter = new ArrayList<Map<String, String>>();
					final Map<String, String> typeFilter = new LinkedHashMap<String, String>();
					typeFilter.put("type", typeStr);
					filter.add(typeFilter);
					typeFilter.put("state", "done");
					filter.add(typeFilter);
					try {
						final JsonArray filtered = this.filterArray(copy_arr, filter);
						if (filtered != null) {
							final int currentSize = filtered.size();
							if (currentSize > 0)
								currentCountOfType = currentSize;
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		Object splitStackObj = null;
		final Set<String> criteriaToDelete = new HashSet<String>();
		if (hasJsonProperty(episode, "splitStack")) {
			splitStackObj = getJsonProperty(episode, "splitStack");
		}
		if ((split_type_dict != null) && ((JsonObject.class).isAssignableFrom(split_type_dict.getClass()))) {
			final JsonObject type_dict = (JsonObject) split_type_dict;
			for (final Map.Entry<String, JsonElement> typeItem : type_dict.entrySet()) {
				final String type = typeItem.getKey();
				final JsonElement criteriaElem = typeItem.getValue();
				if (!criteriaElem.isJsonObject())
					continue;
				final JsonObject criteria = criteriaElem.getAsJsonObject();
				JsonArray criteria_vars;
				String criteria_timestamp;
				if (criteria.has("SPLIT_VAR")) {
					criteria_vars = criteria.get("SPLIT_VAR").getAsJsonArray();
				} else
					continue;
				if (criteria.has("TIMESTAMP_VAR")) {
					criteria_timestamp = criteria.get("TIMESTAMP_VAR").getAsString();
				} else
					continue;
				criteriaToDelete.add(criteria_timestamp);
				boolean match = false;
				final Iterator<JsonElement> varsIt = criteria_vars.iterator();
				while (varsIt.hasNext()) {
					final JsonElement criteria_varElem = varsIt.next();
					if (!criteria_varElem.isJsonObject())
						continue;
					final JsonObject criteria_var = criteria_varElem.getAsJsonObject();
					boolean criteriaMatch = true;
					for (final Map.Entry<String, JsonElement> criteria_varItem : criteria_var.entrySet()) {
						final String propertyName = criteria_varItem.getKey();
						criteriaToDelete.add(propertyName);
						final String propertyValue = criteria_varItem.getValue().getAsString();
						if (!episode.has(propertyName))
							criteriaMatch = false;
						else {
							if (!(episode.get(propertyName).getAsString().contentEquals(propertyValue)))
								criteriaMatch = false;
						}
					}
					if (criteriaMatch)
						match = true;
				}
				if (match) {
					if (splitStackObj == null)
						splitStackObj = new JsonObject();
					final JsonObject splitStack = (JsonObject) splitStackObj;
					JsonArray types = null;
					if (!episode.has(criteria_timestamp))
						continue;
					if (!zofar.isWellFormedTimestamp(episode.get(criteria_timestamp).getAsString())) {
						final String illFormedTimestamp = episode.get(criteria_timestamp).getAsString();
						final String repairedTimestamp = illFormedTimestamp.replace("#3A", ":");
						if (zofar.isWellFormedTimestamp(repairedTimestamp)) {
							episode.addProperty(criteria_timestamp, repairedTimestamp);
						} else
							continue;
					}
					if (splitStack.has(episode.get(criteria_timestamp).getAsString()))
						types = splitStack.get(episode.get(criteria_timestamp).getAsString()).getAsJsonArray();
					if (types == null)
						types = new JsonArray();
					final JsonPrimitive typePrimitive = new JsonPrimitive(type);
					if (!types.contains(typePrimitive))
						types.add(typePrimitive);
					splitStack.add(episode.get(criteria_timestamp).getAsString(), types);
				}
			}
		}
		if (splitStackObj == null)
			return back;
		if (!(JsonObject.class).isAssignableFrom(splitStackObj.getClass()))
			return back;
		final JsonObject splitStack = ((JsonObject) splitStackObj);
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final ArrayList<String> sortedIndex = sortSplitStackIndex(splitStack, format);
		JsonElement currentSplitStack = null;
		String currentSplitDate = null;
		for (final String indexItem : sortedIndex) {
			try {
				final Date indexDate = format.parse(indexItem);
				if (this.isSplittable(episode, indexDate)) {
					currentSplitStack = splitStack.get(indexItem).getAsJsonArray();
					currentSplitDate = indexItem;
				}
			} catch (final ParseException e) {
				e.printStackTrace();
			}
			splitStack.remove(indexItem);
			if (currentSplitStack != null) {
				break;
			}
		}
		if (currentSplitStack != null) {
			final int splitYear = zofar.getYearFromStamp(currentSplitDate);
			final int splitMonth = zofar.getMonthFromStamp(currentSplitDate);
			try {
				final Map<String, Object> parentMods = new HashMap<String, Object>();
				if (this.hasFlag(episode, "eHO")) {
					final JsonArray flagArray = episode.get("flags").getAsJsonArray().deepCopy();
					flagArray.remove(new JsonPrimitive("eHO"));
					parentMods.put("flags", flagArray);
				}
				parentMods.put("splitStack", null);
				parentMods.put("currentSplit", null);
				parentMods.put("state", "done");
				for (final String criteriaVar : criteriaToDelete) {
					parentMods.put(criteriaVar, null);
				}
				final Map<String, Object> childMods = new HashMap<String, Object>();
				if (this.hasFlag(episode, "sHO")) {
					final JsonArray flagArray = episode.get("flags").getAsJsonArray().deepCopy();
					flagArray.remove(new JsonPrimitive("sHO"));
					childMods.put("flags", flagArray);
				}
				if (splitStack.size() == 0) {
					childMods.put("splitStack", null);
				} else {
					childMods.put("splitStack", splitStack);
				}
				childMods.put("currentSplit", currentSplitStack.getAsJsonArray());
				childMods.put("state", "new");
				for (final String criteriaVar : criteriaToDelete) {
					childMods.put(criteriaVar, null);
				}
				childMods.put("slot", null);
				childMods.put("color", null);
				if (episode.has("name")) {
					final List<String> names = new ArrayList<String>();
					names.add(episode.get("name").getAsString());
					if (episode.has("childEpisodes")) {
						final JsonElement childsElement = episode.get("childEpisodes");
						if ((childsElement != null) && (childsElement.isJsonArray())) {
							final JsonArray childs = episode.get("childEpisodes").getAsJsonArray();
							final Iterator<JsonElement> childIt = childs.iterator();
							while (childIt.hasNext()) {
								final JsonElement child = childIt.next();
								if (!child.isJsonObject())
									continue;
								final JsonObject childObj = child.getAsJsonObject();
								if (childObj.has("name"))
									names.add(childObj.get("name").getAsString());
							}
						}
					}
					childMods.put("name", zofar.indexedName(episode.get("name").getAsString(), names));
				}
				if (blacklist != null) {
					for (final String criteriaVar : blacklist) {
						childMods.put(criteriaVar, null);
					}
				}
				final Map<String, JsonElement> splitted = this.splitEpisode(copy_arr, episode, splitYear, splitMonth,
						childMods);
				JsonArray moddedEpisodes = ((JsonArray) splitted.get("data"));
				moddedEpisodes = editEpisode(moddedEpisodes, indexOfJson(moddedEpisodes, (splitted.get("parent"))),
						parentMods);
				back.put("index", indexOfJson(moddedEpisodes, (splitted.get("child"))));
				back.put("episodes", moddedEpisodes);
				if (currentCountOfType >= 0 && currentCountOfType >= limit) {
					moddedEpisodes = removePropertyFromAllEpisodes(moddedEpisodes, "splitStack");
					moddedEpisodes = removePropertyFromAllEpisodes(moddedEpisodes, "currentSplit");
					back.put("index", -1);
					back.put("episodes", moddedEpisodes);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param parentEpisode
	 * @param splitYear
	 * @param splitMonth
	 * @param mods
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<String, JsonElement> splitEpisode(final JsonArray data, final JsonObject parentEpisode,
			final int splitYear, final int splitMonth, final Map<String, Object> mods) throws Exception {
		if (data == null)
			return null;
		if (data.isJsonNull())
			return null;
		final Map<String, JsonElement> back = new LinkedHashMap<String, JsonElement>();
		back.put("data", data);
		if (parentEpisode == null) {
			back.put("parent", JsonNull.INSTANCE);
			back.put("child", null);
			return back;
		}
		if (parentEpisode.isJsonNull()) {
			back.put("parent", JsonNull.INSTANCE);
			back.put("child", null);
			return back;
		}
		final int parentIndex = indexOfJson(data, parentEpisode);
		final JsonObject childEpisode = new JsonObject();
		for (final String propertyName : parentEpisode.keySet()) {
			if (propertyName.contentEquals("id"))
				continue;
			if (propertyName.contentEquals("childEpisodes"))
				continue;
			if (propertyName.contentEquals("parentEpisode"))
				continue;
			if (propertyName.contentEquals("slot"))
				continue;
			if (propertyName.contentEquals("color"))
				continue;
			final JsonElement parentProperty = parentEpisode.get(propertyName);
			if (parentProperty == null)
				continue;
			if (parentProperty.isJsonNull())
				continue;
			childEpisode.add(propertyName, parentProperty);
		}
		childEpisode.add("id", new JsonPrimitive(newEpisodeUID(data)));
		childEpisode.add("state", new JsonPrimitive("new"));
		childEpisode.add("parentEpisode", parentEpisode.get("id"));
		JsonArray childList = null;
		if (parentEpisode.has("childEpisodes"))
			childList = parentEpisode.get("childEpisodes").getAsJsonArray();
		if (childList == null)
			childList = new JsonArray();
		final JsonPrimitive childId = childEpisode.get("id").getAsJsonPrimitive();
		if (!childList.contains(childId))
			childList.add(childId);
		parentEpisode.add("childEpisodes", childList);
		final Calendar cal = new GregorianCalendar();
		cal.set(splitYear, splitMonth, 1, 1, 0, 0);
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
		final String splitDateStr = stampFormat.format(cal.getTime());
		childEpisode.add("startDate", new JsonPrimitive(splitDateStr));
		cal.add(Calendar.DAY_OF_MONTH, -1);
		final String splitDateStr1 = stampFormat.format(cal.getTime());
		parentEpisode.add("endDate", new JsonPrimitive(splitDateStr1));
		JsonArray data_copy = data.deepCopy();
		data_copy.set(parentIndex, parentEpisode);
		data_copy.add(childEpisode);
		if (mods != null) {
			final int indexOfJson = indexOfJson(data_copy, childEpisode);
			data_copy = editEpisode(data_copy.deepCopy(), indexOfJson, mods);
		}
		back.put("data", data_copy);
		back.put("parent", parentEpisode);
		back.put("child", childEpisode);
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param data
	 * @param index
	 * @return
	 */
	public synchronized final JsonArray addOrReplaceJson(JsonArray arr, final JsonElement data, final int index) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		if (index < 0)
			arr.add(data);
		else if (arr.size() <= index)
			arr.add(data);
		else
			arr.set(index, data);
		return arr;
	}
	private synchronized JsonArray removePropertyFromOtherEpisodes(final JsonArray arr, final int currentIndex,
			final String property) {
		if (arr == null)
			return null;
		if (property == null)
			return arr;
		if (property.contentEquals(""))
			return arr;
		if (currentIndex >= arr.size())
			return arr;
		final JsonArray copy_arr = arr.deepCopy();
		final int startIndex = 0;
		final int size = copy_arr.size();
		for (int index = startIndex; index < size; index++) {
			if (index == currentIndex)
				continue;
			final JsonElement episode = copy_arr.get(index);
			if (!episode.isJsonObject())
				continue;
			final JsonObject episodeObj = episode.getAsJsonObject();
			if (!episodeObj.has(property))
				continue;
			final String newPropertyName = property + "_old";
			if ((JsonObject.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.add(newPropertyName, episodeObj.get(property).getAsJsonObject());
			} else if ((JsonArray.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.add(newPropertyName, episodeObj.get(property).getAsJsonArray());
			} else if ((JsonPrimitive.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.add(newPropertyName, episodeObj.get(property).getAsJsonPrimitive());
			} else if ((String.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.addProperty(newPropertyName, episodeObj.get(property).getAsString());
			}
			episodeObj.remove(property);
		}
		return copy_arr;
	}
	private synchronized JsonArray removePropertyFromAllEpisodes(final JsonArray arr, final String property) {
		return removePropertyFromOtherEpisodes(arr, -1, property);
	}
	public synchronized final boolean hasJsonProperty(final JsonElement data, final String property) {
		if (data == null)
			return false;
		if (!data.isJsonObject())
			return false;
		final JsonObject jsonObj = data.getAsJsonObject();
		if (!jsonObj.has(property))
			return false;
		final JsonElement back = jsonObj.get(property);
		if (back.isJsonNull())
			return false;
		if ((JsonPrimitive.class).isAssignableFrom(back.getClass())) {
			if (back.getAsString().contentEquals("null"))
				return false;
		}
		if ((JsonElement.class).isAssignableFrom(back.getClass())) {
			if (back.isJsonNull())
				return false;
		}
		return true;
	}
	private synchronized final ArrayList<String> sortSplitStackIndex(final JsonObject unsortedSplitStack,
			final SimpleDateFormat format) {
		if (unsortedSplitStack == null)
			return null;
		final ArrayList<Date> sortIndex = new ArrayList<Date>();
		for (final Map.Entry<String, JsonElement> property : unsortedSplitStack.entrySet()) {
			final String dateStr = property.getKey();
			Date date = null;
			try {
				date = format.parse(dateStr);
			} catch (final ParseException e) {
				e.printStackTrace();
			}
			if (date != null)
				sortIndex.add(date);
		}
		Collections.sort(sortIndex);
		final ArrayList<String> back = new ArrayList<String>();
		for (final Date date : sortIndex) {
			back.add(format.format(date));
		}
		return back;
	}
	public synchronized final boolean hasFlag(final JsonElement episode, final String flagName) {
		return hasJsonPrimitiveArrayEntry(episode, "flags", flagName);
	}
	public synchronized final boolean hasFlag(final JsonArray arr, final int index, final String flag) {
		return hasJsonPrimitiveArrayEntry(arr, index, "flags", flag);
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param arr
	 * @param element
	 * @return
	 */
	public synchronized final int indexOfJson(final JsonArray arr, final JsonElement element) {
		if (arr == null)
			return -1;
		if (element == null)
			return -1;
		JsonObject elementObj = null;
		if (element.isJsonObject()) {
			elementObj = (JsonObject) element;
		}
		final int size = arr.size();
		for (int a = 0; a < size; a++) {
			final JsonElement tmp = arr.get(a);
			boolean match = false;
			if ((elementObj != null) && (elementObj.has("id"))) {
				if (tmp.isJsonObject()) {
					final JsonObject tmpObj = (JsonObject) tmp;
					if (tmpObj.has("id")) {
						match = (tmpObj.get("id").getAsString().contentEquals((elementObj.get("id").getAsString())));
					}
				}
			}
			if (match) {
				return a;
			}
		}
		return -1;
	}
	/**
	 * Edit Operation for JS-free use of Episode Table
	 * 
	 * @param episodes     JsonArray of all episodes
	 * @param episodeIndex index of focused episode
	 * @param mods         properties to patch the existing focused episode
	 * @return the modified JsonArray (in case the focused episode doesn't exist
	 *         theoriginal JsonArray will be returned unmodified)
	 */
	public synchronized final JsonArray editEpisode(final JsonArray episodes, final int episodeIndex,
			final Map<String, Object> mods) {
		JsonElement currentEpisode = null;
		if (episodeIndex > -1) {
			currentEpisode = episodes.get(episodeIndex).getAsJsonObject();
		}
		if (currentEpisode == null)
			return episodes;
		for (final Map.Entry<String, Object> mod : mods.entrySet()) {
			currentEpisode = this.setJsonProperty(currentEpisode, mod.getKey(), mod.getValue());
		}
		return addOrReplaceJson(episodes, currentEpisode, episodeIndex);
	}
	/**
	 * Method to check if episode has split parent
	 * 
	 * @param episode Episode to analyze
	 * @return true if episode contains property parentEpisode with id of
	 *         parentinside, false else
	 */
	public synchronized boolean hasSplitParent(final JsonObject episode) {
		return (getSplitParentId(episode) > -1);
	}
	/**
	 * Method to extract id of split parent from episode
	 * 
	 * @param episode Episode to analyze
	 * @return id of parent episode if episode contains property parentEpisode
	 *         withid of parent inside, empty list else
	 */
	public synchronized Integer getSplitParentId(final JsonObject episode) {
		if (episode == null)
			return -1;
		if (!episode.isJsonObject())
			return -1;
		final JsonObject episodeObj = episode.getAsJsonObject();
		if (!episodeObj.has("parentEpisode"))
			return -1;
		final JsonElement parentEpisode = episodeObj.get("parentEpisode");
		if (parentEpisode.isJsonPrimitive()) {
			try {
				return parentEpisode.getAsJsonPrimitive().getAsInt();
			} catch (final NumberFormatException e) {
				LOGGER.info("[getSplitParentId] ({}) {}", parentEpisode.getAsJsonPrimitive(),
						"parent id cannot parsed to int");
			}
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public synchronized String maxEpisodeID(final JsonArray data) throws Exception {
		if (data == null)
			return "-1";
		if (data.isJsonNull())
			return "-1";
		if (data.size() == 0)
			return "-1";
		int maxVal = -1;
		for (int index = 0; index < data.size(); index++) {
			final JsonElement tmp = data.get(index);
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (!episode.has("id"))
				continue;
			final int episodeId = episode.get("id").getAsInt();
			maxVal = Math.max(maxVal, episodeId);
		}
		return maxVal + "";
	}
	protected synchronized String newEpisodeUID(final JsonArray data) throws Exception {
		if (data == null)
			return "-1";
		if (data.isJsonNull())
			return "-1";
		if (data.size() == 0)
			return "-1";
		String uuid = UUID.randomUUID().toString();
		for (int index = 0; index < data.size(); index++) {
			final JsonElement tmp = data.get(index);
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (!episode.has("id"))
				continue;
			final String episodeId = episode.get("id").getAsString();
			if (episodeId.contentEquals(uuid)) {
				uuid = UUID.randomUUID().toString();
				index = 0;
			}
		}
		return uuid;
	}
	public synchronized boolean isSplittable(final JsonArray arr, final int index) {
		if (arr == null)
			return false;
		if (arr.size() == 0)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		final JsonObject episode = arr.get(index).getAsJsonObject();
		if (!episode.has("startDate")) {
			return false;
		}
		if (!episode.has("endDate")) {
			return false;
		}
		final String startDate = episode.get("startDate").getAsString();
		final String endDate = episode.get("endDate").getAsString();
		final String startYear = startDate.substring(0, 4);
		final String startMonth = startDate.substring(5, 7);
		final String endYear = endDate.substring(0, 4);
		final String endMonth = endDate.substring(5, 7);
		return !startYear.equals(endYear) || !startMonth.equals(endMonth);
	}
	/**
	 * Checks whether the year and month part of the "endDate" property of a give
	 * episode (a json element) is identical to the given corresponding strings
	 * calEndYear and calEndMonth.
	 * 
	 * @param episode     episode object
	 * @param calEndYear  the year to check the "endDate" timestamp for
	 * @param calEndMonth the month to check the "endDate" timestamp for
	 * @return "true" if the years and months are identical, "false" if not.
	 */
	public synchronized boolean isRecentEpisode(final JsonElement episode, final String calEndYear,
			String calEndMonth) {
		if (episode == null)
			return false;
		if (calEndMonth == null)
			return false;
		if (calEndYear == null)
			return false;
		try {
			Integer.parseInt(calEndMonth);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
		if (Integer.parseInt(calEndMonth) < 1 || Integer.parseInt(calEndMonth) > 12)
			return false;
		if (calEndMonth.length() == 1) {
			calEndMonth = "0".concat(calEndMonth);
		}
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (!episode.isJsonObject())
			return false;
		final JsonObject episodeObj = episode.getAsJsonObject();
		if (!episodeObj.has("endDate"))
			return false;
		final JsonElement propertyValue = episodeObj.get("endDate");
		if (!propertyValue.isJsonPrimitive())
			return false;
		final String timestamp = propertyValue.getAsString();
		if (zofar.isWellFormedTimestamp(timestamp)) {
			final String endYear = timestamp.substring(0, 4);
			final String endMonth = timestamp.substring(5, 7);
			return (endYear.equals(calEndYear) && endMonth.equals(calEndMonth));
		}
		return false;
	}
	public synchronized boolean hasRecentEpisode(final JsonArray arr, final String calEndYear, String calEndMonth) {
		if (arr == null)
			return false;
		if (calEndMonth == null)
			return false;
		if (calEndYear == null)
			return false;
		if (Integer.parseInt(calEndMonth) < 1 || Integer.parseInt(calEndMonth) > 12)
			return false;
		if (calEndMonth.length() == 1) {
			calEndMonth = "0".concat(calEndMonth);
		}
		for (final JsonElement episode : arr) {
			if (isRecentEpisode(episode, calEndYear, calEndMonth))
				return true;
		}
		return false;
	}
	public synchronized JsonArray sortedLikeNextEpisodeIndex(final JsonArray arr) {
		if (arr == null)
			return null;
		if (arr.size() == 0)
			return arr;
		final ArrayList<JsonObject> array = new ArrayList<JsonObject>();
		for (int i = 0; i < arr.size(); i++) {
			array.add(arr.get(i).getAsJsonObject());
		}
		try {
			Collections.sort(array, new Comparator<JsonObject>() {
				@Override
				public int compare(final JsonObject lhs, final JsonObject rhs) {
					if (lhs.get("startDate").getAsString().compareTo(rhs.get("startDate").getAsString()) == 0) {
						if (lhs.get("endDate").getAsString().compareTo(rhs.get("endDate").getAsString()) == 0) {
							return lhs.get("id").getAsString().compareTo(rhs.get("id").getAsString());
						} else
							return lhs.get("endDate").getAsString().compareTo(rhs.get("endDate").getAsString());
					} else
						return lhs.get("startDate").getAsString().compareTo(rhs.get("startDate").getAsString());
				}
			});
		} catch (final Exception e) {
			e.printStackTrace();
			return arr;
		}
		final JsonArray jsonArray = new JsonArray();
		for (final JsonObject episode : array) {
			jsonArray.add(episode);
		}
		return jsonArray;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param stopIndex
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized int beforeEpisodeIndex(final JsonArray data, final int stopIndex,
			final Map<String, String> filter) throws Exception {
		if (data == null)
			return -1;
		if (data.size() == 0)
			return -1;
		for (int index = Math.min(data.size() - 1, stopIndex); index >= 0; index--) {
			final JsonElement tmp = data.get(index);
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			boolean match = true;
			for (final Map.Entry<String, String> filterItem : filter.entrySet()) {
				if (!episode.has(filterItem.getKey())) {
					match = false;
					continue;
				}
				final JsonElement propertyElement = episode.get(filterItem.getKey());
				if (!(propertyElement.isJsonPrimitive() || propertyElement.isJsonArray())) {
					match = false;
					continue;
				}
				if (propertyElement.isJsonPrimitive()) {
					if (!propertyElement.getAsString().contentEquals(filterItem.getValue())) {
						match = false;
						continue;
					}
				}
				if (propertyElement.isJsonArray()) {
					final JsonPrimitive filterObj = new JsonPrimitive(filterItem.getValue());
					if (!propertyElement.getAsJsonArray().contains(filterObj)) {
						match = false;
						continue;
					}
				}
			}
			if (match)
				return index;
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param stopIndex
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized int beforeEpisodeIndex(final JsonArray data, final int stopIndex, final String property,
			final String value) throws Exception {
		if (data == null)
			return -1;
		if (data.size() == 0)
			return -1;
		if (property == null)
			return -1;
		if (value == null)
			return -1;
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		return beforeEpisodeIndex(data, stopIndex, filter);
	}
	/**
	 * returns the index of the next json element that matches the criteria defined
	 * in filterList; tries to find the episode with latest endDate; if more than
	 * one episode with the same latest endDate are found, the index of the episode
	 * with the latest startDate is returned; if there is more than one episode with
	 * the same latest endDate and latest startDate, the episode with the highest id
	 * is returned.
	 *
	 * @param arr        JsonArray with episodes
	 * @param startIndex start index where we start to look for episodes
	 * @param filterList
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndexReversed(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList) {
		if (arr == null)
			return -1;
		if (arr.size() == 0)
			return -1;
		if (arr.size() <= startIndex)
			return -1;
		if (filterList == null)
			return -1;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final int count = arr.size();
		final Map<JsonObject, Integer> indexMap = new LinkedHashMap<JsonObject, Integer>();
		final Map<Date, List<JsonObject>> startMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Date, List<JsonObject>> endMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<String, List<JsonObject>> idMap = new LinkedHashMap<String, List<JsonObject>>();
		for (int index = Math.max(0, startIndex); index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			indexMap.put(episode, index);
			if (episode.has("startDate") && episode.get("startDate").isJsonPrimitive()) {
				final String dateStr = episode.get("startDate").getAsJsonPrimitive().getAsString();
				if (zofar.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (final Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (startMap.containsKey(date))
							tmp = startMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						startMap.put(date, tmp);
					}
				}
			}
			if (episode.has("endDate") && episode.get("endDate").isJsonPrimitive()) {
				final String dateStr = episode.get("endDate").getAsJsonPrimitive().getAsString();
				if (zofar.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (final Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (endMap.containsKey(date))
							tmp = endMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						endMap.put(date, tmp);
					}
				}
			}
			if (episode.has("id") && episode.get("id").isJsonPrimitive()) {
				String idStr = null;
				if (episode.has("id") && episode.get("id").isJsonPrimitive())
					idStr = episode.get("id").getAsJsonPrimitive().getAsString();
				if (idStr != null) {
					List<JsonObject> tmp = null;
					if (idMap.containsKey(idStr))
						tmp = idMap.get(idStr);
					if (tmp == null)
						tmp = new ArrayList<JsonObject>();
					if (!tmp.contains(episode))
						tmp.add(episode);
					idMap.put(idStr, tmp);
				}
			}
		}
		final Map<Date, List<JsonObject>> sortedStartMap = new TreeMap<Date, List<JsonObject>>(startMap)
				.descendingMap();
		final List<JsonObject> results = new ArrayList<JsonObject>();
		for (final Map.Entry<Date, List<JsonObject>> x : sortedStartMap.entrySet()) {
			final List<JsonObject> episodes = x.getValue();
			for (final JsonObject episode : episodes) {
				if (!episode.isJsonObject())
					continue;
				if (!jsonObjectMatchesFilter(episode, filterList))
					continue;
				results.add(episode);
			}
		}
		if (results.size() == 1) {
			return indexMap.get(results.get(0));
		} else if (results.size() > 1) {
			final Map<Date, List<JsonObject>> sortedEndMap = new TreeMap<Date, List<JsonObject>>(endMap)
					.descendingMap();
			final List<JsonObject> results1 = new ArrayList<JsonObject>();
			for (final Map.Entry<Date, List<JsonObject>> x : sortedEndMap.entrySet()) {
				final List<JsonObject> episodes1 = x.getValue();
				for (final JsonObject episode : episodes1) {
					if (results.contains(episode)) {
						results1.add(episode);
					}
				}
				if (!results1.isEmpty())
					break;
			}
			if (results1.size() == 1) {
				return indexMap.get(results1.get(0));
			} else if (results1.size() > 1) {
				final Map<String, List<JsonObject>> sortedIdMap = new TreeMap<String, List<JsonObject>>(idMap)
						.descendingMap();
				final List<JsonObject> results2 = new ArrayList<JsonObject>();
				for (final Map.Entry<String, List<JsonObject>> x : sortedIdMap.entrySet()) {
					final List<JsonObject> episodes1 = x.getValue();
					for (final JsonObject episode : episodes1) {
						if (results1.contains(episode)) {
							results2.add(episode);
						}
					}
					if (!results2.isEmpty())
						break;
				}
				if (results2.size() == 1) {
					return indexMap.get(results2.get(0));
				} else if (results2.size() > 1) {
					System.err.println("there are multiple episodes width the same id : " + results2);
				}
			}
		}
		return -1;
	}
	/**
	 * return the index of the next json element that matches the criteria defined
	 * in filterList
	 *
	 * @param arr        JsonArray with episodes
	 * @param filterList
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndex(final JsonArray arr, final List<Map<String, String>> filterList) {
		return nextEpisodeIndex(arr, 0, filterList);
	}
	/**
	 * return the index of the next json element that matches the criteria defined
	 * in filterList
	 *
	 * @param arr        JsonArray with episodes
	 * @param filterList
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndexReversed(final JsonArray arr, final List<Map<String, String>> filterList) {
		return nextEpisodeIndexReversed(arr, 0, filterList);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param startIndex
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndex(final JsonArray arr, final int startIndex, final String property,
			final String value) throws Exception {
		if (arr == null)
			return -1;
		if (arr.size() == 0)
			return -1;
		if (arr.size() <= startIndex)
			return -1;
		if (property == null)
			return -1;
		if (value == null)
			return -1;
		final List<Map<String, String>> filterList = new ArrayList<>();
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		filterList.add(filter);
		return nextEpisodeIndex(arr, startIndex, filterList);
	}
	/**
	 * returns the index of the next json element that matches the criteria defined
	 * in filterList; tries to find the episode with earliest startDate; if more
	 * than one episode with the same lowest startDate is found, the index of the
	 * episode with the lowest endDate is returned; if there is more than one
	 * episode with the same lowest startDate and lowest endDate, the episode with
	 * the lowest id is returned.
	 *
	 * @param arr        JsonArray with episodes
	 * @param startIndex start index where we start to look for episodes
	 * @param filterList
	 * @return
	 * 
	 */
	public synchronized int nextEpisodeIndex(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList) {
		if (arr == null)
			return -1;
		if (arr.size() == 0)
			return -1;
		if (arr.size() <= startIndex)
			return -1;
		if (filterList == null)
			return -1;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final int count = arr.size();
		final Map<JsonObject, Integer> indexMap = new LinkedHashMap<JsonObject, Integer>();
		final Map<Date, List<JsonObject>> startMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Date, List<JsonObject>> endMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<String, List<JsonObject>> idMap = new LinkedHashMap<String, List<JsonObject>>();
		for (int index = Math.max(0, startIndex); index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			indexMap.put(episode, index);
			if (episode.has("startDate") && episode.get("startDate").isJsonPrimitive()) {
				final String dateStr = episode.get("startDate").getAsJsonPrimitive().getAsString();
				if (zofar.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (final Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (startMap.containsKey(date))
							tmp = startMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						startMap.put(date, tmp);
					}
				}
			}
			if (episode.has("endDate") && episode.get("endDate").isJsonPrimitive()) {
				final String dateStr = episode.get("endDate").getAsJsonPrimitive().getAsString();
				if (zofar.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (final Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (endMap.containsKey(date))
							tmp = endMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						endMap.put(date, tmp);
					}
				}
			}
			if (episode.has("id") && episode.get("id").isJsonPrimitive()) {
				String idStr = null;
				if (episode.has("id") && episode.get("id").isJsonPrimitive())
					idStr = episode.get("id").getAsJsonPrimitive().getAsString();
				if (idStr != null) {
					List<JsonObject> tmp = null;
					if (idMap.containsKey(idStr))
						tmp = idMap.get(idStr);
					if (tmp == null)
						tmp = new ArrayList<JsonObject>();
					if (!tmp.contains(episode))
						tmp.add(episode);
					idMap.put(idStr, tmp);
				}
			}
		}
		final Map<Date, List<JsonObject>> sortedEndMap = new TreeMap<Date, List<JsonObject>>(endMap);
		final List<JsonObject> results = new ArrayList<JsonObject>();
		for (final Map.Entry<Date, List<JsonObject>> x : sortedEndMap.entrySet()) {
			final List<JsonObject> episodes = x.getValue();
			for (final JsonObject episode : episodes) {
				if (!episode.isJsonObject())
					continue;
				if (!jsonObjectMatchesFilter(episode, filterList))
					continue;
				results.add(episode);
			}
		}
		if (results.size() == 1) {
			return indexMap.get(results.get(0));
		} else if (results.size() > 1) {
			final Map<Date, List<JsonObject>> sortedStartMap = new TreeMap<Date, List<JsonObject>>(startMap);
			final List<JsonObject> results1 = new ArrayList<JsonObject>();
			for (final Map.Entry<Date, List<JsonObject>> x : sortedStartMap.entrySet()) {
				final List<JsonObject> episodes1 = x.getValue();
				for (final JsonObject episode : episodes1) {
					if (results.contains(episode)) {
						results1.add(episode);
					}
				}
			}
			if (results1.size() == 1) {
				return indexMap.get(results1.get(0));
			} else if (results1.size() > 1) {
				final Map<String, List<JsonObject>> sortedIdMap = new TreeMap<String, List<JsonObject>>(idMap);
				final List<JsonObject> results2 = new ArrayList<JsonObject>();
				for (final Map.Entry<String, List<JsonObject>> x : sortedIdMap.entrySet()) {
					final List<JsonObject> episodes1 = x.getValue();
					for (final JsonObject episode : episodes1) {
						if (results1.contains(episode)) {
							results2.add(episode);
						}
					}
					if (!results2.isEmpty())
						break;
				}
				if (results2.size() == 1) {
					return indexMap.get(results2.get(0));
				} else if (results2.size() > 1) {
					System.err.println("there are multiple episodes with the same id : " + results2);
				}
			}
		}
		return -1;
	}
	/**
	 * removes empty json objects "{}" from the json array; e.g.: [{}] -> [];
	 * [{"k":"v"},{},{"u":"w"}] -> [{"k":"v"},{"u":"w"}]
	 * 
	 * @param arr a json array
	 * @return the json array with all empty json objects {} removed from the array
	 */
	public JsonArray removeEmptyJsonElementsFromArray(final JsonArray arr) {
		final JsonArray json_array = new JsonArray();
		for (final JsonElement episode : arr) {
			if (episode.getAsJsonObject().size() > 0)
				json_array.add(episode.deepCopy());
		}
		return json_array;
	}
	public synchronized final JsonArray setCurrentSplitType(final JsonArray arr, final int index,
			final String currentSplitType) {
		return setJsonPrimitiveArrayEntry(arr, index, "currentSplit", currentSplitType);
	}
	public synchronized final JsonObject setCurrentSplitType(final JsonElement episode, final String currentSplitType) {
		return setJsonPrimitiveArrayEntry(episode, "currentSplit", currentSplitType);
	}
	private synchronized final JsonArray setJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		if (arr == null)
			return new JsonArray();
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		final JsonArray back = arr.deepCopy();
		final JsonObject tmp = setJsonPrimitiveArrayEntry(back.get(index), jsonPropertyArrayName, arrayEntryName);
		if (tmp != null)
			back.set(index, tmp);
		return back;
	}
	private synchronized final JsonObject setJsonPrimitiveArrayEntry(final JsonElement episode,
			String jsonPropertyArrayName, String arrayEntryName) {
		if (episode == null)
			return null;
		final JsonObject back = ((JsonObject) episode).deepCopy();
		if (!episode.isJsonObject())
			return back;
		if (jsonPropertyArrayName == null)
			return back;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return back;
		if (arrayEntryName == null)
			return back;
		arrayEntryName = arrayEntryName.trim();
		if (arrayEntryName.contentEquals(""))
			return back;
		final JsonArray new_array = new JsonArray();
		new_array.add(arrayEntryName);
		if (back.has(jsonPropertyArrayName)) {
			final JsonElement tmp = back.get(jsonPropertyArrayName);
			if (tmp == null) {
				back.add("flags", new_array);
				return back;
			}
			if (!tmp.isJsonArray()) {
				back.add("flags", new_array);
				return back;
			}
			final JsonArray jsonPropertyArray = tmp.getAsJsonArray();
			if (jsonPropertyArray.size() == 0) {
				back.add("flags", new_array);
				return back;
			}
			final JsonPrimitive arrayEntryNameObj = new JsonPrimitive(arrayEntryName);
			if (!jsonPropertyArray.contains(arrayEntryNameObj))
				jsonPropertyArray.add(arrayEntryNameObj);
			back.add(jsonPropertyArrayName, jsonPropertyArray);
		} else {
			back.add("flags", new_array);
		}
		return back;
	}
	public synchronized final JsonArray setFlag(final JsonArray arr, final int index, final String flag) {
		return setJsonPrimitiveArrayEntry(arr, index, "flags", flag);
	}
	public synchronized final JsonObject setFlag(final JsonElement episode, final String flag) {
		return setJsonPrimitiveArrayEntry(episode, "flags", flag);
	}
	public synchronized final JsonObject deleteCurrentSplitType(final JsonElement episode,
			final List<Object> currentSplitTypeList) {
		return deleteJsonPrimitiveArrayEntry(episode, "currentSplit", currentSplitTypeList);
	}
	public synchronized final JsonArray deleteCurrentSplitType(final JsonArray arr, final int index,
			final List<Object> currentSplitTypeList) {
		return deleteJsonPrimitiveArrayEntry(arr, index, "currentSplit", currentSplitTypeList);
	}
	public synchronized final JsonObject deleteCurrentSplitType(final JsonElement episode,
			final String currentSplitType) {
		return deleteJsonPrimitiveArrayEntry(episode, "currentSplit", currentSplitType);
	}
	public synchronized final JsonArray deleteCurrentSplitType(final JsonArray arr, final int index,
			final String currentSplitType) {
		return deleteJsonPrimitiveArrayEntry(arr, index, "currentSplit", currentSplitType);
	}
	private synchronized final JsonArray deleteJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final List<Object> arrayEntryNamesList) {
		if (arr == null)
			return null;
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		final JsonArray back = arr.deepCopy();
		final JsonObject tmp = deleteJsonPrimitiveArrayEntry(back.get(index), jsonPropertyArrayName,
				arrayEntryNamesList);
		if (tmp != null)
			back.set(index, tmp);
		return back;
	}
	private synchronized final JsonArray deleteJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		if (arr == null)
			return new JsonArray();
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		final JsonArray back = arr.deepCopy();
		final JsonObject tmp = deleteJsonPrimitiveArrayEntry(back.get(index), jsonPropertyArrayName, arrayEntryName);
		if (tmp != null)
			back.set(index, tmp);
		return back;
	}
	private synchronized final JsonObject deleteJsonPrimitiveArrayEntry(final JsonElement episode,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		if (arrayEntryName == null)
			return null;
		if (arrayEntryName.contentEquals(""))
			return null;
		final List<Object> arrayEntryNames = new ArrayList<Object>();
		arrayEntryNames.add(arrayEntryName);
		return this.deleteJsonPrimitiveArrayEntry(episode, jsonPropertyArrayName, arrayEntryNames);
	}
	private synchronized final JsonObject deleteJsonPrimitiveArrayEntry(final JsonElement episode,
			String jsonPropertyArrayName, final List<Object> listOfArrayEntryNames) {
		if (episode == null)
			return null;
		if (!episode.isJsonObject())
			return null;
		if (jsonPropertyArrayName == null)
			return null;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return null;
		boolean listContainsAtLeastOneName = false;
		for (final Object arrayEntryNameObj : listOfArrayEntryNames) {
			String arrayEntryName = arrayEntryNameObj.toString();
			if (arrayEntryName != null) {
				arrayEntryName = arrayEntryName.trim();
				if (!arrayEntryName.contentEquals(""))
					listContainsAtLeastOneName = true;
			}
		}
		if (listOfArrayEntryNames.isEmpty())
			return (JsonObject) episode;
		if (!listContainsAtLeastOneName)
			return (JsonObject) episode;
		final JsonObject back = ((JsonObject) episode).deepCopy();
		if (back.has(jsonPropertyArrayName)) {
			final JsonElement tmp = back.get(jsonPropertyArrayName);
			if (tmp == null)
				return null;
			if (!tmp.isJsonArray())
				return null;
			final JsonArray jsonPropertyArray = tmp.getAsJsonArray();
			if (jsonPropertyArray.size() == 0)
				return null;
			for (final Object arrayEntryNameStrObj : listOfArrayEntryNames) {
				String arrayEntryName = (String) arrayEntryNameStrObj;
				arrayEntryName = arrayEntryName.trim();
				final JsonPrimitive arrayEntryNameObj = new JsonPrimitive(arrayEntryName);
				if (jsonPropertyArray.contains(arrayEntryNameObj))
					jsonPropertyArray.remove(arrayEntryNameObj);
				back.add(jsonPropertyArrayName, jsonPropertyArray);
			}
		}
		return back;
	}
	public synchronized final JsonArray deleteFlag(final JsonArray arr, final int index, final String flag) {
		return deleteJsonPrimitiveArrayEntry(arr, index, "flags", flag);
	}
	public synchronized final JsonObject deleteFlag(final JsonElement episode, final String flag) {
		return deleteJsonPrimitiveArrayEntry(episode, "flags", flag);
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonElement episode, String jsonPropertyArrayName,
			String arrayEntryName) {
		if (episode == null)
			return false;
		if (!episode.isJsonObject())
			return false;
		if (jsonPropertyArrayName == null)
			return false;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return false;
		if (arrayEntryName == null)
			return false;
		arrayEntryName = arrayEntryName.trim();
		if (arrayEntryName.contentEquals(""))
			return false;
		final JsonObject episodeObject = (JsonObject) episode;
		if (episodeObject.has(jsonPropertyArrayName)) {
			final JsonElement property = episodeObject.get(jsonPropertyArrayName);
			if (property.isJsonArray()) {
				final JsonArray tmp = property.getAsJsonArray();
				final JsonPrimitive splitTypeObj = new JsonPrimitive(arrayEntryName);
				return (tmp.contains(splitTypeObj));
			}
		}
		return false;
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final List<Object> arrayEntryNamesList) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		return hasJsonPrimitiveArrayEntry(arr.get(index), jsonPropertyArrayName, arrayEntryNamesList);
	}
	/**
	 * returns "true" if ANY of the given strings in the list can be found in the of
	 * the episode with the key=jsonPropertyArrayName. returns "false" if NONE of
	 * the given strings in the list can be found.
	 * 
	 * @param episode               a json object (an episode)
	 * @param listOfArrayEntryNames the key/name of the json property where to
	 *                              lookin
	 * @return true if ANY of the given strings in the list can be found in thegiven
	 *         property; false if NONE can be found.
	 */
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonElement episode, String jsonPropertyArrayName,
			final List<Object> listOfArrayEntryNames) {
		if (episode == null)
			return false;
		if (!episode.isJsonObject())
			return false;
		if (jsonPropertyArrayName == null)
			return false;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return false;
		boolean listContainsAtLeastOneName = false;
		for (final Object arrayEntryNameObj : listOfArrayEntryNames) {
			String arrayEntryName = arrayEntryNameObj.toString();
			if (arrayEntryName != null) {
				arrayEntryName = arrayEntryName.trim();
				if (!arrayEntryName.contentEquals(""))
					listContainsAtLeastOneName = true;
				break;
			}
		}
		if (listContainsAtLeastOneName) {
			for (final Object arrayEntryNameObj : listOfArrayEntryNames) {
				String arrayEntryName = arrayEntryNameObj.toString();
				if (arrayEntryName == null)
					continue;
				arrayEntryName = arrayEntryName.trim();
				if (arrayEntryName.contentEquals(""))
					continue;
				final JsonObject episodeObject = (JsonObject) episode;
				if (episodeObject.has(jsonPropertyArrayName)) {
					final JsonElement property = episodeObject.get(jsonPropertyArrayName);
					if (property.isJsonArray()) {
						final JsonArray tmp = property.getAsJsonArray();
						final JsonPrimitive splitTypeObj = new JsonPrimitive(arrayEntryName);
						if (tmp.contains(splitTypeObj))
							return true;
					}
				}
			}
		}
		return false;
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		return hasJsonPrimitiveArrayEntry(arr.get(index), jsonPropertyArrayName, arrayEntryName);
	}
	public synchronized final boolean hasCurrentSplitType(final JsonArray arr, final int index,
			final String splitType) {
		return hasJsonPrimitiveArrayEntry(arr, index, "currentSplit", splitType);
	}
	/**
	 * returns "true" if ANY of the given strings in the list can be found in the
	 * "currentSplit"-array of the episode. returns "false" if NONE of the given
	 * strings in the list can be found.
	 * 
	 * @param episode       a json object (an episode)
	 * @param splitTypeList a list of split type name strings
	 * @return true if ANY of the given strings in the list can be found
	 *         in"currentSplit"; false if NONE can be found.
	 */
	public synchronized final boolean hasCurrentSplitType(final JsonElement episode, final List<Object> splitTypeList) {
		return hasJsonPrimitiveArrayEntry(episode, "currentSplit", splitTypeList);
	}
	/**
	 * returns "true" if ANY of the given strings in the list can be found in the
	 * "currentSplit"-array of the episode at the given json array index. returns
	 * "false" if NONE of the given strings in the list can be found.
	 * 
	 * @param arr           a json array with episode json objects
	 * @param index         the index of the episode in question
	 * @param splitTypeList a list of split type name strings
	 * @return true if ANY of the given strings in the list can be found
	 *         in"currentSplit"; false if NONE can be found.
	 */
	public synchronized final boolean hasCurrentSplitType(final JsonArray arr, final int index,
			final List<Object> splitTypeList) {
		return hasJsonPrimitiveArrayEntry(arr, index, "currentSplit", splitTypeList);
	}
	public synchronized final boolean hasCurrentSplitType(final JsonElement episode, final String splitType) {
		return hasJsonPrimitiveArrayEntry(episode, "currentSplit", splitType);
	}
	/**
	 * checks whether a split should occur, given the whole json array, an
	 * episode_index and a list of split types. returns "false" if the current
	 * episode has no property "currentSplit", if the list of split types is empty
	 * or if at least one of the given split types is still present within the
	 * "currentSplit" array (is used to check whether a split type later in the
	 * order of split types is still present in "currentSplit" and yet to be
	 * processed)
	 * 
	 * @param arr           a json array
	 * @param index         the current episode_index
	 * @param splitTypeList a list of split type strings
	 * @return "false" if the current episode has no property "currentSplit", if
	 *         thelist of split types is empty or if at least one of the given split
	 *         types is still present within the "currentSplit" array; "true" iff
	 *         the current episode has "currentSplit" and none of the split types
	 *         given in splitTypeList can be found within the "currentSplit" array.
	 */
	public synchronized final boolean doSplitOnEndPageCandidate(final JsonArray arr, final int index,
			final List<Object> splitTypeList) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		final JsonObject episode = (JsonObject) arr.get(index);
		if (!episode.has("currentSplit"))
			return false;
		if (splitTypeList.size() == 0)
			return true;
		return !hasCurrentSplitType(arr, index, splitTypeList);
	}
	public synchronized final boolean hasEpisodeDetail(final JsonElement typeDetails, final String property) {
		if (typeDetails == null)
			return false;
		if (!typeDetails.isJsonArray())
			return false;
		final JsonArray details = typeDetails.getAsJsonArray();
		final Iterator<JsonElement> it = details.iterator();
		while (it.hasNext()) {
			final JsonElement detail = it.next();
			if (!detail.isJsonObject())
				continue;
			final JsonObject jsonDetail = detail.getAsJsonObject();
			if (jsonDetail.has("property") && (jsonDetail.get("property").getAsString().contentEquals(property)))
				return true;
		}
		return false;
	}
	public synchronized final List<JsonElement> asEpisodeList(final JsonArray episodes) {
		if (episodes == null)
			return null;
		final List<JsonElement> back = new ArrayList<JsonElement>();
		if (episodes.isJsonNull())
			return back;
		final Iterator<JsonElement> it = episodes.iterator();
		while (it.hasNext())
			back.add(it.next());
		return back;
	}
	/**
	 * Delete Operation for JS-free use of Episode Table
	 * 
	 * @param episodes     JsonArray of all episodes
	 * @param episodeIndex index of focused episode
	 * @return the modified JsonArray (in case the focused episode doesn't exist
	 *         theoriginal JsonArray will be returned unmodified)
	 */
	public synchronized final JsonArray deleteEpisode(final JsonArray episodes, final int episodeIndex) {
		final JsonArray updatedEpisodes = this.delJson(episodes, episodeIndex);
		return updatedEpisodes;
	}
	public synchronized JsonObject episodesState(final JsonArray episodes) {
		final JsonObject status = new JsonObject();
		if (episodes == null)
			return status;
		if (episodes.isJsonNull())
			return status;
		status.add("count", new JsonPrimitive(episodes.size()));
		return status;
	}
	private JsonArray removeEpisodeFromArrayViaId(final JsonArray arr, final JsonElement episode) {
		final JsonArray copyJsonArr = arr.deepCopy();
		if (!episode.getAsJsonObject().has("id"))
			return arr;
		final String idRefEpisodeStr = episode.getAsJsonObject().get("id").getAsString();
		final JsonArray newJsonArray = new JsonArray();
		for (final JsonElement episodeElement : copyJsonArr) {
			final JsonObject episodeObject = episodeElement.getAsJsonObject();
			if (episodeObject.has("id") && !episodeObject.get("id").isJsonNull()) {
				final String idEpisodeStr = episodeObject.get("id").getAsString();
				if (!idRefEpisodeStr.equals(idEpisodeStr)) {
					newJsonArray.add(episodeElement);
				}
			}
		}
		return newJsonArray;
	}
	public boolean overlapEpisodeWithArray(final JsonArray filteredArr, final JsonArray inputArr,
			final int inputIndex) {
		if (filteredArr == null)
			return false;
		if (inputArr == null)
			return false;
		if ((filteredArr.isJsonNull()) || (inputArr.isJsonNull())
				|| (!(inputIndex >= 0) && (inputIndex <= inputArr.size() - 1))) {
			return false;
		}
		return overlapEpisodeWithArray(filteredArr, inputArr.get(inputIndex));
	}
	/**
	 * [REVIEWED CM] Uses a deep copy of the given json array and also of the given
	 * reference episode. Checks whether a given referenceEpisode overlaps with any
	 * of the (other) episodes from the given json array. If the reference episode
	 * exhibits a missing or non-well-formed startDate or endDate timestamp, the
	 * return value will be "false". If all of the episodes from the json array
	 * exhibit either a missing or non-well-formed startDate or endDate timestamp,
	 * the return value will be "false". The reference epsiode must have an "id"
	 * property. Using this property, any episode within the json array that shows
	 * the same "id" value (String comparison) will be removed from the array prior
	 * to the overlap-checking.
	 * 
	 * @param arr              a json array that may or may not contain
	 *                         thereferenceEpisode (identified via "id" property)
	 * @param referenceEpisode the reference episode that shall be tested if
	 *                         itoverlaps with the json array
	 * @return true if the reference episode overlaps; false if it does not
	 */
	public boolean overlapEpisodeWithArray(final JsonArray arr, final JsonElement referenceEpisode) {
		if (arr == null)
			return false;
		if (referenceEpisode == null)
			return false;
		if (!referenceEpisode.isJsonObject())
			return false;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final JsonObject episodeObject = referenceEpisode.deepCopy().getAsJsonObject();
		if (!(episodeObject.has("id"))) {
			return false;
		}
		final JsonArray cleanedJsonArray = removeEpisodeFromArrayViaId(arr.deepCopy(), referenceEpisode);
		if (!episodeObject.has("startDate") || !episodeObject.has("endDate")) {
			return false;
		}
		if (episodeObject.get("startDate").isJsonNull() || episodeObject.get("endDate").isJsonNull()) {
			return false;
		}
		final String refEpisodeStartDate = episodeObject.get("startDate").getAsString();
		final String refEpisodeEndDate = episodeObject.get("endDate").getAsString();
		if (!zofar.isWellFormedTimestamp(refEpisodeStartDate) || !zofar.isWellFormedTimestamp(refEpisodeEndDate)) {
			return false;
		}
		if (zofar.isMissingDateValue(refEpisodeStartDate) || zofar.isMissingDateValue(refEpisodeEndDate)) {
			return false;
		}
		for (final JsonElement currentEpisodeElement : cleanedJsonArray) {
			final String currentEpisodeStartDate = (String) getJsonProperty(currentEpisodeElement, "startDate");
			final String currentEpisodeEndDate = (String) getJsonProperty(currentEpisodeElement, "endDate");
			if ((zofar.isWellFormedTimestamp(currentEpisodeStartDate)
					|| zofar.isWellFormedTimestamp(currentEpisodeEndDate))
					&& (!zofar.isMissingDateValue(currentEpisodeStartDate)
							&& !zofar.isMissingDateValue(currentEpisodeEndDate))
					&& (zofar.timestampWithinTimestamps(currentEpisodeStartDate, currentEpisodeEndDate,
							refEpisodeStartDate)
							|| zofar.timestampWithinTimestamps(currentEpisodeStartDate, currentEpisodeEndDate,
									refEpisodeEndDate)
							|| zofar.timestampWithinTimestamps(refEpisodeStartDate, refEpisodeEndDate,
									currentEpisodeStartDate)
							|| zofar.timestampWithinTimestamps(refEpisodeStartDate, refEpisodeEndDate,
									currentEpisodeEndDate))) {
				return true;
			}
		}
		return false;
	}
	public synchronized final String decideSplitTarget(final JsonElement episode, final JsonObject split_type_dict,
			final JsonArray split_type_order, final String fallbackTarget) {
		if (!hasJsonProperty(episode, "currentSplit"))
			return fallbackTarget;
		final Object currentSplit = getJsonProperty(episode, "currentSplit");
		if (currentSplit == null)
			return fallbackTarget;
		if ((JsonArray.class).isAssignableFrom(currentSplit.getClass())) {
			final JsonArray currentSplitArray = (JsonArray) currentSplit;
			final Iterator<JsonElement> it = split_type_order.iterator();
			while (it.hasNext()) {
				final JsonElement type = it.next();
				if (!currentSplitArray.contains(type))
					continue;
				if (!hasJsonProperty(split_type_dict, type.getAsString()))
					continue;
				final JsonElement typedSplitInfo = split_type_dict.get(type.getAsString());
				if (typedSplitInfo == null)
					continue;
				if (!typedSplitInfo.isJsonObject())
					continue;
				final JsonObject typedSplitInfoObj = typedSplitInfo.getAsJsonObject();
				if (!hasJsonProperty(typedSplitInfoObj, "START_PAGE"))
					continue;
				return typedSplitInfo.getAsJsonObject().get("START_PAGE").getAsString();
			}
		}
		return fallbackTarget;
	}
	public synchronized JsonElement mergeEpisodes1(final JsonElement episodes1, final JsonElement episodes2,
			final String key) throws Exception {
		final Map<String, JsonObject> backMap = new HashMap<String, JsonObject>();
		if (episodes1.isJsonArray()) {
			final JsonArray jsons = (JsonArray) episodes1;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					final JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (!backMap.containsKey(id))
						backMap.put(id, tmpJSON);
					else {
						final JsonObject loadedJSON = backMap.get(id);
						for (final Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey()))
								loadedJSON.add(property.getKey(), property.getValue());
						}
					}
				}
			}
		}
		final Set<String> skipIDs = new HashSet<String>();
		if (episodes2.isJsonArray()) {
			final JsonArray jsons = (JsonArray) episodes2;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					final JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (backMap.containsKey(id)) {
						final JsonObject loadedJSON = backMap.get(id);
						boolean typeChanged = false;
						String loadedType = null;
						if (loadedJSON.has("type") && (loadedJSON.get("type").isJsonPrimitive()))
							loadedType = ((JsonPrimitive) loadedJSON.get("type")).getAsString();
						String tmpType = null;
						if (tmpJSON.has("type") && (tmpJSON.get("type").isJsonPrimitive()))
							tmpType = ((JsonPrimitive) tmpJSON.get("type")).getAsString();
						if ((loadedType != null) && (tmpType != null)) {
							typeChanged = (!(loadedType.contentEquals(tmpType)));
						}
						if (typeChanged) {
							skipIDs.add(id);
							if (tmpJSON.has("parentEpisode") && (tmpJSON.get("parentEpisode").isJsonPrimitive())) {
								final String parentId = ((JsonPrimitive) tmpJSON.get("parentEpisode")).getAsString();
								if (parentId != null)
									skipIDs.add(parentId);
							}
							if (tmpJSON.has("childEpisodes")) {
								final JsonElement childsElement = tmpJSON.get("childEpisodes");
								if ((childsElement != null) && (childsElement.isJsonArray())) {
									final JsonArray childs = tmpJSON.get("childEpisodes").getAsJsonArray();
									final Iterator<JsonElement> childIt = childs.iterator();
									while (childIt.hasNext()) {
										final JsonElement child = childIt.next();
										if (!child.isJsonPrimitive())
											continue;
										final String childId = ((JsonPrimitive) child).getAsString();
										skipIDs.add(childId);
									}
								}
							}
						}
					}
				}
			}
		}
		if (episodes2.isJsonArray()) {
			final JsonArray jsons = (JsonArray) episodes2;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					final JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (skipIDs.contains(id))
						continue;
					if (!backMap.containsKey(id)) {
					} else {
						final JsonObject loadedJSON = backMap.get(id);
						for (final Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey())) {
								loadedJSON.add(property.getKey(), property.getValue());
							}
						}
					}
				}
			}
		}
		final JsonArray back = new JsonArray();
		for (final Map.Entry<String, JsonObject> item : backMap.entrySet()) {
			back.add(item.getValue());
		}
		return back;
	}
	/**
	 * [REVIEW][TODO][EXAMPLE] see also:
	 * {@link #addEpisode(com.google.gson.JsonArray, int, int, int, int, java.util.Map)
	 *
	 * @param data
	 * @param startYear
	 * @param startMonth
	 * @param endYear
	 * @param endMonth
	 * @return
	 * @throws Exception
	 */
	/**
	 * Method to add an new Episode to a given JsonArray without map of attribute to
	 * be set in new episode
	 *
	 * @link #addEpisode(final JsonArray data, final int startYear, final int
	 *       startMonth, final int endYear, final int endMonth, final Map < String,
	 *       String > mods)
	 */
	public synchronized JsonObject addEpisode(final JsonArray data, final int startYear, final int startMonth,
			final int endYear, final int endMonth) throws Exception {
		return addEpisode(data, startYear, startMonth, endYear, endMonth, null);
	}
	/**
	 * [REVIEW] Method to add an new Episode to a given JsonArray
	 *
	 * @param data       the given JsonArray
	 * @param startYear  start year of new episode
	 * @param startMonth start month of new episode
	 * @param endYear    end year of new episode
	 * @param endMonth   end month of new episode
	 * @param mods       map of attribute to be set in new episode
	 * @return updated JsonArray incl. new episode, or null in case of data is null
	 *         or has type JsonNull [TODO] (cf 040222) hä? [/TODO]
	 * @ @throws Exception propagates common Exceptions like NullPointer,
	 *           NumberFormat, ...
	 */
	public synchronized JsonObject addEpisode(final JsonArray data, final int startYear, final int startMonth,
			final int endYear, final int endMonth, final Map<String, String> mods) throws Exception {
		if (data == null)
			return null;
		if (data.isJsonNull())
			return null;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final JsonObject newEpisode = new JsonObject();
		newEpisode.add("id", new JsonPrimitive(newEpisodeUID(data)));
		newEpisode.add("state", new JsonPrimitive("new"));
		if (mods != null) {
			for (final Map.Entry<String, String> mod : mods.entrySet()) {
				newEpisode.add(mod.getKey(), new JsonPrimitive(mod.getValue()));
			}
		}
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final Calendar cal = new GregorianCalendar();
		cal.set(startYear, startMonth, 1, 0, 0, 0);
		newEpisode.add("startDate", new JsonPrimitive(stampFormat.format(cal.getTime())));
		cal.set(endYear, endMonth, zofar.lastDayOfMonth(endMonth, endYear), 23, 0, 0);
		newEpisode.add("endDate",
				new JsonPrimitive(zofar.formatDate(zofar.lastDayOfMonth(endMonth, endYear), endMonth, endYear)));
		return newEpisode;
	}
	// https://github.com/dzhw/slc_abs21/issues/40#issuecomment-1287039651
	public synchronized String displayEpisodesStuInt(final JsonArray arr, final String language,
			final int resultsIndex) {
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<>();
		filter.put("type", "stu");
		filter.put("state", "done");
		filter.put("stu021", "ao2");
		filterList.add(filter);
		final Map<String, String> filter2 = new HashMap<>();
		filter2.put("type", "int");
		filter2.put("state", "done");
		filter2.put("int009", "ao2");
		filterList.add(filter2);
		final ArrayList<String> additionalProperties = new ArrayList<String>();
		additionalProperties.add("stu025");
		additionalProperties.add("stu024");
		additionalProperties.add("int013");
		additionalProperties.add("int012");
		return displayEpisodesIndex(arr, 0, filterList, additionalProperties, language, resultsIndex);
	}
	public synchronized String displayEpisodesStu(final JsonArray arr, final String language) {
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<>();
		filter.put("type", "stu");
		filter.put("state", "done");
		filter.put("stu021", "ao2");
		filterList.add(filter);
		final ArrayList<String> additionalProperties = new ArrayList<String>();
		additionalProperties.add("stu025");
		additionalProperties.add("stu024");
		return displayEpisodes(arr, 0, filterList, additionalProperties, language);
	}
	// https://github.com/dzhw/slc_abs21/issues/40#issuecomment-1287039651
	public synchronized String displayEpisodesInt(final JsonArray arr, final String language) {
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<>();
		filter.put("type", "int");
		filter.put("state", "done");
		filter.put("int009", "ao2");
		filterList.add(filter);
		final ArrayList<String> additionalProperties = new ArrayList<String>();
		additionalProperties.add("int013");
		additionalProperties.add("int012");
		return displayEpisodes(arr, 0, filterList, additionalProperties, language);
	}
	/**
	 * @param arr                  Episodes
	 * @param startIndex           Index of Episode to start with
	 * @param property             single filter property
	 * @param value                Value of single filter property
	 * @param additionalProperties Properties to display additional to type
	 *                             startMonth, startYear, endMond and end Year
	 * @return String (additionals, type <br>
	 *         startMonth.startYear<br>
	 *         endMonth.endYear)
	 */
	public synchronized String displayEpisodes(final JsonArray arr, final int startIndex, final String property,
			final String value, final ArrayList<String> additionalProperties, final String language) {
		if (arr == null)
			return "";
		if (arr.size() == 0)
			return "";
		if (arr.size() <= startIndex)
			return "";
		if (property == null)
			return "";
		if (value == null)
			return "";
		final List<Map<String, String>> filterList = new ArrayList<>();
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		filterList.add(filter);
		return displayEpisodes(arr, startIndex, filterList, additionalProperties, language);
	}
	/**
	 * @param arr                  Episodes
	 * @param startIndex           Index of Episode to start with
	 * @param filterList           Map of filter Criteria (AND connected)
	 * @param additionalProperties Properties to display additional to type
	 *                             startMonth, startYear, endMond and end Year
	 * @return String (additionals, type <br>
	 *         startMonth.startYear<br>
	 *         endMonth.endYear)
	 */
	public synchronized String displayEpisodes(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList, final ArrayList<String> additionalProperties,
			final String language) {
		if (arr == null)
			return "";
		if (arr.size() == 0)
			return "";
		if (arr.size() <= startIndex)
			return "";
		if (filterList == null)
			return "";
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final int count = arr.size();
		final Map<JsonObject, Integer> indexMap = new LinkedHashMap<JsonObject, Integer>();
		final Map<Date, List<JsonObject>> startMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Date, List<JsonObject>> endMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<String, List<JsonObject>> idMap = new LinkedHashMap<String, List<JsonObject>>();
		for (int index = Math.max(0, startIndex); index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			indexMap.put(episode, index);
			if (episode.has("startDate") && episode.get("startDate").isJsonPrimitive()) {
				final String dateStr = episode.get("startDate").getAsJsonPrimitive().getAsString();
				if (zofar.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (final Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (startMap.containsKey(date))
							tmp = startMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						startMap.put(date, tmp);
					}
				}
			}
			if (episode.has("endDate") && episode.get("endDate").isJsonPrimitive()) {
				final String dateStr = episode.get("endDate").getAsJsonPrimitive().getAsString();
				if (zofar.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (final Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (endMap.containsKey(date))
							tmp = endMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						endMap.put(date, tmp);
					}
				}
			}
			if (episode.has("id") && episode.get("id").isJsonPrimitive()) {
				final String idStr = episode.get("id").getAsJsonPrimitive().getAsString();
				if (idStr != null) {
					List<JsonObject> tmp = null;
					if (idMap.containsKey(idStr))
						tmp = idMap.get(idStr);
					if (tmp == null)
						tmp = new ArrayList<JsonObject>();
					if (!tmp.contains(episode))
						tmp.add(episode);
					idMap.put(idStr, tmp);
				}
			}
		}
		final Map<Date, List<JsonObject>> sortedEndMap = new TreeMap<Date, List<JsonObject>>(endMap);
		final List<JsonObject> results = new ArrayList<JsonObject>();
		for (final Map.Entry<Date, List<JsonObject>> x : sortedEndMap.entrySet()) {
			final List<JsonObject> episodes = x.getValue();
			for (final JsonObject episode : episodes) {
				if (!episode.isJsonObject())
					continue;
				if (!jsonObjectMatchesFilter(episode, filterList))
					continue;
				results.add(episode);
			}
		}
		if (results.size() == 1) {
			return displayEpisodesHelper(results.get(0), additionalProperties, language);
		} else if (results.size() > 1) {
			final Map<Date, List<JsonObject>> sortedStartMap = new TreeMap<Date, List<JsonObject>>(startMap);
			final List<JsonObject> results1 = new ArrayList<JsonObject>();
			for (final Map.Entry<Date, List<JsonObject>> x : sortedStartMap.entrySet()) {
				final List<JsonObject> episodes1 = x.getValue();
				for (final JsonObject episode : episodes1) {
					if (results.contains(episode)) {
						results1.add(episode);
					}
				}
			}
			if (results1.size() == 1) {
				return displayEpisodesHelper(results1.get(0), additionalProperties, language);
			} else if (results1.size() > 1) {
				final Map<String, List<JsonObject>> sortedIdMap = new TreeMap<String, List<JsonObject>>(idMap);
				final List<JsonObject> results2 = new ArrayList<JsonObject>();
				for (final Map.Entry<String, List<JsonObject>> x : sortedIdMap.entrySet()) {
					final List<JsonObject> episodes1 = x.getValue();
					for (final JsonObject episode : episodes1) {
						if (results1.contains(episode)) {
							results2.add(episode);
						}
					}
				}
				if (results2.size() == 1) {
					return displayEpisodesHelper(results2.get(0), additionalProperties, language);
				} else if (results2.size() > 1) {
					final StringBuffer back = new StringBuffer();
					boolean first = true;
					for (final JsonObject episode : results2) {
						if (!first)
							back.append("\n<br>");
						back.append(displayEpisodesHelper(episode, additionalProperties, language));
						first = false;
					}
					return back.toString();
				}
			}
		}
		return "";
	}
	/**
	 * @param arr                  Episodes
	 * @param startIndex           Index of Episode to start with
	 * @param filterList           Map of filter Criteria (AND connected)
	 * @param additionalProperties Properties to display additional to type
	 *                             startMonth, startYear, endMond and end Year
	 * @return String (additionals, type <br>
	 *         startMonth.startYear<br>
	 *         endMonth.endYear)
	 */
	public synchronized String displayEpisodesIndex(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList, final ArrayList<String> additionalProperties,
			final String language, final int resultsIndex) {
		if (arr == null)
			return "";
		if (arr.size() == 0)
			return "";
		if (arr.size() <= startIndex)
			return "";
		if (filterList == null)
			return "";
		final JsonArray filteredArray = this.filterArray(arr, filterList);
		final JsonArray sortedArray = this.sortedLikeNextEpisodeIndex(filteredArray);
		if (resultsIndex < sortedArray.size()) {
			return displayEpisodesHelper(sortedArray.get(resultsIndex).getAsJsonObject(), additionalProperties,
					language);
		}
		return "";
	}
	private synchronized String displayEpisodesHelper(final JsonObject episode,
			final ArrayList<String> additionalProperties, final String language) {
		final JsonParser jsonParser = new JsonParser();
		final JsonObject config = (JsonObject) jsonParser.parse(
				"{'translation':{'de':{'shortmonth':{'1':'Jan', '2':'Feb', '3':'Mär', '4':'Apr', '5':'Mai', '6':'Jun', '7':'Jul', '8':'Aug', '9':'Sep', '10':'Okt', '11':'Nov', '12':'Dez'}, 'month':{'1':'Januar', '2':'Februar', '3':'März', '4':'April','5':'Mai', '6':'Juni', '7':'Juli', '8':'August', '9':'September',  '10':'Oktober', '11':'November', '12':'Dezember'}}, 'en':{'shortmonth':{'1':'Jan', '2':'Feb', '3':'Mar', '4':'Apr', '5':'May', '6':'Jun', '7':'Jul', '8':'Aug', '9':'Sept', '10':'Oct', '11':'Nov', '12':'Dec'}, 'month':{'1':'January', '2':'February', '3':'March', '4':'April','5':'May', '6':'June', '7':'July', '8':'August', '9':'September', '10':'October', '11':'November', '12':'December'}}}, 'actions':{'update':'episodeUpdateDispatcher','edit':'episodeEdit','delete':'delEpisode'}, 'details':{'SlotDefault':[{'property':'name','label': {'de': 'Bezeichnung', 'en': 'title'}}],'sco':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'sco008','label':{'de':'Schulart','en':'Type of school'}},{'property':'sco014','label':{'de':'Ort','en':'City'}},{'property':'sco015','label':{'de':'PLZ','en':'zip code'}},{'property':'sco015','label':{'de':'Land','en':'Country'}},{'property':'sco016','label':{'de':'Ort','en':'City'}}],'stu':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'stu008','label':{'de':'Studienabschluss','en':'Degree'}},{'property':'stu013','label':{'de':'Studiengang','en':'Study program'}},{'property':'stu014','label':{'de':'Hauptfach 1','en':'Main subject 1'}},{'property':'stu015','label':{'de':'Hauptfach 2','en':'Main subject 2'}},{'property':'stu020','label':{'de':'Hochschule','en':'Higher education institution'}},{'property':'stu021','label':{'de':'Ort','en':'City'}},{'property':'stu024','label':{'de':'Land','en':'Country'}},{'property':'stu025','label':{'de':'Ort','en':'City'}}],'voc':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'voc008','label':{'de':'Art der Ausbildung/Umschulung','en':'Type of vocational training'}},{'property':'voc009','label':{'de':'Ausbildungsberuf','en':'Training occupation'}},{'property':'voc014','label':{'de':'Ort','en':'City'}},{'property':'voc015','label':{'de':'PLZ','en':'zip code'}},{'property':'voc015','label':{'de':'Land','en':'Country'}},{'property':'voc016','label':{'de':'Ort','en':'City'}}],'int':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'int008','label':{'de':'Art des Praktikums','en':'Type of internship'}},{'property':'int010','label':{'de':'Ort','en':'City'}},{'property':'int011','label':{'de':'PLZ','en':'zip code'}},{'property':'int012','label':{'de':'Land','en':'Country'}},{'property':'int013','label':{'de':'Ort','en':'City'}}],'fed':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'fed008','label':{'de':'Art der Fort-/Weiterbildung','en':'Type of further education'}}],'job':[{'property':'name','label': {'de': 'Name', 'en': 'name'}}],'emp':[{'property':'name','label': {'de': 'Arbeitgeber', 'en': 'employer'}},{'property':'emp009','label':{'de':'Beruf ','en':'Occupation'}},{'property':'emp014','label':{'de':'Berufliche Stellung','en':'Occupational status'}},{'property':'emp015','label':{'de':'im Detail','en':'in detail'}},{'property':'emp020','label':{'de':'Ort','en':'City'}},{'property':'emp021','label':{'de':'PLZ','en':'zip code'}},{'property':'emp022','label':{'de':'Land','en':'Country'}},{'property':'emp023','label':{'de':'Ort','en':'City'}},{'property':'emp027','label':{'de':'Vertragsart','en':'Type of contract'}},{'property':'emp031','label':{'de':'Arbeitsverhältnis','en':'Employment relationship'}},{'property':'emp036','label':{'de':'Arbeitsumfang','en':'Work scope'}}],'sem':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'sem016','label':{'de':'Beruf ','en':'Occupation'}},{'property':'sem019','label':{'de':'Ort','en':'City'}},{'property':'sem020','label':{'de':'PLZ','en':'zip code'}},{'property':'sem021','label':{'de':'Land','en':'Country'}},{'property':'sem022','label':{'de':'Ort','en':'City'}}],'doc':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'doc011','label':{'de':'Promotionsfach','en':'Doctoral subject'}},{'property':'doc016','label':{'de':'Hochschule','en':'Higher education institution'}},{'property':'doc018','label':{'de':'Ort','en':'City'}},{'property':'doc020','label':{'de':'Land','en':'Country'}},{'property':'doc021','label':{'de':'Ort','en':'City'}}],'mpl':[{'property':'name','label': {'de': 'Name', 'en': 'name'}}],'fam':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'car008','label':{'de':'Häusliche Pflege','en':'Informal care'}}],'uem':[{'property':'name','label': {'de': 'Name', 'en': 'name'}}],'oth':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'oth008','label':{'de':'Art der sonstigen Tätigkeit','en':'Type of other activity'}},{'property':'oth011','label':{'de':'Ort','en':'City'}},{'property':'oth012','label':{'de':'PLZ','en':'zip code'}},{'property':'oth013','label':{'de':'Land','en':'Country'}},{'property':'oth014','label':{'de':'Ort','en':'City'}}]},'type':{'label': {'de':'Art','en':'Type'}, 'values' : [{'label': {'de': 'Bitte auswählen','en': 'please select'},'id': 'SlotDefault','value': 'SlotDefault','color': '#4b4b4b'},{'label': {'de': 'Schule (Abitur/Fachabitur)','en': 'School (higher education entrance qualification)'},'id': 'sco','value': 'Slot1','color': '#f69f26'},{'label': {'de': 'Studium','en': 'Study'},'id': 'stu','value': 'Slot2','color': '#0069b2'},{'label': {'de': 'Berufsausbildung, Umschulung, Volontariat','en': 'Vocational training, occupational retraining, traineeship (&#8220;Volontariat&#8221;)'},'id': 'voc','value': 'Slot3','color': '#097d95'},{'label': {'de': 'Praktikum','en': 'Internship'},'id': 'int','value': 'Slot7','color': '#CCE1F0'},{'label': {'de': 'Fort-, Weiterbildung (längerfristig, insgesamt mind. 70h)','en': 'Further education (long-term, all together at least 70h)'},'id': 'fed','value': 'Slot4','color': '#FCD8A7'},{'label': {'de': 'Jobben','en': 'Jobbing'},'id': 'job','value': 'Slot5','color': '#F9F391'},{'label': {'de': 'nichtselbständige Erwerbstätigkeit (auch Vorbereitungsdienste wie Referendariat o.Ä.)','en': 'Employment (also preparatory services such as traineeship or similar)'},'id': 'emp','value': 'Slot6','color': '#7ab52a'},{'label': {'de': 'Selbständigkeit (auch Honorar- und Werksverträge)','en': 'Self-employment'},'id': 'sem','value': 'Slot13','color': '#CFC60D'},{'label': {'de': 'Promotion','en': 'Doctorate'},'id': 'doc','value': 'Slot8','color': '#b5163f'},{'label': {'de': 'Elternzeit/Mutterschutz','en': 'Parental/maternity leave'},'id': 'mpl','value': 'Slot9','color': '#D16021'},{'label': {'de': 'Familientätigkeit, häusliche Pflege, Hausmann/-frau','en': 'Family work, care of relatives or acquaintances, househusband/-wife'},'id': 'fam','value': 'Slot10','color': '#79a0c0'},{'label': {'de': 'Arbeitslosigkeit','en': 'Unemployment'},'id': 'uem','value': 'Slot11','color': '#DCE9C4'},{'label': {'de': 'Sonstiges (z.B. Urlaub/Reisen, Krankheit, Studienvorbereitung, Freiwilligendienst)','en': 'Other activity (e.g. vacation/travel, disease, preparation for studies, voluntary service)'},'id': 'oth','value': 'Slot12','color': '#053E49'}], 'description': {'voc':{'de':'Nicht gemeint sind Weiterbildungen (z.B. Facharzt/-ärztin) oder Aufstiegsfortbildungen.','en': 'This does not include further training (e.g. medical specialist) or advanced further training.'},'emp':{'de':'Legen Sie für jede*n Arbeitgeber*in einen eigenen Zeitraum an. Tragen Sie den Namen des/der Arbeitgebers/Arbeitgeberin ein oder ein für Sie verständliches Synonym.','en': 'Please create a separate period for each employer. Enter the name of the employer or a synonym that you understand.'},'oth':{'de':'Geben Sie bitte für die folgenden Tätigkeiten getrennte Zeiträume an: Freiwilligendienst, AuPair, Work &amp; Travel, längere Krankheit, Urlaub, Sonstiges','en': 'Please indicate separate periods for the following activities: Volunteer service, AuPair, Work &amp; Travel, longer illness, vacation, other.'}}}, 'name':{'label':{'de':'','en':'' }, 'values':'Name','description':{'SlotDefault': {'de': 'Bezeichnung', 'en': 'title'},'sco': {'de': 'Name', 'en': 'name'},'stu': {'de': 'Name', 'en': 'name'},'voc': {'de': 'Name', 'en': 'name'},'int': {'de': 'Name', 'en': 'name'},'fed': {'de': 'Name', 'en': 'name'},'job': {'de': 'Name', 'en': 'name'},'emp': {'de': 'Arbeitgeber', 'en': 'employer'},'sem': {'de': 'Name', 'en': 'name'},'doc': {'de': 'Name', 'en': 'name'},'mpl': {'de': 'Name', 'en': 'name'},'fam': {'de': 'Name', 'en': 'name'},'uem': {'de': 'Name', 'en': 'name'},'oth': {'de': 'Name', 'en': 'name'}}}}");
		return this.displayEpisodesHelper(episode, additionalProperties, language, config);
	}
	private synchronized String displayEpisodesHelper(final JsonObject episode,
			final ArrayList<String> additionalProperties, final String language, final JsonObject config) {
		if (episode == null)
			return "";
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final String type = labelOfType(config.get("type").getAsJsonObject().get("values").getAsJsonArray(),
				episode.get("type").getAsString(), language);
		final String startMonth = config.get("translation").getAsJsonObject().get(language).getAsJsonObject()
				.get("shortmonth").getAsJsonObject()
				.get((zofar.getMonthFromJson(episode.get("startDate").getAsString()) + 1) + "").getAsString();
		final int startYear = zofar.getYearFromJson(episode.get("startDate").getAsString());
		final String endMonth = config.get("translation").getAsJsonObject().get(language).getAsJsonObject()
				.get("shortmonth").getAsJsonObject()
				.get((zofar.getMonthFromJson(episode.get("endDate").getAsString()) + 1) + "").getAsString();
		final int endYear = zofar.getYearFromJson(episode.get("endDate").getAsString());
		final Map<String, String> details = new LinkedHashMap<String, String>();
		for (final String detailVar : additionalProperties) {
			if (hasJsonProperty(episode, detailVar)) {
				details.put(detailVar, unQuoteCharJson(zofar.labelOf(detailVar, episode.get(detailVar).getAsString())));
			}
		}
		final StringBuffer back = new StringBuffer();
		for (final Map.Entry<String, String> detailVar : details.entrySet()) {
			if (back.length() > 0)
				back.append(", ");
			back.append(zofar.unescapeSpecialChars(detailVar.getValue()));
		}
		if (back.length() > 0)
			back.append(", ");
		back.append(zofar.unescapeSpecialChars(type) + ", ");
		if (startYear > 1970 && startYear < 4000)
			back.append(startMonth + ". " + startYear);
		else {
			if (language.equals("de"))
				back.append("unbekanntes Startdatum");
			else if (language.equals("en"))
				back.append("unknown start date");
		}
		back.append(" - ");
		if (endYear > 1970 && endYear < 4000)
			back.append(endMonth + ". " + endYear);
		else {
			if (language.equals("de"))
				back.append("unbekanntes Enddatum");
			else if (language.equals("en"))
				back.append("unknown end date");
		}
		return back.toString();
	}
	public synchronized JsonArray sortedLikeNextEpisodeIndex(final JsonArray arr, final List<Object> types) {
		if (arr == null)
			return null;
		if (arr.size() == 0)
			return arr;
		if (types == null)
			return sortedLikeNextEpisodeIndex(arr);
		if (types.size() == 0)
			return sortedLikeNextEpisodeIndex(arr);
		final int count = arr.size();
		final Map<String, JsonArray> typeMap = new LinkedHashMap<String, JsonArray>();
		for (int index = 0; index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			if (episode.has("type") && episode.get("type").isJsonPrimitive()) {
				final String typeStr = episode.get("type").getAsJsonPrimitive().getAsString();
				if (typeStr != null) {
					JsonArray tmp = null;
					if (typeMap.containsKey(typeStr))
						tmp = typeMap.get(typeStr);
					if (tmp == null)
						tmp = new JsonArray();
					if (!tmp.contains(episode))
						tmp.add(episode);
					typeMap.put(typeStr, tmp);
				}
			}
		}
		final JsonArray back = new JsonArray();
		if ((types != null) && (!types.isEmpty())) {
			for (final Object type : types) {
				final String typeStr = (String) type;
				final JsonArray episodes = typeMap.get(typeStr);
				if (episodes == null)
					continue;
				final JsonArray sorted = sortedLikeNextEpisodeIndex(episodes);
				if (sorted == null)
					continue;
				back.addAll(sorted);
			}
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	public synchronized final JsonArray asJson(final List<AbstractAnswerBean> vars) throws Exception {
		return str2jsonArr(defrac(vars));
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public synchronized final JsonElement asJson(final List<AbstractAnswerBean> vars, final int index)
			throws Exception {
		return getJson(asJson(vars), index);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized final JsonElement createJson() {
		return new JsonArray();
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized final JsonElement createJsonArray() {
		return new JsonArray();
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param objectName
	 * @param obj
	 * @param properties
	 * @param notQuoted
	 */
	public synchronized final void setJsonProperties(final String objectName, final JsonElement obj,
			final Map<String, Object> properties, final Set<String> notQuoted) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		for (final Map.Entry<String, Object> property : properties.entrySet()) {
			zofar.assign(objectName, setJsonProperty(obj, property.getKey(), property.getValue(), notQuoted));
		}
	}
	/**
	 *
	 * Redirector for JS-free operations like edit and delete on Episodes Before
	 * reaching target Page the episode_index will be set to the selected episode
	 *
	 *
	 * @param variable   Containing Episode Array
	 * @param episodeId  ID of the selected episode, that shall be edited or deleted
	 * @param targetExpr Jump Target
	 * @param action     Action message used for triggering processAction in
	 *                   SessionController
	 * @return target page name (without leading / or suffixes like .html or .xhtml)
	 */
	public synchronized final String actionEpisode(final IAnswerBean variable, final String episodeId,
			final String targetExpr, final String action) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final FacesContext context = FacesContext.getCurrentInstance();
		final SessionController sessionController = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{sessionController}", SessionController.class);
		final NavigatorBean navigatorBean = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{navigatorBean}", NavigatorBean.class);
		zofar.persistandreload(sessionController, navigatorBean);
		final String target = JsfUtility.getInstance().evaluateValueExpression(context, "#{" + targetExpr + "}",
				String.class);
		final JsonArray episodes = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{zofar.str2jsonArr('" + variable.getStringValue() + "')}", JsonArray.class);
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<String, String>();
		filter.put("id", episodeId);
		filterList.add(filter);
		int episodeIndex = -1;
		try {
			episodeIndex = nextEpisodeIndex(episodes, -1, filterList);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (episodeIndex > -1) {
			final IAnswerBean episodeIndexVar = JsfUtility.getInstance().evaluateValueExpression(context,
					"#{episode_index}", IAnswerBean.class);
			zofar.setVariableValue(episodeIndexVar, episodeIndex + "");
		}
		final HtmlCommandButton sourceBt = new HtmlCommandButton();
		sourceBt.setId("sendBt");
		final ExpressionFactory factory = context.getApplication().getExpressionFactory();
		@SuppressWarnings("rawtypes")
		final Class[] classList = new Class[1];
		classList[0] = ActionEvent.class;
		final MethodExpression actionCall = factory.createMethodExpression(context.getELContext(), action, null,
				classList);
		sourceBt.setActionExpression(actionCall);
		final ActionEvent actionEvent = new ActionEvent(sourceBt);
		sessionController.processAction(actionEvent);
		return "/" + target + ".xhtml";
	}
	public synchronized final String actionEpisode(final String variableValue, final String episodeId,
			final String targetExpr, final String action) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final FacesContext context = FacesContext.getCurrentInstance();
		final SessionController sessionController = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{sessionController}", SessionController.class);
		final NavigatorBean navigatorBean = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{navigatorBean}", NavigatorBean.class);
		zofar.persistandreload(sessionController, navigatorBean);
		final String target = JsfUtility.getInstance().evaluateValueExpression(context, "#{" + targetExpr + "}",
				String.class);
		final JsonArray episodes = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{zofar.str2jsonArr('" + variableValue + "')}", JsonArray.class);
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<String, String>();
		filter.put("id", episodeId);
		filterList.add(filter);
		int episodeIndex = -1;
		try {
			episodeIndex = nextEpisodeIndex(episodes, -1, filterList);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (episodeIndex > -1) {
			final IAnswerBean episodeIndexVar = JsfUtility.getInstance().evaluateValueExpression(context,
					"#{episode_index}", IAnswerBean.class);
			zofar.setVariableValue(episodeIndexVar, episodeIndex + "");
		}
		final HtmlCommandButton sourceBt = new HtmlCommandButton();
		sourceBt.setId("sendBt");
		final ExpressionFactory factory = context.getApplication().getExpressionFactory();
		@SuppressWarnings("rawtypes")
		final Class[] classList = new Class[1];
		classList[0] = ActionEvent.class;
		final MethodExpression actionCall = factory.createMethodExpression(context.getELContext(), action, null,
				classList);
		sourceBt.setActionExpression(actionCall);
		final ActionEvent actionEvent = new ActionEvent(sourceBt);
		sessionController.processAction(actionEvent);
		return "/" + target + ".xhtml";
	}
	public synchronized JsonArray typeEpisodeConversion(final JsonArray episodes, final java.lang.Integer index,
			final String episode_oldType) {
		return episodes;
	}
	public synchronized final JsonObject getIfExists(final JsonArray episodes, final int index) {
		if (episodes == null)
			return null;
		if (index < 0)
			return null;
		if (episodes.size() < (index + 1))
			return null;
		final JsonElement back = episodes.get(index);
		if (back.isJsonObject())
			return back.getAsJsonObject();
		return null;
	}
	public synchronized JsonArray sortEpisodesByStartDate(final JsonArray arr) {
		return sortEpisodesByDate(arr, "startDate");
	}
	public synchronized JsonArray sortEpisodesByEndDate(final JsonArray arr) {
		return sortEpisodesByDate(arr, "endDate");
	}
	public synchronized JsonArray sortEpisodesByDate(final JsonArray arr, final String field) {
		if (arr == null)
			return null;
		if (arr.size() == 0)
			return arr;
		final ArrayList<JsonObject> array = new ArrayList<JsonObject>();
		final int size = arr.size();
		for (int i = 0; i < size; i++) {
			array.add(arr.get(i).getAsJsonObject());
		}
		try {
			Collections.sort(array, new Comparator<JsonObject>() {
				@Override
				public int compare(final JsonObject episode1, final JsonObject episode2) {
					String episode1EndDate = null;
					String episode2EndDate = null;
					final FunctionProvider zofar = JsfUtility.getInstance().evaluateValueExpression(
							FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
					if (episode1.has(field))
						episode1EndDate = episode1.get(field).getAsString();
					if (episode2.has(field))
						episode2EndDate = episode2.get(field).getAsString();
					if (!zofar.comparableTimestamps(episode1EndDate, episode2EndDate))
						return 0;
					return compareJsonTimestamps(episode1EndDate, episode2EndDate);
				}
			});
		} catch (final Exception e) {
			e.printStackTrace();
			return arr;
		}
		final JsonArray jsonArray = new JsonArray();
		for (final JsonObject episode : array) {
			jsonArray.add(episode);
		}
		return jsonArray;
	}
	public synchronized final String decideSplitTarget(final JsonArray arr, final int index,
			final JsonObject split_type_dict, final JsonArray split_type_order, final String fallbackTarget) {
		if (arr == null) {
			return fallbackTarget;
		}
		if (index < 0)
			return fallbackTarget;
		if (index >= arr.size())
			return fallbackTarget;
		final JsonObject episode = arr.get(index).getAsJsonObject();
		return this.decideSplitTarget(episode, split_type_dict, split_type_order, fallbackTarget);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	public synchronized String defrac(final List<AbstractAnswerBean> vars) throws Exception {
		if (vars == null)
			return "";
		if (vars.isEmpty())
			return "";
		final StringBuffer back = new StringBuffer();
		for (final AbstractAnswerBean var : vars) {
			if (var == null) {
				System.err.println("skip null var ");
				continue;
			}
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				back.append(((StringValueTypeBean) var).getStringValue());
			}
		}
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		return zofar.decompress(back.toString());
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @param data
	 * @throws Exception
	 */
	public synchronized void frac(final List<AbstractAnswerBean> vars, final String data) throws Exception {
		if (vars == null)
			return;
		if (vars.isEmpty())
			return;
		if (data == null)
			return;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final String compressed = zofar.compress(data);
		final int chunkSize = 1500;
		final LinkedList<String> chunks = new LinkedList<String>();
		if (chunkSize > data.length())
			chunks.add(compressed);
		else {
			final Matcher m = Pattern.compile(".{1," + chunkSize + "}").matcher(compressed);
			while (m.find()) {
				chunks.add(compressed.substring(m.start(), m.end()));
			}
		}
		for (final Object var : vars) {
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				String chunk = "";
				if (!chunks.isEmpty())
					chunk = chunks.removeFirst();
				((StringValueTypeBean) var).setStringValue(chunk);
			}
		}
		if (!chunks.isEmpty())
			throw new Exception("space too small for content: " + (chunks.size() * chunkSize) + " characters left");
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unlikely-arg-type")
	public synchronized final Map<String, Object> decodeEpisodesWithMeta(final String serializedMap) throws Exception {
		if (serializedMap == null)
			return null;
		if (serializedMap.equals(""))
			return null;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final String decompressed = zofar.decompress(serializedMap);
		final Map<String, Object> back = new HashMap<String, Object>();
		final int dataStart = decompressed.indexOf("DATA(");
		final int dataStop = decompressed.indexOf(")DATA");
		if ((dataStart != -1) && (dataStop != -1) && (dataStart <= dataStop)) {
			final String dataStr = decompressed.substring(dataStart + 5, dataStop);
			back.put("data", decodeEpisodes(dataStr));
		}
		final int metaStart = decompressed.indexOf("META(");
		final int metaStop = decompressed.indexOf(")META");
		if ((metaStart != -1) && (metaStop != -1) && (metaStart <= metaStop)) {
			final String metaStr = decompressed.substring(metaStart + 5, metaStop);
			final Map<Integer, List<Map<String, String>>> metaBack = new LinkedHashMap<Integer, List<Map<String, String>>>();
			final String[] eventSets = metaStr.split(";");
			if (eventSets != null) {
				for (final String eventSet : eventSets) {
					final String[] eventPair = eventSet.split(Pattern.quote("#"));
					if ((eventPair != null) && (eventPair.length == 2)) {
						final String eventIndexStr = eventPair[0];
						final Integer eventIndex = Integer.parseInt(eventIndexStr);
						final String metaListStr = eventPair[1];
						final String[] metaListPairs = metaListStr.split(Pattern.quote("^"));
						List<Map<String, String>> metaPairs = null;
						if (back.containsKey(eventIndex))
							metaPairs = metaBack.get(eventIndex);
						if (metaPairs == null)
							metaPairs = new ArrayList<Map<String, String>>();
						if ((metaListPairs != null) && (metaListPairs.length > 0)) {
							final Map<String, String> metaPairMap = new LinkedHashMap<String, String>();
							for (final String metaListPair : metaListPairs) {
								final String[] metaPair = metaListPair.split(Pattern.quote("°"));
								if (!((metaPair != null) && (metaPair.length == 2)))
									metaPairMap.put(metaPair[0], metaPair[1]);
							}
							metaPairs.add(metaPairMap);
						}
						if (!metaPairs.isEmpty())
							metaBack.put(eventIndex, metaPairs);
					}
				}
			}
			back.put("meta", metaBack);
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedMap
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<Integer, List<List<Integer>>> decodeEpisodes(final String serializedMap) throws Exception {
		if (serializedMap == null)
			return null;
		if (serializedMap.equals(""))
			return null;
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		final String decompressed = zofar.decompress(serializedMap);
		final Map<Integer, List<List<Integer>>> back = new LinkedHashMap<Integer, List<List<Integer>>>();
		final String[] slotSets = decompressed.split(";");
		if (slotSets != null) {
			for (final String slotSet : slotSets) {
				final String[] slotPair = slotSet.split("#");
				if ((slotPair != null) && (slotPair.length == 2)) {
					final String slotIndexStr = slotPair[0];
					final Integer slotIndex = Integer.parseInt(slotIndexStr);
					final String episodes = slotPair[1];
					final String[] episodePairs = episodes.split(":");
					List<List<Integer>> slotPairs = null;
					if (back.containsKey(slotIndex))
						slotPairs = back.get(slotIndex);
					if (slotPairs == null)
						slotPairs = new ArrayList<List<Integer>>();
					if ((episodePairs != null) && (episodePairs.length > 0)) {
						for (final String pair : episodePairs) {
							final String from = pair.substring(0, 3);
							final String to = pair.substring(3);
							slotPairs.add(Arrays.asList(Integer.parseInt(from), Integer.parseInt(to)));
						}
					}
					if (!slotPairs.isEmpty())
						back.put(slotIndex, slotPairs);
				}
			}
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private String encodeEpisodesWithMeta(final Map<String, Object> data) throws Exception {
		if (data == null)
			return null;
		String toCompress = new String();
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (data.containsKey("data")) {
			final Map<Integer, List<List<String>>> beams = (Map<Integer, List<List<String>>>) data.get("data");
			final DecimalFormat formatter = new DecimalFormat("000");
			final StringBuffer serializedMap = new StringBuffer();
			boolean first1 = true;
			for (final Map.Entry<Integer, List<List<String>>> slotBeam : beams.entrySet()) {
				if (!first1)
					serializedMap.append(";");
				serializedMap.append(slotBeam.getKey() + "#");
				final StringBuffer tmp = new StringBuffer();
				for (final List<String> episode : slotBeam.getValue()) {
					for (final String item : episode) {
						tmp.append(formatter.format(Integer.parseInt(item)));
					}
					tmp.append(":");
				}
				String cleaned = tmp.toString();
				cleaned = cleaned.substring(0, cleaned.length() - 1);
				serializedMap.append(cleaned);
				serializedMap.append("");
				first1 = false;
			}
			final String toCompresssData = serializedMap.toString();
			toCompress += "DATA(" + zofar.compress(toCompresssData) + ")DATA";
		}
		if (data.containsKey("meta")) {
			final Map<Integer, List<Map<String, String>>> metaBeams = (Map<Integer, List<Map<String, String>>>) data
					.get("meta");
			final StringBuffer serializedMap = new StringBuffer();
			boolean first1 = true;
			for (final Map.Entry<Integer, List<Map<String, String>>> metaBeam : metaBeams.entrySet()) {
				if (!first1)
					serializedMap.append(";");
				serializedMap.append(metaBeam.getKey() + "#");
				final StringBuffer tmp = new StringBuffer();
				for (final Map<String, String> episodeMeta : metaBeam.getValue()) {
					boolean first2 = true;
					for (final Map.Entry<String, String> episodeMetaItem : episodeMeta.entrySet()) {
						if (!first2)
							tmp.append("^");
						tmp.append(episodeMetaItem.getKey() + "°" + episodeMetaItem.getValue());
						first2 = false;
					}
					tmp.append(":");
				}
				String cleaned = tmp.toString();
				cleaned = cleaned.substring(0, cleaned.length() - 1);
				serializedMap.append(cleaned);
				serializedMap.append("");
				first1 = false;
			}
			final String toCompresssMeta = serializedMap.toString();
			toCompress += "META(" + toCompresssMeta + ")META";
		}
		return zofar.compress(toCompress);
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedData
	 * @return
	 */
	private synchronized final JsonArray decodeEpisodesNew(final String serializedData) {
		if (serializedData == null)
			return null;
		try {
			final String decoded = URLDecoder.decode(serializedData, StandardCharsets.UTF_8.toString());
			final JsonArray jsonArray = new JsonParser().parse(decoded).getAsJsonArray();
			return jsonArray;
		} catch (final UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedData
	 * @param fieldname
	 * @param key
	 * @param rangeStartStr
	 * @param rangeStopStr
	 * @return
	 * @throws Exception
	 */
	public synchronized int countEpisodesInRangeNew(final String serializedData, final String fieldname,
			final String key, final String rangeStartStr, final String rangeStopStr) throws Exception {
		if (serializedData == null)
			return 0;
		if (serializedData.trim().equals(""))
			return 0;
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		final SimpleDateFormat df1 = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss.SSS'Z'");
		final JsonArray episodes = decodeEpisodesNew(serializedData);
		final Date rangeStart = df.parse(rangeStartStr);
		final Date rangeStop = df.parse(rangeStopStr);
		int back = 0;
		final Iterator<JsonElement> it = episodes.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (tmp.isJsonObject()) {
				final JsonObject tmpJSON = (JsonObject) tmp;
				if (!tmpJSON.has(fieldname))
					continue;
				final JsonElement tmpStartDateObj = tmpJSON.get("startDate");
				Date tmpStartDate = null;
				if (tmpStartDateObj != null)
					tmpStartDate = df1.parse(tmpStartDateObj.getAsString());
				final JsonElement value = tmpJSON.get(fieldname);
				if ((value != null) && (value.getAsString().equals(key))) {
					if (tmpStartDate.before(rangeStart))
						continue;
					if (tmpStartDate.after(rangeStop))
						continue;
					back = back + 1;
				}
			}
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param variableTupels
	 * @return
	 * @throws Exception
	 */
	public synchronized String getEpisodesFromVariables(final List<Object> variableTupels) throws Exception {
		if (variableTupels == null)
			return null;
		final Map<Integer, List<List<String>>> strippedBeams = new LinkedHashMap<Integer, List<List<String>>>();
		final Map<Integer, List<Map<String, String>>> metaBeams = new LinkedHashMap<Integer, List<Map<String, String>>>();
		for (final Object variableTupel : variableTupels) {
			if ((Map.class).isAssignableFrom(variableTupel.getClass())) {
				final Map<String, Object> tmp = (Map<String, Object>) variableTupel;
				final Integer eventId = (Integer) tmp.get("event");
				final String start = tmp.get("start") + "";
				final String stop = tmp.get("stop") + "";
				final Object meta = tmp.get("meta");
				if (eventId < 0)
					continue;
				if (Integer.parseInt(start) < 0)
					continue;
				if (Integer.parseInt(stop) < 0)
					continue;
				List<List<String>> slotPairs = null;
				if (strippedBeams.containsKey(eventId))
					slotPairs = strippedBeams.get(eventId);
				if (slotPairs == null)
					slotPairs = new ArrayList<List<String>>();
				List<Map<String, String>> slotMeta = null;
				if (metaBeams.containsKey(eventId))
					slotMeta = metaBeams.get(eventId);
				if (slotMeta == null)
					slotMeta = new ArrayList<Map<String, String>>();
				final List<String> pair = new ArrayList<String>();
				pair.add(start);
				pair.add(stop);
				slotPairs.add(pair);
				final Map<String, String> metaMap = new LinkedHashMap<String, String>();
				if (meta != null) {
					if ((Map.class).isAssignableFrom(meta.getClass()))
						metaMap.putAll((Map) meta);
				}
				slotMeta.add(metaMap);
				strippedBeams.put(eventId, slotPairs);
				metaBeams.put(eventId, slotMeta);
			}
		}
		try {
			final Map<String, Object> back = new HashMap<String, Object>();
			back.put("data", strippedBeams);
			back.put("meta", metaBeams);
			return encodeEpisodesWithMeta(back);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
