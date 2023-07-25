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
package de.his.zofar.persistence.common.entities;
import org.springframework.data.domain.Sort;
/**
 * @author meisner
 * 
 */
public class PageRequestEntity {
    private int pageNumber;
    private int pageSize;
    public PageRequestEntity() {
        super();
    }
    public int getOffset() {
        return this.pageNumber * this.pageSize;
    }
    public int getPageNumber() {
        return this.pageNumber;
    }
    public int getPageSize() {
        return this.pageSize;
    }
    public Sort getSort() {
        throw new RuntimeException("Not yet implemented!");
    }
    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }
    public void setSort(final Sort sort) {
        throw new RuntimeException("Not yet implemented!");
    }
}
