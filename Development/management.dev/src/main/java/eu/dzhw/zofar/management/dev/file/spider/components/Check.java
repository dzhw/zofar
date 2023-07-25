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
package eu.dzhw.zofar.management.dev.file.spider.components;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Check implements Callable<Object> {
	
	final static Logger LOGGER = LoggerFactory.getLogger(Check.class);

	private String directory;
	private boolean quiet;
	private boolean recursive;
	private ExecutorService executor;
	private final FileHandler fileHandler;
	private final DirectoryHandler dirHandler;
	private final String startTag;
	private final String stopTag;
	

	/**
	 * @param directory
	 * @param quiet
	 * @param recursive
	 * @param parent
	 */
	public Check(String directory, boolean quiet, boolean recursive,ExecutorService executor,final DirectoryHandler dirHandler,final FileHandler fileHandler,String startTag,String stopTag) {
		this.directory = directory;
		this.quiet = quiet;
		this.recursive = recursive;
		this.executor = executor;
		this.fileHandler = fileHandler;
		this.dirHandler = dirHandler;
		if(startTag != null)startTag = startTag.trim();
		this.startTag = startTag;
		if(stopTag != null)stopTag = stopTag.trim();
		this.stopTag = stopTag;
	}

	public Object call() throws Exception {
		if((directory != null)&&(!directory.equals(""))){
			File dir = new File(directory);
			if((dir != null)&&(dir.exists())&&(dir.canRead())&&(dir.isDirectory())){
				File[] files = dir.listFiles();
				if(files != null){
					if(!quiet)LOGGER.info("Count files in directory {} : {}",directory,files.length);
					int count = files.length;
					for(int a=0;a<count;a++){
						File tmp = files[a];
						if((tmp != null)&&(tmp.canRead())){
							if(tmp.isDirectory()){
								if(this.dirHandler != null)this.dirHandler.call(tmp);
								if(recursive){
									Vector<Callable<Object>> tasks =  new Vector<Callable<Object>>();
									HashSet<Future> pointer = new HashSet<Future>();
									Check check = new Check(tmp.getAbsolutePath(),quiet,recursive,this.executor,this.dirHandler,this.fileHandler,startTag,stopTag);
									tasks.add(check);
									try {
										pointer.addAll(this.executor.invokeAll(tasks));
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									Iterator<Future> checkIt = pointer.iterator();
									while(checkIt.hasNext()){
										Future<?> tmp1 = checkIt.next();
										if(tmp1.isDone()){
											checkIt.remove();
										}
									}
								}

							}
							else if(tmp.isFile()){
								if(this.fileHandler != null)this.fileHandler.call(tmp,this.startTag,this.stopTag);
							}
						}
					}
				}
			}
		}
		return null;
	}

}
