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
/**
 *
 */
package de.his.zofar.generator.maven.plugin.mojo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.xmlbeans.XmlException;
import au.com.bytecode.opencsv.CSVWriter;
import de.his.zofar.generator.maven.plugin.generator.page.ZofarWebPage;
import de.his.zofar.translation.Exporter;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
/**
 * Generates all xhtml Pages.
 * 
 * Mojo will be executed when calling
 * "mvn de.his.zofar:zofar.generator.maven.plugin:generate-pages"
 * 
 * @author le
 * 
 */
public class PagesGeneratorMojo {
	private static final String MSG_BUNDLE_FILE_PATTERN = "%s_%s.properties";
	private static final String MSG_BUNDLE_KEY_PATTERN = "msgs['%s']";
	private final QuestionnaireDocument questionnaire;
	private final Log log;
	private final File outputDirectory;
	private final File surveyOutputDirectory;
	private final File messageBundleOutputDirectory;
	private final List<String[]> labels = new ArrayList<String[]>();
	private File msgBundleFile;
	private final Properties msgBundle = new Properties();
	private Exporter translationExporter=null;
	private final boolean pretest;
	private final boolean  overrideRendering;
	private final boolean noVisibleMap;
	/**
	 * @param questionnaire
	 * @param log
	 * @param outputDirectory
	 * @param messageBundleOutputDirectory
	 */
	public PagesGeneratorMojo(final QuestionnaireDocument questionnaire, final Log log, final File outputDirectory, final File messageBundleOutputDirectory, final File surveyOutputDirectory,final boolean pretest,final boolean overrideRendering,final boolean noVisibleMap) {
		super();
		this.log = log;
		this.questionnaire = questionnaire;
		this.outputDirectory = outputDirectory;
		this.surveyOutputDirectory = surveyOutputDirectory;
		this.messageBundleOutputDirectory = messageBundleOutputDirectory;
		this.pretest=pretest;
		this.overrideRendering = overrideRendering;
		this.noVisibleMap = noVisibleMap;
	}
	/**
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	public final void execute() throws MojoExecutionException, MojoFailureException {
		final String language = this.questionnaire.getQuestionnaire().getLanguage().toString().toLowerCase();
		this.createMessageBundle(language);
		this.createMessageTranslation(language);
		final PageType[] pages = this.questionnaire.getQuestionnaire().getPageArray();
		final String projectPath = this.outputDirectory.getAbsolutePath();
		if (pages != null) {
			int currentPage = 0;
			for (final PageType page : pages) {
				final String pageName = page.getUid();
				final ZofarWebPage webPage = new ZofarWebPage(pageName, this.calculateProgress(currentPage++, pages.length),overrideRendering,noVisibleMap);
				this.savePage(projectPath, page, pageName, webPage);
			}
		}
		try {
			this.saveMessageBundle();
			this.saveMessageTranslation();
			this.saveLabelsCsv();
		} catch (final IOException e) {
			throw new MojoFailureException("could not save resource property file");
		}
	}
	/**
	 * @param projectPath
	 * @param page
	 * @param pageName
	 * @param webPage
	 * @throws MojoFailureException
	 */
	private void savePage(final String projectPath, final PageType page, final String pageName, final ZofarWebPage webPage) throws MojoFailureException {
		try {
			if (page.getCustomLayout() && webPage.isPageExist(projectPath)) {
				this.log.info("skipping page " + pageName + ", because it has a custom layout and already exists");
			} else {
				webPage.addPageNavigation(page, log);
				webPage.addPageContentRecursively(page);
				if(pretest) webPage.addPretestComments(page, log);
				webPage.save(projectPath);
			}
		} catch (final NullPointerException | IOException | XmlException e) {
			e.printStackTrace();
			throw new MojoFailureException("could not create / save XHTML page: " + pageName);
		}
	}
	private void createMessageBundle(final String language) {
		this.msgBundleFile = new File(this.messageBundleOutputDirectory.getAbsolutePath() + File.separator + String.format(MSG_BUNDLE_FILE_PATTERN, "text", language));
	}
	private void createMessageTranslation(final String language) {
		final File translationFile = new File(this.messageBundleOutputDirectory.getAbsolutePath() + File.separator + "translation.csv");
		translationExporter = new Exporter(translationFile,language);
	}
	/**
	 * @param currentPage
	 * @param pageCount
	 * @return
	 */
	private Integer calculateProgress(final Integer currentPage, final Integer pageCount) {
		final Integer percent = 100;
		final Integer divisor = (pageCount <= 1) ? 1 : pageCount - 1;
		final Integer progress = (currentPage * percent) / divisor;
		return progress;
	}
	/**
	 * @throws IOException
	 */
	private void saveMessageBundle() throws IOException {
		if (!this.msgBundleFile.exists()) {
			if (!this.msgBundleFile.getParentFile().exists()) {
				final Boolean isMkdirSuccessful = this.msgBundleFile.getParentFile().mkdirs();
				if (!isMkdirSuccessful) {
					throw new IOException("could not create directories in " + this.msgBundleFile.getParentFile().getAbsolutePath());
				}
			}
		} else {
			final Boolean isDeleteSuccessful = this.msgBundleFile.delete();
			if (!isDeleteSuccessful) {
				throw new IOException("could not delete file: " + this.msgBundleFile.getAbsolutePath());
			}
		}
		OutputStream out = null;
		try {
			out = new FileOutputStream(this.msgBundleFile);
			this.msgBundle.store(out, "message bundle for");
			out.close();
		} catch (final IOException ioe) {
			if (out != null) {
				out.close();
				throw ioe;
			}
		}
	}
	/**
	 * @throws IOException
	 */
	private void saveMessageTranslation() throws IOException {
		translationExporter.save();
	}
	private static final String CSV_LINE_PATTERN = "%s#%s#%s#%s";
	public void writeLabelInformation(final String answerOptionUid, final String variable, final String visibleCondition, final String messageBundleKey) {
		final String vCondition = visibleCondition != null && !visibleCondition.isEmpty() ? visibleCondition : "true";
		final String line = String.format(CSV_LINE_PATTERN, answerOptionUid, variable, vCondition, messageBundleKey);
		this.labels.add(line.split("#"));
	}
	/**
     *
     */
	private void saveLabelsCsv() throws IOException {
		final File csv = new File(this.surveyOutputDirectory, "labels.csv");
		final CSVWriter writer = new CSVWriter(new FileWriter(csv));
		for (final String[] line : this.labels) {
			writer.writeNext(line);
		}
		writer.close();
	}
	/**
	 * adds the text to the message bundle of the page and returns the key.
	 * 
	 * @param uid
	 * @param text
	 *            the text to be added
	 * @return the message bundle key of the text
	 */
	public final String addTextToBundle(final String uid, final String text) {
		if (text == null) {
			return null;
		}
		String cleanedText = text.replaceAll("\t", " ");
		cleanedText = cleanedText.replaceAll("\n", " ");
		cleanedText = cleanedText.replaceAll(" {2,}", " ");
		final String bundleKey = uid.replace(":", ".");
		final String key = String.format(MSG_BUNDLE_KEY_PATTERN, bundleKey);
		this.msgBundle.put(bundleKey, cleanedText);
		translationExporter.put(bundleKey,cleanedText);
		return key;
	}
	public boolean isOverrideRendering() {
		return overrideRendering;
	}
}
