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
package de.his.zofar.presentation.survey.beans.controller;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.survey.beans.model.SurveyListModelBean;
import de.his.zofar.service.common.model.PageDTO;
import de.his.zofar.service.survey.model.Survey;
import de.his.zofar.service.survey.service.SurveyService;
/**
 * this class holds all actions and listeners for survey list view.
 * 
 * @author le
 * 
 */
@ManagedBean
@ViewScoped
public class SurveyListControllerBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4385558214756777224L;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SurveyListControllerBean.class);
    @ManagedProperty(value = "#{surveyExternalService}")
    private SurveyService surveyService;
    @ManagedProperty(value = "#{surveyListModelBean}")
    private SurveyListModelBean modelBean;
    /**
     * @param surveyService
     *            the surveyService to set
     */
    public void setSurveyService(final SurveyService surveyService) {
        this.surveyService = surveyService;
    }
    /**
     * @param modelBean
     *            the modelBean to set
     */
    public void setModelBean(final SurveyListModelBean modelBean) {
        this.modelBean = modelBean;
    }
    /**
     * loads surveys depending on the SurveyQueryDTO instance and fills the
     * survey list
     * 
     * @param event
     */
    public void search(final ActionEvent event) {
        LOGGER.debug("clicked on search button");
        LOGGER.debug("query: {}, states: {}", modelBean.getSurveyQuery()
                .getName(), modelBean.getSurveyQuery().getStates());
        final PageDTO<Survey> foundSurveys = surveyService.searchAll(modelBean
                .getSurveyQuery());
        modelBean.setSurveysPage(foundSurveys);
    }
    /**
     * delete on survey from the survey list
     * 
     * @param survey
     * @return
     */
    public String delete(final Survey survey) {
        LOGGER.debug("deleting survey with name: {}", survey.getName());
        surveyService.delete(survey);
        final boolean success = modelBean.getSurveysPage().getContent().remove(survey);
        LOGGER.debug("deleting successful? {}", success);
        return null;
    }
}
