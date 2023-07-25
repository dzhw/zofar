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
package eu.dzhw.zofar.management.comm.tomcat;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.gargoylesoftware.htmlunit.Page;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
import eu.dzhw.zofar.management.comm.ssh.SSHClient;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class ManagerClient {
	/** The instance. */
	private static ManagerClient INSTANCE;
	private HTTPClient client;
	/**
	 * Instantiates a new ManagerClient utils.
	 */
	protected ManagerClient() {
		super();
		client = HTTPClient.getInstance();
	}
	/**
	 * Gets the single instance of ConfigurationUtils.
	 * 
	 * @return single instance of ConfigurationUtils
	 */
	public static ManagerClient getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ManagerClient();
		return INSTANCE;
	}
	public boolean deploy(final String serverUrl,final String sshUser,final String sshPass,Map<String,String> logins,final String appName,final File warFile) throws Exception{
		final String cleanedAppName = ("/"+appName).replaceAll("/{2,}", "/");
		String servername = serverUrl;
		servername = servername.replaceAll(Pattern.quote("https://"), "");
		servername = servername.replaceAll(Pattern.quote("http://"), "");
		int portIndex = servername.indexOf(':');
		if(portIndex != -1)servername = servername.substring(0, portIndex);
		int managerIndex = servername.indexOf("/manager/text");
		if(managerIndex != -1)servername = servername.substring(0, managerIndex);
		boolean localCopy = false;
		if(servername.equals("####"))localCopy = true;
		if(servername.equals("127.0.0.1"))localCopy = true;
		String query = "";
		if(!localCopy) {
			SSHClient.getInstance().scpTo(servername, sshUser, sshPass, warFile, "/tmp");
			query = serverUrl+"/manager/text/deploy?path="+cleanedAppName+"&war=file:/tmp/"+warFile.getName();
		}
		else {
			FileClient.getInstance().copyToDir(warFile, DirectoryClient.getInstance().getTemp());
			query = serverUrl+"/manager/text/deploy?path="+cleanedAppName+"&war=file:/tmp/"+warFile.getName();
		}
		return sendQuery(query,logins);
	}
	public boolean undeploy(final String serverUrl,Map<String,String> logins,final String appName) throws Exception{
		final String cleanedAppName = ("/"+appName).replaceAll("/{2,}", "/");
		final String query = serverUrl+"/manager/text/undeploy?path="+cleanedAppName+"";
		return sendQuery(query,logins);
	}
	public boolean start(final String serverUrl,Map<String,String> logins,final String appName) throws Exception{
		final String cleanedAppName = ("/"+appName).replaceAll("/{2,}", "/");
		final String query = serverUrl+"/manager/text/start?path="+cleanedAppName+"";
		return sendQuery(query,logins);
	}
	public boolean stop(final String serverUrl,Map<String,String> logins,final String appName) throws Exception{
		final String cleanedAppName = ("/"+appName).replaceAll("/{2,}", "/");
		final String query = serverUrl+"/manager/text/stop?path="+cleanedAppName+"";
		return sendQuery(query,logins);
	}
	public boolean reload(final String serverUrl,Map<String,String> logins,final String appName) throws Exception{
		final String cleanedAppName = ("/"+appName).replaceAll("/{2,}", "/");
		final String query = serverUrl+"/manager/text/reload?path="+cleanedAppName+"";
		return sendQuery(query,logins);
	}
	private boolean sendQuery(final String query,Map<String,String> logins) throws Exception{
		Page response = client.loadPage(query,logins);
		final String result = response.getWebResponse().getContentAsString();
		if(result != null){
			if(result.startsWith("FAIL"))throw new Exception(result);
			if(result.startsWith("OK"))return true;
		}
		return false;
	}
	public boolean isInstalled(final String name,final String serverUrl,Map<String,String> logins){
		final Map<String,Map<String,String>> applications = getApplicationList(serverUrl,logins);
		return applications.containsKey(name);
	}
	public Map<String,Map<String,String>> getApplicationList(final String serverUrl,Map<String,String> logins){
		final String query = serverUrl+"/manager/text/list";
		Page response = client.loadPage(query,logins);
		final String result = response.getWebResponse().getContentAsString();
		final String[] resultLines = result.split("\\n");
		final Map<String,Map<String,String>> back = new HashMap<String,Map<String,String>>();
		for(final String line : resultLines){
			if(line.indexOf(':') == -1)continue;
			String[] app = line.split(":");
			final String appPath = app[0].trim();
			final String appStatus = app[1].trim();
			final String appSessions = app[2].trim();
			final String appWar = app[3].trim();
			final Map<String,String> info = new LinkedHashMap<String,String>();
			info.put("Path", appPath);
			info.put("Status", appStatus);
			info.put("Sessions", appSessions);
			info.put("WAR", appWar);
			back.put(appPath, info);
		}
		return back;
	}
	public Map<String,String> getInfo(final String serverUrl,Map<String,String> logins){
		final String query = serverUrl+"/manager/text/serverinfo";
		Page response = client.loadPage(query,logins);
		final String result = response.getWebResponse().getContentAsString();
		final String[] resultLines = result.split("\\n");
		final Map<String,String> back = new HashMap<String,String>();
		for(final String line : resultLines){
			if(line.indexOf(':') == -1)continue;
			String[] pair = line.split(":");
			back.put(pair[0].trim(), pair[1].trim());
		}
		return back;
	}
}
