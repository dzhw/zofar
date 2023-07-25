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
package eu.dzhw.zofar.management.dev.automation.screenshot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.images.ImageConverter;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
public class ScreenshotGenerator {
	private static final ScreenshotGenerator INSTANCE = new ScreenshotGenerator();
	private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotGenerator.class);
	private ScreenshotGenerator() {
		super();
	}
	public static ScreenshotGenerator getInstance() {
		return INSTANCE;
	}
	public void spiderSurvey(final String url, final File screenshotDir, final ArrayList<java.awt.Dimension> dimensions,
			boolean maximizeHeight, final List<String> languages) throws Exception {
		if (url == null)
			throw new Exception("URL is null");
		if (screenshotDir == null)
			throw new Exception("Screenshot directory is null");
		if (!screenshotDir.exists())
			throw new Exception("Screenshot directory does not exist");
		if (!screenshotDir.canWrite())
			throw new Exception("cannot write to Screenshot directory");
		final DirectoryClient directoryClient = DirectoryClient.getInstance();
		final File cache = directoryClient.createDir(directoryClient.getTemp(), "screenshotFileCache");
		directoryClient.cleanDirectory(cache);
		spiderSurveyHelperAsFiles(url, cache, screenshotDir, dimensions, maximizeHeight, languages);
		directoryClient.cleanDirectory(cache);
	}
	public void spiderSurveyForMDM(final String url, final File screenshotDir,
			final ArrayList<java.awt.Dimension> dimensions, boolean maximizeHeight, final List<String> languages)
			throws Exception {
		if (url == null)
			throw new Exception("URL is null");
		if (screenshotDir == null)
			throw new Exception("Screenshot directory is null");
		if (!screenshotDir.exists())
			throw new Exception("Screenshot directory does not exist");
		if (!screenshotDir.canWrite())
			throw new Exception("cannot write to Screenshot directory");
		final DirectoryClient directoryClient = DirectoryClient.getInstance();
		final File cache = directoryClient.createDir(directoryClient.getTemp(), "screenshotFileCache");
		directoryClient.cleanDirectory(cache);
		spiderSurveyHelperAsFilesForMDM(url, cache, screenshotDir, dimensions, maximizeHeight, languages);
		directoryClient.cleanDirectory(cache);
	}
	private void spiderSurveyHelperAsFiles(String url, File cache, File screenshotDir,
			final ArrayList<java.awt.Dimension> dimensions, boolean maximizeHeight, final List<String> languages)
			throws Exception {
		if (url == null)
			throw new Exception("URL is null");
		if (cache == null)
			throw new Exception("cache directory is null");
		if (!cache.exists())
			throw new Exception("cache directory does not exist");
		if (!cache.canWrite())
			throw new Exception("cannot write to cache directory");
		System.setProperty("webdriver.chrome.driver", "/home/####/Entwicklung/Browser/chromedriver");
		final WebDriver driver = new ChromeDriver();
		driver.get(url);
		String currentUrl = driver.getCurrentUrl();
		ArrayList<Dimension> windowDimensions = new ArrayList<Dimension>();
		driver.manage().window().maximize();
		int maxHeight = driver.manage().window().getSize().getHeight();
		int maxWidth = driver.manage().window().getSize().getWidth();
		LOGGER.info("Max Dimension : {} x {}", maxWidth, maxHeight);
		boolean overrideCarousel = false;
		if ((dimensions == null) || (dimensions.isEmpty())) {
			windowDimensions.add(new Dimension(1280, 720));
			overrideCarousel = true;
		} else {
			for (final java.awt.Dimension dim : dimensions) {
				final int width = (int) dim.getWidth();
				int height = (int) dim.getHeight();
				if (maximizeHeight)
					height = (int) Math.max(height, maxHeight);
				windowDimensions.add(new Dimension(width, height));
			}
		}
		while (currentUrl != null) {
			final Map<String, String> variantURLS = new LinkedHashMap<String, String>();
			if ((languages == null) || languages.isEmpty()) {
				variantURLS.put(currentUrl, "");
			} else {
				final boolean hasParameter = currentUrl.contains("?");
				for (final String language : languages) {
					if (!hasParameter)
						variantURLS.put(currentUrl + "?zofar_lang=" + language, language);
					else
						variantURLS.put(currentUrl + "&zofar_lang=" + language, language);
				}
			}
			for (final Map.Entry<String, String> variantURL : variantURLS.entrySet()) {
				if (!driver.getCurrentUrl().equals(variantURL.getKey())) {
					driver.get(variantURL.getKey());
				}
				String filename = variantURL.getKey().substring(variantURL.getKey().lastIndexOf('/') + 1,
						variantURL.getKey().lastIndexOf('.'));
				if (filename.contains(";jsessionid=")) {
					filename = filename.substring(0, filename.indexOf(';'));
					filename = filename.replaceAll(Pattern.quote(".html"), "");
				}
				if (filename.contains("_")) {
					filename = filename.replaceAll(Pattern.quote("_"), "**");
				}
				for (final Dimension dim : windowDimensions) {
					driver.manage().window().setSize(dim);
					File screenshot = null;
					try {
						String tmp = filename + "##" + dim.getWidth() + "x" + dim.getHeight();
						if (!variantURL.getValue().equals(""))
							tmp = tmp + "##" + variantURL.getValue();
						final File renamedFile = FileClient.getInstance().createOrGetFile(tmp, ".png", cache);
						final Screenshot tmpScreenshot = new AShot()
								.shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
						final BufferedImage image = tmpScreenshot.getImage();
						ImageIO.write(image, "png", renamedFile);
						screenshot = renamedFile;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (screenshot != null) {
						File destFile = FileClient.getInstance().move(screenshot, screenshotDir);
					}
					if (!overrideCarousel) {
						List<WebElement> carousels = driver.findElements(By.cssSelector(".carousel"));
						if (carousels != null) {
							for (WebElement carousel : carousels) {
								String carouselId = carousel.getAttribute("id");
								boolean carouselMode = false;
								try {
									WebElement carouselInner = driver
											.findElement(By.cssSelector("#" + carouselId + " .carousel-inner"));
									if (carouselInner != null) {
										final String overflow = carouselInner.getCssValue("overflow");
										carouselMode = (overflow.equals("visible"));
									}
								} catch (NoSuchElementException e) {
								}
								if (!carouselMode)
									continue;
								WebElement carouselBtn = driver.findElement(
										By.cssSelector(".carousel-control-next[hRef='#" + carouselId + "']"));
								List<WebElement> carouselSlides = null;
								try {
									carouselSlides = driver
											.findElements(By.cssSelector("#" + carouselId + " .carousel-item"));
								} catch (NoSuchElementException e) {
								}
								if (carouselSlides != null) {
									WebElement activeSlide = null;
									try {
										activeSlide = driver.findElement(By.cssSelector("#" + carouselId + " .active"));
									} catch (NoSuchElementException e) {
									}
									final int slideCount = carouselSlides.size();
									int lft = 0;
									while ((activeSlide != null) && (lft < slideCount)) {
										File carouselScreenshot = null;
										try {
											String tmp = filename + "##" + dim.getWidth() + "x" + dim.getHeight();
											if (!variantURL.getValue().equals(""))
												tmp = tmp + "##" + variantURL.getValue();
											tmp = tmp + "carousel" + carouselId + "_slide" + lft;
											final File renamedFile = FileClient.getInstance().createOrGetFile(tmp,
													".png", cache);
											carouselScreenshot = renamedFile;
											final Screenshot fullPageScreenshot = new AShot()
													.shootingStrategy(ShootingStrategies.viewportPasting(100))
													.takeScreenshot(driver);
											final BufferedImage image = fullPageScreenshot.getImage();
											ImageIO.write(image, "png", carouselScreenshot);
										} catch (Exception e) {
											e.printStackTrace();
										}
										if (carouselScreenshot != null) {
											File destFile = FileClient.getInstance().move(carouselScreenshot,
													screenshotDir);
										}
										if (!carouselBtn.isDisplayed())
											break;
										carouselBtn.click();
										Thread.sleep(5 * 1000);
										lft = lft + 1;
										try {
											activeSlide = driver
													.findElement(By.cssSelector("#" + carouselId + " .active"));
										} catch (NoSuchElementException e) {
											break;
										}
									}
								}
							}
						}
					}
				}
			}
			if (currentUrl.endsWith("end.html")) {
				break;
			}
			WebElement forwardBtn = null;
			try {
				forwardBtn = driver.findElement(By.cssSelector(".zo-forward"));
			} catch (NoSuchElementException e) {
			}
			if (forwardBtn == null) {
				try {
					forwardBtn = driver.findElement(By.cssSelector(".zofar-btn-forward"));
				} catch (NoSuchElementException e) {
				}
			}
			if (forwardBtn != null) {
				if (!forwardBtn.isDisplayed()) {
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].click();", forwardBtn);
				} else
					forwardBtn.click();
			} else {
				System.err.println("No forward Button found");
				break;
			}
			if (driver.getCurrentUrl().equals(currentUrl)) {
				LOGGER.error("loop : " + currentUrl);
				break;
			}
			currentUrl = driver.getCurrentUrl();
		}
		driver.quit();
	}
	private void spiderSurveyHelperAsFilesForMDM(String url, File cache, File screenshotDir,
			final ArrayList<java.awt.Dimension> dimensions, boolean maximizeHeight, final List<String> languages)
			throws Exception {
		if (url == null)
			throw new Exception("URL is null");
		if (cache == null)
			throw new Exception("cache directory is null");
		if (!cache.exists())
			throw new Exception("cache directory does not exist");
		if (!cache.canWrite())
			throw new Exception("cannot write to cache directory");
		System.setProperty("webdriver.chrome.driver", "/home/####/Entwicklung/Browser/chromedriver");
		final WebDriver driver = new ChromeDriver();
		driver.get(url);
		String currentUrl = driver.getCurrentUrl();
		ArrayList<Dimension> windowDimensions = new ArrayList<Dimension>();
		driver.manage().window().maximize();
		int maxHeight = driver.manage().window().getSize().getHeight();
		int maxWidth = driver.manage().window().getSize().getWidth();
		if ((dimensions == null) || (dimensions.isEmpty())) {
			windowDimensions.add(new Dimension(1280, 720));
		} else {
			for (final java.awt.Dimension dim : dimensions) {
				final int width = (int) dim.getWidth();
				int height = (int) dim.getHeight();
				if (maximizeHeight)
					height = (int) Math.max(height, maxHeight);
				windowDimensions.add(new Dimension(width, height));
			}
		}
		while (currentUrl != null) {
			final Map<String, String> variantURLS = new LinkedHashMap<String, String>();
			if ((languages == null) || languages.isEmpty()) {
				variantURLS.put(currentUrl, "");
			} else {
				final boolean hasParameter = currentUrl.contains("?");
				for (final String language : languages) {
					if (!hasParameter)
						variantURLS.put(currentUrl + "?zofar_lang=" + language, language);
					else
						variantURLS.put(currentUrl + "&zofar_lang=" + language, language);
				}
			}
			for (final Map.Entry<String, String> variantURL : variantURLS.entrySet()) {
				if (!driver.getCurrentUrl().equals(variantURL.getKey())) {
					driver.get(variantURL.getKey());
				}
				String filename = variantURL.getKey().substring(variantURL.getKey().lastIndexOf('/') + 1,
						variantURL.getKey().lastIndexOf('.'));
				if (filename.contains(";jsessionid=")) {
					filename = filename.substring(0, filename.indexOf(';'));
					filename = filename.replaceAll(Pattern.quote(".html"), "");
				}
				for (final Dimension dim : windowDimensions) {
					driver.manage().window().setSize(dim);
					File screenshot = null;
					BufferedImage filteredImage = null;
					try {
						String tmp = filename + "##" + dim.getWidth() + "x" + dim.getHeight();
						if (!variantURL.getValue().equals(""))
							tmp = tmp + "##" + variantURL.getValue();
						final File renamedFile = FileClient.getInstance().createOrGetFile(tmp, ".png", cache);
						screenshot = renamedFile;
						final Screenshot fullPageScreenshot = new AShot()
								.shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
						final BufferedImage image = fullPageScreenshot.getImage();
						filteredImage = ImageConverter.getInstance().cropBackground(image, 10, -1000000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					boolean hasCarouselScreenshots = false;
					List<WebElement> carousels = driver.findElements(By.cssSelector(".carousel"));
					if (carousels != null) {
						for (WebElement carousel : carousels) {
							String carouselId = carousel.getAttribute("id");
							boolean carouselMode = false;
							try {
								WebElement carouselIndicators = driver
										.findElement(By.cssSelector("#" + carouselId + " .carousel-indicators"));
								if (carouselIndicators != null) {
									final boolean indicatorFlag = carouselIndicators.isDisplayed();
									carouselMode = indicatorFlag;
								}
							} catch (NoSuchElementException e) {
							}
							if (!carouselMode)
								continue;
							hasCarouselScreenshots = true;
							WebElement carouselBtn = driver
									.findElement(By.cssSelector(".carousel-control-next[hRef='#" + carouselId + "']"));
							List<WebElement> carouselSlides = null;
							try {
								carouselSlides = driver
										.findElements(By.cssSelector("#" + carouselId + " .carousel-item"));
							} catch (NoSuchElementException e) {
							}
							if (carouselSlides != null) {
								WebElement activeSlide = null;
								try {
									activeSlide = driver.findElement(By.cssSelector("#" + carouselId + " .active"));
								} catch (NoSuchElementException e) {
								}
								final int slideCount = carouselSlides.size();
								int lft = 0;
								while ((activeSlide != null) && (lft < slideCount)) {
									File carouselScreenshot = null;
									try {
										String tmp = filename + "_carousel" + carouselId + "_slide" + lft + "##" + dim.getWidth() + "x" + dim.getHeight();
										if (!variantURL.getValue().equals(""))
											tmp = tmp + "##" + variantURL.getValue();
										final File renamedFile = FileClient.getInstance().createOrGetFile(tmp, ".png",
												cache);
										carouselScreenshot = renamedFile;
										final Screenshot fullPageScreenshot = new AShot()
												.shootingStrategy(ShootingStrategies.viewportPasting(100))
												.takeScreenshot(driver);
										final BufferedImage image = fullPageScreenshot.getImage();
										final BufferedImage carouselFilteredImage = ImageConverter.getInstance().cropBackground(image, 10, -1000000);
										System.out.println("carouselScreenshot "+carouselScreenshot.getName());
										ImageIO.write(carouselFilteredImage, "png", carouselScreenshot);
									} catch (Exception e) {
										e.printStackTrace();
									}
									if (carouselScreenshot != null) {
										File destFile = FileClient.getInstance().move(carouselScreenshot,
												screenshotDir);
									}
									if (!carouselBtn.isDisplayed())
										break;
									carouselBtn.click();
									Thread.sleep(2 * 1000);
									lft = lft + 1;
									try {
										activeSlide = driver.findElement(By.cssSelector("#" + carouselId + " .active"));
									} catch (NoSuchElementException e) {
										break;
									}
								}
							}
						}
					}
					if(!hasCarouselScreenshots) {
						if (screenshot != null) {
							if(filteredImage != null) {
								System.out.println("screenshot "+screenshot.getName());
								ImageIO.write(filteredImage, "png", screenshot);
							}
							File destFile = FileClient.getInstance().move(screenshot, screenshotDir);
						}
					}
				}
			}
			if (currentUrl.endsWith("end.html")) {
				break;
			}
			WebElement forwardBtn = null;
			try {
				forwardBtn = driver.findElement(By.cssSelector(".zo-forward"));
			} catch (NoSuchElementException e) {
			}
			if (forwardBtn == null) {
				try {
					forwardBtn = driver.findElement(By.cssSelector(".zofar-btn-forward"));
				} catch (NoSuchElementException e) {
				}
			}
			if (forwardBtn != null)
				forwardBtn.click();
			else {
				System.err.println("No forward Button found");
				break;
			}
			if (driver.getCurrentUrl().equals(currentUrl)) {
				LOGGER.error("loop : " + currentUrl);
				break;
			}
			currentUrl = driver.getCurrentUrl();
		}
		driver.quit();
	}
}
