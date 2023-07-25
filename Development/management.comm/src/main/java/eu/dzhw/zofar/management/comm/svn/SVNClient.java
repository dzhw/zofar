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
 * 
 */
package eu.dzhw.zofar.management.comm.svn;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNCommitPacket;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
/**
 * The Class SVNClient.
 */
public class SVNClient{
	/** The Constant INSTANCE. */
	private static final SVNClient INSTANCE = new SVNClient();
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(SVNClient.class);
	/**
	 * Instantiates a new SVN client.
	 */
	private SVNClient() {
		super();
		// http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
		System.setProperty("jsse.enableSNIExtension", "false");
	}
	/**
	 * Gets the single instance of SVNClient.
	 * 
	 * @return single instance of SVNClient
	 */
	public static synchronized SVNClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Get file from SVN Repository.
	 * 
	 * @param url
	 *            the url of Repository
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @param path
	 *            the path to File in Repository
	 * @param dir
	 *            the dir where the file will be stored
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SVNException
	 *             the SVN exception
	 */
	public File getFile(String url, String user, String password, String path, File dir) throws IOException, SVNException {
		SVNRepository repository = null;
		repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		repository.setAuthenticationManager(authManager);
		File tmp = new File(dir, path.substring(path.lastIndexOf('/') + 1));
		LOGGER.info("Loaded file {} from {}", tmp.getName(), url + "/" + path);
		FileOutputStream fos = new FileOutputStream(tmp);
		repository.getFile(path, -1, null, fos);
		fos.close();
		repository.closeSession();
		return tmp;
	}
	/**
	 * checks out head Version of Project from remote svn server .
	 * 
	 * @param url
	 *            the url of Repository
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @param path
	 *            the path to Project in Repository
	 * @param dir
	 *            the dir where the file will be stored
	 * @return the revision number of the working copy.
	 * @throws SVNException
	 *             the SVN exception
	 */
	public long doCheckout(String url, String user, String password, String path, final File dir) throws SVNException {
		if (user.isEmpty()) {
			throw new IllegalStateException("username must not be null.");
		}
		if (password.isEmpty()) {
			throw new IllegalStateException("password must not be null.");
		}
		final SVNURL svnUrl = SVNURL.parseURIEncoded(url + "/" + path);
		LOGGER.debug("creating the directory in which the project will be checked out.");
		final File projectDir = dir;
		if (!projectDir.exists()) {
			projectDir.mkdirs();
		}
		DirectoryClient.getInstance().cleanDirectory(projectDir);
		LOGGER.debug("set up the connection to the remote svn server");
		DAVRepositoryFactory.setup();
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		final SVNUpdateClient client = new SVNUpdateClient(authManager, SVNWCUtil.createDefaultOptions(true));
		LOGGER.debug("check out to: {}", projectDir.getAbsolutePath());
		return client.doCheckout(svnUrl, projectDir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
	}
	public void createProject(String url, String user, String password, final String projectName, final File tmpDir) throws SVNException {
		if (user.isEmpty()) {
			throw new IllegalStateException("username must not be null.");
		}
		if (password.isEmpty()) {
			throw new IllegalStateException("password must not be null.");
		}
		final String path = url + "/" + projectName;
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(path));
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		final SVNURL location = repository.getLocation();
		final SVNCommitClient client = new SVNCommitClient(authManager, SVNWCUtil.createDefaultOptions(true));
		client.doImport(tmpDir, location, "Initial Commit", null, false, false, SVNDepth.INFINITY);
		repository.closeSession();
		doCheckout(url, user, password, projectName, tmpDir);
	}
	public void deleteProject(String url,String user, String password, final String projectName) throws SVNException {
		if (user.isEmpty()) {
			throw new IllegalStateException("username must not be null.");
		}
		if (password.isEmpty()) {
			throw new IllegalStateException("password must not be null.");
		}
		final String path = url + "/" + projectName;
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(path));
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		final SVNCommitClient client = new SVNCommitClient(authManager, SVNWCUtil.createDefaultOptions(true));
		SVNURL[] urls = new SVNURL[1];
		urls[0] = repository.getLocation();
		client.doDelete(urls, "Project removed");
		repository.closeSession();
	}
	public void doCommit(String user, String password, final File toCommit, final String message) throws SVNException {
		if (user.isEmpty()) {
			throw new IllegalStateException("username must not be null.");
		}
		if (password.isEmpty()) {
			throw new IllegalStateException("password must not be null.");
		}
		File[] paths = new File[1];
		paths[0] = toCommit;
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		final SVNCommitClient client = new SVNCommitClient(authManager, SVNWCUtil.createDefaultOptions(true));
		SVNCommitPacket[] packets = null;
		packets = client.doCollectCommitItems(paths, true, true, SVNDepth.INFINITY, true, null);
		SVNCommitInfo[] result = client.doCommit(packets, true, true, message, null);
	}
	public List<String> getProjects(String url, String user, String password, String path) throws IOException, SVNException {
		SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		repository.setAuthenticationManager(authManager);
		List<SVNDirEntry> results = new ArrayList<SVNDirEntry>();
		repository.getDir(path, SVNRepository.INVALID_REVISION, false, results);
		List<String> back = new ArrayList<String>();
		for (final SVNDirEntry project : results) {
			final String name = project.getName();
			if (name != null)
				back.add(name);
		}
		repository.closeSession();
		return back;
	}
	public void unlock(String user, String password, File fileToUnlock) throws SVNException {
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		final SVNWCClient client = new SVNWCClient(authManager, SVNWCUtil.createDefaultOptions(true));
		File[] paths = new File[1];
		paths[0] = fileToUnlock;
		client.doUnlock(paths, true);
	}
	public void lock(String user, String password, File fileToLock,final String comment) throws SVNException {
		final ISVNAuthenticationManager authManager = new BasicAuthenticationManager(user, password);
		final SVNWCClient client = new SVNWCClient(authManager, SVNWCUtil.createDefaultOptions(true));
		File[] paths = new File[1];
		paths[0] = fileToLock;
		client.doLock(paths, true, comment);
	}
}
