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
package eu.dzhw.zofar.management.generator.qml.calendar;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;

import eu.dzhw.zofar.management.dev.automation.AbstractExecutor;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.system.ConfigurationUtils;

public class Executor extends AbstractExecutor {

	private static final long serialVersionUID = 4303089499568520249L;
	private static final Executor INSTANCE = new Executor();
	private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
	
	final CalendarGenerator calendarGenerator = CalendarGenerator.getInstance();

	public enum Parameter implements ABSTRACTPARAMETER {
		slots, prefixes, rows, columns, workDir;
	};

	private Executor() {
		super();

	}

	public static Executor getInstance() {
		return INSTANCE;
	}

	public ParameterMap<ABSTRACTPARAMETER, Object> getParameterMap(final String slots, final String prefixes, final File workDir, final String columns, final String rows) {
		final ParameterMap<ABSTRACTPARAMETER, Object> back = new ParameterMap<ABSTRACTPARAMETER, Object>();
		back.put(Parameter.slots, slots);
		back.put(Parameter.prefixes, prefixes);
		back.put(Parameter.columns, columns);
		back.put(Parameter.rows, rows);
		back.put(Parameter.workDir, workDir);
		return back;
	}

	@Override
	public void process(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String slots = (String) parameter.get(Parameter.slots);
		final String prefixes = (String) parameter.get(Parameter.prefixes);

		final String columns = (String) parameter.get(Parameter.columns);
		final String rows = (String) parameter.get(Parameter.rows);

		final File workDir = (File) parameter.get(Parameter.workDir);

		if (workDir != null) {
			try {
				calendarGenerator.generateQML("calendar1",getItems(prefixes), null,null, getItems(slots), getItems(columns), getItems(rows), workDir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String[] getItems(String items) {
			
			String [] itemsN = items.split("\\s*;\\s*");
			return itemsN;
		}
}

