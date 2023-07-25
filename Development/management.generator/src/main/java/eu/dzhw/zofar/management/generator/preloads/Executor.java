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
package eu.dzhw.zofar.management.generator.preloads;

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
	
	final PreloadClient preloadGenerator = PreloadClient.getInstance();

	public enum Parameter implements ABSTRACTPARAMETER {
		fileName, workDir;
	};

	private Executor() {
		super();
	}

	public static Executor getInstance() {
		return INSTANCE;
	}

	public ParameterMap<ABSTRACTPARAMETER, Object> getParameterMap(final String fileName, final File workDir) {
		final ParameterMap<ABSTRACTPARAMETER, Object> back = new ParameterMap<ABSTRACTPARAMETER, Object>();
		back.put(Parameter.fileName, fileName);
		back.put(Parameter.workDir, workDir);
		return back;
	}

	@Override
	public void process(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String fileName = (String) parameter.get(Parameter.fileName);
		final File workDir = (File) parameter.get(Parameter.workDir);
		
		final File csvFile = new File(workDir+""+File.separator+""+fileName);
		final File sqlParticipantFile = new File(csvFile.getParentFile(),"participants.sql");
		final File sqlDataFile = new File(csvFile.getParentFile(),"surveydata.sql");
		
		final PreloadClient preloader = PreloadClient.getInstance();
		try {
			final Map<PreloadClient.FIELDS,String> parsed = preloader.csv2sql(csvFile);
			if(parsed != null){
				FileClient.getInstance().writeToFile(sqlParticipantFile.getAbsolutePath(), parsed.get(PreloadClient.FIELDS.PARTICIPANT), false);
				FileClient.getInstance().writeToFile(sqlDataFile.getAbsolutePath(), parsed.get(PreloadClient.FIELDS.DATA), false);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}

