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
package de.his.zofar.presentation.surveyengine.provider;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * Bean to provide Episode specific EL - Functions
 * 
 * @author meisner dick friedrich
 * @version 0.0.1
 * 
 */
@ManagedBean(name = "episodesFunctions")
@ApplicationScoped
public class EpisodesProvider implements Serializable {
	private static final long serialVersionUID = -5807912056733640461L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EpisodesProvider.class);
	public EpisodesProvider() {
		super();
	}
	@PostConstruct
	private void init() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("init");
		}
	}
	public synchronized final String decideSplitTarget(final JsonArray arr, final int index,
			final JsonObject split_type_dict,final JsonArray split_type_order,final String fallbackTarget) {
		final FunctionProvider zofar = JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(),
				"#{zofar}", FunctionProvider.class);
		return decideSplitTarget(arr,index,split_type_dict,split_type_order,fallbackTarget,zofar);
	}
	public synchronized final String decideSplitTarget(final JsonArray arr, final int index,
			final JsonObject split_type_dict,final JsonArray split_type_order,final String fallbackTarget,final FunctionProvider zofar) {
		if (arr == null) {
			return fallbackTarget;
		}
		if (index < 0)
			return fallbackTarget;
		if (index >= arr.size())
			return fallbackTarget;
		final JsonObject episode = arr.get(index).getAsJsonObject();
		return this.decideSplitTarget(episode,split_type_dict,split_type_order,fallbackTarget);
	}
	public synchronized final String decideSplitTarget(final JsonElement episode,
			final JsonObject split_type_dict,final JsonArray split_type_order,final String fallbackTarget) {
		final FunctionProvider zofar = JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(),
				"#{zofar}", FunctionProvider.class);
		return this.decideSplitTarget(episode, split_type_dict, split_type_order, fallbackTarget,zofar);
	}
	public synchronized final String decideSplitTarget(final JsonElement episode,
			final JsonObject split_type_dict,final JsonArray split_type_order,final String fallbackTarget,final FunctionProvider zofar) {
		if(!zofar.hasJsonProperty(episode,"currentSplit")) return fallbackTarget;
		final Object currentSplit = zofar.getJsonProperty(episode,"currentSplit");
		if(currentSplit == null)return fallbackTarget;
		if ((JsonArray.class).isAssignableFrom(currentSplit.getClass())) {
			final JsonArray currentSplitArray = (JsonArray)currentSplit;
			Iterator<JsonElement> it = split_type_order.iterator();
			while(it.hasNext()) {
				final JsonElement type = it.next();
				if(!currentSplitArray.contains(type))continue;
				if(!zofar.hasJsonProperty(split_type_dict,type.getAsString()))continue;
				final JsonElement typedSplitInfo = split_type_dict.get(type.getAsString());
				if(typedSplitInfo == null)continue;
				if(!typedSplitInfo.isJsonObject())continue;
				final JsonObject typedSplitInfoObj = typedSplitInfo.getAsJsonObject();
				if(!zofar.hasJsonProperty(typedSplitInfoObj,"START_PAGE"))continue;
				return typedSplitInfo.getAsJsonObject().get("START_PAGE").getAsString();
			}
		}
		return fallbackTarget;
	}
}
