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
package eu.dzhw.zofar.management.generator.screenshots;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import eu.dzhw.zofar.management.comm.network.http.HTTPClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
public class ScreenshotGenerator {
	private static final ScreenshotGenerator INSTANCE = new ScreenshotGenerator();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ScreenshotGenerator.class);
	private final HTTPClient spider;
	private ScreenshotGenerator() {
		super();
		spider = HTTPClient.getInstance();
	}
	public static ScreenshotGenerator getInstance() {
		return INSTANCE;
	}
	public void spiderSurvey(final String url,final File screenshotDir) throws Exception{
		if(url == null) throw new Exception("URL is null");
		if(screenshotDir == null)throw new Exception("Screenshot directory is null");
		if(!screenshotDir.exists())throw new Exception("Screenshot directory does not exist");
		if(!screenshotDir.canWrite())throw new Exception("cannot write to Screenshot directory");
		spiderSurveyHelper(url,screenshotDir);
	}
	private void spiderSurveyHelper(final String url,final File screenshotDir) throws Exception{
		if(url == null) throw new Exception("URL is null");
		if(screenshotDir == null)throw new Exception("Screenshot directory is null");
		if(!screenshotDir.exists())throw new Exception("Screenshot directory does not exist");
		if(!screenshotDir.canWrite())throw new Exception("cannot write to Screenshot directory");
		final Page page = spider.loadPage(url);
		if((page != null)&&((HtmlPage.class).isAssignableFrom(page.getClass()))){
			final HtmlPage tmp = (HtmlPage)page;
			final String currentUrl = tmp.getUrl().toString();
			final File screenshot = spider.getScreenshot(tmp);
			if(screenshot != null) FileClient.getInstance().move(screenshot, screenshotDir);
			List<HtmlSubmitInput> buttons = spider.getSubmits(tmp);
			if (buttons != null) {
				for (final HtmlSubmitInput button : buttons) {
					if(button.getNameAttribute().endsWith("forwardBt")){
						final HtmlPage nextPage = button.click();
						final String nextUrl = nextPage.getUrl().toString();
						if(nextUrl.equals(currentUrl))throw new Exception("loop : "+nextUrl);
						if(nextUrl.endsWith("/end.html")){
							return;
						}
						spiderSurveyHelper(nextUrl,screenshotDir);
					}
				}
			}
		}
	}
}
