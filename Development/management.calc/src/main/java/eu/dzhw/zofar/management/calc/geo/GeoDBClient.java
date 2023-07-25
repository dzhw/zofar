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
package eu.dzhw.zofar.management.calc.geo;
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import com.jcraft.jsch.Logger;
import eu.dzhw.zofar.management.comm.db.DBClient;
import eu.dzhw.zofar.management.comm.db.mysql.MySQLClient;
public class GeoDBClient extends AbstractGeoClient {
	private static final long serialVersionUID = -5554989713039981534L;
	private final String dbServer;
	private final int dbPort;
	private final String dbName;
	private final String dbUser;
	private final String dbPass;
	public GeoDBClient(final String dbServer, final int dbPort, final String dbName,final String dbUser, final String dbPass) {
		super();
		this.dbServer = dbServer;
		this.dbPort = dbPort;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
	}
	@Override
	public Point2D getLocation(Object data) throws Exception {
		if(data == null)return null;
		final Map<String,Object> tmp = (Map<String,Object>)data;
		if(tmp.containsKey("coords")){
			Map<String,String> coords = (Map<String,String>)tmp.get("coords");
			if(coords.containsKey("lat")&& coords.containsKey("lon")){
				final Double latitude = Double.parseDouble(coords.get("lat"));
				final Double longitude = Double.parseDouble(coords.get("lon"));
				return new Point2D.Double(latitude,longitude);
			}
		}
		return null;
	}
	public Map<String, List<Map<String, Object>>> getObjectByPostcode(final Object postcode, final String country) throws Exception {
		final DBClient db = MySQLClient.getInstance();
		Connection conn = null;
		try {
			conn = db.getConnection(dbServer, dbPort+"", dbName, dbUser, dbPass);
			final List<Map<String, String>> result = db.queryDb(conn, "SELECT gl.loc_id FROM geodb_textdata plz LEFT JOIN geodb_textdata name ON name.loc_id = plz.loc_id LEFT JOIN geodb_locations gl ON gl.loc_id = plz.loc_id LEFT JOIN geodb_coordinates coord ON plz.loc_id = coord.loc_id WHERE plz.text_type =500300000 AND plz.text_val = '"+postcode+"' AND name.text_type =500100000 AND  gl.loc_type =100600000");
			if(result != null){
				Map<String, List<Map<String, Object>>> back = new HashMap<String, List<Map<String, Object>>>();
				for(final Map<String, String> data:result){
					final String loc_id = data.get("loc_id");
					Map<String,Object> details = getDetails(loc_id);
					if(details != null){
						details.put("postalcode", postcode+"");
						List<Map<String, Object>> detailsList = null;
						if(back.containsKey(loc_id))detailsList = back.get(loc_id);
						if(detailsList == null)detailsList = new ArrayList<Map<String, Object>>();
						detailsList.add(details);
						back.put(loc_id,detailsList);
					}
				}
				return back;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(conn != null)db.close(conn);
		}
		return null;
	}
	public Map<String, List<Map<String, Object>>> getObjectByName(final String name, final String country) throws Exception {
		final DBClient db = MySQLClient.getInstance();
		Connection conn = null;
		try {
			conn = db.getConnection(dbServer, dbPort+"", dbName, dbUser, dbPass);
			final List<Map<String, String>> result = db.queryDb(conn, "SELECT loc_id FROM geodb_textdata WHERE text_val LIKE '"+name+"' AND text_type IN (500100000);");
			if(result != null){
				Map<String, List<Map<String, Object>>> back = new HashMap<String, List<Map<String, Object>>>();
				for(final Map<String, String> data:result){
					final String loc_id = data.get("loc_id");
					Map<String,Object> details = getDetails(loc_id);
					if(details != null){
						List<Map<String, Object>> detailsList = null;
						if(back.containsKey(loc_id))detailsList = back.get(loc_id);
						if(detailsList == null)detailsList = new ArrayList<Map<String, Object>>();
						detailsList.add(details);
						back.put(loc_id,detailsList);
					}
				}
				return back;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(conn != null)db.close(conn);
		}
		return null;
	}
	private Map<String,Object> getDetails(String loc_id){
		final DBClient db = MySQLClient.getInstance();
		final Map<String,String> typeMap = new HashMap<String,String>();
		typeMap.put("500300000", "Postleitzahl");
		typeMap.put("100800000", "Postleitzahlgebiet");
		typeMap.put("100400000", "Regierungsbezirk");
		typeMap.put("100200000", "Staat");
		typeMap.put("100300000", "Staatsteil");
		typeMap.put("500400000", "Telefonvorwahl");
		typeMap.put("400300000", "Typ");
		typeMap.put("650700001", "Einwohnerzahl");
		typeMap.put("100700000", "Ortschaft");
		typeMap.put("500500000", "KFZ-Kennzeichen");
		typeMap.put("100500000", "Landkreis");
		typeMap.put("500100000", "Name");
		typeMap.put("400200000", "Ebene");
		typeMap.put("500600000", "Gemeindeschlüssel");
		typeMap.put("500100002", "Sortiername");
		Connection conn = null;
		try {
			conn = db.getConnection(dbServer, dbPort+"", dbName, dbUser, dbPass);
			final Map<String,String> coords = getCoordinates(conn,loc_id);
			final Map<String,Object> details = getDetailsHelper(conn,loc_id,typeMap);
			if(details != null){
				details.put("Identifier", details.get("Name"));
				Map<String,Object> plainDetails = plainDetails(details);
				if(coords!=null)plainDetails.put("coords", coords);
				return plainDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(conn != null)db.close(conn);
		}		
		return null;
	}
	private Map<String,Object> plainDetails(Map<String,Object> details){
		if(details == null)return null;
		Map<String,Object> plainDetails = new LinkedHashMap<String,Object>();
		boolean flag = false;
		if(details.containsKey("Name")&& details.containsKey("Typ")){
			final String key = (details.get("Typ")+"").replaceAll(" ", "_").replaceAll(Pattern.quote("."), "");
			if(!plainDetails.containsKey(key))plainDetails.put(key, details.get("Name"));
			flag = true;
		}
		for(final Map.Entry<String, Object> data:details.entrySet()){
			if(data.getKey().equals("parent"))continue;
			if(flag){
				if(data.getKey().equals("Name"))continue;
				if(data.getKey().equals("Typ"))continue;
			}
			if(!plainDetails.containsKey(data.getKey()))plainDetails.put(data.getKey(), data.getValue());
		}
		if(details.containsKey("parent")){
			Map<String,Object> parentDetails  = plainDetails((Map<String,Object>)details.get("parent"));
			if(parentDetails != null){
				for(final Map.Entry<String, Object> data:parentDetails.entrySet()){
					if(!plainDetails.containsKey(data.getKey()))plainDetails.put(data.getKey(), data.getValue());
				}
			}
		}
		return plainDetails;
	}
	private Map<String,Object> getDetailsHelper(Connection conn, String loc_id, final Map<String,String> typeMap){
		final DBClient db = MySQLClient.getInstance();
		try {
			final List<Map<String, String>> result = db.queryDb(conn, "SELECT * FROM geodb_textdata WHERE loc_id='"+loc_id+"';");
			if(result != null){
				final Map<String,Object> details = new LinkedHashMap<String,Object>();
				details.put("loc_id", loc_id);
				for(final Map<String, String> data:result){
					String type = data.get("text_type");
					final String value = data.get("text_val");
					if(type.equals("400100000")){
						details.put("parent",getDetailsHelper(conn,value,typeMap));
					}
					else{
						Object valueObj = null;
						if(typeMap.containsKey(type))type = typeMap.get(type);
						if(details.containsKey(type)){
							valueObj = details.get(type);
							if(((List.class).isAssignableFrom(valueObj.getClass()))){
							}
							else{
								List valueList = new ArrayList<Object>();
								valueList.add(valueObj);
								valueObj = valueList;
							}
							((List)valueObj).add(value);
							details.put(type, valueObj);
						}
						else details.put(type, value);
					}
				}
				return details;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	private Map<String,String> getCoordinates(Connection conn, String loc_id){
		final DBClient db = MySQLClient.getInstance();
		try {
			final List<Map<String, String>> result = db.queryDb(conn, "SELECT * FROM geodb_coordinates WHERE loc_id="+loc_id+" AND coord_type=200100000;");
			if(result != null){
				final Map<String,String> coords = new LinkedHashMap<String,String>();
				for(final Map<String, String> data:result){
					final String lat = data.get("lat");
					final String lon = data.get("lon");
					coords.put("lat", lat);
					coords.put("lon", lon);
				}
				return coords;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
}
