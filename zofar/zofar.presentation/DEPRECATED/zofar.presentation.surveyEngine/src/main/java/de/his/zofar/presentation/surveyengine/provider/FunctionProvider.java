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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.sun.faces.component.visit.FullVisitContext;
import de.his.zofar.presentation.surveyengine.AbstractAnswerBean;
import de.his.zofar.presentation.surveyengine.BooleanValueTypeBean;
import de.his.zofar.presentation.surveyengine.NumberValueTypeBean;
import de.his.zofar.presentation.surveyengine.SingleChoiceAnswerOptionTypeBean;
import de.his.zofar.presentation.surveyengine.StringValueTypeBean;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendar;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarLegend;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarSheet;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.util.SystemConfiguration;
import de.his.zofar.service.surveyengine.model.Participant;
/**
 * Bean to provide Zofar specific EL - Functions
 * 
 * @author meisner
 * 
 */
@ManagedBean(name = "zofar")
@ApplicationScoped
public class FunctionProvider implements Serializable {
	private static final long serialVersionUID = -78074230548327907L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FunctionProvider.class);
	private final static DecimalFormatSymbols customFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);
	private final Random random;
	public FunctionProvider() {
		super();
		this.random = new Random();
	}
	@PostConstruct
	private void init() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("init");
		}
		customFormatSymbols.setDecimalSeparator('.');
	}
	public synchronized boolean recorderEnabled(){
		final SystemConfiguration system = SystemConfiguration.getInstance();
		return system.record();
	}
	public synchronized Object valueOf(final AbstractAnswerBean var) {
		if (var != null) {
			if ((BooleanValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((BooleanValueTypeBean) var).getValue();
			}
			if ((NumberValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((NumberValueTypeBean) var).getValue();
			}
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((StringValueTypeBean) var).getValue();
			}
			if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var
					.getClass())) {
				return ((SingleChoiceAnswerOptionTypeBean) var).getValue();
			}
		}
		return null;
	}
	public synchronized String labelOf(final AbstractAnswerBean var) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("labelOf : {}",var);
		}
		if (var != null) {
			if ((BooleanValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((BooleanValueTypeBean) var).getValue()+"";
			}
			if ((NumberValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((NumberValueTypeBean) var).getValue()+"";
			}
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((StringValueTypeBean) var).getValue();
			}
			if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var
					.getClass())) {
				return ((SingleChoiceAnswerOptionTypeBean) var).getLabel();
			}
		}
		return null;
	}
	private synchronized <T> List<T> mergeLists(final List<T> list1,
			final List<T> list2) {
		if ((list1 == null) && (list2 == null))
			return null;
		final List<T> list = new ArrayList<T>();
		if (list1 != null)
			list.addAll(list1);
		if (list2 != null)
			list.addAll(list2);
		return list;
	}
	private synchronized <T, S> Map<T, S> mergeListsToMap(
			final List<T> keyList, final List<S> valueList) {
		final Iterator<T> i1 = keyList.iterator();
		final Iterator<S> i2 = valueList.iterator();
		final Map<T, S> map = new HashMap<T, S>();
		while (i1.hasNext() && i2.hasNext()) {
			map.put(i1.next(), i2.next());
		}
		return map;
	}
	public synchronized List<Object> list(final Object element1) {
		return listHelper(element1);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2) {
		return listHelper(element1, element2);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3) {
		return listHelper(element1, element2, element3);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3, final Object element4) {
		return listHelper(element1, element2, element3, element4);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5) {
		return listHelper(element1, element2, element3, element4, element5);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6) {
		return listHelper(element1, element2, element3, element4, element5,
				element6);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5,
			final Object element6, final Object element7) {
		return listHelper(element1, element2, element3, element4, element5,
				element6, element7);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5,
			final Object element6, final Object element7, final Object element8) {
		return listHelper(element1, element2, element3, element4, element5,
				element6, element7, element8);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5,
			final Object element6, final Object element7,
			final Object element8, final Object element9) {
		return listHelper(element1, element2, element3, element4, element5,
				element6, element7, element8, element9);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5,
			final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10) {
		return listHelper(element1, element2, element3, element4, element5,
				element6, element7, element8, element9, element10);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5,
			final Object element6, final Object element7,
			final Object element8, final Object element9, 
			final Object element10, final Object element11, final Object element12, final Object element13, final Object element14,
			final Object element15, final Object element16, final Object element17, final Object element18) {
		return listHelper(element1, element2, element3, element4, element5,
				element6, element7, element8, element9, element10, element11, element12, element13, element14, element15, element16
				, element17, element18);
	}
	public synchronized List<Object> list(final Object element1,
			final Object element2, final Object element3,
			final Object element4, final Object element5,
			final Object element6, final Object element7,
			final Object element8, final Object element9, 
			final Object element10, final Object element11, final Object element12, final Object element13, final Object element14,
			final Object element15, final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21) {
		return listHelper(element1, element2, element3, element4, element5,
				element6, element7, element8, element9, element10, element11, element12, element13, element14, element15, element16
				, element17, element18, element19, element20, element21);
	}
	public synchronized List<Object> list(final Object element1,
				final Object element2, final Object element3,
				final Object element4, final Object element5,
				final Object element6, final Object element7,
				final Object element8, final Object element9, 
				final Object element10, final Object element11, final Object element12, final Object element13, final Object element14,
				final Object element15, final Object element16, final Object element17, final Object element18, final Object element19,
				final Object element20, final Object element21, final Object element22, final Object element23, final Object element24, final Object element25, final Object element26, final Object element27
				, final Object element28, final Object element29, final Object element30, final Object element31, final Object element32, final Object element33, final Object element34, final Object element35
				, final Object element36, final Object element37, final Object element38, final Object element39, final Object element40, final Object element41, final Object element42, final Object element43
				, final Object element44, final Object element45, final Object element46, final Object element47, final Object element48, final Object element49, final Object element50, final Object element51
				, final Object element52, final Object element53, final Object element54, final Object element55, final Object element56, final Object element57, final Object element58, final Object element59
				, final Object element60, final Object element61, final Object element62, final Object element63, final Object element64, final Object element65, final Object element66, final Object element67
				, final Object element68, final Object element69, final Object element70, final Object element71, final Object element72, final Object element73, final Object element74, final Object element75
				, final Object element76, final Object element77, final Object element78, final Object element79, final Object element80, final Object element81, final Object element82, final Object element83
			) {
			return listHelper(element1, element2, element3, element4, element5,
					element6, element7, element8, element9, element10, element11, element12, element13, element14, element15, element16
					, element17, element18, element19, element20, element21
					,element22,element23,element24,element25,element26,element27
					,element28,element29,element30,element31,element32,element33,element34,element35
					,element36,element37,element38,element39,element40,element41,element42,element43
					,element44,element45,element46,element47,element48,element49,element50,element51
					,element52,element53,element54,element55,element56,element57,element58,element59
					,element60,element61,element62,element63,element64,element65,element66,element67
					,element68,element69,element70,element71,element72,element73,element74,element75
					,element76,element77,element78,element79,element80,element81,element82,element83
					);
		}
    public synchronized boolean inList(final List<Object> list, final Object pattern){
        if(list == null)return false;
        if(pattern == null) return false;
        if(list.isEmpty()) return false;
        return list.contains(pattern);
    }
    public synchronized Map<String,String> map(final String toParse) {
        return this.map(toParse, "=", ",");
    }
    public synchronized Map<String,String> map(final String toParse, final String pairSplit, final String itemSplit) {
        if(toParse == null)return null;
        if(toParse.equals(""))return null;
        if(pairSplit == null)return null;
        if(pairSplit.equals(""))return null;
        if(itemSplit == null)return null;
        if(itemSplit.equals(""))return null;
        Map<String,String> back = new LinkedHashMap<String,String>();
        if(!toParse.contains(itemSplit)){
            if(toParse.contains(pairSplit)){
                String[] pair = toParse.split(pairSplit);
                if((pair != null)&&(pair.length == 2)){
                    back.put(pair[0], pair[1]);
                }
            }
        }
        else{
            String[] items = toParse.split(itemSplit);
            if(items != null){
                for(final String item : items){
                    if(item.contains(pairSplit)){
                        String[] pair = item.split(pairSplit);
                        if((pair != null)&&(pair.length == 2)){
                            back.put(pair[0], pair[1]);
                        }
                    }
                }
            }
        }
        return back;
    }
	private List<Object> listHelper(Object... objects) {
		return (List<Object>) Arrays.asList(objects);
	}
	public synchronized List<Object> explode(final String toSplit,final String splitBy){
		return listHelper(toSplit.split(splitBy));
	}
	public synchronized List<?> reverseList(final List<?> list) {
		if (list == null)
			return null;
		final List<?> reversedValues = new ArrayList<Object>(list);
		Collections.reverse(reversedValues);
		return reversedValues;
	}
	public synchronized List<?> optionsOf(final AbstractAnswerBean var) {
		if (var == null)
			return null;
		if ((BooleanValueTypeBean.class).isAssignableFrom(var.getClass())) {
			return new ArrayList<Boolean>();
		}
		if ((NumberValueTypeBean.class).isAssignableFrom(var.getClass())) {
			return new ArrayList<Number>();
		}
		if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
			return new ArrayList<String>();
		}
		if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var
				.getClass())) {
			final SingleChoiceAnswerOptionTypeBean tmp = (SingleChoiceAnswerOptionTypeBean) var;
			final Map<String, Object> options = tmp.getOptionValues();
			List<Object> valueList = new ArrayList<Object>(options.values());
			valueList.removeAll(missingsOf(var));
			return valueList;
		}
		return null;
	}
	public synchronized List<?> missingsOf(final AbstractAnswerBean var) {
		if (var == null)
			return null;
		if ((BooleanValueTypeBean.class).isAssignableFrom(var.getClass())) {
			return new ArrayList<Boolean>();
		}
		if ((NumberValueTypeBean.class).isAssignableFrom(var.getClass())) {
			final List<Object> missingList = new ArrayList<Object>();
			missingList.add("");
			return missingList;
		}
		if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
			final List<Object> missingList = new ArrayList<Object>();
			missingList.add("");
			return missingList;
		}
		if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var
				.getClass())) {
			final SingleChoiceAnswerOptionTypeBean tmp = (SingleChoiceAnswerOptionTypeBean) var;
			final List<Object> missingList = new ArrayList<Object>();
			final Map<String, Object> options = tmp.getMissingValues();
			if(options != null)missingList.addAll(options.values());
			missingList.add("");
			return missingList;
		}
		return null;
	}
	public synchronized Boolean isMissing(final AbstractAnswerBean var) {
		if (var != null) {
			final List<?> missingList = missingsOf(var);
			final Object value = valueOf(var);
			if ((value != null) && (missingList != null))
				return missingList.contains(value);
		}
		return false;
	}
	public synchronized Boolean isSet(final AbstractAnswerBean var,final Participant participant) {
		if(var == null)return false;
		if(participant == null)return false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("is Set {} for {}",var.getVariableName(),participant.getToken());
		}
		if(participant.getSurveyData().containsKey(var.getVariableName()))return true;
		return false;
	}
	public synchronized Boolean isBooleanSet(final AbstractAnswerBean var,final Participant participant) {
		if(var == null)return false;
		if(participant == null)return false;
		if(participant.getSurveyData().containsKey(var.getVariableName()) && var.getStringValue().equals("true")){
			return true;
		}
		return false;
	}
	public synchronized Integer isSetCounter(
			final List<AbstractAnswerBean> vars,final Participant participant) {
		if (vars != null) {
			int back = 0;
			for (Object var : vars) {
				if (((AbstractAnswerBean.class).isAssignableFrom(var
						.getClass()))
						&& (isBooleanSet((AbstractAnswerBean) var,participant)))
					back = back + 1;
			}
			return back;
		}
		return 0;
	}
	public synchronized Integer missingCount(
			final List<AbstractAnswerBean> vars) {
		if (vars != null) {
			int back = 0;
			for (Object var : vars) {
				if (((AbstractAnswerBean.class).isAssignableFrom(var
						.getClass()))
						&& (isMissing((AbstractAnswerBean) var)))
					back = back + 1;
			}
			return back;
		}
		return 0;
	}
	public synchronized Object reverseValue(final AbstractAnswerBean var) {
		if (var == null)
			return null;
		final List<?> values = optionsOf(var);
		Map<?,?> map = mergeListsToMap(values, reverseList(values));
		return recodeValue(var, map,
				valueOf(var));
	}
	public synchronized Object recodeValue(final AbstractAnswerBean var,
			final Map<?, ?> mapping, final Object alternative) {
		if (var == null)
			return null;
		if (mapping == null)
			return alternative;
		if (mapping.isEmpty())
			return alternative;
		if (mapping.containsKey(valueOf(var)))
			return mapping.get(valueOf(var));
		return alternative;
	}
	public synchronized Object chooseSetted(final SingleChoiceAnswerOptionTypeBean option1, final SingleChoiceAnswerOptionTypeBean option2) {
		String value1 = null;
		if (option1!=null){
			value1=option1.getValue();
		}		
		String value2 = null;
		if (option2!=null){
			value2=option2.getValue();
		}
		if((value1 == null)&&(value2 == null))return null;
		if((value1 != null)&&(value2 == null))return value1;
		if((value1 == null)&&(value2 != null))return value2;
		value1 = value1.trim();
		value2 = value2.trim();
		if((value1.equals(""))&&(value2.equals("")))return "";
		if((value1.equals(""))&&(!value2.equals("")))return option2.getStringValue();
		if((!value1.equals(""))&&(value2.equals("")))return option1.getStringValue();
		if((!value1.equals(""))&&(!value2.equals("")))return option1.getStringValue()+option2.getStringValue();
		return "";
	}
	public synchronized Object chooseSettedDropDown(final SingleChoiceAnswerOptionTypeBean option1, final SingleChoiceAnswerOptionTypeBean option2) {
		String value1 = null;
		if (option1!=null){
			value1=option1.getValueId();
		}		
		String value2 = null;
		if (option2!=null){
			value2=option2.getValueId();
		}
		if((value1 == null)&&(value2 == null))return null;
		if((value1 != null)&&(value2 == null))return value1;
		if((value1 == null)&&(value2 != null))return value2;
		if((value1.equals(""))&&(value2.equals("")))return null;
		if((!value1 .equals(""))&&(value2.equals("")))return value1;
		if((value1.equals(""))&&(!value2.equals("")))return value2;
		if((!value1.equals(""))&&(!value2.equals(""))){
			final String firstId = option2.getOptionValues().keySet().toArray()[0]+"";
			if(!value2.equals(firstId))return value2;
			else if(!value1.equals(firstId)) return value1;
			else return firstId;
		}
		return null;
	}
	public synchronized Object ifthenelse(final Boolean condition,
			final Object thenElement, final Object elseElement) {
		if (condition)
			return thenElement;
		return elseElement;
	}
	public synchronized Double asNumber(final Object input) {
		if (input == null)
			return 0D;
		Object toConvert = input;
		if ((AbstractAnswerBean.class).isAssignableFrom(input.getClass()))
			toConvert = valueOf((AbstractAnswerBean) input);
		try {
			return Double.parseDouble(toConvert + "");
		} catch (NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("keine Zahl {}", input);
			}
		}
		return 0D;
	}
	public Double asNumber(final Object input,final int digits) {
		if(digits <= 0)return this.asNumber(input);
		final Double back = new Double (new DecimalFormat("#."+StringUtils.repeat("#", digits),customFormatSymbols).format(this.asNumber(input)));
		return back;
	}
	public Long toLong(final Object input) {
		if (input == null)
			return 0L;
		Object toConvert = input;
		if ((Number.class).isAssignableFrom(input.getClass()))
			toConvert = ((Number)input).longValue();
		try {
			return Long.parseLong(toConvert + "");
		} catch (NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("keine Zahl {}", input);
			}
		}
		return 0L;
	}
	public void assign(final String varname, final Object expression) {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.put(varname, expression);
	}
	public boolean xor(final boolean a,final boolean b){
		return (a ^ b);
	}
	public synchronized Object scaleValue(
			final List<AbstractAnswerBean> vars, final String missing) {
		return scaleValue(vars, null, missing);
	}
	public synchronized Object scaleValue(
			final List<AbstractAnswerBean> vars,
			final List<AbstractAnswerBean> reverseVars, final String missing) {
		final List<AbstractAnswerBean> allVars = mergeLists(vars,
				reverseVars);
		final Integer varCount = allVars.size();
		final Integer missingCount = missingCount(allVars);
		if ((missing != null) && (!(missing.trim()).equals(""))
				&& (missingCount == varCount))
			return Double.parseDouble(missing);
		Double sum = 0D;
		Boolean flag = false;
		for (final AbstractAnswerBean var : allVars) {
			Object value = valueOf(var);
			if (!isMissing(var)) {
				if ((reverseVars != null) && reverseVars.contains(var))
					value = reverseValue(var);
				try {
					sum = sum + Double.parseDouble(value + "");
					flag = true;
				} catch (NumberFormatException exp) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("converting {} value {} to number failed",
								var.getVariableName(), value);
					}
				}
			}
		}
		if (!flag)
			return Double.parseDouble(missing);
		final Double data = sum / (varCount - missingCount);
		return data;
	}
	public synchronized Object sumValue(final List<AbstractAnswerBean> vars) {
		final List<AbstractAnswerBean> allVars = vars;
		int sum = 0;
		for (final AbstractAnswerBean var : allVars) {
			Object value = valueOf(var);
				if (value==null){
					sum = sum + Integer.parseInt("0");
				}
				if (value!=null && !value.toString().isEmpty()){
					try {
						sum = sum + Integer.parseInt(value.toString());
					} catch (NumberFormatException exp) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("sumValue converting {} value {} to number failed",
									var.getVariableName(), value);
						}
					}
				}
		}
		return sum;
	}
	public synchronized Object valueCounter(final List<AbstractAnswerBean> vars) {
		final List<AbstractAnswerBean> allVars = vars;
		int counter = 0;
		for (final AbstractAnswerBean var : allVars) {
			Object value = valueOf(var);
				if (value==null){
				}
				if (value!=null && !value.toString().isEmpty()){
					try {
						if (Integer.parseInt(value.toString())>0){
							counter++;
						}
					} catch (NumberFormatException exp) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("valueCounter converting {} value {} to number failed",
									var.getVariableName(), value);
						}
					}
				}
		}
		return counter;
	}
	public synchronized String difference(final String value1, final String value2) {
		int val1 = 0;
		int val2 = 0;
		try {
			if (value1!=null && !value1.isEmpty()){
				val1=Integer.parseInt(value1.toString());
			} else {
				val1=0;
			}
			if (value2!=null && !value2.isEmpty()){
				val2=Integer.parseInt(value2.toString());
			} else {
				val2=0;
			}
		} catch (NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("converting {} value {} to number failed");
			}
		}
		return ""+(val1-val2);
	}
	public Object locate() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext
				.getRequest();
		final String ip = httpServletRequest.getRemoteAddr();
		final String completeUrl = "http://ipinfo.io/"+ ip + "/city";
		String result = callURL(completeUrl);
		result = result.replace('\n', ' ');
		result = result.replace('\"', ' ');
		result = result.trim();
		return result;
	}
	public synchronized String userAgent() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext
				.getRequest();
		return httpServletRequest.getHeader("User-Agent");
	}
	public synchronized boolean isMobile() {
		return userAgent().contains("Mobi");
	}
	public String baseUrl() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext
				.getRequest();
