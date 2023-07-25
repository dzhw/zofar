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
 * Class to handle Streams
 */
package eu.dzhw.zofar.management.utils.stream;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class StreamClient.
 */
public class StreamClient {
	/** The Constant INSTANCE. */
	private static final StreamClient INSTANCE = new StreamClient();

	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory.getLogger(StreamClient.class);

	/**
	 * Instantiates a new string utils.
	 */
	private StreamClient() {
		super();
	}

	/**
	 * Gets the single instance of StringUtils.
	 * 
	 * @return single instance of StringUtils
	 */
	public static synchronized StreamClient getInstance() {
		return StreamClient.INSTANCE;
	}
	
	/**
	 * Link Output streams.
	 *
	 * @param stream1 the stream1
	 * @param stream2 the stream2
	 * @return the tee output stream
	 */
	public OutputStream linkStreams(final OutputStream stream1, final OutputStream stream2) {
		final OutputStream back = new TeeOutputStream(stream1, stream2);
		return back;
	}
	
	/**
	 * Link Input streams.
	 *
	 * @param streams the streams
	 * @return the input stream
	 */
	public InputStream linkStreams(final InputStream... streams) {
		final InputStream back = new SequenceInputStream( Collections.enumeration(Arrays.asList(streams)));
		return back;
	}	
}
