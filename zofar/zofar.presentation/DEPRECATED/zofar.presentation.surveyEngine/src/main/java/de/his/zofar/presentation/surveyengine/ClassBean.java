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
package de.his.zofar.presentation.surveyengine;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.component.UINamingContainer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * @author meisner
 *
 */
@Controller
@Scope("request")
public class ClassBean implements Serializable {


	private static final long serialVersionUID = 5113768690081569958L;


	@PostConstruct
	private void init() {
	}

	public String getType(final Object obj){
		if(obj == null)return null;
		final Map<String,Object> map = ((UINamingContainer)obj).getAttributes();
		String back = map.get("org.apache.myfaces.compositecomponent.location")+"";
		back = back.split(" ")[0];
		back = back.replaceAll("^/", "");
		back = back.replaceAll(".xhtml", "");
		return back;
	}

	public String getTemplate(final Object obj){
		if(obj == null)return null;
		final Map<String,Object> map = ((UINamingContainer)obj).getAttributes();
		String back = map.get("org.apache.myfaces.compositecomponent.location")+"";
		back = back.split(" ")[0];
		return back;
	}

}
