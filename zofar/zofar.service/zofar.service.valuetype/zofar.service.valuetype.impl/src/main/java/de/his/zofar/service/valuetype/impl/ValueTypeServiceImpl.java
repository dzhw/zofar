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
package de.his.zofar.service.valuetype.impl;
import java.util.Set;
import javax.inject.Inject;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import de.his.zofar.service.common.AbstractService;
import de.his.zofar.service.valuetype.internal.ValueTypeInternalService;
import de.his.zofar.service.valuetype.model.ValueType;
import de.his.zofar.service.valuetype.service.ValueTypeService;
/**
 * @author le
 *
 */
@Service("valueTypeService")
public class ValueTypeServiceImpl extends AbstractService implements
        ValueTypeService {
    @Inject
    private ValueTypeInternalService valueTypeInternalService;
    @Inject
    public ValueTypeServiceImpl(final Mapper mapper) {
        super(mapper);
    }
	@Override
	public ValueType createNumberValueType(final String identifier,final String description,final String measurementLevel,int decimalPlaces, long minimum,
			long maximum) {
		return null;
	}
	@Override
	public ValueType createStringValueType(final String identifier,final String description,final String measurementLevel,int length, boolean canbeempty) {
		return null;
	}
	@Override
	public ValueType createBooleanValueType(final String identifier,final String description,final String measurementLevel) {
		return null;
	}
	@Override
	public ValueType loadByIdentifier(String identifier) {
		return null;
	}
	@Override
	public Set<ValueType> loadByType(Class<? extends ValueType> type) {
		return null;
	}
	@Override
	public void removeValueType(ValueType valueType) {
	}
	@Override
	public ValueType save(ValueType valueType) {
		return null;
	}
}
