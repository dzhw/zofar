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
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import com.github.sardine.impl.SardineRedirectStrategy;
import com.github.sardine.impl.methods.HttpMkCol;
import com.github.sardine.impl.methods.HttpPropFind;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class WebDAVClient {
	private class CustomRedirectStrategy extends SardineRedirectStrategy{
		private final String[] REDIRECT_METHODS = new String[] { HttpGet.METHOD_NAME, HttpPost.METHOD_NAME, HttpHead.METHOD_NAME, HttpPut.METHOD_NAME, HttpDelete.METHOD_NAME, HttpMkCol.METHOD_NAME };
		@Override
		protected boolean isRedirectable(String method) {
			for (final String m : REDIRECT_METHODS) {
				  if (m.equalsIgnoreCase(method)) {
				    return true;
				  }
				}
				return method.equalsIgnoreCase(HttpPropFind.METHOD_NAME);
		}
		@Override
		public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
				throws ProtocolException {
			final URI uri = getLocationURI(request, response, context);
			final String method = request.getRequestLine().getMethod();
			if (method.equalsIgnoreCase(HttpHead.METHOD_NAME)) {
			  return new HttpHead(uri);
			} else if (method.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
			  return new HttpGet(uri);
			} else if (method.equalsIgnoreCase(HttpPut.METHOD_NAME)) {
			  HttpPut httpPut = new HttpPut(uri);
			  httpPut.setEntity(((HttpEntityEnclosingRequest) request).getEntity());
			  return httpPut;
			} else if (method.equalsIgnoreCase("MKCOL")) {
			  return new HttpMkCol(uri);
			} else if (method.equalsIgnoreCase("DELETE")) {
			  return new HttpDelete(uri);
			} else {
			  final int status = response.getStatusLine().getStatusCode();
			  if (status == HttpStatus.SC_TEMPORARY_REDIRECT) {
			    return RequestBuilder.copy(request).setUri(uri).build();
			  } else {
			    return new HttpGet(uri);
			  }
			}
		}
	}
	private final Sardine sardine;
	private static final WebDAVClient INSTANCE = new WebDAVClient();
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebDAVClient.class);
	private WebDAVClient() {
		super();
		sardine = new SardineImpl() {
			protected SardineRedirectStrategy createDefaultRedirectStrategy()
			{
				return new CustomRedirectStrategy();
			}
		};
	}
	public static WebDAVClient getInstance() {
		return INSTANCE;
	}
	public List<DavResource> list(final String url, final String user, final String pass,final String baseDirectory) throws IOException {
		sardine.setCredentials(user, pass);
		List<DavResource> resources = sardine.list(url+"/"+baseDirectory);
		final List<DavResource> back = new ArrayList<DavResource>();
		for (DavResource res : resources)
		{
			back.add(res);
		}
		return back;
	}
	public boolean exist(final String url, final String user, final String pass,final String directory,final String name) throws IOException {
		sardine.setCredentials(user, pass);
		return sardine.exists(url+"/"+directory+"/"+name);
	}
	public void put(final String url, final String user, final String pass,final String directory,final File file) throws FailingHttpStatusCodeException, IOException {
		System.out.println("WebDAV put : "+url+"/"+directory+"/"+file.getName());
		sardine.setCredentials(user, pass);
		sardine.put(url+"/"+directory+"/"+file.getName(), file, Files.probeContentType(file.toPath()));
	}
	public File get(final String url, final String user, final String pass,final String directory,final String name) throws FailingHttpStatusCodeException, IOException {
		sardine.setCredentials(user, pass);
		final InputStream is = sardine.get(url+"/"+directory+"/"+name);
	    File targetFile = FileClient.getInstance().createTempFile(name, ".downloadedFromWebDav");
	    java.nio.file.Files.copy(is,targetFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
	    IOUtils.closeQuietly(is);
	    return targetFile;
	}
	public void createDir(final String url, final String user, final String pass,final String parentDirectory,final String directoryName) throws FailingHttpStatusCodeException, IOException {
		sardine.setCredentials(user, pass);
		sardine.createDirectory(url+"/"+parentDirectory+"/"+directoryName);
	}
	public void delete(final String url, final String user, final String pass,final String directory,final String name) throws FailingHttpStatusCodeException, IOException {
		sardine.setCredentials(user, pass);
		sardine.delete(url+"/"+directory+"/"+name);
	}
	public void copy(final String url, final String user, final String pass,final String directoryFrom,final String nameFrom,final String directoryTo,final String nameTo,final boolean overwrite) throws FailingHttpStatusCodeException, IOException {
		sardine.setCredentials(user, pass);
		sardine.copy(url+"/"+directoryFrom+"/"+nameFrom, url+"/"+directoryTo+"/"+nameTo, overwrite);
	}
	public void move(final String url, final String user, final String pass,final String directoryFrom,final String nameFrom,final String directoryTo,final String nameTo,final boolean overwrite) throws FailingHttpStatusCodeException, IOException {
		sardine.setCredentials(user, pass);
		sardine.move(url+"/"+directoryFrom+"/"+nameFrom, url+"/"+directoryTo+"/"+nameTo, overwrite);
	}
	public List<DavResource> patch(final String url, final String user, final String pass,final String directory,final String name,final Map<String,String> properties) throws FailingHttpStatusCodeException, IOException {
		sardine.setCredentials(user, pass);
		sardine.delete(url+"/"+directory+"/"+name);
		Map<QName,String> patchProperties = new LinkedHashMap<QName,String>();
		for(final Map.Entry<String,String> property : properties.entrySet()) {
			final QName patchedProperty = new QName(property.getKey());
			patchProperties.put(patchedProperty, property.getValue());
		}
		return sardine.patch(url+"/"+directory+"/"+name, patchProperties);
	}
}
