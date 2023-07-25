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
package de.his.zofar.presentation.surveyengine.controller;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
/**
 * @author meisner
 * Controller to realize Link-based (Side- and Inline-) Navigation 
 *
 */
@Controller("jumperBean")
@Scope("request")
public class JumperBean implements Serializable,ActionListener{
	private static final long serialVersionUID = -9030015079457000536L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JumperBean.class);
	@Inject
	private NavigatorBean navigator;
	private String target;
	@PostConstruct
	private void init() {
	}
	public String getTarget() {
		String back = navigator.getSameViewID();
		if(target != null){
			navigator.jumperMove(target);
			back = target;
		}
		return back;
	}
	public void setTarget(final String target) {
		this.target = target;
	}
	public boolean active(){
		return (target != null);
	}
	public boolean sideNavigationEnabled(){
		return true;
	}
	public boolean topNavigationEnabled(){
		return true;
	}
	@Override
	public void processAction(ActionEvent event)
			throws AbortProcessingException {
		target = (String)event.getComponent().getAttributes().get("targetPage");
	}
}
