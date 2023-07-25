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
package de.his.zofar.service.surveyengine.mapper;

import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.persistence.surveyengine.entities.ParticipantEntity;
import de.his.zofar.persistence.surveyengine.entities.SurveyDataEntity;
import de.his.zofar.service.surveyengine.model.Participant;
import de.his.zofar.service.surveyengine.model.SurveyData;

/**
 * @author le
 *
 */
public class PersistentMapMapper extends BeanMappingBuilder {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PersistentMapMapper.class);

    /*
     * (non-Javadoc)
     *
     * @see org.dozer.loader.api.BeanMappingBuilder#configure()
     */
    @Override
    protected void configure() {
        LOGGER.info("adding custom mapper for persistent maps on participant entity");
        mapping(ParticipantEntity.class, Participant.class).fields(
                "surveyData", "surveyData",
                FieldsMappingOptions.hintA(SurveyDataEntity.class),
                FieldsMappingOptions.hintB(SurveyData.class));
    }

}
