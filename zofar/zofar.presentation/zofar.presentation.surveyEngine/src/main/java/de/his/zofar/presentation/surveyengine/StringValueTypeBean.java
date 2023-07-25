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
import de.his.zofar.presentation.surveyengine.util.StringUtility;
/**
 * implementation of the abstract value type bean for string values.
 *
 * @author le
 *
 */
public class StringValueTypeBean extends AbstractAnswerBean {
    /**
     *
     */
    private static final long serialVersionUID = -806135259939169619L;
    /**
     * the value which is stored.
     */
    private String value;
    /**
     * delegates to the parent constructor.
     *
     * @param sessionController
     * @param variableName
     */
    @Inject
    public StringValueTypeBean(final SessionController sessionController,
            final String variableName) {
        super(sessionController, variableName);
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value
     *            the value to set
     */
    public void setValue(final String value) {
    	final StringUtility stringUtility = StringUtility.getInstance();
        this.value = stringUtility.escapeHtml(value);
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
    	setValue(stringValue);
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
        return value;
    }
}
