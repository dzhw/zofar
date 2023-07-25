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
package de.his.zofar.service.valuetype.internal;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import de.his.zofar.persistence.valuetype.daos.ValueTypeDao;
import de.his.zofar.service.common.exceptions.NotYetImplementedException;
import de.his.zofar.service.common.internal.InternalServiceInterface;
import de.his.zofar.service.common.model.PageDTO;
import de.his.zofar.service.valuetype.model.ValueType;
/**
 * @author le
 *
 */
@Service
public class ValueTypeInternalService implements InternalServiceInterface {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ValueTypeInternalService.class);
    /**
     * the dao instance to work on the database.
     */
    @Resource
    private ValueTypeDao valueTypeDao;
    /**
     * search for all value types from persistence layer.
     *
     * @param query
     *            the query
     * @return a page with all value types
     */
    /**
     * loads a value type by its identifier. returns null if the identifier
     * doesn't exist.
     *
     * @param identifier
     *            the value type id
     * @return the value type with the identifier
     */
    public ValueType loadByIdentifier(final String identifier) {
        throw new NotYetImplementedException();
    }
    /**
     * saves the value type and return the saved value type. do not use the
     * argument for further usage. use the return value instead.
     *
     * @param valueType
     *            the value type to save
     * @return the saved value type
     */
    public ValueType save(final ValueType valueType) {
        throw new NotYetImplementedException();
    }
}
