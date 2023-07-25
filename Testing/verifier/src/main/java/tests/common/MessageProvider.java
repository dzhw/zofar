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
package tests.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProvider {
	
	private static final String WARN_PREFIX = "* ";
	private static final String INFO_PREFIX = "# ";
	private static final String ERROR_PREFIX = "! ";
	
	public static void warn(final Object source, final String message){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.warn(WARN_PREFIX+message);
	}
	
	public static void warn(final Object source, final String message,final Object arg1){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.warn(WARN_PREFIX+message,arg1);
	}
	
	public static void warn(final Object source, final String message,final Object arg1,final Object arg2){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.warn(WARN_PREFIX+message,arg1,arg2);
	}

	public static void info(final Object source, final String message){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.info(INFO_PREFIX+message);
	}
	
	public static void info(final Object source, final String message,final Object arg1){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.info(INFO_PREFIX+message,arg1);
	}
	
	public static void info(final Object source, final String message,final Object arg1,final Object arg2){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.info(INFO_PREFIX+message,arg1,arg2);
	}
	
	public static void error(final Object source, final String message){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.error(ERROR_PREFIX+message);
	}
	
	public static void error(final Object source, final String message,final Object arg1){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.error(ERROR_PREFIX+message,arg1);
	}
	
	public static void error(final Object source, final String message,final Object arg1,final Object arg2){
		final Logger LOGGER = LoggerFactory.getLogger(retrieveClass(source));
		LOGGER.error(ERROR_PREFIX+message,arg1,arg2);
	}
	
	private static Class retrieveClass(final Object obj){
		if(obj == null)return null;
		if(obj instanceof Class)return (Class)obj;
		return obj.getClass();
	}

}
