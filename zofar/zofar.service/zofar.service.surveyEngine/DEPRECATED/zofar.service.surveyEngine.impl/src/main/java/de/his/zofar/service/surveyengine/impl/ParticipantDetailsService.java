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
package de.his.zofar.service.surveyengine.impl;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import de.his.zofar.persistence.surveyengine.daos.ParticipantDao;
import de.his.zofar.persistence.surveyengine.entities.ParticipantEntity;
import de.his.zofar.service.common.AbstractService;
import de.his.zofar.service.surveyengine.model.ParticipantPrincipal;

/**
 * @author le
 *
 */
public class ParticipantDetailsService extends AbstractService implements
        UserDetailsService {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ParticipantDetailsService.class);

    /**
     *
     */
    private static final String PARTICIPANT_NOT_FOUND_MESSAGE = "No Participant with token '%s' in DB";

    /**
     *
     */
    private final ParticipantDao participantDao;

    /**
     * constructor injection to provide instance wide access to daos.
     *
     * @param participantDao
     */
    @Inject
    public ParticipantDetailsService(final Mapper mapper,
            final ParticipantDao participantDao) {
        super(mapper);
        this.participantDao = participantDao;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "loading Participant for token '{}' for authentication.",
                    username);
        }
        final ParticipantEntity foundEntity = this.participantDao
                .findByToken(username);
        if (foundEntity == null) {
            throw new UsernameNotFoundException(String.format(
                    PARTICIPANT_NOT_FOUND_MESSAGE, username));
        }
        return new ParticipantPrincipal(foundEntity.getToken(),
                foundEntity.getPassword());
    }

}
