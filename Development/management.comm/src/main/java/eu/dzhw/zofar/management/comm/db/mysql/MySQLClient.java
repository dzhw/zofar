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
package eu.dzhw.zofar.management.comm.db.mysql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import eu.dzhw.zofar.management.comm.db.DBClient;
public class MySQLClient extends DBClient {
	/** The Constant INSTANCE. */
	private static final MySQLClient INSTANCE = new MySQLClient();
	private MySQLClient() {
		super();
		try {
			Class.forName("com.mysql.jdbc.Driver");
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
	public static MySQLClient getInstance() {
		return INSTANCE;
	}
	@Override
	public Connection getConnection(String url, String port, String database, String user, String pass) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql:
		return connection;
	}
	@Override
	public Connection getMaintenanceConnection(String url, String port, String user, String pass) throws SQLException {
		throw new SQLException("not implemented yet");
	}
	@Override
	public boolean disconnectOtherFromDB(Connection conn, String dbName) throws Exception {
		throw new SQLException("not implemented yet");
	}
	@Override
	public boolean existDB(Connection conn, String dbName) throws Exception {
	    try{
	        ResultSet resultSet = conn.getMetaData().getCatalogs();
	        while (resultSet.next()) {
	          String databaseName = resultSet.getString(1);
	            if(databaseName.equals(dbName)){
	                return true;
	            }
	        }
	        resultSet.close();
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }
		return false;
	}
}
