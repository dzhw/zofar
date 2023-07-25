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
 * Utility Class to interact with System-Environment
 */
package eu.dzhw.zofar.management.utils.system;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SystemClient.
 */
public class SystemClient {

	/** The Constant INSTANCE. */
	private static final SystemClient INSTANCE = new SystemClient();

	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(SystemClient.class);

	/**
	 * Instantiates a new system client.
	 */
	private SystemClient() {
		super();
	}

	/**
	 * Gets the single instance of SystemClient.
	 * 
	 * @return single instance of SystemClient
	 */
	public static synchronized SystemClient getInstance() {
		return INSTANCE;
	}

	/**
	 * Used Heap Space.
	 * 
	 * @return the long
	 */
	public long usedMem() {
		final Runtime runtime = Runtime.getRuntime();
		final long total = runtime.totalMemory();
		final long free = runtime.freeMemory();
		return (total - free);
	}

	/**
	 * Used Heap Space in percent.
	 * 
	 * @return the double
	 */
	public double memLoadInPercent() {
		final Runtime runtime = Runtime.getRuntime();
		final long total = runtime.totalMemory();
		final long used = this.usedMem();
		return used / total;
	}

	/**
	 * Trigger for Garbage Collection.
	 * 
	 * @param reason
	 *            the reason
	 */
	public void doGC(final String reason) {
		LOGGER.info("run GC, because {}", reason);
		System.gc();
	}

	/**
	 * Copy String Content To System Clipboard.
	 * 
	 * @param text
	 *            the text
	 */
	public void toClipboard(final String text) {
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Clipboard clipboard = toolkit.getSystemClipboard();

		clipboard.setContents(new StringSelection(text), null);
	}

	/**
	 * Get String Content From System Clipboard.
	 * 
	 * @return the string
	 */
	public String fromClipboard() {
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Clipboard clipboard = toolkit.getSystemClipboard();
		final Transferable contents = clipboard.getContents(null);
		String result = null;
		final boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (final UnsupportedFlavorException e) {
				LOGGER.error("Unsupported Flavor", e);
			} catch (final IOException e) {
				LOGGER.error("IO Exception", e);
			}
		}
		return result;
	}
	
	public String getUser() {
		return System.getProperty("user.name");
	}
}
