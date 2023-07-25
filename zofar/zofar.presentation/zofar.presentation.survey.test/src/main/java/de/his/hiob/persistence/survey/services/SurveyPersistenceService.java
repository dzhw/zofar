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
package de.his.hiob.persistence.survey.services;
import java.util.List;
import org.springframework.stereotype.Service;
import de.his.hiob.model.common.exceptions.NotYetImplementedException;
import de.his.hiob.model.common.models.PageDTO;
import de.his.hiob.model.questionnaire.models.Questionnaire;
import de.his.hiob.model.survey.models.Survey;
import de.his.hiob.model.survey.models.SurveyQueryDTO;
import de.his.hiob.persistence.common.services.PersistenceService;
/**
 * @author le
 *
 */
@Service
public class SurveyPersistenceService extends PersistenceService {
    public Survey create(final Survey survey) {
        throw new NotYetImplementedException();
    }
    public void delete(final Survey survey) {
        throw new NotYetImplementedException();
    }
    public List<Survey> searchAll() {
        throw new NotYetImplementedException();
    }
    public PageDTO<Survey> searchAll(final SurveyQueryDTO surveyQuery) {
        throw new NotYetImplementedException();
    }
    public Survey search(final SurveyQueryDTO surveyQuery) {
    	throw new NotYetImplementedException();
    }
}
