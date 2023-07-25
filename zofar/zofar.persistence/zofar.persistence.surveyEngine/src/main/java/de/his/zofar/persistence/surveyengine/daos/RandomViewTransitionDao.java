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
package de.his.zofar.persistence.surveyengine.daos;

import de.his.zofar.persistence.common.daos.GenericCRUDDao;
import de.his.zofar.persistence.surveyengine.entities.ParticipantEntity;
import de.his.zofar.persistence.surveyengine.entities.RandomViewTransitionEntity;

public interface RandomViewTransitionDao extends
        GenericCRUDDao<RandomViewTransitionEntity> {

    /*
     * Overwritten to be able to monitor run times with Jamon.
     *
     * (non-Javadoc)
     *
     * @see org.springframework.data.repository.CrudRepository#save(S)
     */
    @Override
    @SuppressWarnings("unchecked")
    public RandomViewTransitionEntity save(
            RandomViewTransitionEntity randomViewTransition);

    public RandomViewTransitionEntity findByParticipantAndFromViewId(
            ParticipantEntity participant, String fromViewId);

}
