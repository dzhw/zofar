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
package eu.dzhw.zofar.management.dev.file.spider.components;

import java.io.File;

public abstract class FileHandler {
	
	public abstract void call(final File file,final String startTag,final String stopTag);
	
	protected String getSuffix(final File file){
		if(file == null)return null;
		if(!file.isFile())return null;
		int suffixIndex = (file.getName()).lastIndexOf('.');
		if(suffixIndex != -1){
			return file.getName().substring(suffixIndex+1);
		}
		return null;
	}

}
