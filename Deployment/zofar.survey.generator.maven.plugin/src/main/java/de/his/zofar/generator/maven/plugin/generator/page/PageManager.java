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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.his.zofar.generator.maven.plugin.mojo.PagesGeneratorMojo;
import de.his.zofar.xml.questionnaire.VariableType;

/**
 * singleton class to store the current page.
 *
 * @author le
 *
 */
public final class PageManager {

    private static final PageManager INSTANCE = new PageManager();

    private final Set<VariableType> additionalVariables = new HashSet<>();
    private final Map<VariableType, Map<String,String>> additionalVariableOptions = new HashMap<>();

    private ZofarWebPage currentPage;

    private PagesGeneratorMojo mojo;

    private PageManager() {
        super();
    }

    public static final PageManager getInstance() {
        return INSTANCE;
    }

    /**
     * @return the currentPage
     */
    public ZofarWebPage getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage
     *            the currentPage to set
     */
    public void setCurrentPage(final ZofarWebPage currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return the mojo
     */
    public PagesGeneratorMojo getMojo() {
        return mojo;
    }

    /**
     * @param mojo
     *            the mojo to set
     */
    public void setMojo(final PagesGeneratorMojo mojo) {
        this.mojo = mojo;
    }

    /**
     * @return the additionalVariables
     */
    public Set<VariableType> getAdditionalVariables() {
        return additionalVariables;
    }
    
    /**
     * @return the additionalVariables
     */
    public Map<VariableType, Map<String,String>> getAdditionalVariableOptions() {
        return additionalVariableOptions;
    }

}
