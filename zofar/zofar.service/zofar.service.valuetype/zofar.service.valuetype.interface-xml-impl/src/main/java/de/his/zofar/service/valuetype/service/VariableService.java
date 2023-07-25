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
package de.his.zofar.service.valuetype.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.his.zofar.service.valuetype.model.PanelVariable;
import de.his.zofar.service.valuetype.model.Question;
import de.his.zofar.service.valuetype.model.SurveyVariable;
import de.his.zofar.service.valuetype.model.ValueType;
import de.his.zofar.service.valuetype.model.Variable;

/**
 * @author meisner
 * 
 */
@Service
public interface VariableService {

	@Transactional
	public abstract PanelVariable createPanelVariable(final String uuid,final String name,final String label,final ValueType valueType);
	
	@Transactional
	public abstract SurveyVariable createSurveyVariable(final String uuid,final String name,final String label, final ValueType valueType, final Question question);

	@Transactional
	public abstract Variable loadVariable(final String uuid);
	
	@Transactional
	public Set<Variable> loadByType(Class<? extends Variable> type);
	
	@Transactional
	public abstract void removeVariable(final Variable variable);
	@Transactional
	public abstract Variable save(final Variable variable);


}
