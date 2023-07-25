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
package eu.dzhw.zofar.management.dev.automation.createProject;
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
import eu.dzhw.zofar.management.comm.continuousintegration.jenkins.JenkinsClient;
import eu.dzhw.zofar.management.comm.json.JSONClient;
import eu.dzhw.zofar.management.dev.automation.AbstractExecutor;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.system.ConfigurationUtils;
public class Executor extends AbstractExecutor {
	private static final long serialVersionUID = -7369649930100173777L;
	private static final Executor INSTANCE = new Executor();
	private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
	public enum Parameter implements ABSTRACTPARAMETER {
		survey, addon, responsive, svnServer, svnUser, svnPass, dbLocation, dbPort, dbUser, dbPass, workDir,
		jenkinsServer, jenkinsUser, jenkinsPass, monitoringServerProtocol, monitoringServerURL, monitoringServerUser,
		monitoringServerPass;
	};
	private Executor() {
		super();
	}
	public static Executor getInstance() {
		return INSTANCE;
	}
	public ParameterMap<ABSTRACTPARAMETER, Object> getParameterMap(final String survey, final String addon,
			final boolean responsive, final File workDir, final String svnServer, final String svnUser,
			final String svnPass, final String jenkinsServer, final String jenkinsUser, final String jenkinsPass,
			final String dbLocation, final String dbPort, final String dbUser, final String dbPass,
			final String monitoringServerProtocol, final String monitoringServerURL, final String monitoringServerUser,
			final String monitoringServerPass) {
		final ParameterMap<ABSTRACTPARAMETER, Object> back = new ParameterMap<ABSTRACTPARAMETER, Object>();
		back.put(Parameter.survey, survey);
		back.put(Parameter.addon, addon);
		back.put(Parameter.responsive, responsive);
		back.put(Parameter.workDir, workDir);
		back.put(Parameter.svnServer, svnServer);
		back.put(Parameter.svnUser, svnUser);
		back.put(Parameter.svnPass, svnPass);
		back.put(Parameter.jenkinsServer, jenkinsServer);
		back.put(Parameter.jenkinsUser, jenkinsUser);
		back.put(Parameter.jenkinsPass, jenkinsPass);
		back.put(Parameter.dbLocation, dbLocation);
		back.put(Parameter.dbUser, dbUser);
		back.put(Parameter.dbPass, dbPass);
		back.put(Parameter.dbPort, dbPort);
		back.put(Parameter.monitoringServerProtocol, monitoringServerProtocol);
		back.put(Parameter.monitoringServerURL, monitoringServerURL);
		back.put(Parameter.monitoringServerUser, monitoringServerUser);
		back.put(Parameter.monitoringServerPass, monitoringServerPass);
		return back;
	}
	@Override
	public void process(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String survey = (String) parameter.get(Parameter.survey);
		final String addon = (String) parameter.get(Parameter.addon);
		final boolean resonsive = (boolean) parameter.get(Parameter.responsive);
		final String svnServer = (String) parameter.get(Parameter.svnServer);
		final String svnUrl = svnServer + "/svn/hiob/tags/surveys";
		final String svnUser = (String) parameter.get(Parameter.svnUser);
		final String svnPass = (String) parameter.get(Parameter.svnPass);
		final File workDir = (File) parameter.get(Parameter.workDir);
		if (survey != null) {
			File project = null;
			if (resonsive)
				project = builder.createProject(survey, workDir, svnUrl, svnUser, svnPass);
			else
				project = builder.createNonResponsiveProject(survey, workDir, svnUrl, svnUser, svnPass);
			final File clone = DirectoryClient.getInstance().createDir(project.getParentFile(),
					project.getName() + "_clean");
			DirectoryClient.getInstance().copyDirectory(project, clone);
			removeIrrelevantRelevantFiles(clone);
			final File dbFile = new File(clone.getAbsolutePath() + "/src/main/resources/survey/database.properties");
			modifyDBSettings(dbFile);
			final File sessionSecFile = new File(clone.getAbsolutePath() + "/src/main/resources/session-security.cfg.xml");
			modifySessionSecuritySettings(sessionSecFile);
			builder.commitProject(survey, clone, svnUrl, svnUser, svnPass);
			final String dbName = "build_" + survey;
			File preloadFile = new File(project.getAbsolutePath() + "/src/main/resources/survey/preloads_postgres.sql");
			installToDB(preloadFile, dbName, parameter);
			installToJenkins(survey, dbName, parameter);
			installMonitoring(dbName, parameter);
		}
		if ((addon != null) && (!addon.equals(""))) {
			final File addon_project = builder.createProject(addon, workDir, svnUrl, svnUser, svnPass);
			final File clone = DirectoryClient.getInstance().createDir(addon_project.getParentFile(),
					addon_project.getName() + "_clean");
			DirectoryClient.getInstance().copyDirectory(addon_project, clone);
			removeIrrelevantRelevantFiles(clone);
			final File dbFile = new File(clone.getAbsolutePath() + "/src/main/resources/survey/database.properties");
			modifyDBSettings(dbFile);
			final File sessionSecFile = new File(clone.getAbsolutePath() + "/src/main/resources/session-security.cfg.xml");
			modifySessionSecuritySettings(sessionSecFile);
			builder.commitProject(addon, clone, svnUrl, svnUser, svnPass);
			final String dbName = "build_" + addon;
			File preloadFile = new File(clone.getAbsolutePath() + "/src/main/resources/survey/preloads_postgres.sql");
			installToDB(preloadFile, dbName , parameter);
			installToJenkins(addon, dbName, parameter);
			installMonitoring(dbName, parameter);
		}
	}
	private void removeIrrelevantRelevantFiles(final File current) {
		final List<File> files = DirectoryClient.getInstance().readDir(current, null);
		for (final File file : files) {
			final String fileName = file.getName();
			boolean flag = false;
			if (fileName.startsWith("."))
				flag = true;
			if (fileName.equals("target"))
				flag = true;
			if (flag) {
				System.out.println("Delete : " + file.getAbsolutePath());
				try {
					FileUtils.forceDelete(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (file.isDirectory())
					removeIrrelevantRelevantFiles(file);
				else if (file.isFile()) {
				}
			}
		}
	}
	private File modifyDBSettings(final File settings) {
		final ConfigurationUtils conf = ConfigurationUtils.getInstance();
		final Properties props = conf.getConfigurationFromFileSystem(settings.getAbsolutePath());
		props.setProperty("jdbc.username", "${db.username}");
		props.setProperty("jdbc.password", "${db.password}");
		props.setProperty("jdbc.url", "jdbc:postgresql:
		props.setProperty("connectionPool.maxSize", "10");
		props.setProperty("connectionPool.maxStatements", "0");
		props.setProperty("connectionPool.minSize", "1");
		props.setProperty("connectionPool.testConnectionOnCheckout", "true");
		props.setProperty("connectionPool.idleConnectionTestPeriod", "300");
		props.setProperty("connectionPool.preferredTestQuery", "select 1;");
		if (conf.saveConfiguration(props, settings.getAbsolutePath())) {
		} else {
			System.err.println("cannot modify " + settings);
		}
		return settings;
	}
	private File modifySessionSecuritySettings(final File settings) throws IOException {
		final FileClient file = FileClient.getInstance();
		final File templateFile = file.getResource("builder/session-securityTemplate.txt");
		if ((templateFile != null) && (templateFile.exists())) {
			final String content = file.readAsString(templateFile);
			file.writeToFile(settings, content, false);
		}
		return settings;
	}
	private boolean installToDB(final File preloads, final String dbName,
			ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final String dbPort = (String) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final Connection dbConn = postgresClient.getMaintenanceConnection(dbLocation, dbPort, dbUser, dbPass);
		if (dbConn != null) {
			if (postgresClient.existDB(dbConn, dbName)) {
				LOGGER.info("DB {} already exist.", dbName);
			} else {
				postgresClient.createDB(dbConn, dbName);
			}
			if (postgresClient.existDB(dbConn, dbName + ".session")) {
				LOGGER.info("DB {} already exist.", dbName + ".session");
			} else {
				postgresClient.createDB(dbConn, dbName + ".session");
			}
			postgresClient.close(dbConn);
		}
		if ((preloads != null) && (preloads.exists())) {
			final String content = FileClient.getInstance().readAsString(preloads);
			if ((content != null) && (!content.equals(""))) {
				final Connection preloadConn = postgresClient.getConnection(dbLocation, dbPort, dbName, dbUser, dbPass);
				postgresClient.executeDb(preloadConn,
						"CREATE TABLE participant (id bigint NOT NULL,version integer NOT NULL,password character varying(255) NOT NULL,token character varying(255) NOT NULL,CONSTRAINT participant_pkey PRIMARY KEY (id),CONSTRAINT participant_token_key UNIQUE (token)) WITH (OIDS=FALSE); ALTER TABLE participant OWNER TO postgres;");
				postgresClient.executeDb(preloadConn, "TRUNCATE participant CASCADE;");
				postgresClient.executeDb(preloadConn,
						"CREATE TABLE surveydata ( id bigint NOT NULL, version integer NOT NULL, value character varying(2000) NOT NULL, variablename character varying(255) NOT NULL, participant_id bigint NOT NULL, CONSTRAINT surveydata_pkey PRIMARY KEY (id), CONSTRAINT fkd7db5a04a3f7481e FOREIGN KEY (participant_id) REFERENCES participant (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION, CONSTRAINT surveydata_participant_id_variablename_key UNIQUE (participant_id, variablename) ) WITH ( OIDS=FALSE ); ALTER TABLE surveydata OWNER TO postgres;");
				postgresClient.executeDb(preloadConn, "TRUNCATE surveydata CASCADE;");
				postgresClient.close(preloadConn);
			} else
				System.err.println("Preload Content empty");
		}
		return false;
	}
	private boolean installToJenkins(final String surveyName, final String dbName,
			ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String jenkinsServer = (String) parameter.get(Parameter.jenkinsServer);
		final String jenkinsUser = (String) parameter.get(Parameter.jenkinsUser);
		final String jenkinsPass = (String) parameter.get(Parameter.jenkinsPass);
		final String svnServer = (String) parameter.get(Parameter.svnServer);
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final String dbPort = (String) parameter.get(Parameter.dbPort);
		final JenkinsServer jenkins = JenkinsClient.getInstance().getServer(jenkinsServer, jenkinsUser, jenkinsPass);
		if (jenkins != null) {
			final JobWithDetails job = JenkinsClient.getInstance().createJob(jenkins, surveyName, svnServer,
					"/svn/hiob/tags/surveys", dbLocation, dbPort, dbName, dbUser, dbPass);
			return (job != null);
		}
		return false;
	}
	private void installMonitoring(final String dbName, ParameterMap<ABSTRACTPARAMETER, Object> parameter)
			throws Exception {
		final String survey = (String) parameter.get(Parameter.survey);
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final String dbPort = (String) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final String svnServer = (String) parameter.get(Parameter.svnServer);
		final String svnUser = (String) parameter.get(Parameter.svnUser);
		final String svnPass = (String) parameter.get(Parameter.svnPass);
		final String stamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(System.currentTimeMillis());
		final String configName = "configuration_" + survey + "_" + stamp;
		final JSONClient jsonClient = JSONClient.getInstance();
		final Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("id", configName);
		data.put("type", "ALL");
		data.put("server", dbLocation);
		data.put("port", dbPort);
		data.put("database", dbName);
		data.put("user", dbUser);
		data.put("password", dbPass);
		data.put("svnProject", survey);
		data.put("svnUser", svnUser);
		data.put("svnPass", svnPass);
		data.put("svnServer", svnServer);
		data.put("svnPathPrefix", "svn/hiob/tags/surveys/");
		data.put("svnPathPostfix", "/src/main/resources/");
		data.put("packetSize", "50");
		data.put("qml", "questionnaire_generated.xml");
		data.put("limitLabels", "false");
		data.put("limitStrings", "false");
		data.put("exportKey", "testCertificate.pem");
		data.put("missingNotAnswered", "-9990");
		data.put("missingNotSeen", "-9992");
		data.put("missingNotVisited", "-9991");
		data.put("missingInitGet", "-9995");
		data.put("variables", new ArrayList<String>());
		final String url = parameter.get(Parameter.monitoringServerProtocol)+":
				+ parameter.get(Parameter.monitoringServerPass) + "@" + parameter.get(Parameter.monitoringServerURL);
		jsonClient.transferToRest(url, jsonClient.createObject(data));
	}
}
