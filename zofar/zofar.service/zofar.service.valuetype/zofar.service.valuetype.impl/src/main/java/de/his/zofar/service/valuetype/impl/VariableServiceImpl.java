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
import java.util.Set;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import de.his.zofar.service.common.AbstractService;
import de.his.zofar.service.valuetype.model.PanelVariable;
import de.his.zofar.service.valuetype.model.Question;
import de.his.zofar.service.valuetype.model.SurveyVariable;
import de.his.zofar.service.valuetype.model.ValueType;
import de.his.zofar.service.valuetype.model.Variable;
import de.his.zofar.service.valuetype.service.VariableService;
/**
 * @author meisner
 *
 */
@Service("variableService")
public class VariableServiceImpl extends AbstractService implements
		VariableService {
	public VariableServiceImpl(final Mapper mapper) {
		super(mapper);
	}
	@Override
	public PanelVariable createPanelVariable(final String uuid, final String name,final String label,final ValueType valueType) {
		return null;
	}
	@Override
	public SurveyVariable createSurveyVariable(final String uuid,final String name,final String label,final ValueType valueType,
			final Question question) {
		return null;
	}
	@Override
	public Variable loadVariable(final String uuid) {
		return null;
	}
	@Override
	public Set<Variable> loadByType(final Class<? extends Variable> type) {
		return null;
	}
	@Override
	public void removeVariable(final Variable variable) {
	}
	@Override
	public Variable save(final Variable variable) {
		return variable;
	}
}
