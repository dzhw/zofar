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
package de.his.zofar.presentation.surveyengine.ui.util;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain;

/**
 * singleton utility class for JSF functions that need a faces context instance.
 * 
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

    /**
     * evaluate an EL expression against the given faces context. the return
     * type of the evaluated EL expression is defined by the type argument.
     *
     * @param context
     * @param expression
     * @param type
     * @return
     */
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
    
    
    public UIComponent getParentResponseDomain(final UIComponent parentComponent) {
        UIComponent parentResponseDomain = null;
        UIComponent parent = parentComponent;

        if (parent != null) {
	        if (parent instanceof IResponseDomain) {
	            parentResponseDomain = parent;
	        } else {
	            while (parent != null) {
	                parent = parent.getParent();
	                if (parent instanceof IResponseDomain) {
	                    parentResponseDomain = parent;
	                    break;
	                }
	            }
	        }
        }

        return parentResponseDomain;
    }

	public void setExpressionValue(final FacesContext context,final String expression,final Object value) {
        final Application application = context.getApplication();
        final ELContext elContext = context.getELContext();
        final ExpressionFactory ef = application.getExpressionFactory();
        final ValueExpression valueExpression = ef.createValueExpression(
                elContext, expression, value.getClass());
        valueExpression.setValue(elContext, value);
	}

}
