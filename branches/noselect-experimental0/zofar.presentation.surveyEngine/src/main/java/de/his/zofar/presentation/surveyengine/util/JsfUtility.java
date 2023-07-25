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
package de.his.zofar.presentation.surveyengine.util;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

/**
 * @author le
 *
 */
public class JsfUtility {

    private static final JsfUtility INSTANCE = new JsfUtility();

    private JsfUtility() {
        super();
    }

    public static final JsfUtility getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> T evaluateValueExpression(final FacesContext context,
            final String expression, final Class<T> type) {
        final Application application = context.getApplication();
        final ELContext elContext = context.getELContext();
        final ExpressionFactory ef = application.getExpressionFactory();
        final ValueExpression valueExpression = ef.createValueExpression(
                elContext, expression, type);
        final T value = (T) valueExpression.getValue(elContext);

        return value;
    }

}
