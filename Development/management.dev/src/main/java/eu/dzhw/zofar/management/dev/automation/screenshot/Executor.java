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
package eu.dzhw.zofar.management.dev.automation.screenshot;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.dev.automation.AbstractExecutor;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
public class Executor extends AbstractExecutor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6647962711067148320L;
	private static final Executor INSTANCE = new Executor();
	private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
	public enum Parameter implements ABSTRACTPARAMETER{
		appLoginChain, survey, svnUser, svnPass, mdm, noVisibleMap, token, workDir, appSSHUser, appSSHPass, svnServer, dbLocation, dbUser, dbPass, dbPort, screenshotDir, appURL;
	};
	private Executor() {
		super();
	}
	public static Executor getInstance() {
		return INSTANCE;
	}
	public ParameterMap<ABSTRACTPARAMETER, Object> getParameterMap(final String survey, final File workDir,final File screenshotDir, final String svnServer, final String svnUser, final String svnPass, final String dbLocation, final String dbPort, final String dbUser, final String dbPass,final String appURL, final String appSSHUser, final String appSSHPass, final Map<String, String> appLoginChain, boolean mdm, boolean noVisibleMap,final String token){
		final ParameterMap<ABSTRACTPARAMETER, Object> back = new ParameterMap<ABSTRACTPARAMETER, Object>();
		back.put(Parameter.survey,survey);
		back.put(Parameter.workDir, workDir);
		back.put(Parameter.screenshotDir,screenshotDir);
		back.put(Parameter.svnServer, svnServer);
		back.put(Parameter.svnUser, svnUser);
		back.put(Parameter.svnPass, svnPass);
		back.put(Parameter.dbLocation, dbLocation);
		back.put(Parameter.dbUser, dbUser);
		back.put(Parameter.dbPass, dbPass);
		back.put(Parameter.dbPort, dbPort);
		back.put(Parameter.appURL,appURL);
		back.put(Parameter.appLoginChain, appLoginChain);
		back.put(Parameter.appSSHUser, appSSHUser);
		back.put(Parameter.appSSHPass, appSSHPass);
		back.put(Parameter.dbLocation,dbLocation);
		back.put(Parameter.dbPort,dbPort);
		back.put(Parameter.dbUser,dbUser);
		back.put(Parameter.dbPass,dbPass);
		back.put(Parameter.mdm,mdm);
		back.put(Parameter.noVisibleMap,noVisibleMap);
		back.put(Parameter.token,token);
		return back;
	}
	public void process(ParameterMap<ABSTRACTPARAMETER, Object> parameter,final ArrayList<java.awt.Dimension> dimensions,final boolean maximizeHeight,final List<String> languages) throws Exception {
		final String survey = (String)parameter.get(Parameter.survey);
		final File workDir = (File)parameter.get(Parameter.workDir);
		final File screenshotDir = (File)parameter.get(Parameter.screenshotDir);
		final String svnServer = (String)parameter.get(Parameter.svnServer);
		final String svnUser = (String)parameter.get(Parameter.svnUser);
		final String svnPass = (String)parameter.get(Parameter.svnPass);
		final String serverUrl = (String)parameter.get(Parameter.appURL);
		final String serverUser = (String)parameter.get(Parameter.appSSHUser);
		final String serverPass = (String)parameter.get(Parameter.appSSHPass);
		final String dbLocation = (String)parameter.get(Parameter.dbLocation);
		final String dbPort = (String)parameter.get(Parameter.dbPort);
		final String dbUser = (String)parameter.get(Parameter.dbUser);
		final String dbPass = (String)parameter.get(Parameter.dbPass);
		final boolean mdm = (Boolean)parameter.get(Parameter.mdm);
		final boolean noVisibleMap = (Boolean)parameter.get(Parameter.noVisibleMap);
		final String token = (String)parameter.get(Parameter.token);
		final Map<String, String> appLoginChain = (Map<String, String>) parameter.get(Parameter.appLoginChain);
		File projectDir = builder.getProject(survey, workDir);
		if((projectDir != null)&&(projectDir.exists()))DirectoryClient.getInstance().deleteDir(workDir, survey);
		projectDir = builder.checkoutProject(survey, workDir, svnServer+"/svn/hiob/tags/surveys", svnUser, svnPass);
		try {
			final String dbName = ("screenshot_"+projectDir.getName()).toLowerCase().replace('.', '_').replace('-', '_');
			final File modifiedProjectDir = builder.buildExistingProject(projectDir, dbLocation,dbPort, dbName,dbName+".session",dbUser, dbPass, false, true, false, true, true,mdm,noVisibleMap, "forward,backward,same", "TOKEN", true,false,false, svnServer, svnUser, svnPass, true,token);
			final File warFile = mavenClient.doCleanInstall(modifiedProjectDir);
			System.out.println("war : ("+(warFile != null)+")("+(warFile.exists())+") "+warFile.getAbsolutePath());
			if ((warFile != null) && (warFile.exists())) {
				final String surveyName = warFile.getName().replaceAll(Pattern.quote(".war"), "");
				final Connection dbConn = postgresClient.getMaintenanceConnection(dbLocation, dbPort, dbUser, dbPass);
				if(dbConn != null){
					if(postgresClient.existDB(dbConn, dbName)){
						postgresClient.disconnectOtherFromDB(dbConn, dbName);
						postgresClient.dropDB(dbConn, dbName);
					}
					postgresClient.createDB(dbConn, dbName);
				}
				if(tomcatManager.isInstalled("/" + surveyName, serverUrl, appLoginChain)){
					LOGGER.info("Application already installed on Tomcat");
					tomcatManager.undeploy(serverUrl, appLoginChain, "/"+surveyName);
				}
				if (tomcatManager.deploy(serverUrl, serverUser, serverPass, appLoginChain, surveyName, warFile)) {
					LOGGER.info("" + survey + " deployed ");
					takeScreenshots(serverUrl,surveyName,token,screenshotDir,dimensions,maximizeHeight,languages);
					if (tomcatManager.stop(serverUrl, appLoginChain, surveyName)) {
						LOGGER.info("" + survey + " stopped ");
						if (tomcatManager.undeploy(serverUrl, appLoginChain, surveyName)) {
							LOGGER.info("" + survey + " undeployed ");
						} else {
							LOGGER.info("" + survey + " not undeployed ");
						}
					} else {
						LOGGER.info("" + survey + " not stopped ");
					}
				} else {
					LOGGER.info("" + survey + " not deployed ");
				}
				if(dbConn != null)postgresClient.dropDB(dbConn, dbName);
				postgresClient.close(dbConn);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	@Deprecated
	public void takeScreenshotsMDM(final String serverUrl,final String surveyName,final String token,final File screenshotDir,final ArrayList<java.awt.Dimension> dimensions,final boolean maximizeHeight,final List<String> languages){
		ScreenshotGenerator generator = ScreenshotGenerator.getInstance();
		try {
			final String query = serverUrl+"/"+surveyName+"/special/login.html?zofar_token="+token;
			LOGGER.info("screenshotQuery : "+query);
			DirectoryClient directoryClient = DirectoryClient.getInstance();
			File surveyScreenshotDir = directoryClient.createDir(screenshotDir,surveyName);
			directoryClient.cleanDirectory(surveyScreenshotDir);
			generator.spiderSurveyForMDM(query, screenshotDir,dimensions,maximizeHeight,languages);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void takeScreenshots(final String serverUrl,final String surveyName,final String token,final File screenshotDir,final ArrayList<java.awt.Dimension> dimensions,final boolean maximizeHeight,final List<String> languages){
		ScreenshotGenerator generator = ScreenshotGenerator.getInstance();
		try {
			final String query = serverUrl+"/"+surveyName+"/special/login.html?zofar_token="+token;
			LOGGER.info("screenshotQuery : "+query);
			DirectoryClient directoryClient = DirectoryClient.getInstance();
			File surveyScreenshotDir = directoryClient.createDir(screenshotDir,surveyName);
			directoryClient.cleanDirectory(surveyScreenshotDir);
			generator.spiderSurvey(query, screenshotDir,dimensions,maximizeHeight,languages);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void process(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final List<String> languages = new ArrayList<String>();
		languages.add("de");
		this.process(parameter,null,false,languages);
	}
}
