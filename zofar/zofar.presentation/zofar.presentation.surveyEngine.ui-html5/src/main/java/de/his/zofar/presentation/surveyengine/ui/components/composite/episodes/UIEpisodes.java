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
import java.util.ResourceBundle;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.renderer.episodes.EpisodesRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesComponent(value = "de.his.zofar.Calendar.episodes")
public class UIEpisodes extends UINamingContainer implements Identificational, Visible  {
	public static final String COMPONENT_FAMILY = "de.his.zofar.Calendar.episodes";
	private ResourceBundle bundle;
	public UIEpisodes() {
		super();
		this.bundle = ResourceBundle.getBundle("de.his.zofar.messages.episodes", FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	public ResourceBundle getBundle() {
		if ((this.bundle != null) && (!this.bundle.getLocale().equals(FacesContext.getCurrentInstance().getViewRoot().getLocale()))) {
			this.bundle = ResourceBundle.getBundle("de.his.zofar.messages.episodes", FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		return this.bundle;
	}
	@Override
	public String getRendererType() {
		return EpisodesRenderer.RENDERER_TYPE;
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
	public void setConfiguration(final String configuration) {
		this.getStateHelper().put("configuration", configuration);
	}
	/**
	 * We do not want to participate in state saving.
	 */
	@Override
	public boolean isTransient() {
		return true;
	}
}
