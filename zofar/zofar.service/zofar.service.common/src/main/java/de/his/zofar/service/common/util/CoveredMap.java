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
package de.his.zofar.service.common.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CoveredMap<K, V> extends AbstractMap<K, V> implements Serializable {

	private static final long serialVersionUID = -1106468279422790782L;
	private final Map<K,V> parentMap;
	private final Object caller;
    private transient Method function;

	public CoveredMap(final Map<K,V> parentMap,final Object caller, final String fallbackFunction, final Class<K> keyClass) {
		super();
		this.parentMap = parentMap;
		this.caller = caller;

		@SuppressWarnings("rawtypes")
        final
		Class[] parameterTypes = new Class[1];
		parameterTypes[0] = keyClass;

		try {
			function = caller.getClass().getDeclaredMethod(fallbackFunction, parameterTypes);
		} catch (final NoSuchMethodException e) {
			e.printStackTrace();
		} catch (final SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int size() {
		return this.parentMap.size();
	}

	@Override
	public boolean isEmpty() {
		return this.parentMap.isEmpty();
	}

	@Override
	public boolean containsValue(final Object value) {
		return this.parentMap.containsValue(value);
	}

	@Override
	public boolean containsKey(final Object key) {
		return this.parentMap.containsKey(key);
	}

	@Override
	public V get(final Object key) {
		if (key == null) return null;
		if(this.parentMap.containsKey(key)){
			return this.parentMap.get(key);
		}
		else{
			if (caller == null) return null;
			if (function == null) return null;
			try {
				@SuppressWarnings("unchecked")
                final
	            V result = (V) function.invoke(caller, key);
				return result;
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public V put(final K key, final V value) {
		return this.parentMap.put(key, value);
	}

	@Override
	public V remove(final Object key) {
		return this.parentMap.remove(key);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m) {
		this.parentMap.putAll(m);
	}

	@Override
	public void clear() {
		this.parentMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return this.parentMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return this.parentMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return this.parentMap.entrySet();
	}

	@Override
	public boolean equals(final Object o) {
		return this.parentMap.equals(o);
	}

	@Override
	public int hashCode() {
		return this.parentMap.hashCode();
	}

	@Override
	public String toString() {
		return "CoverMap of "+this.parentMap.toString();
	}
}
