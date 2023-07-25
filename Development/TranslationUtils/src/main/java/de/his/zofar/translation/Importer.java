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
package de.his.zofar.translation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import au.com.bytecode.opencsv.CSVReader;
public class Importer {
	private static final String MSG_BUNDLE_FILE_PATTERN = "%s_%s.properties";
	private final File sourceFile;
	public Importer(final File sourceFile) {
		this.sourceFile = sourceFile;
	}
	public void exportTo(final File targetDir) throws IOException{
		if(this.sourceFile == null)return;
		if(!this.sourceFile.exists())return;
		if(!this.sourceFile.canRead())return;
		if(targetDir == null)return;
		if(!targetDir.exists())return;
		if(!targetDir.canWrite())return;
		final Map<String,Properties> result = new HashMap<String,Properties>();
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(sourceFile));
			List<String[]> content = reader.readAll();
			if((content != null)&&(!content.isEmpty())){
				final String[] header = content.get(0);
				final int count = header.length;
				content = content.subList(1, content.size()-1);
				for(final String[] row : content){
					final String key = row[0];
					for(int a=1;a<count;a++){
						final String language = header[a];
						final String value = row[a];
						Properties languageMap = null;
						if(result.containsKey(language))languageMap = result.get(language);
						if(languageMap == null)languageMap = new Properties();
						languageMap.put(key, value);						
						result.put(language, languageMap);
					}
				}
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		finally{
			if(reader != null)reader.close();
		}
		for(final Map.Entry<String, Properties> languageSet: result.entrySet()){
			final String language = languageSet.getKey();
			final Properties props = languageSet.getValue();
			OutputStream out = null;
			try {
				final File messageBundleFile = new File(targetDir,String.format(MSG_BUNDLE_FILE_PATTERN, "text", language));
				if (!messageBundleFile.exists()) {
					if (!messageBundleFile.getParentFile().exists()) {
						final Boolean isMkdirSuccessful = messageBundleFile.getParentFile().mkdirs();
						if (!isMkdirSuccessful) {
							throw new IOException("could not create directories in " + messageBundleFile.getParentFile().getAbsolutePath());
						}
					}
				} else {
					final Boolean isDeleteSuccessful = messageBundleFile.delete();
					if (!isDeleteSuccessful) {
						throw new IOException("could not delete file: " + messageBundleFile.getAbsolutePath());
					}
				}
				out = new FileOutputStream(messageBundleFile);
				props.store(out, "message bundle for");
				out.close();
			} catch (final IOException ioe) {
				if (out != null) {
					out.close();
					throw ioe;
				}
			}
		}
	}
	public Map<String,Properties> loadAsMap() throws IOException{
		if(this.sourceFile == null)return null;
		if(!this.sourceFile.exists())return null;
		if(!this.sourceFile.canRead())return null;
		final Map<String,Properties> result = new HashMap<String,Properties>();
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(sourceFile));
			List<String[]> content = reader.readAll();
			if((content != null)&&(!content.isEmpty())){
				final String[] header = content.get(0);
				final int count = header.length;
				content = content.subList(1, content.size()-1);
				for(final String[] row : content){
					final String key = row[0];
					for(int a=1;a<count;a++){
						final String language = header[a];
						final String value = row[a];
						Properties languageMap = null;
						if(result.containsKey(language))languageMap = result.get(language);
						if(languageMap == null)languageMap = new Properties();
						languageMap.put(key, value);						
						result.put(language, languageMap);
					}
				}
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		finally{
			if(reader != null)reader.close();
		}
		return result;
	}
}
