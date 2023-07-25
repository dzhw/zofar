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
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIEpisodes.COMPONENT_FAMILY, rendererType = EpisodesRendererOrig.RENDERER_TYPE)
public class EpisodesRendererOrig extends Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EpisodesRendererOrig.class);
	public static final String RENDERER_TYPE = "de.his.zofar.Calendar.episodesOrig";
	public EpisodesRendererOrig() {
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
		addModal(context,component,"calendar"+fingerprint);
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
	private void addModal(FacesContext context, UIComponent component,final String id) throws IOException {
		final UIEpisodes episode = (UIEpisodes)component;
		final int minYear=Integer.parseInt(episode.getMinYear());
		final int maxYear=Integer.parseInt(episode.getMaxYear());
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", "event-modal"+id, null);
		writer.writeAttribute("name", "event-modal"+id, null);
		writer.writeAttribute("class", "modal fade", null);
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
						writer.write("Event");
						writer.endElement("h4");
					writer.endElement("div");
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-body", null);
						writer.startElement("input", component);
						writer.writeAttribute("type", "hidden", null);
						writer.writeAttribute("id", "event-id", null);	
						writer.writeAttribute("name", "event-id", null);
						writer.endElement("input");
						final JsonObject config = this.getConfig(component);
						final Set<String> properties = config.keySet();
						for(final String propertyKey : properties) {
							if(propertyKey.contentEquals("link"))continue;
							final JsonElement property = config.get(propertyKey);
							if (!property.isJsonObject())continue;
							final JsonObject propertyObj = (JsonObject)property;
							writer.startElement("div", component);
							writer.writeAttribute("class", "form-group", null);	
							if(propertyObj.has("label")) {
								writer.startElement("label", component);
									writer.writeAttribute("class", "col-sm-4 control-label", null);	
									writer.writeAttribute("for", "event-"+propertyKey, null);	
									writer.write(propertyObj.get("label").getAsString());
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
										if(answerOption.has("label"))label = answerOption.get("label").getAsString();
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
								}
								if(typeofval == 3) {
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
						writer.writeAttribute("class", "form-group", null);
							writer.startElement("div", component);
							writer.writeAttribute("id", "monthpickerStart", null);
							writer.writeAttribute("class", "input-append date monthpickerCustom", null);
							writer.writeAttribute("data-date", "01-"+minYear, null);
							writer.writeAttribute("data-value", "0", null);
							writer.writeAttribute("data-startDate", minYear+",01,"+"01", null);
							writer.writeAttribute("data-endDate", maxYear+",12,"+"31", null);
							writer.writeAttribute("data-dates-disabled", "07-2018,08-2018", null);
								writer.startElement("label", component);
								writer.writeAttribute("class", "col-sm-12 control-label", null);	
								writer.writeAttribute("for", "episodeStart", null);	
								writer.write("Beginn");
								writer.startElement("input", component);
								writer.writeAttribute("type", "text", null);
								writer.writeAttribute("readonly", "readonly", null);
								writer.writeAttribute("class", "episodeStart", null);
								writer.writeAttribute("name", "episodeStart", null);
								writer.writeAttribute("id", "episodeStart", null);
								writer.writeAttribute("value", "", null);
								writer.endElement("input");
								writer.startElement("span", component);
								writer.writeAttribute("class", "add-on", null);
								writer.startElement("i", component);
								writer.writeAttribute("class", "glyphicon glyphicon-calendar", null);
								writer.endElement("i");
								writer.endElement("span");
								writer.endElement("label");
							writer.endElement("div");
							writer.startElement("div", component);
							writer.writeAttribute("id", "monthpickerEnd", null);
							writer.writeAttribute("class", "input-append date monthpickerCustom", null);
							writer.writeAttribute("data-date", "01-"+minYear, null);
							writer.writeAttribute("data-value", "0", null);
							writer.writeAttribute("data-startDate", minYear+",01,"+"01", null);
							writer.writeAttribute("data-endDate", maxYear+",12,"+"31", null);
							writer.writeAttribute("data-dates-disabled", "07-2018,08-2018", null);
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-12 control-label", null);	
							writer.writeAttribute("for", "episodeEnd", null);	
							writer.write("Ende");
							writer.startElement("input", component);
							writer.writeAttribute("type", "text", null);
							writer.writeAttribute("readonly", "readonly", null);
							writer.writeAttribute("class", "episodeEnd", null);
							writer.writeAttribute("name", "episodeEnd", null);
							writer.writeAttribute("id", "episodeEnd", null);
							writer.writeAttribute("value", "", null);
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
						/*
						writer.startElement("div", component);
						writer.writeAttribute("class", "form-group", null);
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-12 control-label", null);	
							writer.writeAttribute("for", "event-start-date", null);	
							writer.write("Beginn");
							writer.endElement("label");
							String[][] months = {{"Jan", "01"}, {"Feb", "02"}, {"Mär", "03"}, {"Apr", "04"}, {"Mai", "05"}, {"Jun", "06"}, {"Jul", "07"}, {"Aug", "08"}, {"Sept", "09"}, {"Okt", "10"}, {"Nov", "11"}, {"Dez", "12"}};
							writer.startElement("select", component);
							writer.writeAttribute("class", "form-control browser-default custom-select event-start-month", null);
							writer.writeAttribute("id", "event-start-month", null);	
							writer.writeAttribute("name", "event-start-month", null);	
							for(int i=0; i<months.length;i++) {	
								writer.startElement("option", component);
								writer.writeAttribute("id", "event-month1"+months[i][1], null);
								writer.writeAttribute("value", ""+months[i][1]+"", null);
								writer.writeAttribute("data-value", ""+(i+1)+"", null);
								writer.write(months[i][0]);
								writer.endElement("option");					
							}	
							writer.endElement("select");
							writer.startElement("select", component);
							writer.writeAttribute("class", "form-control browser-default custom-select event-start-year", null);
							writer.writeAttribute("id", "event-start-year", null);	
							writer.writeAttribute("name", "event-start-year", null);	
							for(int i=minYear; i<=maxYear;i++) {	
								writer.startElement("option", component);
								writer.writeAttribute("id", "event-year"+i, null);
								writer.writeAttribute("value", ""+i+"", null);
								writer.writeAttribute("data-value", ""+i+"", null);
								writer.write(""+i);
								writer.endElement("option");	
							}
							writer.endElement("select");
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-12 control-label", null);	
							writer.writeAttribute("for", "event-end-date", null);	
							writer.write("Ende");
							writer.endElement("label");
							writer.startElement("select", component);
							writer.writeAttribute("class", "form-control browser-default custom-select event-end-month", null);
							writer.writeAttribute("id", "event-end-month", null);	
							writer.writeAttribute("name", "event-end-month", null);	
							for(int i=0; i<months.length;i++) {	
								writer.startElement("option", component);
								writer.writeAttribute("id", "event-month1"+months[i][1], null);
								writer.writeAttribute("value", ""+months[i][1]+"", null);
								writer.writeAttribute("data-value", ""+(i+1)+"", null);
								writer.write(months[i][0]);
								writer.endElement("option");					
							}	
							writer.endElement("select");
							writer.startElement("select", component);
							writer.writeAttribute("class", "form-control browser-default custom-select event-end-year", null);
							writer.writeAttribute("id", "event-end-year", null);	
							writer.writeAttribute("name", "event-end-year", null);	
							for(int i=minYear; i<= maxYear;i++) {	
								writer.startElement("option", component);
								writer.writeAttribute("id", "event-year"+i, null);
								writer.writeAttribute("value", ""+i+"", null);
								writer.writeAttribute("data-value", ""+i+"", null);
								writer.write(""+i);
								writer.endElement("option");	
							}
							writer.endElement("select");
						writer.endElement("div");
					writer.endElement("div");
					*/
						/*
						writer.startElement("div", component);
						writer.writeAttribute("class", "form-group", null);
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-4 control-label", null);	
							writer.writeAttribute("for", "event-start-date", null);	
							writer.write("Beginn");
							writer.endElement("label");
							writer.startElement("label", component);
							writer.writeAttribute("class", "col-sm-4 control-label", null);	
							writer.writeAttribute("for", "event-end-date", null);	
							writer.write("Ende");
							writer.endElement("label");
							writer.startElement("div", component);
							writer.writeAttribute("class", "col-sm-7", null);	
							writer.startElement("div", component);
							writer.writeAttribute("class", "input-group input-daterange", null);
								writer.startElement("input", component);
								writer.writeAttribute("class", "form-control", null);	
								writer.writeAttribute("id", "event-start-date", null);	
								writer.writeAttribute("name", "event-start-date", null);	
								writer.writeAttribute("type", "text", null);
								writer.endElement("input");
								writer.startElement("input", component);
								writer.writeAttribute("class", "form-control", null);	
								writer.writeAttribute("id", "event-end-date", null);	
								writer.writeAttribute("name", "event-end-date", null);	
								writer.writeAttribute("type", "text", null);
								writer.endElement("input");
							writer.endElement("div");
							writer.endElement("div");
						writer.endElement("div");
					writer.endElement("div");
					*/
					writer.startElement("div", component);
					writer.writeAttribute("class", "modal-footer", null);
						writer.startElement("button", component);
						writer.writeAttribute("type", "button", null);
						writer.writeAttribute("class", "btn btn-default", null);
						writer.writeAttribute("data-dismiss", "modal", null);
						writer.write("Cancel");
						writer.endElement("button");
						writer.startElement("button", component);
						writer.writeAttribute("type", "button", null);
						writer.writeAttribute("class", "btn btn-primary", null);
						writer.writeAttribute("id", "save-event"+id, null);
						writer.write("Save");
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
		String script = "var calendar"+id+";"
				+ "$(document).ready(function(){"
				+ "	var fieldObj = $('#'+$.escapeSelector('"+component.getClientId(context)+":data'));"
				+ "	fieldObj.attr('data-episode-link','"+this.getConfig(component).get("link").getAsString()+"');"
				+ "	fieldObj.on(\"change\",function() {\n" 
				+ "		console.log( \"Handler0 for .change() called.\" + event );\n" 
				+ "		$(\"input[data-episode-link='"+this.getConfig(component).get("link").getAsString()+"']\").each(function(){"
				+ "		var cfield = $(this);"
				+ "		var ref =fieldObj;"
				+ "		if(cfield.attr('id') != ref.attr('id')){"
				+ "			cfield.val(ref.val());"
				+ "			cfield.trigger('update',[ref]);"
				+ "		}"
				+ "	});"
				+ "});"
				+ "	fieldObj.on(\"update\",function() {\n" 
				+ " 	console.log( \"Handler0 for .update() called.\" + event );\n"
				+ "		update"+id+"();" 
				+ "	});"
				+ ""
				+"	$('#event-modal"+id+" #monthpickerStart').datepicker({"
				+"		autoclose: true,"
				+"		format: \"M yyyy\","
				+"      startView: \"months\", "
				+"      minViewMode: \"months\","
				+"      startDate: new Date($( '#event-modal"+id+" #monthpickerStart' ).attr( 'data-startdate' )),"
				+"      endDate: new Date($( '#event-modal"+id+" #monthpickerStart' ).attr( 'data-enddate' ))"
				+"	    }).on(\"changeDate\", function(event) {"
				+"	    	var cDate = new Date(event.date.valueOf());"
				+"	    	cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() , 1).getDate());"
				+"	    	$(this).attr(\"data-value\",cDate);"
				+"	    	var cmonth = (cDate.getMonth()+1);"
				+"	    	var cmonthStr = \"\";"
				+"	    	if(cmonth < 10)cmonthStr += \"0\";"
				+"	    	cmonthStr += cmonth;"
				+"	    	var cDateStr = cmonthStr+\" \"+cDate.getFullYear();"
				+"			$('#event-modal"+id+" #monthpickerEnd').datepicker('setStartDate', cDateStr);"
				+"	});"
				+"	$('#event-modal"+id+" #monthpickerEnd').datepicker({"
				+"		autoclose: true,"
				+"		format: \"M yyyy\","
				+"      startView: \"months\", "
				+"      minViewMode: \"months\","
				+"      startDate: new Date($( '#event-modal"+id+" #monthpickerEnd' ).attr( 'data-startdate' )),"
				+"      endDate: new Date($( '#event-modal"+id+" #monthpickerEnd' ).attr( 'data-enddate' )),"
				+"	    }).on(\"changeDate\", function(event) {"
				+"	    	var cDate = new Date(event.date.valueOf());"
				+"	    	cDate.setDate(new Date(cDate.getFullYear(), cDate.getMonth() +1, 0).getDate());"
				+"	    	$(this).attr(\"data-value\",cDate);"
				+"	    	var cmonth = (cDate.getMonth()+1);"
				+"	    	var cmonthStr = \"\";"
				+"	    	if(cmonth < 10)cmonthStr += \"0\";"
				+"	    	cmonthStr += cmonth;"
				+"	    	var cDateStr = cmonthStr+\" \"+cDate.getFullYear();"
				+"			$('#event-modal"+id+" #monthpickerStart').datepicker('setEndDate', cDateStr);"
				+"	});"				
				+ ""
				+ ""
				+ ""
				+ "function editEvent"+id+"(event) {"
				+ "		var dataSource = calendar"+id+"._dataSource;"
				+ "		var exists = false;";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + "$('#event-modal"+id+" #event-"+propertyKey+"').val('');";
				}
				script = script+ ""
				+ "		if(event.event){"
				+ "		for(var i in dataSource) {"
				+ "		   if(dataSource[i].id == event.event.id) {"
				+ "			   exists = true;"	
				+ "			   $('#event-modal"+id+" input[name=\"event-id\"]').val(dataSource[i].id);";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " $('#event-modal"+id+" #event-"+propertyKey+"').val(dataSource[i]."+propertyKey+");";
				}
				script = script + "	"
				+ "			  var startDate = new Date(dataSource[i].startDate);"
				+ "			  startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "		  	  var endDate = new Date(dataSource[i].endDate);"
				+ "				endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "			   $('#event-modal"+id+" #monthpickerStart').attr('data-date',''+moment(startDate).format( 'MM')+'-'+moment(startDate).format( 'YYYY')+'');"
				+"				$('#event-modal"+id+" #monthpickerStart').datepicker('setDate', ''+$( '#monthpickerStart' ).attr( 'data-date' ));"
				+ "			   $('#event-modal"+id+" #monthpickerEnd').attr('data-date',''+moment(endDate).format( 'MM')+'-'+moment(endDate).format( 'YYYY')+'');"
				+"				$('#event-modal"+id+" #monthpickerEnd').datepicker('setDate', ''+$( '#monthpickerEnd' ).attr( 'data-date' ));"
				+ "		       break;"
				+ "		   }"
				+ "		}"
				+ "		} "
				+ "		if(!exists){"
				+ "			   $('#event-modal"+id+" #event-id').val(event.id);";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " $('#event-modal"+id+" #event-"+propertyKey+"').val(event."+propertyKey+");";
				}
				script = script + ""
				+ "				var startDate = new Date(event.startDate);"
				+ "				startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "				var endDate = new Date(event.endDate);"
				+ "				endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
+ "			   $('#event-modal"+id+" #monthpickerStart').attr('data-date',''+moment(startDate).format( 'MM')+'-'+moment(startDate).format( 'YYYY')+'');"
+"				$('#event-modal"+id+" #monthpickerStart').datepicker('setDate', ''+$( '#monthpickerStart' ).attr( 'data-date' ));"
+ "			   $('#event-modal"+id+" #monthpickerEnd').attr('data-date',''+moment(endDate).format( 'MM')+'-'+moment(endDate).format( 'YYYY')+'');"
+"				$('#event-modal"+id+" #monthpickerEnd').datepicker('setDate', ''+$( '#monthpickerEnd' ).attr( 'data-date' ));"
				+ "		}"
				+ "		$('#event-modal"+id+"').modal();"
				+ "}"
				+ "$('#save-event"+id+"').click(function() {"
				+ "		console.log('modal save clicked');"
				+ "		var flag = true;"
				+ "		try{"
				+"	var startDate =new Date($('#event-modal"+id+" #monthpickerStart').attr('data-value'));"
				+ " console.log('start date : '+startDate);"
				+ "			startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "		}catch(e){flag=false;alert('kein gültiges Startdatum! ');}"
				+ "		try{"
				+"	var endDate =new Date($('#event-modal"+id+" #monthpickerEnd').attr('data-value'));"
				+ " console.log('end date : '+endDate);"
				+ "		endDate.setDate(new Date(endDate.getFullYear(), endDate.getMonth() +1, 0).getDate());"
				+ "			endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "		}catch(e){flag=false;alert('kein gültiges Enddatum!');console.log(e);}"
				+ "		if(flag){"
				+ "			var event = {"
				+ "	  	   		id: $('#event-modal"+id+" #event-id').val()," ;
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " "+propertyKey+": $('#event-modal"+id+" #event-"+propertyKey+"').val(),";
					if(property.getAsJsonObject().get("values").isJsonArray()) {
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
						if(colorFlag)script = script + " \""+propertyKey+"Color\" : $('#event-modal"+id+" #event-"+propertyKey+" option:selected').data('color'),";
					}
					else if(property.getAsJsonObject().has("color")) {
						script = script + " \""+propertyKey+"Color\" : $('#event-modal"+id+" #event-"+propertyKey+"').data('color'),";
					}
				}
				script = script+ "	"
				+ "        		startDate: startDate," 
				+ "	       		endDate: endDate"
				+ "	   		};"
				+ "    		var splittedEvents = splitEvent"+id+"(event);" 
				+ "			if(splittedEvents.length > 1)splittedEvents.forEach(splittedEvent => modifyEvent"+id+"(splittedEvent));"
				+ "			else modifyEvent"+id+"(event);"
				+ "$('#event-modal"+id+"').modal('hide');"
				+ "	   }"
				+ "});"
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
				+ ""
				+ "function modifyEvent"+id+"(event) {"
				+ "		console.log('modify event');"
				+ "		console.log(event);"
				+ "		var dataSource = calendar"+id+"._dataSource;"
				+ "		var exists = false;"
				+ "		var startDate = new Date(event.startDate);"
				+ "		startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "		var endDate = new Date(event.endDate);"
				+ "		endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ "		for(var i in dataSource) {"
				+ "		       if(dataSource[i].id == event.id) {"
				+ "				   exists = true;"
				+ "				   const oldColor = dataSource[i].typeColor;";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " dataSource[i]."+propertyKey+" = event."+propertyKey+";";
					boolean colorFlag = false;
					if(property.getAsJsonObject().get("values").isJsonArray()) {
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
				+ "		           dataSource[i].state = 'updated';"
				+ "				   if(dataSource[i].typeColor != oldColor)dataSource[i].color = null;"
				+ "		           break;"
				+ "		       }"
				+ "		}"
				+ "		if(!exists){"
				+ "			var nextId = -1;"
				+ "			for(var tmp in dataSource) {"
				+ "				var id = dataSource[tmp].id;"
				+ "				nextId = Math.max(nextId,id);"
				+ "			};"
				+ "			nextId = nextId + 1;"
				+ ""
				+ " 		var newEvent = {"
				+ "				\"id\" : nextId,";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
					final JsonElement property = config.get(propertyKey);
					if (!property.isJsonObject())continue;
					script = script + " \""+propertyKey+"\": event."+propertyKey+",";
					boolean colorFlag = false;
					if(property.getAsJsonObject().get("values").isJsonArray()) {
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
				+ "				\"state\" : 'new'"
				+ "			};"
				+ ""
				+ "			calendar"+id+".addEvent(newEvent);"
				+ "		}"
				+ "		saveData"+id+"(dataSource);"
				+ "		update"+id+"();"
				+ "}"
				+ ""
				+ "function deleteEvent"+id+"(event) {"
				+"		console.log('delete event');"
				+ "		console.log(event);"
				+ "		var dataSource = calendar"+id+"._dataSource;"
				+ "		for(var i in dataSource) {\n"
				+ "		       if(dataSource[i].id == event.event.id) {"
				+ "		           dataSource.splice(i, 1);"
				+ "		           break;"
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
				+ "		console.log('select range');"
				+ "		console.log(event);"
				+ "		var nextId = -1;"
				+ "		var startDate = new Date(event.startDate);"
				+ "		startDate = new Date(startDate.getFullYear()+'-'+(startDate.getMonth() < 9 ? '0': '') + (startDate.getMonth()+1)+'-'+(startDate.getDate() < 9 ? '0': '') +startDate.getDate()+'T01:00:00Z');"
				+ "		var endDate = new Date(event.endDate);"
				+ "		endDate = new Date(endDate.getFullYear()+'-'+(endDate.getMonth() < 9 ? '0': '') + (endDate.getMonth()+1)+'-'+(endDate.getDate() < 9 ? '0': '') +endDate.getDate()+'T01:00:00Z');"
				+ " 	var newEvent = {"
				+ "			\"id\" : nextId,";
				for(final String propertyKey : properties) {
					if(propertyKey.contentEquals("link"))continue;
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
				+ "			back.push(obj);"
				+ "		}" 
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
				+ "		console.log(data);"
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
				+ "		calendar"+id+".setContextMenuItems([{text: 'Update',click: function(e) {editEvent"+id+"({event: e});}},{text: 'Delete',click: function(e) {deleteEvent"+id+"({event: e});}}]);\n"
				+ "		calendar"+id+".setDataSource(setData"+id+");\n"
				+ "		calendar"+id+".setYear(currentYear"+id+");\n"
				+ "});"
				+ ""
				+ "$(window).resize(function() {\n" + 
				"	console.log('forced rendering');"
				+ "	calendar"+id+".render();\n" + 
				"});";
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
