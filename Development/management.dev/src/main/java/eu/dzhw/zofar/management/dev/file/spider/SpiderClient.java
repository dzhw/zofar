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
package eu.dzhw.zofar.management.dev.file.spider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dzhw.zofar.management.dev.file.spider.components.Check;
import eu.dzhw.zofar.management.dev.file.spider.components.DirectoryHandler;
import eu.dzhw.zofar.management.dev.file.spider.components.FileHandler;

public class SpiderClient {
	private static final SpiderClient INSTANCE = new SpiderClient();

	final static Logger LOGGER = LoggerFactory.getLogger(SpiderClient.class);

	private SpiderClient() {
		super();
	}

	public static synchronized SpiderClient getInstance() {
		return INSTANCE;
	}
	
	public void doSpider(final String directory,final boolean quiet,final boolean recursive,final DirectoryHandler dirHandler,final FileHandler fileHandler,String startTag,String stopTag){
		final ExecutorService executor = Executors.newCachedThreadPool();
		final Set<Callable<Object>> tasks =  new LinkedHashSet<Callable<Object>>();
		final HashSet<Future> pointer = new LinkedHashSet<Future>();
		final Check check = new Check(directory,quiet,recursive,executor,dirHandler,fileHandler,startTag,stopTag);
		tasks.add(check);
		try {
			pointer.addAll(executor.invokeAll(tasks));
		} catch (final InterruptedException e1) {
			e1.printStackTrace();
		}
		final Iterator<Future> checkIt = pointer.iterator();
		while(checkIt.hasNext()){
			final Future<?> tmp = checkIt.next();
			if(tmp.isDone()){
				checkIt.remove();
			}
		}
		LOGGER.info("Done");
		executor.shutdown();
	}
	
}
