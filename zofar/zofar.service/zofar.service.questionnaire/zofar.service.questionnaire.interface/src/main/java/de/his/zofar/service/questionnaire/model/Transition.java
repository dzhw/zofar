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
package de.his.zofar.service.questionnaire.model;

import java.io.Serializable;
import java.util.UUID;

import de.his.zofar.service.common.model.BaseDTO;


/**
 * @author le
 * 
 */
public class Transition extends BaseDTO implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5548605077167306773L;
    private String condition;
    private UUID nextPageUuid;

    /**
     * 
     */
    public Transition() {
        super();
    }

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition
     *            the condition to set
     */
    public void setCondition(final String conditions) {
        this.condition = conditions;
    }

    /**
     * @return the nextPageUuid
     */
    public UUID getNextPageUuid() {
        return nextPageUuid;
    }

    /**
     * @param nextPageUuid
     *            the nextPage to set
     */
    public void setNextPageUuid(final UUID nextPage) {
        this.nextPageUuid = nextPage;
    }

}
