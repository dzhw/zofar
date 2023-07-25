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
package eu.dzhw.zofar.management.comm.network.webservices;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class RemoteScreenshot {
	/** The Constant INSTANCE. */
	private static final RemoteScreenshot INSTANCE = new RemoteScreenshot();
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteScreenshot.class);
	private static final String USER_NAME = "dzhwgmbh";
	private static final String USER_KEY = "c15048e6-35f1-461d-b865-8201f31d9303";
	private static final String SAUCELABS_URL = "https://" + USER_NAME + ":" + USER_KEY + "@ondemand.saucelabs.com:443/wd/hub";
	/**
	 * Instantiates a new HTTP client.
	 */
	private RemoteScreenshot() {
		super();
	}
	public static RemoteScreenshot getInstance() {
		return INSTANCE;
	}
	//https://wiki.saucelabs.com/display/DOCS/Platform+Configurator#/
	public File getScreenshot(final String url,final DesiredCapabilities caps) throws MalformedURLException {
		final FileClient fileClient = FileClient.getInstance();
	    LOGGER.info("Use Caps : {}",caps.toString());
	    WebDriver driver = new RemoteWebDriver(new URL(SAUCELABS_URL), caps);
	    driver.get(url);
	    System.out.println("title of page is: " + driver.getTitle());
	    final String name = url.replace(':', '_').replace('/', '_');
		final File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		final File targetFile = fileClient.createOrGetFile(name, ".png", DirectoryClient.getInstance().getTemp());
		fileClient.copyFile(scrFile, targetFile);
		driver.quit();
		return targetFile;
	}
}
