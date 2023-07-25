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
package de.his.zofar.generator.maven.plugin.generator.page;

import org.apache.xmlbeans.XmlException;

import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;

import de.his.zofar.xml.questionnaire.IdentificationalType;

/**
 * @author le
 *
 */
interface IXhtmlCreator {

    /**
     * adds the element to the section, more precisely to the body of the
     * section.
     *
     * @param source
     * @param target
     * @throws XmlException
     */
    void addToSection(IdentificationalType source, SectionType target,final boolean root)
            throws XmlException;

    /**
     * @param source
     * @param target
     * @throws XmlException
     */
    void addToSort(IdentificationalType source, SortType target)
            throws XmlException;

}
