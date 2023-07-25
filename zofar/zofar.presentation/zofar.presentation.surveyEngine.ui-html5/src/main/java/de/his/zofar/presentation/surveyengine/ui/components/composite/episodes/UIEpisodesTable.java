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
package de.his.zofar.presentation.surveyengine.ui.components.composite.episodes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.ui.renderer.episodes.EpisodesTableRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesComponent(value = "de.his.zofar.Calendar.table")
public class UIEpisodesTable extends UINamingContainer implements Identificational, Visible  {
	public static final String COMPONENT_FAMILY = "de.his.zofar.Calendar.table";
	private ResourceBundle bundle;
	public UIEpisodesTable() {
		super();
		this.bundle = ResourceBundle.getBundle("de.his.zofar.messages.episodes", FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	public String getRendererType() {
		return EpisodesTableRenderer.RENDERER_TYPE;
	}
	public ResourceBundle getBundle() {
		if ((this.bundle != null) && (!this.bundle.getLocale().equals(FacesContext.getCurrentInstance().getViewRoot().getLocale()))) {
			this.bundle = ResourceBundle.getBundle("de.his.zofar.messages.episodes", FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		return this.bundle;
	}
	public String getLanguage() {
		return (String) this.getStateHelper().eval("lang");
	}
	public void setLanguage(final String language) {
		this.getStateHelper().put("lang", language);
	}
	public String getDisabled() {
		return (String) this.getStateHelper().eval("disabled");
	}
	public String getFocus() {
		return (String) this.getStateHelper().eval("focus");
	}
	public void setFocus(final String focus) {
		this.getStateHelper().put("focus", focus);
	}
	public void setDisabled(final String disabled) {
		this.getStateHelper().put("disabled", disabled);
	}
	public String getMinYear() {
		final Object tmp = this.getStateHelper().eval("minYear");
		if(tmp == null)return null;
		final FacesContext context = FacesContext.getCurrentInstance();
		final String back = JsfUtility.getInstance().evaluateValueExpression(context, "#{"+tmp+"}", String.class);
		return (String) back;
	}
	public void setMinYear(final String minYear) {
		this.getStateHelper().put("minYear", minYear);
	}
	public String getMaxYear() {
		final String tmp = (String)this.getStateHelper().eval("maxYear");
		if(tmp == null)return null;
		final FacesContext context = FacesContext.getCurrentInstance();
		final String back = JsfUtility.getInstance().evaluateValueExpression(context, "#{"+tmp+"}", String.class);
		return (String) back;
	}
	public void setMaxYear(final String maxYear) {
		this.getStateHelper().put("maxYear", maxYear);
	}
	public String getMinMonth() {
		final Object tmp = this.getStateHelper().eval("minMonth");
		if(tmp == null)return null;
		final FacesContext context = FacesContext.getCurrentInstance();
		final String back = JsfUtility.getInstance().evaluateValueExpression(context, "#{"+tmp+"}", String.class);
		return (String) back;
	}
	public void setMinMonth(final String minMonth) {
		this.getStateHelper().put("minMonth", minMonth);
	}
	public String getMaxMonth() {
		final String tmp = (String)this.getStateHelper().eval("maxMonth");
		if(tmp == null)return null;
		final FacesContext context = FacesContext.getCurrentInstance();
		final String back = JsfUtility.getInstance().evaluateValueExpression(context, "#{"+tmp+"}", String.class);
		return (String) back;
	}
	public void setMaxMonth(final String maxMonth) {
		this.getStateHelper().put("maxMonth", maxMonth);
	}
	public String getConfiguration() {
		final String tmp = (String)this.getStateHelper().eval("configuration");
		if(tmp == null)return null;
		final FacesContext context = FacesContext.getCurrentInstance();
		final String back = JsfUtility.getInstance().evaluateValueExpression(context, "#{"+tmp+"}", String.class);
		return (String) back;
	}
	public JsonObject getConfigAsJson() {
		final String configStr = this.getConfiguration();
		if(configStr == null)return null;
		if(configStr.contentEquals(""))return null;
		final JsonParser jsonParser = new JsonParser();
		final JsonElement tmp = jsonParser.parse(configStr);
		if(tmp.isJsonObject())return (JsonObject)tmp;
		return null;
	}
	public void setConfiguration(final String configuration) {
		this.getStateHelper().put("configuration", configuration);
	}
	public JsonArray getData() {
		final FacesContext context = FacesContext.getCurrentInstance();
		final IAnswerBean var =  (IAnswerBean)this.getStateHelper().eval("var");
		final JsonObject config = this.getConfigAsJson();
		final JsonArray typeArr = config.get("type").getAsJsonObject().get("values").getAsJsonArray();
		Iterator<JsonElement> it = typeArr.iterator();
		boolean first = true;
		final StringBuffer typesStr = new StringBuffer();
		while(it.hasNext()) {
			final JsonElement typeItem = it.next();
			if(!typeItem.isJsonObject())continue;
			final JsonObject typeObj = typeItem.getAsJsonObject();
			if(!first)typesStr.append(",");
			typesStr.append("'"+typeObj.get("id").getAsString()+"'");
			first = false;
		}
		final JsonArray dataJson = JsfUtility.getInstance().evaluateValueExpression(context, "#{zofar.sortedLikeNextEpisodeIndex(zofar.str2jsonArr('"+var.getStringValue()+"'),zofar.list("+typesStr.toString()+"))}", JsonArray.class);
		return dataJson;
	}
	public List<JsonObject> getDataList() {
		final List<JsonObject> back = new ArrayList<JsonObject>();
		final JsonArray data = this.getData();
		if(data != null) {
			Iterator<JsonElement> it = data.iterator();
			while(it.hasNext()) {
				final JsonElement element = it.next();
				if(element.isJsonObject())back.add(element.getAsJsonObject());
			}
		}
		return back;
	}
	public IAnswerBean getVar() {
		final IAnswerBean var =  (IAnswerBean)this.getStateHelper().eval("var");
		return var;
	}
	/**
	 * We do not want to participate in state saving.
	 */
	@Override
	public boolean isTransient() {
		return true;
	}
}
