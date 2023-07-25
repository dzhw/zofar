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
package eu.dzhw.zofar.management.dev.automation.export;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import eu.dzhw.zofar.management.comm.db.postgresql.PostgresClient;
import eu.dzhw.zofar.management.dev.automation.AbstractExecutor;
import eu.dzhw.zofar.management.dev.qml.QMLClient;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.string.StringUtils;
import service.automation.counting.CountingService;
import service.automation.data.DataService;
import service.automation.feedback.FeedbackService;
import service.automation.statistics.StatisticService;
public class Executor extends AbstractExecutor {
	private static final long serialVersionUID = 8117968813518658482L;
	private static final Executor INSTANCE = new Executor();
	public enum Parameter implements ABSTRACTPARAMETER {
		dbLocation, dbPort, dbUser, dbPass, dbName, svnProject, svnUser, svnPass, svnUrl
	};
	private Executor() {
		super();
	}
	public static Executor getInstance() {
		return INSTANCE;
	}
	public ParameterMap<ABSTRACTPARAMETER, Object> getParameterMap(final String svnUrl, final String svnUser,
			final String svnPass, final String svnProject, final String dbLocation, final int dbPort,
			final String dbUser, final String dbPass, final String dbName) {
		final ParameterMap<ABSTRACTPARAMETER, Object> back = new ParameterMap<ABSTRACTPARAMETER, Object>();
		back.put(Parameter.svnUrl, svnUrl);
		back.put(Parameter.svnUser, svnUser);
		back.put(Parameter.svnPass, svnPass);
		back.put(Parameter.svnProject, svnProject);
		back.put(Parameter.dbLocation, dbLocation);
		back.put(Parameter.dbPort, dbPort);
		back.put(Parameter.dbUser, dbUser);
		back.put(Parameter.dbPass, dbPass);
		back.put(Parameter.dbName, dbName);
		return back;
	}
	@Override
	public void process(final ParameterMap<ABSTRACTPARAMETER, Object> parameter) throws Exception {
		final String svnUrl = (String) parameter.get(Parameter.svnUrl);
		final String svnUser = (String) parameter.get(Parameter.svnUser);
		final String svnPass = (String) parameter.get(Parameter.svnPass);
		final String svnProject = (String) parameter.get(Parameter.svnProject);
		final File baseDir = DirectoryClient.getInstance().createDir(DirectoryClient.getInstance().getHome(),
				"Exports");
		final String threadedsvnProjekt = svnProject+StringUtils.getInstance().randomUUID();
		final File workDir = DirectoryClient.getInstance().createDir(baseDir, "Automation");
		final File projectDir = DirectoryClient.getInstance().createDir(workDir, threadedsvnProjekt);
		final File tempDir = DirectoryClient.getInstance().createDir(baseDir, "temp");
		final File projectTempDir = DirectoryClient.getInstance().createDir(tempDir, threadedsvnProjekt);
		DirectoryClient.getInstance().cleanDirectory(projectDir);
		DirectoryClient.getInstance().cleanDirectory(projectTempDir);
		final String participantTable = "batch_participant";
		final String historyTable = "surveyhistory";
		final String dataTable = "clean_surveydata";
		int pageCount = 0;
		final File qmlFile = new File("/home/meisner/Entwicklung/workspaces/120819/sid-2021/src/main/resources/questionnaire_generated.xml");
		if (qmlFile != null) {
			final QuestionnaireDocument doc = QMLClient.getInstance().getDocument(qmlFile);
			final PageType[] pages = doc.getQuestionnaire().getPageArray();
			if (pages != null)
				pageCount = pages.length;
		}
		final int packetSize = 20000;
		int offset = 0;
		boolean flag = true;
		while (flag) {
			try {
				cleanViews(parameter, participantTable, historyTable, dataTable);
				createView(parameter, offset, packetSize, participantTable, historyTable, dataTable);
				final int tokenCount = countTokens(parameter, participantTable);
				if (tokenCount == 0) {
					flag = false;
					break;
				}
				final File projectBatchTempDir = DirectoryClient.getInstance().createDir(projectTempDir,
						offset + "-" + (offset + packetSize));
				DirectoryClient.getInstance().cleanDirectory(projectBatchTempDir);
				File exportDone = export(parameter, qmlFile, projectBatchTempDir, participantTable, historyTable,
						dataTable);
				exportDone = FileClient.getInstance().renameTo(exportDone,
						offset + "-" + (offset + packetSize) + "_" + exportDone.getName());
				FileClient.getInstance().copyToDir(exportDone, projectDir);
				DirectoryClient.getInstance().cleanDirectory(projectBatchTempDir);
				projectBatchTempDir.delete();
				offset = offset + packetSize;
			}
			catch(Exception e) {
				throw e;
			}
		}
		cleanViews(parameter, participantTable, historyTable, dataTable);
	}
	private void createView(final ParameterMap<ABSTRACTPARAMETER, Object> parameter, final int offset, final int count,
			final String participantTable, final String historyTable, final String dataTable) throws Exception {
		final PostgresClient postgres = PostgresClient.getInstance();
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final int dbPort = (int) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final String dbName = (String) parameter.get(Parameter.dbName);
		Connection connection = null;
		try {
			connection = postgres.getConnection(dbLocation, dbPort + "", dbName, dbUser, dbPass);
		} catch (final Exception e) {
			throw new Exception("cannot establish connection : location=" + dbLocation + " port=" + dbPort + " dbname="
					+ dbName + " db_user=" + dbUser + " db_password=" + dbPass, e);
		}
		if (connection != null) {
			try {
				postgresClient.executeDb(connection, "CREATE VIEW " + participantTable
						+ " AS SELECT * FROM clean_participant where true LIMIT " + count + " OFFSET " + offset + ";");
			} catch (final Exception e) {
				throw new Exception("cannot query source db : location=" + dbLocation + " port=" + dbPort + " dbname="
						+ dbName + " db_user=" + dbUser + " db_password=" + dbPass, e);
			} finally {
				postgresClient.close(connection);
			}
		}
	}
	private File export(final ParameterMap<ABSTRACTPARAMETER, Object> parameter, final File qmlFile,
			final File projectTempDir, final String participantTable, final String historyTable, final String dataTable)
			throws Exception {
		final DataService dataService = new DataService();
		final CountingService countingService = new CountingService();
		final FeedbackService feedbackService = new FeedbackService();
		final StatisticService statisticService = new StatisticService(countingService, dataService, feedbackService);
		final de.his.zofar.exporter.main.AutomationClient exporter = new de.his.zofar.exporter.main.AutomationClient(
				dataService, countingService, feedbackService, statisticService);
		final Map<Object, Object> properties = new HashMap<Object, Object>();
		properties.put("packetSize", "50");
		properties.put("server", parameter.get(Parameter.dbLocation));
		properties.put("port", (int) parameter.get(Parameter.dbPort));
		properties.put("database", parameter.get(Parameter.dbName));
		properties.put("user", parameter.get(Parameter.dbUser));
		properties.put("password", parameter.get(Parameter.dbPass));
		properties.put("qml", qmlFile.getName());
		properties.put("qmlFile", qmlFile);
		properties.put("limitLabels", "false");
		properties.put("limitStrings", "false");
		properties.put("exportKey", "testCertificate.pem");
		properties.put("svnProject", parameter.get(Parameter.svnProject));
		properties.put("svnUser", parameter.get(Parameter.svnUser));
		properties.put("svnPass", parameter.get(Parameter.svnPass));
		properties.put("svnServer", parameter.get(Parameter.svnUrl));
		properties.put("svnPathPrefix", "");
		properties.put("svnPathPostfix", "/src/main/resources/");
		properties.put("defaultField", "-9999");
		properties.put("missingNotAnswered", "-9990");
		properties.put("missingNullValue", "-9992");
		properties.put("missingNoPage", "-9995");
		properties.put("missingNotSeen", "-9992");
		properties.put("missingNotVisited", "-9991");
		properties.put("missingInitGet", "-9995");
		properties.put("noEmpty", "false");
		properties.put("pretestComments", "false");
		properties.put("paraData", "true");
		try {
			return exporter.execute(properties, projectTempDir, System.out, participantTable, historyTable, dataTable);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private int countTokens(final ParameterMap<ABSTRACTPARAMETER, Object> parameter, final String participantTable)
			throws Exception {
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final int dbPort = (int) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final String dbName = (String) parameter.get(Parameter.dbName);
		final PostgresClient postgres = PostgresClient.getInstance();
		Connection connection = null;
		try {
			connection = postgres.getConnection(dbLocation, dbPort + "", dbName, dbUser, dbPass);
		} catch (final Exception e) {
			throw new Exception("cannot establish connection : location=" + dbLocation + " port=" + dbPort + " dbname="
					+ dbName + " db_user=" + dbUser + " db_password=" + dbPass, e);
		}
		if (connection != null) {
			List<Map<String, String>> fromDB = null;
			try {
				fromDB = postgres.queryDb(connection, "select distinct token from " + participantTable + ";");
			} catch (final Exception e) {
				throw new Exception("cannot query db : location=" + dbLocation + " port=" + dbPort + " dbname=" + dbName
						+ " db_user=" + dbUser + " db_password=" + dbPass, e);
			}
			if (fromDB != null) {
				return fromDB.size();
			}
		}
		return 0;
	}
	private boolean cleanViews(final ParameterMap<ABSTRACTPARAMETER, Object> parameter, final String participantTable,
			final String historyTable, final String dataTable) throws Exception {
		final String dbLocation = (String) parameter.get(Parameter.dbLocation);
		final int dbPort = (int) parameter.get(Parameter.dbPort);
		final String dbUser = (String) parameter.get(Parameter.dbUser);
		final String dbPass = (String) parameter.get(Parameter.dbPass);
		final String dbName = (String) parameter.get(Parameter.dbName);
		boolean flag = false;
		final PostgresClient postgres = PostgresClient.getInstance();
		Connection connection = null;
		try {
			connection = postgres.getConnection(dbLocation, dbPort + "", dbName, dbUser, dbPass);
		} catch (final Exception e) {
			throw new Exception("cannot establish connection : location=" + dbLocation + " port=" + dbPort + " dbname="
					+ dbName + " db_user=" + dbUser + " db_password=" + dbPass, e);
		}
		if (connection != null) {
			try {
				postgresClient.executeDb(connection, "DROP VIEW IF EXISTS batch_" + dataTable + ";");
				postgresClient.executeDb(connection, "DROP VIEW IF EXISTS batch_" + historyTable + ";");
				postgresClient.executeDb(connection, "DROP VIEW IF EXISTS " + participantTable + ";");
				flag = true;
			} catch (final Exception e) {
				throw new Exception("cannot query db : location=" + dbLocation + " port=" + dbPort + " dbname=" + dbName
						+ " db_user=" + dbUser + " db_password=" + dbPass, e);
			} finally {
				postgresClient.close(connection);
			}
		}
		return flag;
	}
}
