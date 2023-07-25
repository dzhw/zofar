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
package de.his.zofar.persistence.common.daos;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

import de.his.zofar.persistence.common.entities.BaseEntity;

public interface QueryDslJpaRepository<T extends BaseEntity, ID extends Serializable> {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.querydsl.QueryDslPredicateExecutor#count(com
     * .mysema.query.types.Predicate)
     */
    public long count(Predicate predicate);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.querydsl.QueryDslPredicateExecutor#findAll(com
     * .mysema.query.types.Predicate)
     */
    public List<T> findAll(Predicate predicate);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.querydsl.QueryDslPredicateExecutor#findAll(com
     * .mysema.query.types.Predicate,
     * com.mysema.query.types.OrderSpecifier<?>[])
     */
    public List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.querydsl.QueryDslPredicateExecutor#findAll(com
     * .mysema.query.types.Predicate, org.springframework.data.domain.Pageable)
     */
    public Page<T> findAll(Predicate predicate, Pageable pageable);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.querydsl.QueryDslPredicateExecutor#findOne(com
     * .mysema.query.types.Predicate)
     */
    public T findOne(Predicate predicate);

}
