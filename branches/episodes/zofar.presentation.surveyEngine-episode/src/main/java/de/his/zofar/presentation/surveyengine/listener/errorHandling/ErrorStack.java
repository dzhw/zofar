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
package de.his.zofar.presentation.surveyengine.listener.errorHandling;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Bean to hold Exception-Information till Error-Page is shown,
 * without to transfer Exception in Query-Response 
 * 
 * @author meisner
 * 
 */
@Controller("errorStack")
@Scope("session")
public class ErrorStack implements Serializable {

	private static final long serialVersionUID = -211674186050004671L;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ErrorStack.class);
	
	private Throwable exception;

	@PostConstruct
	private void init() {
		LOGGER.info("init");
	}

	/**
	 * @return the Throwable to be shown
	 */
	public Throwable getException() {
		return this.exception;
	}

	/**
	 * @param exception that shall be shown on error-Page
	 */
	public void setException(final Throwable exception) {
		this.exception = exception;
	}

	/**
	 * @return the message, which is returned by the setted Throwable. if no Throwable is set, an empty String, 
	 */
	public String getMessage(){
        if(this.getException() != null){
        	final String message = this.getException().getMessage();
        	return message;
        }
		return "";
	}
	
	/**
	 * clear setted Throwable
	 */
	public void reset(){
		LOGGER.info("reset");
		this.exception = null;
	}

}
