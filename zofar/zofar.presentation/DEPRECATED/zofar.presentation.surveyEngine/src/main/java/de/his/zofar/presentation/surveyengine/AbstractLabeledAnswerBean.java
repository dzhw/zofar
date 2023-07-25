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

import java.util.Map;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.controller.SessionController;
import de.his.zofar.presentation.surveyengine.util.JsfUtility;

/**
 * @author le
 *
 */
public abstract class AbstractLabeledAnswerBean extends AbstractAnswerBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractLabeledAnswerBean.class);

    /**
     *
     */
    private static final long serialVersionUID = 8239039866530156642L;

    private static final String EL_PATTERN = "#{%s}";

    /**
     * @param sessionController
     * @param variableName
     */
    public AbstractLabeledAnswerBean(final SessionController sessionController,
            final String variableName) {
        super(sessionController, variableName);
    }

    protected abstract Map<String, String> loadLabels();

    public final String getLabel() {
        final Map<String, String> labels = this.loadLabels();

        String result = "";

        final FacesContext context = FacesContext.getCurrentInstance();

        for (final String visibleCondition : labels.keySet()) {
            final Boolean visible = JsfUtility.getInstance()
                    .evaluateValueExpression(context,
                            String.format(EL_PATTERN, visibleCondition),
                            Boolean.class);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("visible condition: {}, result: {}",
                        visibleCondition, visible);
            }
            if (visible) {
                result = JsfUtility.getInstance()
                        .evaluateValueExpression(
                                context,
                                String.format(EL_PATTERN,
                                        labels.get(visibleCondition)),
                                String.class);
                break;
            }
        }

        return result;
    }

}
