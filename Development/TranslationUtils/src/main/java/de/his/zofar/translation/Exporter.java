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
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import au.com.bytecode.opencsv.CSVWriter;
public class Exporter {
	private final File targetFile;
	private final Map<String, String> translation;
	private final String initLanguage;
	public Exporter(final File targetFile,final String initLanguage) {
		System.out.println("Translation Exporter : "+targetFile+" ("+initLanguage+")");
		this.targetFile = targetFile;
		if(initLanguage == null)this.initLanguage = "de";
		else this.initLanguage = initLanguage;
		translation = new LinkedHashMap<String, String>();
	}
	public void put(final String key,final String value){
		translation.put(key, value);
	}
	public File save() throws IOException{
		if(this.targetFile == null)return null;
		if (!this.targetFile.exists()) {
			if (!this.targetFile.getParentFile().exists()) {
				final Boolean isMkdirSuccessful = this.targetFile.getParentFile().mkdirs();
				if (!isMkdirSuccessful) {
					throw new IOException("could not create directories in " + this.targetFile.getParentFile().getAbsolutePath());
				}
			}
		} else {
			final Boolean isDeleteSuccessful = this.targetFile.delete();
			if (!isDeleteSuccessful) {
				throw new IOException("could not delete file: " + this.targetFile.getAbsolutePath());
			}
		}
		final CSVWriter writer = new CSVWriter(new FileWriter(this.targetFile));
		final String[] headLine = new String[2];
		headLine[0] = "key";
		headLine[1] = this.initLanguage;
		writer.writeNext(headLine);
		for (final Map.Entry<String, String> item : this.translation.entrySet()) {
			String cleanedValue = item.getValue();
			cleanedValue = cleanedValue.replaceAll(Pattern.quote(";"), "\\;");
			cleanedValue = cleanedValue.replaceAll(Pattern.quote("\""), "\\\"");
			final String[] line = new String[2];
			line[0] = item.getKey();
			line[1] = cleanedValue;
			writer.writeNext(line);
		}
		writer.close();
		return this.targetFile;
	}
}
