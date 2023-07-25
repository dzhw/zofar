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
package de.his.zofar.service.valuetype.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.his.zofar.service.valuetype.model.PanelVariable;
import de.his.zofar.service.valuetype.model.Question;
import de.his.zofar.service.valuetype.model.SurveyVariable;
import de.his.zofar.service.valuetype.model.ValueType;
import de.his.zofar.service.valuetype.model.Variable;
import de.his.zofar.service.valuetype.service.VariableService;
import de.his.zofar.service.valuetype.util.Generator;

/**
 * @author meisner
 *
 */
@Service("variableService")
public class VariableServiceMock implements
		VariableService {
	
    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(VariableServiceMock.class);
    
    private Map<String, Variable> variables;
	
    /**
     * set up the fake db.
     */
    @PostConstruct
    private void init() {
        LOGGER.warn("You are using a MOCK!");
        variables = new HashMap<String, Variable>();
        final PanelVariable panel1 = createPanelVariable(Generator.getInstance().createUUID(),"panel1","label",null);
        variables.put(panel1.getUuid(), panel1);
        
        final SurveyVariable survey1 = createSurveyVariable(Generator.getInstance().createUUID(),"survey1","label",null,null);
        variables.put(survey1.getUuid(), survey1);
    }

	@Override
	public PanelVariable createPanelVariable(final String uuid, final String name,final String label,final ValueType valueType) {
		final PanelVariable var = PanelVariable.Factory.newInstance();
		var.setUuid(uuid);
		var.setName(name);
		var.setLabel(label);
		var.setValueType(valueType);
		return var;
	}

	@Override
	public SurveyVariable createSurveyVariable(final String uuid, final String name,final String label,final ValueType valueType,
			final Question question) {
		final SurveyVariable var = SurveyVariable.Factory.newInstance();
		var.setUuid(uuid);
		var.setName(name);
		var.setLabel(label);
		var.setValueType(valueType);
		var.setQuestion(question);
		return var;
	}

	@Override
	public Variable loadVariable(final String uuid) {
		return variables.get(uuid);
	}

	@Override
	public Set<Variable> loadByType(final Class<? extends Variable> type) {
		final Set<Variable> back = new HashSet<Variable>();
		final Iterator<String> it = variables.keySet().iterator();
		while(it.hasNext()){
			final String identificator = it.next();
			final Variable tmptype = variables.get(identificator);
			if(type.isAssignableFrom(tmptype.getClass()))back.add(tmptype);
		}
		return back;
	}

	@Override
	public void removeVariable(final Variable variable) {
		variables.remove(variable.getUuid());
	}
	
	@Override
	public Variable save(final Variable variable) {
		variables.put(variable.getUuid(),variable);
		return variable;
	}

}
