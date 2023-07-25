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
package eu.dzhw.zofar.management.dev.automation.loadTest;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import eu.dzhw.zofar.management.comm.db.postgresql.PostgresClient;
import eu.dzhw.zofar.management.dev.automation.AbstractExecutor;
import eu.dzhw.zofar.management.dev.qml.QMLClient;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import service.automation.counting.CountingService;
import service.automation.data.DataService;
import service.automation.feedback.FeedbackService;
import service.automation.statistics.StatisticService;
public class Executor extends AbstractExecutor {
	private static final long serialVersionUID = 8117968813518658482L;
	private static final Executor INSTANCE = new Executor();
	public enum Parameter implements ABSTRACTPARAMETER {
		dbLocation, dbPort, dbUser, dbPass, dbName, targetSurvey, targetProtocol, targetUrl, targetPort, packetSize, repeatCount, repeatTimeout, packetTimeout, minThreadTimeout, maxThreadTimeout, actionDelayMin, actionDelayMax, count, offset, runsynchron, svnProject, svnUser, svnPass, svnUrl
	};
	private Executor() {
		super();
	}
	public static Executor getInstance() {
		return INSTANCE;
	}
	public ParameterMap<ABSTRACTPARAMETER, Object> getParameterMap(final String svnUrl, final String svnUser, final String svnPass, final String svnProject, final String dbLocation, final int dbPort, final String dbUser, final String dbPass,final String dbName, final String targetProtocol, final String targetUrl, final int targetPort, final String targetSurvey, final int repeatCount, final int repeatTimeout, final int packetTimeout, final int minThreadTimeout, final int maxThreadTimeout, final int actionDelayMin, final int actionDelayMax, final int packetSize, final int count, final int offset, final int runsynchron) {
		final ParameterMap<ABSTRACTPARAMETER, Object> back = new ParameterMap<ABSTRACTPARAMETER, Object>();
		back.put(Parameter.targetProtocol, targetProtocol);
		back.put(Parameter.targetUrl, targetUrl);
		back.put(Parameter.targetPort, targetPort);
		back.put(Parameter.targetSurvey, targetSurvey);
		back.put(Parameter.svnUrl, svnUrl);
		back.put(Parameter.svnUser, svnUser);
		back.put(Parameter.svnPass, svnPass);
		back.put(Parameter.svnProject, svnProject);
		back.put(Parameter.dbLocation, dbLocation);
		back.put(Parameter.dbPort, dbPort);
		back.put(Parameter.dbUser, dbUser);
		back.put(Parameter.dbPass, dbPass);
		back.put(Parameter.dbName, dbName);
		back.put(Parameter.repeatCount, repeatCount);
		back.put(Parameter.repeatTimeout, repeatTimeout);
		back.put(Parameter.packetSize, packetSize);
		back.put(Parameter.packetTimeout, packetTimeout);
		back.put(Parameter.minThreadTimeout, minThreadTimeout);
		back.put(Parameter.maxThreadTimeout, maxThreadTimeout);
		back.put(Parameter.actionDelayMin, actionDelayMin);
		back.put(Parameter.actionDelayMax, actionDelayMax);
		back.put(Parameter.count, count);
		back.put(Parameter.offset, offset);
		back.put(Parameter.runsynchron, runsynchron);
		return back;
	}
	@Override
	public void process(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String svnUrl = (String)parameter.get(Parameter.svnUrl);
		final String svnUser = (String)parameter.get(Parameter.svnUser);
		final String svnPass = (String)parameter.get(Parameter.svnPass);
		final String svnProject = (String)parameter.get(Parameter.svnProject);
		final File baseDir =DirectoryClient.getInstance().createDir(DirectoryClient.getInstance().getHome(),"LastTest");
		final File workDir = DirectoryClient.getInstance().createDir(baseDir,"work");
		final File projectDir = DirectoryClient.getInstance().createDir(workDir,svnProject);
		final File recordsDir = DirectoryClient.getInstance().createDir(baseDir,"records");
		final File projectRecordDir = DirectoryClient.getInstance().createDir(recordsDir,svnProject);
		final File tempDir = DirectoryClient.getInstance().createDir(baseDir,"temp");
		final File projectTempDir = DirectoryClient.getInstance().createDir(tempDir,svnProject);
		int pageCount = 0;
		File qmlFile = null;
		final File projectSVNDir = builder.checkoutProject(svnProject, workDir, svnUrl, svnUser, svnPass);
		if ((projectSVNDir != null) && (projectSVNDir.exists())) {
			final List<File> projectFiles = DirectoryClient.getInstance().readDir(projectSVNDir, true);
			for(final File projectFile:projectFiles) {
				final String fileName = projectFile.getName();
				final String filePath = projectFile.getParent();
				if(filePath.endsWith("src/main/resources")) {
					if(fileName.contentEquals("questionnaire_generated.xml")) {
						qmlFile = projectFile;
						break;
					}
				}
			}
		}
		if(qmlFile != null) {
			QuestionnaireDocument doc = QMLClient.getInstance().getDocument(qmlFile);
			final PageType[] pages = ((QuestionnaireDocument) doc).getQuestionnaire().getPageArray();
			if(pages != null)pageCount = pages.length;
		}
		final List<String> tokens = this.retrieveTokens(parameter);
		if(tokens != null) {
			boolean cleaned1 = true;
			if(cleaned1) {
				final boolean simulationDone = true;
				if(simulationDone) {
					final File exportFile = export(parameter,qmlFile,projectTempDir);
				}
			}
			boolean cleaned2 = false;
			if(cleaned2) {
			}
		}
	}
	private boolean simulate(ParameterMap<ABSTRACTPARAMETER, Object> parameter,final int pageCount,final File qmlFile,final List<String> tokens,final File recordDir) throws Exception {
		final Map<Object,Object> properties = new HashMap<Object,Object>();
		properties.put("parameterbase", ((String)parameter.get(Parameter.targetSurvey))+"/special/login.html?zofar_token=");
		properties.put("targetprotocol", (String)parameter.get(Parameter.targetProtocol));
		properties.put("targeturl", (String)parameter.get(Parameter.targetUrl));
		properties.put("targetport", (int)parameter.get(Parameter.targetPort));
		properties.put("recordsdir", recordDir.getAbsolutePath());
		properties.put("packetsize", (int)parameter.get(Parameter.packetSize));
		properties.put("repeatcount", (int)parameter.get(Parameter.repeatCount));
		properties.put("repeattimeout", (int)parameter.get(Parameter.repeatTimeout));
		properties.put("packettimeout", (int)parameter.get(Parameter.packetTimeout));
		properties.put("minthreadtimeout", (int)parameter.get(Parameter.minThreadTimeout));
		properties.put("maxthreadtimeout", (int)parameter.get(Parameter.maxThreadTimeout));
		properties.put("actiondelaymin", (int)parameter.get(Parameter.actionDelayMin));
		properties.put("actiondelaymax", (int)parameter.get(Parameter.actionDelayMax));
		properties.put("count", (int)parameter.get(Parameter.count));
		properties.put("offset", (int)parameter.get(Parameter.offset));
		properties.put("runsynchron", (int)parameter.get(Parameter.runsynchron));
		properties.put("pagecount", pageCount);
		properties.put("qmlfile", qmlFile);
		properties.put("tokenin", tokens);
		properties.put("db_check_location", (String)parameter.get(Parameter.dbLocation));
		properties.put("db_check_port", (int)parameter.get(Parameter.dbPort));
		properties.put("db_check_database", (String)parameter.get(Parameter.dbName));
		properties.put("db_check_user", (String)parameter.get(Parameter.dbUser));
		properties.put("db_check_password", (String)parameter.get(Parameter.dbPass));
		final de.his.zofar.simulator.main.AutomationClient simulator = de.his.zofar.simulator.main.AutomationClient.getInstance();
		try {
			return simulator.execute(properties);
		}
		catch(final Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	private File export(ParameterMap<ABSTRACTPARAMETER, Object> parameter,final File qmlFile,final File projectTempDir) throws Exception {
		final DataService dataService = new DataService();
		final CountingService countingService = new CountingService();
		final FeedbackService feedbackService = new FeedbackService();
		final StatisticService statisticService = new StatisticService(countingService, dataService, feedbackService);
		final de.his.zofar.exporter.main.AutomationClient exporter = new de.his.zofar.exporter.main.AutomationClient(dataService, countingService, feedbackService, statisticService);
		final Map<Object,Object> properties = new HashMap<Object,Object>();
		properties.put("packetSize", "50");
		properties.put("server", (String)parameter.get(Parameter.dbLocation));
		properties.put("port", (int)parameter.get(Parameter.dbPort));
		properties.put("database", (String)parameter.get(Parameter.dbName));
		properties.put("user", (String)parameter.get(Parameter.dbUser));
		properties.put("password", (String)parameter.get(Parameter.dbPass));
		properties.put("qml", qmlFile.getName());
		properties.put("limitLabels", "false");
		properties.put("limitStrings", "false");
		properties.put("exportKey", "testCertificate.pem");
		properties.put("svnProject", (String)parameter.get(Parameter.svnProject));
		properties.put("svnUser", (String)parameter.get(Parameter.svnUser));
		properties.put("svnPass", (String)parameter.get(Parameter.svnPass));
		properties.put("svnServer", (String)parameter.get(Parameter.svnUrl));
		properties.put("svnPathPrefix", "");
		properties.put("svnPathPostfix", "/src/main/resources/");
		properties.put("defaultField", "-9999");
		properties.put("missingNotAnswered", "-9990");
		properties.put("missingNotSeen", "-9992");
		properties.put("missingNotVisited", "-9991");
		properties.put("missingInitGet", "-9995");
		properties.put("noEmpty", "true");
		try {
			return exporter.execute(properties,projectTempDir,System.out,"participant","surveyhistory","surveydata");
		}
		catch(final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private List<String> retrieveTokens(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception{
		final String dbLocation = (String)parameter.get(Parameter.dbLocation);
		final int dbPort = (int)parameter.get(Parameter.dbPort);
		final String dbUser = (String)parameter.get(Parameter.dbUser);
		final String dbPass = (String)parameter.get(Parameter.dbPass);
		final String dbName = (String)parameter.get(Parameter.dbName);	
		PostgresClient postgres = PostgresClient.getInstance();
		Connection connection = null;
		try{
			connection = postgres.getConnection(dbLocation, dbPort+"", dbName, dbUser, dbPass);
		}
		catch(Exception e){
			throw new Exception("cannot establish connection : location="+dbLocation+" port="+dbPort+" dbname="+dbName+" db_user="+dbUser+" db_password="+dbPass,e);
		}
		if(connection != null){
			List<Map<String, String>> fromDB = null;
			try{
				fromDB = postgres.queryDb(connection, "select distinct token from participant;");
			}
			catch(Exception e){
				throw new Exception("cannot query db : location="+dbLocation+" port="+dbPort+" dbname="+dbName+" db_user="+dbUser+" db_password="+dbPass,e);
			}
			if(fromDB != null) {
				final List<String> retrievedTokens = new ArrayList<String>();
				for(final Map<String,String> entry : fromDB) {
					retrievedTokens.add(entry.get("token"));
				}
				return retrievedTokens;
			}
		}
		return null;
	}
	private boolean cleanData(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception{
		final String dbLocation = (String)parameter.get(Parameter.dbLocation);
		final int dbPort = (int)parameter.get(Parameter.dbPort);
		final String dbUser = (String)parameter.get(Parameter.dbUser);
		final String dbPass = (String)parameter.get(Parameter.dbPass);
		final String dbName = (String)parameter.get(Parameter.dbName);		
		boolean flag = false;
		PostgresClient postgres = PostgresClient.getInstance();
		Connection connection = null;
		try{
			connection = postgres.getConnection(dbLocation, dbPort+"", dbName, dbUser, dbPass);
		}
		catch(Exception e){
			throw new Exception("cannot establish connection : location="+dbLocation+" port="+dbPort+" dbname="+dbName+" db_user="+dbUser+" db_password="+dbPass,e);
		}
		if(connection != null){
			try{
				postgresClient.executeDb(connection, "delete from surveydata where variablename not like '%PRELOAD%';");
				postgresClient.executeDb(connection, "delete from surveyhistory where true;");
				flag = true;
			}
			catch(Exception e){
				throw new Exception("cannot query db : location="+dbLocation+" port="+dbPort+" dbname="+dbName+" db_user="+dbUser+" db_password="+dbPass,e);
			}
			finally {
				postgresClient.close(connection);
			}
		}
		return flag;
	}
}