//		final String url = httpServletRequest.getRequestURL().toString();
//		final String uri = httpServletRequest.getRequestURI();
		final String referer = httpServletRequest.getHeader("referer");
		final String forwarded = httpServletRequest.getHeader("X-Forwarded-Host");
//		Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
//			final String headerValue = httpServletRequest.getHeader(name);
		String back = referer;
		if(back == null)back = forwarded;
		if(back == null)back = "UNKOWN";
		return back;
	}
	public boolean startwith(final String haystack,final String pattern){
		if(haystack == null)return false;
		if(pattern == null)return false;
		if(haystack.startsWith(pattern))return true;
		return false;
	}
	public boolean contains(final String haystack,final String pattern){
		if(haystack == null)return false;
		if(pattern == null)return false;
		if(haystack.contains(pattern))return true;
		return false;
	}
	public Object temperatur(final String city) {
		final String completeUrl = "http://api.openweathermap.org/data/2.5/weather?q="+ city + "&mode=xml&units=metric";
		try {
			final SAXParserFactory spfac = SAXParserFactory.newInstance();
			final SAXParser sp = spfac.newSAXParser();
			final class ContentHandler extends DefaultHandler{
				private String temperature;
				public String getTemperatur(){
					return this.temperature;
				}
				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					super.startElement(uri, localName, qName, attributes);
					if(qName.equals("temperature")){
						String current = attributes.getValue("value");
						String min = attributes.getValue("min");
						String max = attributes.getValue("max");
						String unit = attributes.getValue("unit");
						this.temperature = current;
					}
				}
			}
			final ContentHandler handler = new ContentHandler();
			sp.parse(completeUrl, handler);
			return handler.getTemperatur();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private synchronized String callURL(String myURL) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
			in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:" + myURL,
					e);
		}
		return sb.toString();
	}
	public String injectExternal(final String relativePath,final String filename,final String url){
		final String content = callURL(url);
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final String absolutePath = externalContext.getRealPath("/"+relativePath+"/"+filename);
		final File file = new File(absolutePath);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inject {} to {}",url,absolutePath);
		}
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}
	public synchronized UIComponent retrieveElementByID(final String id) {
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot root = context.getViewRoot();
		final UIComponent[] found = new UIComponent[1];
		root.visitTree(new FullVisitContext(context), new VisitCallback() {
			@Override
			public VisitResult visit(VisitContext context, UIComponent component) {
				if (component != null && component.getId() != null && component.getId().equals(id)) {
					found[0] = component;
					return VisitResult.COMPLETE;
				}
				return VisitResult.ACCEPT;
			}
		});
		return found[0];
	}
	private synchronized UICalendarLegend getLegend(final UICalendar calendar) {
		if (calendar == null)
			return null;
		final UIComponent tmp = calendar.getFacet("javax.faces.component.COMPOSITE_FACET_NAME");
		if (tmp == null)
			return null;
		final List<UIComponent> childs = tmp.getChildren();
		for (final UIComponent child : childs) {
			if ((UICalendarSheet.class).isAssignableFrom(child.getClass())) {
				final UIComponent legendFacet = ((UICalendarSheet)child).getFacet("legend");
				if (legendFacet != null) {
					if ((UICalendarLegend.class).isAssignableFrom(legendFacet.getClass()))
						return ((UICalendarLegend) legendFacet);
				}
			}
		}
		return null;
	}
	private synchronized Map<Integer, String> getLegendLabels(final UICalendarLegend calendarLegend) {
		if (calendarLegend == null)
			return null;
		final UIComponent tmp = calendarLegend.getFacet("javax.faces.component.COMPOSITE_FACET_NAME");
		if (tmp == null)
			return null;
		final List<UIComponent> items = calendarLegend.getChildren();
		if (items != null) {
			Map<Integer, String> back = new HashMap<Integer, String>();
			for(final UIComponent item : items){
				if ((UISelectItem.class).isAssignableFrom(item.getClass())) {
					final UISelectItem selectItem = ((UISelectItem)item);
					final String value = selectItem.getItemValue()+"";
					final String label = selectItem.getItemLabel();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("legend item : {} = {}",value,label);
					}
					back.put(new Integer(value.replaceAll("Slot", "")),label);
				}
			}
			return back;
		}
		return null;
	}
	private synchronized List<UICalendarItem> getItems(final UICalendar calendar) {
		if (calendar == null)
			return null;
		final UIComponent tmp = calendar.getFacet("javax.faces.component.COMPOSITE_FACET_NAME");
		if (tmp == null)
			return null;
		final List<UIComponent> childs = tmp.getChildren();
		for (final UIComponent child : childs) {
			if ((UICalendarSheet.class).isAssignableFrom(child.getClass())) {
				List<UICalendarItem> back = new ArrayList<UICalendarItem>();
				final UICalendarSheet sheet = (UICalendarSheet) child;
				for (final UIComponent sheetChild : sheet.getChildren()) {
					if ((UICalendarItem.class).isAssignableFrom(sheetChild.getClass())) {
						back.add((UICalendarItem) sheetChild);
					}
				}
				return back;
			}
		}
		return null;
	}
	private synchronized Map<Integer, BooleanValueTypeBean> getSlots(final UICalendarItem calendarItem) {
		if (calendarItem == null)
			return null;
		final Map<String, Object> attributes = calendarItem.getAttributes();
		if (attributes != null) {
			Map<Integer, BooleanValueTypeBean> back = new HashMap<Integer, BooleanValueTypeBean>();
			for(int a = 1;a<=20;a++){
				final IAnswerBean variable = calendarItem.getSlot(a);
				if(variable == null)continue;
				if ((BooleanValueTypeBean.class).isAssignableFrom(variable.getClass()))
				back.put(new Integer(a), (BooleanValueTypeBean)variable);
			}
			return back;
		}
		return null;
	}
	public synchronized Map<Integer, Integer> calendarSlotCounts(final UIComponent calendar) {
		if (calendar == null)
			return null;
		if (!(UICalendar.class).isAssignableFrom(calendar.getClass()))
			return null;
		final List<UICalendarItem> items = getItems((UICalendar) calendar);
		if (items != null) {
			final Map<Integer, Integer> back = new HashMap<Integer, Integer>();
			for (final UICalendarItem item : items) {
				final Map<Integer, BooleanValueTypeBean> slots = getSlots(item);
				if (slots != null) {
					for (Map.Entry<Integer, BooleanValueTypeBean> slot : slots.entrySet()) {
						Integer count = null;
						if (back.containsKey(slot.getKey()))
							count = back.get(slot.getKey());
						if (count == null)
							count = new Integer(0);
						if (slot.getValue().getValue())
							back.put(slot.getKey(), count + 1);
					}
				}
			}
			return back;
		}
		return null;
	}
	/**
	 * Generate a random Boolean Decision
	 * @return random boolean
	 */
	public synchronized boolean randomBoolean(){
		return random.nextBoolean();
	}
	/**
	 * Generate a weighted random Boolean Decision
	 * @param percent Value between 1 and 100
	 * @return random boolean
	 */
	public synchronized boolean randomBoolean(final int percent){
		final double weight = ((double)percent)/100;
		final double rnd = random.nextDouble();
		final boolean back =  rnd < weight;
		return back;
	}
	/**
	 * Generate a random Decision from Item set
	 * @return random Item
	 */
	public synchronized String randomOf(final List<String> items) {
		final Random random = new SecureRandom();
		final String selected = items.get(random.nextInt(items.size()));
		return selected;
	}
	public void log(final String message,final Participant participant){
		LOGGER.info("[MESSAGE] ({}) {}",participant.getToken(),message);
	}
}
