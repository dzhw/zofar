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
/*
 * Class of methods to clone Objects or Collections of Objects
 */
package eu.dzhw.zofar.management.utils.objects;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class CloneClient.
 */
public class CloneClient {
	/** The Constant INSTANCE. */
	private static final CloneClient INSTANCE = new CloneClient();
	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(CloneClient.class);
	/**
	 * Instantiates a new clone client.
	 */
	private CloneClient() {
		super();
	}
	/**
	 * Gets the single instance of CloneClient.
	 * 
	 * @return single instance of CloneClient
	 */
	public static synchronized CloneClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Clone list.
	 * 
	 * @param <V>
	 *            the value type
	 * @param source
	 *            the source
	 * @return the list
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	public <V> List<V> cloneList(final List<V> source) throws CloneNotSupportedException {
		if (source == null)
			return null;
		List<V> copy = null;
		if (source instanceof ArrayList)
			copy = Collections.synchronizedList(new ArrayList<V>(source.size()));
		else if (source instanceof LinkedList)
			copy = new LinkedList<V>();
		else {
			copy = Collections.synchronizedList(new ArrayList<V>(source.size()));
		}
		if (copy != null) {
			for (final V entry : source) {
				V tmpValue = null;
				if (entry != null)
					tmpValue = this.cloneObj(entry);
				final V clonedValue = tmpValue;
				copy.add(clonedValue);
			}
		} else {
			System.err.println("List type " + source.getClass() + " not supported");
			throw new CloneNotSupportedException();
		}
		return copy;
	}
	/**
	 * Clone array.
	 * 
	 * @param <V>
	 *            the value type
	 * @param source
	 *            the source
	 * @return the v[]
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	public <V> V[] cloneArray(final V[] source) throws CloneNotSupportedException {
		if (source == null)
			return null;
		final int count = source.length;
		final List<V> arrayList = Collections.synchronizedList(new ArrayList<V>(count));
		for (int a = 0; a < count; a++) {
			arrayList.add(source[a]);
		}
		final V[] copy = arrayList.toArray(source);
		return copy;
	}
	/**
	 * Clone map.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param source
	 *            the source
	 * @return the map
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	public <K, V> Map<K, V> cloneMap(final Map<K, V> source) throws CloneNotSupportedException {
		if (source == null)
			return null;
		Map<K, V> copy = null;
		if (source instanceof HashMap)
			copy = new HashMap<K, V>();
		if (source instanceof LinkedHashMap)
			copy = new LinkedHashMap<K, V>();
		if (source instanceof Hashtable)
			copy = new Hashtable<K, V>();
		if (copy != null) {
			for (final Map.Entry<K, V> entry : source.entrySet()) {
				K tmpKey = null;
				if (entry.getKey() != null) {
					tmpKey = this.cloneObj(entry.getKey());
				}
				V tmpValue = null;
				if (entry.getValue() != null) {
					tmpValue = this.cloneObj(entry.getValue());
				}
				final K clonedKey = tmpKey;
				final V clonedValue = tmpValue;
				copy.put(clonedKey, clonedValue);
			}
		} else {
			System.err.println("Map type " + source.getClass() + " not supported");
			throw new CloneNotSupportedException();
		}
		return copy;
	}
	/**
	 * Clone Object.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param original
	 *            the original
	 * @return the t
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T cloneObj(final T original) throws CloneNotSupportedException {
		if (original == null)
			return null;
		T copy = null;
		final Class<T> clazz = (Class<T>) original.getClass();
		try {
			final Method clone = clazz.getMethod("clone");
			if (clone != null) {
				copy = (T) clone.invoke(original);
			} else {
				copy = this.clonePrimitive(original);
			}
		} catch (final SecurityException e) {
			copy = this.clonePrimitive(original);
		} catch (final NoSuchMethodException e) {
			copy = this.clonePrimitive(original);
		} catch (final IllegalArgumentException e) {
			copy = this.clonePrimitive(original);
		} catch (final IllegalAccessException e) {
			copy = this.clonePrimitive(original);
		} catch (final InvocationTargetException e) {
			copy = this.clonePrimitive(original);
		}
		return copy;
	}
	/**
	 * Clone primitive.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param original
	 *            the original
	 * @return the t
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	@SuppressWarnings("unchecked")
	private <T> T clonePrimitive(final T original) throws CloneNotSupportedException {
		if (original == null)
			return null;
		T copy = null;
		final Class<T> clazz = (Class<T>) (original).getClass();
		try {
			final Class<?>[] param = new Class[1];
			param[0] = String.class;
			final Object[] init = new Object[1];
			init[0] = original + "";
			final Constructor<?> constructor = clazz.getConstructor(param);
			if (constructor != null) {
				copy = (T) constructor.newInstance(init);
			} else {
				copy = this.deepClone(original);
			}
		} catch (final SecurityException e) {
			copy = this.deepClone(original);
		} catch (final NoSuchMethodException e) {
			copy = this.deepClone(original);
		} catch (final IllegalArgumentException e) {
			copy = this.deepClone(original);
		} catch (final InstantiationException e) {
			copy = this.deepClone(original);
		} catch (final IllegalAccessException e) {
			copy = this.deepClone(original);
		} catch (final InvocationTargetException e) {
			copy = this.deepClone(original);
		}
		return copy;
	}
	/**
	 * Deep clone.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param original
	 *            the original
	 * @return the t
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T deepClone(final T original) throws CloneNotSupportedException {
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(original);
			final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			final ObjectInputStream ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (final IOException e) {
			throw new CloneNotSupportedException();
		} catch (final ClassNotFoundException e) {
			throw new CloneNotSupportedException();
		}
	}
	/**
	 * Convert Object to Byte array.
	 * 
	 * @param original
	 *            the original
	 * @return the byte[]
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	public byte[] obj2byteArray(final Object original) throws CloneNotSupportedException {
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(original);
			return baos.toByteArray();
		} catch (final IOException e) {
			throw new CloneNotSupportedException();
		}
	}
}
