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
package eu.dzhw.zofar.management.comm.db;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class DBClient {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DBClient.class);
	public DBClient() {
		super();
	}
	/**
	 * Query db.
	 *
	 * @param conn
	 *            the conn
	 * @param query
	 *            the query
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	public List<Map<String, String>> queryDb(final Connection conn, final String query) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs != null) {
				return writeResultSet(rs);
			}
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
		return null;
	}
	public boolean executeDb(final Connection conn, final String batch) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			return stmt.execute(batch);
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
	}
	/**
	 * Write result set.
	 *
	 * @param resultSet
	 *            the result set
	 * @return the list
	 */
	private List<Map<String, String>> writeResultSet(ResultSet resultSet) {
		if (resultSet == null)
			return null;
		try {
			final List<Map<String, String>> back = new ArrayList<Map<String, String>>();
			while (resultSet.next()) {
				final int colCount = resultSet.getMetaData().getColumnCount();
				if (colCount > 0) {
					final Map<String, String> row = new LinkedHashMap<String, String>();
					for (int a = 1; a <= colCount; a++) {
						final String colName = resultSet.getMetaData().getColumnName(a);
						String colValue = "ERROR";
						try {
							colValue = resultSet.getString(a);
						} catch (SQLException e) {
						}
						row.put(colName, colValue);
					}
					back.add(row);
				}
			}
			return back;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public boolean createDB(final Connection conn, final String dbName) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			final int count = stmt.executeUpdate("CREATE DATABASE \"" + dbName + "\";");
			return true;
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
	}
	public boolean dropDB(final Connection conn, final String dbName) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			final int count = stmt.executeUpdate("DROP DATABASE \"" + dbName + "\";");
			return true;
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
	}
	/**
	 * Close connection.
	 *
	 * @param conn
	 *            the conn
	 */
	public void close(final Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public abstract Connection getConnection(final String url, final String port, final String database, final String user, final String pass) throws SQLException;
	public abstract Connection getMaintenanceConnection(final String url, final String port, final String user, final String pass) throws SQLException;
	public abstract boolean disconnectOtherFromDB(final Connection conn, final String dbName) throws Exception;
	public abstract boolean existDB(final Connection conn, final String dbName) throws Exception;
}
