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
/*
 * Class to communicate with postgresql
 */
package eu.dzhw.zofar.management.comm.db.postgresql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.comm.db.DBClient;
/**
 * The Class PostgresClient.
 */
public class PostgresClient extends DBClient{
	/** The Constant INSTANCE. */
	private static final PostgresClient INSTANCE = new PostgresClient();
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PostgresClient.class);
	/**
	 * Instantiates a new postgres client.
	 */
	private PostgresClient() {
		super();
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}
	/**
	 * Gets the single instance of PostgresClient.
	 *
	 * @return single instance of PostgresClient
	 */
	public static PostgresClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Gets the connection.
	 *
	 * @param url
	 *            the url
	 * @param port
	 *            the port
	 * @param database
	 *            the database
	 * @param user
	 *            the user
	 * @param pass
	 *            the pass
	 * @return the connection
	 * @throws SQLException
	 *             the SQL exception
	 */
	public Connection getConnection(final String url, final String port, final String database, final String user, final String pass) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql:
		return connection;
	}
	public Connection getMaintenanceConnection(final String url, final String port, final String user, final String pass) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:postgresql:
		return connection;
	}
	public boolean disconnectOtherFromDB(final Connection conn, final String dbName) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE datname = '" + dbName + "' AND pid <> pg_backend_pid();");
		} catch (SQLException ex) {
			throw new Exception(ex);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	public boolean existDB(final Connection conn, final String dbName) throws Exception {
		try {
			final String query = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "';";
			final List<Map<String, String>> result = queryDb(conn, query);
			LOGGER.info("existDB : " + query + " => " + result);
			return ((result != null) && (result.size() >= 1));
		} catch (SQLException ex) {
		}
		return false;
	}
}
