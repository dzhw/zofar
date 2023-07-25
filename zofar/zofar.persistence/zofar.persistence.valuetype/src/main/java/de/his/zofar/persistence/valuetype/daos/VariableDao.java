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
package de.his.zofar.persistence.valuetype.daos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import de.his.zofar.persistence.common.daos.GenericCRUDDao;
import de.his.zofar.persistence.valuetype.entities.QVariableEntity;
import de.his.zofar.persistence.valuetype.entities.VariableEntity;

@Repository
public interface VariableDao<T extends VariableEntity> extends GenericCRUDDao<T> {
    public static QVariableEntity qVariable = QVariableEntity.variableEntity;

    public Page<VariableEntity> findByName(String name, Pageable pageable);
}
