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
package eu.dzhw.zofar.management.dev.file.system.impl;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dzhw.zofar.management.dev.file.system.FileHandler;
import eu.dzhw.zofar.management.utils.files.DirectoryClient;
import eu.dzhw.zofar.management.utils.files.FileClient;
@Deprecated
public class Temp implements FileHandler{
	final static Logger LOGGER = LoggerFactory.getLogger(Temp.class);
	public Temp() {
		super();
	}
}
