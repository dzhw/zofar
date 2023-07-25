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
package eu.dzhw.zofar.testing.condition;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xmlbeans.XmlObject;
import org.springframework.expression.spel.SpelEvaluationException;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.PreloadItemType;
import de.his.zofar.xml.questionnaire.PreloadType;
import de.his.zofar.xml.questionnaire.PreloadsType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.TransitionType;
import de.his.zofar.xml.questionnaire.TransitionsType;
import de.his.zofar.xml.questionnaire.TriggerType;
import de.his.zofar.xml.questionnaire.TriggerVariableType;
import de.his.zofar.xml.questionnaire.TriggersType;
import de.his.zofar.xml.questionnaire.VariableType;
import de.his.zofar.xml.questionnaire.VariablesType;
import eu.dzhw.zofar.management.dev.qml.QMLClient;
import eu.dzhw.zofar.management.utils.bool.DecisionClient;
import eu.dzhw.zofar.management.utils.files.CSVClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
import model.ParticipantEntity;
import model.SurveyData;
import support.elements.conditionEvaluation.NavigatorBean;
import support.elements.conditionEvaluation.SessionControllerBean;
import support.elements.conditionEvaluation.VariableBean;
import support.elements.conditionEvaluation.ZofarBean;
public class SpelDebugerClient implements Serializable  {
	private static final long serialVersionUID = 7520667564325966941L;
	private final static SpelDebugerClient INSTANCE = new SpelDebugerClient();
	private SpelDebugerClient() {
		super();
	}
	public static SpelDebugerClient getInstance() {
		return INSTANCE;
	}
	private void log(final File logFile, final File qml,final String type,final String pageId,final String condition, final Exception e) throws Exception {
		System.out.println("File :"+qml.getName());
		System.out.println("Error : ("+e.getClass()+") "+e.getMessage());
		if(((SpelEvaluationException.class).isAssignableFrom(e.getClass()))) {
			SpelEvaluationException spelE = (SpelEvaluationException)e;
			System.out.println("Error Pos: "+spelE.getPosition());
			System.out.println("Error Code: "+condition.substring(spelE.getPosition()));
		}
		ArrayList<String> header = new ArrayList<String>();
		header.add("File");
		header.add("Type");
		header.add("Page");
		header.add("condition");
		header.add("Error Type");
		header.add("Error Message");
		List<Map<String, String>> data = CSVClient.getInstance().loadCSV(logFile, header, true);
		final Map<String,String> entry = new HashMap<String,String>();
		entry.put("File", qml.getName());
		entry.put("Type", type);
		entry.put("Page", pageId);
		entry.put("condition", condition);
		entry.put("Error Type", e.getClass().getName());
		entry.put("Error Message", e.getMessage());
		data.add(entry);
		CSVClient.getInstance().saveCSV(logFile, header, data);
	}
	public void executeSpelDebug(final File base) throws Exception {
		final File baseDir = new File(base.toString()+"/src/main/resources");
		final File logFile = FileClient.getInstance().createOrGetFile("SPELDebug", ".csv",baseDir);
		FileClient.getInstance().writeToFile(logFile, "", false);
		final List<File> toCheck = new ArrayList<File>();
		toCheck.add(new File(baseDir,"questionnaire.xml"));
		toCheck.add(new File(baseDir,"questionnaire1.xml"));
		toCheck.add(new File(baseDir,"questionnaire2.xml"));
		toCheck.add(new File(baseDir,"questionnaire3.xml"));
		toCheck.add(new File(baseDir,"questionnaire4.xml"));
		toCheck.add(new File(baseDir,"questionnaire5.xml"));
		toCheck.add(new File(baseDir,"questionnaire6.xml"));
		toCheck.add(new File(baseDir,"questionnaire7.xml"));
		toCheck.add(new File(baseDir,"questionnaire8.xml"));
		toCheck.add(new File(baseDir,"questionnaire9.xml"));
		toCheck.add(new File(baseDir,"questionnaire10.xml"));
		toCheck.add(new File(baseDir,"questionnaire11.xml"));
		toCheck.add(new File(baseDir,"questionnaire12.xml"));
		toCheck.add(new File(baseDir,"questionnaire13.xml"));
		toCheck.add(new File(baseDir,"questionnaire14.xml"));
		toCheck.add(new File(baseDir,"questionnaire15.xml"));
		toCheck.add(new File(baseDir,"questionnaire16.xml"));
		toCheck.add(new File(baseDir,"questionnaire17.xml"));
		toCheck.add(new File(baseDir,"questionnaire18.xml"));
		toCheck.add(new File(baseDir,"questionnaire19.xml"));
		toCheck.add(new File(baseDir,"questionnaire20.xml"));	
		for(final File qml: toCheck) {
			if(!qml.exists())continue;
			System.out.println("File : "+qml.getName());
			final QuestionnaireDocument doc = QMLClient.getInstance().getDocument(qml);
			final Map<String, String> declaredVariables = new HashMap<String, String>();
			final VariablesType variablesBlock = doc.getQuestionnaire().getVariables();
			if(variablesBlock != null) {
				final VariableType[] variables = variablesBlock.getVariableArray();
				for (final VariableType variable : variables) {
					declaredVariables.put(variable.getName(), variable.getType().toString());
				}
			}
			final PreloadsType preloads = doc.getQuestionnaire().getPreloads();
			if(preloads != null) {
				final PreloadType[] preloadArray = preloads.getPreloadArray();
				for (final PreloadType preload : preloadArray) {
					PreloadItemType[] preloadItems = preload.getPreloadItemArray();
					if (preloadItems != null) {
						for (final PreloadItemType preloadItem : preloadItems) {
							declaredVariables.put("PRELOAD" + preloadItem.getVariable(), "string");
						}
					}
				}
			}
			final Map<String, String> missings = new HashMap<String, String>();
			final Map<String, Map<String, String>> values = new HashMap<String, Map<String, String>>();
			final Map<String, SurveyData> surveyData = new HashMap<String, SurveyData>();
			final List<String> pageList = new ArrayList<String>();
			final List<String> pagesFromHistory = new ArrayList<String>();
			final Map<String, List<String>> pageTree = new HashMap<String, List<String>>();
			final ParticipantEntity participant = new ParticipantEntity();
			participant.setToken("dummy");
			participant.setSurveyData(surveyData);
			final Map<String, Object> contextData = new HashMap<String, Object>();
			contextData.put("zofar", new ZofarBean(missings, values));
			contextData.put("sessionController", new SessionControllerBean(participant));
			contextData.put("navigatorBean",new NavigatorBean(pageList, pagesFromHistory, pageTree));
		    for(final Map.Entry<String,String> tmp : declaredVariables.entrySet()) {
		    	final String variableName = tmp.getKey();
		    	final String type = tmp.getValue();
		    	if (type != null) {
					try {
						if (type.equals("singleChoiceAnswerOption")) {
							contextData.put(variableName,new VariableBean(variableName, 0, "ao0"));
							contextData.put(variableName+"dropDown",new VariableBean(variableName+"dropDown", 0, "ao0"));
							contextData.put(variableName+"missing",new VariableBean(variableName+"missing", 0, "ao0"));
						} else if (type.equals("boolean")) {
							contextData.put(variableName,new VariableBean(variableName, false, false));
						} else if (type.equals("string")) {
							contextData.put(variableName,new VariableBean(variableName, "0", "0"));
						} 
						else if (type.equals("text")) {
							contextData.put(variableName,new VariableBean(variableName, "", ""));
						} 
						else if (type.equals("number")) {
							contextData.put(variableName,new VariableBean(variableName, 0, 0));
						} 
						else if (type.equals("grade")) {
							contextData.put(variableName,new VariableBean(variableName, "0", "0"));
						} 
						else if (type.equals("preload")) {
							contextData.put(variableName,new VariableBean(variableName, "", ""));
						} else {
							contextData.put(variableName,new VariableBean(variableName, "false", "false"));
						}
					} catch (final Exception e) {
						throw new Exception("Error while Context Build for Visibility-Check ("
								+ participant.getToken() + " , " + variableName + ") : " + e
								+ " ");
					}
				}
		    }
			final PageType[] pages = doc.getQuestionnaire().getPageArray();
			for (final PageType page : pages) {
				final String pageId = page.getUid();
				XmlObject[] visibles = XmlClient.getInstance().getByXPath(page.getBody(), "//*[@visible]");
				if (visibles != null) {
					for (final XmlObject visible : visibles) {
						final String condition = XmlClient.getInstance().getAttribute(visible, "visible");
						if(condition == null)continue;
						try {
							final boolean flag = DecisionClient.getInstance().evaluateSpel("" + condition + "",contextData,participant.getToken());
						}
						catch(final ClassCastException e) {
						}
						catch(final Exception e) {
						};
					}
				}
				final TriggersType triggers = page.getTriggers();
				if (triggers != null) {
					final TriggerType[] vtriggers = triggers.getVariableArray();
					if (vtriggers != null) {
						for (final TriggerType vtrigger : vtriggers) {
							final String condition = vtrigger.getCondition();
							if(condition == null)continue;
							try {
								final boolean flag = DecisionClient.getInstance().evaluateSpel("" + condition + "",contextData,participant.getToken());
							}
							catch(final ClassCastException e) {
							}
							catch(final Exception e) {
							};
						}
						for (final TriggerType vtrigger : vtriggers) {
							final String value = ((TriggerVariableType)vtrigger).getValue();
							if(value == null)continue;
							try {
								final boolean flag = DecisionClient.getInstance().evaluateSpel("" + value + "",contextData,participant.getToken());
							}
							catch(final ClassCastException e) {
							}
							catch(final Exception e) {
							};
						}
					}
				}
				final TransitionsType transitions = page.getTransitions();
				if (transitions != null) {
					final TransitionType[] transition = transitions.getTransitionArray();
					if (transition != null) {
						for (final TransitionType trans : transition) {
							final String condition = trans.getCondition();
							if(condition == null)continue;
							try {
								final boolean flag = DecisionClient.getInstance().evaluateSpel("" + condition + "",contextData,participant.getToken());
							}
							catch(final ClassCastException e) {
							}
							catch(final Exception e) {
								log(logFile,qml,"transition",pageId,condition,e);
							};
						}
					}
				}
			}			
		}
		System.out.println(logFile.getAbsolutePath());
	}
}
