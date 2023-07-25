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
 * HTTP Downloader
 */
package eu.dzhw.zofar.management.comm.network.download.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dzhw.zofar.management.comm.network.download.Loader;

/**
 * The Class HttpLoader.
 */
public class HttpLoader implements Loader {
	
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(HttpLoader.class);

	/**
	 * Instantiates a new http loader.
	 */
	public HttpLoader() {
		super();
	}

	/**
	 * Load Content from target.
	 *
	 * @param target the target
	 * @param onlyBinaries if true load only binary files
	 * @return the content as File
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File getContent(final String target,final boolean onlyBinaries) throws IOException {
		URL url = null;
		try {
			url = new URL(target);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		LOGGER.info("load {} ",url);
		URLConnection connection = null;
		if(url != null){
			try {
				connection = url.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(connection != null){
			String contentType = connection.getContentType();
		    int contentLength = connection.getContentLength();
		    if (onlyBinaries && (contentType.startsWith("text/") || contentLength == -1)) {
		      throw new IOException("This is not a binary file.");
		    }
		    InputStream raw = connection.getInputStream();
		    InputStream in = new BufferedInputStream(raw);
		    byte[] data = new byte[contentLength];
		    int bytesRead = 0;
		    int offset = 0;
		    while (offset < contentLength) {
		      bytesRead = in.read(data, offset, data.length - offset);
		      if (bytesRead == -1)
		        break;
		      offset += bytesRead;
		    }
		    in.close();

		    if (offset != contentLength) {
		      throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
		    }

		    String filename = url.getFile();
		    filename = filename.substring(filename.lastIndexOf('/') + 1);
		    File tmpFile = File.createTempFile("downloaded_", filename);
		    FileOutputStream out = new FileOutputStream(tmpFile);
		    out.write(data);
		    out.flush();
		    out.close();
		    return tmpFile;
		}
	return null;
}

	/**
	 * Load Content from target.
	 *
	 * @param target the target
	 * @return the content as File
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File getContent(String target) throws IOException {
		return this.getContent(target, false);
	}

}
