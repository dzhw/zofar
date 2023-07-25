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
 * Class to convenient interact with Web-Pages
 */
package eu.dzhw.zofar.management.comm.network.http;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.fit.cssbox.demo.ImageRenderer;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
/**
 * The Class HTTPClient.
 */
public class HTTPClient {
	/** The Constant INSTANCE. */
	private static final HTTPClient INSTANCE = new HTTPClient();
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(HTTPClient.class);
	/** The web client. */
	private final WebClient webClient;
	/**
	 * Instantiates a new HTTP client.
	 */
	private HTTPClient() {
		super();
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "trace");
		webClient = new WebClient();
		final WebClientOptions options = webClient.getOptions();
		options.setThrowExceptionOnScriptError(false);
		options.setRedirectEnabled(true);
	}
	/**
	 * Gets the single instance of HTTPClient.
	 * 
	 * @return single instance of HTTPClient
	 */
	public static HTTPClient getInstance() {
		return INSTANCE;
	}
	public void setJavaScriptEnabled(final boolean flag) {
		final WebClientOptions options = webClient.getOptions();
		options.setJavaScriptEnabled(flag);
		if(!flag)webClient.getWebConsole().setLogger(null);
		else{
			webClient.getWebConsole().setLogger(new com.gargoylesoftware.htmlunit.WebConsole.Logger() {
				public boolean isTraceEnabled() {
					return true;
				}
				public boolean isDebugEnabled() {
					return true;
				}
				public boolean isInfoEnabled() {
					return true;
				}
				public boolean isWarnEnabled() {
					return true;
				}
				public boolean isErrorEnabled() {
					return true;
				}
				public void trace(Object message) {
					LOGGER.trace("[JSCONSOLE] "+message);
				}
				public void debug(Object message) {
					LOGGER.debug("[JSCONSOLE] "+message);
				}
				public void info(Object message) {
					LOGGER.info("[JSCONSOLE] "+message);
				}
				public void warn(Object message) {
					LOGGER.warn("[JSCONSOLE] "+message);
				}
				public void error(Object message) {
					LOGGER.error("[JSCONSOLE] "+message);
				}
			});
		}
	}
	public void setCSSEnabled(final boolean flag) {
		final WebClientOptions options = webClient.getOptions();
		options.setCssEnabled(flag);
	}
	public void printContentIfNecessary(final boolean flag) {
		final WebClientOptions options = webClient.getOptions();
		options.setPrintContentOnFailingStatusCode(flag);
	}
	public URL getParent(final Page page) throws MalformedURLException {
		final URL url = page.getUrl();
		final String parentPath = new File(page.getUrl().getPath()).getParent();
		final URL parentUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), parentPath);
		return parentUrl;
	}
	public URL getRoot(final Page page) throws MalformedURLException {
		final URL url = page.getUrl();
		final URL rootUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
		return rootUrl;
	}
	/**
	 * Load Web-Page.
	 * 
	 * @param url
	 *            the url
	 * @return HtmlPage
	 */
	public Page loadPage(final String url) throws Exception {
		final Page page = webClient.getPage(url);
		return page;
	}
	public Page loadPage(final File file) throws Exception {
		final Page page = webClient.getPage(file.toURI().toURL());
		return page;
	}
	public Page post(final String url,final Map<String,String> headers,final String body) throws FailingHttpStatusCodeException, IOException{
	    WebRequest requestHeaders = new WebRequest(new URL(url), HttpMethod.POST);
	    for(Map.Entry<String, String> header : headers.entrySet()){
	    	requestHeaders.setAdditionalHeader(header.getKey(), header.getValue());
	    }
	    requestHeaders.setRequestBody(body);
	    return webClient.getPage(requestHeaders);
	}
	public void closeAllWindows() {
		webClient.close();
	}
	/**
	 * Load Secured Web-Page.
	 * 
	 * @param url
	 *            the url
	 * @return HtmlPage
	 */
	@Deprecated
	public Page loadPage(final String url, final String user, final String pass) {
		final Map<String, String> logins = new HashMap<String, String>();
		logins.put(user, pass);
		return loadPage(url, logins);
	}
	public Page loadPage(final String url, Map<String, String> logins) {
		try {
			DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
			if (logins != null) {
				for (Map.Entry<String, String> login : logins.entrySet()) {
					credentialsProvider.addCredentials(login.getKey(), login.getValue());
				}
			}
			final Page page = webClient.getPage(url);
			credentialsProvider.clear();
			return page;
		} catch (final FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Map<String, String> getQueryMap(final HtmlPage page) throws URISyntaxException, MalformedURLException {
		if (page == null)
			return null;
		URL url = page.getUrl();
		String urlStr = url.toString();
		if (urlStr.indexOf('?') == -1) {
			final int index = urlStr.indexOf(';');
			if (index > -1)
				urlStr = urlStr.substring(0, index) + "?" + urlStr.substring(index + 1);
			url = new URL(urlStr);
		}
		final String query = url.getQuery();
		Map<String, String> map = new HashMap<String, String>();
		if (query != null) {
			String[] params = query.split("&");
			for (String param : params) {
				String name = param.split("=")[0];
				String value = param.split("=")[1];
				map.put(name, value);
			}
		}
		return map;
	}
	/**
	 * Retrieve filtered Links from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @param filter
	 *            XPath filter 
	 * @return A list representations of HTML "anchor" element.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlAnchor> getLinks(final HtmlPage page, final String filter) {
		return (List<HtmlAnchor>) page.getByXPath("//a[" + filter + "]");
	}
	/**
	 * Retrieve all Links from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "anchor" element.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlAnchor> getLinks(final HtmlPage page) {
		if (page == null)
			return null;
		return (List<HtmlAnchor>) page.getByXPath("//a");
	}
	/**
	 * Retrieve all Submits from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "input" element of type submit.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlSubmitInput> getSubmits(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlSubmitInput> submits = new ArrayList<HtmlSubmitInput>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				submits.addAll((List<HtmlSubmitInput>) form.getByXPath("//input[@type='submit']"));
			}
		}
		return submits;
	}
	@SuppressWarnings("unchecked")
	public List<HtmlInput> getInputs(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlInput> inputs = new ArrayList<HtmlInput>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				inputs.addAll((List<HtmlInput>) form.getByXPath("//input[@type]"));
			}
		}
		return inputs;
	}
	/**
	 * Retrieve all Input Fields from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "input" element of type text.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlTextInput> getFields(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlTextInput> fields = new ArrayList<HtmlTextInput>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				fields.addAll((List<HtmlTextInput>) form.getByXPath("//input[@type='text']"));
			}
		}
		return fields;
	}
	/**
	 * Retrieve all hidden Input Fields from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "input" element of type text.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlHiddenInput> getHiddenFields(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlHiddenInput> fields = new ArrayList<HtmlHiddenInput>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				fields.addAll((List<HtmlHiddenInput>) form.getByXPath("//input[@type='hidden']"));
			}
		}
		return fields;
	}
	/**
	 * Retrieve all Radio-Buttons from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "input" element of type text.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlRadioButtonInput> getRadios(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlRadioButtonInput> fields = new ArrayList<HtmlRadioButtonInput>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				fields.addAll((List<HtmlRadioButtonInput>) form.getByXPath("//input[@type='radio']"));
			}
		}
		return fields;
	}
	/**
	 * Retrieve all Checkboxes from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "input" element of type text.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlCheckBoxInput> getCheckboxes(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlCheckBoxInput> fields = new ArrayList<HtmlCheckBoxInput>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				fields.addAll((List<HtmlCheckBoxInput>) form.getByXPath("//input[@type='checkbox']"));
			}
		}
		return fields;
	}
	/**
	 * Retrieve all Selectboxes from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "select" element of type text.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlSelect> getSelects(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlSelect> fields = new ArrayList<HtmlSelect>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				fields.addAll((List<HtmlSelect>) form.getByXPath("//select"));
			}
		}
		return fields;
	}
	public List<HtmlPasswordInput> getPasswordFields(HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlPasswordInput> fields = new ArrayList<HtmlPasswordInput>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				fields.addAll((List<HtmlPasswordInput>) form.getByXPath("//input[@type='password']"));
			}
		}
		return fields;
	}
	/**
	 * Retrieve all DIV from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "div" element of type submit.
	 */
	@SuppressWarnings("unchecked")
	public List<HtmlDivision> getDivs(final HtmlPage page) {
		if (page == null)
			return null;
		final List<HtmlForm> forms = page.getForms();
		final List<HtmlDivision> fields = new ArrayList<HtmlDivision>();
		if (forms != null) {
			final Iterator<HtmlForm> it = forms.iterator();
			while (it.hasNext()) {
				final HtmlForm form = it.next();
				fields.addAll((List<HtmlDivision>) form.getByXPath("//div"));
			}
		}
		return fields;
	}
	/**
	 * Retrieve all DIV from HtmlPage.
	 * 
	 * @param page
	 *            the page
	 * @return A list representations of HTML "div" element of type submit.
	 */
	// fields.addAll((List<HtmlElement>) form.getByXPath("//*[contains(concat('
	@SuppressWarnings("unchecked")
	public List<HtmlElement> getItemsByClass(final HtmlElement parent, final String className) {
		if (parent == null)
			return null;
		final List<HtmlElement> fields = new ArrayList<HtmlElement>();
		fields.addAll((List<HtmlElement>) parent.getByXPath("//*[contains(concat(' ', @class, ' '), ' " + className + " ')]"));
		return fields;
	}
	public File savePage(final HtmlPage page, final File saveDir) throws Exception {
		if (saveDir == null)
			throw new Exception("Save directory is null");
		if (!saveDir.exists())
			throw new Exception("Save directory does not exist");
		if (!saveDir.canWrite())
			throw new Exception("cannot write to Save directory");
		final String name = page.getUrl().toString().replace('/', '_').replace(':', '_').replace('.', '_').replaceAll("_{2,}", "_");
		final File savedHtml = new File(saveDir, name + ".html");
		page.save(savedHtml);
		return savedHtml;
	}
	public File getScreenshot(final HtmlPage page) throws Exception {
		return this.getScreenshot(page, page.getUrl().toString().replace('/', '_').replace(':', '_').replace('.', '_').replaceAll("_{2,}", "_"));
	}
	public File getScreenshot(final HtmlPage page, final String name) throws Exception {
		final DirectoryClient directoryClient = DirectoryClient.getInstance();
		final File cache = directoryClient.createDir(directoryClient.getTemp(), "screenshotCache");
		directoryClient.cleanDirectory(cache);
		System.out.println("enc : "+page.getPageEncoding());
		final File savedHtml = this.savePage(page, cache);
		if (savedHtml != null) {
			return this.getScreenshot(savedHtml, cache, name, true);
		}
		return null;
	}
	public Document getSVG(final HtmlPage page) throws Exception {
		final DirectoryClient directoryClient = DirectoryClient.getInstance();
		final File cache = directoryClient.createDir(directoryClient.getTemp(), "screenshotCache");
		directoryClient.cleanDirectory(cache);
		final File savedHtml = this.savePage(page, cache);
		System.out.println("saved : " + savedHtml.toURI().toURL());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ImageRenderer imageRenderer = new ImageRenderer();
		imageRenderer.renderURL(savedHtml.toURI().toURL() + "", stream, ImageRenderer.Type.SVG);
		stream.flush();
		final byte[] svg = stream.toByteArray();
		stream.close();
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Reader targetReader = new InputStreamReader(new ByteArrayInputStream(svg));
		Document doc = db.parse(new InputSource(targetReader));
		return doc;
	}
	public File getScreenshot(final HtmlPage page, final File cache, final String name, final boolean dummyFlag) throws Exception {
		final FileClient fileClient = FileClient.getInstance();
		LOGGER.info("Take screenshot from " + page.getUrl());
			System.setProperty("webdriver.chrome.driver", "/home/####/Downloads/chromedriver");
			System.setProperty("webdriver.gecko.driver", "/home/####/Downloads/geckodriver");
			System.setProperty("webdriver.firefox.bin", "/usr/bin/firefox45");
			System.setProperty("webdriver.opera.driver", "/home/####/Downloads/operadriver");
			final WebDriver driver = new FirefoxDriver();
			final File tmpFile = fileClient.createOrGetFile(name, ".tmp", cache);
			fileClient.writeToFile(tmpFile, page.asXml(), false);
			driver.get(tmpFile.toURI().toURL().toString());
			final File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			final File targetFile = fileClient.createOrGetFile(name, ".png", cache);
			fileClient.copyFile(scrFile, targetFile);
			fileClient.deleteFile(tmpFile);
			driver.quit();
			return targetFile;
	}
	public File getScreenshot(final File savedHtml, final File cache, final String name, final boolean dummyFlag) throws Exception {
		final FileClient fileClient = FileClient.getInstance();
		String localPath = savedHtml.getAbsolutePath();
		String url = "file:
		LOGGER.info("Take screenshot from " + url);
		if (savedHtml != null) {
			System.setProperty("webdriver.chrome.driver", "/home/####/Downloads/chromedriver");
			System.setProperty("webdriver.gecko.driver", "/home/####/Downloads/geckodriver");
			System.setProperty("webdriver.firefox.bin", "/usr/bin/firefox45");
			System.setProperty("webdriver.opera.driver", "/home/####/Downloads/operadriver");
			final WebDriver driver = new FirefoxDriver();
			driver.get(url);
			final File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			final File targetFile = fileClient.createOrGetFile(name, ".png", cache);
			fileClient.copyFile(scrFile, targetFile);
			driver.quit();
			return targetFile;
		}
		return null;
	}
}
