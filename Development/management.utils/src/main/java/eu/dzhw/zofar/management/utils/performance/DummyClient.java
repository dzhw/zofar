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
 * Class of Dummy Methods for Testing
 */
package eu.dzhw.zofar.management.utils.performance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DummyClient.
 */
public class DummyClient {

	/** The Constant INSTANCE. */
	private static final DummyClient INSTANCE = new DummyClient();

	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(DummyClient.class);

	/**
	 * The Class WaitTask.
	 */
	private class WaitTask extends Thread {

		/** The wait. */
		private final long wait;

		/**
		 * Instantiates a new wait task.
		 * 
		 * @param wait
		 *            the wait
		 */
		public WaitTask(final long wait) {
			super();
			this.wait = wait;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			super.run();
			try {
				LOGGER.info(this + " wait for " + this.wait + " ms");
				sleep(this.wait);
				LOGGER.info(this + " done");
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Instantiates a new dummy client.
	 */
	private DummyClient() {
		super();
	}

	/**
	 * Method with defined runtime.
	 * 
	 * @param timeout
	 *            the timeout
	 */
	public void handbrake(final long timeout) {
		final ExecutorService executor = Executors.newFixedThreadPool(1);
		final Thread task = new WaitTask(timeout);
		executor.execute(task);
		try {
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}
