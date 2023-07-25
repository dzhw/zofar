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
package de.his.zofar.presentation.surveyengine.controller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import de.his.zofar.presentation.surveyengine.util.SystemConfiguration;
/**
 * @author meisner
 *
 */
@Controller("recorder")
@Scope("request")
public class Recorder implements ActionListener, Serializable{
	private static final long serialVersionUID = -2041709376155367784L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Recorder.class);
	private static final String RECORD_SEP = "::";
	@Inject
	private SessionController session;
	@Inject
	private NavigatorBean navigation;
	@Override
	public void processAction(ActionEvent actionEvent)
			throws AbortProcessingException {
		final SystemConfiguration system = SystemConfiguration.getInstance();
		if(system.record()){
			final FacesContext context = FacesContext.getCurrentInstance();
			final ExternalContext externalContext = context	.getExternalContext();
			final HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			final String token = session.getParticipant().getToken();
			final Enumeration<String> paramNames = request.getParameterNames();
			final StringBuffer recordData = new StringBuffer();
			recordData.append("token=" + token + RECORD_SEP);
			recordData.append("page=" + navigation.getSameViewID() + RECORD_SEP);
			String dir = "UNKOWN";
			if(navigation.isBackward())dir="BACKWARD";
			else if(navigation.isForward())dir="FORWARD";
			else if(navigation.isSame())dir="SAME";
			recordData.append("dir=" + dir + RECORD_SEP);
			while(paramNames.hasMoreElements()){
				final String key = paramNames.nextElement();
				final Object value = request.getParameter(key);
				recordData.append(key+"="+value+RECORD_SEP);
			}
			writeToRecordFile(new File(system.recordDir()),token+".rec",recordData.toString());
		}
	}
	private void writeToRecordFile(final File outputDirectory,final String recordFileName, final String content) {
		if (!outputDirectory.exists())
			outputDirectory.mkdir();
		File target = new File(outputDirectory.getAbsolutePath()+File.separator+recordFileName);
		try {
			boolean newFile = false;
			if (!target.exists()){
				target.createNewFile();
				newFile = true;
			}
			if(!newFile)IOUtils.write("\n"+content, new FileOutputStream(target,true));
			else IOUtils.write(content, new FileOutputStream(target,false));
		} catch (IOException e) {
			LOGGER.error("failed to create record file", e);
		}
	}
}
