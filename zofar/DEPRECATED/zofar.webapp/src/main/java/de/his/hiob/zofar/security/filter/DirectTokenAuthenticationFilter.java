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
package de.his.hiob.zofar.security.filter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;
/**
 *
 */
/**
 * Filter for authentication with direct links.
 *
 * @author le
 *
 */
public class DirectTokenAuthenticationFilter extends
        AbstractAuthenticationProcessingFilter {
    /**
     *
     */
    private static final String DEFAULT_TOKEN_REQUEST_PARAMETER = "zofar_token";
    /**
     *
     */
    private String tokenRequestParameter = DEFAULT_TOKEN_REQUEST_PARAMETER;
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DirectTokenAuthenticationFilter.class);
    /**
     *
     */
    public DirectTokenAuthenticationFilter() {
        super("/survey/index.html");
    }
    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.authentication.
     * AbstractAuthenticationProcessingFilter
     * #attemptAuthentication(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Authentication attemptAuthentication(
            final HttpServletRequest request, final HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String token = obtainTokenFromRequest(request);
        LOGGER.debug("token from request: {}", token);
        if (token == null) {
            token = "";
        }
        token = token.trim();
        final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                token, token);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
    /**
     * Provided so that subclasses may configure what is put into the
     * authentication request's details property.
     *
     * @param request
     *            that an authentication request is being created for
     * @param authRequest
     *            the authentication request object that should have its details
     *            set
     */
    protected void setDetails(final HttpServletRequest request,
            final UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource
                .buildDetails(request));
    }
    /**
     * @param request
     *            the request to obtain the token from
     * @return the token found in the request
     */
    protected final String obtainTokenFromRequest(
            final HttpServletRequest request) {
        return request.getParameter(tokenRequestParameter);
    }
    /**
     * @param tokenRequestParameter
     *            the tokenRequestParameter to set
     */
    public void setTokenRequestParameter(final String tokenRequestParameter) {
        Assert.hasText(tokenRequestParameter, "Token parameter cannot be empty");
        this.tokenRequestParameter = tokenRequestParameter;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.web.authentication.
     * AbstractAuthenticationProcessingFilter
     * #requiresAuthentication(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected boolean requiresAuthentication(final HttpServletRequest request,
            final HttpServletResponse response) {
        final String tokenFromRequest = obtainTokenFromRequest(request);
        if (tokenFromRequest == null || tokenFromRequest.isEmpty()) {
            return false;
        } else {
            return super.requiresAuthentication(request, response);
        }
    }
}
