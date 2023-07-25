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
package de.his.zofar.presentation.surveyengine.ui.components.tablebase;
import javax.faces.component.UINamingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain;
/**
 * abstract custom component for all response domain that rendered in a table
 * with matrix items as rows.
 *
 * functionality that are included are: * sorting * zebra rendering of items
 *
 * @author le
 *
 */
public abstract class AbstractTableResponseDomain extends UINamingContainer implements Identificational, IResponseDomain {
	public static final String COMPONENT_FAMILY = "org.zofar.MatrixResponseDomain";
    /**
     * the HTML class. children of this class should set it own HTML classes
     * with setter of this member.
     */
    /**
     * LOGGER
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractTableResponseDomain.class);
    /**
     *
     */
    public AbstractTableResponseDomain() {
        super();
    }
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	@Deprecated
	public String getUID() {
		return this.getId();
	}
}
