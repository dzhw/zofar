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
 * Class of Methods to load and copy Objects
 */
package eu.dzhw.zofar.management.utils.objects;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class LoaderClient.
 */
public class LoaderClient {
	/** The Constant INSTANCE. */
	private static final LoaderClient INSTANCE = new LoaderClient();
	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(LoaderClient.class);
	/**
	 * Instantiates a new loader client.
	 */
	private LoaderClient() {
		super();
	}
	/**
	 * Gets the single instance of LoaderClient.
	 * 
	 * @return single instance of LoaderClient
	 */
	public static synchronized LoaderClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Copy object.
	 * 
	 * @param o
	 *            the o
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	public Object copyObject(final Object o) throws Exception {
		return CloneClient.getInstance().cloneObj(o);
	}
	/**
	 * Serialize Object to file.
	 * 
	 * @param filename
	 *            the filename
	 * @param obj
	 *            the obj
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void serialize(final String filename, final Object obj) throws IOException {
		final File file = new File(filename);
		if ((file.exists()) && (file.isFile()) && (file.canWrite())) {
			file.delete();
			file.createNewFile();
		}
		final ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
		objOut.writeObject(obj);
		objOut.close();
	}
	/**
	 * Deserialize Object from file.
	 * 
	 * @param filename
	 *            the filename
	 * @return the object
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public Object deserialize(final String filename) throws IOException, ClassNotFoundException {
		if ((filename == null) || (filename.equals("")))
			return null;
		final ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
		final Object back = objIn.readObject();
		objIn.close();
		return back;
	}
	/**
	 * Load raw data.
	 * 
	 * @param filename
	 *            the filename
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public byte[] loadRawData(final String filename) throws IOException {
		if ((filename == null) || (filename.equals("")))
			return null;
		final File file = new File(filename);
		final InputStream is = new FileInputStream(file);
		final long length = file.length();
		if (length > Integer.MAX_VALUE) {
			is.close();
			return null;
		}
		final byte[] back = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < back.length && (numRead = is.read(back, offset, back.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < back.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}
		is.close();
		return back;
	}
}
