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
package eu.dzhw.zofar.management.comm.network.WebDAV;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class OwnCloudClient {
	private static final OwnCloudClient INSTANCE = new OwnCloudClient();

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OwnCloudClient.class);

	public OwnCloudClient() {
		super();
	}
	
	/**
	 * Gets the single instance of HTTPClient.
	 * 
	 * @return single instance of HTTPClient
	 */
	public static OwnCloudClient getInstance() {
		return INSTANCE;
	}

	public List<DavResource> list(final String url, final String user, final String pass) throws FailingHttpStatusCodeException, IOException {
		final WebDAVClient webdav = WebDAVClient.getInstance();
		return webdav.list(url, user, pass,"");
	}
	
	public List<DavResource> list(final String url, final String user, final String pass,final String baseDirectory) throws FailingHttpStatusCodeException, IOException {
		final WebDAVClient webdav = WebDAVClient.getInstance();
		return webdav.list(url, user, pass,baseDirectory);
	}
	
	public boolean exist(final String url, final String user, final String pass,final String directory,final String name) throws FailingHttpStatusCodeException, IOException {
		final WebDAVClient webdav = WebDAVClient.getInstance();
		return webdav.exist(url, user, pass,directory,name);
	}
	
	public void createDirectory(final String url, final String user, final String pass,final String directory,final String name) throws FailingHttpStatusCodeException, IOException {
		final WebDAVClient webdav = WebDAVClient.getInstance();
		webdav.createDir(url, user, pass, directory,name);
	}
	
	public void put(final String url, final String user, final String pass,final String directory,final File file) throws FailingHttpStatusCodeException, IOException {
		final WebDAVClient webdav = WebDAVClient.getInstance();
		webdav.put(url, user, pass, directory,file);
	}
	
	public File get(final String url, final String user, final String pass,final String directory,final String name) throws FailingHttpStatusCodeException, IOException {
		final WebDAVClient webdav = WebDAVClient.getInstance();
		return webdav.get(url, user, pass, directory,name);
	}
	
	public void delete(final String url, final String user, final String pass,final String directory,final String name) throws FailingHttpStatusCodeException, IOException {
		final WebDAVClient webdav = WebDAVClient.getInstance();
		webdav.delete(url, user, pass, directory,name);
	}
}
