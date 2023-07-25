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
package eu.dzhw.zofar.management.utils.objects;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.string.StringUtils;
public class CollectionClient {
	private static final CollectionClient INSTANCE = new CollectionClient();
	private final static Logger LOGGER = LoggerFactory.getLogger(CollectionClient.class);
	private CollectionClient() {
		super();
	}
	public static synchronized CollectionClient getInstance() {
		return INSTANCE;
	}
	public <T> List<T> intersection(final List<T> list1, final List<T> list2) {
		if (list1 == null) {
			return null;
		}
		if (list2 == null) {
			return null;
		}
		final List<T> list = new ArrayList<T>();
		for (final T t : list1) {
			if (list2.contains(t)) {
				list.add(t);
			}
		}
		return list;
	}
	public <K, V> Map<K, List<V>> addToMap(final Map<K, List<V>> map, final K key, final V value) {
		if (map == null) {
			return null;
		}
		if (key == null) {
			return map;
		}
		if (value == null) {
			return map;
		}
		if (map.containsKey(key)) {
			map.get(key).add(value);
		} else {
			final List<V> list = new ArrayList<V>();
			list.add(value);
			map.put(key, list);
		}
		return map;
	}
	public <K, V> Map<K, List<V>> addListToMap(final Map<K, List<V>> map, final K key, final List<V> value) {
		if (map == null) {
			return null;
		}
		if (key == null) {
			return map;
		}
		if (value == null) {
			return map;
		}
		if (map.containsKey(key)) {
			map.get(key).addAll(value);
		} else {
			map.put(key, value);
		}
		return map;
	}
	/**
	 * Implode List of elements to String glued by ','.
	 *
	 * @param elements
	 *            the elements
	 * @return the string
	 */
	public String implode(final List<Object> elements) {
		if (elements == null) {
			return "NULL";
		} else {
			return this.implode(elements.toArray(), ",");
		}
	}
	public String implode(final Set<?> elements) {
		if (elements == null) {
			return "NULL";
		} else {
			return this.implode(elements.toArray(), ",");
		}
	}
	/**
	 * Implode List of elements to String glued by custom glue.
	 *
	 * @param elements
	 *            the elements
	 * @param glue
	 *            the glue
	 * @return the string
	 */
	public String implode(final Object[] elements, final String glue) {
		if (elements == null) {
			return "NULL";
		} else {
			String back = "";
			final int count = elements.length;
			for (int a = 0; a < count; a++) {
				if(elements[a] == null)continue;
				back += (elements[a].toString()).trim();
				if (a < (count - 1)) {
					back += glue;
				}
			}
			return back;
		}
	}
	/**
	 * Explode String to list split by ','.
	 *
	 * @param data
	 *            the data
	 * @return the list
	 */
	public List<String> explode(final String data) {
		return explode(data, ",");
	}
	/**
	 * Convert Object[] to List.
	 *
	 * @param objects
	 *            the objects
	 * @return the list
	 */
	public <T> List<T> asList(final T... objects) {
		return Arrays.asList(objects);
	}
	public <T> List<T> shuffledList(final List<T> list) {
		final List<T> clone = new ArrayList<T>(list);
		Collections.shuffle(clone);
		return clone;
	}
	/**
	 * Convert Object[] to Enumeration.
	 *
	 * @param objects
	 *            the objects
	 * @return the enumeration
	 */
	public <T> Enumeration<T> asEnum(final T... objects) {
		return Collections.enumeration(asList(objects));
	}
	public <T> void removeDoubles(final List<T> list) {
		if (list == null) {
			return;
		}
		if (list.isEmpty()) {
			return;
		}
		final Set<T> cache = new HashSet<T>();
		final Iterator<T> it = list.listIterator();
		while (it.hasNext()) {
			final T item = it.next();
			if (cache.contains(item)) {
				it.remove();
			} else {
				cache.add(item);
			}
		}
	}
	/**
	 * Explode String to list split by custom glue.
	 *
	 * @param data
	 *            the data
	 * @param glue
	 *            the glue
	 * @return the list
	 */
	public List<String> explode(final String data, final String glue) {
		if (data == null) {
			return null;
		}
		final String[] tmp = data.split(glue);
		if (tmp != null) {
			return Arrays.asList(tmp);
		}
		return null;
	}
	public String mapAsString(final Map<?, ?> map, final String columnGlue) {
		if (map == null) {
			return null;
		}
		final StringBuffer back = new StringBuffer();
		for (final Map.Entry<?, ?> item : map.entrySet()) {
			back.append(item.getKey() + columnGlue + item.getValue() + "\n");
		}
		return back.toString();
	}
	public String mapAsString(final Map<?, ?> map, final String columnGlue, final String itemGlue) {
		if (map == null) {
			return null;
		}
		final StringBuffer back = new StringBuffer();
		for (final Map.Entry<?, ?> item : map.entrySet()) {
			back.append(item.getKey() + columnGlue + item.getValue() + itemGlue);
		}
		String backStr = back.toString();
		backStr = backStr.substring(0, backStr.lastIndexOf(itemGlue));
		return backStr;
	}
	public String structureAsString(final Map<String, Object> map) {
		return structureAsString(map,1,"\t");
	}
	public String structureAsString(final Map<String, Object> map,final String stageStr) {
		return structureAsString(map,1,stageStr);
	}
	public String structureAsString(final Map<String, Object> map,final int stage,final String stageStr) {
		if (map == null) {
			return null;
		}
		final StringBuffer back = new StringBuffer();
		for (final Map.Entry<String, Object> item : map.entrySet()) {
			final String key = item.getKey();
			back.append(StringUtils.getInstance().times(stage, stageStr, "")+"\""+key + "\"\n");
			final Object values = item.getValue();
			if((Set.class).isAssignableFrom(values.getClass())) {
				for (final Object value : (Set)values) {
					back.append(StringUtils.getInstance().times(stage+1, stageStr, "")+value + "\n");
				}
			}
			else if((Map.class).isAssignableFrom(values.getClass())) {
					back.append(structureAsString((Map<String,Object>)values,stage+1,stageStr)+"\n");
			}
		}
		return back.toString();
	}
	public String groupedMapAsString(final Map<String, Set<String>> map, final String columnGlue) {
		if (map == null) {
			return null;
		}
		final StringBuffer back = new StringBuffer();
		for (final Map.Entry<String, Set<String>> item : map.entrySet()) {
			final String key = item.getKey();
			final Set<String> values = item.getValue();
			for (final String value : values) {
				back.append(key + columnGlue + value + "\n");
			}
		}
		return back.toString();
	}
	public String groupedListMapAsString(final Map<String, List<String>> map, final String columnGlue) {
		if (map == null) {
			return null;
		}
		final StringBuffer back = new StringBuffer();
		for (final Map.Entry<String, List<String>> item : map.entrySet()) {
			final String key = item.getKey();
			final List<String> values = item.getValue();
			for (final String value : values) {
				back.append(key + columnGlue + value + "\n");
			}
		}
		return back.toString();
	}
	public Map<String, Set<String>> parseGroupedMap(final String toParse, final String columnGlue) throws IOException {
		if (toParse == null) {
			return null;
		}
		final Map<String, Set<String>> back = new LinkedHashMap<String, Set<String>>();
		if (toParse.equals("")) {
			return back;
		}
		final List<String> lines = IOUtils.readLines(new StringReader(toParse));
		for (final String line : lines) {
			final String[] columns = line.split(Pattern.quote(columnGlue));
			Set<String> set = null;
			if (back.containsKey(columns[0])) {
				set = back.get(columns[0]);
			}
			if (set == null) {
				set = new LinkedHashSet<String>();
			}
			if (columns.length > 1) {
				set.add(columns[1]);
			}
			back.put(columns[0], set);
		}
		return back;
	}
	public Map<String, String> parseMap(final String toParse, final String columnGlue, final String valueGlue)
			throws IOException {
		if (toParse == null) {
			return null;
		}
		final Map<String, String> back = new LinkedHashMap<String, String>();
		if (toParse.equals("")) {
			return back;
		}
		final String[] columns = toParse.split(Pattern.quote(columnGlue));
		if (columns != null) {
			for (final String column : columns) {
				final String[] pair = column.split(Pattern.quote(valueGlue));
				if ((pair != null) && (pair.length == 2)) {
					back.put(pair[0], pair[1]);
				}
			}
		}
		return back;
	}
}
