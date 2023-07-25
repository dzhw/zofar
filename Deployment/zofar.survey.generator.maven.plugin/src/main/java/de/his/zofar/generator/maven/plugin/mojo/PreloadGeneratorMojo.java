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
package de.his.zofar.generator.maven.plugin.mojo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import au.com.bytecode.opencsv.CSVReader;
import de.his.zofar.generator.maven.plugin.generator.page.PageManager;
import de.his.zofar.xml.questionnaire.PreloadItemType;
import de.his.zofar.xml.questionnaire.PreloadType;
import de.his.zofar.xml.questionnaire.PreloadsType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.VariableType;
public class PreloadGeneratorMojo {
	private final Log log;
	private final QuestionnaireDocument questionnaire;
	private final File preloads;
	private PageManager pageManager = PageManager.getInstance();
	private final static String PRELOAD_PREFIX = "PRELOAD";
	private final static String PRELOAD_ITEM_QUOTE = "";
	private final static char PRELOAD_ITEM_SEPARATOR = '#';
	private final static String PRELOAD_LINE_SEPARATOR = "\n";
	private static final String SQL_POSTGRES_FILE = "preloads_postgres.sql";
	private final File outputDirectory;
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
			result = prime * result
					+ ((content == null) ? 0 : content.hashCode());
			result = prime * result
					+ ((header == null) ? 0 : header.hashCode());
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
	public PreloadGeneratorMojo(final QuestionnaireDocument questionnaire,
			final File preloads, final File outputDirectory, final Log log) {
		super();
		this.log = log;
		this.preloads = preloads;
		this.questionnaire = questionnaire;
		this.outputDirectory = outputDirectory;
	}
	/**
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		updateCsv(preloads);
		if (preloads != null && preloads.exists()) {
			final CsvPackage existingCsv = this.readCSV(preloads,
					PRELOAD_ITEM_SEPARATOR, "name");
			if (existingCsv != null) {
				final StringBuffer sql = new StringBuffer();
				final ShaPasswordEncoder encoder = new ShaPasswordEncoder();
				final List<String> header = existingCsv.getHeader();
				header.remove("name");
				header.remove("password");
				final Map<String, Map<String, String>> content = existingCsv
						.getContent();
				final Iterator<String> it = content.keySet().iterator();
				while (it.hasNext()) {
					final String token = it.next();
					final Map<String, String> data = content.get(token);
					String password = null;
					if(data.containsKey("password")&&data.get("name").equals(token))password = data.get("password");
					if(password != null){
						sql.append("INSERT INTO participant(id, version, password, token) VALUES (nextval('seq_participant_id'),0,'"+ encoder.encodePassword(password,null) + "','" + token + "');\n");
					}
					final Iterator<String> variableIt = header.iterator();
					while(variableIt.hasNext()){
						final String variable = variableIt.next();
						final String value = data.get(variable);
						 if(value != null)sql.append("INSERT INTO surveydata(id, version, value, variablename, participant_id) VALUES (nextval('seq_surveydata_id'), 0, '"+ value+ "', '"+ PRELOAD_PREFIX+variable + "', (select DISTINCT id from participant where token = '" + token + "'));\n");
					}
				}
				writeToPostgresFile(sql);
				if (!header.isEmpty()) {
					final Iterator<String> varIt = header.iterator();
					while (varIt.hasNext()) {
						final String variablename = varIt.next();
						final VariableType preloadVariable = VariableType.Factory
								.newInstance();
						preloadVariable.setName(PRELOAD_PREFIX + variablename);
						preloadVariable.setType(VariableType.Type.STRING);
						pageManager.getAdditionalVariables().add(
								preloadVariable);
					}
				}
			}
		} else {
			log.error("preload file do not exist");
		}
	}
	private CsvPackage readCSV(final File cvsFile, final char separator,
			final String keyFieldName) {
		if (cvsFile == null)
			return null;
		if (!cvsFile.exists())
			try {
				cvsFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		CsvPackage back = new CsvPackage();
		InputStream in = null;
		try {
			in = new FileInputStream(cvsFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (in != null) {
			final CSVReader reader = new CSVReader(new InputStreamReader(in),
					separator);
			String[] nextLine;
			try {
				boolean firstLine = true;
				while ((nextLine = reader.readNext()) != null) {
					if (firstLine) {
						back.getHeader().addAll(Arrays.asList(nextLine));
						firstLine = false;
					} else {
						final Iterator<String> variableIt = back.getHeader()
								.iterator();
						final Map<String, String> data = new LinkedHashMap<String, String>();
						String keyField = null;
						while (variableIt.hasNext()) {
							final String variablename = variableIt.next();
							final int index = back.getHeader().indexOf(
									variablename);
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
	private void writeCSV(final File cvsFile, final CsvPackage csvContent,
			final char separator) throws IOException {
		if (cvsFile == null)
			return;
		if (!cvsFile.exists())
			cvsFile.createNewFile();
		final StringBuffer csv = new StringBuffer();
		Iterator<String> variableIt = csvContent.getHeader().iterator();
		while (variableIt.hasNext()) {
			final String variablename = variableIt.next();
			csv.append(PRELOAD_ITEM_QUOTE + variablename + PRELOAD_ITEM_QUOTE);
			if (variableIt.hasNext())
				csv.append(separator);
			else
				csv.append(PRELOAD_LINE_SEPARATOR);
		}
		final Iterator<String> preloadIt = csvContent.getContent().keySet()
				.iterator();
		while (preloadIt.hasNext()) {
			final String preloadKey = preloadIt.next();
			final Map<String, String> preloadMap = csvContent.getContent().get(
					preloadKey);
			variableIt = csvContent.getHeader().iterator();
			while (variableIt.hasNext()) {
				final String variablename = variableIt.next();
				String value = "";
				if (preloadMap.containsKey(variablename))
					value = PRELOAD_ITEM_QUOTE + preloadMap.get(variablename)
							+ PRELOAD_ITEM_QUOTE;
				csv.append(value);
				if (variableIt.hasNext())
					csv.append(separator);
			}
			if (preloadIt.hasNext())
				csv.append(PRELOAD_LINE_SEPARATOR);
		}
		writeToCsvFile(cvsFile, csv.toString());
	}
	/**
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	private final void updateCsv(File cvsFile) {
		if (questionnaire != null
				&& questionnaire.getQuestionnaire().isSetPreloads()) {
			final PreloadsType preloadsBlock = questionnaire.getQuestionnaire()
					.getPreloads();
			final PreloadType[] preloads = preloadsBlock.getPreloadArray();
			if ((preloads != null) && (preloads.length > 0)) {
				final CsvPackage existingCsv = this.readCSV(cvsFile,
						PRELOAD_ITEM_SEPARATOR, "name");
				if (existingCsv != null) {
					final int preloadCount = preloads.length;
					for (int a = 0; a < preloadCount; a++) {
						final PreloadType preload = preloadsBlock
								.getPreloadArray(a);
						final String name = preload.getName();
						final String password = preload.getPassword();
						boolean dirty = false;
						Map<String, String> data = null;
						if (existingCsv.getContent().containsKey(name))
							data = existingCsv.getContent().get(name);
						if (data == null) {
							dirty = true;
							data = new LinkedHashMap<String, String>();
						}
						if (!existingCsv.getHeader().contains("name"))
							existingCsv.getHeader().add("name");
						if (!existingCsv.getHeader().contains("password"))
							existingCsv.getHeader().add("password");
						if (!data.containsKey("name")) {
							data.put("name", name);
							dirty = true;
						}
						if (!data.containsKey("password")) {
							data.put("password", password);
							dirty = true;
						}
						final PreloadItemType[] items = preload
								.getPreloadItemArray();
						if ((items != null) && (items.length > 0)) {
							final int itemCount = items.length;
							for (int b = 0; b < itemCount; b++) {
								final PreloadItemType item = preload
										.getPreloadItemArray(b);
								final String variable = item.getVariable();
								final String value = item.getValue();
								if (!existingCsv.getHeader().contains(variable))
									existingCsv.getHeader().add(variable);
								if (!data.containsKey(variable)) {
									data.put(variable, value);
									dirty = true;
								} else {
									log.info("Field " + variable
											+ " already exists for " + name);
								}
							}
						}
						if (dirty)
							existingCsv.getContent().put(name, data);
					}
					try {
						this.writeCSV(cvsFile, existingCsv,
								PRELOAD_ITEM_SEPARATOR);
					} catch (IOException e) {
						log.error(e);
					}
				}
			}
		}
	}
	private void writeToCsvFile(final File cvsFile, final String content) {
		if (!outputDirectory.exists())
			outputDirectory.mkdir();
		File target = cvsFile;
		try {
			if (!target.exists())
				target.createNewFile();
			FileUtils.writeStringToFile(target, content, false);
		} catch (IOException e) {
			log.error("failed to create preload file", e);
		}
	}
	private void writeToPostgresFile(final StringBuffer content) {
		if (!outputDirectory.exists())
			outputDirectory.mkdir();
		File target = new File(outputDirectory.getAbsolutePath() + "/"
				+ SQL_POSTGRES_FILE);
		try {
			if (!target.exists())
				target.createNewFile();
			FileUtils.writeStringToFile(target, content.toString(), false);
		} catch (IOException e) {
			log.error("failed to create preload file", e);
		}
	}
}
