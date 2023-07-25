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
package de.his.zofar.presentation.common.util;

import javax.faces.context.FacesContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Help resolving bean instances from the FacesContext.
 * 
 * Injection should be preferred over this helper method, however since we need
 * a way to separate the session scoped beans from the singleton (service) beans
 * this helper is used in order to be able to call service methods from a
 * session scoped bean.
 * 
 * @author Reitmann
 */
public class BeanHelper {

    /**
     * Get a reference to the given bean from the FacesContext.
     * 
     * @param clazz
     *            The type of the bean
     * @return a reference to the given bean from the FacesContext.
     */
    public static <T> T findBean(final Class<T> beanClazz) {
        final WebApplicationContext webContext = FacesContextUtils
                .getRequiredWebApplicationContext(FacesContext
                        .getCurrentInstance());
        return webContext.getBean(beanClazz);
    }
}
