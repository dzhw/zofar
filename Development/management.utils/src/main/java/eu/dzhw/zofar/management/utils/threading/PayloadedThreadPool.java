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
package eu.dzhw.zofar.management.utils.threading;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class PayloadedThreadPool implements Observer {
	final ExecutorService executor;
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(PayloadedThreadPool.class);
	public PayloadedThreadPool() {
		this(10);
	}
	public PayloadedThreadPool(final int maxPoolSize) {
		super();
		executor = Executors.newFixedThreadPool(maxPoolSize);
	}
	/**
	 * Here we add our jobs to working queue
	 * 
	 * @param task
	 *            a Runnable task
	 */
	public void runTask(PayloadedThread task) {
		task.addObserver(this);
		executor.execute(task);
	}
	/**
	 * Shutdown the Threadpool if it's finished
	 */
	public void shutdown() {
		executor.shutdown();
	}
	public boolean isTerminated() {
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	public void update(Observable o, Object arg) {
		LOGGER.info("Observable {} send message {}",o,arg);
	}
}
