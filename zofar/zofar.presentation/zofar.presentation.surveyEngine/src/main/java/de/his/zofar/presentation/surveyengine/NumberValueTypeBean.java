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
package de.his.zofar.presentation.surveyengine;

import javax.inject.Inject;

import de.his.zofar.presentation.surveyengine.controller.SessionController;

/**
 * an implementation of the abstract value type bean for number values.
 *
 * @author le
 *
 */
public class NumberValueTypeBean extends AbstractAnswerBean {

    /**
     *
     */
    private static final long serialVersionUID = 49785145868298064L;

    /**
     * the value which must be stored.
     */
    private Double value;

    /**
     * constructor delegate for the parent constructor.
     * 
     * @param sessionController
     * @param variableName
     */
    @Inject
    public NumberValueTypeBean(final SessionController sessionController,
            final String variableName) {
        super(sessionController, variableName);
    }

    /**
     * @return the value
     */
    public Double getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final Double value) {
        this.value = value;
        saveValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.his.zofar.surveyengine.AbstractValueTypeBean#toPlaceholer()
     */
    @Override
    public Object toPlaceholder() {
        return this.getValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.zofar.presentation.surveyengine.AbstractAnswerBean#setStringValue
     * (java.lang.String)
     */
    @Override
    public void setStringValue(final String stringValue) {
    	if (stringValue == null || stringValue.isEmpty() || stringValue.equals("null")) {
    		this.value = null;
    	} else {
    		this.value = Double.valueOf(stringValue);
    	}
    	saveValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.zofar.presentation.surveyengine.AbstractAnswerBean#getStringValue
     * ()
     */
    @Override
    public String getStringValue() {
        return String.valueOf(value);
    }

}
