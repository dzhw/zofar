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
package eu.dzhw.zofar.management.generator.qml.answerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.dzhw.zofar.management.utils.files.CSVClient;

public class SingleChoiceGenerator {

	private static SingleChoiceGenerator INSTANCE;

	private SingleChoiceGenerator() {
		super();
	}

	public static SingleChoiceGenerator getInstance() {
		if (INSTANCE == null)
			INSTANCE = new SingleChoiceGenerator();
		return INSTANCE;
	}
	
	public List<String> generate(final File csv){
		return generate(csv,1);
	}
	
	public List<String> generate(final File csv,final int startIndex){
		if(csv == null)return null;
		if(!csv.canRead())return null;
		try {
			List<String> headers = new ArrayList<String>();
			headers.add("Value");
			headers.add("Label");
			
			List<Map<String,String>> list = CSVClient.getInstance().loadCSV(csv,headers,false);
			if(list == null)return null;
			if(list.isEmpty())return null;
			
			int lft = startIndex;
			final List<String> back = new ArrayList<String>();
			for(Map<String,String> entry : list){
				back.add("<zofar:answerOption uid=\"ao"+lft+"\" value=\""+entry.get("Value")+"\" label=\""+entry.get("Label")+"\"></zofar:answerOption>");
				lft++;
			}
			return back;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
