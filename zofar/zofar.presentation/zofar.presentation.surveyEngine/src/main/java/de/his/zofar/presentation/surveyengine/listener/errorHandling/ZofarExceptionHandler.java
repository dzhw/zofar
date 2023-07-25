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
package de.his.zofar.presentation.surveyengine.listener.errorHandling;
import java.util.Iterator;
import javax.el.ELContext;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.listener.errorHandling.exception.ZofarConstraintsOnexitException;
import de.his.zofar.presentation.surveyengine.listener.errorHandling.exception.ZofarConstraintsOnloadException;
import de.his.zofar.presentation.surveyengine.listener.errorHandling.exception.ZofarException;
/**
 * @author meisner
 * @see javax.faces.context.ExceptionHandlerWrapper
 * 
 */
public class ZofarExceptionHandler extends ExceptionHandlerWrapper {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarExceptionHandler.class);
	private ExceptionHandler bypassHandler;
	private ErrorStack errorStack;
	public ZofarExceptionHandler(ExceptionHandler bypassHandler) {
		super();
		this.bypassHandler = bypassHandler;
	}
	@Override
	public javax.faces.context.ExceptionHandler getWrapped() {
		return this.bypassHandler;
	}
	public ErrorStack getErrorStack() {
		return errorStack;
	}
	public void setErrorStack(ErrorStack errorStack) {
		this.errorStack = errorStack;
	}
	/** depending on the given Zofar-Exception redirect to associated Error-Page
	 * configured as constraintsOnExitError and constraintsOnLoadError in faces-config
	 * @see javax.faces.context.ExceptionHandlerWrapper#handle()
	 */
	@Override
	public void handle() throws FacesException {
		final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents()
				.iterator();
		while (i.hasNext()) {
			Throwable t = i.next().getContext().getException();
			Throwable relevantException = getRelevant(t);
			if (relevantException != null) {
				String targetOutcome = "same";
				boolean redirect = false;
				if ((ZofarConstraintsOnexitException.class)
						.isAssignableFrom(relevantException.getClass())) {
					targetOutcome = "constraintsOnExitError";
					redirect = true;
				}
				if ((ZofarConstraintsOnloadException.class)
						.isAssignableFrom(relevantException.getClass())) {
					targetOutcome = "constraintsOnLoadError";
					redirect = true;
				}
				if (redirect) {
					try {
						final FacesContext fc = FacesContext
								.getCurrentInstance();
						final NavigationHandler nav = fc.getApplication()
								.getNavigationHandler();
						if (errorStack == null) {
							final Application app = fc.getApplication();
							final ELContext elContext = fc.getELContext();
							errorStack = (ErrorStack) app.getELResolver()
									.getValue(elContext, null, "errorStack");
						}
						LOGGER.info("ErrorStack {}", errorStack);
						if (errorStack != null) {
							errorStack.setException(relevantException);
						}
						nav.handleNavigation(fc, null, targetOutcome);
					} finally {
						i.remove();
					}
				}
			}
		}
		getWrapped().handle();
	}
	private Throwable getRelevant(Throwable exception) {
		if (exception == null)
			return null;
		if ((ZofarException.class).isAssignableFrom(exception.getClass()))
			return exception;
		if (exception.getCause() != null)
			return getRelevant(exception.getCause());
		return null;
	}
}
