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
package eu.dzhw.zofar.management.dev.builder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import eu.dzhw.zofar.management.comm.svn.SVNClient;
import eu.dzhw.zofar.management.dev.maven.MavenClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.system.ConfigurationUtils;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
public class BuilderClient implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 70472259058126660L;
	private static final BuilderClient INSTANCE = new BuilderClient();
	private static final Logger LOGGER = LoggerFactory.getLogger(BuilderClient.class);
	private BuilderClient() {
		super();
	}
	public static BuilderClient getInstance() {
		return INSTANCE;
	}
	public File buildExistingProject(final File projectDir, final String dbLocation, final String dbPort,
			final String dbname, final String sessionDbname, final String dbUser, final String dbPass,
			final boolean overrideNav, final boolean cutHistory, final boolean pretest, final boolean linearNavigation,
			final boolean overrideRendering, final boolean mdm, final boolean noVisibleMap, final String saveMode,
			final String login, final boolean preloadOnStart, final boolean record, final boolean cluster,
			final String svnUrl, final String svnUser, final String svnPass, final boolean overrideCommit,
			final String token) throws Exception {
		return this.buildExistingProject(projectDir.getName(), projectDir, dbLocation, dbPort, dbname, sessionDbname,
				dbUser, dbPass, overrideNav, cutHistory, pretest, linearNavigation, overrideRendering, mdm,
				noVisibleMap, saveMode, login, preloadOnStart, record, cluster, svnUrl, svnUser, svnPass,
				overrideCommit, token);
	}
	public File buildExistingProject(final String finalName, final File projectDir, final String dbLocation,
			final String dbPort, final String dbname, final String sessionDbname, final String dbUser,
			final String dbPass, final boolean overrideNav, final boolean cutHistory, final boolean pretest,
			final boolean linearNavigation, final boolean overrideRendering, final boolean mdm,
			final boolean noVisibleMap, final String saveMode, final String login, final boolean preloadOnStart,
			final boolean record, final boolean cluster, final String svnUrl, final String svnUser,
			final String svnPass, final boolean overrideCommit, final String token) throws Exception {
		final Reader reader = new FileReader(this.getPom(projectDir));
		final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
		final Model model = xpp3Reader.read(reader);
		final Build build = model.getBuild();
		final Map<String, Plugin> plugins = build.getPluginsAsMap();
		final boolean surveyTypeResponsive;
		Plugin plugin = plugins.get("de.his.zofar:zofar.survey.generator.maven.plugin-html5");
		if (plugin != null) {
			surveyTypeResponsive=true;
			this.modifyPom(finalName, this.getPom(projectDir), pretest, linearNavigation, overrideRendering, mdm,
					noVisibleMap, svnUrl, svnUser, svnPass);
		} else {
			surveyTypeResponsive=false;
			this.modifyPomNonResponsive(finalName, this.getPom(projectDir), pretest, linearNavigation, overrideRendering, mdm,
					noVisibleMap, svnUrl, svnUser, svnPass);
		}
		final File qmlFile = new File(projectDir.getAbsolutePath() + "/src/main/resources/questionnaire.xml");
		this.modifyQML(qmlFile, finalName, svnUser, svnPass, overrideCommit, token);
		final File dbFile = new File(projectDir.getAbsolutePath() + "/src/main/resources/survey/database.properties");
		this.modifyDB(dbFile, dbLocation, dbPort, dbname, dbUser, dbPass, svnUrl, svnUser, svnPass, overrideCommit);
		final File jsfTemplateFile = new File(projectDir.getAbsolutePath() + "/src/main/webapp/template/survey.xhtml");
		this.modifyJSFTemplate(jsfTemplateFile, mdm, svnUser, svnPass, overrideCommit, noVisibleMap, surveyTypeResponsive);
		File securityFile = null; 
		final File systemFile = new File(projectDir.getAbsolutePath() + "/src/main/resources/system.properties");
		this.modifySystem(systemFile, overrideNav, cutHistory, saveMode, login, preloadOnStart, record, cluster,
				svnUser, svnPass, overrideCommit);
		final File cssFile = new File(projectDir.getAbsolutePath() + "/src/main/webapp/resources/html5/css/additional.css");
		this.modifyCSS(qmlFile,cssFile, overrideNav, cutHistory, saveMode, login, preloadOnStart, record, cluster,
				svnUser, svnPass, overrideCommit);
		final File jsFile = new File(projectDir.getAbsolutePath() + "/src/main/webapp/resources/html5/js/additional.js");
		this.modifyJS(qmlFile,jsFile, overrideNav, cutHistory, saveMode, login, preloadOnStart, record, cluster,
				svnUser, svnPass, overrideCommit);
		return projectDir;
	}
	public File createProject(final String name, final File baseDir, final String svnUrl, final String svnUser,
			final String svnPass) throws Exception {
		final MavenClient mavenClient = MavenClient.getInstance();
		final File tmpProjectDir = mavenClient.createProjectFromArchetype("eu.dzhw.zofar", name, "de.his.zofar",
				"zofar.survey.archetype.surveytemplate_responsive", "0.0.3-SNAPSHOT", baseDir);
		try {
			this.modifyPom(name, this.getPom(tmpProjectDir), false, false, false, false, false, svnUrl, svnUser,
					svnPass);
			mavenClient.doCleanInstall(tmpProjectDir);
			return tmpProjectDir;
		} catch (final MavenInvocationException e) {
			throw new Exception(e);
		}
	}
	public File createNonResponsiveProject(final String name, final File baseDir, final String svnUrl,
			final String svnUser, final String svnPass) throws Exception {
		final MavenClient mavenClient = MavenClient.getInstance();
		final File tmpProjectDir = mavenClient.createProjectFromArchetype("eu.dzhw.zofar", name, "de.his.zofar",
				"zofar.survey.archetype.surveytemplate", "0.0.1-SNAPSHOT", baseDir);
		try {
			this.modifyPom(name, this.getPom(tmpProjectDir), false, false, false, false, false, svnUrl, svnUser,
					svnPass);
			mavenClient.doCleanInstall(tmpProjectDir);
			return tmpProjectDir;
		} catch (final MavenInvocationException e) {
			throw new Exception(e);
		}
	}
	public File getProject(final String name, final File baseDir) {
		return new File(baseDir, name);
	}
	public File checkoutProject(final String name, final File baseDir, final String svnUrl, final String svnUser,
			final String svnPass) throws Exception {
		final SVNClient svn = SVNClient.getInstance();
		try {
			final File projectDir = new File(baseDir, name);
			svn.doCheckout(svnUrl, svnUser, svnPass, name, projectDir);
			return projectDir;
		} catch (final SVNException e) {
			throw new Exception(e);
		}
	}
	public void commitProject(final String name, final File projectDir, final String svnUrl, final String svnUser,
			final String svnPass) throws Exception {
		final SVNClient svn = SVNClient.getInstance();
		try {
			svn.createProject(svnUrl, svnUser, svnPass, name, projectDir);
		} catch (final SVNException e) {
			throw new Exception(e);
		}
	}
	public void deleteProject(final String svnUrl, final String svnUser, final String svnPass, final String name)
			throws Exception {
		final SVNClient svn = SVNClient.getInstance();
		try {
			svn.deleteProject(svnUrl, svnUser, svnPass, name);
		} catch (final SVNException e) {
			throw new Exception(e);
		}
	}
	public List<String> listProjects(final String svnUrl, final String svnUser, final String svnPass) throws Exception {
		final SVNClient svn = SVNClient.getInstance();
		try {
			return svn.getProjects(svnUrl, svnUser, svnPass, "");
		} catch (final SVNException e) {
			throw new Exception(e);
		} catch (final IOException e) {
			throw new Exception(e);
		}
	}
	private void modifyQML(final File qmlFile, final String name, final String svnUser, final String svnPass,
			final boolean overrideCommit, final String token) throws Exception {
		final XmlClient qml = XmlClient.getInstance();
		final SVNClient svn = SVNClient.getInstance();
		final Document qmlDoc = qml.getDocument(qmlFile.getAbsolutePath());
		final NodeList nodes = qml.getByXPath(qmlDoc, "//*");
		final StringBuffer comments = new StringBuffer();
		if (nodes != null) {
			try {
				final int count = nodes.getLength();
				for (int a = 0; a < count; a++) {
					final Node node = nodes.item(a);
					final String nodeName = node.getNodeName();
					if (nodeName.equals("zofar:name")) {
						final String oldName = node.getTextContent();
						node.setTextContent(name);
						comments.append("Name changed from " + oldName + " to " + name + ", ");
					} else if (nodeName.equals("zofar:description")) {
						final String oldDescription = node.getTextContent();
						node.setTextContent(name);
						comments.append("Description changed from " + oldDescription + " to " + name + ", ");
					}
					else if (nodeName.equals("zofar:variables")) {
						boolean langExist = false;
						final NodeList variables = node.getChildNodes();
						if (variables != null) {
							final int variableCount = variables.getLength();
							for (int b = 0; b < variableCount; b++) {
								final Node varNode = variables.item(b);
								if ((varNode != null) && (varNode.getNodeName().equals("zofar:variable"))) {
									final String nameValue = ((Element) varNode).getAttribute("name");
									if ((nameValue != null) && (nameValue.equals("language"))) {
										langExist = true;
									}
								}
							}
						}
						if (!langExist) {
							final Node varNode = qmlDoc.createElement("zofar:variable");
							((Element) varNode).setAttribute("type", "string");
							((Element) varNode).setAttribute("name", "language");
							node.appendChild(varNode);
						}
					}
					else if (nodeName.equals("zofar:preloads")) {
						boolean tokenExist = false;
						final NodeList tokens = node.getChildNodes();
						if (tokens != null) {
							final int tokenCount = tokens.getLength();
							for (int b = 0; b < tokenCount; b++) {
								final Node tokenNode = tokens.item(b);
								if ((tokenNode != null) && (tokenNode.getNodeName().equals("zofar:preload"))) {
									final String nameValue = ((Element) tokenNode).getAttribute("name");
									if ((nameValue != null) && (nameValue.equals(token))) {
										tokenExist = true;
									}
								}
							}
						}
						if (!tokenExist) {
							final Node tokenNode = qmlDoc.createElement("zofar:preload");
							((Element) tokenNode).setAttribute("password", token);
							((Element) tokenNode).setAttribute("name", token);
							node.appendChild(tokenNode);
						}
					}
					else if (nodeName.equals("zofar:page")) {
						final NodeList transitionList = qml.getByXPath(node, "transitions/*");
						int transitionCount = 0;
						if (transitionList != null)
							transitionCount = transitionList.getLength();
						if (transitionCount == 0) {
							final String pageUID = ((Element) node).getAttribute("uid");
							if (!pageUID.equals("end")) {
								final NodeList transitionsList = qml.getByXPath(node, "transitions");
								int transitionsCount = 0;
								if (transitionsList != null)
									transitionsCount = transitionsList.getLength();
								Node transistionsContainer = null;
								if (transitionsCount == 0) {
									transistionsContainer = qmlDoc.createElement("zofar:transitions");
									node.appendChild(transistionsContainer);
								} else {
									transistionsContainer = transitionsList.item(0);
								}
								final Node transNode = qmlDoc.createElement("zofar:transition");
								((Element) transNode).setAttribute("target", "end");
								transistionsContainer.appendChild(transNode);
							}
						}
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		qml.saveDocument(qmlDoc, qmlFile);
		if (!overrideCommit) {
			try {
				svn.doCommit(svnUser, svnPass, qmlFile, comments.toString());
			} catch (final SVNException e) {
				throw new Exception(e);
			}
		}
	}
	private void modifyJSFTemplate(final File templateFile, final boolean mdm, final String svnUser,
			final String svnPass, final boolean overrideCommit, final boolean noVisibleMap, final boolean surveyTypeResponsive) throws Exception {
		final SVNClient svn = SVNClient.getInstance();
		final FileClient fileClient = FileClient.getInstance();
		if (mdm) {
			if ((templateFile != null) && (templateFile.exists())) {
				final String[] fileName = templateFile.getName().split(Pattern.quote("."));
				File backup = fileClient.createOrGetFile(fileName[0] + "_backup", "." + fileName[1],
						templateFile.getParentFile());
				fileClient.copyFile(templateFile, backup);
			}
			final File screenshotTemplateFile= null;
			if (surveyTypeResponsive) {
				fileClient.getResource("builder/mdmSurveyScreenshot_html5.xhtml"); 
			} else {
				fileClient.getResource("builder/mdmSurveyScreenshot.xhtml"); 
			}
			if ((screenshotTemplateFile != null) && (screenshotTemplateFile.exists())) {
				final String content = fileClient.readAsString(screenshotTemplateFile);
				fileClient.writeToFile(templateFile, content, false);
			}
		}
		if (!noVisibleMap) {
			XmlClient xmlClient = XmlClient.getInstance();
			final Document doc = xmlClient.getDocument(templateFile.getAbsolutePath());
			NodeList divs = doc.getElementsByTagName("div");
			int count = divs.getLength();
			for (int a = 0; a < count; a++) {
				final Node div = divs.item(a);
				final Node classAttr = div.getAttributes().getNamedItem("class");
				if(classAttr != null) {
					final String clazz = classAttr.getTextContent();
					if (!clazz.equals("survey-outer"))
						continue;
					Element createdElement = doc.createElement("ui:insert");
					createdElement.setAttribute("name", "template-footer");
					div.appendChild(createdElement);
					break;
				}
			}
			final String header = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
			String content = xmlClient.show(doc);
			content = content.replaceAll(Pattern.quote("<meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\">"),"<meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\"/>");
			content = content.replaceAll(Pattern.quote("<meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\">"),"<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"/>");			
			content = content.replaceAll(Pattern.quote("<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\">"),"<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"/>");
			content = content.replaceAll(Pattern.quote("<meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">"),"<meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"/>");
			content = content.replaceAll(Pattern.quote("<link href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css\" rel=\"stylesheet\">"), "	<link href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css\" rel=\"stylesheet\" />");
			content = content.replaceAll(Pattern.quote("<hr class=\"zo-spacer\">"), "<hr class=\"zo-spacer\"/>");
			fileClient.writeToFile(templateFile, header, false);
			fileClient.writeToFile(templateFile, content, true);
		}
		// HTTPClient httpClient = HTTPClient.getInstance();
		// Page page = httpClient.loadPage(templateFile);
		// htmlPage.getNamespaces().put("c", "http://java.sun.com/jstl/core");
		// File modifiedFile = httpClient.savePage(htmlPage,
	}
	private XmlObject docToXmlObject(final Document doc) throws Exception {
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		XmlOptions opts = new XmlOptions();
		opts.setCharacterEncoding("utf8");
		opts.setSavePrettyPrint();
		opts.setSavePrettyPrintIndent(4);
		opts.setSaveOuter();
		opts.setSaveAggressiveNamespaces();
		opts.setCompileNoValidation();
		final XmlObject back = XmlObject.Factory.parse(writer.toString(), opts);
		return back;
	}
	private void modifyDB(final File dbPropertieFile, final String dbLocation, final String dbPort, final String dbName,
			final String dbUser, final String dbPass, final String svnUrl, final String svnUser, final String svnPass,
			final boolean overrideCommit) throws Exception {
		final ConfigurationUtils conf = ConfigurationUtils.getInstance();
		final SVNClient svn = SVNClient.getInstance();
		final Properties props = conf.getConfigurationFromFileSystem(dbPropertieFile.getAbsolutePath());
		props.setProperty("jdbc.username", dbUser);
		props.setProperty("jdbc.password", dbPass);
		props.setProperty("jdbc.url", "jdbc:postgresql:
		props.setProperty("connectionPool.maxSize", "50");
		props.setProperty("connectionPool.maxStatements", "0");
		props.setProperty("connectionPool.minSize", "10");
		props.setProperty("connectionPool.testConnectionOnCheckout", "true");
		props.setProperty("connectionPool.idleConnectionTestPeriod", "300");
		props.setProperty("connectionPool.preferredTestQuery", "select 1;");
		if (conf.saveConfiguration(props, dbPropertieFile.getAbsolutePath())) {
			if (!overrideCommit) {
				try {
					svn.doCommit(svnUser, svnPass, dbPropertieFile, "Database Connection set to\n Location: "
							+ dbLocation + "\n DBName : " + dbName + "\n User: " + dbUser + "\n Pass: " + dbPass + "");
				} catch (final SVNException e) {
					throw new Exception(e);
				}
			}
		} else {
			System.err.println("cannot modify " + dbPropertieFile);
		}
	}
	private void modifySessionSecurity(final File securityFile, final String dbLocation, final String dbPort,
			final String dbName, final String dbUser, final String dbPass, final String svnUrl, final String svnUser,
			final String svnPass, final boolean overrideCommit) throws Exception {
		final SVNClient svn = SVNClient.getInstance();
		final XmlClient xml = XmlClient.getInstance();
		final Document doc = xml.getDocument(securityFile.getAbsolutePath());
		if (doc != null) {
			final XmlObject config = xml.docToXmlObject(doc);
			// "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
			// http://xml.apache.org/~edwingo/jaxp-faq.html
			// props.setDoctypeSystemId("xxhttp://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
			config.save(securityFile);
			final XmlObject[] propertyNodes = xml.getByXPath(config,
					"/hibernate-configuration/session-factory/property[@name]");
			if (propertyNodes != null) {
				for (final XmlObject propertyNode : propertyNodes) {
					final String type = xml.getAttribute(propertyNode, "name");
					if (type.equals("connection.url")) {
						propertyNode.newCursor()
								.setTextValue("jdbc:postgresql:
					} else if (type.equals("connection.username")) {
						propertyNode.newCursor().setTextValue(dbUser);
					} else if (type.equals("connection.password")) {
						propertyNode.newCursor().setTextValue(dbPass);
					}
				}
				config.save(securityFile);
			}
			if ((!overrideCommit)) {
				try {
					svn.doCommit(svnUser, svnPass, securityFile, "Session Security Connection set to\n Location: "
							+ dbLocation + "\n DBName : " + dbName + "\n User: " + dbUser + "\n Pass: " + dbPass + "");
				} catch (final SVNException e) {
					throw new Exception(e);
				}
			}
			final String content = FileClient.getInstance().readAsString(securityFile);
		}
	}
	private void modifyCSS(final File qmlFile,final File cssFile, final boolean overrideNav, final boolean cutHistory,
			final String saveMode, final String login, final boolean preloadOnStart, final boolean record,
			final boolean cluster, final String svnUser, final String svnPass, final boolean overrideCommit)
			throws Exception {
		final FileClient fileClient = FileClient.getInstance();
		final XmlClient qml = XmlClient.getInstance();
		if ((cssFile != null) && (cssFile.exists())) {
			String cssContent = fileClient.readAsString(cssFile);
			final Document qmlDoc = qml.getDocument(qmlFile.getAbsolutePath());
			final NodeList pages = qml.getByXPath(qmlDoc, "questionnaire/page");
			final int count = pages.getLength();
			for (int a = 0; a < count; a++) {
				final Node page = pages.item(a);
				final String uid = page.getAttributes().getNamedItem("uid").getNodeValue();
				cssContent = cssContent.replaceAll(Pattern.quote(".custom-"+uid), "[class^=custom-"+uid+"]");
				cssContent = cssContent.replaceAll(Pattern.quote("#form\\:"+uid+"\\:")+"([^ {]*)", "[id^=form\\:"+uid+"]");
			}
			fileClient.writeToFile(cssFile, cssContent, false);
		}
	}
	private void modifyJS(final File qmlFile,final File jsFile, final boolean overrideNav, final boolean cutHistory,
			final String saveMode, final String login, final boolean preloadOnStart, final boolean record,
			final boolean cluster, final String svnUser, final String svnPass, final boolean overrideCommit)
			throws Exception {
		final FileClient fileClient = FileClient.getInstance();
		final XmlClient qml = XmlClient.getInstance();
		if ((jsFile != null) && (jsFile.exists())) {
			String jsContent = fileClient.readAsString(jsFile);
			final Document qmlDoc = qml.getDocument(qmlFile.getAbsolutePath());
			final NodeList pages = qml.getByXPath(qmlDoc, "questionnaire/page");
			final int count = pages.getLength();
			for (int a = 0; a < count; a++) {
				final Node page = pages.item(a);
				final String uid = page.getAttributes().getNamedItem("uid").getNodeValue();
				jsContent = jsContent.replaceAll(Pattern.quote(".custom-"+uid), "[class^=custom-"+uid+"]");
				jsContent = jsContent.replaceAll(Pattern.quote("#form\\:"+uid+"\\:")+"([^ {]*)", "[id^=form\\:"+uid+"]");
			}
			fileClient.writeToFile(jsFile, jsContent, false);
		}
	}
	private void modifySystem(final File systemPropertieFile, final boolean overrideNav, final boolean cutHistory,
			final String saveMode, final String login, final boolean preloadOnStart, final boolean record,
			final boolean cluster, final String svnUser, final String svnPass, final boolean overrideCommit)
			throws Exception {
		final ConfigurationUtils conf = ConfigurationUtils.getInstance();
		final SVNClient svn = SVNClient.getInstance();
		final StringBuffer comments = new StringBuffer();
		final Properties props = conf.getConfigurationFromFileSystem(systemPropertieFile.getAbsolutePath());
		props.setProperty("overrideNavigation", "true");
		comments.append("overrideNavigation : " + overrideNav + ",");
		props.setProperty("cutHistory", cutHistory + "");
		comments.append("cutHistory : " + cutHistory + ",");
		props.setProperty("saveMode", saveMode);
		comments.append("saveMode : " + saveMode + ",");
		props.setProperty("login", login);
		comments.append("login : " + login + ",");
		props.setProperty("preload_on_start", preloadOnStart + "");
		comments.append("preload_on_start : " + preloadOnStart + ",");
		props.setProperty("record", record + "");
		comments.append("record : " + record + ",");
		props.setProperty("cluster", cluster + "");
		comments.append("record : " + record + ",");
		if (conf.saveConfiguration(props, systemPropertieFile.getAbsolutePath())) {
			if (!overrideCommit) {
				try {
					svn.doCommit(svnUser, svnPass, systemPropertieFile,
							"System properties modified: " + comments.toString());
				} catch (final SVNException e) {
					throw new Exception(e);
				}
			}
		} else {
			System.err.println("cannot modify " + systemPropertieFile);
		}
	}
	public File getPom(final File projectDir) {
		final File pom = new File(projectDir.getAbsolutePath() + File.separator + "pom.xml");
		return pom;
	}
	private boolean removeXpp3Node(final Xpp3Dom parent, final Xpp3Dom child) {
		int removeIndex = -1;
		final int parentCount = parent.getChildCount();
		for (int i = 0; i < parentCount; i++) {
			if (parent.getChild(i) == child) {
				removeIndex = i;
				break;
			}
		}
		if (removeIndex == -1) {
			return false;
		} else {
			parent.removeChild(removeIndex);
			return true;
		}
	}
	private void modifyPom(final String finalName, final File pomFile, final boolean pretest,
			final boolean linearNavigation, final boolean overrideRendering, final boolean mdm,
			final boolean noVisibleMap, final String svnServer, final String svnUser, final String svnPass)
			throws Exception {
		final Reader reader = new FileReader(pomFile);
		try {
			final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
			final Model model = xpp3Reader.read(reader);
			final Properties props = model.getProperties();
			props.put("automation.recommit_qml", "${recommit_qml}");
			final Build build = model.getBuild();
			build.setFinalName(finalName);
			final Map<String, Plugin> plugins = build.getPluginsAsMap();
			if (plugins != null) {
				Plugin plugin = plugins.get("de.his.zofar:zofar.survey.generator.maven.plugin");
				if (plugin != null) {
					plugin.setVersion("1.0.0MultipleQML");
				}
				if (plugin == null) {
					plugin = plugins.get("de.his.zofar:zofar.survey.generator.maven.plugin-html5");
					if (plugin != null) {
						plugin.setVersion("0.0.2");
					}
				}
				if (plugin != null) {
					Xpp3Dom conf = (Xpp3Dom) plugin.getConfiguration();
					if (conf == null) {
						conf = new Xpp3Dom("configuration");
						plugin.setConfiguration(conf);
					}
					removeXpp3Node(conf, conf.getChild("pretest"));
					final Xpp3Dom pretestDom = new Xpp3Dom("pretest");
					pretestDom.setParent(conf);
					pretestDom.setValue("" + pretest);
					conf.addChild(pretestDom);
					removeXpp3Node(conf, conf.getChild("linearNavigation"));
					final Xpp3Dom linearNavDom = new Xpp3Dom("linearNavigation");
					linearNavDom.setParent(conf);
					linearNavDom.setValue("" + linearNavigation);
					conf.addChild(linearNavDom);
					removeXpp3Node(conf, conf.getChild("overrideRendering"));
					final Xpp3Dom overrideRenderingDom = new Xpp3Dom("overrideRendering");
					overrideRenderingDom.setParent(conf);
					overrideRenderingDom.setValue("" + overrideRendering);
					conf.addChild(overrideRenderingDom);
					removeXpp3Node(conf, conf.getChild("mdm"));
					final Xpp3Dom mdmDom = new Xpp3Dom("mdm");
					mdmDom.setParent(conf);
					mdmDom.setValue("" + mdm);
					conf.addChild(mdmDom);
					removeXpp3Node(conf, conf.getChild("noVisibleMap"));
					final Xpp3Dom noVisibleMapDom = new Xpp3Dom("noVisibleMap");
					noVisibleMapDom.setParent(conf);
					noVisibleMapDom.setValue("" + noVisibleMap);
					conf.addChild(noVisibleMapDom);
					removeXpp3Node(conf, conf.getChild("recommit_qml"));
					final Xpp3Dom recommitqml = new Xpp3Dom("recommit_qml");
					recommitqml.setParent(conf);
					recommitqml.setValue("${automation.recommit_qml}");
					conf.addChild(recommitqml);
					final Map<String, PluginExecution> executions = plugin.getExecutionsAsMap();
					final PluginExecution execution = executions.get("recommit-qml");
					if (execution == null) {
						PluginExecution exec = new PluginExecution();
						exec.setId("recommit-qml");
						exec.setPhase("install");
						exec.addGoal("recommit-qml");
						executions.put("recommit-qml", exec);
						plugin.addExecution(exec);
					}
				}
				Plugin verifier = plugins.get("zofar.testing:verifier");
				if (verifier == null) {
					verifier = new Plugin();
					verifier.setArtifactId("verifier");
					verifier.setGroupId("zofar.testing");
					plugins.put("zofar.testing:verifier", verifier);
					build.getPlugins().add(verifier);
				}
				if (verifier != null) {
					verifier.setVersion("1.0.2-SNAPSHOT");
					Xpp3Dom conf = (Xpp3Dom) verifier.getConfiguration();
					if (conf == null) {
						conf = new Xpp3Dom("configuration");
						verifier.setConfiguration(conf);
					}
					removeXpp3Node(conf, conf.getChild("contentPath"));
					final Xpp3Dom contentPath = new Xpp3Dom("contentPath");
					contentPath.setParent(conf);
					contentPath.setValue("${basedir}/src/main/resources/questionnaire_generated.xml");
					conf.addChild(contentPath);
					String debugSvnServer = svnServer;
					if (debugSvnServer.endsWith("/svn/hiob/tags/surveys")) {
						LOGGER.warn("[BUG] need to clean svnServer");
						debugSvnServer = debugSvnServer.replace("/svn/hiob/tags/surveys", "");
					}
					removeXpp3Node(conf, conf.getChild("svnServer"));
					final Xpp3Dom svnServerDom = new Xpp3Dom("svnServer");
					svnServerDom.setParent(conf);
					svnServerDom.setValue(debugSvnServer);
					conf.addChild(svnServerDom);
					removeXpp3Node(conf, conf.getChild("svnUser"));
					final Xpp3Dom svnUserDom = new Xpp3Dom("svnUser");
					svnUserDom.setParent(conf);
					svnUserDom.setValue("####");
					conf.addChild(svnUserDom);
					removeXpp3Node(conf, conf.getChild("svnPass"));
					final Xpp3Dom svnPassDom = new Xpp3Dom("svnPass");
					svnPassDom.setParent(conf);
					svnPassDom.setValue("####");
					conf.addChild(svnPassDom);
					removeXpp3Node(conf, conf.getChild("svnPath"));
					final Xpp3Dom svnPath = new Xpp3Dom("svnPath");
					svnPath.setParent(conf);
					svnPath.setValue(
							"svn/hiob/trunk/zofar/zofar.service/zofar.service.questionnaire/zofar.service.questionnaire.xml/src/main/xsd/de/his/zofar/xml");
					conf.addChild(svnPath);
					removeXpp3Node(conf, conf.getChild("qmlSchemaFileName"));
					final Xpp3Dom qmlSchemaFileName = new Xpp3Dom("qmlSchemaFileName");
					qmlSchemaFileName.setParent(conf);
					qmlSchemaFileName.setValue("zofar_questionnaire_0.2.xsd");
					conf.addChild(qmlSchemaFileName);
					removeXpp3Node(conf, conf.getChild("navigationSchemaFileName"));
					final Xpp3Dom navigationSchemaFileName = new Xpp3Dom("navigationSchemaFileName");
					navigationSchemaFileName.setParent(conf);
					navigationSchemaFileName.setValue("navigation_0.1.xsd");
					conf.addChild(navigationSchemaFileName);
					removeXpp3Node(conf, conf.getChild("displaySchemaFileName"));
					final Xpp3Dom displaySchemaFileName = new Xpp3Dom("displaySchemaFileName");
					displaySchemaFileName.setParent(conf);
					displaySchemaFileName.setValue("display_0.1.xsd");
					conf.addChild(displaySchemaFileName);
					removeXpp3Node(conf, conf.getChild("researchdatacenterSchemaFileName"));
					final Xpp3Dom researchdatacenterSchemaFileName = new Xpp3Dom("researchdatacenterSchemaFileName");
					researchdatacenterSchemaFileName.setParent(conf);
					researchdatacenterSchemaFileName.setValue("researchdatacenter_0.1.xsd");
					conf.addChild(researchdatacenterSchemaFileName);
					final Map<String, PluginExecution> executions = verifier.getExecutionsAsMap();
					final List<PluginExecution> newExecutions = new ArrayList<PluginExecution>();
					PluginExecution execution = new PluginExecution();
					if (execution != null) {
						execution.setId("validate-qml");
						execution.setPhase("process-resources");
						if(!execution.getGoals().contains("validate-qml"))execution.getGoals().add("validate-qml");
					}
					executions.put("validate-qml", execution);
					newExecutions.add(execution);
					PluginExecution spel_execution = new PluginExecution();
					if (spel_execution != null) {
						spel_execution.setId("conditioncheck-qml");
						spel_execution.setPhase("process-resources");
						if(!spel_execution.getGoals().contains("conditioncheck"))spel_execution.getGoals().add("conditioncheck-qml");
					}
					executions.put("conditioncheck-qml", spel_execution);
					newExecutions.add(spel_execution);
					verifier.setExecutions(newExecutions);
				}
			}
			if (mdm) {
				if ((pomFile != null) && (pomFile.exists())) {
					final String[] fileName = pomFile.getName().split(Pattern.quote("."));
					File backup = FileClient.getInstance().createOrGetFile(
							fileName[0] + "_backup" + System.currentTimeMillis(), "." + fileName[1],
							pomFile.getParentFile());
					FileClient.getInstance().copyFile(pomFile, backup);
				}
			}
			final MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(new FileOutputStream(pomFile), model);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
	}
	private void modifyPomNonResponsive(final String finalName, final File pomFile, final boolean pretest,
			final boolean linearNavigation, final boolean overrideRendering, final boolean mdm,
			final boolean noVisibleMap, final String svnServer, final String svnUser, final String svnPass)
			throws Exception {
		final Reader reader = new FileReader(pomFile);
		try {
			final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
			final Model model = xpp3Reader.read(reader);
			final Build build = model.getBuild();
			build.setFinalName(finalName);
			final Map<String, Plugin> plugins = build.getPluginsAsMap();
			if (plugins != null) {
				Plugin plugin = plugins.get("de.his.zofar:zofar.survey.generator.maven.plugin");
				if (plugin != null) {
					plugin.setVersion("1.0.0MultipleQML");
				}
				if (plugin == null) {
					plugin = plugins.get("de.his.zofar:zofar.survey.generator.maven.plugin");
					if (plugin != null)
						plugin.setVersion("1.0.0MultipleQML");
				}
				if (plugin != null) {
					Xpp3Dom conf = (Xpp3Dom) plugin.getConfiguration();
					if (conf == null) {
						conf = new Xpp3Dom("configuration");
						plugin.setConfiguration(conf);
					}
					removeXpp3Node(conf, conf.getChild("pretest"));
					final Xpp3Dom pretestDom = new Xpp3Dom("pretest");
					pretestDom.setParent(conf);
					pretestDom.setValue("" + pretest);
					conf.addChild(pretestDom);
					removeXpp3Node(conf, conf.getChild("linearNavigation"));
					final Xpp3Dom linearNavDom = new Xpp3Dom("linearNavigation");
					linearNavDom.setParent(conf);
					linearNavDom.setValue("" + linearNavigation);
					conf.addChild(linearNavDom);
					removeXpp3Node(conf, conf.getChild("overrideRendering"));
					final Xpp3Dom overrideRenderingDom = new Xpp3Dom("overrideRendering");
					overrideRenderingDom.setParent(conf);
					overrideRenderingDom.setValue("" + overrideRendering);
					conf.addChild(overrideRenderingDom);
					removeXpp3Node(conf, conf.getChild("mdm"));
					final Xpp3Dom mdmDom = new Xpp3Dom("mdm");
					mdmDom.setParent(conf);
					mdmDom.setValue("" + mdm);
					conf.addChild(mdmDom);
					removeXpp3Node(conf, conf.getChild("noVisibleMap"));
					final Xpp3Dom noVisibleMapDom = new Xpp3Dom("noVisibleMap");
					noVisibleMapDom.setParent(conf);
					noVisibleMapDom.setValue("" + noVisibleMap);
					conf.addChild(noVisibleMapDom);
				}
				Plugin verifier = plugins.get("zofar.testing:verifier");
				if (verifier == null) {
					verifier = new Plugin();
					verifier.setArtifactId("verifier");
					verifier.setGroupId("zofar.testing");
					plugins.put("zofar.testing:verifier", verifier);
					build.getPlugins().add(verifier);
				}
				if (verifier != null) {
					verifier.setVersion("1.0.2-SNAPSHOT");
					Xpp3Dom conf = (Xpp3Dom) verifier.getConfiguration();
					if (conf == null) {
						conf = new Xpp3Dom("configuration");
						verifier.setConfiguration(conf);
					}
					removeXpp3Node(conf, conf.getChild("contentPath"));
					final Xpp3Dom contentPath = new Xpp3Dom("contentPath");
					contentPath.setParent(conf);
					contentPath.setValue("${basedir}/src/main/resources/questionnaire.xml");
					conf.addChild(contentPath);
					String debugSvnServer = svnServer;
					if (debugSvnServer.endsWith("/svn/hiob/tags/surveys")) {
						LOGGER.warn("[BUG] need to clean svnServer");
						debugSvnServer = debugSvnServer.replace("/svn/hiob/tags/surveys", "");
					}
					removeXpp3Node(conf, conf.getChild("svnServer"));
					final Xpp3Dom svnServerDom = new Xpp3Dom("svnServer");
					svnServerDom.setParent(conf);
					svnServerDom.setValue(debugSvnServer);
					conf.addChild(svnServerDom);
					removeXpp3Node(conf, conf.getChild("svnUser"));
					final Xpp3Dom svnUserDom = new Xpp3Dom("svnUser");
					svnUserDom.setParent(conf);
					svnUserDom.setValue("####");
					conf.addChild(svnUserDom);
					removeXpp3Node(conf, conf.getChild("svnPass"));
					final Xpp3Dom svnPassDom = new Xpp3Dom("svnPass");
					svnPassDom.setParent(conf);
					svnPassDom.setValue("####");
					conf.addChild(svnPassDom);
					removeXpp3Node(conf, conf.getChild("svnPath"));
					final Xpp3Dom svnPath = new Xpp3Dom("svnPath");
					svnPath.setParent(conf);
					svnPath.setValue(
							"svn/hiob/trunk/zofar/zofar.service/zofar.service.questionnaire/zofar.service.questionnaire.xml/src/main/xsd/de/his/zofar/xml");
					conf.addChild(svnPath);
					removeXpp3Node(conf, conf.getChild("qmlSchemaFileName"));
					final Xpp3Dom qmlSchemaFileName = new Xpp3Dom("qmlSchemaFileName");
					qmlSchemaFileName.setParent(conf);
					qmlSchemaFileName.setValue("zofar_questionnaire_0.2.xsd");
					conf.addChild(qmlSchemaFileName);
					removeXpp3Node(conf, conf.getChild("navigationSchemaFileName"));
					final Xpp3Dom navigationSchemaFileName = new Xpp3Dom("navigationSchemaFileName");
					navigationSchemaFileName.setParent(conf);
					navigationSchemaFileName.setValue("navigation_0.1.xsd");
					conf.addChild(navigationSchemaFileName);
					removeXpp3Node(conf, conf.getChild("displaySchemaFileName"));
					final Xpp3Dom displaySchemaFileName = new Xpp3Dom("displaySchemaFileName");
					displaySchemaFileName.setParent(conf);
					displaySchemaFileName.setValue("display_0.1.xsd");
					conf.addChild(displaySchemaFileName);
					removeXpp3Node(conf, conf.getChild("researchdatacenterSchemaFileName"));
					final Xpp3Dom researchdatacenterSchemaFileName = new Xpp3Dom("researchdatacenterSchemaFileName");
					researchdatacenterSchemaFileName.setParent(conf);
					researchdatacenterSchemaFileName.setValue("researchdatacenter_0.1.xsd");
					conf.addChild(researchdatacenterSchemaFileName);
					final Map<String, PluginExecution> executions = verifier.getExecutionsAsMap();
					final List<PluginExecution> newExecutions = new ArrayList<PluginExecution>();
					PluginExecution execution = new PluginExecution();
					if (execution != null) {
						execution.setId("validate-qml");
						execution.setPhase("process-resources");
						if(!execution.getGoals().contains("validate-qml"))execution.getGoals().add("validate-qml");
					}
					executions.put("validate-qml", execution);
					newExecutions.add(execution);
					PluginExecution spel_execution = new PluginExecution();
					if (spel_execution != null) {
						spel_execution.setId("conditioncheck-qml");
						spel_execution.setPhase("process-resources");
						if(!spel_execution.getGoals().contains("conditioncheck"))spel_execution.getGoals().add("conditioncheck-qml");
					}
					executions.put("conditioncheck-qml", spel_execution);
					newExecutions.add(spel_execution);
					verifier.setExecutions(newExecutions);
				}
			}
			if (mdm) {
				if ((pomFile != null) && (pomFile.exists())) {
					final String[] fileName = pomFile.getName().split(Pattern.quote("."));
					File backup = FileClient.getInstance().createOrGetFile(
							fileName[0] + "_backup" + System.currentTimeMillis(), "." + fileName[1],
							pomFile.getParentFile());
					FileClient.getInstance().copyFile(pomFile, backup);
				}
			}
			final MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(new FileOutputStream(pomFile), model);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
		}
	}
}
