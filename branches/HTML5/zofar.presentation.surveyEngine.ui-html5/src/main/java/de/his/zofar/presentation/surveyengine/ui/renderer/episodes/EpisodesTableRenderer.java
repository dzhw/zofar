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
package de.his.zofar.presentation.surveyengine.ui.renderer.episodes;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.his.zofar.presentation.surveyengine.ui.components.composite.episodes.UIEpisodes;
import de.his.zofar.presentation.surveyengine.ui.components.composite.episodes.UIEpisodesTable;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIEpisodesTable.COMPONENT_FAMILY, rendererType = EpisodesTableRenderer.RENDERER_TYPE)
public class EpisodesTableRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EpisodesTableRenderer.class);
	public static final String RENDERER_TYPE = "de.his.zofar.Calendar.table";
	public EpisodesTableRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final String fingerprint = component.getClientId(context).replace(':', '_');
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("article", component);
		writer.writeAttribute("id", component.getClientId(context) + "_main", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-orientation form-responsive", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "episodesTable", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		writer.startElement("div", component);
		writer.writeAttribute("id", "calendarTable"+fingerprint, null);
		writer.writeAttribute("class", "calendarTable"+fingerprint+"_Table calendarTable calendar-table table-responsive", null);
		final UIEpisodesTable episodes = (UIEpisodesTable)component;
		addModal(context,component,"calendarTable"+fingerprint,episodes.getLanguage());
	}
	private JsonObject getConfig(final UIComponent component) {
		if(component == null) return null;
		final UIEpisodesTable episodes = (UIEpisodesTable)component;
		return episodes.getConfigAsJson();
	}
	private JsonObject getDisabled(final UIComponent component) {
		if(component == null) return null;
		final UIEpisodesTable episodes = (UIEpisodesTable)component;
		if(episodes.getDisabled() == null)return null;
		if(episodes.getDisabled().contentEquals(""))return null;
		final JsonParser jsonParser = new JsonParser();
		final JsonElement tmp = jsonParser.parse(episodes.getDisabled());
		if(tmp.isJsonObject())return (JsonObject)tmp;
		return null;
	}
	private String getFocus(final UIComponent component) {
		if(component == null) return "()";
		final UIEpisodesTable episodes = (UIEpisodesTable)component;
		if(episodes.getFocus() == null)return "";
		final String focusStr = episodes.getFocus();
		if(focusStr.contentEquals(""))return "";
		final List<String> back = new ArrayList<String>();
		final String[] tmp = focusStr.split(",");
		if (tmp != null) {
			back.addAll( Arrays.asList(tmp));
		}	
		return back.toString().replaceAll(Pattern.quote("("), "").replaceAll(Pattern.quote(")"), "").replaceAll(Pattern.quote("["), "").replaceAll(Pattern.quote("]"), "");
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final String fingerprint = component.getClientId(context).replace(':', '_');
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent tmp = JsfUtility.getInstance().getComposite(component);
		if (tmp != null) {
			for (final UIComponent child : tmp.getChildren()) {
				child.encodeAll(context);
			}
		}
	}
	private void addModal(FacesContext context, UIComponent component,final String id, final String language) throws IOException {
		final UIEpisodesTable episode = (UIEpisodesTable)component;
		final int minYear=Integer.parseInt(episode.getMinYear());
		final int maxYear=Integer.parseInt(episode.getMaxYear());
		final int minMonthTemp=Integer.parseInt(episode.getMinMonth());
		String minMonth= ""+minMonthTemp;
		if(minMonthTemp<10) {
			minMonth="0"+minMonthTemp;
		}
		final int maxMonthTemp=Integer.parseInt(episode.getMaxMonth());
		String maxMonth= ""+maxMonthTemp;
		if(maxMonthTemp<10) {
			maxMonth="0"+maxMonthTemp;
		}
		String minMonthLabelStr = minMonth;
		String maxMonthLabelStr = maxMonth;
		final JsonObject config = this.getConfig(component);
		if(config.has("translation")) {
			final JsonObject translation = config.get("translation").getAsJsonObject();
			if(translation.has(language)) {
				final JsonObject langTranslation = translation.get(language).getAsJsonObject();
				if(langTranslation.has("month")) {
					final JsonObject langMonth = langTranslation.get("month").getAsJsonObject();
					if(langMonth.has(""+minMonthTemp))minMonthLabelStr = langMonth.get(""+minMonthTemp).getAsString();
					if(langMonth.has(""+maxMonthTemp))maxMonthLabelStr = langMonth.get(""+maxMonthTemp).getAsString();
				}
			}
		}
		final String modalTitleStr = episode.getBundle().getString("modalTitle");
		final String saveLabelStr = episode.getBundle().getString("saveLabel");
		final String cancelLabelStr = episode.getBundle().getString("cancelLabel");
		final String startLabelStr = episode.getBundle().getString("startDate");
		final String endLabelStr = episode.getBundle().getString("endDate");		
		final String sHOLabelStr = episode.getBundle().getString("sHO");
		final String eHOLabelStr = episode.getBundle().getString("eHO");
		final String warningNoEpisodeRangeStr = episode.getBundle().getString("warningNoEpisodeRange");
		final String warningNoEpisodeRangeLabelStr = episode.getBundle().getString("warningNoEpisodeRangeLabel");
		final String warningTypeChangedStr = episode.getBundle().getString("warningTypeChanged");
		final String warningNoTypeStr = episode.getBundle().getString("warningNoType");
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", "table-event-modal"+id, null);
		writer.writeAttribute("class", "modal", null);
		writer.writeAttribute("role", "dialog", null);
			writer.startElement("div", component);
			writer.writeAttribute("class", "modal-dialog", null);
				writer.startElement("div", component);
				writer.writeAttribute("class", "modal-content", null);
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-header", null);
						writer.startElement("button", component);
						writer.writeAttribute("type", "button", null);
						writer.writeAttribute("class", "close", null);
						writer.writeAttribute("data-dismiss", "modal", null);
							writer.startElement("span", component);
							writer.writeAttribute("aria-hidden", "true", null);
							writer.write("x");
							writer.endElement("span");
							writer.startElement("span", component);
							writer.writeAttribute("class", "sr-only", null);
							writer.write("Close");
							writer.endElement("span");
						writer.endElement("button");
						writer.startElement("h4", component);
						writer.writeAttribute("class", "modal-title", null);
						writer.write(""+modalTitleStr);
						writer.endElement("h4");
					writer.endElement("div");
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-body", null);
						writer.startElement("input", component);
						writer.writeAttribute("type", "hidden", null);
						writer.writeAttribute("id", "event-id", null);	
						writer.writeAttribute("name", "event-id", null);
						writer.endElement("input");
						final Set<String> properties = config.keySet();
						for(final String propertyKey : properties) {
							if(propertyKey.contentEquals("link"))continue;
							if(propertyKey.contentEquals("actions"))continue;
							if(propertyKey.contentEquals("translation"))continue;
							if(propertyKey.contentEquals("details"))continue;
							if(propertyKey.contentEquals("settings"))continue;
							final JsonElement property = config.get(propertyKey);
							if (!property.isJsonObject())continue;
							final JsonObject propertyObj = (JsonObject)property;
							writer.startElement("div", component);
							writer.writeAttribute("class", "form-group", null);	
							if(propertyObj.has("label")) {
								writer.startElement("label", component);
									writer.writeAttribute("class", "col-sm-4 control-label", null);	
									writer.writeAttribute("for", "event-"+propertyKey, null);	
									writer.write(propertyObj.get("label").getAsJsonObject().get(language).getAsString());
								writer.endElement("label");
							}
							if(propertyObj.has("values")) {
								final JsonElement propValues = propertyObj.get("values");
								writer.startElement("div", component);
								writer.writeAttribute("class", "col-sm-7", null);
								int typeofval = -1;
								if(propValues == null)typeofval = 0;
								else if (propValues.isJsonObject())typeofval = 1;
								else if (propValues.isJsonArray())typeofval = 2;
								else if (propValues.isJsonPrimitive())typeofval = 3;
								if(typeofval == 1) {
								}
								if(typeofval == 2) {
									writer.startElement("select", component);
									writer.writeAttribute("class", "form-control browser-default custom-select", null);
									writer.writeAttribute("id", "event-"+propertyKey, null);	
									writer.writeAttribute("name", "event-"+propertyKey, null);	
									final JsonArray answerOptions = (JsonArray)propValues;
									final Iterator<?> it = answerOptions.iterator();
									int lft = 0;
									while(it.hasNext()) {
										final JsonObject answerOption = (JsonObject)it.next();
										String aoid = null;
										if(answerOption.has("id"))aoid = answerOption.get("id").getAsString();
										if(aoid == null)continue;
										String label = null;
										if(answerOption.has("label"))label = answerOption.get("label").getAsJsonObject().get(language).getAsString();
										if(label == null)continue;
										String value = null;
										if(answerOption.has("value"))value = answerOption.get("value").getAsString();
										if(value == null)continue;
										String color = null;
										if(answerOption.has("color"))color = answerOption.get("color").getAsString();
										writer.startElement("option", component);
										writer.writeAttribute("id", "event-"+propertyKey+"-"+lft, null);
										writer.writeAttribute("value", aoid, null);
										writer.writeAttribute("data-value", value, null);
										if(color != null)writer.writeAttribute("data-color", color, null);
										writer.write(label);
										writer.endElement("option");
										lft = lft + 1;
									}
									writer.endElement("select");
									writer.startElement("div", component);
									writer.writeAttribute("class", "zo-modal-"+propertyKey+"-description zofar_episodes_disabled", null);
										writer.startElement("span", component);
										writer.writeAttribute("id", "event-desc-"+propertyKey, null);	
										writer.writeAttribute("name", "event-desc-"+propertyKey, null);	
											writer.write(propertyKey+" description");
										writer.endElement("span");
									writer.endElement("div");
								}
								if(typeofval == 3) {
									writer.startElement("div", component);
									writer.writeAttribute("class", "zo-modal-"+propertyKey+"-description zofar_episodes_disabled", null);
										writer.startElement("span", component);
										writer.writeAttribute("id", "event-desc-"+propertyKey, null);	
										writer.writeAttribute("name", "event-desc-"+propertyKey, null);	
										writer.write(propertyKey+" description");
										writer.endElement("span");
									writer.endElement("div");
									writer.startElement("input", component);
									writer.writeAttribute("class", "form-control", null);	
									writer.writeAttribute("id", "event-"+propertyKey, null);	
									writer.writeAttribute("name", "event-"+propertyKey, null);	
									writer.writeAttribute("placeholder", propValues.getAsString(), null);
									writer.writeAttribute("type", "text", null);
									writer.endElement("input");
								}
								writer.endElement("div");
							}
							writer.endElement("div");
						}
						writer.startElement("div", component);
						writer.writeAttribute("class", "zo-modal-start-description zofar_episodes_disabled", null);
							writer.startElement("span", component);
							writer.writeAttribute("id", "event-desc-start", null);	
							writer.writeAttribute("name", "event-desc-start", null);	
								writer.write("start description");
							writer.endElement("span");
						writer.endElement("div");
						writer.startElement("div", component);
						writer.writeAttribute("class", "form-group", null);
							writer.startElement("div", component);
							writer.writeAttribute("id", "monthpickerStartTable", null);
							writer.writeAttribute("class", "input-append date monthpickerCustom", null);
							writer.writeAttribute("data-value", "0", null);
							writer.writeAttribute("data-startDate", minYear+","+minMonth+","+"01", null);
							writer.writeAttribute("data-endDate", maxYear+","+maxMonth+","+(YearMonth.of(maxYear, Integer.parseInt(maxMonth))).lengthOfMonth(), null);
							writer.writeAttribute("data-dates-disabled", "07-2018,08-2018", null);
								writer.startElement("label", component);
								writer.writeAttribute("class", "col-sm-12 control-label", null);	
								writer.writeAttribute("for", "episodeStart", null);	
								writer.write(startLabelStr);
								writer.startElement("input", component);
								writer.writeAttribute("type", "text", null);
								writer.writeAttribute("readonly", "readonly", null);
								writer.writeAttribute("class", "episodeStart", null);
								writer.writeAttribute("name", "episodeStart", null);
								writer.writeAttribute("id", "episodeStart", null);
								writer.writeAttribute("data-placeholder", startLabelStr, null);
								writer.writeAttribute("placeholder", startLabelStr, null);
								writer.endElement("input");
								writer.startElement("span", component);
								writer.writeAttribute("class", "add-on", null);
								writer.startElement("i", component);
								writer.writeAttribute("class", "glyphicon glyphicon-calendar", null);
								writer.endElement("i");
								writer.endElement("span");
								writer.endElement("label");
							writer.endElement("div");
							/*Check Box START*/
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-12 control-label start-before-label-table", null);	
							writer.writeAttribute("for", "start-before-table", null);	
							writer.startElement("input", component);
								writer.writeAttribute("type", "checkbox", null);
								writer.writeAttribute("class", "start-before-table", null);
								writer.writeAttribute("name", "start-before-table", null);
								writer.writeAttribute("id", "start-before-table", null);
								writer.writeAttribute("checked", "false", null);
								writer.writeAttribute("value", "", null);
							writer.endElement("input");
							writer.write(" "+sHOLabelStr+" "+minMonthLabelStr+" "+minYear);
							writer.endElement("label");
							/*Check Box END*/
							writer.startElement("div", component);
							writer.writeAttribute("class", "zo-modal-end-description zofar_episodes_disabled", null);
								writer.startElement("span", component);
								writer.writeAttribute("id", "event-desc-end", null);	
								writer.writeAttribute("name", "event-desc-end", null);	
									writer.write("end description");
								writer.endElement("span");
							writer.endElement("div");
							writer.startElement("div", component);
							writer.writeAttribute("id", "monthpickerEndTable", null);
							writer.writeAttribute("class", "input-append date monthpickerCustom", null);
							writer.writeAttribute("data-value", "0", null);
							writer.writeAttribute("data-startDate", minYear+","+minMonth+","+"01", null);
							writer.writeAttribute("data-endDate", maxYear+","+maxMonth+","+(YearMonth.of(maxYear, Integer.parseInt(maxMonth))).lengthOfMonth(), null);
							writer.writeAttribute("data-dates-disabled", "07-2018,08-2018", null);
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-12 control-label", null);	
							writer.writeAttribute("for", "episodeEnd", null);	
							writer.write(endLabelStr);
							writer.startElement("input", component);
							writer.writeAttribute("type", "text", null);
							writer.writeAttribute("readonly", "readonly", null);
							writer.writeAttribute("class", "episodeEnd", null);
							writer.writeAttribute("name", "episodeEnd", null);
							writer.writeAttribute("id", "episodeEnd", null);
							writer.writeAttribute("data-placeholder", endLabelStr, null);
							writer.writeAttribute("placeholder", endLabelStr, null);
							writer.endElement("input");
							writer.startElement("span", component);
							writer.writeAttribute("class", "add-on", null);
							writer.startElement("i", component);
							writer.writeAttribute("class", "glyphicon glyphicon-calendar", null);
							writer.endElement("i");
							writer.endElement("span");
							writer.endElement("label");
							writer.endElement("div");
							/*Check Box START*/
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-12 control-label end-continues-label-table", null);	
							writer.writeAttribute("for", "end-continues-table", null);	
							writer.startElement("input", component);
								writer.writeAttribute("type", "checkbox", null);
								writer.writeAttribute("class", "end-continues-table", null);
								writer.writeAttribute("name", "end-continues-table", null);
								writer.writeAttribute("id", "end-continues-table", null);
								writer.writeAttribute("value", "", null);
								writer.writeAttribute("checked", "false", null);
							writer.endElement("input");
							writer.write(" "+eHOLabelStr+" "+maxMonthLabelStr+" "+maxYear);
							writer.endElement("label");
						writer.endElement("div");
					writer.endElement("div");
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-warning-range zofar_episodes_disabled", null);
					writer.startElement("span", component);
					writer.writeAttribute("class", "modal-warning-span modal-warning-info ", null);
					writer.write(warningNoEpisodeRangeStr);
					writer.endElement("span");
						writer.startElement("label", component);
						writer.writeAttribute("class", "col-sm-12 control-label end-continues-label-table", null);	
						writer.writeAttribute("for", "warning-table-range", null);	
							writer.startElement("input", component);
								writer.writeAttribute("type", "checkbox", null);
								writer.writeAttribute("class", "warning-table-range", null);
								writer.writeAttribute("name", "warning-table-range", null);
								writer.writeAttribute("id", "warning-table-range", null);
								writer.writeAttribute("value", "", null);
								writer.writeAttribute("checked", "false", null);
							writer.endElement("input");
						writer.startElement("span", component);
						writer.writeAttribute("class", "modal-warning-span", null);
						writer.write(warningNoEpisodeRangeLabelStr);
						writer.endElement("span");
						writer.endElement("label");
					writer.endElement("div");
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-warning-type zofar_episodes_disabled", null);
					writer.startElement("span", component);
					writer.writeAttribute("class", "modal-warning-span", null);
					writer.write(warningNoTypeStr);
					writer.endElement("span");
					writer.endElement("div");
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-warning-typechange zofar_episodes_disabled", null);
					writer.startElement("span", component);
					writer.writeAttribute("class", "modal-warning-span", null);
					writer.write(warningTypeChangedStr);
					writer.endElement("span");
					writer.endElement("div");
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-footer", null);
						writer.startElement("button", component);
						writer.writeAttribute("type", "button", null);
						writer.writeAttribute("class", "btn btn-default", null);
						writer.writeAttribute("data-dismiss", "modal", null);
						writer.writeAttribute("id", "table-dismiss-event"+id, null);
						writer.write(cancelLabelStr);
						writer.endElement("button");
						writer.startElement("button", component);
						writer.writeAttribute("type", "button", null);
						writer.writeAttribute("class", "btn btn-primary", null);
						writer.writeAttribute("id", "new-table-save-event"+id, null);
							writer.startElement("span", component);
							writer.writeAttribute("class", "span-save-button", null);
							writer.write(saveLabelStr);
							writer.endElement("span");
						writer.endElement("button");	
					writer.endElement("div");
				writer.endElement("div");
			writer.endElement("div");
		writer.endElement("div");
	}
	private String initScript(FacesContext context, UIComponent component, final String id, final String language) throws IOException {
		final String fingerprint = component.getClientId(context).replace(':', '_');
		final JsonObject config = this.getConfig(component);
		final Set<String> properties = config.keySet();
		final StringBuffer back = new StringBuffer();
		final int minYear=Integer.parseInt(((UIEpisodesTable)component).getMinYear());
		final int maxYear=Integer.parseInt(((UIEpisodesTable)component).getMaxYear());
		final int minMonth=Integer.parseInt(((UIEpisodesTable)component).getMinMonth());
		final int maxMonth=Integer.parseInt(((UIEpisodesTable)component).getMaxMonth());
		final String unknownStartStr = ((UIEpisodesTable)component).getBundle().getString("unknownStart");
		final String unknownEndStr = ((UIEpisodesTable)component).getBundle().getString("unknownEnd");
		final String confirmDeleteStr = ((UIEpisodesTable)component).getBundle().getString("confirmDelete");
		final String confirmUnknownStartStr = ((UIEpisodesTable)component).getBundle().getString("confirmUnknownStart");
		final String confirmUnknownEndStr = ((UIEpisodesTable)component).getBundle().getString("confirmUnknownEnd");
		final String startLabelStr = ((UIEpisodesTable)component).getBundle().getString("startDate");
		final String endLabelStr = ((UIEpisodesTable)component).getBundle().getString("endDate");		
		final String sHOLabelStr = ((UIEpisodesTable)component).getBundle().getString("sHO");
		final String eHOLabelStr = ((UIEpisodesTable)component).getBundle().getString("eHO");
		back.append(""
				+ "var minDate = new Date(1970,00,1,1,0,0);"
				+ "var maxDate = new Date(4000,11,31,1,0,0);"
				+"var typeChangedFlagTable"+id+"=false;"
				+"var typeValTable"+id+"='SlotDefault';"
				+ ""
							+ ""
				+ "$(document).ready(function(){"
				+ "		var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));"
				+ "		fieldObj.attr('data-episode-link','"+this.getConfig(component).get("link").getAsString()+"');"
				+ "		fieldObj.on(\"change\",function() {\n" 
				+ "			$(\"input[data-episode-link='"+this.getConfig(component).get("link").getAsString()+"']\").each(function(){"
				+ "				var cfield = $(this);"
				+ " 			var ref =fieldObj;"
				+ "				if(cfield.attr('id') != ref.attr('id')){"
				+ "					cfield.val(ref.val());"
				+ "					cfield.trigger('update',[ref]);"
				+ "				}"
				+ "			});"
				+ "		});"
				+ "		var fieldObjBlacklist = $('#'+$.escapeSelector('"+component.getClientId(context)+":blckList'));"
				+ "		fieldObjBlacklist.attr('data-list-link','"+this.getConfig(component).get("link").getAsString()+"');"
				+ "		fieldObjBlacklist.on(\"change\",function() {\n" 
				+ "			$(\"input[data-list-link='"+this.getConfig(component).get("link").getAsString()+"']\").each(function(){"
				+ "				var cfield = $(this);"
				+ " 			var ref =fieldObjBlacklist;"
				+ "				if(cfield.attr('id') != ref.attr('id')){"
				+ "					cfield.val(ref.val());"
				+ "					cfield.trigger('update',[ref]);"
				+ "				}"
				+ "			});"
				+ "		});"
				+"$('#table-event-modal"+id+"').keydown(function(e) {"
				+"	if (e.keyCode == 13) {"
				+"		if(event.target.id=='table-dismiss-event"+id+"') {"
				+"			$('#table-event-modal"+id+"').modal('toggle'); "	
				+"		} else {"
				+"			$('#new-table-save-event"+id+"').click();"
				+"		}"
				+"		e.preventDefault();"
				+"		return false;"
				+"	}"
				+"});"
				+"	var list = [];"
				+ "		fieldObj.on(\"update\",function() {\n" 
				+ "			update"+id+"();" 
				+ "		});"
				+ "		setJSMode"+id+"();"
				+ "});"
				+ ""
				+ "function setJSMode"+id+"() {"
				+ "		var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));"
				+ "		$('#"+id+" input[episode-action=\"update\"]').each(function(){"
				+ "			var episodeId = $(this).attr('episode-id');"
				+ "		});"
				+ "		$('#"+id+" input[episode-action=\"edit\"]').each(function(){"
				+ "			var episodeId = $(this).attr('episode-id');"
				+ "			var parentObj = $(this).parent();"
				+ "			var editBtn = $('<img></img>');\n"
				+ "			editBtn.attr('id', '"+id+"_'+episodeId+'_edit');\n"				
				+ "			editBtn.attr('name', '"+id+"_'+episodeId+'_edit');\n"				
				+ "			editBtn.attr('src', 'resources/html5/images/edit.svg');\n"			
				+ "			editBtn.attr('class', 'svg-edit');\n"			
				+ "			editBtn.attr('episode-action', 'edit');\n"			
				+ "			editBtn.attr('episode-id', episodeId);\n"			
				+ "			editBtn.attr('value', '');\n"
				+ "			$(editBtn).click(edit"+id+");"
				+ "			$(this).replaceWith(editBtn);\n"
				+ "		});"
				+ "		$('#"+id+" input[episode-action=\"delete\"]').each(function(){"
				+ "			var episodeId = $(this).attr('episode-id');"
				+ "			var parentObj = $(this).parent();"
				+ "			var deleteBtn = $('<img></img>');\n"
				+ "			deleteBtn.attr('id', '"+id+"_'+episodeId+'_delete');\n"				
				+ "			deleteBtn.attr('name', '"+id+"_'+episodeId+'_delete');\n"				
				+ "			deleteBtn.attr('src', 'resources/html5/images/trash.svg');\n"			
				+ "			deleteBtn.attr('class', 'svg-delete');\n"			
				+ "			deleteBtn.attr('episode-action', 'delete');\n"			
				+ "			deleteBtn.attr('episode-id', episodeId);\n"			
				+ "			deleteBtn.attr('value', '');\n"
				+ "			$(deleteBtn).click(delete"+id+");"
				+ "			$(this).replaceWith(deleteBtn);\n"
				+ "		});"
				+ ""
				+ "		$('#"+id+" input[episode-action=\"add\"]').each(function(){"
				+ "			var addBtn = $('<button></button>');\n"
				+ "			addBtn.attr('type', 'button');\n"			
				+ "			addBtn.attr('class', $(this).attr('class'));\n"			
				+ "			addBtn.attr('episode-action', 'add ');\n"			
				+ "			addBtn.attr('value', $(this).attr('value'));\n"
				+ "			addBtn.text( $(this).attr('value'));\n"
				+ "			$(addBtn).click(addEpisode"+id+");"
				+ "			$(this).replaceWith(addBtn);\n"
				+ "		});"
				+ "}"
				+ "function addEpisode"+id+"() {"
				+ ""
				+ "		var dataSource = loadData"+id+"();"
				+ "		var startDate = minDate;"
				+ "		var endDate = maxDate;"
				+"		var blackListT=[];"	
				+ "		var blckObjT = $('#'+$.escapeSelector('"+component.getClientId(context)+":blckList'));"
				+ "		var blckValT = $(blckObjT).val();"
				+ "		var blckContentT = decodeURIComponent(blckValT);"
				+"		if(blckContentT == ''){ blackListT= JSON.parse('[]');}"
				+ "		else {blackListT= JSON.parse(blckContentT);}"
				+ "		var nextId = -1;"
				+ "		for(var tmp in dataSource) {"
				+ "			var id = dataSource[tmp].id;"
				+ "			nextId = Math.max(nextId,id);"
				+ "		}"
				+ "		nextId = nextId + 1;"
				+ "		var v=-33;"
				+ "		for(var i in blackListT) {"
				+ "			v=blackListT[i];"
				+ "			if (nextId == v) {"
				+ "				nextId = nextId + 1;"
				+"				break;"
				+ "			}"
				+ "		}"
				+ " 	var newEvent = {\n"
				+ "			\"id\" : nextId,\n"
				+ "			\"startDate\": startDate,\n"
				+ "			\"endDate\" : endDate,\n"
				+ "			\"state\" : 'new'\n"
				+ "		};\n"
				+ ""
				+ "		dataSource.push(newEvent);\n"
				+ "		saveData"+id+"(dataSource);\n"
				+ "		dataSource = editCurrentEpisode"+id+"(dataSource,nextId);"
				+ "}"
				+ "function update"+id+"() {"
				+ "		var dataSource = loadData"+id+"();"
				+ "}"
				+ "function edit"+id+"() {"
				+ "		var dataSource = loadData"+id+"();"
				+ "		var episodeId = event.target.getAttribute('episode-id');"
				+ "		dataSource = editCurrentEpisode"+id+"(dataSource,episodeId);"
				+ "}\n\n"
				+ "function editCurrentEpisode"+id+"(dataSource,episodeId) {\n"
				+ "		console.log('editCurrentEpisode '+dataSource+' '+episodeId);\n"
				+ "     var currentEpisode ;\n"
				+ "		for(var i in dataSource) {\n"
				+ "		       if(dataSource[i].id == episodeId) {\n"
				+ "		          currentEpisode = dataSource[i];\n"
				+"					if (dataSource[i].type===undefined) {"
				+ "						typeValTable"+id+"='SlotDefault';"
				+ "					} else {"
				+ "						typeValTable"+id+"=dataSource[i].type;"
				+ "					}"
				+ "		           break;\n"
				+ "		       }\n"
				+ "		}\n"
				+ "		if(currentEpisode === undefined){\n"
				+ "			alert('episode undefined');\n"
				+ "		} else {\n");
							for(final String propertyKey : properties) {
								if(propertyKey.contentEquals("link"))continue;
								if(propertyKey.contentEquals("actions"))continue;
								if(propertyKey.contentEquals("translation"))continue;
								if(propertyKey.contentEquals("details"))continue;
								if(propertyKey.contentEquals("settings"))continue;
								final JsonElement property = config.get(propertyKey);
								if (!property.isJsonObject())continue;
								back.append("	list = [];");
								back.append( "$('#table-event-modal"+id+"').modal({");
								back.append( "backdrop: 'static'");
								back.append( "});");
								back.append( "$('#table-event-modal"+id+" #event-"+propertyKey+"').val('');\n");
								back.append( "clearAndHideWarnings"+id+"();"); 
								back.append( "$('#table-event-modal"+id+" #episodeStart').prop( 'disabled', false );");
								back.append( "$('#table-event-modal"+id+" #episodeEnd').prop( 'disabled', false);");
								back.append( "$('#table-event-modal"+id+" #start-before-table').prop('checked',false);");
								back.append( "$('#table-event-modal"+id+" #end-continues-table').prop('checked',false);");
								back.append( "$('#table-event-modal"+id+" #warning-table-range').prop('checked',false);");
							}
				back.append(""
				+ "		for(var i in dataSource) {\n"
				+ "		   if(dataSource[i].id == episodeId) {\n"
				+ "			   exists = true;\n"	
				+ "			   $('#table-event-modal"+id+" input[name=\"event-id\"]').val(dataSource[i].id);\n");
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("actions"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("details"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					back.append(" $('#table-event-modal"+id+" #event-"+propertyKey+"').val(dataSource[i]."+propertyKey+");\n");
					back.append("console.log('"+propertyKey+" : '+dataSource[i]."+propertyKey+");\n");
				}
				back.append(""
				+"		if (!(dataSource[i].flags=== undefined)) {"
				+ "			   $('#table-event-modal"+id+" #start-before-table').prop('checked', dataSource[i].flags.includes('sHO'));"
				+ "			   $('#table-event-modal"+id+" #end-continues-table').prop('checked', dataSource[i].flags.includes('eHO'));"
				+ "			   $('#table-event-modal"+id+" #episodeStart').prop('disabled', dataSource[i].flags.includes('sHO'));"
				+ "			   $('#table-event-modal"+id+" #episodeEnd').prop('disabled', dataSource[i].flags.includes('eHO'));"
				+"		}"
				+ "			console.log('datasource['+i+']');"
				+ "			console.log(dataSource[i]);"
				+ "			var startDate = new Date(dataSource[i].startDate);\n"
				+ "			startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');\n"
				+"			console.log('startDate');console.log(startDate);"
				+ "			$('#table-event-modal"+id+" #monthpickerStartTable').datepicker({\n" 
				+ "				autoclose: true,\n"
				+ "				clearBtn: true,\n" 
				+ "				language: '"+language+"',\n"
				+ "				format: \"M yyyy\",\n" 
				+ "        		startView: \"months\", \n" 
				+ "       		minViewMode: \"months\",\n"
				+ "        		startDate: new Date(Number($('#table-event-modal"+id+" #monthpickerStartTable').attr('data-startdate').substring(0,4)),Number($('#table-event-modal"+id+" #monthpickerStartTable').attr('data-startdate').substring(5,7))-1,Number($('#table-event-modal"+id+" #monthpickerStartTable').attr('data-startdate').substring(8,10))),\n" 
				+ "        		endDate: new Date(Number($('#table-event-modal"+id+" #monthpickerStartTable').attr('data-enddate').substring(0,4)),Number($('#table-event-modal"+id+" #monthpickerStartTable').attr('data-enddate').substring(5,7))-1,Number($('#table-event-modal"+id+" #monthpickerStartTable').attr('data-enddate').substring(8,10)))\n"
				+ "	    	}).on(\"changeDate\", function(event) {"
				+ "	    		var cDate = new Date($(this).datepicker('getDate'));\n"
				+ "	    		cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() , 1).getDate());\n" 
				+ "				cDate.setHours(0);"
				+ "				cDate.setMinutes(0);"
				+ "				cDate.setSeconds(0);"
				+ "				$(this).attr(\"data-value\",cDate);"
				+ ""
				+ "	    		var cmonth = (cDate.getMonth()+1);\n" 
				+ "	    		var cmonthStr = \"\";\n" 
				+ "	    		if(cmonth < 10)cmonthStr += \"0\";\n" 
				+ "	    		cmonthStr += cmonth;\n" 
				+ "	    		var cDateStr = cmonthStr+\" \"+cDate.getFullYear();\n" 
				+ "				$('#table-event-modal"+id+" #monthpickerEndTable').datepicker({" 
				+ "					autoclose: true,\n"
				+ "					clearBtn: true,\n" 
				+ "					language: '"+language+"',\n"
				+ "					format: \"M yyyy\"," 
				+ "        			startView: \"months\"," 
				+ "       			minViewMode: \"months\","
				+ "					endDate: new Date(Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(0,4)),Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(5,7))-1,Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(8,10)))\n"
				+ "	    		});"
				+ "				if(cDateStr === 'NaN NaN' || cDateStr === 'NaN'){"
				+ "					console.log('invalid start '+cDateStr);"
				+ "					$('#table-event-modal"+id+" #monthpickerEndTable').datepicker('setStartDate',new Date(Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-startdate').substring(0,4)),Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-startdate').substring(5,7))-1,Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-startdate').substring(8,10))));"
				+ "				}"
				+ "				else{"
				+ "					$('#table-event-modal"+id+" #monthpickerEndTable').datepicker('setStartDate',cDate);"
				+ "				}" 
				+ "				$('#table-event-modal"+id+" #monthpickerEndTable').datepicker('update');"
				+ "			});"
				+"			$('#table-event-modal"+id+" #monthpickerStartTable').datepicker('setDate', startDate);"
				+ "	    	var cDate = startDate;\n"
				+ "	    	cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() , 1).getDate());\n" 
				+ "			cDate.setHours(0);"
				+ "			cDate.setMinutes(0);"
				+ "			cDate.setSeconds(0);"
				+ "			var cmonth = monthLabels"+id+"[''+(cDate.getMonth()+1)+''];"
				+ "	    	var cDateStr = cmonth+ \" \"+cDate.getFullYear();\n"
				+ "			var debugStr = $('#table-event-modal"+id+" #monthpickerStartTable input').val();"
				+"			if (dataSource[i].type==undefined) {"
				+"				$('#table-event-modal"+id+" #event-type').prop('selectedIndex', 0);"		
				+"			}"
				+"			if(debugStr == ''){"
				+ "				console.log('debug fix : '+cDateStr);"
				+"				if(cDate.getFullYear()!=1970) {"		
				+ "					$('#table-event-modal"+id+" #monthpickerStartTable input').val(cDateStr);"
				+"				}"
				+ "			}"
				+ "			$('#table-event-modal"+id+" #monthpickerStartTable input').attr(\"placeholder\",$('#table-event-modal"+id+" #monthpickerStartTable input').attr( 'data-placeholder' ));"
				+ "		  	var endDate = new Date(dataSource[i].endDate);\n"
				+ "			endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');\n"
				+ "			endDate.setHours(0);"
				+ "			endDate.setMinutes(0);"
				+ "			endDate.setSeconds(0);"
				+ "			$('#table-event-modal"+id+" #monthpickerEndTable').datepicker({\n" 
				+ "				autoclose: true,\n" 
				+ "				clearBtn: true,\n" 
				+ "				language: '"+language+"',\n"
				+ "				format: \"M yyyy\",\n" 
				+ "        		startView: \"months\", \n" 
				+ "       		minViewMode: \"months\",\n"
				+ "        		startDate: new Date(Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-startdate').substring(0,4)),Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-startdate').substring(5,7))-1,Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-startdate').substring(8,10))),\n" 
				+ "        		endDate: new Date(Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(0,4)),Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(5,7))-1,Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(8,10)))\n"
				+ "	    	}).on(\"changeDate\", function(event) {"
				+ "	    		var cDate = new Date($(this).datepicker('getDate'));\n" 
				+ "	    		cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() +1 , 0).getDate());\n" 
				+ "				cDate.setHours(0);"
				+ "				cDate.setMinutes(0);"
				+ "				cDate.setSeconds(0);"
				+ "				$(this).attr(\"data-value\",cDate);"
				+ ""
				+ "	    		var cmonth = (cDate.getMonth()+1);\n" 
				+ "	    		var cmonthStr = \"\";\n" 
				+ "	    		if(cmonth < 10)cmonthStr += \"0\";\n" 
				+ "	    		cmonthStr += cmonth;\n" 
				+ "	    		var cDateStr = cmonthStr+\" \"+cDate.getFullYear();\n" 
				+ "				if(cDateStr === 'NaN NaN' || cDateStr === 'NaN'){"
				+ "					$('#table-event-modal"+id+" #monthpickerStartTable').datepicker('setEndDate', new Date(Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(0,4)),Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(5,7))-1,Number($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-enddate').substring(8,10))));"
				+ "				}"
				+ "				else{"
				+ "					$('#table-event-modal"+id+" #monthpickerStartTable').datepicker('setEndDate',cDate);"
				+ "				}"
				+ "			});"
				+"			$('#table-event-modal"+id+" #monthpickerEndTable').datepicker('setDate', endDate);"
				+ "			$('#table-event-modal"+id+" #monthpickerEndTable input').attr(\"placeholder\",$('#table-event-modal"+id+" #monthpickerEndTable input').attr( 'data-placeholder' ));"
				+ "			break;\n"
				+ "		   }\n"
				+ "		}\n"
				+ "		if(!exists){\n"
				+ "		}\n"
				+ "		$('#table-event-modal"+id+"').modal();\n"
				+ "		}\n"
				+ "}\n"
				+ ""
				+ "$('#table-event-modal"+id+"').ready(function() {"
				+ "		var typeSelector = $('#table-event-modal"+id+" #event-type');"
				+ "		$('#table-event-modal"+id+"').on('show.bs.modal', function(){");
							for(final String propertyKey : properties) {
								if(propertyKey.contentEquals("link"))continue;
								if(propertyKey.contentEquals("actions"))continue;
								if(propertyKey.contentEquals("translation"))continue;
								if(propertyKey.contentEquals("details"))continue;
								if(propertyKey.contentEquals("settings"))continue;
								back.append("var descSelector = $('#table-event-modal"+id+" #event-desc-"+propertyKey+"');");
								back.append("var inputSelector = $('#table-event-modal"+id+" #event-"+propertyKey+"');");
								final JsonElement property = config.get(propertyKey);
								if (!property.isJsonObject())continue;
								JsonObject propertyObj = property.getAsJsonObject();
								if(propertyObj.has("description")) {
									back.append("var selectedType = $(typeSelector).val();\n");
									back.append("console.log('selected Type 1 : '+selectedType);\n");
									final JsonElement desc = propertyObj.get("description");
									if (!desc.isJsonObject())continue;
									JsonObject descObj = desc.getAsJsonObject();
									final Set<String> descSlots = descObj.keySet();
									for(final String descSlot : descSlots) {
										back.append("var internationalText = '"+descObj.get(descSlot).getAsJsonObject().get(language).getAsString()+"'\n");
										back.append("if('"+descSlot+"' == selectedType){$(descSelector).text(internationalText);$(descSelector).parent().removeClass('zofar_episodes_disabled');}\n");
										back.append("else{$(descSelector).text('');$(descSelector).parent().addClass('zofar_episodes_disabled');}\n");
									}
								}
							}
				back.append("	"
				+ "		});"
				+ "		$( typeSelector ).change(function( event ) {"
				+"				typeChangedFlagTable"+id+"=false;");
							for(final String propertyKey : properties) {
								if(propertyKey.contentEquals("link"))continue;
								if(propertyKey.contentEquals("actions"))continue;
								if(propertyKey.contentEquals("translation"))continue;
								if(propertyKey.contentEquals("details"))continue;
								if(propertyKey.contentEquals("settings"))continue;
								back.append("var descSelector = $('#table-event-modal"+id+" #event-desc-"+propertyKey+"');");
								back.append("var inputSelector = $('#table-event-modal"+id+" #event-"+propertyKey+"');");
								final JsonElement property = config.get(propertyKey);
								if (!property.isJsonObject())continue;
								JsonObject propertyObj = property.getAsJsonObject();
								if(propertyObj.has("description")) {
									back.append("var selectedType = $(typeSelector).val();\n");
									back.append("console.log('selected Type 2 : '+selectedType);\n");
									final JsonElement desc = propertyObj.get("description");
									if (!desc.isJsonObject())continue;
									JsonObject descObj = desc.getAsJsonObject();
									final Set<String> descSlots = descObj.keySet();
									back.append("$(descSelector).text('');$(descSelector).parent().addClass('zofar_episodes_disabled');\n");
									for(final String descSlot : descSlots) {
										back.append("var internationalText = '"+descObj.get(descSlot).getAsJsonObject().get(language).getAsString()+"'\n");
										back.append("if('"+descSlot+"' == selectedType){$(descSelector).text(internationalText);$(descSelector).parent().removeClass('zofar_episodes_disabled');}\n");
									}
								}
							}
				back.append("	"
				+ "		});"
				+ "});"
				+ "$('#table-event-modal"+id+" #table-dismiss-event"+id+"').click(function(event) {"
				+ "		clearAndHideWarnings"+id+"();"
				+ "});\n"
	+ "$('#table-event-modal"+id+" #new-table-save-event"+id+"').click(function(event) {"
	+"		var saveFlag=false;"	
	+"		clearAndHideWarnings"+id+"();"
	+" 		if($('#table-event-modal"+id+" #event-type').val()==='SlotDefault') {"
	+"			$('#table-event-modal"+id+" .modal-warning-type').removeClass('zofar_episodes_disabled');"
	+ "		}"
	+"		else if ( (($('#table-event-modal"+id+" #episodeStart').val()=='') || ($('#table-event-modal"+id+" #episodeEnd').val()=='')) && !$('#table-event-modal"+id+" #warning-table-range').prop('checked')) {"
	+"			clearAndHideWarnings"+id+"();"
	+"			$('#table-event-modal"+id+" .modal-warning-range').removeClass('zofar_episodes_disabled');"
	+ "		}"
	+" 		else if (typeValTable"+id+"!=$('#table-event-modal"+id+" #event-type').val() && (typeValTable"+id+"!='SlotDefault' && $('#table-event-modal"+id+" #event-type').val()!='SlotDefault')){"
	+"			clearAndHideWarnings"+id+"();"	
	+"			$('#table-event-modal"+id+" .modal-warning-typechange').removeClass('zofar_episodes_disabled');"
	+"			if (typeChangedFlagTable"+id+") saveFlag=true;"
	+"			typeChangedFlagTable"+id+"=true;"
	+ "		}"
	+ "		else {"
	+"			clearAndHideWarnings"+id+"();"
	+ "			saveFlag=true;"
	+ "		}"
	+ "		if (saveFlag) {"
	+ "			saveEpisodeTable(event);"
	+ "		}"
	+ "});\n\n"
	+"	function clearAndHideWarnings"+id+"() {"
	+"			$('#table-event-modal"+id+" .span-save-button').text('"+((UIEpisodesTable)component).getBundle().getString("saveLabel")+"');"
	+"			$('#table-event-modal"+id+" .modal-warning-typechange').addClass('zofar_episodes_disabled');"
	+"			$('#table-event-modal"+id+" .modal-warning-type').addClass('zofar_episodes_disabled');"
	+"			$('#table-event-modal"+id+" .modal-warning-range').addClass('zofar_episodes_disabled');"
	+ "}"
				+"	function saveEpisodeTable(event) {"
				+"		clearAndHideWarnings"+id+"();"
				+"		typeChangedFlagTable"+id+"=false;"
				+ "		var flag = true;\n"
				+ "		try{\n"
				+"			var startDate =new Date($('#table-event-modal"+id+" #monthpickerStartTable').attr('data-value'));"
				+ "			startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');\n"
				+ "		}catch(e){flag=false;alert('kein gültiges Startdatum! ');}\n"
				+ "		try{\n"
				+"			var endDate =new Date($('#table-event-modal"+id+" #monthpickerEndTable').attr('data-value'));"
				+ "			endDate.setDate(new Date(endDate.getFullYear(), endDate.getMonth() +1, 0).getDate());"
				+ "			endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');\n"
				+ "		}catch(e){flag=false;alert('kein gültiges Enddatum!');}\n"
				+ "		"
				+ "		if(flag){\n"
				+"			if ($('#start-before-table').prop('checked')) {list.push('sHO');}"
				+"			if ($('#end-continues-table').prop('checked')) {list.push('eHO');}"
				+ "			var event = {\n"
				+ "	  	   		id: $('#table-event-modal"+id+" #event-id').val(),\n") ;
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("actions"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("details"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					back.append(" "+propertyKey+": $('#table-event-modal"+id+" #event-"+propertyKey+"').val(),\n");
					if(property.getAsJsonObject().has("values") && property.getAsJsonObject().get("values").isJsonArray()) {
						final JsonArray options = property.getAsJsonObject().get("values").getAsJsonArray();
						boolean colorFlag = false;
						Iterator<JsonElement> it = options.iterator();
						while(it.hasNext()) {
							final JsonElement tmp = it.next();
							if(tmp.isJsonObject()) {
								if(tmp.getAsJsonObject().has("color"))colorFlag = true;
								if(colorFlag) break;
							}
						}
						if(colorFlag)back.append(" \""+propertyKey+"Color\" : $('#table-event-modal"+id+" #event-"+propertyKey+" option:selected').data('color'),\n");
					}
					else if(property.getAsJsonObject().has("color")) {
						back.append(" \""+propertyKey+"Color\" : $('#table-event-modal"+id+" #event-"+propertyKey+"').data('color'),\n");
					}
				}
				back.append("	"
				+ "        		flags: list,\n"
				+ "        		startDate: startDate,\n" 
				+ "	       		endDate: endDate\n"
				+ "	   		};\n"
				+ "		modifyEvent"+id+"(event);\n"
				+"		$('#table-event-modal"+id+"').modal('hide');"
				+ "	  }\n"
				+ "}"
				+ "function modifyEvent"+id+"(event) {\n"
				+ "		console.log('modify event');\n"
				+ "		console.log(event);\n"
				+ "		var dataSource = loadData"+id+"();\n"
				+ "		var exists = false;\n"
				+ "		var startDate = new Date(event.startDate);\n"
				+ "		startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');\n"
				+ "		console.log('startDate '+startDate);\n"
				+ " 	if(isNaN(event.startDate)){"
				+ "			startDate = minDate;"
				+ "		}"
				+ "		var endDate = new Date(event.endDate);\n"
				+ "		endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');\n"
				+ " 	if(isNaN(event.endDate)){"
				+ "			endDate = maxDate;"
				+ "		}"
				+ "		for(var i in dataSource) {\n"
				+ "		       if(dataSource[i].id == event.id) {\n"
				+ "				   exists = true;\n"
				+ "				   const oldColor = dataSource[i].typeColor;\n"
				+ "				   const oldType = dataSource[i].type;"
				+ "");
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("actions"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("details"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					back.append( " dataSource[i]."+propertyKey+" = event."+propertyKey+";\n");
					boolean colorFlag = false;
					if(property.getAsJsonObject().has("values") && property.getAsJsonObject().get("values").isJsonArray()) {
						final JsonArray options = property.getAsJsonObject().get("values").getAsJsonArray();
						Iterator<JsonElement> it = options.iterator();
						while(it.hasNext()) {
							final JsonElement tmp = it.next();
							if(tmp.isJsonObject()) {
								if(tmp.getAsJsonObject().has("color"))colorFlag = true;
								if(colorFlag) break;
							}
						}
					}
					else if(property.getAsJsonObject().has("color")) {
						colorFlag = true;
					}
					if(colorFlag) {
						back.append(" dataSource[i]."+propertyKey+"Color = event."+propertyKey+"Color;\n");
					}
				}
				back.append( "	"
				+ "				   dataSource[i].startDate = startDate;\n"
				+ "		           dataSource[i].endDate = endDate;\n"
				+ "		           dataSource[i].flags = list;"
				+ "				   if(dataSource[i].typeColor != oldColor)dataSource[i].color = null;\n"
				+"					if (dataSource[i].type!==oldType) {"
				+ "						 dataSource = typeEpisodeConversion"+id+"(dataSource,i,oldType);"
				+ "					}"
				+ "					console.log('dataSource x: ');"
				+ "					console.log(dataSource);"
				+ "		           break;\n"
				+ "		       }\n"
				+ "		}\n"
				+ "		if(!exists){\n"
				+ "			var nextId = -1;\n"
				+ "			if (dataSource.length==0) {\n"
				+ " 			nextId = Math.floor(Math.random() * (1000 - 501) ) + 501;\n"
				+ "			} else {\n"
				+ "				for(var tmp in dataSource) {\n"
				+ "					var id = dataSource[tmp].id;\n"
				+ "					nextId = Math.max(nextId,id);\n"
				+ "				}\n"
				+ "				nextId = nextId + 1;\n"
				+ "			}\n"
				+ " 		var newEvent = {\n"
				+ "				\"id\" : nextId,\n");
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("actions"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("details"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					back.append(" \""+propertyKey+"\": event."+propertyKey+",\n");
					boolean colorFlag = false;
					if(property.getAsJsonObject().has("values") && property.getAsJsonObject().get("values").isJsonArray()) {
						final JsonArray options = property.getAsJsonObject().get("values").getAsJsonArray();
						Iterator<JsonElement> it = options.iterator();
						while(it.hasNext()) {
							final JsonElement tmp = it.next();
							if(tmp.isJsonObject()) {
								if(tmp.getAsJsonObject().has("color"))colorFlag = true;
								if(colorFlag) break;
							}
						}
					}
					else if(property.getAsJsonObject().has("color")) {
						colorFlag = true;
					}
					if(colorFlag) {
						back.append(" \""+propertyKey+"Color\" : event."+propertyKey+"Color,\n");
					}
				}
				back.append(""
				+ "				\"startDate\": startDate,\n"
				+ "				\"endDate\" : endDate,\n"
				+ "				\"state\" : 'new',\n"
				+"				\"flags\" : list\n"
				+ "			};\n"
				+ ""
				+ "			dataSource.push(newEvent);\n"
				+ "		}\n"
				+ "		saveData"+id+"(dataSource);\n"
				+ "		update"+id+"();\n"
				+ "}\n\n"
				+ "function typeEpisodeConversion"+id+"(dataSource,index,oldType){"
				+ " var event = dataSource[index];"
				+ " console.log('typeEpisodeConversion');"
				+ " console.log('event  a');"
				+ " console.log(event);"
				+ "	const whitelist = ['id','name','type','typeColor','startDate','endDate','flags'];"
				+ " for (var key in event) {"
				+ "		if(whitelist.indexOf(key) < 0){"
				+ "			delete event[key];"
				+ "		}"		
				+ "	}"
				+ " event.state = 'new';"
				+ " if(typeof event.flags === 'undefined')event.flags = list;"
				+ "	if(!event.flags.includes('tyC'))event.flags.push('tyC');"
				+ " console.log('event  b');"
				+ " console.log(event);"
				+ "	dataSource[index] = event;"
				+ "		for(var i in dataSource) {\n"
				+ "		       if(dataSource[i].id != event.id) {"
				+ "				   if(!(typeof dataSource[i].childEpisodes === 'undefined')){"
				+ "						console.log('childEpisodes ');"
				+ "						console.log(dataSource[i].childEpisodes);"
				+ "				   		if(dataSource[i].childEpisodes.includes(event.id)){"
				+ "							dataSource[i].childEpisodes.splice(dataSource[i].childEpisodes.indexOf(event.id),1);"
				+ " 			   			if(typeof dataSource[i].flags === 'undefined')dataSource[i].flags = list;"
				+ "				   			if(!dataSource[i].flags.includes('cTC'))dataSource[i].flags.push('cTC');"
				+ "						}"
				+ "				   }"				
				+ "				   if(!(typeof dataSource[i].parentEpisode === 'undefined')){"
				+ "				   		if(dataSource[i].parentEpisode == event.id){"
				+ "							delete dataSource[i]['parentEpisode'];"
				+ " 			   			if(typeof dataSource[i].flags === 'undefined')dataSource[i].flags = list;"
				+ "				   			if(!dataSource[i].flags.includes('pTC'))dataSource[i].flags.push('pTC');"
				+ "				   		}"
				+ "					}"
				+ "		       }"
				+ "		}"
				+ " console.log('dataSource  c');"
				+ " console.log(dataSource);"
				+ "	return dataSource;"
				+ "}"
				+ "function delete"+id+"() {\n"
				+ "		var dataSource = loadData"+id+"();\n"
				+ "		var episodeId = event.target.getAttribute('episode-id');\n"
				+ "		let toDelete = confirm(\""+confirmDeleteStr+"\");\n" 
				+ "		if(toDelete){\n"
				+"			var blackList=[];\n"
				+ "			var blckObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":blckList'));\n"
				+ "			var blckVal = $(blckObj).val();\n"
				+ "			var blckContent = decodeURIComponent(blckVal);\n"
				+"			if(blckContent == ''){blackList= JSON.parse('[]');}\n"
				+ "			else {blackList= JSON.parse(blckContent);}\n"
				+ "			for(var i in dataSource) {\n"
				+ "				if(dataSource[i].id == episodeId) {\n"
				+ "					if (!blackList.includes(episodeId)) {\n"
				+ "						blackList.push(episodeId);\n"
				+ "						var encoded = encodeURIComponent(JSON.stringify(blackList,null,0));"
				+ "						var compressed = encoded;"
				+ "						$(blckObj).val(compressed);"
				+ "						$(blckObj).change();"
				+ "					}\n"	
				+ "		    		dataSource.splice(i, 1);\n"
				+ "		        	break;\n"
				+ "		      	}\n"
				+ "			}\n"
				+ "		}\n"
				+ "		for(var i in dataSource) {\n"
				+ "		       if(dataSource[i].id != episodeId) {"
				+ "				   if(!(typeof dataSource[i].childEpisodes === 'undefined')){"
				+ "				   		if(dataSource[i].childEpisodes.includes(episodeId)){"
				+ "							dataSource[i].childEpisodes.splice(dataSource[i].childEpisodes.indexOf(episodeId),1);"
				+ "						}"
				+ "				   }"				
				+ "				   if(!(typeof dataSource[i].parentEpisode === 'undefined')){"
				+ "				   		if(dataSource[i].parentEpisode == episodeId){"
				+ "							delete dataSource[i]['parentEpisode'];"
				+ "				   		}"
				+ "					}"
				+ "		       }"
				+ "		}"
				+ "		saveData"+id+"(dataSource);\n"
				+ "		update"+id+"();\n"
				+ "}\n\n"
				+ "function loadData"+id+"() {\n"
				+ "		var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));\n"
				+ "		var decompressed = $(fieldObj).val();\n"
				+ "		var fieldContent = decodeURIComponent(decompressed);\n"
				+ " 	var data= null;\n"
				+ "		if(fieldContent == ''){data= JSON.parse('[]');}\n"
				+ "		else { data= JSON.parse(fieldContent);}\n"
				+ "		var back = [];\n"
				+ "		for ( var i = 0; i < data.length; i++) {\n" 
				+ "			var obj = data[i];\n"
				+ "			var startDate = new Date(obj.startDate);\n"
				+ "			startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');\n"
				+ "			obj.startDate = startDate;\n"
				+ "			var endDate = new Date(obj.endDate);\n"
				+ "			endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');\n"
				+ "			obj.endDate = endDate;\n"
				+ "			back.push(obj);\n"
				+ "		}\n" 
				+ "		return back;\n"
				+ "}\n\n"
				+ ""
				+ "function saveData"+id+"(dataObj) {\n"
				+ "		for ( var i = 0; i < dataObj.length; i++) {\n" 
				+ "			var obj = dataObj[i];\n"
				+ "			var startDate = new Date(obj.startDate);\n"
				+ "			var endDate = new Date(obj.endDate);\n"
				+ "		}\n" 
				+ "		var data = JSON.stringify(dataObj,null,0);\n"
				+ "		var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));\n"
				+ "		var encoded = encodeURIComponent(data);\n"
				+ "		var compressed = encoded;\n"
				+ "		$(fieldObj).val(compressed);\n"
				+ "		$(fieldObj).change();\n"
				+ "}\n\n"
				+ ""
				+ "function update"+id+"() {\n"
				+ "		var content = $('#calendarTable"+fingerprint+" .table-episodes .row');"
				+ "		var container = $('#calendarTable"+fingerprint+" .table-episodes');"
				+ "		var dataSource = loadData"+id+"();\n"
				+ "		var stay = new Array();"				
				+ "		for(var episodeIndex in dataSource) {\n"
				+ "				var episode = dataSource[episodeIndex];"
				+ "				stay.push(episode.id);"
				+ "		}\n"
				+ "		clear"+id+"(content,stay);"
				+ "		var leftEpisodeIds = new Array();"
				+ "		$('.row.serverside[data-episode-id]',container).each(function(){"
				+ "			var episodeid = $(this).attr('data-episode-id');"
				+ "			leftEpisodeIds.push(Number(episodeid));"	
				+ "		});"
				+ ""
				+ "		for(var episodeIndex in dataSource) {\n"
				+ "			var episode = dataSource[episodeIndex];"
				+ "			if(!leftEpisodeIds.includes(episode.id)){"
				+ "				addToContainer"+id+"(container,episode);"
				+ "			}"
				+ "			else{"
				+ "				updateInContainer"+id+"(container,episode);"
				+ "			}"
				+ "		}\n"
				+ "}\n"
				+ ""
				+ "\n\n"
				+ "var config"+id+" = JSON.parse(\'"+config.getAsJsonObject()+"\');"
				+ "var types"+id+" = config"+id+".type.values;"
				+ "function labelOfType"+id+"(type){"
				+ "	for(var i = 0; i < types"+id+".length; i++) {\n" 
				+ "	    var obj = types"+id+"[i];\n" 
				+ "	    if(obj.id == type)return obj.label."+language+";\n" 
				+ "	}"
				+ "	return 'UNKNOWN';"
				+ "}"
				+ ""
				+ "var details"+id+" = config"+id+".details;"
				+ "function hasEpisodeDetail"+id+"(type,property){"
				+ "		if(details"+id+" === undefined)return false;\n"
				+ "		var typeDetails"+id+" = details"+id+"[type];"
				+ "		if(typeDetails"+id+" === undefined)return false;\n"
				+ "		for(var i = 0; i < typeDetails"+id+".length; i++) {\n" 
				+ "	    	var obj = typeDetails"+id+"[i];\n" 
				+ "	   		if(obj.property == property)return true;\n" 
				+ "		}"
				+ "		return false"
				+ "}"
				+ "var monthLabels"+id+" = config"+id+".translation."+language+".shortmonth;"
				+ ""
				+ ""
				+ "function replaceAll"+id+"(str, find, replace) {"
				+ "		if(str === undefined)return '';"
				+ "		if(find === undefined)return str;"
				+ "		if(replace === undefined)return str;"
				+ "  	return str.replace(find, replace);" 
				+ "}"
				+ ""
				+ "const QUOTE_CHARS"+id+" = new Map();" 
				+ "QUOTE_CHARS"+id+".set('\\'','\\#27');\n" 
				+ "QUOTE_CHARS"+id+".set('{','\\#7B');\n" 
				+ "QUOTE_CHARS"+id+".set('}','\\#7D');\n" 
				+ "QUOTE_CHARS"+id+".set('[','\\#5B');\n" 
				+ "QUOTE_CHARS"+id+".set(']','\\#5D');\n" 
				+ "QUOTE_CHARS"+id+".set(':','\\#3A');\n" 
				+ "QUOTE_CHARS"+id+".set(',','\\#2c');\n" 
				+ "QUOTE_CHARS"+id+".set('\\\\','\\#5C');"
				+ "function quoteCharJson"+id+"(str){"
				+ "		for (var [key, value] of QUOTE_CHARS"+id+".entries()) {"
				+ "			str = replaceAll"+id+"(str,key,value);"
				+ "		}"
				+ "		return str;"
				+ "}"
				+ ""
				+ "function unQuoteCharJson"+id+"(str){"
				+ "		for (var [key, value] of QUOTE_CHARS"+id+".entries()) {"
				+ "			str = replaceAll"+id+"(str,value,key);"
				+ "		}"
				+ "		return str;"
				+ "}"
				+ "function updateInContainer"+id+"(container,episode){\n"
				+ "		var nameField = $(\".row[data-episode-id='\"+episode.id+\"'] [data-episode-row='name']\",container);\n"
				+ "		var nameAria = $(\" [data-episode-cell='aria-label']\",nameField);\n"
				+ "		var nameValue = $(\" [data-episode-cell='value']\",nameField);\n"
				+ "		nameAria.text(nameAria.attr('data-episode-aria-prefix')+' '+unQuoteCharJson"+id+"(episode.name));\n"
				+ "		nameValue.text(unQuoteCharJson"+id+"(episode.name));\n"
				+ "		if(hasEpisodeDetail"+id+"(episode.type,'name')){"
				+ "			nameField.css(\"visibility\",\"\");"
				+ "			nameField.css(\"display\",\"\");"
				+ "		}else{"
				+ "			nameField.css(\"visibility\",\"hidden\");"
				+ "			nameField.css(\"display\",\"none\");"
				+ "		}"
				+ "		var typeField = $(\".row[data-episode-id='\"+episode.id+\"'] [data-episode-row='type']\",container);\n"
				+ "		var typeAria = $(\" [data-episode-cell='aria-label']\",typeField);\n"
				+ "		var typeValue = $(\" [data-episode-cell='value']\",typeField);\n"
				+ "		var labelOfType = labelOfType"+id+"(episode.type);"
				+ "		typeAria.text(typeAria.attr('data-episode-aria-prefix')+' '+unQuoteCharJson"+id+"(labelOfType));\n"
				+ "		typeValue.text(unQuoteCharJson"+id+"(labelOfType));\n"
				+ "		var colorField = $(\".row[data-episode-id='\"+episode.id+\"'] [data-episode-row='id'] [data-episode-cell='color']\",container);\n"
				+ "		colorField.attr('style','background:'+episode.color);\n"
				+ "		if(episode.color == null)colorField.attr('style','background:'+episode.typeColor);\n"
				+ "		var startDateField = $(\".row[data-episode-id='\"+episode.id+\"'] [data-episode-row='startDate']\",container);\n"
				+ "		var startDateAria = $(\" [data-episode-cell='aria-label']\",startDateField);\n"
				+ "		var startDateValue = $(\" [data-episode-cell='value']\",startDateField);\n"
				+ "		var startDate = new Date(episode.startDate);"
				+ " 	if((startDate.getMonth() == minDate.getMonth())&&(startDate.getFullYear() == minDate.getFullYear())){"
				+ "			var label = '"+unknownStartStr+"';"
				+ "			startDateAria.text(startDateAria.attr('data-episode-aria-prefix')+label);\n"
				+ "			startDateValue.text(label);\n"
				+ "		}"
				+ " 	else{"
				+ "			var prefix = '';"
				+ "			if(!(episode.flags === undefined)){"
				+ "				if(episode.flags.indexOf('sHO') > -1)prefix = '"+sHOLabelStr+" ';"
				+ "			}"
				+ "			var startMonthLabel = monthLabels"+id+"[''+(startDate.getMonth()+1)+''];"				
				+ "			startDateAria.text(startDateAria.attr('data-episode-aria-prefix')+' '+prefix+''+startMonthLabel+' '+startDate.getFullYear());\n"
				+ "			startDateValue.text(prefix+''+startMonthLabel+' '+startDate.getFullYear());\n"
				+ "		}"
				+ "		var endDateField = $(\".row[data-episode-id='\"+episode.id+\"'] [data-episode-row='endDate']\",container);\n"
				+ "		var endDateAria = $(\" [data-episode-cell='aria-label']\",endDateField);\n"
				+ "		var endDateValue = $(\" [data-episode-cell='value']\",endDateField);\n"
				+ "		var endDate = new Date(episode.endDate);"
				+ " 	if((endDate.getMonth() == maxDate.getMonth())&&(endDate.getFullYear() == maxDate.getFullYear())){"
				+ "			var label = '"+unknownEndStr+"';"
				+ "			endDateAria.text(endDateAria.attr('data-episode-aria-prefix')+' '+label);\n"
				+ "			endDateValue.text(''+label);\n"
				+ "		}"
				+ " 	else{"
				+ "			var prefix = '';"
				+ "			if(!(episode.flags === undefined)){"
				+ "				if(episode.flags.indexOf('eHO') > -1)prefix = '"+eHOLabelStr+" ';"
				+ "			}"
				+ "			var endMonthLabel = monthLabels"+id+"[''+(endDate.getMonth()+1)+''];"
				+ "			endDateAria.text(endDateAria.attr('data-episode-aria-prefix')+' '+prefix+''+endMonthLabel+' '+endDate.getFullYear());\n"
				+ "			endDateValue.text(''+prefix+''+endMonthLabel+' '+endDate.getFullYear());\n"
				+ "		}"
				+ "		\n"
				+ "}\n"
				+ ""
				+ "function addToContainer"+id+"(container,episode){\n"
				+ "		   var row = $('<div></div>');\n" 
				+ "		   row.attr('class', 'row');\n"
				+ "		   row.attr('id', '"+id+"_row_'+episode.id);\n"
				+ "		   var col1 = $('<div></div>');\n"
				+ "		   col1.attr('class', 'col-10');\n"
				+ "		   col1.attr('role', 'table');\n"
				+ "		   col1.attr('aria-label', 'Episode '+episode.id);\n"
				+ "		   col1.attr('aria-describedby', episode.id+'_desc');\n"
				+ ""
				+ "		   var desc = $('<div></div>');\n"
				+ "		   desc.attr('id', episode.id+'_desc');\n"
				+ "		   desc.attr('style', 'visibility:hidden;display:none');\n"
				+ "		   desc.text('Informationen zur Episode '+episode.id);\n"
				+ "		   $(col1).append(desc);\n"
				+ "		   var common_row = $('<div></div>');\n"
				+ "		   common_row.attr('role', 'row');\n"
				+ "		   common_row.attr('style', 'display:flex');\n"
				+ "		   var id_row = $('<div></div>');\n"
				+ "		   id_row.attr('role', 'row');\n"
				+ "		   id_row.attr('data-episode-row', 'id');\n"
				+ "		   var id_aria = $('<span></span>');\n"
				+ "		   id_aria.attr('id', episode.id+'_label1');\n"
				+ "		   id_aria.attr('role', 'cell');\n"
				+ "		   id_aria.attr('style', 'visibility:hidden;display:none');\n"
				+ "		   id_aria.attr('data-episode-cell', 'aria-label');\n"
				+ "		   id_aria.attr('data-episode-aria-prefix', 'ID');\n"
				+ "		   id_aria.text('ID '+episode.id);\n"
				+ "		   $(common_row).append(id_aria);\n"
				+ "		   var id_label = $('<span></span>');\n"
				+ "		   id_label.attr('class', 'col-id');\n"
				+ "		   id_label.attr('role', 'cell');\n"
				+ "		   id_label.attr('aria-labelledby', episode.id+'_label1');\n"
				+ "		   id_label.attr('data-episode-cell', 'label');\n"
				+ "		   var id_color = $('<span></span>');\n"
				+ "		   id_color.attr('data-episode-cell', 'color');\n"
				+ "		   id_color.attr('class', 'episode_color');\n"
				+ "		   id_color.attr('style', 'background:'+episode.typeColor);\n"
				+ "		   $(id_label).append(id_color);\n"
				+ "		   $(id_row).append(id_label);\n"
				+ "		   $(common_row).append(id_row);\n"
				+ "		   var type_row = $('<div></div>');\n"
				+ "		   type_row.attr('role', 'row');\n"
				+ "		   type_row.attr('data-episode-row', 'type');\n"
				+ "		   type_row.attr('style', 'margin-left: 10px;');\n"
				+ "		   var type_aria = $('<span></span>');\n"
				+ "		   type_aria.attr('id', episode.id+'_label3');\n"
				+ "		   type_aria.attr('role', 'cell');\n"
				+ "		   type_aria.attr('style', 'visibility:hidden;display:none');\n"
				+ "		   type_aria.attr('data-episode-cell', 'aria-label');\n"
				+ "		   type_aria.attr('data-episode-aria-prefix', 'Type');\n"
				+ "		   type_aria.text('Type '+unQuoteCharJson"+id+"(labelOfType"+id+"(episode.type)));\n"
				+ "		   $(type_row).append(type_aria);\n"
				+ "		   var type_label = $('<span></span>');\n"
				+ "		   type_label.attr('id', 'col_type');\n"
				+ "		   type_label.attr('role', 'cell');\n"
				+ "		   type_label.attr('aria-labelledby', episode.type+'_label3');\n"
				+ "		   type_label.attr('data-episode-cell', 'value');\n"
				+ "		   type_label.text(unQuoteCharJson"+id+"(labelOfType"+id+"(episode.type)));\n"
				+ "		   $(type_row).append(type_label);\n"
				+ "		   $(common_row).append(type_row);\n"
				+ "		   $(col1).append(common_row);\n"
				+ "		   var name_row = $('<div></div>');\n"
				+ "		   name_row.attr('role', 'row');\n"
				+ "		   name_row.attr('data-episode-row', 'name');\n"
				+ "		   var name_aria = $('<span></span>');\n"
				+ "		   name_aria.attr('id', episode.id+'_label2');\n"
				+ "		   name_aria.attr('role', 'cell');\n"
				+ "		   name_aria.attr('style', 'visibility:hidden;display:none');\n"
				+ "		   name_aria.attr('data-episode-cell', 'aria-label');\n"
				+ "		   name_aria.attr('data-episode-aria-prefix', 'Name');\n"
				+ "		   name_aria.text('Name '+unQuoteCharJson"+id+"(episode.name));\n"
				+ "		   $(name_row).append(name_aria);\n"
				+ "		   var name_label = $('<span></span>');\n"
				+ "		   name_label.attr('id', 'col_name');\n"
				+ "		   name_label.attr('role', 'cell');\n"
				+ "		   name_label.attr('aria-labelledby', episode.id+'_label2');\n"
				+ "		   name_label.attr('data-episode-cell', 'value');\n"
				+ "		   name_label.text(unQuoteCharJson"+id+"(episode.name));\n"
				+ "		   $(name_row).append(name_label);\n"
				+ "			if(hasEpisodeDetail"+id+"(episode.type,'name')){"
				+ "				name_row.css(\"visibility\",\"\");"
				+ "				name_row.css(\"display\",\"\");"
				+ "			}else{"
				+ "				name_row.css(\"visibility\",\"hidden\");"
				+ "				name_row.css(\"display\",\"none\");"
				+ "			}"
				+ "		   $(col1).append(name_row);\n"
				+ "		   var range_row = $('<div></div>');\n"
				+ "		   range_row.attr('role', 'row');\n"
				+ "		   range_row.attr('style', 'display:flex');\n"
				+ "		   var start_row = $('<div></div>');\n"
				+ "		   start_row.attr('data-episode-row', 'startDate');\n"
				+ "		   var start_aria = $('<span></span>');\n"
				+ "		   start_aria.attr('id', episode.id+'_label4');\n"
				+ "		   start_aria.attr('role', 'cell');\n"
				+ "		   start_aria.attr('style', 'visibility:hidden;display:none');\n"
				+ "		   start_aria.attr('data-episode-cell', 'aria-label');\n"
				+ "		   start_aria.attr('data-episode-aria-prefix', '"+startLabelStr+"');\n"
				+ "		   var startDate = new Date(episode.startDate);"
				+ " 	   if((startDate.getMonth() == minDate.getMonth())&&(startDate.getFullYear() == minDate.getFullYear())){"
				+ "				var label = '"+unknownStartStr+"';"
				+ "		   		start_aria.text(start_aria.attr('data-episode-aria-prefix')+' '+label);\n"
				+ "		   		$(start_row).append(start_aria);\n"
				+ "		   		var start_label = $('<span></span>');\n"
				+ "		   		start_label.attr('id', 'col_period');\n"
				+ "		   		start_label.attr('role', 'cell');\n"
				+ "		   		start_label.attr('aria-labelledby', episode.startDate+'_label4');\n"
				+ "		   		start_label.attr('data-episode-cell', 'value');\n"
				+ "		   		start_label.text(label);\n"
				+ "		   		$(start_row).append(start_label);\n"
				+ "		   		$(range_row).append(start_row);\n"
				+ "		   }"
				+ " 	   else{"
				+ "		   		var startMonth = startDate.getMonth() + 1;"
				+ "		   		var startMonthLabel = monthLabels"+id+"[''+(startDate.getMonth()+1)+''];"
				+ "				var prefix = '';"
				+ "				if(!(episode.flags === undefined)){"
				+ "					if(episode.flags.indexOf('sHO') > -1)prefix = '"+sHOLabelStr+" ';"
				+ "				}"
				+ "		   		start_aria.text(start_aria.attr('data-episode-aria-prefix')+' '+prefix+''+startMonth+' '+startDate.getFullYear());\n"
				+ "		   		$(start_row).append(start_aria);\n"
				+ "		   		var start_label = $('<span></span>');\n"
				+ "		   		start_label.attr('id', 'col_period');\n"
				+ "		   		start_label.attr('role', 'cell');\n"
				+ "		   		start_label.attr('aria-labelledby', episode.startDate+'_label4');\n"
				+ "		   		start_label.attr('data-episode-cell', 'value');\n"
				+ "		   		start_label.text(prefix+''+startMonthLabel+' '+startDate.getFullYear());\n"
				+ "		   		$(start_row).append(start_label);\n"
				+ "		   		$(range_row).append(start_row);\n"
				+ "		   }"
				+"				var dash_label = $('<span></span>');\n"
				+ "		   		dash_label.attr('class', 'zo-episode-dash');\n"
				+ "		   		dash_label.attr('style', 'margin-left: 5px;margin-right: 5px;');\n"
				+ "		   		dash_label.text('-');\n"
				+ "		   		$(range_row).append(dash_label);\n"
				+ "		   var end_row = $('<div></div>');\n"
				+ "		   end_row.attr('data-episode-row', 'endDate');\n"
				+ "		   var end_aria = $('<span></span>');\n"
				+ "		   end_aria.attr('id', episode.id+'_label5');\n"
				+ "		   end_aria.attr('role', 'cell');\n"
				+ "		   end_aria.attr('style', 'visibility:hidden;display:none');\n"
				+ "		   end_aria.attr('data-episode-cell', 'aria-label');\n"
				+ "		   end_aria.attr('data-episode-aria-prefix', '"+endLabelStr+"');\n"
				+ "		   var endDate = new Date(episode.endDate);"
				+ " 	   if((endDate.getMonth() == maxDate.getMonth())&&(endDate.getFullYear() == maxDate.getFullYear())){"
				+ "				var label = '"+unknownEndStr+"';"
				+ "		   		end_aria.text(end_aria.attr('data-episode-aria-prefix')+' '+label);\n"
				+ "		   		$(end_row).append(end_aria);\n"
				+ "		   		var end_label = $('<span></span>');\n"
				+ "		   		end_label.attr('id', 'col_period');\n"
				+ "		   		end_label.attr('role', 'cell');\n"
				+ "		   		end_label.attr('aria-labelledby', episode.endDate+'_label4');\n"
				+ "		   		end_label.attr('data-episode-cell', 'value');\n"
				+ "		   		end_label.text(''+label);\n"
				+ "		   		$(end_row).append(end_label);\n"
				+ "		   }"
				+ " 	   else{"
				+ "		   		var endMonth = endDate.getMonth() + 1;"
				+ "		   		var endMonthLabel = monthLabels"+id+"[''+(endDate.getMonth()+1)+''];"
				+ "				var prefix = '';"
				+ "				if(!(episode.flags === undefined)){"
				+ "					if(episode.flags.indexOf('eHO') > -1)prefix = '"+eHOLabelStr+" ';"
				+ "				}"
				+ "		   		end_aria.text(end_aria.attr('data-episode-aria-prefix')+' '+prefix+''+endMonth+' '+endDate.getFullYear());\n"
				+ "		   		$(end_row).append(end_aria);\n"
				+ "		   		var end_label = $('<span></span>');\n"
				+ "		   		end_label.attr('id', 'col_period');\n"
				+ "		   		end_label.attr('role', 'cell');\n"
				+ "		   		end_label.attr('aria-labelledby', episode.endDate+'_label4');\n"
				+ "		   		end_label.attr('data-episode-cell', 'value');\n"
				+ "		   		end_label.text(''+prefix+''+endMonthLabel+' '+endDate.getFullYear());\n"
				+ "		   		$(end_row).append(end_label);\n"
				+ "		   }"
				+ "		   $(range_row).append(end_row);\n"
				+ "		   $(col1).append(range_row);\n"
				+ "		   $(row).append(col1);\n"
				+ "		   var col2 = $('<div></div>');\n"
				+ "		   col2.attr('class', 'col-2');\n"
				+ "		   var editBtn = $('<img></img>');\n"
				+ "		   editBtn.attr('id', '"+id+"_'+episode.id+'_edit');\n"				
				+ "		   editBtn.attr('name', '"+id+"_'+episode.id+'_edit');\n"				
				+ "		   editBtn.attr('src', 'resources/html5/images/edit.svg');\n"			
				+ "		   editBtn.attr('class', 'svg-edit');\n"			
				+ "		   editBtn.attr('episode-action', 'edit');\n"			
				+ "		   editBtn.attr('episode-id', episode.id);\n"			
				+ "		   editBtn.attr('value', '');\n"
				+ "		   $(editBtn).click(edit"+id+");"
				+ "		   $(col2).append(editBtn);\n"
				+ "		   var deleteBtn = $('<img></img>');\n"
				+ "		   deleteBtn.attr('id', '"+id+"_'+episode.id+'_delete');\n"				
				+ "		   deleteBtn.attr('name', '"+id+"_'+episode.id+'_delete');\n"				
				+ "		   deleteBtn.attr('src', 'resources/html5/images/trash.svg');\n"			
				+ "		   deleteBtn.attr('class', 'svg-delete');\n"			
				+ "		   deleteBtn.attr('episode-action', 'delete');\n"			
				+ "		   deleteBtn.attr('episode-id', episode.id);\n"			
				+ "		   deleteBtn.attr('value', '');\n"
				+ "		   $(deleteBtn).click(delete"+id+");"
				+ "		   $(col2).append(deleteBtn);\n"
				+ "		   $(row).append(col2);\n"
				+ "   	   $(container).append(row);"
				+ ""
				+ "}\n\n"
				+ ""
				+ "function clear"+id+"(content,stay){"
				+ "		$(content).each(function(){"
				+ "			var episodeid = $(this).attr('data-episode-id');"
				+ "			if(!stay.includes(Number(episodeid))){"
				+ "				$(this).remove();"
				+ "			}"
				+ "		});"
				+ "}"
				+ "$('#start-before-table').change(function() {"
				+" 	if ($('#start-before-table').prop('checked')) {"
				+" 		$('#table-event-modal"+id+" #monthpickerStartTable').datepicker('setDate',new Date("+minYear+","+(minMonth-1)+",01));"
				+" 		$( '#table-event-modal"+id+" #episodeStart').prop( 'disabled', true );"
				+"	} else {$( '#table-event-modal"+id+" #episodeStart').prop( 'disabled', false );}"
				+"});"
				+ "$('#end-continues-table').change(function() {"
				+" if ($('#end-continues-table').prop('checked')) {"
				+" 		$('#table-event-modal"+id+" #monthpickerEndTable').datepicker('setDate',new Date("+maxYear+","+maxMonth+",0));"				
				+" 		$( '#table-event-modal"+id+" #episodeEnd').prop( 'disabled', true );"
				+"	} else {$( '#table-event-modal"+id+" #episodeEnd').prop( 'disabled', false);}"
				+"});"
				+ "");
		return back.toString();
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIEpisodesTable episodes = (UIEpisodesTable)component;
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("article");
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", null);
		final String fingerprint = episodes.getClientId(context).replace(':', '_');
		writer.write(initScript(context,episodes,"calendarTable"+fingerprint,episodes.getLanguage()));
		writer.endElement("script");
	}
	public synchronized String getMonthLabelFromJson(final String stamp) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date parse = sdf.parse(stamp);
            Calendar c = Calendar.getInstance();
            c.setTime(parse);
            return ""+new SimpleDateFormat("MMM").format(c.getTime());
		} catch (Exception e) {
			LOGGER.error("getMonthFromStamp failed ", e);
		}
		return "";
	}
	public synchronized int getYearFromJson(final String stamp) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date parse = sdf.parse(stamp);
            Calendar c = Calendar.getInstance();
            c.setTime(parse);
            return c.get(Calendar.YEAR);
		} catch (Exception e) {
			LOGGER.error("getMonthFromStamp failed ", e);
		}
		return -1;
	}
}
