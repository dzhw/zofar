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
package de.his.hiob.zofar.security.entrypoints;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author le
 *
 */
public class ParticipantEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ParticipantEntryPoint.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.web.AuthenticationEntryPoint#commence(javax
     * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * org.springframework.security.core.AuthenticationException)
     */
    @Override
    public void commence(final HttpServletRequest request,
            final HttpServletResponse response, final AuthenticationException authException)
            throws IOException, ServletException {
        LOGGER.debug("you are using the entry point for participants");
    }

}
