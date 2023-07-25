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
package de.his.zofar.presentation.surveyengine.security;
import java.io.Serializable;
import java.util.UUID;
import javax.faces.validator.FacesValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import de.his.zofar.presentation.surveyengine.facades.SurveyEngineServiceFacade;
import de.his.zofar.service.surveyengine.model.Participant;
/**
 * @author meisner
 * 
 */
@Controller("anonymousCreator")
@Scope("application")
@FacesValidator("de.his.zofar.security.captcha")
public class AnonymousCreator implements Serializable {
	private static final long serialVersionUID = 3186592828969020596L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AnonymousCreator.class);
	public AnonymousCreator() {
		super();
	}
	public String createAnonymousAccount() {
		Participant anonymous = null;
		if (anonymous == null) {
			final SurveyEngineServiceFacade surveyEngineService = new SurveyEngineServiceFacade();
			anonymous = surveyEngineService.createAnonymousParticipant();
		}
		return anonymous.getToken();
	}
	public boolean checkCaptcha(final String captchaId, final String captchaText) {
		return true;
	}
	public String generateCaptchaId() {
		return UUID.randomUUID().toString();
	}
}
