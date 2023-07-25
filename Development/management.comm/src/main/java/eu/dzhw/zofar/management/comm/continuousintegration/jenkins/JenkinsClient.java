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
package eu.dzhw.zofar.management.comm.continuousintegration.jenkins;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class JenkinsClient {
	private static final JenkinsClient INSTANCE = new JenkinsClient();
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsClient.class);
	/**
	 * Instantiates a new HTTP client.
	 */
	private JenkinsClient() {
		super();
	}
	/**
	 * Gets the single instance of HTTPClient.
	 * 
	 * @return single instance of HTTPClient
	 */
	public static JenkinsClient getInstance() {
		return INSTANCE;
	}
	public JenkinsServer getServer(final String url, final String user, final String pass) throws URISyntaxException {
		return new JenkinsServer(new URI(url), user, pass);
	}
	public Map<String, Job> getJobs(final JenkinsServer server) throws Exception {
		if(server == null)throw new Exception("JenkinsServer is null");
		return server.getJobs();
	}
	public JobWithDetails getJob(final JenkinsServer server,final String name) throws Exception {
		if(server == null)throw new Exception("JenkinsServer is null");
		return server.getJob(name);
	}
	public JobWithDetails createJob(final JenkinsServer server,final String name,final String svnServer,final String svnPath, final String dbhost,final String dbport, final String dbName,final String dbUser, final String dbPass) throws Exception {
		if(server == null)throw new Exception("JenkinsServer is null");
		final FileClient file = FileClient.getInstance();
		File configFile = file.getResource("jenkins/jenkins_config.xml");
		System.out.println("Jenkins Config File ("+configFile.exists()+") "+configFile);
		if ((configFile != null) && (configFile.exists())) {
			Map<String, String> replacements = new HashMap<String,String>();
			replacements.put("NAME", name);
			replacements.put("SVNSERVER", svnServer);
			replacements.put("SVNPATH", svnPath);
			replacements.put("DBHOST", dbhost);
			replacements.put("DBPORT", dbport);
			replacements.put("DBUSER", dbUser);
			replacements.put("DBPASS", dbPass);
			replacements.put("DBNAME", dbName);
			String config = file.readAsString(configFile);
			for (final Map.Entry<String, String> replacement : replacements.entrySet()) {
				config = config.replaceAll(Pattern.quote("{" + replacement.getKey() + "}"), replacement.getValue());
			}
			server.createJob(name, config,true);
		}
		return getJob(server,name);
	}
}
