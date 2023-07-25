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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
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
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIEpisodes.COMPONENT_FAMILY, rendererType = EpisodesRenderer.RENDERER_TYPE)
public class EpisodesRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EpisodesRenderer.class);
	public static final String RENDERER_TYPE = "de.his.zofar.Calendar.episodes";
	public EpisodesRenderer() {
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
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("article", component);
		writer.writeAttribute("id", component.getClientId(context) + "_main", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-orientation form-responsive bootstrap-fs-modal", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "episodes", null);
		final String fingerprint = component.getClientId(context).replace(':', '_');
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		writer.startElement("div", component);
		writer.writeAttribute("id", "calendar"+fingerprint, null);
		writer.writeAttribute("class", "calendar"+fingerprint+" calendar", null);
		writer.endElement("div");
		writer.endElement("div");
		final UIEpisodes episodes = (UIEpisodes)component;
		addModal(context,component,"calendar"+fingerprint,episodes.getLanguage());
	}
	private JsonObject getConfig(final UIComponent component) {
		if(component == null) return null;
		final UIEpisodes episodes = (UIEpisodes)component;
		if(episodes.getConfiguration() == null)return null;
		if(episodes.getConfiguration().contentEquals(""))return null;
		final JsonParser jsonParser = new JsonParser();
		final JsonElement tmp = jsonParser.parse(episodes.getConfiguration());
		if(tmp.isJsonObject())return (JsonObject)tmp;
		return null;
	}
	private JsonObject getDisabled(final UIComponent component) {
		if(component == null) return null;
		final UIEpisodes episodes = (UIEpisodes)component;
		if(episodes.getDisabled() == null)return null;
		if(episodes.getDisabled().contentEquals(""))return null;
		final JsonParser jsonParser = new JsonParser();
		final JsonElement tmp = jsonParser.parse(episodes.getDisabled());
		if(tmp.isJsonObject())return (JsonObject)tmp;
		return null;
	}
	private String getFocus(final UIComponent component) {
		if(component == null) return "()";
		final UIEpisodes episodes = (UIEpisodes)component;
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
	private void addModal(FacesContext context, UIComponent component,final String id,final String language) throws IOException {
		final UIEpisodes episode = (UIEpisodes)component;
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
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", "event-modal"+id, null);
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
							if(propertyKey.contentEquals("translation"))continue;
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
								writer.write("start description");
							writer.endElement("span");
						writer.endElement("div");
						writer.startElement("div", component);
						writer.writeAttribute("class", "form-group", null);
							writer.startElement("div", component);
							writer.writeAttribute("id", "monthpickerStart", null);
							writer.writeAttribute("class", "graphCal input-append date monthpickerCustom", null);
							writer.writeAttribute("data-value", "0", null);
							writer.writeAttribute("data-startDate", minYear+","+minMonth+",01", null);
							writer.writeAttribute("data-endDate", maxYear+","+maxMonth+","+(YearMonth.of(maxYear, Integer.parseInt(maxMonth))).lengthOfMonth(), null);
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
							writer.writeAttribute("class", "col-sm-12 control-label start-before-label", null);	
							writer.writeAttribute("for", "start-before", null);	
							writer.startElement("input", component);
								writer.writeAttribute("type", "checkbox", null);
								writer.writeAttribute("class", "start-before", null);
								writer.writeAttribute("name", "start-before", null);
								writer.writeAttribute("id", "start-before", null);
								writer.writeAttribute("checked", "false", null);
								writer.writeAttribute("value", "", null);
							writer.endElement("input");
							writer.write(" "+sHOLabelStr+" "+minMonthLabelStr+" "+minYear);
							writer.endElement("label");
							/*Check Box END*/
							writer.startElement("div", component);
							writer.writeAttribute("class", "zo-modal-end-description zofar_episodes_disabled", null);
								writer.startElement("span", component);
									writer.write("end description");
								writer.endElement("span");
							writer.endElement("div");
							writer.startElement("div", component);
							writer.writeAttribute("id", "monthpickerEnd", null);
							writer.writeAttribute("class", "graphCal input-append date monthpickerCustom", null);
							writer.writeAttribute("data-value", "0", null);
							writer.writeAttribute("data-startDate", minYear+","+minMonth+",01", null);
							writer.writeAttribute("data-endDate", maxYear+","+maxMonth+","+(YearMonth.of(maxYear, Integer.parseInt(maxMonth))).lengthOfMonth(), null);
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
						writer.endElement("div");
						/*Check Box START*/
						writer.startElement("label", component);
						writer.writeAttribute("class", "col-sm-12 control-label end-continues-label", null);	
						writer.writeAttribute("for", "end-continues", null);	
						writer.startElement("input", component);
							writer.writeAttribute("type", "checkbox", null);
							writer.writeAttribute("class", "end-continues", null);
							writer.writeAttribute("name", "end-continues", null);
							writer.writeAttribute("id", "end-continues", null);
							writer.writeAttribute("value", "", null);
							writer.writeAttribute("checked", "false", null);
						writer.endElement("input");
						writer.write(" "+eHOLabelStr+" "+maxMonthLabelStr+" "+maxYear);
						writer.endElement("label");
						/*Check Box END*/
						writer.startElement("div", component);
						writer.writeAttribute("class", "modal-warning zofar_episodes_disabled", null);
						writer.write("''");
						writer.endElement("div");
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-footer", null);
						writer.startElement("button", component);
						writer.writeAttribute("type", "button", null);
						writer.writeAttribute("class", "btn btn-default", null);
						writer.writeAttribute("data-dismiss", "modal", null);
						writer.writeAttribute("id", "cancel-event"+id, null);
						writer.write(cancelLabelStr);
						writer.endElement("button");
						writer.startElement("button", component);
						writer.writeAttribute("type", "button", null);
						writer.writeAttribute("class", "btn btn-primary", null);
						writer.writeAttribute("id", "graph-save-event"+id, null);
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
		final JsonObject config = this.getConfig(component);
		final Set<String> properties = config.keySet();
		final UIEpisodes episodes = (UIEpisodes)component;
		final String updateLabelStr = episodes.getBundle().getString("updateLabel");
		final String deleteLabelStr = episodes.getBundle().getString("deleteLable");
		String script = "var calendar"+id+";"
				+"var typeChangedFlag"+id+"=false;"
				+"var typeVal"+id+"='';"
				+ "var minRange = new Date("+episodes.getMinYear()+",0,1,0,0,0);		"
				+ "var maxRange = new Date("+episodes.getMaxYear()+",11,31,23,59,0);		"
				+ "$(document).ready(function(){"
				+ "	var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));"
				+ "	fieldObj.attr('data-episode-link','"+this.getConfig(component).get("link").getAsString()+"');"
				+ "	fieldObj.on(\"change\",function() {\n" 
				+ "		$(\"input[data-episode-link='"+this.getConfig(component).get("link").getAsString()+"']\").each(function(){"
				+ "		var cfield = $(this);"
				+ "		var ref =fieldObj;"
				+ "		if(cfield.attr('id') != ref.attr('id')){"
				+ "			cfield.val(ref.val());"
				+ "			cfield.trigger('update',[ref]);"
				+ "		}"
				+ "	});"
				+ "});"
				+ "	var fieldObjBlacklist = $('#'+$.escapeSelector('"+component.getClientId(context)+":blckList'));"
				+ "	fieldObjBlacklist.attr('data-list-link','"+this.getConfig(component).get("link").getAsString()+"');"
				+ "	fieldObjBlacklist.on(\"change\",function() {\n" 
				+ "		$(\"input[data-list-link='"+this.getConfig(component).get("link").getAsString()+"']\").each(function(){"
				+ "		var cfield = $(this);"
				+ "		var ref =fieldObjBlacklist;"
				+ "		if(cfield.attr('id') != ref.attr('id')){"
				+ "			cfield.val(ref.val());"
				+ "			cfield.trigger('update',[ref]);"
				+ "		}"
				+ "	});"
				+ "});"
				+"$('#event-modal"+id+"').keydown(function(e) {"
				+"	if (e.keyCode == 13) {"
				+"		if(event.target.id=='cancel-event"+id+"') {"
				+"			$('#event-modal"+id+"').modal('toggle'); "	
				+"		} else {"
				+"			$('#graph-save-event"+id+"').click();"
				+"		}"
				+"		e.preventDefault();"
				+"		return false;"
				+"	}"
				+"});"
				+"	var list = [];"
				+ "	fieldObj.on(\"update\",function() {\n" 
				+ "		update"+id+"();" 
				+ "	});"
				+ "function editEvent"+id+"(event) {"
				+ "		var dataSource = calendar"+id+"._dataSource;"
				+ "		var exists = false;";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script+"	list = [];";
					script = script + "clearAndHideWarnings"+id+"();";
					script = script+ "$('#event-modal"+id+"').modal({";
					script = script+ "backdrop: 'static'";
					script = script+ "});";
					script = script + "$('#event-modal"+id+" #event-"+propertyKey+"').val('');"; 
					script = script +"		$( '#event-modal"+id+" #episodeStart').prop( 'disabled', false );";
					script = script +"		$( '#event-modal"+id+" #episodeEnd').prop( 'disabled', false);";
					script = script + "			   $('#event-modal"+id+" #start-before').prop('checked',false);";
					script = script + "			   $('#event-modal"+id+" #end-continues').prop('checked',false);";
				}
				script = script+ ""
				+ "		if(event.event){"
				+ "		for(var i in dataSource) {"
				+ "		   if(dataSource[i].id == event.event.id) {"
				+ "			   exists = true;"	
				+ "			   $('#event-modal"+id+" input[name=\"event-id\"]').val(dataSource[i].id);";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " $('#event-modal"+id+" #event-"+propertyKey+"').val(dataSource[i]."+propertyKey+");"
							+ "console.log('"+propertyKey+" : '+dataSource[i]."+propertyKey+");";
				}
				script = script + "	"
				+ "			   $('#event-modal"+id+" #start-before').prop('checked', dataSource[i].flags.includes('sHO'));"
				+ "			   $('#event-modal"+id+" #end-continues').prop('checked', dataSource[i].flags.includes('eHO'));"
				+"			   $( '#event-modal"+id+" #episodeStart').prop( 'disabled', dataSource[i].flags.includes('sHO'));"
				+"			   $( '#event-modal"+id+" #episodeEnd').prop( 'disabled', dataSource[i].flags.includes('eHO') );"
				+ "			var startDate = new Date(dataSource[i].startDate);"
				+ "			startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "			$('#event-modal"+id+" #monthpickerStart').datepicker({\n" 
				+ "				autoclose: true,\n" 
				+ "				language: '"+episodes.getLanguage()+"',\n"
				+ "				format: \"M yyyy\",\n" 
				+ "        		startView: \"months\", \n" 
				+ "       		minViewMode: \"months\",\n"
				+ "        		startDate: new Date($('#event-modal"+id+" #monthpickerStart').attr('data-startdate').substring(0,4),$('#event-modal"+id+" #monthpickerStart').attr('data-startdate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerStart').attr('data-startdate').substring(8,10)),\n" 
				+ "        		endDate: new Date($('#event-modal"+id+" #monthpickerStart').attr('data-enddate').substring(0,4),$('#event-modal"+id+" #monthpickerStart').attr('data-enddate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerStart').attr('data-enddate').substring(8,10))\n" 
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
				+ "				$('#event-modal"+id+" #monthpickerEnd').datepicker({" 
				+ "					autoclose: true,\n" 
				+ "					language: '"+episodes.getLanguage()+"',\n"
				+ "					format: \"M yyyy\"," 
				+ "        			startView: \"months\"," 
				+ "       			minViewMode: \"months\","
				+ "        			endDate: new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(0,4),$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(8,10))\n" 
				+ "	    		});"
				+ "				$('#event-modal"+id+" #monthpickerEnd').datepicker('setStartDate',cDateStr);"
				+ "	    		if ((typeof $(\"#event-modal"+id+" #monthpickerStart\").datepicker('getDate') !== 'undefined' && moment($(\"#event-modal"+id+" #monthpickerStart\").datepicker('getDate')).isValid()) 	&& 	(typeof $(\"#event-modal"+id+" #monthpickerEnd\").datepicker('getDate') !== 'undefined' &&moment($(\"#event-modal"+id+" #monthpickerEnd\").datepicker('getDate')).isValid())) \n" 
				+ "	    		{\n" 
				+ "	    		}"
				+ "				else{"
				+ "	  		  			$(\"#event-modal"+id+" #monthpickerEnd\").datepicker('setDate', cDateStr);\n" 
				+ "				}"
				+ "				$('#event-modal"+id+" #monthpickerEnd').datepicker('update');"
				+ "			});"
				+"			$('#event-modal"+id+" #monthpickerStart').datepicker('setDate', startDate);"
				+ "			$('#event-modal"+id+" #monthpickerStart input').attr(\"placeholder\",$('#event-modal"+id+" #monthpickerStart input').attr( 'data-placeholder' ));"
				+ "			var endDate = new Date(dataSource[i].endDate);"
				+ "			endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "			endDate.setHours(0);"
				+ "			endDate.setMinutes(0);"
				+ "			endDate.setSeconds(0);"
				+ "			$('#event-modal"+id+" #monthpickerEnd').datepicker({\n" 
				+ "				autoclose: true,\n" 
				+ "				language: '"+episodes.getLanguage()+"',\n"
				+ "				format: \"M yyyy\",\n" 
				+ "        		startView: \"months\", \n" 
				+ "       		minViewMode: \"months\",\n"
				+ "        		startDate: new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-startdate').substring(0,4),$('#event-modal"+id+" #monthpickerEnd').attr('data-startdate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerEnd').attr('data-startdate').substring(8,10)),\n" 
				+ "        		endDate: new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(0,4),$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(8,10))\n"
				+ "	    	}).on(\"changeDate\", function(event) {"
				+ "	    		var cDate = new Date($(this).datepicker('getDate'));\n" 
				+ "	    		cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() +1 , 0).getDate());\n" 
				+ "				$(this).attr(\"data-value\",cDate);"				
				+ "			});"
				+"			$('#event-modal"+id+" #monthpickerEnd').datepicker('setDate', endDate);"
				+ "			$('#event-modal"+id+" #monthpickerEnd input').attr(\"placeholder\",$('#event-modal"+id+" #monthpickerEnd input').attr( 'data-placeholder' ));"
				+ "		    break;"
				+ "		   }"
				+ "		}"
				+ "		} "
				+ "		if(!exists){"
				+ "			   $('#event-modal"+id+" #event-id').val(event.id);";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " $('#event-modal"+id+" #event-"+propertyKey+"').val(event."+propertyKey+");";
				}
				script = script + ""
				+ "		var startDate = new Date(event.startDate);"
				+ "		startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "			$('#event-modal"+id+" #monthpickerStart').datepicker({\n" 
				+ "				autoclose: true,\n" 
				+ "				language: '"+episodes.getLanguage()+"',\n"
				+ "				format: \"M yyyy\",\n" 
				+ "        		startView: \"months\", \n" 
				+ "       		minViewMode: \"months\",\n"
				+ "        		startDate: new Date($('#event-modal"+id+" #monthpickerStart').attr('data-startdate').substring(0,4),$('#event-modal"+id+" #monthpickerStart').attr('data-startdate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerStart').attr('data-startdate').substring(8,10)),\n" 
				+ "        		endDate: new Date($('#event-modal"+id+" #monthpickerStart').attr('data-enddate').substring(0,4),$('#event-modal"+id+" #monthpickerStart').attr('data-enddate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerStart').attr('data-enddate').substring(8,10))\n"
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
				+ "				$('#event-modal"+id+" #monthpickerEnd').datepicker({" 
				+ "					autoclose: true,\n" 
				+ "					language: '"+episodes.getLanguage()+"',\n"
				+ "					format: \"M yyyy\"," 
				+ "        			startView: \"months\"," 
				+ "       			minViewMode: \"months\","
				+ "        			endDate: new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(0,4),$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(8,10))\n"
				+ "	    		});"
				+ "				$('#event-modal"+id+" #monthpickerEnd').datepicker('setStartDate',cDateStr);"
				+ "	    		if ((typeof $(\"#event-modal"+id+" #monthpickerStart\").datepicker('getDate') !== 'undefined' && moment($(\"#event-modal"+id+" #monthpickerStart\").datepicker('getDate')).isValid()) 	&& 	(typeof $(\"#event-modal"+id+" #monthpickerEnd\").datepicker('getDate') !== 'undefined' &&moment($(\"#event-modal"+id+" #monthpickerEnd\").datepicker('getDate')).isValid())) \n" 
				+ "	    		{\n" 
				+ "	    		}"
				+ "				else{"
				+ "	  		  			$(\"#event-modal"+id+" #monthpickerEnd\").datepicker('setDate', cDateStr);\n" 
				+ "				}"
				+ "				$('#event-modal"+id+" #monthpickerEnd').datepicker('update');"
				+ "			});"
				+"			$('#event-modal"+id+" #monthpickerStart').datepicker('setDate', startDate);"
				+ "			$('#event-modal"+id+" #monthpickerStart input').attr(\"placeholder\",$('#event-modal"+id+" #monthpickerStart input').attr( 'data-placeholder' ));"
			+ "			var endDate = new Date(event.endDate);"
			+ "			endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
			+ "			endDate.setHours(0);"
			+ "			endDate.setMinutes(0);"
			+ "			endDate.setSeconds(0);"
			+ "			$('#event-modal"+id+" #monthpickerEnd').datepicker({\n" 
			+ "				autoclose: true,\n" 
			+ "				language: '"+episodes.getLanguage()+"',\n"
			+ "				format: \"M yyyy\",\n" 
			+ "        		startView: \"months\", \n" 
			+ "       		minViewMode: \"months\",\n"
			+ "        		startDate: new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-startdate').substring(0,4),$('#event-modal"+id+" #monthpickerEnd').attr('data-startdate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerEnd').attr('data-startdate').substring(8,10)),\n" 
			+ "        		endDate: new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(0,4),$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(5,7)-1,$('#event-modal"+id+" #monthpickerEnd').attr('data-enddate').substring(8,10))\n" 
			+ "	    	}).on(\"changeDate\", function(event) {"
			+ "	    		var cDate = new Date($(this).datepicker('getDate'));\n" 
			+ "	    		cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() +1 , 0).getDate());\n" 
			+ "				$(this).attr(\"data-value\",cDate);"				
			+ "			});"
			+"			$('#event-modal"+id+" #monthpickerEnd').datepicker('setDate', endDate);"
			+ "			$('#event-modal"+id+" #monthpickerEnd input').attr(\"placeholder\",$('#event-modal"+id+" #monthpickerEnd input').attr( 'data-placeholder' ));"
			+ "		}"
			+ "		$('#event-modal"+id+"').modal();"
			+ "}"
				+ "$('#event-modal"+id+"').ready(function() {"
				+ "		var typeSelector = $('#event-modal"+id+" #event-type');"
				+ "		$('#event-modal"+id+"').on('show.bs.modal', function(){"
				+"			typeVal"+id+" = $(typeSelector).val();";
							for(final String propertyKey : properties) {
								if(propertyKey.contentEquals("link"))continue;
								if(propertyKey.contentEquals("actions"))continue;
								if(propertyKey.contentEquals("translation"))continue;
								if(propertyKey.contentEquals("details"))continue;
								if(propertyKey.contentEquals("settings"))continue;
								script = script+ "var descSelector = $('#event-modal"+id+" #event-desc-"+propertyKey+"');";
								script = script+ "var inputSelector = $('#event-modal"+id+" #event-"+propertyKey+"');";
								final JsonElement property = config.get(propertyKey);
								if (!property.isJsonObject())continue;
								JsonObject propertyObj = property.getAsJsonObject();
								if(propertyObj.has("description")) {
									script = script+ "var selectedType = $(typeSelector).val();\n";
									script = script+ "typeVal"+id+" = $(typeSelector).val();\n";
									script = script+ "console.log('selected Type 1 : '+selectedType);\n";
									final JsonElement desc = propertyObj.get("description");
									if (!desc.isJsonObject())continue;
									JsonObject descObj = desc.getAsJsonObject();
									final Set<String> descSlots = descObj.keySet();
									for(final String descSlot : descSlots) {
										script = script+ "var internationalText = '"+descObj.get(descSlot).getAsJsonObject().get(language).getAsString()+"'\n";
										script = script+ "if('"+descSlot+"' == selectedType){$(descSelector).text(internationalText);$(descSelector).parent().removeClass('zofar_episodes_disabled');}\n";
										script = script+ "else{$(descSelector).text('');$(descSelector).parent().addClass('zofar_episodes_disabled');}\n";
									}
								}
							}
				script = script+ "	"
				+ "		});"
				+ "		$( typeSelector ).change(function( event ) {"
				+"			typeChangedFlag"+id+"=false;"
				+ "";
					for(final String propertyKey : properties) {
						if(propertyKey.contentEquals("link"))continue;
						if(propertyKey.contentEquals("actions"))continue;
						if(propertyKey.contentEquals("translation"))continue;
						if(propertyKey.contentEquals("details"))continue;
						if(propertyKey.contentEquals("settings"))continue;
						script = script+ "var descSelector = $('#event-modal"+id+" #event-desc-"+propertyKey+"');";
						script = script+ "var inputSelector = $('#event-modal"+id+" #event-"+propertyKey+"');";
						final JsonElement property = config.get(propertyKey);
						if (!property.isJsonObject())continue;
						JsonObject propertyObj = property.getAsJsonObject();
						if(propertyObj.has("description")) {
							script = script+ "var selectedType = $(typeSelector).val();\n";
							script = script+ "console.log('selected Type 2 : '+selectedType);\n";
							final JsonElement desc = propertyObj.get("description");
							if (!desc.isJsonObject())continue;
							JsonObject descObj = desc.getAsJsonObject();
							final Set<String> descSlots = descObj.keySet();
							script = script+ "$(descSelector).text('');$(descSelector).parent().addClass('zofar_episodes_disabled');";
							for(final String descSlot : descSlots) {
								script = script+ "var internationalText = '"+descObj.get(descSlot).getAsJsonObject().get(language).getAsString()+"'\n";
								script = script+ "if('"+descSlot+"' === selectedType){"
										+ "$(descSelector).text(internationalText);"
										+ "$(descSelector).parent().removeClass('zofar_episodes_disabled');}\n";
							}
						}
					}
				script = script+ "	"
				+ "		});"
				+ "});"
				+ "$('#event-modal"+id+" #cancel-event"+id+"').click(function(event) {"
				+"				clearAndHideWarnings"+id+"();"
				+ "});\n\n"
				+ "$('#event-modal"+id+" #graph-save-event"+id+"').click(function(event) {"
				+" 		if($('#event-modal"+id+" #event-type').val()==='SlotDefault') {"
				+"			clearAndHideWarnings"+id+"();"
				+"			$('#event-modal"+id+" .modal-warning').removeClass('zofar_episodes_disabled');"
				+"			$('#event-modal"+id+" .modal-warning').text('"+((UIEpisodes)component).getBundle().getString("warningNoType")+"');"
				+"			$('#event-modal"+id+" .span-save-button').text('"+((UIEpisodes)component).getBundle().getString("buttonCkeckAgain")+"');"
				+ "		}else {"
				+ "			if (typeVal"+id+"!=$('#event-modal"+id+" #event-type').val() && typeVal"+id+"!='SlotDefault'){"
				+"				clearAndHideWarnings"+id+"();"
				+"				$('#event-modal"+id+" .modal-warning').removeClass('zofar_episodes_disabled');"
				+"				$('#event-modal"+id+" .modal-warning').text('"+((UIEpisodes)component).getBundle().getString("warningTypeChanged")+"');"
				+ "				if (typeChangedFlag"+id+") saveEpisode"+id+"(event);"
				+"				typeChangedFlag"+id+"=true;"
				+"			} else {"	
				+"				clearAndHideWarnings"+id+"();"
				+"				saveEpisode"+id+"(event);"
				+ "			}"	
				+ "		}"
				+ "});\n\n"
				+"	function clearAndHideWarnings"+id+"() {"
				+"		$('#event-modal"+id+" .modal-warning').text('');"
				+"		$('#event-modal"+id+" .modal-warning').addClass('zofar_episodes_disabled');"
				+"		$('#event-modal"+id+" .span-save-button').text('"+((UIEpisodes)component).getBundle().getString("saveLabel")+"');"
				+ "}"
				+"	function saveEpisode"+id+"(event) {"
				+"			clearAndHideWarnings"+id+"();"
				+"			typeChangedFlag"+id+"=false;"
				+ "			var flag = true;"
				+ "			try{"
				+"				var startDate =new Date($('#event-modal"+id+" #monthpickerStart').attr('data-value'));"
				+ " 			console.log('start date : '+startDate);"
				+ "				startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "			}"
				+ "			catch(e){	"
				+ "				flag=false;alert('kein gültiges Startdatum! ');"
				+ "			}"
				+ "			try{"
				+"				var endDate =new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-value'));"
				+ " 			console.log('end date : '+endDate);"
				+ "				endDate.setDate(new Date(endDate.getFullYear(), endDate.getMonth() +1, 0).getDate());"
				+ "				endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "			}"
				+ "			catch(e){"
				+ "				flag=false;alert('kein gültiges Enddatum!');"
				+ "				console.log(e);"
				+ "			}"
				+ "			if(flag){"
				+"				if ($('#start-before').prop('checked')) {list.push('sHO');}"
				+"				if ($('#end-continues').prop('checked')) {list.push('eHO');}"	
				+ "				var event = {"
				+ "	  	   			id: $('#event-modal"+id+" #event-id').val()," ;
									for(final String propertyKey : properties) {
										if(propertyKey.contentEquals("link"))continue;
										final JsonElement property = config.get(propertyKey);
										if (!property.isJsonObject())continue;
										script = script + " "+propertyKey+": $('#event-modal"+id+" #event-"+propertyKey+"').val(),";
										final JsonObject jsonObj = property.getAsJsonObject();
										JsonArray values = null;
										if(jsonObj.has("values")) {
											if(jsonObj.get("values").isJsonArray())values = jsonObj.get("values").getAsJsonArray();
										}
										if(values != null) {
											boolean colorFlag = false;
											final Iterator<JsonElement> it = values.iterator();
											while(it.hasNext()) {
												final JsonElement tmp = it.next();
												if(tmp.isJsonObject()) {
													if(tmp.getAsJsonObject().has("color"))colorFlag = true;
													if(colorFlag) break;
												}
											}
											if(colorFlag)script = script + " \""+propertyKey+"Color\" : $('#event-modal"+id+" #event-"+propertyKey+" option:selected').data('color'),";
										}
										else if(property.getAsJsonObject().has("color")) {
											script = script + " \""+propertyKey+"Color\" : $('#event-modal"+id+" #event-"+propertyKey+"').data('color'),";
										}
									}
				script = script+ "	"
				+ "        			startDate: startDate," 
				+ "	       			endDate: endDate"
				+ "	   			};"
				+ "    			var splittedEvents = splitEvent"+id+"(event);" 
				+ "				if(splittedEvents.length > 1)splittedEvents.forEach(splittedEvent => modifyEvent"+id+"(splittedEvent));"
				+ "				else modifyEvent"+id+"(event);"
				+ "				$('#event-modal"+id+"').modal('hide');"
				+ "	   		}"
				+ "}"
				+ ""
				+ "function splitEvent"+id+"(event) {"
				+ "		const disabled = JSON.parse('"+getDisabled(component)+"');"
				+ "		const timezone = \"Europe/Berlin\";"
				+ "		var back = new Array(0);"
				+ "		var startDate = new Date(event.startDate);"
				+ "		startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "		var endDate = new Date(event.endDate);"
				+ "		endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "		var lftEvent = event;"
				+ "		var lftDate = startDate;"
				+ "		var lftState = 1;"
				+ "		const origName = event.name;"
				+ "		while(lftDate.getTime() < endDate.getTime()) {"
				+ "     	var checkMoment = moment(lftDate).add(1 , 'month');"
				+ "     	checkDate = new Date(checkMoment.tz(timezone).format(\"YYYY-MM-DDT00:00:00Z\"));"
				+ "			var checkMonth = (checkDate.getMonth() + 1);"
				+ "			var checkYear = checkDate.getFullYear();"
				+ "			var checkState = 1;"
				+ "			var disabledYear = disabled[checkYear];"
				+ "			if(disabledYear != null){"
        		+ "  			if(disabledYear.includes(checkMonth)){"
        		+ "					checkState = 0;"
        		+ "				}"
        	  	+ "			}"
        	  	+ "			if(lftState != checkState){"
        	  	+ "				if(checkState == 0){"
        	  	+ "					const cloneEvent = JSON.parse(JSON.stringify(event));"
        	  	+ "					cloneEvent.name = origName;"
        	  	+ "					cloneEvent.endDate = lftDate;"
        	  	+ "					back.push(cloneEvent);"
        	  	+ "				}"
        	  	+ "				else if (checkState == 1){"
        	  	+ "					event.startDate = checkDate;"
        	  	+ "				}"
        	  	+ "				lftState = checkState;"
        	  	+ "			}"
				+ "     	lftDate = checkDate;"
				+ "		}"
        	  	+ "		event.name = origName;"
 				+ "		back.push(event);"
				+ "		return back;"
				+ "}"
				+ "function modifyEvent"+id+"(event) {"
				+ "		var dataSource = calendar"+id+"._dataSource;"
				+ "		var exists = false;"
				+ "		var startDate = new Date(event.startDate);"
				+ "		startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "		var endDate = new Date(event.endDate);"
				+ "		endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "		for(var i in dataSource) {"
				+ "		       if(dataSource[i].id == event.id) {"
				+ "				   exists = true;"
				+ "				   const oldColor = dataSource[i].typeColor;"
				+ "				   const oldType = dataSource[i].type;";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " dataSource[i]."+propertyKey+" = event."+propertyKey+";";
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
						script = script + " dataSource[i]."+propertyKey+"Color = event."+propertyKey+"Color;";
					}
				}
				script = script+ "	"
				+ "				   dataSource[i].startDate = startDate;"
				+ "		           dataSource[i].endDate = endDate;"
				+ "		           dataSource[i].flags = list;"
				+ "				   if(dataSource[i].typeColor != oldColor)dataSource[i].color = null;"
				+"					if (dataSource[i].type!==oldType) {"
				+ "						 dataSource = typeEpisodeConversion"+id+"(dataSource,i,oldType);"
				+ "					}"
				+ "		    }"
				+ "		}"
				+ "		if(!exists){"
				+"		var blackList=[];"	
				+ "		var blckObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":blckList'));"
				+ "		var blckVal = $(blckObj).val();"
				+ "		var blckContent = decodeURIComponent(blckVal);"
				+"		if(blckContent == ''){ blackList= JSON.parse('[]');}"
				+ "		else {blackList= JSON.parse(blckContent);}"
				+ "		var nextId = -1;"
				+ "		for(var tmp in dataSource) {"
				+ "			var id = dataSource[tmp].id;"
				+ "			nextId = Math.max(nextId,id);"
				+ "		}"
				+ "		nextId = nextId + 1;"
				+ "		var v=-33;"
				+ "		for(var i in blackList) {"
				+ "			v=blackList[i];"
				+ "			if (nextId == v) {"
				+ "				nextId = nextId + 1;"
				+ "				break;"
				+ "			}"
				+ "		}"
				+ ""
				+ " 		var newEvent = {"
				+ "				\"id\" : nextId,";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " \""+propertyKey+"\": event."+propertyKey+",";
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
						script = script + " \""+propertyKey+"Color\" : event."+propertyKey+"Color,";
					}
				}
				script = script+ ""
				+ "				\"startDate\": startDate,"
				+ "				\"endDate\" : endDate,"				
				+ "				\"state\" : 'new',"
				+"				\"flags\" : list"
				+ "			};"
				+ ""
				+ "			calendar"+id+".addEvent(newEvent);"
				+ "		}"
				+ "		saveData"+id+"(dataSource);"
				+ "		update"+id+"();"
				+ "}"
				+ "function typeEpisodeConversion"+id+"(dataSource,index,oldType){"
				+ " var event = dataSource[index];"
				+ " console.log('typeEpisodeConversion');"
				+ "	const whitelist = ['id','name','type','typeColor','startDate','endDate','flags'];"
				+ " for (var key in event) {"
				+ "		if(whitelist.indexOf(key) < 0){"
				+ "			delete event[key];"
				+ "		}"		
				+ "	}"
				+ " event.state = 'new';"
				+ " if(typeof event.flags === 'undefined')event.flags = list;"
				+ "	if(!event.flags.includes('tyC'))event.flags.push('tyC');"
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
				+ "	return dataSource;"
				+ "}"
				+ "function deleteEvent"+id+"(event) {"
				+"		var blackList=[];"	
				+ "		var blckObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":blckList'));"
				+ "		var blckVal = $(blckObj).val();"
				+ "		var blckContent = decodeURIComponent(blckVal);"
				+"		if(blckContent == ''){"
				+ "			blackList= JSON.parse('[]');"
				+ "		} else {"	
				+"			blackList= JSON.parse(blckContent);"
				+ "		}"
				+ "		var dataSource = calendar"+id+"._dataSource;"
				+ "		for(var i in dataSource) {\n"
				+ "			if(dataSource[i].id == event.event.id) {"
				+ "				if (!blackList.includes(event.event.id)) {"
				+ "					blackList.push(event.event.id);"
				+ "					var encoded = encodeURIComponent(JSON.stringify(blackList,null,0));" 
				+ "					var compressed = encoded;"
				+ "					$(blckObj).val(compressed);"
				+ "					$(blckObj).change();"
				+ "				}"	
				+ "		    	dataSource.splice(i, 1);"
				+ "		        break;"
				+ "		      }"
				+ "		}"
				+ "		for(var i in dataSource) {\n"
				+ "		       if(dataSource[i].id != event.event.id) {"
				+ "				   if(!(typeof dataSource[i].childEpisodes === 'undefined')){"
				+ "				   		if(dataSource[i].childEpisodes.includes(event.event.id)){"
				+ "							dataSource[i].childEpisodes.splice(dataSource[i].childEpisodes.indexOf(event.event.id),1);"
				+ "						}"
				+ "				   }"				
				+ "				   if(!(typeof dataSource[i].parentEpisode === 'undefined')){"
				+ "				   		if(dataSource[i].parentEpisode == event.event.id){"
				+ "							delete dataSource[i]['parentEpisode'];"
				+ "				   		}"
				+ "					}"
				+ "		       }"
				+ "		}"
				+ "		saveData"+id+"(dataSource);"
				+ "		update"+id+"();"
				+ "}"
				+ ""
				+ "function details"+id+"(event) {"
				+ "		var events = event.event.events;\n" 
				+ "		for (var i = 0; i < events.length; i++) {"
				+ "			var tmpEvent = events[i];"
				+ "		}"
				+ "}"
				+ ""
				+ "function selectRange"+id+"(event) {"
				+ "		var nextId = -1;"
				+ "		var startDate = new Date(event.startDate);"
				+ "		startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "		var endDate = new Date(event.endDate);"
				+ "		endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ " 	var newEvent = {"
				+ "			\"id\" : nextId,";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					if(propertyKey.contentEquals("translation"))continue;
					if(propertyKey.contentEquals("settings"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					final JsonObject propertyObj = (JsonObject)property;
					if(propertyKey.contentEquals("type")) {
							script = script + " \""+propertyKey+"\": 'SlotDefault',";
					}
					else {
						if(propertyObj.has("values")) {
							script = script + " \""+propertyKey+"\": '',";
						}	
					}
				}
				script = script+ "	"
				+ "			\"startDate\": startDate,"
				+ "			\"endDate\" : endDate"
				+ "		};"
				+ "		editEvent"+id+"(newEvent);"
				+ "}"
				+ ""
				+ "function loadData"+id+"() {"
				+ "		var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));"
				+ "		var decompressed = $(fieldObj).val();"
				+ "		var fieldContent = decodeURIComponent(decompressed);"
				+ " 	var data= null;"
				+ "		if(fieldContent == ''){data= JSON.parse('[]');}"
				+ "		else { data= JSON.parse(fieldContent);}"
				+ "		var back = [];"
				+ "		for ( var i = 0; i < data.length; i++) {" 
				+ "			var obj = data[i];"
				+ "			var startDate = new Date(obj.startDate);"
				+ "			startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "			obj.startDate = startDate;"
				+ "			var endDate = new Date(obj.endDate);"
				+ "			endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "			obj.endDate = endDate;"
				+ "			var inRange = true;"
				+ "			if(inRange)back.push(obj);"
+ "						}" 
				+ "		return back;"
				+ "}"
				+ ""
				+ "function saveData"+id+"(dataObj) {"
				+ "		for ( var i = 0; i < dataObj.length; i++) {" 
				+ "			var obj = dataObj[i];"
				+ "			var startDate = new Date(obj.startDate);"
				+ "			var endDate = new Date(obj.endDate);"
				+ "		}" 
				+ "		var data = JSON.stringify(dataObj,null,0);"
				+ "		var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));"
				+ "		var encoded = encodeURIComponent(data);"
				+ "		var compressed = encoded;"
				+ "		$(fieldObj).val(compressed);"
				+ "		$(fieldObj).change();"
				+ "}"
				+ ""
				+ "function update"+id+"() {"
				+ "		calendar"+id+".setDataSource(setData"+id+");"
				+ "}"
				+ ""
				+ "function setData"+id+"(year) {"
				+ "		return loadData"+id+"();"
				+ "}"
				+ "		var currentYear"+id+" = new Date().getFullYear();"
				+ "		var minDate"+id+" = new Date('"+episodes.getMinYear()+"-01-01T00:00:00Z');"
				+ "		var maxDate"+id+" = new Date('"+(episodes.getMaxYear())+"-12-31T22:59:59Z');"
				+ "		calendar"+id+" = new Calendar('#"+id+".calendar',{'focusData': '"+getFocus(component)+"','disabledData': "+getDisabled(component)+",enableContextMenu: true,enableRangeSelection: true,style: 'border',minDate: minDate"+id+",maxDate: maxDate"+id+",language:'"+language+"'});\n"	
				+ "		document.querySelector('#"+id+".calendar').addEventListener('mouseOnDay', function(e) {details"+id+"({event: e});});\n"
				+ "		document.querySelector('#"+id+".calendar').addEventListener('selectRange', function(e) {selectRange"+id+"({startDate: e.startDate, endDate: e.endDate });});\n"
				+ "		calendar"+id+".setContextMenuItems([{text: '"+updateLabelStr+"',click: function(e) {editEvent"+id+"({event: e});}},{text: '"+deleteLabelStr+"',click: function(e) {deleteEvent"+id+"({event: e});}}]);\n"
				+ "		calendar"+id+".setDataSource(setData"+id+");\n"
				+ "		calendar"+id+".setYear(currentYear"+id+");\n"
				+ "});"
				+ ""
				+ "$(window).resize(function() {\n" + 
				"	console.log('forced rendering');"
				+ "	calendar"+id+".render();\n" + 
				"});"
				+ "$('#start-before').change(function() {"
				+" 	if ($('#start-before').prop('checked')) {"
				+" 		$('#event-modal"+id+" #monthpickerStart').datepicker('setDate',new Date("+episodes.getMinYear()+","+(Integer.parseInt(episodes.getMinMonth())-1)+",01));"
				+" 		$( '#event-modal"+id+" #episodeStart').prop( 'disabled', true );"
				+"	} else {$( '#event-modal"+id+" #episodeStart').prop( 'disabled', false );}"
				+"});"
				+ "$('#end-continues').change(function() {"
				+" if ($('#end-continues').prop('checked')) {"
				+" 		$('#event-modal"+id+" #monthpickerEnd').datepicker('setDate',new Date("+episodes.getMaxYear()+","+Integer.parseInt(episodes.getMaxMonth())+",0));"
				+" 		$( '#event-modal"+id+" #episodeEnd').prop( 'disabled', true );"
				+"	} else {$( '#event-modal"+id+" #episodeEnd').prop( 'disabled', false );}"
				+"});"
				+ "";
		return script;
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent tmp = JsfUtility.getInstance().getComposite(component);
		if (tmp != null) {
			for (final UIComponent child : tmp.getChildren()) {
				if ((javax.faces.component.html.HtmlInputHidden.class).isAssignableFrom(child.getClass())) {
					if(this.getConfig(component).has("link"))child.getAttributes().put("data-episode-link",this.getConfig(component).get("link").getAsString());
					child.encodeAll(context);
				}
			}
		}
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIEpisodes episodes = (UIEpisodes)component;
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("article");
		writer.startElement("script", component);
		writer.writeAttribute("type", "text/javascript", null);
		final String fingerprint = component.getClientId(context).replace(':', '_');
		writer.write(initScript(context,component,"calendar"+fingerprint,episodes.getLanguage()));
		writer.endElement("script");
	}
}
