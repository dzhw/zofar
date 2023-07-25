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
package de.his.zofar.presentation.surveyengine.listener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import au.com.bytecode.opencsv.CSVReader;
import de.his.zofar.persistence.surveyengine.daos.AnswerOptionDao;
import de.his.zofar.persistence.surveyengine.entities.AnswerOptionEntity;
import de.his.zofar.presentation.surveyengine.security.concurrentSession.ConcurrentSessionManager;
import de.his.zofar.presentation.surveyengine.util.SystemConfiguration;
import de.his.zofar.service.surveyengine.model.Participant;
import de.his.zofar.service.surveyengine.model.SurveyData;
import de.his.zofar.service.surveyengine.service.SurveyEngineService;
/**
 * @author le
 * 
 */
public class InitializeDatabaseContextListener implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializeDatabaseContextListener.class);
	private final static String PRELOAD_PREFIX = "PRELOAD";
	private class CsvPackage {
		private List<String> header;
		private Map<String, Map<String, String>> content;
		public CsvPackage() {
			super();
			this.header = new ArrayList<String>();
			this.content = new HashMap<String, Map<String, String>>();
		}
		public List<String> getHeader() {
			return header;
		}
		public Map<String, Map<String, String>> getContent() {
			return content;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((content == null) ? 0 : content.hashCode());
			result = prime * result + ((header == null) ? 0 : header.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CsvPackage other = (CsvPackage) obj;
			if (content == null) {
				if (other.content != null)
					return false;
			} else if (!content.equals(other.content))
				return false;
			if (header == null) {
				if (other.header != null)
					return false;
			} else if (!header.equals(other.header))
				return false;
			return true;
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		LOGGER.info("initialize database listener");
		final ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sce.getServletContext());
		SystemConfiguration systemConfiguration = SystemConfiguration.getInstance();
		if (systemConfiguration.preloadOnStart())
			this.createPreloads(context);
		this.createAnswerOptionLabels(context);
		ConcurrentSessionManager manager = (ConcurrentSessionManager) context.getBean("concurrentSessionManager");
	}
	private CsvPackage readCSV(final InputStream in, final char separator, final String keyFieldName) {
		if (in == null)
			return null;
		;
		CsvPackage back = new CsvPackage();
		if (in != null) {
			final CSVReader reader = new CSVReader(new InputStreamReader(in), separator);
			String[] nextLine;
			try {
				boolean firstLine = true;
				while ((nextLine = reader.readNext()) != null) {
					if (firstLine) {
						back.getHeader().addAll(Arrays.asList(nextLine));
						firstLine = false;
					} else {
						final Iterator<String> variableIt = back.getHeader().iterator();
						final Map<String, String> data = new LinkedHashMap<String, String>();
						String keyField = null;
						while (variableIt.hasNext()) {
							final String variablename = variableIt.next();
							final int index = back.getHeader().indexOf(variablename);
							final String value = nextLine[index];
							if (variablename.equals(keyFieldName))
								keyField = value;
							if (!value.equals(""))
								data.put(variablename, value);
						}
						if (keyField != null)
							back.getContent().put(keyField, data);
					}
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return back;
	}
	private void createPreloads(final ApplicationContext context) {
		final SurveyEngineService service = context.getBean("surveyEngineService", SurveyEngineService.class);
		final InputStream in = InitializeDatabaseContextListener.class.getResourceAsStream("/survey/preload.csv");
		if (in != null) {
			final CsvPackage existingCsv = this.readCSV(in, '#', "name");
			if (existingCsv != null) {
				final List<String> header = existingCsv.getHeader();
				header.remove("name");
				header.remove("password");
				final Map<String, Map<String, String>> content = existingCsv.getContent();
				final Iterator<String> it = content.keySet().iterator();
				while (it.hasNext()) {
					final String token = it.next();
					final Map<String, String> data = content.get(token);
					boolean dirty = false;
					Participant participant = service.loadParticipant(token);
					if (participant == null) {
						String password = null;
						if (data.containsKey("password") && data.get("name").equals(token))
							password = data.get("password");
						if (password != null) {
							dirty = true;
							participant = service.createParticipant(token, password);
						}
						LOGGER.info("create Participant {} ({})", token, password);
					}
					if (participant != null) {
						final Iterator<String> variableIt = header.iterator();
						while (variableIt.hasNext()) {
							final String variable = variableIt.next();
							final String value = data.get(variable);
							final String preloadVariable = PRELOAD_PREFIX + variable;
							if (value != null) {
								if (!participant.getSurveyData().containsKey(preloadVariable)) {
									dirty = true;
									final SurveyData preload = new SurveyData();
									preload.setParticipant(participant);
									preload.setVariableName(preloadVariable);
									preload.setValue(value);
									participant.addSurveyData(preload);
								} else {
									LOGGER.info("Preload Information {} already exists for Participant {}", variable,
											token);
								}
							}
						}
					}
					if (dirty)
						service.saveParticipant(participant);
				}
			}
		} else {
			LOGGER.error("preload file do not exist");
		}
	}
	/**
	 * @param context
	 */
	/**
	 * @param context
	 */
	private void createAnswerOptionLabels(final ApplicationContext context) {
		final AnswerOptionDao answerOptionDao = context.getBean("answerOptionDao", AnswerOptionDao.class);
		final InputStream in = InitializeDatabaseContextListener.class.getResourceAsStream("/survey/labels.csv");
		if (in != null) {
			final CSVReader reader = new CSVReader(new InputStreamReader(in));
			String[] nextLine;
			try {
				final Map<String, AnswerOptionEntity> entities = new HashMap<String, AnswerOptionEntity>();
				while ((nextLine = reader.readNext()) != null) {
					final String uid = nextLine[0];
					final String variable = nextLine[1];
					final String condition = nextLine[2];
					final String bundlekey = nextLine[3];
					final String key = uid + "#" + variable;
					if (!entities.containsKey(key)) {
						AnswerOptionEntity entity = answerOptionDao.findByVariableNameAndAnswerOptionUid(variable, uid);
						if(entity == null) entity = new AnswerOptionEntity(uid, variable);
						else{
							entity.getConditionedResourceKeys().clear();
							LOGGER.debug("Already there : {} , {}",variable, uid);
						}
						entities.put(key, entity);
					}
					if (entities.containsKey(key))entities.get(key).addConditionAndKey(condition, bundlekey);
				}
				if (!entities.isEmpty()) {
					LOGGER.debug("creating visible conditions and resource keys of answer options.");
					answerOptionDao.save(entities.values());
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
	}
}
