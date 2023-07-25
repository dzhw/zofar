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
package eu.dzhw.zofar.management.dev.file.spider.components.impl;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.dev.file.spider.components.FileHandler;
import eu.dzhw.zofar.management.utils.string.ReplaceClient;
public class AddTextHandler extends FileHandler {
	final static Logger LOGGER = LoggerFactory.getLogger(AddTextHandler.class);
	private final String text;
	private final String suffix;
	public AddTextHandler(final String text,final String suffix) {
		super();
		this.text = text;
		this.suffix = suffix;
	}
	public void call(final File file,final String startTag,final String stopTag) {
		if(file == null)return;
		if(file.isDirectory())return;
		if(!file.canRead())return;
		if(!file.canWrite())return;
		if(!this.getSuffix(file).equals(this.suffix))return;
		try {
			String oldContent = FileUtils.readFileToString(file, "UTF-8");
			if(oldContent != null){
				if((startTag != null)&&(!startTag.equals(""))&&(stopTag != null)&&(!stopTag.equals(""))){
					oldContent = ReplaceClient.getInstance().replaceBetweenTags(oldContent, startTag, stopTag,"");
					oldContent = ReplaceClient.getInstance().stripLeadingBreaks(oldContent);
				}	
				final String newContent = startTag+"\n"+text+"\n"+stopTag+"\n"+oldContent;
				LOGGER.info("write header to {}",file.getAbsolutePath());
				FileUtils.writeStringToFile(file, newContent , "UTF-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
