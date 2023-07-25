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
package de.his.zofar.presentation.surveyengine.controller;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.facades.SurveyEngineServiceFacade;
import de.his.zofar.presentation.surveyengine.util.SecurityHelper;
import de.his.zofar.presentation.surveyengine.util.SystemConfiguration;
import de.his.zofar.service.surveyengine.model.Participant;
import de.his.zofar.service.surveyengine.model.ParticipantPrincipal;
import de.his.zofar.service.surveyengine.model.SurveyData;
/**
 * @author le
 * 
 */
public class SessionController implements ActionListener, Serializable {
	/**
     *
     */
	private static final long serialVersionUID = 116831000200260361L;
	/**
     *
     */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);
	/**
     *
     */
	private static final String MISSING = "";
	private Map<String, String> dataCache = new HashMap<String, String>();
	/**
     *
     */
	private final SurveyEngineServiceFacade surveyEngineService;
	/**
     *
     */
	private Participant participant;
	private Locale currentLocale;
	private boolean exceptionFlag = false;;
	/**
     *
     */
	public SessionController() {
		this(true);
	}
	public SessionController(final boolean flag) {
		super();
		if (flag) {
			this.currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			this.surveyEngineService = new SurveyEngineServiceFacade();
			final ParticipantPrincipal principal = SecurityHelper.retrieveParticipantFromSecurityContext();
			this.participant = this.surveyEngineService.loadParticipant(principal.getUsername());
		} else {
			this.surveyEngineService = null;
		}
	}
	public Locale getCurrentLocale() {
		return this.currentLocale;
	}
	/**
	 * @return the participant
	 */
	public Participant getParticipant() {
		return this.participant;
	}
	/**
	 * @param variableName
	 * @return
	 */
	public String getValueForVariablename(final String variableName) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("request value for {}", variableName);
		}
		if (!this.participant.getSurveyData().containsKey(variableName)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no value for {} found, creating a new entry in the survey data.", variableName);
			}
			dataCache.put(variableName, MISSING);
		} else {
			final SurveyData fromParticipant = this.participant.getSurveyData().get(variableName);
			dataCache.put(fromParticipant.getVariableName(), fromParticipant.getValue());
		}
		return dataCache.get(variableName);
	}
	public boolean isExceptionFlag() {
		return exceptionFlag;
	}
	public void setExceptionFlag(boolean exceptionFlag) {
		this.exceptionFlag = exceptionFlag;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent
	 * )
	 */
	@Override
	public void processAction(final ActionEvent actionEvent) throws AbortProcessingException {
		boolean flag = false;
		final Object source = actionEvent.getSource();
		if ((source != null) && (HtmlCommandButton.class).isAssignableFrom(source.getClass())) {
			final SystemConfiguration conf = SystemConfiguration.getInstance();
			final String id = ((HtmlCommandButton) source).getId();
			final String action = (String) ((HtmlCommandButton) source).getActionExpression().getExpressionString();
			if (id.equals("defaultBt") && (conf.saveOnForward() || conf.saveOnSame()))
				flag = true;
			if (id.equals("forwardBt") && (conf.saveOnForward() || conf.saveOnSame()))
				flag = true;
			if (id.equals("backwardBt") && (conf.saveOnBackward() || conf.saveOnSame()))
				flag = true;
			if (id.equals("sendBt") && (conf.saveOnForward() || conf.saveOnBackward() || conf.saveOnSame()))
				flag = true;
			if (action.equals("same") && conf.saveOnSame())
				flag = true;
		}
		if (flag) {
			Iterator<String> it = dataCache.keySet().iterator();
			while (it.hasNext()) {
				final String key = it.next();
				final String value = dataCache.get(key);
				if (this.participant.getSurveyData().containsKey(key)) {
					this.participant.getSurveyData().get(key).setValue(value);
				} else {
					final SurveyData data = new SurveyData(this.participant, key, value);
					this.participant.getSurveyData().put(key, data);
				}
			}
		}
		boolean canBeCleaned = false;
		final HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("save participant {} ({})", this.participant.getToken(), session.getId());
		}
		if (this.isExceptionFlag()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ATTENTION");
			}
			try {
				this.participant = this.surveyEngineService.saveParticipant(this.participant);
				canBeCleaned = true;
			} catch (Exception e) {
				if ((e.getCause() != null) && ((org.hibernate.exception.ConstraintViolationException.class).isAssignableFrom(e.getCause().getClass()))) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("NOPANIC:  catch Duplicated Entry Exception {}", e.getCause());
					}
				} else {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("[SESSION EXCEPTION] Exception cause for {}: {}","{"+this.participant.getToken()+"} ("+dataCache+")", e);
					}
				}
			}
			this.setExceptionFlag(false);
		} else {
			try {
				this.participant = this.surveyEngineService.saveParticipant(this.participant);
				canBeCleaned = true;
			} catch (Exception e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("[SESSION EXCEPTION] Exception cause for {}: {}","{"+this.participant.getToken()+"} ("+dataCache+")", e);
				}
			}
		}
		if(canBeCleaned)dataCache.clear();
	}
	/**
	 * @param variableName
	 * @param value
	 */
	public void setValueForVariablename(final String variableName, final String value) {
		dataCache.put(variableName, value);
	}
	public String switchCurrentLocale(final String locale) {
		this.currentLocale = new Locale(locale);
		return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true";
	}
 	public void switchPageLocale(final String locale) {
		this.currentLocale = new Locale(locale);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(this.currentLocale); 
	}
	public Map<String, String> loadLabelMap(final String variable, final String answerOptionUid) {
		return this.surveyEngineService.loadLabelsAndConditions(variable, answerOptionUid);
	}
}
