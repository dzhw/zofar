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
package eu.dzhw.zofar.management.dev.automation.deploy;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jcraft.jsch.JSchException;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import eu.dzhw.zofar.management.comm.db.postgresql.PostgresClient;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
import eu.dzhw.zofar.management.comm.ssh.SSHClient;
import eu.dzhw.zofar.management.dev.automation.AbstractExecutor;
import eu.dzhw.zofar.management.dev.qml.QMLClient;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.objects.CollectionClient;
import eu.dzhw.zofar.management.utils.string.ReplaceClient;
public class Executor extends AbstractExecutor {
	private static final long serialVersionUID = 826392989658757721L;
	private static final Executor INSTANCE = new Executor();
	private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
	public enum Parameter implements ABSTRACTPARAMETER {
		survey, addon, workDir, clusterMap, clusterNames, svnServer, svnUser, svnPass, dbLocation, dbPort, dbUser, dbPass, lbLocation, lbSSHUser, lbSSHPass, appSSHUser, appSSHPass, monitorLocation, monitorSSHUser, monitorSSHPass, appLoginChain, invitationURL;
	};
	private Executor() {
		super();
	}
	public static Executor getInstance() {
		return INSTANCE;
	}
	public ParameterMap<ABSTRACTPARAMETER, Object> getParameterMap(final String survey, final String addon, final File workDir, final String svnServer, final String svnUser, final String svnPass, final String dbLocation, final String dbPort, final String dbUser, final String dbPass, final Map<String, List<String>> clusterMap, final List<String> clusterNames, final String lbLocation, final String lbSSHUser, final String lbSSHPass, final String monitorLocation, final String monitorSSHUser, final String monitorSSHPass, final String appSSHUser, final String appSSHPass, final Map<String, String> appLoginChain, final String invitationURL) {
		final ParameterMap<ABSTRACTPARAMETER, Object> back = new ParameterMap<ABSTRACTPARAMETER, Object>();
		back.put(Parameter.survey, survey);
		back.put(Parameter.addon, addon);
		back.put(Parameter.invitationURL, invitationURL);
		back.put(Parameter.workDir, workDir);
		back.put(Parameter.clusterMap, clusterMap);
		back.put(Parameter.clusterNames, clusterNames);
		back.put(Parameter.svnServer, svnServer);
		back.put(Parameter.svnUser, svnUser);
		back.put(Parameter.svnPass, svnPass);
		back.put(Parameter.dbLocation, dbLocation);
		back.put(Parameter.dbUser, dbUser);
		back.put(Parameter.dbPass, dbPass);
		back.put(Parameter.dbPort, dbPort);
		back.put(Parameter.lbLocation, lbLocation);
		back.put(Parameter.lbSSHUser, lbSSHUser);
		back.put(Parameter.lbSSHPass, lbSSHPass);
		back.put(Parameter.appLoginChain, appLoginChain);
		back.put(Parameter.appSSHUser, appSSHUser);
		back.put(Parameter.appSSHPass, appSSHPass);
		back.put(Parameter.monitorLocation, monitorLocation);
		back.put(Parameter.monitorSSHUser, monitorSSHUser);
		back.put(Parameter.monitorSSHPass, monitorSSHPass);
		return back;
	}
	@Override
	public void process(ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String survey = (String) parameter.get(Parameter.survey);
		final String addon = (String) parameter.get(Parameter.addon);
		String invitationURL = (String) parameter.get(Parameter.invitationURL);
		final File workDir = (File) parameter.get(Parameter.workDir);
		final String svnServer = (String) parameter.get(Parameter.svnServer);
		final String svnUrl = svnServer + "/svn/hiob/tags/surveys";
		final String svnUser = (String) parameter.get(Parameter.svnUser);
		final String svnPass = (String) parameter.get(Parameter.svnPass);
		Map<String, List<String>> clusterMap = (Map<String, List<String>>) parameter.get(Parameter.clusterMap);
		List<String> clusterNames = (List<String>) parameter.get(Parameter.clusterNames);
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final String dbPort = (String) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final Map<String, String> appLoginChain = (Map<String, String>) parameter.get(Parameter.appLoginChain);
		final String app_sshUser = (String) parameter.get(Parameter.appSSHUser);
		final String app_sshPass = (String) parameter.get(Parameter.appSSHPass);
		final String devwiki_server = (String) parameter.get(Parameter.monitorLocation);
		final String devwiki_sshUser = (String) parameter.get(Parameter.monitorSSHUser);
		final String devwiki_sshPass = (String) parameter.get(Parameter.monitorSSHPass);
		final String lb_server = (String) parameter.get(Parameter.lbLocation);
		final String lb_sshUser = (String) parameter.get(Parameter.lbSSHUser);
		final String lb_sshPass = (String) parameter.get(Parameter.lbSSHPass);
		final String mainDB = ("prod_" + survey).toLowerCase().replace('.', '_').replace('-', '_');
		final String addonDB = ("prod_" + survey).toLowerCase().replace('.', '_').replace('-', '_') + "_addon";
		File mainPreloads = null;
		File mainQML = null;
		final File mainProjectDir = builder.checkoutProject(survey, workDir, svnUrl, svnUser, svnPass);
		if ((mainProjectDir != null) && (mainProjectDir.exists())) {
			mainPreloads = new File(mainProjectDir.getAbsolutePath() + "/src/main/resources/survey/preloads_postgres.sql");
			mainQML = new File(mainProjectDir.getAbsolutePath() + "/src/main/resources/questionnaire.xml");
			boolean overrideNav = false;
			boolean cutHistory = true;
			boolean pretest = false;
			boolean linearNavigation = false;
			boolean overrideRendering = false;
			boolean mdm = false;
			boolean noVisibleMap = false;
			String saveMode = "forward,backward,same";
			String login = "TOKEN";
			boolean preloadOnStart = false;
			boolean record = false;
			final boolean overrideCommit = true;
			File mainWar = this.build(survey, mainProjectDir, mainDB, dbLocation, dbPort, dbUser, dbPass, svnUrl, svnUser, svnPass, overrideNav, cutHistory, pretest, linearNavigation, overrideRendering, mdm, noVisibleMap, saveMode, login, preloadOnStart, record, overrideCommit,true);
			if ((mainWar != null) && (mainWar.exists())) {
				this.install(survey, mainDB, mainWar, mainPreloads, appLoginChain, app_sshUser, app_sshPass, devwiki_server, devwiki_sshUser, devwiki_sshPass, lb_server, lb_sshUser, lb_sshPass, clusterMap, clusterNames, parameter);
			}
		}
		if (addon != null) {
			final File addonProjectDir = builder.checkoutProject(addon, workDir, svnUrl, svnUser, svnPass);
			if ((addonProjectDir != null) && (addonProjectDir.exists())) {
				boolean overrideNav = false;
				boolean cutHistory = true;
				boolean pretest = false;
				boolean linearNavigation = false;
				boolean overrideRendering = false;
				boolean mdm = false;
				boolean noVisibleMap = false;
				String saveMode = "forward,backward,same";
				String login = "TOKEN";
				boolean preloadOnStart = false;
				boolean record = false;
				final boolean overrideCommit = true;
				File war = this.build(addon, addonProjectDir, addonDB, dbLocation, dbPort, dbUser, dbPass, svnUrl, svnUser, svnPass, overrideNav, cutHistory, pretest, linearNavigation, overrideRendering, mdm, noVisibleMap, saveMode, login, preloadOnStart, record, overrideCommit,true);
				if ((war != null) && (war.exists())) {
					File preloadFile = new File(addonProjectDir.getAbsolutePath() + "/src/main/resources/survey/preloads_postgres.sql");
					this.install(addon, addonDB, war, preloadFile, appLoginChain, app_sshUser, app_sshPass, devwiki_server, devwiki_sshUser, devwiki_sshPass, lb_server, lb_sshUser, lb_sshPass, clusterMap, clusterNames, parameter);
				}
			}
		}
		if(!this.test(survey, addon, mainDB, addonDB, mainQML, invitationURL, parameter)){
			System.err.println("Post Deployment Tests failed");
		}
		return;
	}
	private File build(final String survey, final File projectDir, final String dbName, final String dbLocation, final String dbPort, final String dbUser, final String dbPass, final String svnUrl, final String svnUser, final String svnPass, final boolean overrideNav, final boolean cutHistory, final boolean pretest, final boolean linearNavigation, final boolean overrideRendering, final boolean mdm, final boolean noVisibleMap, final String saveMode, final String login, final boolean preloadOnStart, final boolean record, final boolean overrideCommit, final boolean cluster) throws Exception {
		final File modifiedProjectDir = builder.buildExistingProject(survey, projectDir, dbLocation,dbPort, dbName, dbName + ".session", dbUser, dbPass, overrideNav, cutHistory, pretest, linearNavigation, overrideRendering, mdm, noVisibleMap, saveMode, login, preloadOnStart, record,cluster, svnUrl, svnUser, svnPass, overrideCommit, null);
		File qmlFile = new File(modifiedProjectDir.getAbsolutePath() + "/src/main/resources/questionnaire.xml");
		verifiyQml(qmlFile);
		File templateFile = new File(modifiedProjectDir.getAbsolutePath() + "/src/main/webapp/template/survey.xhtml");
		verifiyTemplate(templateFile);
		File warFile = null;
		warFile = mavenClient.doCleanInstall(modifiedProjectDir);
		return warFile;
	}
	private void install(final String survey, final String dbName, final File war, final File preloads, final Map<String, String> app_logins, final String app_sshUser, final String app_sshPass, final String devwiki_server, final String devwiki_sshUser, final String devwiki_sshPass, final String lb_server, final String lb_sshUser, final String lb_sshPass, final Map<String, List<String>> clusterMap, final List<String> clusterNames, ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		System.out.println("War : " + war.getAbsolutePath());
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
		if ((clusterNames != null) && (!clusterNames.isEmpty())) {
			for (final String clusterName : clusterNames) {
				final List<String> serverList = clusterMap.get(clusterName);
				if ((serverList == null) || (serverList.isEmpty()))
					System.err.println("No Server in List for Cluster " + clusterName);
				else {
					for (final String serverUrlFromList : serverList) {
						if(!this.installToTomcat(survey, serverUrlFromList, app_logins, app_sshUser, app_sshPass, war,true))throw new Exception("Deployment failed on "+serverUrlFromList+". Deployment aborted");
					}
					if ((preloads != null) && (preloads.exists())) {
						final String content = FileClient.getInstance().readAsString(preloads);
						if ((content != null) && (!content.equals(""))) {
							final Connection preloadConn = postgresClient.getConnection(dbLocation, dbPort, dbName, dbUser, dbPass);
							postgresClient.executeDb(preloadConn, "TRUNCATE participant CASCADE;");
							postgresClient.executeDb(preloadConn, "TRUNCATE surveydata CASCADE;");
							postgresClient.executeDb(preloadConn, "TRUNCATE surveyhistory CASCADE;");
							postgresClient.executeDb(preloadConn, content);
							postgresClient.close(preloadConn);
						} else
							System.err.println("Preload Content empty");
					}
					this.installToLoadBalancer(survey, clusterName, lb_server, lb_sshUser, lb_sshPass);
				}
			}
			this.installCockpit(devwiki_server, devwiki_sshUser, devwiki_sshPass, dbName, parameter);
			this.installExporter(devwiki_server, devwiki_sshUser, devwiki_sshPass, dbName, parameter);
		} else {
			System.err.println("No Clusters defined to deploy to");
		}
	}
	private boolean test(final String survey, final String addon, final String dbName, final String addonDBName, final File mainQML, final String invitationURL, ParameterMap<ABSTRACTPARAMETER, Object> parameter) {
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final String dbPort = (String) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		boolean back = true;
		PostgresClient postgresClient = PostgresClient.getInstance();
		Connection mainConn = null;
		Connection addonConn = null;
		try {
			QMLClient qmlClient = QMLClient.getInstance();
			QuestionnaireDocument doc = qmlClient.getDocument(mainQML);
			if (doc != null) {
				ReplaceClient replacer = ReplaceClient.getInstance();
				mainConn = postgresClient.getConnection(dbLocation, dbPort, dbName, dbUser, dbPass);
				addonConn = postgresClient.getConnection(dbLocation, dbPort, addonDBName, dbUser, dbPass);
				final String pattern = addon + "/special/login.html?zofar_token=";
				final Map<String, List<String>> lostTokens = new HashMap<String, List<String>>();
				final int pageCount = doc.getQuestionnaire().sizeOfPageArray();
				for (int index = pageCount - 1; index >= 0; index--) {
					final PageType page = doc.getQuestionnaire().getPageArray(index);
					final int addonLinkIndex = page.xmlText().indexOf(pattern);
					if (addonLinkIndex > -1) {
						int endIndex = page.xmlText().indexOf(",", addonLinkIndex);
						if (endIndex == -1)
							endIndex = page.xmlText().indexOf(" ", addonLinkIndex);
						if (endIndex > addonLinkIndex) {
							final String relevant = page.xmlText().substring(addonLinkIndex + pattern.length(), endIndex);
							final List<String> found = replacer.findInString("PRELOAD([A-Za-z0-9]*)", relevant);
							if ((found != null) && (!found.isEmpty())) {
								for (final String preload : found) {
									if (mainConn != null) {
										List<Map<String, String>> result = postgresClient.queryDb(mainConn, "Select value from surveydata where variablename='" + preload + "';");
										List<Object> preloadTokens = new ArrayList<Object>();
										if (result != null) {
											for (final Map<String, String> item : result) {
												preloadTokens.add(item.get("value"));
											}
										}
										if (!preloadTokens.isEmpty()) {
											final String searchArray = "'" + CollectionClient.getInstance().implode(preloadTokens.toArray(), "','") + "'";
											List<Map<String, String>> addonResult = postgresClient.queryDb(addonConn, "Select token from participant where token NOT IN (" + searchArray + ");");
											if (addonResult != null) {
												final List<String> lostTokenList = new ArrayList<String>();
												for (final Map<String, String> item : addonResult) {
													lostTokenList.add(item.get("token"));
												}
												if (!lostTokenList.isEmpty()) {
													CollectionClient.getInstance().addListToMap(lostTokens, preload, lostTokenList);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				if (!lostTokens.isEmpty()) {
					System.err.println("Found Preload-Tokens for Addon which are not registered as participants in Addon : " + lostTokens);
					back = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mainConn != null)
				postgresClient.close(mainConn);
			if (addonConn != null)
				postgresClient.close(addonConn);
		}
		return back;
	}
	private void verifiyQml(final File file) throws Exception {
		QMLClient qmlClient = QMLClient.getInstance();
		QuestionnaireDocument doc = qmlClient.getDocument(file);
	}
	private void verifiyTemplate(final File file) throws Exception {
		HTTPClient httpClient = HTTPClient.getInstance();
		Page page = httpClient.loadPage(file);
		if ((page != null) && ((HtmlPage.class).isAssignableFrom(page.getClass()))) {
			List<HtmlElement> nodes = (List<HtmlElement>) ((HtmlPage) page).getByXPath("//h:form");
			if (nodes != null) {
				for (final HtmlElement node : nodes) {
					boolean defaultExist = false;
					if (node.hasChildNodes()) {
						final Node firstChild = node.getFirstChild();
						if ((firstChild != null) && (firstChild.getNodeName().equals("zo:default")))
							defaultExist = true;
					}
					if (!defaultExist) {
						DomElement createdElement = ((HtmlPage) page).createElement("zo:default");
						createdElement.setAttribute("isLastPage", "#{lastPage}");
						node.insertBefore(createdElement, node.getFirstChild());
					}
					break;
				}
			}
			File modifiedFile = httpClient.savePage((HtmlPage) page, file.getParentFile());
			if ((modifiedFile != null) && (modifiedFile.exists())) {
				final FileClient fileClient = FileClient.getInstance();
				final String[] fileName = file.getName().split(Pattern.quote("."));
				File backup = fileClient.createOrGetFile(fileName[0] + "_backup", "." + fileName[1], file.getParentFile());
				fileClient.copyFile(file, backup);
				fileClient.copyFile(modifiedFile, file);
			}
		}
	}
	private boolean installToTomcat(final String surveyName, final String serverUrl, final Map<String, String> app_logins, final String sshUser, final String sshPass, final File warFile,final boolean service) throws Exception {
		if(service) {
			String serverName = serverUrl;
			if(serverName.indexOf("
			if(serverName.indexOf(":") > -1)serverName = serverName.substring(0,serverName.indexOf(":"));
			return this.installToTomcatService(serverName,  sshUser, sshPass, warFile);
		}
		else return this.installToTomcatREST(surveyName, serverUrl, app_logins, sshUser, sshPass, warFile);
	}
	private boolean installToTomcatREST(final String surveyName, final String serverUrl, final Map<String, String> app_logins, final String sshUser, final String sshPass, final File warFile) throws Exception {
		if (tomcatManager.isInstalled("/" + surveyName, serverUrl, app_logins)) {
			LOGGER.info("Application already installed on Tomcat");
			tomcatManager.undeploy(serverUrl, app_logins, "/" + surveyName);
		}
		if (tomcatManager.deploy(serverUrl, sshUser, sshPass, app_logins, surveyName, warFile)) {
			LOGGER.info("" + surveyName + " deployed ");
			return true;
		} else {
			LOGGER.error("" + surveyName + " not deployed ");
		}
		return false;
	}
	private boolean installToTomcatService(final String server, final String sshUser, final String sshPass, final File warFile) throws Exception {
		SSHClient ssh = SSHClient.getInstance();
		final String webappsDir = "/var/lib/tomcat7/webapps";
		try {
			ssh.scpTo(server, sshUser, sshPass, warFile, "/home/####");
			ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "service tomcat7 stop");
			ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "rm -r -f " + webappsDir + "/" + FileClient.getInstance().getNameWithoutSuffix(warFile) + " ");
			ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "rm -r -f " + webappsDir + "/" + warFile.getName() + " ");
			ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "mv /home/####/" + warFile.getName() + " "	+ webappsDir + "/" + warFile.getName() + " ");
			ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "service tomcat7 start");
			return true;
		} catch (Exception e) {
			LOGGER.error("Error in installToTomcatService", e);
			System.err.println("Sudo cmd failed in installToTomcatService "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	private void installToLoadBalancer(final String surveyName, final String clusterName, final String server, final String sshUser, final String sshPass) throws Exception {
		final String fromFileName = "00-vmzofarprod-lb####-http";
		final String configDir = "/etc/apache2/sites-available/";
		final String toFileName = "00-vmzofarprod-lb####-http_auto";
		final String fileSuffix = ".conf";
		SSHClient ssh = SSHClient.getInstance();
		DirectoryClient dir = DirectoryClient.getInstance();
		FileClient file = FileClient.getInstance();
		final File config = file.copyFile(ssh.scpFrom(server, sshUser, sshPass, configDir + fromFileName + fileSuffix), file.createOrGetFile(toFileName, fileSuffix, dir.getTemp()));
		if (config != null) {
			String content = file.readAsString(config);
			final String toAdd = "JkMount /" + surveyName + "*  " + clusterName;
			boolean exists = (content.indexOf(toAdd) > -1);
			if (!exists) {
				final int endTagIndex = content.indexOf("</VirtualHost>");
				if (endTagIndex > -1) {
					String commentedAdd = "\n###### Automatically added at " + SimpleDateFormat.getInstance().format(new Date()) + " #####\n" + toAdd + "\n###### ##### #####\n";
					content = content.substring(0, endTagIndex) + commentedAdd + content.substring(endTagIndex);
				}
				try {
					file.writeToFile(config, content, false);
					ssh.scpTo(server, sshUser, sshPass, config, "/home/####");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "mv /home/####/" + toFileName + fileSuffix + " " + configDir + "/" + toFileName + fileSuffix + " ");
					final String stamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(System.currentTimeMillis());
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "mv " + configDir + "/" + fromFileName + fileSuffix + " " + configDir + "/" + fromFileName + stamp + ".backup");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "ln -s " + configDir + "/" + toFileName + fileSuffix + " " + configDir + "/" + fromFileName + fileSuffix + "");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "chown root:root " + configDir + "/" + toFileName + fileSuffix + " ");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "chmod a+r+w+x " + configDir + "/" + toFileName + fileSuffix + " ");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "service apache2 reload");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSchException e) {
					e.printStackTrace();
				}
			} else
				System.err.println("Entry already exist in LoadBalancer-Configuration");
		}
	}
	private void installExporter(final String server, final String sshUser, final String sshPass, final String dbName, ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String survey = (String) parameter.get(Parameter.survey);
		final String svnServer = (String) parameter.get(Parameter.svnServer);
		final String svnUser = (String) parameter.get(Parameter.svnUser);
		final String svnPass = (String) parameter.get(Parameter.svnPass);
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final String dbPort = (String) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final String stamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(System.currentTimeMillis());
		final String jnlpName = "exporter" + survey + "_" + stamp;
		final Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("SERVER", "http://" + server);
		replacements.put("FILENAME", "exporter" + survey + "_" + stamp + ".jnlp");
		replacements.put("SURVEYNAME", survey);
		replacements.put("DBSERVER", dbLocation);
		replacements.put("DBPORT", dbPort);
		replacements.put("DBNAME", dbName);
		replacements.put("DBUSER", dbUser);
		replacements.put("DBPASS", dbPass);
		replacements.put("SVNNAME", survey);
		replacements.put("SVNUSER", svnUser);
		replacements.put("SVNPASS", svnPass);
		replacements.put("SVNSERVER", svnServer);
		final DirectoryClient dir = DirectoryClient.getInstance();
		final FileClient file = FileClient.getInstance();
		final File templateFile = file.getResource("deploy/exporterTemplate.jnlp");
		if ((templateFile != null) && (templateFile.exists())) {
			String content = file.readAsString(templateFile);
			for (final Map.Entry<String, String> replacement : replacements.entrySet()) {
				content = content.replaceAll(Pattern.quote("{" + replacement.getKey() + "}"), replacement.getValue());
			}
			final File jnlpFile = file.createOrGetFile(jnlpName, ".jnlp", dir.getTemp());
			file.writeToFile(jnlpFile, content, false);
			uploadToMonitor(survey, server, sshUser, sshPass, "Exporter", jnlpFile, stamp);
		}
	}
	private void installCockpit(final String server, final String sshUser, final String sshPass, final String dbName, ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String survey = (String) parameter.get(Parameter.survey);
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final String dbPort = (String) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final String stamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(System.currentTimeMillis());
		final String jnlpName = "cockpit" + survey + "_" + stamp;
		final Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("SERVER", "http://" + server);
		replacements.put("FILENAME", jnlpName + ".jnlp");
		replacements.put("SURVEYNAME", survey);
		replacements.put("DBSERVER", dbLocation);
		replacements.put("DBPORT", dbPort);
		replacements.put("DBNAME", dbName);
		replacements.put("DBUSER", dbUser);
		replacements.put("DBPASS", dbPass);
		final DirectoryClient dir = DirectoryClient.getInstance();
		final FileClient file = FileClient.getInstance();
		final File templateFile = file.getResource("deploy/cockpitTemplate.jnlp");
		if ((templateFile != null) && (templateFile.exists())) {
			String content = file.readAsString(templateFile);
			for (final Map.Entry<String, String> replacement : replacements.entrySet()) {
				content = content.replaceAll(Pattern.quote("{" + replacement.getKey() + "}"), replacement.getValue());
			}
			final File jnlpFile = file.createOrGetFile(jnlpName, ".jnlp", dir.getTemp());
			file.writeToFile(jnlpFile, content, false);
			uploadToMonitor(survey, server, sshUser, sshPass, "Cockpit", jnlpFile, stamp);
		}
	}
	private void uploadToMonitor(final String surveyName, final String server, final String sshUser, final String sshPass, final String dirPath, final File jnlpFile, final String tag) {
		SSHClient ssh = SSHClient.getInstance();
		DirectoryClient dir = DirectoryClient.getInstance();
		FileClient file = FileClient.getInstance();
		 System.out.println("Upload Monitor JNLP : "+jnlpFile.getAbsolutePath());
		final File index = file.copyFile(ssh.scpFrom(server, sshUser, sshPass, "/var/www/" + dirPath + "/index.html"), file.createOrGetFile(dirPath.replace('/', '_') + "_index" + System.currentTimeMillis(), ".html", dir.getTemp()));
		File updatedIndex = null;
		final String PLACEHOLDER = "<!-- TAG -->";
		if (index != null) {
			try {
				String content = file.readAsString(index);
				final String toAdd = " <a hRef=\"./" + jnlpFile.getName() + "\">Start " + surveyName + "</a><br/><br/>";
				boolean exists = (content.indexOf(toAdd) > -1);
				if (!exists) {
					content = content.replaceAll(Pattern.quote(PLACEHOLDER), toAdd + "\n" + PLACEHOLDER);
					updatedIndex = file.createOrGetFile("index_" + tag, ".html", dir.getTemp());
					file.writeToFile(updatedIndex, content, false);
				}
				if (updatedIndex != null) {
					ssh.scpTo(server, sshUser, sshPass, jnlpFile, "/home/####");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "mv /home/####/" + jnlpFile.getName() + " /var/www/" + dirPath + "/" + jnlpFile.getName());
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "chown ####:#### /var/www/" + dirPath + "/" + jnlpFile.getName());
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "chmod a+r+w+x /var/www/" + dirPath + "/" + jnlpFile.getName());
					ssh.scpTo(server, sshUser, sshPass, updatedIndex, "/home/####");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "mv /home/####/" + updatedIndex.getName() + " /var/www/" + dirPath + "/" + updatedIndex.getName());
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "mv /var/www/" + dirPath + "/index.html /var/www/" + dirPath + "/index_" + tag + ".backup");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "ln -s /var/www/" + dirPath + "/" + updatedIndex.getName() + " /var/www/" + dirPath + "/index.html");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "chown ####:#### /var/www/" + dirPath + "/index.html");
					ssh.remotSudoCmd(server, sshUser, sshPass, sshPass, "chmod a+r+w+x /var/www/" + dirPath + "/index.html");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}
	}
}
