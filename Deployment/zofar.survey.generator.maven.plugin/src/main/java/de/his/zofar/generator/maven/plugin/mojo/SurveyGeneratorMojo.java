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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import de.his.zofar.generator.maven.plugin.generator.page.PageManager;
import de.his.zofar.generator.maven.plugin.reader.QuestionnaireReader;
import de.his.zofar.xml.questionnaire.AbstractMatrixType;
import de.his.zofar.xml.questionnaire.AbstractQuestionType;
import de.his.zofar.xml.questionnaire.CalendarType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.PreloadType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.SectionBodyType;
import de.his.zofar.xml.questionnaire.TransitionType;
import de.his.zofar.xml.questionnaire.TransitionsType;
import de.his.zofar.xml.questionnaire.VariableType;
import eu.dzhw.zofar.management.dev.qml.QMLClient;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
/**
 * @author le
 * 
 */
@Mojo(name = "generate-survey")
public class SurveyGeneratorMojo extends AbstractMojo {
	private final QuestionnaireReader reader = QuestionnaireReader.getInstance();
	private QuestionnaireDocument questionnaire;
	@Parameter(defaultValue = "${project}", required = true)
	private MavenProject project;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire.xml", required = true)
	private File xml;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire1.xml", required = false)
	private File xml1;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire2.xml", required = false)
	private File xml2;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire3.xml", required = false)
	private File xml3;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire4.xml", required = false)
	private File xml4;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire5.xml", required = false)
	private File xml5;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire6.xml", required = false)
	private File xml6;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire7.xml", required = false)
	private File xml7;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire8.xml", required = false)
	private File xml8;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire9.xml", required = false)
	private File xml9;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire10.xml", required = false)
	private File xml10;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire11.xml", required = false)
	private File xml11;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire12.xml", required = false)
	private File xml12;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire13.xml", required = false)
	private File xml13;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire14.xml", required = false)
	private File xml14;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire15.xml", required = false)
	private File xml15;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire16.xml", required = false)
	private File xml16;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire17.xml", required = false)
	private File xml17;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire18.xml", required = false)
	private File xml18;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire19.xml", required = false)
	private File xml19;
	@Parameter(defaultValue = "${basedir}/src/main/resources/questionnaire20.xml", required = false)
	private File xml20;
	@Parameter(defaultValue = "${basedir}/src/main/resources/survey/preload.csv", required = true)
	private File preloads;
	@Parameter(defaultValue = "${basedir}/src/main/resources/survey", required = true)
	private File surveyResource;
	@Parameter(defaultValue = "${basedir}/src/main/webapp", required = true)
	private File webapp;
	@Parameter(defaultValue = "${basedir}/src/main/resources/de/his/zofar/messages", required = true)
	private File resourceBundle;
	@Parameter(defaultValue = "${basedir}/src/main/webapp/WEB-INF", required = true)
	private File webinf;
	@Parameter(defaultValue = "${basedir}/src/main/resources", required = true)
	private File resources;
	@Parameter(defaultValue = "false", required = true)
	private boolean pretest;
	@Parameter(defaultValue = "false", required = true)
	private boolean linearNavigation;
	@Parameter(defaultValue = "false", required = true)
	private boolean overrideRendering;
	@Parameter(defaultValue = "true", required = true)
	private boolean noVisibleMap;
	@Parameter(defaultValue = "false", required = true)
	private boolean mdm;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		readQuestionnaireDocument();
		if (mdm) {
			linearNavigation = true;
			overrideRendering = true;
			questionnaire = restructure(questionnaire);
		}
		final PagesGeneratorMojo generator = new PagesGeneratorMojo(questionnaire, getLog(), webapp, resourceBundle,
				surveyResource, pretest, overrideRendering, noVisibleMap);
		PageManager.getInstance().setMojo(generator);
		generatePreloads();
		generatePages();
		generateVariables();
		generateTransitions();
		generateCSS();
	}
	private XmlObject getParentQuestion(final XmlObject child) {
		if (child == null)
			return null;
		final XmlClient client = XmlClient.getInstance();
		final List<XmlObject> questionPath = new ArrayList<XmlObject>();
		questionPath.add(child);
		XmlObject parent = client.getParent(child);
		while (parent != null) {
			if (!client.hasParent(parent.getDomNode(), "zofar:page"))
				break;
			questionPath.add(parent);
			parent = client.getParent(parent);
		}
		Collections.reverse(questionPath);
		for (final XmlObject item : questionPath) {
			if ((AbstractQuestionType.class).isAssignableFrom(item.getClass()))
				return item;
			if ((AbstractMatrixType.class).isAssignableFrom(item.getClass()))
				return item;
			if ((CalendarType.class).isAssignableFrom(item.getClass()))
				return item;
		}
		return child;
	}
	private List<XmlObject> getQuestionsFromObj(XmlObject obj) {
		if (obj == null)
			return null;
		final XmlClient client = XmlClient.getInstance();
		XmlObject[] resultSet = client.getByXPath(obj, "descendant::*/@variable");
		if ((resultSet != null) && (resultSet.length > 0)) {
			List<XmlObject> back = new ArrayList<XmlObject>();
			for (final XmlObject item : resultSet) {
				XmlObject parent = getParentQuestion(item);
				if (parent != null) {
					if (parent.getDomNode().getNodeName().equals("zofar:question"))
						parent = getParentQuestion(parent);
					if ((!back.contains(parent)))
						back.add(parent);
				}
			}
			return back;
		}
		return null;
	}
	private QuestionnaireDocument restructure(QuestionnaireDocument questionnaireOrig) {
		final XmlOptions opts = new XmlOptions();
		opts.setCharacterEncoding("utf8");
		opts.setSavePrettyPrint();
		opts.setSavePrettyPrintIndent(4);
		QuestionnaireDocument back = (QuestionnaireDocument) questionnaireOrig.copy();
		final PageType[] pages = questionnaireOrig.getQuestionnaire().getPageArray();
		final List<PageType> newPages = new ArrayList<PageType>();
		PageType[] dummyPage = new PageType[0];
		TransitionType[] dummyTrans = new TransitionType[0];
		back.getQuestionnaire().setPageArray(dummyPage);
		if (pages != null) {
			for (PageType page : pages) {
				final String origUID = page.getUid();
				if (origUID.equals("index")) {
					page.getTransitions().setTransitionArray(dummyTrans);
					TransitionType transition = page.getTransitions().addNewTransition();
					transition.setTarget("end");
					newPages.add(page);
				}
				if (origUID.equals("end")) {
					newPages.add(page);
					continue;
				}
				if (!origUID.equals("index"))
					newPages.add(page);
				List<XmlObject> questions = getQuestionsFromObj(page);
				if (questions != null) {
					for (final XmlObject question : questions) {
						if (!(IdentificationalType.class).isAssignableFrom(question.getClass()))
							continue;
						final PageType newPage = back.getQuestionnaire().addNewPage();
						String pagename = QMLClient.getInstance().getIdPath((IdentificationalType) question);
						newPage.setUid(pagename);
						newPage.setTransitions(page.getTransitions());
						TransitionsType transitions = newPage.addNewTransitions();
						TransitionType transition = transitions.addNewTransition();
						transition.setTarget("end");
						final String questionType = question.getDomNode().getNodeName();
						System.out.println(origUID + " " + ((IdentificationalType) question).getUid()
								+ " Question Type : " + questionType);
						final SectionBodyType body = newPage.addNewBody();
						body.setUid("body");
						IdentificationalType newQuestion = null;
						if (questionType.equals("zofar:multipleChoice")) {
							newQuestion = body.addNewMultipleChoice();
						} else if (questionType.equals("zofar:questionSingleChoice")) {
							newQuestion = body.addNewQuestionSingleChoice();
						} else if (questionType.equals("zofar:questionOpen")) {
							newQuestion = body.addNewQuestionOpen();
						} else if (questionType.equals("zofar:matrixQuestionSingleChoice")) {
							newQuestion = body.addNewMatrixQuestionSingleChoice();
						} else if (questionType.equals("zofar:matrixQuestionOpen")) {
							newQuestion = body.addNewMatrixQuestionOpen();
						} else if (questionType.equals("zofar:matrixDouble")) {
							newQuestion = body.addNewMatrixDouble();
						} else if (questionType.equals("zofar:matrixQuestionMixed")) {
							newQuestion = body.addNewMatrixQuestionMixed();
						} else if (questionType.equals("zofar:matrixMultipleChoice")) {
							newQuestion = body.addNewMatrixMultipleChoice();
						} else if (questionType.equals("zofar:comparison")) {
							newQuestion = body.addNewComparison();
						} else if (questionType.equals("zofar:calendar")) {
							newQuestion = body.addNewCalendar();
						} else {
						}
						newQuestion.setUid("question");
						((XmlObject) newQuestion).set((XmlObject) question);
						newPages.add(newPage);
					}
				}
			}
			back.getQuestionnaire().setPageArray(newPages.toArray(dummyPage));
		}
		return back;
	}
	private QuestionnaireDocument restructureOrig(QuestionnaireDocument questionnaireOrig) {
		final XmlOptions opts = new XmlOptions();
		opts.setCharacterEncoding("utf8");
		opts.setSavePrettyPrint();
		opts.setSavePrettyPrintIndent(4);
		QuestionnaireDocument back = (QuestionnaireDocument) questionnaireOrig.copy();
		final PageType[] pages = questionnaireOrig.getQuestionnaire().getPageArray();
		final List<PageType> newPages = new ArrayList<PageType>();
		PageType[] dummyPage = new PageType[0];
		TransitionType[] dummyTrans = new TransitionType[0];
		back.getQuestionnaire().setPageArray(dummyPage);
		if (pages != null) {
			for (PageType page : pages) {
				final String origUID = page.getUid();
				if (origUID.equals("index")) {
					page.getTransitions().setTransitionArray(dummyTrans);
					TransitionType transition = page.getTransitions().addNewTransition();
					transition.setTarget("end");
					newPages.add(page);
				}
				if (origUID.equals("end")) {
					newPages.add(page);
					continue;
				}
				List<XmlObject> questions = getQuestionsFromObj(page);
				if (questions != null) {
					final Map<String, Integer> indexMap = new HashMap<String, Integer>();
					for (final XmlObject question : questions) {
						if (!(IdentificationalType.class).isAssignableFrom(question.getClass()))
							continue;
						final PageType newPage = back.getQuestionnaire().addNewPage();
						String pagename = QMLClient.getInstance().getIdPath((IdentificationalType) question);
						newPage.setUid(pagename);
						newPage.setTransitions(page.getTransitions());
						TransitionsType transitions = newPage.addNewTransitions();
						TransitionType transition = transitions.addNewTransition();
						transition.setTarget("end");
						final String questionType = question.getDomNode().getNodeName();
						System.out.println(origUID + " " + ((IdentificationalType) question).getUid()
								+ " Question Type : " + questionType);
						final SectionBodyType body = newPage.addNewBody();
						body.setUid("body");
						IdentificationalType newQuestion = null;
						if (questionType.equals("zofar:multipleChoice")) {
							newQuestion = body.addNewMultipleChoice();
						} else if (questionType.equals("zofar:questionSingleChoice")) {
							newQuestion = body.addNewQuestionSingleChoice();
						} else if (questionType.equals("zofar:questionOpen")) {
							newQuestion = body.addNewQuestionOpen();
						} else if (questionType.equals("zofar:matrixQuestionSingleChoice")) {
							newQuestion = body.addNewMatrixQuestionSingleChoice();
						} else if (questionType.equals("zofar:matrixQuestionOpen")) {
							newQuestion = body.addNewMatrixQuestionOpen();
						} else if (questionType.equals("zofar:matrixDouble")) {
							newQuestion = body.addNewMatrixDouble();
						} else if (questionType.equals("zofar:matrixQuestionMixed")) {
							newQuestion = body.addNewMatrixQuestionMixed();
						} else if (questionType.equals("zofar:matrixMultipleChoice")) {
							newQuestion = body.addNewMatrixMultipleChoice();
						} else if (questionType.equals("zofar:comparison")) {
							newQuestion = body.addNewComparison();
						} else if (questionType.equals("zofar:calendar")) {
							newQuestion = body.addNewCalendar();
						} else {
						}
						newQuestion.setUid("question");
						((XmlObject) newQuestion).set((XmlObject) question);
						newPages.add(newPage);
					}
				}
			}
			back.getQuestionnaire().setPageArray(newPages.toArray(dummyPage));
		}
		return back;
	}
	private void generateVariables() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating variables");
		final VariablesGeneratorMojo generator = new VariablesGeneratorMojo(questionnaire, surveyResource);
		generator.execute();
	}
	private void generatePages() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating zofar pages AKA composite components.");
		final PagesGeneratorMojo generator = PageManager.getInstance().getMojo();
		generator.execute();
	}
	private void generatePreloads() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating Preloads.");
		final PreloadGeneratorMojo generator = new PreloadGeneratorMojo(questionnaire, preloads, surveyResource,
				getLog());
		generator.execute();
	}
	private void generateCSS() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating general CSS.");
		final CssGeneratorMojo generator = new CssGeneratorMojo(webapp);
		generator.execute();
	}
	/**
	 *
	 */
	private void generateTransitions() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating zofar transitions and the faces-config.xml.");
		final TransitionsGeneratorMojo generator = new TransitionsGeneratorMojo(questionnaire, webinf,
				linearNavigation);
		generator.execute();
	}
	/**
	 * @return
	 */
	private void readQuestionnaireDocument() {
		try {
			questionnaire = reader.readDocument(xml, true);
			if ((xml1 != null) && (xml1.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml1, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml2 != null) && (xml2.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml2, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml3 != null) && (xml3.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml3, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml4 != null) && (xml4.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml4, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml5 != null) && (xml5.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml5, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml6 != null) && (xml6.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml6, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml7 != null) && (xml7.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml7, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml8 != null) && (xml8.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml8, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml9 != null) && (xml9.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml9, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml10 != null) && (xml10.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml10, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml11 != null) && (xml11.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml11, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml12 != null) && (xml12.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml12, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml13 != null) && (xml13.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml13, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml14 != null) && (xml14.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml14, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml15 != null) && (xml15.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml15, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml16 != null) && (xml16.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml16, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml17 != null) && (xml17.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml17, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml18 != null) && (xml18.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml18, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml19 != null) && (xml19.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml19, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
			if ((xml20 != null) && (xml20.exists())) {
				QuestionnaireDocument tmp = reader.readDocument(xml20, true);
				if (tmp != null) {
					questionnaire = appendQML(questionnaire, tmp);
				}
			}
		} catch (final IllegalArgumentException iae) {
			getLog().warn("No questionnaire XML file! Creating basic survey.");
		}
	}
	private QuestionnaireDocument appendQML(final QuestionnaireDocument base, final QuestionnaireDocument toAppend) {
		if ((base == null) && (toAppend == null))
			return null;
		if (toAppend == null)
			return base;
		if (base == null)
			return toAppend;
		QuestionnaireDocument back = base;
		PreloadType[] appendPreloads = null;
		if (toAppend.getQuestionnaire().getPreloads() != null)
			appendPreloads = toAppend.getQuestionnaire().getPreloads().getPreloadArray();
		if(appendPreloads != null) {
			if (back.getQuestionnaire().getPreloads() != null)back.getQuestionnaire().addNewPreloads();
			for(final PreloadType preload : appendPreloads) {
				PreloadType newPreload = back.getQuestionnaire().getPreloads().addNewPreload();
				newPreload.setName(preload.getName());
				newPreload.setPassword(preload.getPassword());
				newPreload.setPreloadItemArray(preload.getPreloadItemArray());
			}
		}
		VariableType[] appendVariables = null;
		if (toAppend.getQuestionnaire().getVariables() != null)
			appendVariables = toAppend.getQuestionnaire().getVariables().getVariableArray();
		if(appendVariables != null) {
			if (back.getQuestionnaire().getVariables() != null)back.getQuestionnaire().addNewVariables();
			for(final VariableType variable : appendVariables) {
				VariableType newVariable = back.getQuestionnaire().getVariables().addNewVariable();
				newVariable.setName(variable.getName());
				newVariable.setAlternativeText(variable.getAlternativeText());
				newVariable.setType(variable.getType());
			}
		}
		final PageType[] appendPages = toAppend.getQuestionnaire().getPageArray();
		if(appendPages != null) {
			for(final PageType page : appendPages) {
				final PageType newPage = back.getQuestionnaire().addNewPage();
				newPage.setUid(page.getUid());
				newPage.setAdditionalLanguages(page.getAdditionalLanguages());
				newPage.setHeader(page.getHeader());
				newPage.setBody(page.getBody());
				newPage.setBreadcrumb(page.getBreadcrumb());
				newPage.setCustomLayout(page.getCustomLayout());
				newPage.setResearchdata(page.getResearchdata());
				newPage.setSide(page.getSide());
				newPage.setTriggers(page.getTriggers());
				newPage.setTransitions(page.getTransitions());
			}
		}
		return back;
	}
}
