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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.xerial.snappy.Snappy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.faces.component.visit.FullVisitContext;
import de.his.zofar.presentation.common.util.BeanHelper;
import de.his.zofar.presentation.surveyengine.AbstractAnswerBean;
import de.his.zofar.presentation.surveyengine.BooleanValueTypeBean;
import de.his.zofar.presentation.surveyengine.NumberValueTypeBean;
import de.his.zofar.presentation.surveyengine.SingleChoiceAnswerOptionTypeBean;
import de.his.zofar.presentation.surveyengine.StringValueTypeBean;
import de.his.zofar.presentation.surveyengine.controller.NavigatorBean;
import de.his.zofar.presentation.surveyengine.controller.SessionController;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendar;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.calendar.UICalendarSheet;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
import de.his.zofar.presentation.surveyengine.util.SystemConfiguration;
import de.his.zofar.service.surveyengine.model.Participant;
import de.his.zofar.service.surveyengine.service.SurveyEngineService;
/**
 * Bean to provide Zofar specific EL - Functions
 *
 * @author meisner dick
 * @version 0.0.1
 */
/**
 * @author meisner
 *
 */
@ManagedBean(name = "zofar")
@ApplicationScoped
public class FunctionProvider implements Serializable {
	private static final long serialVersionUID = -78074230548327907L;
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionProvider.class);
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
	/**
	 * [REVIEW]
	 *
	 * @return
	 * @throws ParseException
	 */
	public synchronized String getTimeStamp() throws ParseException {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Date date = new Date();
		String dateString = simpleDateFormat.format(date);
		return dateString;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 * @throws ParseException
	 */
	public synchronized int getMonth(final AbstractAnswerBean var) throws ParseException {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		int month = 0;
		if (var != null && (StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
			String dateString = ((StringValueTypeBean) var).getValue();
			try {
				Date date = simpleDateFormat.parse(dateString);
				simpleDateFormat.applyPattern("M");
				month = Integer.parseInt(simpleDateFormat.format(date));
			} catch (ParseException e) {
			}
		}
		return month;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 * @throws ParseException
	 */
	public synchronized int getYear(final AbstractAnswerBean var) throws ParseException {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		int year = 0;
		if (var != null && (StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
			String dateString = ((StringValueTypeBean) var).getValue();
			Date date = simpleDateFormat.parse(dateString);
			simpleDateFormat.applyPattern("YYYY");
			year = Integer.parseInt(simpleDateFormat.format(date));
		}
		return year;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 * @throws ParseException
	 */
	public synchronized String getMonthLabel(final AbstractAnswerBean var) throws ParseException {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		String label = "";
		if (var != null && (StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
			String dateString = ((StringValueTypeBean) var).getValue();
			Date date = simpleDateFormat.parse(dateString);
			simpleDateFormat.applyPattern("MMMMM");
			label = simpleDateFormat.format(date);
		}
		return label;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 * @throws ParseException
	 */
	public synchronized int getCalcDate(final AbstractAnswerBean var) throws ParseException {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		int sum = 0;
		if (var != null && (StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
			String dateString = ((StringValueTypeBean) var).getValue();
			Date date = simpleDateFormat.parse(dateString);
			simpleDateFormat.applyPattern("YYYY");
			int year = Integer.parseInt(simpleDateFormat.format(date));
			simpleDateFormat.applyPattern("M");
			int month = Integer.parseInt(simpleDateFormat.format(date));
			sum = ((year * 100) + month);
		}
		return sum;
	}
	/**
	 * [REVIEW]
	 *
	 * @return
	 */
	public synchronized boolean recorderEnabled() {
		final SystemConfiguration system = SystemConfiguration.getInstance();
		return system.record();
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param var
	 * @return
	 */
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
			if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((SingleChoiceAnswerOptionTypeBean) var).getValue();
			}
		}
		return null;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param var
	 * @return
	 */
	public synchronized String labelOf(final AbstractAnswerBean var) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("labelOf : {}", var);
		}
		if (var != null) {
			if ((BooleanValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((BooleanValueTypeBean) var).getValue() + "";
			}
			if ((NumberValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((NumberValueTypeBean) var).getValue() + "";
			}
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((StringValueTypeBean) var).getValue();
			}
			if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var.getClass())) {
				return ((SingleChoiceAnswerOptionTypeBean) var).getLabel();
			}
		}
		return null;
	}
	public synchronized String labelOf(final String varname, final Object value) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("episode labelOf : {} {}", varname, value);
		}
		final AbstractAnswerBean var = JsfUtility.getInstance().evaluateValueExpression(
				FacesContext.getCurrentInstance(), "#{" + varname + "}", AbstractAnswerBean.class);
		if (var != null) {
			if ((BooleanValueTypeBean.class).isAssignableFrom(var.getClass())) {
				BooleanValueTypeBean tmp = (BooleanValueTypeBean) var;
				return tmp.getSessionController().loadLabelMap(tmp.getVariableName(), value + "") + "";
			}
			if ((NumberValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return value + "";
			}
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				return value + "";
			}
			if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var.getClass())) {
				final SurveyEngineService ses = BeanHelper.findBean(SurveyEngineService.class);
				final String back = JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(),
						"#{" + ses.loadLabelsAndConditions(var.getVariableName(), value + "").get("true") + "}",
						String.class);
				return back;
			}
		}
		return null;
	}
	public synchronized String labelOfType(final JsonArray types, final String type, final String language) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("episode labelOf : {} {}", types, type);
		}
		if (types == null)
			return "";
		if (types.isJsonNull())
			return "";
		if (type == null)
			return "";
		String back = "";
		final Iterator<JsonElement> it = types.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject tmpObj = tmp.getAsJsonObject();
			if (tmpObj.get("id").getAsString().contentEquals(type)) {
				back = tmpObj.get("label").getAsJsonObject().get(language).getAsString();
				break;
			}
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 */
	public synchronized String labelOfDropDown(final SingleChoiceAnswerOptionTypeBean var) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("labelOfDropDown : {}", var);
		}
		if (var != null) {
			final Map<String, Object> labels = ((SingleChoiceAnswerOptionTypeBean) var).getLabels();
			if ((labels != null) && (!labels.isEmpty())) {
				final String uid = var.getValueId();
				if ((uid != null) && (labels.containsKey(uid)))
					return (String) labels.get(uid);
			}
		}
		return null;
	}
	/**
	 * [REVIEW]
	 *
	 * @param list1
	 * @param list2
	 * @param <T>
	 * @return
	 */
	private synchronized <T> List<T> mergeLists(final List<T> list1, final List<T> list2) {
		if ((list1 == null) && (list2 == null))
			return null;
		final List<T> list = new ArrayList<T>();
		if (list1 != null)
			list.addAll(list1);
		if (list2 != null)
			list.addAll(list2);
		return list;
	}
	/**
	 * [REVIEW]
	 *
	 * @param keyList
	 * @param valueList
	 * @param <T>
	 * @param <S>
	 * @return
	 */
	private synchronized <T, S> Map<T, S> mergeListsToMap(final List<T> keyList, final List<S> valueList) {
		final Iterator<T> i1 = keyList.iterator();
		final Iterator<S> i2 = valueList.iterator();
		final Map<T, S> map = new HashMap<T, S>();
		while (i1.hasNext() && i2.hasNext()) {
			map.put(i1.next(), i2.next());
		}
		return map;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized List<Object> list() {
		return new ArrayList<Object>();
	}
	public synchronized List<Object> list(final Object element1) {
		return listHelper(element1);
	}
	public synchronized List<Object> list(final Object element1, final Object element2) {
		return listHelper(element1, element2);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3) {
		return listHelper(element1, element2, element3);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4) {
		return listHelper(element1, element2, element3, element4);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5) {
		return listHelper(element1, element2, element3, element4, element5);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6) {
		return listHelper(element1, element2, element3, element4, element5, element6);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148, final Object element149) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148, element149);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148, final Object element149, final Object element150) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148, element149, element150);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148, final Object element149, final Object element150, final Object element151,
			final Object element152, final Object element153, final Object element154, final Object element155,
			final Object element156, final Object element157, final Object element158, final Object element159,
			final Object element160, final Object element161, final Object element162) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148, element149, element150, element151, element152, element153, element154, element155,
				element156, element157, element158, element159, element160, element161, element162);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148, final Object element149, final Object element150, final Object element151,
			final Object element152, final Object element153, final Object element154, final Object element155,
			final Object element156, final Object element157, final Object element158, final Object element159,
			final Object element160, final Object element161, final Object element162, final Object element163,
			final Object element164, final Object element165, final Object element166, final Object element167,
			final Object element168, final Object element169, final Object element170, final Object element171,
			final Object element172, final Object element173, final Object element174) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148, element149, element150, element151, element152, element153, element154, element155,
				element156, element157, element158, element159, element160, element161, element162, element163,
				element164, element165, element166, element167, element168, element169, element170, element171,
				element172, element173, element174);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148, final Object element149, final Object element150, final Object element151,
			final Object element152, final Object element153, final Object element154, final Object element155,
			final Object element156, final Object element157, final Object element158, final Object element159,
			final Object element160, final Object element161, final Object element162, final Object element163,
			final Object element164, final Object element165, final Object element166, final Object element167,
			final Object element168, final Object element169, final Object element170, final Object element171,
			final Object element172, final Object element173, final Object element174, final Object element175,
			final Object element176, final Object element177, final Object element178, final Object element179,
			final Object element180, final Object element181, final Object element182, final Object element183,
			final Object element184, final Object element185, final Object element186, final Object element187,
			final Object element188, final Object element189, final Object element190, final Object element191,
			final Object element192, final Object element193, final Object element194, final Object element195,
			final Object element196, final Object element197, final Object element198, final Object element199,
			final Object element200) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148, element149, element150, element151, element152, element153, element154, element155,
				element156, element157, element158, element159, element160, element161, element162, element163,
				element164, element165, element166, element167, element168, element169, element170, element171,
				element172, element173, element174, element175, element176, element177, element178, element179,
				element180, element181, element182, element183, element184, element185, element186, element187,
				element188, element189, element190, element191, element192, element193, element194, element195,
				element196, element197, element198, element199, element200);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148, final Object element149, final Object element150, final Object element151,
			final Object element152, final Object element153, final Object element154, final Object element155,
			final Object element156, final Object element157, final Object element158, final Object element159,
			final Object element160, final Object element161, final Object element162, final Object element163,
			final Object element164, final Object element165, final Object element166, final Object element167,
			final Object element168, final Object element169, final Object element170, final Object element171,
			final Object element172, final Object element173, final Object element174, final Object element175,
			final Object element176, final Object element177, final Object element178, final Object element179,
			final Object element180, final Object element181, final Object element182, final Object element183,
			final Object element184, final Object element185, final Object element186, final Object element187,
			final Object element188, final Object element189, final Object element190, final Object element191,
			final Object element192, final Object element193, final Object element194, final Object element195,
			final Object element196, final Object element197, final Object element198, final Object element199,
			final Object element200, final Object element201, final Object element202, final Object element203,
			final Object element204, final Object element205, final Object element206, final Object element207,
			final Object element208, final Object element209, final Object element210, final Object element211,
			final Object element212, final Object element213, final Object element214, final Object element215,
			final Object element216, final Object element217, final Object element218, final Object element219,
			final Object element220, final Object element221, final Object element222, final Object element223,
			final Object element224, final Object element225, final Object element226, final Object element227,
			final Object element228, final Object element229, final Object element230, final Object element231,
			final Object element232, final Object element233, final Object element234, final Object element235,
			final Object element236, final Object element237, final Object element238, final Object element239,
			final Object element240, final Object element241, final Object element242, final Object element243,
			final Object element244, final Object element245, final Object element246) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148, element149, element150, element151, element152, element153, element154, element155,
				element156, element157, element158, element159, element160, element161, element162, element163,
				element164, element165, element166, element167, element168, element169, element170, element171,
				element172, element173, element174, element175, element176, element177, element178, element179,
				element180, element181, element182, element183, element184, element185, element186, element187,
				element188, element189, element190, element191, element192, element193, element194, element195,
				element196, element197, element198, element199, element200, element201, element202, element203,
				element204, element205, element206, element207, element208, element209, element210, element211,
				element212, element213, element214, element215, element216, element217, element218, element219,
				element220, element221, element222, element223, element224, element225, element226, element227,
				element228, element229, element230, element231, element232, element233, element234, element235,
				element236, element237, element238, element239, element240, element241, element242, element243,
				element244, element245, element246);
	}
	/**
	 * [REVIEW]
	 *
	 * @param list
	 * @param pattern
	 * @return
	 */
	public synchronized boolean inList(final List<Object> list, final Object pattern) {
		if (list == null)
			return false;
		if (pattern == null)
			return false;
		if (list.isEmpty())
			return false;
		return list.contains(pattern);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized Map<String, String> map() {
		return new LinkedHashMap<String, String>();
	}
	/**
	 * [REVIEW]
	 *
	 * @param toParse
	 * @return
	 */
	public synchronized Map<String, String> map(final String toParse) {
		return this.map(toParse, "=", ",");
	}
	/**
	 * [REVIEW]
	 *
	 * @param toParse
	 * @param pairSplit
	 * @param itemSplit
	 * @return
	 */
	public synchronized Map<String, String> map(final String toParse, final String pairSplit, final String itemSplit) {
		if (toParse == null)
			return null;
		if (toParse.equals(""))
			return null;
		if (pairSplit == null)
			return null;
		if (pairSplit.equals(""))
			return null;
		if (itemSplit == null)
			return null;
		if (itemSplit.equals(""))
			return null;
		Map<String, String> back = new LinkedHashMap<String, String>();
		if (!toParse.contains(itemSplit)) {
			if (toParse.contains(pairSplit)) {
				String[] pair = toParse.split(Pattern.quote(pairSplit));
				if ((pair != null) && (pair.length == 2)) {
					back.put(pair[0], pair[1]);
				}
			}
		} else {
			String[] items = toParse.split(Pattern.quote(itemSplit));
			if (items != null) {
				for (final String item : items) {
					if (item.contains(pairSplit)) {
						String[] pair = item.split(Pattern.quote(pairSplit));
						if ((pair != null) && (pair.length == 2)) {
							back.put(pair[0], pair[1]);
						}
					}
				}
			}
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param map
	 * @param key
	 * @return
	 */
	public synchronized String getFromMap(final Map<String, String> map, final String key) {
		if (map == null)
			return null;
		if (key == null)
			return null;
		if (!map.containsKey(key))
			return null;
		return map.get(key);
	}
	/**
	 * [REVIEW]
	 *
	 * @param objects
	 * @return
	 */
	private List<Object> listHelper(Object... objects) {
		return (List<Object>) Arrays.asList(objects);
	}
	/**
	 * [REVIEW]
	 *
	 * @param toSplit
	 * @param splitBy
	 * @return
	 */
	public synchronized List<Object> explode(final String toSplit, final String splitBy) {
		return listHelper(toSplit.split(splitBy));
	}
	/**
	 * [REVIEW]
	 *
	 * @param stack
	 * @return
	 */
	public synchronized List<Object> asList(final Stack<Object> stack) {
		return (List<Object>) new ArrayList(stack);
	}
	/**
	 * [REVIEW]
	 *
	 * @param list
	 * @param toAdd
	 * @return
	 */
	public synchronized List<Object> addList(List<Object> list, final Object toAdd) {
		if (list == null)
			list = new ArrayList<Object>();
		if (toAdd != null)
			list.add(toAdd);
		return list;
	}
	/**
	 * [REVIEW]
	 *
	 * @param list
	 * @return
	 */
	public synchronized List<?> reverseList(final List<?> list) {
		if (list == null)
			return null;
		final List<?> reversedValues = new ArrayList<Object>(list);
		Collections.reverse(reversedValues);
		return reversedValues;
	}
	/**
	 * [REVIEW]
	 *
	 * @param element1
	 * @param element2
	 * @return
	 */
	public synchronized String concat(final String element1, final String element2) {
		return concatHelper(element1, element2);
	}
	public synchronized String concat(final String element1, final String element2, final String element3) {
		return concatHelper(element1, element2, element3);
	}
	public synchronized String concat(final String element1, final String element2, final String element3,
			final String element4) {
		return concatHelper(element1, element2, element3, element4);
	}
	public synchronized String concat(final String element1, final String element2, final String element3,
			final String element4, final String element5) {
		return concatHelper(element1, element2, element3, element4, element5);
	}
	public synchronized String concat(final String element1, final String element2, final String element3,
			final String element4, final String element5, final String element6) {
		return concatHelper(element1, element2, element3, element4, element5, element6);
	}
	public synchronized String concat(final String element1, final String element2, final String element3,
			final String element4, final String element5, final String element6, final String element7) {
		return concatHelper(element1, element2, element3, element4, element5, element6, element7);
	}
	public synchronized String concat(final String element1, final String element2, final String element3,
			final String element4, final String element5, final String element6, final String element7,
			final String element8) {
		return concatHelper(element1, element2, element3, element4, element5, element6, element7, element8);
	}
	public synchronized String concat(final String element1, final String element2, final String element3,
			final String element4, final String element5, final String element6, final String element7,
			final String element8, final String element9) {
		return concatHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9);
	}
	public synchronized String concat(final String element1, final String element2, final String element3,
			final String element4, final String element5, final String element6, final String element7,
			final String element8, final String element9, final String element10) {
		return concatHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146, final Object element147,
			final Object element148, final Object element149, final Object element150, final Object element151,
			final Object element152, final Object element153, final Object element154, final Object element155,
			final Object element156, final Object element157, final Object element158, final Object element159,
			final Object element160, final Object element161) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146, element147,
				element148, element149, element150, element151, element152, element153, element154, element155,
				element156, element157, element158, element159, element160, element161);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47,
			final Object element48, final Object element49, final Object element50, final Object element51,
			final Object element52, final Object element53, final Object element54, final Object element55,
			final Object element56, final Object element57, final Object element58, final Object element59,
			final Object element60, final Object element61, final Object element62, final Object element63,
			final Object element64, final Object element65, final Object element66, final Object element67,
			final Object element68, final Object element69, final Object element70, final Object element71,
			final Object element72, final Object element73, final Object element74, final Object element75,
			final Object element76, final Object element77, final Object element78, final Object element79,
			final Object element80, final Object element81, final Object element82, final Object element83,
			final Object element84, final Object element85, final Object element86, final Object element87,
			final Object element88, final Object element89, final Object element90, final Object element91,
			final Object element92, final Object element93, final Object element94, final Object element95,
			final Object element96, final Object element97, final Object element98, final Object element99,
			final Object element100, final Object element101, final Object element102, final Object element103,
			final Object element104, final Object element105, final Object element106, final Object element107,
			final Object element108, final Object element109, final Object element110, final Object element111,
			final Object element112, final Object element113, final Object element114, final Object element115,
			final Object element116, final Object element117, final Object element118, final Object element119,
			final Object element120, final Object element121, final Object element122, final Object element123,
			final Object element124, final Object element125, final Object element126, final Object element127,
			final Object element128, final Object element129, final Object element130, final Object element131,
			final Object element132, final Object element133, final Object element134, final Object element135,
			final Object element136, final Object element137, final Object element138, final Object element139,
			final Object element140, final Object element141, final Object element142, final Object element143,
			final Object element144, final Object element145, final Object element146) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47, element48, element49, element50, element51, element52, element53, element54,
				element55, element56, element57, element58, element59, element60, element61, element62, element63,
				element64, element65, element66, element67, element68, element69, element70, element71, element72,
				element73, element74, element75, element76, element77, element78, element79, element80, element81,
				element82, element83, element84, element85, element86, element87, element88, element89, element90,
				element91, element92, element93, element94, element95, element96, element97, element98, element99,
				element100, element101, element102, element103, element104, element105, element106, element107,
				element108, element109, element110, element111, element112, element113, element114, element115,
				element116, element117, element118, element119, element120, element121, element122, element123,
				element124, element125, element126, element127, element128, element129, element130, element131,
				element132, element133, element134, element135, element136, element137, element138, element139,
				element140, element141, element142, element143, element144, element145, element146);
	}
	public synchronized List<Object> list(final Object element1, final Object element2, final Object element3,
			final Object element4, final Object element5, final Object element6, final Object element7,
			final Object element8, final Object element9, final Object element10, final Object element11,
			final Object element12, final Object element13, final Object element14, final Object element15,
			final Object element16, final Object element17, final Object element18, final Object element19,
			final Object element20, final Object element21, final Object element22, final Object element23,
			final Object element24, final Object element25, final Object element26, final Object element27,
			final Object element28, final Object element29, final Object element30, final Object element31,
			final Object element32, final Object element33, final Object element34, final Object element35,
			final Object element36, final Object element37, final Object element38, final Object element39,
			final Object element40, final Object element41, final Object element42, final Object element43,
			final Object element44, final Object element45, final Object element46, final Object element47) {
		return listHelper(element1, element2, element3, element4, element5, element6, element7, element8, element9,
				element10, element11, element12, element13, element14, element15, element16, element17, element18,
				element19, element20, element21, element22, element23, element24, element25, element26, element27,
				element28, element29, element30, element31, element32, element33, element34, element35, element36,
				element37, element38, element39, element40, element41, element42, element43, element44, element45,
				element46, element47);
	}
	/**
	 * [REVIEW]
	 *
	 * @param objects
	 * @return
	 */
	private String concatHelper(String... objects) {
		if (objects == null)
			return null;
		return StringUtils.join(objects);
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 */
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
		if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var.getClass())) {
			final SingleChoiceAnswerOptionTypeBean tmp = (SingleChoiceAnswerOptionTypeBean) var;
			final Map<String, Object> options = tmp.getOptionValues();
			List<Object> valueList = new ArrayList<Object>(options.values());
			valueList.removeAll(missingsOf(var));
			return valueList;
		}
		return null;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 */
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
		if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var.getClass())) {
			final SingleChoiceAnswerOptionTypeBean tmp = (SingleChoiceAnswerOptionTypeBean) var;
			final List<Object> missingList = new ArrayList<Object>();
			final Map<String, Object> options = tmp.getMissingValues();
			if (options != null)
				missingList.addAll(options.values());
			missingList.add("");
			return missingList;
		}
		return null;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param var
	 * @return
	 */
	public synchronized Boolean isMissing(final AbstractAnswerBean var) {
		if (var != null) {
			final List<?> missingList = missingsOf(var);
			final Object value = valueOf(var);
			if ((value != null) && (missingList != null))
				return missingList.contains(value);
		}
		return false;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @param participant
	 * @return
	 */
	public synchronized Boolean isSet(final String var, final Participant participant) {
		if (var == null)
			return false;
		if (participant == null)
			return false;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("is Set {} for {}", var, participant.getToken());
		}
		if (participant.getSurveyData().containsKey(var))
			return true;
		return false;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @param participant
	 * @return
	 */
	public synchronized Boolean isBooleanSetOld(final AbstractAnswerBean var, final Participant participant) {
		if (var == null)
			return false;
		if (participant == null)
			return false;
		if (participant.getSurveyData().containsKey(var.getVariableName()) && var.getStringValue().equals("true")) {
			return true;
		}
		return false;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @param participant
	 * @return
	 */
	public synchronized Boolean isBooleanSet(final String var, final Participant participant) {
		if (var == null)
			return false;
		if (participant == null)
			return false;
		if (participant.getSurveyData().containsKey(var)
				&& participant.getSurveyData().get(var).getValue().equals("true")) {
			return true;
		}
		return false;
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @param participant
	 * @return
	 */
	public synchronized Integer isSetCounter(final List<AbstractAnswerBean> vars, final Participant participant) {
		if (vars != null) {
			int back = 0;
			for (final Object var : vars) {
				if (var == null)
					continue;
				if (((AbstractAnswerBean.class).isAssignableFrom(var.getClass()))
						&& (isBooleanSetOld((AbstractAnswerBean) var, participant)))
					back = back + 1;
			}
			return back;
		}
		return 0;
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @param participant
	 * @return
	 */
	public synchronized Integer isSetCounterNew(final List<String> vars, final Participant participant) {
		if (vars != null) {
			int back = 0;
			for (final Object var : vars) {
				if (var == null)
					continue;
				if (((String.class).isAssignableFrom(var.getClass())) && (isBooleanSet((String) var, participant)))
					back = back + 1;
			}
			return back;
		}
		return 0;
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @return
	 */
	public synchronized Integer missingCount(final List<AbstractAnswerBean> vars) {
		if (vars != null) {
			int back = 0;
			for (final Object var : vars) {
				if (var == null)
					continue;
				if (((AbstractAnswerBean.class).isAssignableFrom(var.getClass()))
						&& (isMissing((AbstractAnswerBean) var)))
					back = back + 1;
			}
			return back;
		}
		return 0;
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @return
	 */
	public synchronized Object reverseValue(final AbstractAnswerBean var) {
		if (var == null)
			return null;
		final List<?> values = optionsOf(var);
		final Map<?, ?> map = mergeListsToMap(values, reverseList(values));
		return recodeValue(var, map, valueOf(var));
	}
	/**
	 * [REVIEW]
	 *
	 * @param var
	 * @param mapping
	 * @param alternative
	 * @return
	 */
	public synchronized Object recodeValue(final AbstractAnswerBean var, final Map<?, ?> mapping,
			final Object alternative) {
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
	/**
	 * [REVIEW]
	 *
	 * @param option1
	 * @param option2
	 * @return
	 */
	public synchronized Object chooseSetted(final SingleChoiceAnswerOptionTypeBean option1,
			final SingleChoiceAnswerOptionTypeBean option2) {
		String value1 = null;
		if (option1 != null) {
			value1 = option1.getValue();
		}
		String value2 = null;
		if (option2 != null) {
			value2 = option2.getValue();
		}
		if ((value1 == null) && (value2 == null))
			return null;
		if ((value1 != null) && (value2 == null))
			return value1;
		if ((value1 == null) && (value2 != null))
			return value2;
		value1 = value1.trim();
		value2 = value2.trim();
		if ((value1.equals("")) && (value2.equals("")))
			return "";
		if ((value1.equals("")) && (!value2.equals("")))
			return option2.getStringValue();
		if ((!value1.equals("")) && (value2.equals("")))
			return option1.getStringValue();
		if ((!value1.equals("")) && (!value2.equals("")))
			return option1.getStringValue() + option2.getStringValue();
		return "";
	}
	/**
	 * [REVIEW]
	 *
	 * @param option1
	 * @param option2
	 * @return
	 */
	public synchronized Object chooseSettedDropDown(final SingleChoiceAnswerOptionTypeBean option1,
			final SingleChoiceAnswerOptionTypeBean option2) {
		String value1 = null;
		if (option1 != null) {
			value1 = option1.getValueId();
		}
		String value2 = null;
		if (option2 != null) {
			value2 = option2.getValueId();
		}
		if ((value1 == null) && (value2 == null))
			return null;
		if ((value1 != null) && (value2 == null))
			return value1;
		if ((value1 == null) && (value2 != null))
			return value2;
		if ((value1.equals("")) && (value2.equals("")))
			return null;
		if ((!value1.equals("")) && (value2.equals("")))
			return value1;
		if ((value1.equals("")) && (!value2.equals("")))
			return value2;
		if ((!value1.equals("")) && (!value2.equals(""))) {
			final String firstId = option2.getOptionValues().keySet().toArray()[0] + "";
			if (!value2.equals(firstId))
				return value2;
			else if (!value1.equals(firstId))
				return value1;
			else
				return firstId;
		}
		return null;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param condition
	 * @param thenElement
	 * @param elseElement
	 * @return
	 */
	public synchronized Object ifthenelse(final Boolean condition, final Object thenElement, final Object elseElement) {
		if (condition)
			return thenElement;
		return elseElement;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public synchronized Double asNumber(final Object input) throws Exception {
		if (input == null)
			return 0D;
		Object toConvert = input;
		if ((AbstractAnswerBean.class).isAssignableFrom(input.getClass())) {
			toConvert = valueOf((AbstractAnswerBean) input);
		}
		try {
			return Double.parseDouble(toConvert + "");
		} catch (NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("keine Zahl {}", input);
			}
		}
		return 0D;
	}
	public synchronized Double asNumberNeu(final Object input, SessionController session) throws Exception {
		try {
			if (input == null)
				return 0D;
			Object toConvert = null;
			if ((AbstractAnswerBean.class).isAssignableFrom(input.getClass())) {
				final FacesContext fc = FacesContext.getCurrentInstance();
				toConvert = JsfUtility.getInstance().evaluateValueExpression(fc, String.valueOf(input), String.class);
			} else {
				toConvert = input;
			}
			if (toConvert == null)
				return 0D;
			toConvert = toConvert + "".trim();
			if (toConvert.equals(""))
				return 0D;
			try {
				return Double.parseDouble(toConvert + "");
			} catch (NumberFormatException exp) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("keine Zahl {}", input);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0D;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param input
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public synchronized Integer asInteger(final Object input, SessionController session) throws Exception {
		return asNumberNeu(input, session).intValue();
	}
	/**
	 * [REVIEW]
	 *
	 * @param input
	 * @param digits
	 * @return
	 * @throws Exception
	 */
	public Double asNumber(final Object input, final int digits) throws Exception {
		if (digits <= 0)
			return this.asNumber(input);
		final Double back = Double
				.parseDouble(new DecimalFormat("#." + StringUtils.repeat("#", digits), customFormatSymbols)
						.format(this.asNumber(input)));
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public Long toLong(final Object input) throws Exception {
		if (input == null)
			return 0L;
		Object toConvert = input;
		if ((Number.class).isAssignableFrom(input.getClass()))
			toConvert = ((Number) input).longValue();
		try {
			return Long.parseLong(toConvert + "");
		} catch (NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("keine Zahl {}", input);
			}
		}
		return 0L;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public Integer toInteger(final Object input) throws Exception {
		if (input == null)
			return 0;
		Object toConvert = input;
		if ((Number.class).isAssignableFrom(input.getClass()))
			toConvert = ((Number) input).longValue();
		if (toConvert.equals(""))
			return 0;
		try {
			return Integer.parseInt(toConvert + "");
		} catch (NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("keine Zahl {}", input);
			}
		}
		return 0;
	}
	/**
	 * [REVIEW]
	 *
	 * @param value1
	 * @param value2
	 * @return
	 */
	public synchronized Integer minInt(final Integer value1, final Integer value2) {
		return Math.min(value1, value2);
	}
	/**
	 * [REVIEW]
	 *
	 * @param value1
	 * @param value2
	 * @return
	 */
	public synchronized Integer maxInt(final Integer value1, final Integer value2) {
		return Math.max(value1, value2);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param varname
	 * @param expression
	 */
	public synchronized final void assign(final String varname, final Object expression) {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.put(varname, expression);
	}
	/**
	 * [REVIEW]
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean xor(final boolean a, final boolean b) {
		return (a ^ b);
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @param missing
	 * @return
	 */
	public synchronized Object scaleValue(final List<AbstractAnswerBean> vars, final String missing) {
		return scaleValue(vars, null, missing);
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @param reverseVars
	 * @param missing
	 * @return
	 */
	public synchronized Object scaleValue(final List<AbstractAnswerBean> vars,
			final List<AbstractAnswerBean> reverseVars, final String missing) {
		final List<AbstractAnswerBean> allVars = mergeLists(vars, reverseVars);
		final Integer varCount = allVars.size();
		final Integer missingCount = missingCount(allVars);
		if ((missing != null) && (!(missing.trim()).equals("")) && (missingCount == varCount))
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
						LOGGER.debug("converting {} value {} to number failed", var.getVariableName(), value);
					}
				}
			}
		}
		if (!flag)
			return Double.parseDouble(missing);
		final Double data = sum / (varCount - missingCount);
		return data;
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @return
	 */
	public synchronized Object sumValue(final List<AbstractAnswerBean> vars) {
		final List<AbstractAnswerBean> allVars = vars;
		int sum = 0;
		for (final AbstractAnswerBean var : allVars) {
			final Object value = valueOf(var);
			if (value == null) {
				sum = sum + Integer.parseInt("0");
			}
			if (value != null && !value.toString().isEmpty()) {
				try {
					sum = sum + Integer.parseInt(value.toString());
				} catch (NumberFormatException exp) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("sumValue converting {} value {} to number failed", var.getVariableName(), value);
					}
				}
			}
		}
		return sum;
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @return
	 */
	public synchronized Object sumDoubleValue(final List<AbstractAnswerBean> vars) {
		final List<AbstractAnswerBean> allVars = vars;
		double sum = 0;
		for (final AbstractAnswerBean var : allVars) {
			Object value = valueOf(var);
			if (value == null) {
				sum = sum + Double.parseDouble("0");
			}
			if (value != null && !value.toString().isEmpty()) {
				try {
					sum = sum + Double.parseDouble(value.toString());
				} catch (NumberFormatException exp) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("sumValue converting {} value {} to number failed", var.getVariableName(), value);
					}
				}
			}
		}
		return sum;
	}
	/**
	 * [REVIEW]
	 *
	 * @param vars
	 * @return
	 */
	public synchronized Object valueCounter(final List<AbstractAnswerBean> vars) {
		final List<AbstractAnswerBean> allVars = vars;
		int counter = 0;
		for (final AbstractAnswerBean var : allVars) {
			Object value = valueOf(var);
			if (value == null) {
			}
			if (value != null && !value.toString().isEmpty()) {
				try {
					if (Integer.parseInt(value.toString()) > 0) {
						counter++;
					}
				} catch (NumberFormatException exp) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("valueCounter converting {} value {} to number failed", var.getVariableName(),
								value);
					}
				}
			}
		}
		return counter;
	}
	/**
	 * [REVIEW]
	 *
	 * @param value1
	 * @param value2
	 * @return
	 */
	public synchronized String difference(final String value1, final String value2) {
		int val1 = 0;
		int val2 = 0;
		try {
			if (value1 != null && !value1.isEmpty()) {
				val1 = Integer.parseInt(value1.toString());
			} else {
				val1 = 0;
			}
			if (value2 != null && !value2.isEmpty()) {
				val2 = Integer.parseInt(value2.toString());
			} else {
				val2 = 0;
			}
		} catch (NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("converting {} value {} to number failed");
			}
		}
		return "" + (val1 - val2);
	}
	/**
	 * [REVIEW]
	 *
	 * @param content
	 * @param pattern
	 * @param replacement
	 * @return
	 */
	public synchronized String replace(final String content, final String pattern, final String replacement) {
		if (content == null)
			return null;
		if (pattern == null)
			return content;
		if (replacement == null)
			return content;
		return content.replaceAll(Pattern.quote(pattern), replacement);
	}
	/**
	 * [REVIEW]
	 *
	 * @param content
	 * @return
	 */
	public synchronized String unescapeSpecialChars(final String content) {
		if (content == null)
			return null;
		return StringEscapeUtils.unescapeHtml4(content);
	}
	/**
	 * [REVIEW]
	 *
	 * @return
	 */
	public Object locate() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		final String ip = httpServletRequest.getRemoteAddr();
		final String completeUrl = "http://ipinfo.io/" + ip + "/city";
		String result = callURL(completeUrl);
		result = result.replace('\n', ' ');
		result = result.replace('\"', ' ');
		result = result.trim();
		return result;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized String userAgent() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		return httpServletRequest.getHeader("User-Agent");
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized boolean isMobile() {
		final String userAgent = this.userAgent();
		if (userAgent != null)
			return userAgent.contains("Mobi");
		return false;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public String baseUrl() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		final String referer = httpServletRequest.getHeader("referer");
		final String forwarded = httpServletRequest.getHeader("X-Forwarded-Host");
		String back = referer;
		if (back == null)
			back = forwarded;
		if (back == null)
			back = "UNKOWN";
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @return
	 */
	public String hostUrl() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		String back = httpServletRequest.getServerName();
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @return
	 */
	public String hostPort() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		String back = httpServletRequest.getServerPort() + "";
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @return
	 */
	public String hostProtocol() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		String back = httpServletRequest.getProtocol();
		if (back.contains("/"))
			back = back.substring(0, back.indexOf('/'));
		back = back.toLowerCase();
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param haystack
	 * @param pattern
	 * @return
	 */
	public boolean startwith(final String haystack, final String pattern) {
		if (haystack == null)
			return false;
		if (pattern == null)
			return false;
		if (haystack.startsWith(pattern))
			return true;
		return false;
	}
	/**
	 * [REVIEW]
	 *
	 * @param haystack
	 * @param pattern
	 * @return
	 */
	public boolean contains(final String haystack, final String pattern) {
		if (haystack == null)
			return false;
		if (pattern == null)
			return false;
		if (haystack.contains(pattern))
			return true;
		return false;
	}
	/**
	 * [REVIEW]
	 *
	 * @param city
	 * @return
	 */
	public Object temperatur(final String city) {
		final String completeUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city
				+ "&mode=xml&units=metric";
		try {
			final SAXParserFactory spfac = SAXParserFactory.newInstance();
			final SAXParser sp = spfac.newSAXParser();
			final class ContentHandler extends DefaultHandler {
				private String temperature;
				public String getTemperatur() {
					return this.temperature;
				}
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					super.startElement(uri, localName, qName, attributes);
					if (qName.equals("temperature")) {
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
	/**
	 * [REVIEW]
	 *
	 * @param protocol
	 * @param url
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<String, String> summarize(final String protocol, final String url, final String token)
			throws Exception {
		final JsonObject tmp = summarizeHelper(protocol, url, token);
		final Object result = getJsonProperty(tmp, "result");
		final Map<String, String> back = new HashMap<String, String>();
		if ((result != null) && ((JsonObject.class).isAssignableFrom(result.getClass()))) {
			final JsonObject tmpJSON = (JsonObject) result;
			for (Entry<String, JsonElement> item : tmpJSON.entrySet()) {
				final String property = item.getKey();
				final JsonElement value = item.getValue();
				String count = null;
				if (value.isJsonArray()) {
					final JsonArray valueArray = value.getAsJsonArray();
					if ((valueArray.size() == 1)) {
						final JsonElement countElement = valueArray.get(0);
						if (countElement.isJsonObject()) {
							final JsonObject countObj = (JsonObject) countElement;
							if (countObj.keySet().contains("count"))
								count = countObj.get("count").getAsString();
						}
					}
				}
				back.put(property, count);
			}
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param protocol
	 * @param url
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonObject summarizeHelper(final String protocol, final String url, final String token)
			throws Exception {
		final String completeURL = protocol + ":
		final String back = callURL(completeURL, token, token);
		try {
			final String decoded = URLDecoder.decode(back, StandardCharsets.UTF_8.toString());
			final JsonObject jsonObj = new JsonParser().parse(decoded).getAsJsonObject();
			return jsonObj;
		} catch (UnsupportedEncodingException e) {
			throw e;
		}
	}
	/**
	 * [REVIEW]
	 *
	 * @param myURL
	 * @param username
	 * @param password
	 * @return
	 */
	private String callURL1(String myURL, final String username, final String password) {
		WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
		Resource resource = applicationContext.getResource(myURL);
		final StringBuffer result = new StringBuffer();
		try {
			InputStream is = resource.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String back = result.toString();
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param myURL
	 * @return
	 */
	private synchronized String callURL(String myURL) {
		return this.callURL(myURL, null, null);
	}
	/**
	 * [REVIEW]
	 *
	 * @param myURL
	 * @param username
	 * @param password
	 * @return
	 */
	private synchronized String callURL(String myURL, final String username, final String password) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		String authHeaderValue = null;
		if ((username != null) && (!username.contentEquals("")) && (password != null)
				&& (!password.contentEquals(""))) {
			final String auth = username + ":" + password;
			final byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8));
			authHeaderValue = "Basic " + new String(encodedAuth);
		}
		try {
			URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null) {
				((HttpURLConnection) urlConn).setInstanceFollowRedirects(true);
				urlConn.setReadTimeout(15000);
				if (authHeaderValue != null)
					urlConn.setRequestProperty("Authorization", authHeaderValue);
			}
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
			if (in != null)
				in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL:" + myURL, e);
		}
		return sb.toString();
	}
	/**
	 * [REVIEW]
	 *
	 * @param relativePath
	 * @param filename
	 * @param url
	 * @return
	 */
	public String injectExternal(final String relativePath, final String filename, final String url) {
		final String content = callURL(url);
		final FacesContext fc = FacesContext.getCurrentInstance();
		final ExternalContext externalContext = fc.getExternalContext();
		final String absolutePath = externalContext.getRealPath("/" + relativePath + "/" + filename);
		final File file = new File(absolutePath);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inject {} to {}", url, absolutePath);
		}
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filename;
	}
	/**
	 * [REVIEW]
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public synchronized UIComponent retrieveElementByID(final String id) throws Exception {
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
	/**
	 * [REVIEW]
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public synchronized UIComponent findComponent(final String id) throws Exception {
		UIComponent result = null;
		UIComponent root = FacesContext.getCurrentInstance().getViewRoot();
		if (root != null) {
			result = findComponent(root, id);
		}
		return result;
	}
	/**
	 * [REVIEW]
	 *
	 * @param root
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private UIComponent findComponent(UIComponent root, String id) throws Exception {
		UIComponent result = null;
		if (root.getId().equals(id))
			return root;
		for (UIComponent child : root.getChildren()) {
			if (child.getId().equals(id)) {
				result = child;
				break;
			}
			result = findComponent(child, id);
			if (result != null)
				break;
		}
		return result;
	}
	/**
	 * [REVIEW]
	 *
	 * @param calendar
	 * @param columnList
	 * @param rowList
	 * @return
	 * @throws Exception
	 */
	public synchronized String getEpisodes(final UIComponent calendar, final List<Object> columnList,
			final List<Object> rowList) throws Exception {
		if (calendar == null)
			return null;
		final UIComponent tmp = calendar.getFacet("javax.faces.component.COMPOSITE_FACET_NAME");
		if (tmp == null)
			return null;
		final List<UIComponent> childs = tmp.getChildren();
		for (final UIComponent child : childs) {
			if ((UICalendarSheet.class).isAssignableFrom(child.getClass())) {
				final List<UICalendarItem> back = new ArrayList<UICalendarItem>();
				final UICalendarSheet sheet = (UICalendarSheet) child;
				for (final UIComponent sheetChild : sheet.getChildren()) {
					if ((UICalendarItem.class).isAssignableFrom(sheetChild.getClass())) {
						UICalendarItem item = (UICalendarItem) sheetChild;
						back.add(item);
					}
				}
				final Map<Integer, List<List<String>>> beams = new LinkedHashMap<Integer, List<List<String>>>();
				for (int a = 1; a <= 20; a++) {
					List<List<String>> slotBeam = null;
					if (beams.containsKey(a))
						slotBeam = beams.get(a);
					if (slotBeam == null)
						slotBeam = new ArrayList<List<String>>();
					List<String> currentEpisode = new ArrayList<String>();
					int itemIndex = 1;
					for (UICalendarItem item : back) {
						final IAnswerBean variable = item.getSlot(a);
						if (variable != null) {
							final Boolean value = Boolean.valueOf(variable.getStringValue());
							if (value) {
								currentEpisode.add(itemIndex + "");
							} else {
								if (!currentEpisode.isEmpty()) {
									slotBeam.add(currentEpisode);
									currentEpisode = new ArrayList<String>();
								}
							}
						}
						itemIndex = itemIndex + 1;
					}
					beams.put(a, slotBeam);
				}
				final Map<Integer, List<List<String>>> strippedBeams = new LinkedHashMap<Integer, List<List<String>>>();
				for (Map.Entry<Integer, List<List<String>>> slotBeam : beams.entrySet()) {
					List<List<String>> slotPairs = null;
					if (strippedBeams.containsKey(slotBeam.getKey()))
						slotPairs = strippedBeams.get(slotBeam.getKey());
					if (slotPairs == null)
						slotPairs = new ArrayList<List<String>>();
					for (List<String> episode : slotBeam.getValue()) {
						if (episode.isEmpty())
							continue;
						final String startId = episode.get(0);
						final String endId = episode.get(episode.size() - 1);
						final List<String> pair = new ArrayList<String>();
						pair.add(startId);
						pair.add(endId);
						slotPairs.add(pair);
					}
					if ((slotPairs != null) && (!slotPairs.isEmpty())) {
						strippedBeams.put(slotBeam.getKey(), slotPairs);
					}
				}
				return encodeEpisodes(strippedBeams);
			}
		}
		return null;
	}
	/**
	 * [REVIEW]
	 *
	 * @param calendar
	 * @param columnList
	 * @param rowList
	 * @return
	 */
	public synchronized String getEpisodesHTML5(final UIComponent calendar, final List<Object> columnList,
			final List<Object> rowList) {
		if (calendar == null)
			return null;
		final List<UIComponent> items = calendar.getFacet("items").getChildren();
		final List<UICalendarItem> back = new ArrayList<UICalendarItem>();
		for (final UIComponent item : items) {
			if ((UICalendarItem.class).isAssignableFrom(item.getClass())) {
				UICalendarItem calendarItem = (UICalendarItem) item;
				back.add(calendarItem);
			}
		}
		final Map<Integer, List<List<String>>> beams = new LinkedHashMap<Integer, List<List<String>>>();
		for (int a = 1; a <= 20; a++) {
			List<List<String>> slotBeam = null;
			if (beams.containsKey(a))
				slotBeam = beams.get(a);
			if (slotBeam == null)
				slotBeam = new ArrayList<List<String>>();
			List<String> currentEpisode = new ArrayList<String>();
			boolean dirty = false;
			int itemIndex = 1;
			for (final UICalendarItem item : back) {
				final IAnswerBean variable = item.getSlot(a);
				if (variable != null) {
					final Boolean value = Boolean.valueOf(variable.getStringValue());
					if (value) {
						currentEpisode.add(itemIndex + "");
						dirty = true;
					} else {
						if (!currentEpisode.isEmpty()) {
							slotBeam.add(currentEpisode);
							currentEpisode = new ArrayList<String>();
							dirty = false;
						}
					}
				}
				itemIndex = itemIndex + 1;
			}
			if (dirty)
				slotBeam.add(currentEpisode);
			beams.put(a, slotBeam);
		}
		final Map<Integer, List<List<String>>> strippedBeams = new LinkedHashMap<Integer, List<List<String>>>();
		for (final Map.Entry<Integer, List<List<String>>> slotBeam : beams.entrySet()) {
			List<List<String>> slotPairs = null;
			if (strippedBeams.containsKey(slotBeam.getKey()))
				slotPairs = strippedBeams.get(slotBeam.getKey());
			if (slotPairs == null)
				slotPairs = new ArrayList<List<String>>();
			for (final List<String> episode : slotBeam.getValue()) {
				if (episode.isEmpty())
					continue;
				final String startId = episode.get(0);
				final String endId = episode.get(episode.size() - 1);
				final List<String> pair = new ArrayList<String>();
				pair.add(startId);
				pair.add(endId);
				slotPairs.add(pair);
			}
			if ((slotPairs != null) && (!slotPairs.isEmpty())) {
				strippedBeams.put(slotBeam.getKey(), slotPairs);
			}
		}
		try {
			return encodeEpisodes(strippedBeams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * [REVIEW]
	 *
	 * @param eventId
	 * @param start
	 * @param stop
	 * @param meta
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<String, Object> episodeTupel(final Integer eventId, final Object start, final Object stop,
			final Object meta) throws Exception {
		if (eventId == null)
			return null;
		if (start == null)
			return null;
		if (stop == null)
			return null;
		final HashMap<String, Object> back = new HashMap<String, Object>();
		back.put("event", eventId);
		back.put("start", start);
		back.put("stop", stop);
		if (meta != null)
			back.put("meta", meta);
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param variableTupels
	 * @return
	 * @throws Exception
	 */
	public synchronized String getEpisodesFromVariables(final List<Object> variableTupels) throws Exception {
		if (variableTupels == null)
			return null;
		final Map<Integer, List<List<String>>> strippedBeams = new LinkedHashMap<Integer, List<List<String>>>();
		final Map<Integer, List<Map<String, String>>> metaBeams = new LinkedHashMap<Integer, List<Map<String, String>>>();
		for (final Object variableTupel : variableTupels) {
			if ((Map.class).isAssignableFrom(variableTupel.getClass())) {
				final Map<String, Object> tmp = (Map<String, Object>) variableTupel;
				final Integer eventId = (Integer) tmp.get("event");
				final String start = tmp.get("start") + "";
				final String stop = tmp.get("stop") + "";
				final Object meta = tmp.get("meta");
				if (eventId < 0)
					continue;
				if (Integer.parseInt(start) < 0)
					continue;
				if (Integer.parseInt(stop) < 0)
					continue;
				List<List<String>> slotPairs = null;
				if (strippedBeams.containsKey(eventId))
					slotPairs = strippedBeams.get(eventId);
				if (slotPairs == null)
					slotPairs = new ArrayList<List<String>>();
				List<Map<String, String>> slotMeta = null;
				if (metaBeams.containsKey(eventId))
					slotMeta = metaBeams.get(eventId);
				if (slotMeta == null)
					slotMeta = new ArrayList<Map<String, String>>();
				final List<String> pair = new ArrayList<String>();
				pair.add(start);
				pair.add(stop);
				slotPairs.add(pair);
				final Map<String, String> metaMap = new LinkedHashMap<String, String>();
				if (meta != null) {
					if ((Map.class).isAssignableFrom(meta.getClass()))
						metaMap.putAll((Map) meta);
				}
				slotMeta.add(metaMap);
				strippedBeams.put(eventId, slotPairs);
				metaBeams.put(eventId, slotMeta);
			}
		}
		try {
			Map<String, Object> back = new HashMap<String, Object>();
			back.put("data", strippedBeams);
			back.put("meta", metaBeams);
			return encodeEpisodesWithMeta(back);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * [REVIEW]
	 *
	 * @param beams
	 * @return
	 * @throws Exception
	 */
	private String encodeEpisodes(final Map<Integer, List<List<String>>> beams) throws Exception {
		if (beams == null)
			return null;
		final DecimalFormat formatter = new DecimalFormat("000");
		final StringBuffer serializedMap = new StringBuffer();
		boolean first1 = true;
		for (Map.Entry<Integer, List<List<String>>> slotBeam : beams.entrySet()) {
			if (!first1)
				serializedMap.append(";");
			serializedMap.append(slotBeam.getKey() + "#");
			final StringBuffer tmp = new StringBuffer();
			for (List<String> episode : slotBeam.getValue()) {
				for (final String item : episode) {
					tmp.append(formatter.format(Integer.parseInt(item)));
				}
				tmp.append(":");
			}
			String cleaned = tmp.toString();
			cleaned = cleaned.substring(0, cleaned.length() - 1);
			serializedMap.append(cleaned);
			serializedMap.append("");
			first1 = false;
		}
		final String toCompresss = serializedMap.toString();
		final String compressed = compress(toCompresss);
		return compressed;
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedMap
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<Integer, List<List<Integer>>> decodeEpisodes(final String serializedMap) throws Exception {
		if (serializedMap == null)
			return null;
		if (serializedMap.equals(""))
			return null;
		final String decompressed = decompress(serializedMap);
		final Map<Integer, List<List<Integer>>> back = new LinkedHashMap<Integer, List<List<Integer>>>();
		final String[] slotSets = decompressed.split(";");
		if (slotSets != null) {
			for (final String slotSet : slotSets) {
				final String[] slotPair = slotSet.split("#");
				if ((slotPair != null) && (slotPair.length == 2)) {
					final String slotIndexStr = slotPair[0];
					final Integer slotIndex = Integer.parseInt(slotIndexStr);
					final String episodes = slotPair[1];
					final String[] episodePairs = episodes.split(":");
					List<List<Integer>> slotPairs = null;
					if (back.containsKey(slotIndex))
						slotPairs = back.get(slotIndex);
					if (slotPairs == null)
						slotPairs = new ArrayList<List<Integer>>();
					if ((episodePairs != null) && (episodePairs.length > 0)) {
						for (final String pair : episodePairs) {
							final String from = pair.substring(0, 3);
							final String to = pair.substring(3);
							slotPairs.add(Arrays.asList(Integer.parseInt(from), Integer.parseInt(to)));
						}
					}
					if (!slotPairs.isEmpty())
						back.put(slotIndex, slotPairs);
				}
			}
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private String encodeEpisodesWithMeta(final Map<String, Object> data) throws Exception {
		if (data == null)
			return null;
		String toCompress = new String();
		if (data.containsKey("data")) {
			final Map<Integer, List<List<String>>> beams = (Map<Integer, List<List<String>>>) data.get("data");
			final DecimalFormat formatter = new DecimalFormat("000");
			final StringBuffer serializedMap = new StringBuffer();
			boolean first1 = true;
			for (Map.Entry<Integer, List<List<String>>> slotBeam : beams.entrySet()) {
				if (!first1)
					serializedMap.append(";");
				serializedMap.append(slotBeam.getKey() + "#");
				final StringBuffer tmp = new StringBuffer();
				for (List<String> episode : slotBeam.getValue()) {
					for (final String item : episode) {
						tmp.append(formatter.format(Integer.parseInt(item)));
					}
					tmp.append(":");
				}
				String cleaned = tmp.toString();
				cleaned = cleaned.substring(0, cleaned.length() - 1);
				serializedMap.append(cleaned);
				serializedMap.append("");
				first1 = false;
			}
			final String toCompresssData = serializedMap.toString();
			toCompress += "DATA(" + compress(toCompresssData) + ")DATA";
		}
		if (data.containsKey("meta")) {
			final Map<Integer, List<Map<String, String>>> metaBeams = (Map<Integer, List<Map<String, String>>>) data
					.get("meta");
			final StringBuffer serializedMap = new StringBuffer();
			boolean first1 = true;
			for (Map.Entry<Integer, List<Map<String, String>>> metaBeam : metaBeams.entrySet()) {
				if (!first1)
					serializedMap.append(";");
				serializedMap.append(metaBeam.getKey() + "#");
				final StringBuffer tmp = new StringBuffer();
				for (Map<String, String> episodeMeta : metaBeam.getValue()) {
					boolean first2 = true;
					for (final Map.Entry<String, String> episodeMetaItem : episodeMeta.entrySet()) {
						if (!first2)
							tmp.append("^");
						tmp.append(episodeMetaItem.getKey() + "°" + episodeMetaItem.getValue());
						first2 = false;
					}
					tmp.append(":");
				}
				String cleaned = tmp.toString();
				cleaned = cleaned.substring(0, cleaned.length() - 1);
				serializedMap.append(cleaned);
				serializedMap.append("");
				first1 = false;
			}
			final String toCompresssMeta = serializedMap.toString();
			toCompress += "META(" + toCompresssMeta + ")META";
		}
		return compress(toCompress);
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedData
	 * @return
	 */
	private synchronized final JsonArray decodeEpisodesNew(final String serializedData) {
		if (serializedData == null)
			return null;
		try {
			final String decoded = URLDecoder.decode(serializedData, StandardCharsets.UTF_8.toString());
			JsonArray jsonArray = new JsonParser().parse(decoded).getAsJsonArray();
			return jsonArray;
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedData
	 * @param fieldname
	 * @param key
	 * @param rangeStartStr
	 * @param rangeStopStr
	 * @return
	 * @throws Exception
	 */
	public synchronized int countEpisodesInRangeNew(final String serializedData, final String fieldname,
			final String key, String rangeStartStr, String rangeStopStr) throws Exception {
		if (serializedData == null)
			return 0;
		if (serializedData.trim().equals(""))
			return 0;
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		final SimpleDateFormat df1 = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss.SSS'Z'");
		JsonArray episodes = decodeEpisodesNew(serializedData);
		final Date rangeStart = df.parse(rangeStartStr);
		final Date rangeStop = df.parse(rangeStopStr);
		int back = 0;
		final Iterator<JsonElement> it = episodes.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (tmp.isJsonObject()) {
				JsonObject tmpJSON = (JsonObject) tmp;
				if (!tmpJSON.has(fieldname))
					continue;
				final JsonElement tmpStartDateObj = tmpJSON.get("startDate");
				Date tmpStartDate = null;
				if (tmpStartDateObj != null)
					tmpStartDate = df1.parse(tmpStartDateObj.getAsString());
				final JsonElement tmpEndDateObj = tmpJSON.get("endDate");
				Date tmpEndDate = null;
				if (tmpEndDateObj != null)
					tmpEndDate = df1.parse(tmpEndDateObj.getAsString());
				final JsonElement value = tmpJSON.get(fieldname);
				if ((value != null) && (value.getAsString().equals(key))) {
					if (tmpStartDate.before(rangeStart))
						continue;
					if (tmpStartDate.after(rangeStop))
						continue;
					back = back + 1;
				}
			}
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param episodes1
	 * @param episodes2
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonElement mergeEpisodes(final JsonElement episodes1, final JsonElement episodes2,
			final String key) throws Exception {
		final Map<String, JsonObject> backMap = new HashMap<String, JsonObject>();
		if (episodes1.isJsonArray()) {
			final JsonArray jsons = (JsonArray) episodes1;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (!backMap.containsKey(id))
						backMap.put(id, tmpJSON);
					else {
						JsonObject loadedJSON = backMap.get(id);
						for (Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey()))
								loadedJSON.add(property.getKey(), property.getValue());
						}
					}
				}
			}
		}
		if (episodes2.isJsonArray()) {
			JsonArray jsons = (JsonArray) episodes2;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (!backMap.containsKey(id)) {
					} else {
						JsonObject loadedJSON = backMap.get(id);
						boolean typeChanged = false;
						String loadedType = null;
						if (loadedJSON.has("type") && (loadedJSON.get("type").isJsonPrimitive()))
							loadedType = ((JsonPrimitive) loadedJSON.get("type")).getAsString();
						String tmpType = null;
						if (tmpJSON.has("type") && (tmpJSON.get("type").isJsonPrimitive()))
							tmpType = ((JsonPrimitive) tmpJSON.get("type")).getAsString();
						if ((loadedType != null) && (tmpType != null)) {
							typeChanged = (!(loadedType.contentEquals(tmpType)));
						}
						for (Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey())) {
								if (!typeChanged)
									loadedJSON.add(property.getKey(), property.getValue());
							} else {
								final String value1 = loadedJSON.get(property.getKey()).toString();
								final String value2 = property.getValue().toString();
							}
						}
					}
				}
			}
		}
		JsonArray back = new JsonArray();
		for (Map.Entry<String, JsonObject> item : backMap.entrySet()) {
			back.add(item.getValue());
		}
		return back;
	}
	public synchronized JsonElement mergeEpisodes1(final JsonElement episodes1, final JsonElement episodes2,
			final String key) throws Exception {
		final Map<String, JsonObject> backMap = new HashMap<String, JsonObject>();
		if (episodes1.isJsonArray()) {
			final JsonArray jsons = (JsonArray) episodes1;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (!backMap.containsKey(id))
						backMap.put(id, tmpJSON);
					else {
						JsonObject loadedJSON = backMap.get(id);
						for (Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey()))
								loadedJSON.add(property.getKey(), property.getValue());
						}
					}
				}
			}
		}
		final Set<String> skipIDs = new HashSet<String>();
		if (episodes2.isJsonArray()) {
			JsonArray jsons = (JsonArray) episodes2;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (backMap.containsKey(id)) {
						JsonObject loadedJSON = backMap.get(id);
						boolean typeChanged = false;
						String loadedType = null;
						if (loadedJSON.has("type") && (loadedJSON.get("type").isJsonPrimitive()))
							loadedType = ((JsonPrimitive) loadedJSON.get("type")).getAsString();
						String tmpType = null;
						if (tmpJSON.has("type") && (tmpJSON.get("type").isJsonPrimitive()))
							tmpType = ((JsonPrimitive) tmpJSON.get("type")).getAsString();
						if ((loadedType != null) && (tmpType != null)) {
							typeChanged = (!(loadedType.contentEquals(tmpType)));
						}
						if (typeChanged) {
							skipIDs.add(id);
							if (tmpJSON.has("parentEpisode") && (tmpJSON.get("parentEpisode").isJsonPrimitive())) {
								final String parentId = ((JsonPrimitive) tmpJSON.get("parentEpisode")).getAsString();
								if (parentId != null)
									skipIDs.add(parentId);
							}
							if (tmpJSON.has("childEpisodes")) {
								final JsonElement childsElement = tmpJSON.get("childEpisodes");
								if ((childsElement != null) && (childsElement.isJsonArray())) {
									final JsonArray childs = tmpJSON.get("childEpisodes").getAsJsonArray();
									final Iterator<JsonElement> childIt = childs.iterator();
									while (childIt.hasNext()) {
										final JsonElement child = childIt.next();
										if (!child.isJsonPrimitive())
											continue;
										final String childId = ((JsonPrimitive) child).getAsString();
										skipIDs.add(childId);
									}
								}
							}
						}
					}
				}
			}
		}
		if (episodes2.isJsonArray()) {
			JsonArray jsons = (JsonArray) episodes2;
			final Iterator<JsonElement> it = jsons.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (tmp.isJsonObject()) {
					JsonObject tmpJSON = (JsonObject) tmp;
					String id = "UNKOWN";
					if (tmpJSON.has(key) && (tmpJSON.get(key).isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get(key)).getAsString();
					else if (tmpJSON.has("\"" + key + "\"") && (tmpJSON.get("\"" + key + "\"").isJsonPrimitive()))
						id = ((JsonPrimitive) tmpJSON.get("\"" + key + "\"")).getAsString();
					if (skipIDs.contains(id))
						continue;
					if (!backMap.containsKey(id)) {
					} else {
						JsonObject loadedJSON = backMap.get(id);
						for (Map.Entry<String, JsonElement> property : tmpJSON.entrySet()) {
							if (property.getKey().contentEquals("startDate"))
								continue;
							if (property.getKey().contentEquals("endDate"))
								continue;
							if (!loadedJSON.has(property.getKey())) {
								loadedJSON.add(property.getKey(), property.getValue());
							}
						}
					}
				}
			}
		}
		JsonArray back = new JsonArray();
		for (Map.Entry<String, JsonObject> item : backMap.entrySet()) {
			back.add(item.getValue());
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public synchronized final String urlencode(final String data) throws UnsupportedEncodingException {
		return URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
	}
	/**
	 * [REVIEW]
	 *
	 * @param serializedMap
	 * @return
	 * @throws Exception
	 */
	public synchronized final Map<String, Object> decodeEpisodesWithMeta(final String serializedMap) throws Exception {
		if (serializedMap == null)
			return null;
		if (serializedMap.equals(""))
			return null;
		final String decompressed = decompress(serializedMap);
		final Map<String, Object> back = new HashMap<String, Object>();
		final int dataStart = decompressed.indexOf("DATA(");
		final int dataStop = decompressed.indexOf(")DATA");
		if ((dataStart != -1) && (dataStop != -1) && (dataStart <= dataStop)) {
			final String dataStr = decompressed.substring(dataStart + 5, dataStop);
			back.put("data", decodeEpisodes(dataStr));
		}
		final int metaStart = decompressed.indexOf("META(");
		final int metaStop = decompressed.indexOf(")META");
		if ((metaStart != -1) && (metaStop != -1) && (metaStart <= metaStop)) {
			final String metaStr = decompressed.substring(metaStart + 5, metaStop);
			final Map<Integer, List<Map<String, String>>> metaBack = new LinkedHashMap<Integer, List<Map<String, String>>>();
			final String[] eventSets = metaStr.split(";");
			if (eventSets != null) {
				for (final String eventSet : eventSets) {
					final String[] eventPair = eventSet.split(Pattern.quote("#"));
					if ((eventPair != null) && (eventPair.length == 2)) {
						final String eventIndexStr = eventPair[0];
						final Integer eventIndex = Integer.parseInt(eventIndexStr);
						final String metaListStr = eventPair[1];
						final String[] metaListPairs = metaListStr.split(Pattern.quote("^"));
						List<Map<String, String>> metaPairs = null;
						if (back.containsKey(eventIndex))
							metaPairs = metaBack.get(eventIndex);
						if (metaPairs == null)
							metaPairs = new ArrayList<Map<String, String>>();
						if ((metaListPairs != null) && (metaListPairs.length > 0)) {
							Map<String, String> metaPairMap = new LinkedHashMap<String, String>();
							for (final String metaListPair : metaListPairs) {
								final String[] metaPair = metaListPair.split(Pattern.quote("°"));
								if ((metaPair != null) && (metaPair.length == 2))
									metaPairMap.put(metaPair[0], metaPair[1]);
								else {
								}
							}
							metaPairs.add(metaPairMap);
						}
						if (!metaPairs.isEmpty())
							metaBack.put(eventIndex, metaPairs);
					}
				}
			}
			back.put("meta", metaBack);
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public synchronized String compress(final String content) throws Exception {
		if (content == null)
			return null;
		if (content.contentEquals(""))
			return "";
		return compress(content, true);
	}
	private synchronized String compress(final String content, final boolean encoded) throws Exception {
		if (content == null)
			return null;
		if (content.contentEquals(""))
			return "";
		byte[] compressed = Snappy.compress(content.getBytes());
		String back = null;
		if (encoded) {
			back = new String(Hex.encodeHex(compressed));
		} else
			back = new String(compressed);
		return back;
	}
	public synchronized JsonObject parseJsonObj(final String toJson) {
		if (toJson == null)
			return null;
		if (toJson.contentEquals(""))
			return new JsonObject();
		final String decoded = toJson.replace('*', '\"');
		JsonObject back = new JsonParser().parse(decoded).getAsJsonObject();
		return back;
	}
	public synchronized String decompress(final String content) throws Exception {
		if (content == null)
			return null;
		if (content.contentEquals(""))
			return "";
		return decompress(content, true);
	}
	private synchronized String decompress(final String content, final boolean encoded) throws Exception {
		if (content == null)
			return null;
		if (content.contentEquals(""))
			return "";
		byte[] back = null;
		if (encoded) {
			back = Hex.decodeHex(content.toCharArray());
		} else
			back = content.getBytes();
		return new String(Snappy.uncompress(back));
	}
	/**
	 * [REVIEWED CM]
	 * 
	 * @param beams
	 * @param slots
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean hasEpisodes(final Map<Integer, List<List<Integer>>> beams, final List<Long> slots)
			throws Exception {
		if (beams == null)
			return false;
		if (beams.isEmpty())
			return false;
		if (slots == null)
			return false;
		if (slots.isEmpty())
			return false;
		for (final Long slot : slots) {
			if (!beams.containsKey(slot.intValue()))
				continue;
			if (!beams.get(slot.intValue()).isEmpty())
				return true;
		}
		return false;
	}
	/**
	 * [REVIEWED CM]
	 * 
	 * @param beams
	 * @param slot
	 * @param rangeStart
	 * @param rangeStop
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean hasEpisodesInRange(final Map<Integer, List<List<Integer>>> beams, final Integer slot,
			final Integer rangeStart, final Integer rangeStop) throws Exception {
		if (beams == null)
			return false;
		if (beams.isEmpty())
			return false;
		if (slot == null)
			return false;
		if (rangeStart == null)
			return false;
		if (rangeStop == null)
			return false;
		if (!beams.containsKey(slot.intValue()))
			return false;
		if (!beams.get(slot.intValue()).isEmpty()) {
			final List<List<Integer>> beamList = beams.get(slot.intValue());
			for (final List<Integer> beam : beamList) {
				final Integer beamStart = beam.get(0);
				final Integer beamStop = beam.get(beam.size() - 1);
				if ((beamStart >= rangeStart) && (beamStart <= rangeStop))
					return true;
			}
		}
		return false;
	}
	/**
	 * [REVIEWED CM]
	 * 
	 * @param beams
	 * @param slot
	 * @param rangeStart
	 * @param rangeStop
	 * @return
	 * @throws Exception
	 */
	public synchronized int countEpisodesInRange(final Map<Integer, List<List<Integer>>> beams, final Integer slot,
			final Integer rangeStart, final Integer rangeStop) throws Exception {
		if (beams == null)
			return 0;
		if (beams.isEmpty())
			return 0;
		if (slot == null)
			return 0;
		if (rangeStart == null)
			return 0;
		if (rangeStop == null)
			return 0;
		if (!beams.containsKey(slot.intValue()))
			return 0;
		if (!beams.get(slot.intValue()).isEmpty()) {
			final List<List<Integer>> beamList = beams.get(slot.intValue());
			int back = 0;
			for (final List<Integer> beam : beamList) {
				final Integer beamStart = beam.get(0);
				final Integer beamStop = beam.get(beam.size() - 1);
				if ((beamStart >= rangeStart) && (beamStart <= rangeStop))
					back = back + 1;
			}
			return back;
		}
		return 0;
	}
	/**
	 * [REVIEWED CM]
	 *
	 * @param beams
	 * @param slots
	 * @param rangeStart
	 * @param rangeStop
	 * @return
	 * @throws Exception
	 */
	public synchronized int countTilesWithEpisodesInRange(final Map<Integer, List<List<Integer>>> beams,
			final List<Long> slots, final Integer rangeStart, final Integer rangeStop) throws Exception {
		if (beams == null)
			return 0;
		if (beams.isEmpty())
			return 0;
		if (slots == null)
			return 0;
		if (slots.isEmpty())
			return 0;
		if (rangeStart == null)
			return 0;
		if (rangeStop == null)
			return 0;
		final List<Integer> tilesWithEpisodes = new ArrayList<Integer>();
		for (final Long slot : slots) {
			if (!beams.containsKey(slot.intValue()))
				continue;
			final Integer beamKey = slot.intValue();
			if (beams.get(beamKey).isEmpty())
				continue;
			final List<List<Integer>> beamList = beams.get(beamKey);
			for (final List<Integer> beam : beamList) {
				final Integer beamStart = beam.get(0);
				final Integer beamStop = beam.get(beam.size() - 1);
				final Integer tileStart = Math.max(beamStart, rangeStart);
				final Integer tileStop = Math.min(beamStop, rangeStop);
				if ((tileStart <= rangeStop) && (tileStop >= rangeStart)) {
					for (Integer lft = tileStart; lft <= tileStop; lft++) {
						if (!tilesWithEpisodes.contains(lft.intValue()))
							tilesWithEpisodes.add(lft.intValue());
					}
				}
			}
		}
		return tilesWithEpisodes.size();
	}
	/**
	 * [REVIEWED CM]
	 *
	 * @param beams
	 * @param slot
	 * @return
	 * @throws Exception
	 */
	public synchronized List<List<Integer>> getEpisodes(final Map<Integer, List<List<Integer>>> beams,
			final Integer slot) throws Exception {
		if (beams == null)
			return null;
		if (beams.isEmpty())
			return null;
		if (slot == null)
			return null;
		if (beams.containsKey(slot))
			return beams.get(slot);
		return null;
	}
	/**
	 * [REVIEWED CM] [SEE REVIEWPROPOSEL]
	 *
	 * @param colIndexBean
	 * @param rowIndexBean
	 * @param colCount
	 * @param rowCount
	 * @return
	 * @throws Exception
	 */
	public synchronized Integer getTileIndex(final SingleChoiceAnswerOptionTypeBean colIndexBean,
			final SingleChoiceAnswerOptionTypeBean rowIndexBean, final Long colCount, final Long rowCount)
			throws Exception {
		if (rowIndexBean == null)
			return -1;
		final Integer rowIndex = toInteger(asNumber(rowIndexBean));
		if (rowIndex < 0)
			return -1;
		if (colIndexBean == null)
			return -1;
		final Integer colIndex = toInteger(asNumber(colIndexBean));
		if (colIndex < 0)
			return -1;
		Integer back = -1;
		if (rowIndex == 0) {
			back = colIndex;
		} else if (rowIndex > 0) {
			back = (int) ((rowIndex * colCount) + colIndex);
		}
		return back;
	}
	public synchronized Integer getTileIndexREVIEWPROPOSAL(final SingleChoiceAnswerOptionTypeBean colIndexBean,
			final SingleChoiceAnswerOptionTypeBean rowIndexBean, final Long colCount) throws Exception {
		if (rowIndexBean == null)
			return -1;
		final Integer rowIndex = toInteger(asNumber(rowIndexBean));
		if (rowIndex < 0)
			return -1;
		if (colIndexBean == null)
			return -1;
		final Integer colIndex = toInteger(asNumber(colIndexBean));
		if (colIndex < 0)
			return -1;
		Integer back = -1;
		if (rowIndex == 0) {
			back = colIndex;
		} else if (rowIndex > 0) {
			if (colCount != null)
				back = (int) ((rowIndex * colCount) + colIndex);
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param episode
	 * @param columnList
	 * @param rowList
	 * @return
	 * @throws Exception
	 */
	public synchronized List<String> convertEpisode(final List<Integer> episode, final List<Object> columnList,
			final List<Object> rowList) throws Exception {
		if (episode == null)
			return null;
		if (episode.isEmpty())
			return null;
		if (columnList == null)
			return null;
		if (columnList.isEmpty())
			return null;
		if (rowList == null)
			return null;
		if (rowList.isEmpty())
			return null;
		final List<String> back = new ArrayList<String>();
		final int colCount = columnList.size();
		for (final Integer episodeItem : episode) {
			final int tileIndex = episodeItem;
			final int rowIndex = (int) Math.ceil(tileIndex / colCount);
			final int colIndex = tileIndex - (rowIndex * colCount);
			back.add(columnList.get(colIndex) + " " + rowList.get(rowIndex));
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	private synchronized List<UICalendarItem> getItems(final UICalendar calendar) throws Exception {
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
	/**
	 * [REVIEW]
	 *
	 * @param calendarItem
	 * @return
	 * @throws Exception
	 */
	private synchronized Map<Integer, BooleanValueTypeBean> getSlots(final UICalendarItem calendarItem)
			throws Exception {
		if (calendarItem == null)
			return null;
		final Map<String, Object> attributes = calendarItem.getAttributes();
		if (attributes != null) {
			Map<Integer, BooleanValueTypeBean> back = new HashMap<Integer, BooleanValueTypeBean>();
			for (int a = 1; a <= 20; a++) {
				final IAnswerBean variable = calendarItem.getSlot(a);
				if (variable == null)
					continue;
				if ((BooleanValueTypeBean.class).isAssignableFrom(variable.getClass()))
					back.put(Integer.valueOf(a), (BooleanValueTypeBean) variable);
			}
			return back;
		}
		return null;
	}
	/**
	 * [REVIEW]
	 *
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<Integer, Integer> calendarSlotCounts(final UIComponent calendar) throws Exception {
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
							count = Integer.valueOf(0);
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
	 * [REVIEW] Generate a random Boolean Decision
	 *
	 * @return random boolean
	 */
	public synchronized boolean randomBoolean() {
		return random.nextBoolean();
	}
	/**
	 * [REVIEW] Generate a weighted random Boolean Decision
	 *
	 * @param percent Value between 1 and 100
	 * @return random boolean
	 */
	public synchronized boolean randomBoolean(final int percent) {
		final double weight = ((double) percent) / 100;
		final double rnd = random.nextDouble();
		final boolean back = rnd < weight;
		return back;
	}
	/**
	 * [REVIEW][TODO] Generate a random Decision from Item set
	 *
	 * @param items
	 * @return random Item
	 */
	public synchronized String randomOf(final List<String> items) {
		final Random random = new SecureRandom();
		final String selected = items.get(random.nextInt(items.size()));
		return selected;
	}
	/**
	 * [REVIEW][TODO] Generate a random Set from Item set
	 *
	 * @param items
	 * @param count
	 * @return random Set of Items
	 */
	public synchronized List<Object> randomOf(final List<Object> items, final int count) {
		if (items == null)
			return null;
		final List<Object> back = new ArrayList<Object>();
		if (items.isEmpty())
			return back;
		if (count <= 0)
			return back;
		if (count > items.size())
			return items;
		final Random rand = new Random();
		for (int i = 0; i < count; i++) {
			int randomIndex = rand.nextInt(items.size());
			final Object randomElement = items.get(randomIndex);
			back.add(randomElement);
			items.remove(randomIndex);
		}
		return back;
	}
	/**
	 * [REVIEW]
	 *
	 * Parse JSON from String
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<String, Object> asXmlMap(final String input) throws Exception {
		if (input == null)
			return null;
		final SAXParserFactory spfac = SAXParserFactory.newInstance();
		final SAXParser sp = spfac.newSAXParser();
		final class ContentHandler extends DefaultHandler {
			Map<String, Object> map;
			private Deque<Map<String, Object>> stack = new ArrayDeque<Map<String, Object>>();
			public ContentHandler(Map<String, Object> map) {
				super();
				this.map = map;
			}
			public Map<String, Object> getMap() {
				return map;
			}
			@Override
			public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
				Map<String, Object> elemMap = new LinkedHashMap<String, Object>();
				elemMap.put("type", qName);
				int attrLen = atts.getLength();
				Map<String, Object> attrMap = new LinkedHashMap<String, Object>();
				for (int i = 0; i < attrLen; i++) {
					attrMap.put(atts.getQName(i), atts.getValue(i));
				}
				if (!attrMap.isEmpty())
					elemMap.put("attributes", attrMap);
				stack.push(elemMap);
			}
			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				super.endElement(uri, localName, qName);
				Map<String, Object> elemMap = stack.pop();
				List<Object> childs = (List<Object>) stack.peek().get("childs");
				if (childs == null)
					childs = new ArrayList<Object>();
				childs.add(elemMap);
				stack.peek().put("childs", childs);
			}
			@Override
			public void startDocument() throws SAXException {
				stack.push(this.map);
			}
			@Override
			public void endDocument() throws SAXException {
			}
			@Override
			public void characters(char[] ch, int start, int length) {
				String content = String.valueOf(ch).substring(start, start + length);
				if (content != null) {
					content = content.trim();
					if (!content.equals(""))
						stack.peek().put("content", content);
				}
			}
		}
		final ContentHandler handler = new ContentHandler(new HashMap<String, Object>());
		sp.parse(IOUtils.toInputStream(input, "UTF-8"), handler);
		return handler.getMap();
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	public synchronized String defrac(final List<AbstractAnswerBean> vars) throws Exception {
		if (vars == null)
			return "";
		if (vars.isEmpty())
			return "";
		final StringBuffer back = new StringBuffer();
		for (final AbstractAnswerBean var : vars) {
			if (var == null) {
				System.err.println("skip null var ");
				continue;
			}
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				back.append(((StringValueTypeBean) var).getStringValue());
			}
		}
		return decompress(back.toString());
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @param data
	 * @throws Exception
	 */
	public synchronized void frac(final List<AbstractAnswerBean> vars, final String data) throws Exception {
		if (vars == null)
			return;
		if (vars.isEmpty())
			return;
		if (data == null)
			return;
		final String compressed = compress(data);
		final int chunkSize = 1500;
		final LinkedList<String> chunks = new LinkedList<String>();
		if (chunkSize > data.length())
			chunks.add(compressed);
		else {
			final Matcher m = Pattern.compile(".{1," + chunkSize + "}").matcher(compressed);
			while (m.find()) {
				chunks.add(compressed.substring(m.start(), m.end()));
			}
		}
		for (final Object var : vars) {
			if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
				String chunk = "";
				if (!chunks.isEmpty())
					chunk = chunks.removeFirst();
				((StringValueTypeBean) var).setStringValue(chunk);
			}
		}
		if (!chunks.isEmpty())
			throw new Exception("space too small for content: " + (chunks.size() * chunkSize) + " characters left");
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @return
	 * @throws ParseException
	 */
	public synchronized final boolean isEpisodesComplete(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		if (data == null)
			return false;
		if (data.size() == 0)
			return false;
		if (rangeStart == null)
			return false;
		if (rangeStop == null)
			return false;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		return this.isEpisodesCompleteHelper(data, rangeStartDate, rangeEndDate);
	}
	/**
	 * [REVIEW][TODO] Helper method for EpisodesComplete
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @return
	 * @throws ParseException
	 */
	private synchronized final boolean isEpisodesCompleteHelper(final JsonArray data, final Date rangeStart,
			final Date rangeStop) throws ParseException {
		if (data == null)
			return false;
		if (data.size() == 0)
			return false;
		if (rangeStart == null)
			return false;
		if (rangeStop == null)
			return false;
		final Set<Date> hasEpisodes = new HashSet<Date>();
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			if (tmp.isJsonNull())
				continue;
			final JsonObject json = (JsonObject) tmp;
			if (!json.has("startDate"))
				continue;
			if (!json.has("endDate"))
				continue;
			if (json.get("startDate").isJsonNull())
				continue;
			if (json.get("endDate").isJsonNull())
				continue;
			String startDateStr = json.get("startDate").getAsString();
			startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentStartDate = format.parse(startDateStr);
			String endDateStr = json.get("endDate").getAsString();
			endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentEndDate = format.parse(endDateStr);
			final Calendar episodeStartCal = Calendar.getInstance();
			episodeStartCal.setTime(currentStartDate);
			final Calendar episodeStopCal = Calendar.getInstance();
			episodeStopCal.setTime(currentEndDate);
			for (Calendar lft = episodeStartCal; (lft.before(episodeStopCal) || lft.equals(episodeStopCal)); lft
					.add(Calendar.DAY_OF_YEAR, 1)) {
				final Date xx = lft.getTime();
				if (!hasEpisodes.contains(xx)) {
					hasEpisodes.add(xx);
				}
			}
		}
		final Calendar startCal = Calendar.getInstance();
		startCal.setTime(rangeStart);
		startCal.set(Calendar.HOUR_OF_DAY, 1);
		final Calendar endCal = Calendar.getInstance();
		endCal.setTime(rangeStop);
		endCal.set(Calendar.HOUR_OF_DAY, 1);
		for (Calendar lft = startCal; (lft.before(endCal) || lft.equals(endCal)); lft.add(Calendar.DAY_OF_YEAR, 1)) {
			final Date xx = lft.getTime();
			if (!hasEpisodes.contains(xx)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * [REVIEW]
	 *
	 * @param data
	 * @return
	 * @throws ParseException
	 */
	public synchronized final boolean isEpisodesComplete(final JsonArray data) throws ParseException {
		if (data == null)
			return false;
		if (data.size() == 0)
			return false;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date minStartDate = null;
		Date maxEndDate = null;
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject json = (JsonObject) tmp;
			if (!json.has("startDate"))
				continue;
			if (!json.has("endDate"))
				continue;
			String startDateStr = json.get("startDate").getAsString();
			startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentStartDate = format.parse(startDateStr);
			if (minStartDate == null)
				minStartDate = currentStartDate;
			else if (currentStartDate.before(minStartDate))
				minStartDate = currentStartDate;
			String endDateStr = json.get("endDate").getAsString();
			endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T01:00:00.000Z";
			final Date currentEndDate = format.parse(endDateStr);
			if (maxEndDate == null)
				maxEndDate = currentEndDate;
			else if (currentEndDate.after(maxEndDate))
				maxEndDate = currentEndDate;
		}
		return this.isEpisodesCompleteHelper(data, minStartDate, maxEndDate);
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data       input JSON object with episodes
	 * @param rangeStart start date of the time period that is being considered
	 * @param rangeStop  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final float countEpisodesRatio(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		if (data == null)
			return 0;
		if (data.size() == 0)
			return 0;
		if (rangeStart == null)
			return 0;
		if (rangeStop == null)
			return 0;
		if (rangeStart.contentEquals(""))
			return 0;
		if (rangeStop.contentEquals(""))
			return 0;
		if(!this.isWellFormedTimestamp(rangeStart)) 
			return 0;
		if(!this.isTimeStampParseable(rangeStart)) 
			return 0;
		if(!this.isWellFormedTimestamp(rangeStop)) 
			return 0;
		if(!this.isTimeStampParseable(rangeStop)) 
			return 0;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		return this.countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, false).get(0);
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data            input JSON object with episodes
	 * @param rangeStartYear  start date of the time period that is being considered
	 * @param rangeStartMonth start date of the time period that is being considered
	 * @param rangeStopYear   end date of the time period that is being considered
	 * @param rangeStopMonth  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final float countEpisodesRatio(final JsonArray data, final String rangeStartYear,
			String rangeStartMonth, final String rangeStopYear, String rangeStopMonth) throws ParseException {
		if (data == null)
			return Float.valueOf(0);
		if (data.size() == 0)
			return Float.valueOf(0);
		if (rangeStartMonth == null)
			return Float.valueOf(0);
		if (rangeStopMonth == null)
			return Float.valueOf(0);
		if (rangeStartYear == null)
			return Float.valueOf(0);
		if (rangeStopYear == null)
			return Float.valueOf(0);
		if (rangeStartMonth.length() == 1) {
			rangeStartMonth = "0".concat(rangeStartMonth);
		}
		if (rangeStopMonth.length() == 1) {
			rangeStopMonth = "0".concat(rangeStopMonth);
		}
		final String rangeStart = rangeStartYear.concat("-").concat(rangeStartMonth).concat("-")
				.concat("01T00:00:00.000Z");
		final String rangeStop = rangeStopYear.concat("-").concat(rangeStopMonth).concat("-")
				.concat("01T00:00:00.000Z");
		if(!this.isWellFormedTimestamp(rangeStart)) 
			return Float.valueOf(0);
		if(!this.isTimeStampParseable(rangeStart)) 
			return Float.valueOf(0);
		if(!this.isWellFormedTimestamp(rangeStop)) 
			return Float.valueOf(0);
		if(!this.isTimeStampParseable(rangeStop)) 
			return Float.valueOf(0);
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final Calendar tempCal = new GregorianCalendar();
		tempCal.clear();
		tempCal.setTime(rangeEndDate);
		tempCal.add(Calendar.MONTH, 1);
		tempCal.add(Calendar.DATE, -1);
		rangeEndDate = tempCal.getTime();
		return this.countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, false).get(0);
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data       input JSON object with episodes
	 * @param rangeStart start date of the time period that is being considered
	 * @param rangeStop  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final List<Float> countEpisodesRatioYearwise(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		ArrayList<Float> returnList = new ArrayList<>();
		returnList.add((float) 0);
		if (data == null)
			return returnList;
		if (data.size() == 0)
			return returnList;
		if (rangeStart == null)
			return returnList;
		if (rangeStop == null)
			return returnList;
		if (rangeStart.contentEquals(""))
			return returnList;
		if (rangeStop.contentEquals(""))
			return returnList;
		if(!this.isWellFormedTimestamp(rangeStart)) 
			return returnList;
		if(!this.isTimeStampParseable(rangeStart)) 
			return returnList;
		if(!this.isWellFormedTimestamp(rangeStop)) 
			return returnList;
		if(!this.isTimeStampParseable(rangeStop)) 
			return returnList;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		List<Float> resultsList = this.countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, true);
		return resultsList;
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data            input JSON object with episodes
	 * @param rangeStartYear  start date of the time period that is being considered
	 * @param rangeStartMonth start date of the time period that is being considered
	 * @param rangeStopYear   end date of the time period that is being considered
	 * @param rangeStopMonth  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final List<Float> countEpisodesRatioYearwise(final JsonArray data, final String rangeStartYear,
			String rangeStartMonth, final String rangeStopYear, String rangeStopMonth) throws ParseException {
		ArrayList<Float> returnList = new ArrayList<>();
		returnList.add((float) 0);
		if (data == null)
			return returnList;
		if (data.size() == 0)
			return returnList;
		if (rangeStartMonth == null)
			return returnList;
		if (rangeStopMonth == null)
			return returnList;
		if (rangeStartYear == null)
			return returnList;
		if (rangeStopYear == null)
			return returnList;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		if (rangeStartMonth.length() == 1) {
			rangeStartMonth = "0".concat(rangeStartMonth);
		}
		if (rangeStopMonth.length() == 1) {
			rangeStopMonth = "0".concat(rangeStopMonth);
		}
		final String rangeStart = rangeStartYear.concat("-").concat(rangeStartMonth).concat("-")
				.concat("01T00:00:00.000Z");
		final String rangeStop = rangeStopYear.concat("-").concat(rangeStopMonth).concat("-")
				.concat("01T00:00:00.000Z");
		if(!this.isWellFormedTimestamp(rangeStart)) 
			return returnList;
		if(!this.isTimeStampParseable(rangeStart)) 
			return returnList;
		if(!this.isWellFormedTimestamp(rangeStop)) 
			return returnList;
		if(!this.isTimeStampParseable(rangeStop)) 
			return returnList;
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final Calendar tempCal = new GregorianCalendar();
		tempCal.clear();
		tempCal.setTime(rangeEndDate);
		tempCal.add(Calendar.MONTH, 1);
		tempCal.add(Calendar.DATE, -1);
		rangeEndDate = tempCal.getTime();
		List<Float> resultsList = this.countEpisodesRatioHelper(data, rangeStartDate, rangeEndDate, format, true);
		return resultsList;
	}
	public synchronized Float minFloatList(List<Float> valueList) {
		return Collections.min(valueList);
	}
	/**
	 * [REVIEW][TODO] helper function, returns a float value representing the
	 * portion of a given time period that is filled with episode data (calculated
	 * on the level of days)
	 *
	 * @param data            input JSON object with episodes
	 * @param rangeStartInput start date of the time period that is being considered
	 * @param rangeStopInput  end date of the time period that is being considered
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	private synchronized List<Float> countEpisodesRatioHelper(final JsonArray data, final Date rangeStartInput,
			final Date rangeStopInput, final SimpleDateFormat format, final boolean yearwise) throws ParseException {
		final List<Float> returnList = new LinkedList<>();
		returnList.add((float) 0);
		if (data == null)
			return returnList;
		if (data.size() == 0)
			return returnList;
		if (rangeStartInput == null)
			return returnList;
		if (rangeStopInput == null)
			return returnList;
		final Calendar timestampCal = new GregorianCalendar();
		final Calendar outputCal = new GregorianCalendar();
		timestampCal.setTime(rangeStartInput);
		outputCal.clear();
		outputCal.set(Calendar.YEAR, timestampCal.get(Calendar.YEAR));
		outputCal.set(Calendar.MONTH, timestampCal.get(Calendar.MONTH));
		outputCal.set(Calendar.DATE, timestampCal.get(Calendar.DATE));
		outputCal.set(Calendar.HOUR, 0);
		outputCal.set(Calendar.MINUTE, 0);
		outputCal.set(Calendar.SECOND, 0);
		outputCal.set(Calendar.MILLISECOND, 0);
		final Date rangeStart = outputCal.getTime();
		outputCal.clear();
		timestampCal.clear();
		timestampCal.setTime(rangeStopInput);
		outputCal.set(Calendar.YEAR, timestampCal.get(Calendar.YEAR));
		outputCal.set(Calendar.MONTH, timestampCal.get(Calendar.MONTH));
		outputCal.set(Calendar.DATE, timestampCal.get(Calendar.DATE));
		outputCal.set(Calendar.HOUR, 23);
		outputCal.set(Calendar.MINUTE, 59);
		outputCal.set(Calendar.SECOND, 59);
		outputCal.set(Calendar.MILLISECOND, 999);
		final Date rangeStop = outputCal.getTime();
		final List<Float> resultsList = new LinkedList<>();
		final Set<Date> hasEpisodes = new HashSet<Date>();
		if (yearwise) {
			final Calendar episodeStartTimeCal = Calendar.getInstance();
			episodeStartTimeCal.setTime(rangeStart);
			final int yearrangestart = episodeStartTimeCal.get(Calendar.YEAR);
			final Calendar episodeEndTimeCal = Calendar.getInstance();
			episodeEndTimeCal.setTime(rangeStop);
			final int yearrangeend = episodeEndTimeCal.get(Calendar.YEAR);
			final List<Integer> yearsList = new LinkedList<>();
			for (int i = yearrangestart; i <= yearrangeend; i++) {
				yearsList.add(i);
			}
			for (int i = 0; i < yearsList.size(); i++) {
				hasEpisodes.clear();
				int currentYear = yearsList.get(i);
				Date currentRangeStart = new Date();
				Date currentRangeStop = new Date();
				if (i != 0) {
					Calendar currentCalendarStart = new GregorianCalendar();
					currentCalendarStart.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0);
					currentCalendarStart.set(Calendar.MILLISECOND, 0);
					currentRangeStart = currentCalendarStart.getTime();
				} else {
					currentRangeStart = rangeStart;
				}
				if (i != yearsList.size() - 1) {
					Calendar currentCalendarStop = new GregorianCalendar();
					currentCalendarStop.set(currentYear, Calendar.DECEMBER, 31, 0, 0, 0);
					currentCalendarStop.set(Calendar.MILLISECOND, 0);
					currentCalendarStop.add(Calendar.DATE, 1);
					currentRangeStop = currentCalendarStop.getTime();
				} else {
					currentRangeStop = rangeStop;
				}
				final Iterator<JsonElement> it = data.iterator();
				while (it.hasNext()) {
					final JsonElement tmp = it.next();
					if (!tmp.isJsonObject())
						continue;
					final JsonObject json = (JsonObject) tmp;
					if (!json.has("startDate"))
						continue;
					if (!json.has("endDate"))
						continue;
					if (json.get("startDate").isJsonNull())
						continue;
					if (json.get("endDate").isJsonNull())
						continue;
					String startDateStr = json.get("startDate").getAsString();
					startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T00:00:00.000Z";
					if(!this.isWellFormedTimestamp(startDateStr)) 
						continue;
					if(!this.isTimeStampParseable(startDateStr)) 
						continue;
					final Date currentStartDate = format.parse(startDateStr);
					String endDateStr = json.get("endDate").getAsString();
					endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T23:59:59.999Z";
					if(!this.isWellFormedTimestamp(endDateStr)) 
						continue;
					if(!this.isTimeStampParseable(endDateStr)) 
						continue;
					final Date currentEndDate = format.parse(endDateStr);
					final Calendar episodeStartCal = Calendar.getInstance();
					episodeStartCal.setTime(currentStartDate);
					final Calendar episodeStopCal = Calendar.getInstance();
					episodeStopCal.setTime(currentEndDate);
					for (Calendar lft = episodeStartCal; (lft.before(episodeStopCal) || lft.equals(episodeStopCal)); lft
							.add(Calendar.DAY_OF_YEAR, 1)) {
						if (lft.get(Calendar.YEAR) != currentYear) {
							continue;
						}
						final Date xx = lft.getTime();
						if (!hasEpisodes.contains(xx)) {
							if ((!(xx.before(currentRangeStart)) || xx.equals(currentRangeStart))
									&& !(xx.after(currentRangeStop))) {
								hasEpisodes.add(xx);
							}
						}
					}
				}
				final long diffInMillies = Math.abs(currentRangeStop.getTime() - currentRangeStart.getTime());
				final float diff = TimeUnit.DAYS.convert(diffInMillies + 1L, TimeUnit.MILLISECONDS);
				final float hasEpisodesFloat = hasEpisodes.size();
				final float result = (hasEpisodesFloat / diff);
				TreeSet myTreeSet = new TreeSet();
				myTreeSet.addAll(hasEpisodes);
				resultsList.add(result);
			}
		} else {
			final Iterator<JsonElement> it = data.iterator();
			while (it.hasNext()) {
				final JsonElement tmp = it.next();
				if (!tmp.isJsonObject())
					continue;
				final JsonObject json = (JsonObject) tmp;
				if (!json.has("startDate"))
					continue;
				if (!json.has("endDate"))
					continue;
				if (json.get("startDate").isJsonNull())
					continue;
				if (json.get("endDate").isJsonNull())
					continue;
				String startDateStr = json.get("startDate").getAsString();
				if (startDateStr.contentEquals(""))
					continue;
				startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T00:00:00.000Z";
				if(!this.isWellFormedTimestamp(startDateStr)) 
					continue;
				if(!this.isTimeStampParseable(startDateStr)) 
					continue;
				final Date currentStartDate = format.parse(startDateStr);
				String endDateStr = json.get("endDate").getAsString();
				if (endDateStr.contentEquals(""))
					continue;
				endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T23:59:59.999Z";
				if(!this.isWellFormedTimestamp(endDateStr)) 
					continue;
				if(!this.isTimeStampParseable(endDateStr)) 
					continue;
				final Date currentEndDate = format.parse(endDateStr);
				final Calendar episodeStartCal = Calendar.getInstance();
				episodeStartCal.setTime(currentStartDate);
				final Calendar episodeStopCal = Calendar.getInstance();
				episodeStopCal.setTime(currentEndDate);
				for (Calendar lft = episodeStartCal; (lft.before(episodeStopCal) || lft.equals(episodeStopCal)); lft
						.add(Calendar.DAY_OF_YEAR, 1)) {
					final Date xx = lft.getTime();
					if (!hasEpisodes.contains(xx)) {
						if (!(xx.before(rangeStart)) && !(xx.after(rangeStop))) {
							hasEpisodes.add(xx);
						}
					}
				}
			}
			final long diffInMillies = Math.abs(rangeStop.getTime() - rangeStart.getTime());
			final float diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1L;
			final float hasEpisodesFloat = hasEpisodes.size();
			final float result = (hasEpisodesFloat / diff);
			final TreeSet myTreeSet = new TreeSet();
			myTreeSet.descendingSet();
			myTreeSet.addAll(hasEpisodes);
			resultsList.add(result);
		}
		return resultsList;
	}
	/**
	 * [REVIEW][TODO] calls countEpisodesRatioHelper returns a float value
	 * representing the portion of a given time period that is filled with episode
	 * data (calculated on the level of days)
	 *
	 * @param data input JSON object with episodes
	 * @return ratio float value (range 0 to 1), returns 0 if data is empty or no
	 *         rangeStart or rangeStop is given
	 * @throws ParseException
	 */
	public synchronized final float countEpisodesRatio(final JsonArray data) throws ParseException {
		if (data == null)
			return 0;
		if (data.size() == 0)
			return 0;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date minStartDate = null;
		Date maxEndDate = null;
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			if (tmp.isJsonNull())
				continue;
			final JsonObject json = (JsonObject) tmp;
			if (!json.has("startDate"))
				continue;
			if (!json.has("endDate"))
				continue;
			String startDateStr = json.get("startDate").getAsString();
			startDateStr = startDateStr.substring(0, startDateStr.indexOf('T')) + "T01:00:00.000Z";
			if(!this.isWellFormedTimestamp(startDateStr)) 
				return 0;
			if(!this.isTimeStampParseable(startDateStr)) 
				return 0;
			final Date currentStartDate = format.parse(startDateStr);
			if (minStartDate == null)
				minStartDate = currentStartDate;
			else if (currentStartDate.before(minStartDate))
				minStartDate = currentStartDate;
			String endDateStr = json.get("endDate").getAsString();
			endDateStr = endDateStr.substring(0, endDateStr.indexOf('T')) + "T01:00:00.000Z";
			if(!this.isWellFormedTimestamp(endDateStr)) 
				return 0;
			if(!this.isTimeStampParseable(endDateStr)) 
				return 0;
			final Date currentEndDate = format.parse(endDateStr);
			if (maxEndDate == null)
				maxEndDate = currentEndDate;
			else if (currentEndDate.after(maxEndDate))
				maxEndDate = currentEndDate;
		}
		return this.countEpisodesRatioHelper(data, minStartDate, maxEndDate, format, false).get(0);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String property, final String value)
			throws Exception {
		return hasEpisodes(data, null, null, property, value);
	}
	public synchronized final boolean isSplittable(final JsonElement episode, final Date splitDate) {
		if (episode == null)
			return false;
		if (episode.isJsonNull())
			return false;
		if (!episode.isJsonObject())
			return false;
		if (splitDate == null)
			return false;
		final Calendar splitCal = new GregorianCalendar();
		splitCal.setTime(splitDate);
		splitCal.set(Calendar.DAY_OF_MONTH, 1);
		splitCal.set(Calendar.HOUR, 1);
		splitCal.set(Calendar.MINUTE, 0);
		splitCal.set(Calendar.SECOND, 0);
		splitCal.set(Calendar.MILLISECOND, 0);
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		JsonObject episodeObj = episode.getAsJsonObject();
		if (!episodeObj.has("startDate"))
			return false;
		Date startDate = null;
		try {
			startDate = stampFormat.parse(episodeObj.get("startDate").getAsString());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if (startDate == null)
			return false;
		final Calendar startCal = new GregorianCalendar();
		startCal.setTime(startDate);
		if (!startCal.before(splitCal))
			return false;
		if (!episodeObj.has("endDate"))
			return false;
		Date endDate = null;
		try {
			endDate = stampFormat.parse(episodeObj.get("endDate").getAsString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (endDate == null)
			return false;
		final Calendar endCal = new GregorianCalendar();
		endCal.setTime(endDate);
		if (!splitCal.before(endCal))
			return false;
		return true;
	}
	/**
	 * method for splitting of episodes
	 *
	 * @param arr
	 * @param index
	 * @param split_type_dict
	 * @return
	 */
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict) {
		return this.splitEpisode(arr, index, split_type_dict, null, -1);
	}
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict, final int limit) {
		return this.splitEpisode(arr, index, split_type_dict, null, limit);
	}
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict, final List<String> blacklist) {
		return this.splitEpisode(arr, index, split_type_dict, blacklist, -1);
	}
	public synchronized final Map<String, Object> splitEpisode(final JsonArray arr, final int index,
			final JsonElement split_type_dict, final List<String> blacklist, final int limit) {
		final Map<String, Object> back = new HashMap<String, Object>();
		back.put("index", -1);
		back.put("episodes", arr);
		if (arr == null) {
			back.put("episodes", new JsonArray());
			return back;
		}
		if (index < 0)
			return back;
		if (index >= arr.size())
			return back;
		JsonArray copy_arr = arr.deepCopy();
		final JsonObject episode = copy_arr.get(index).getAsJsonObject();
		episode.add("state", new JsonPrimitive("done"));
		copy_arr = addOrReplaceJson(copy_arr, episode, index);
		back.put("index", -1);
		back.put("episodes", copy_arr);
		copy_arr = removePropertyFromAllEpisodes(copy_arr, "splitStack");
		copy_arr = removePropertyFromAllEpisodes(copy_arr, "currentSplit");
		back.put("episodes", copy_arr);
		int currentCountOfType = -1;
		if (limit > -1) {
			if (this.hasJsonProperty(episode, "type")) {
				final Object typeObj = this.getJsonProperty(episode, "type");
				if ((typeObj != null) && ((String.class).isAssignableFrom(typeObj.getClass()))) {
					final String typeStr = (String) typeObj;
					final List<Map<String, String>> filter = new ArrayList<Map<String, String>>();
					final Map<String, String> typeFilter = new HashMap<String, String>();
					typeFilter.put("type", typeStr);
					filter.add(typeFilter);
					typeFilter.put("state", "done");
					filter.add(typeFilter);
					try {
						JsonArray filtered = this.filterArray(copy_arr, filter);
						if (filtered != null) {
							final int currentSize = filtered.size();
							if (currentSize > 0)
								currentCountOfType = currentSize;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		Object splitStackObj = null;
		final Set<String> criteriaToDelete = new HashSet<String>();
		if (this.hasJsonProperty(episode, "splitStack")) {
			splitStackObj = this.getJsonProperty(episode, "splitStack");
		}
		if ((split_type_dict != null) && ((JsonObject.class).isAssignableFrom(split_type_dict.getClass()))) {
			JsonObject type_dict = (JsonObject) split_type_dict;
			for (final Map.Entry<String, JsonElement> typeItem : type_dict.entrySet()) {
				final String type = typeItem.getKey();
				final JsonElement criteriaElem = typeItem.getValue();
				if (!criteriaElem.isJsonObject())
					continue;
				final JsonObject criteria = criteriaElem.getAsJsonObject();
				JsonArray criteria_vars;
				String criteria_timestamp;
				if (criteria.has("SPLIT_VAR")) {
					criteria_vars = criteria.get("SPLIT_VAR").getAsJsonArray();
				} else
					continue;
				if (criteria.has("TIMESTAMP_VAR")) {
					criteria_timestamp = criteria.get("TIMESTAMP_VAR").getAsString();
				} else
					continue;
				criteriaToDelete.add(criteria_timestamp);
				boolean match = false;
				Iterator<JsonElement> varsIt = criteria_vars.iterator();
				while (varsIt.hasNext()) {
					final JsonElement criteria_varElem = varsIt.next();
					if (!criteria_varElem.isJsonObject())
						continue;
					final JsonObject criteria_var = criteria_varElem.getAsJsonObject();
					boolean criteriaMatch = true;
					for (Map.Entry<String, JsonElement> criteria_varItem : criteria_var.entrySet()) {
						final String propertyName = criteria_varItem.getKey();
						criteriaToDelete.add(propertyName);
						final String propertyValue = criteria_varItem.getValue().getAsString();
						if (!episode.has(propertyName))
							criteriaMatch = false;
						else {
							if (!(episode.get(propertyName).getAsString().contentEquals(propertyValue)))
								criteriaMatch = false;
						}
					}
					if (criteriaMatch)
						match = true;
				}
				if (match) {
					if (splitStackObj == null)
						splitStackObj = new JsonObject();
					JsonObject splitStack = (JsonObject) splitStackObj;
					JsonArray types = null;
					if (!episode.has(criteria_timestamp))
						continue;
					if (!this.isWellFormedTimestamp(episode.get(criteria_timestamp).getAsString())) {
						final String illFormedTimestamp = episode.get(criteria_timestamp).getAsString();
						final String repairedTimestamp = illFormedTimestamp.replace("#3A", ":");
						if (this.isWellFormedTimestamp(repairedTimestamp)) {
							episode.addProperty(criteria_timestamp, repairedTimestamp);
						} else
							continue;
					}
					if (splitStack.has(episode.get(criteria_timestamp).getAsString()))
						types = splitStack.get(episode.get(criteria_timestamp).getAsString()).getAsJsonArray();
					if (types == null)
						types = new JsonArray();
					final JsonPrimitive typePrimitive = new JsonPrimitive(type);
					if (!types.contains(typePrimitive))
						types.add(typePrimitive);
					splitStack.add(episode.get(criteria_timestamp).getAsString(), types);
				}
			}
		}
		if (splitStackObj == null)
			return back;
		if (!(JsonObject.class).isAssignableFrom(splitStackObj.getClass()))
			return back;
		final JsonObject splitStack = ((JsonObject) splitStackObj);
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		ArrayList<String> sortedIndex = sortSplitStackIndex(splitStack, format);
		JsonElement currentSplitStack = null;
		String currentSplitDate = null;
		for (final String indexItem : sortedIndex) {
			try {
				final Date indexDate = format.parse(indexItem);
				if (this.isSplittable(episode, indexDate)) {
					currentSplitStack = splitStack.get(indexItem).getAsJsonArray();
					currentSplitDate = indexItem;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			splitStack.remove(indexItem);
			if (currentSplitStack != null) {
				break;
			}
		}
		if (currentSplitStack != null) {
			int splitYear = getYearFromStamp(currentSplitDate);
			int splitMonth = getMonthFromStamp(currentSplitDate);
			try {
				final Map<String, Object> parentMods = new HashMap<String, Object>();
				if (this.hasFlag(episode, "eHO")) {
					JsonArray flagArray = episode.get("flags").getAsJsonArray().deepCopy();
					flagArray.remove(new JsonPrimitive("eHO"));
					parentMods.put("flags", flagArray);
				}
				parentMods.put("splitStack", null);
				parentMods.put("currentSplit", null);
				parentMods.put("state", "done");
				for (final String criteriaVar : criteriaToDelete) {
					parentMods.put(criteriaVar, null);
				}
				final Map<String, Object> childMods = new HashMap<String, Object>();
				if (this.hasFlag(episode, "sHO")) {
					JsonArray flagArray = episode.get("flags").getAsJsonArray().deepCopy();
					flagArray.remove(new JsonPrimitive("sHO"));
					childMods.put("flags", flagArray);
				}
				if (splitStack.size() == 0) {
					childMods.put("splitStack", null);
				} else {
					childMods.put("splitStack", splitStack);
				}
				childMods.put("currentSplit", currentSplitStack.getAsJsonArray());
				childMods.put("state", "new");
				for (final String criteriaVar : criteriaToDelete) {
					childMods.put(criteriaVar, null);
				}
				childMods.put("slot", null);
				childMods.put("color", null);
				if (episode.has("name")) {
					final List<String> names = new ArrayList<String>();
					names.add(episode.get("name").getAsString());
					if (episode.has("childEpisodes")) {
						final JsonElement childsElement = episode.get("childEpisodes");
						if ((childsElement != null) && (childsElement.isJsonArray())) {
							final JsonArray childs = episode.get("childEpisodes").getAsJsonArray();
							final Iterator<JsonElement> childIt = childs.iterator();
							while (childIt.hasNext()) {
								final JsonElement child = childIt.next();
								if (!child.isJsonObject())
									continue;
								final JsonObject childObj = child.getAsJsonObject();
								if (childObj.has("name"))
									names.add(childObj.get("name").getAsString());
							}
						}
					}
					childMods.put("name", this.indexedName(episode.get("name").getAsString(), names));
				}
				if (blacklist != null) {
					for (final String criteriaVar : blacklist) {
						childMods.put(criteriaVar, null);
					}
				}
				final Map<String, JsonElement> splitted = this.splitEpisode(copy_arr, episode, splitYear, splitMonth,
						childMods);
				JsonArray moddedEpisodes = ((JsonArray) splitted.get("data"));
				moddedEpisodes = this.editEpisode(moddedEpisodes,
						this.indexOfJson(moddedEpisodes, ((JsonObject) splitted.get("parent"))), parentMods);
				back.put("index", this.indexOfJson(moddedEpisodes, ((JsonObject) splitted.get("child"))));
				back.put("episodes", moddedEpisodes);
				if (currentCountOfType >= 0 && currentCountOfType >= limit) {
					moddedEpisodes = removePropertyFromAllEpisodes(moddedEpisodes, "splitStack");
					moddedEpisodes = removePropertyFromAllEpisodes(moddedEpisodes, "currentSplit");
					back.put("index", -1);
					back.put("episodes", moddedEpisodes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return back;
	}
	/**
	 *
	 * Method to check if episode has split parent
	 *
	 * @param episode Episode to analyze
	 * @return true if episode contains property parentEpisode with id of parent
	 *         inside, false else
	 */
	public synchronized boolean hasSplitParent(final JsonObject episode) {
		return (getSplitParentId(episode) > -1);
	}
	/**
	 *
	 * Method to extract id of split parent from episode
	 *
	 * @param episode Episode to analyze
	 * @return id of parent episode if episode contains property parentEpisode with
	 *         id of parent inside, empty list else
	 */
	public synchronized Integer getSplitParentId(final JsonObject episode) {
		if (episode == null)
			return -1;
		if (!episode.isJsonObject())
			return -1;
		final JsonObject episodeObj = episode.getAsJsonObject();
		if (!episodeObj.has("parentEpisode"))
			return -1;
		JsonElement parentEpisode = episodeObj.get("parentEpisode");
		if (parentEpisode.isJsonPrimitive()) {
			try {
				return parentEpisode.getAsJsonPrimitive().getAsInt();
			} catch (final NumberFormatException e) {
				LOGGER.info("[getSplitParentId] ({}) {}", parentEpisode.getAsJsonPrimitive(),
						"parent id cannot parsed to int");
			}
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param parentEpisode
	 * @param splitYear
	 * @param splitMonth
	 * @param mods
	 * @return
	 * @throws Exception
	 */
	public synchronized Map<String, JsonElement> splitEpisode(final JsonArray data, final JsonObject parentEpisode,
			final int splitYear, final int splitMonth, final Map<String, Object> mods) throws Exception {
		if (data == null)
			return null;
		if (data.isJsonNull())
			return null;
		final Map<String, JsonElement> back = new HashMap<String, JsonElement>();
		back.put("data", data);
		if (parentEpisode == null) {
			back.put("parent", JsonNull.INSTANCE);
			back.put("child", null);
			return back;
		}
		if (parentEpisode.isJsonNull()) {
			back.put("parent", JsonNull.INSTANCE);
			back.put("child", null);
			return back;
		}
		final int parentIndex = this.indexOfJson(data, parentEpisode);
		final JsonObject childEpisode = new JsonObject();
		for (final String propertyName : parentEpisode.keySet()) {
			if (propertyName.contentEquals("id"))
				continue;
			if (propertyName.contentEquals("childEpisodes"))
				continue;
			if (propertyName.contentEquals("parentEpisode"))
				continue;
			if (propertyName.contentEquals("slot"))
				continue;
			if (propertyName.contentEquals("color"))
				continue;
			final JsonElement parentProperty = parentEpisode.get(propertyName);
			if (parentProperty == null)
				continue;
			;
			if (parentProperty.isJsonNull())
				continue;
			childEpisode.add(propertyName, parentProperty);
		}
		childEpisode.add("id", new JsonPrimitive((maxEpisodeID(data) + 1) + ""));
		childEpisode.add("state", new JsonPrimitive("new"));
		childEpisode.add("parentEpisode", parentEpisode.get("id"));
		JsonArray childList = null;
		if (parentEpisode.has("childEpisodes"))
			childList = parentEpisode.get("childEpisodes").getAsJsonArray();
		if (childList == null)
			childList = new JsonArray();
		childList.add(childEpisode.get("id").getAsString());
		parentEpisode.add("childEpisodes", childList);
		final Calendar cal = new GregorianCalendar();
		cal.set(splitYear, splitMonth, 1, 1, 0, 0);
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
		final String splitDateStr = stampFormat.format(cal.getTime());
		childEpisode.add("startDate", new JsonPrimitive(splitDateStr));
		cal.add(Calendar.DAY_OF_MONTH, -1);
		final String splitDateStr1 = stampFormat.format(cal.getTime());
		parentEpisode.add("endDate", new JsonPrimitive(splitDateStr1));
		data.set(parentIndex, parentEpisode);
		data.add(childEpisode);
		JsonArray data_copy = data.deepCopy();
		if (mods != null) {
			data_copy = this.editEpisode(data_copy, this.indexOfJson(data_copy, childEpisode), mods);
		}
		back.put("data", data_copy);
		back.put("parent", parentEpisode);
		back.put("child", childEpisode);
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public synchronized int maxEpisodeID(final JsonArray data) throws Exception {
		if (data == null)
			return -1;
		if (data.isJsonNull())
			return -1;
		if (data.size() == 0)
			return -1;
		int maxVal = -1;
		for (int index = 0; index < data.size(); index++) {
			final JsonElement tmp = data.get(index);
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (!episode.has("id"))
				continue;
			final int episodeId = episode.get("id").getAsInt();
			maxVal = Math.max(maxVal, episodeId);
		}
		return maxVal;
	}
	/**
	 * [REVIEW][TODO][EXAMPLE] see also:
	 * {@link #addEpisode(com.google.gson.JsonArray, int, int, int, int, java.util.Map)
	 *
	 * @param data
	 * @param startYear
	 * @param startMonth
	 * @param endYear
	 * @param endMonth
	 * @return
	 * @throws Exception
	 */
	/**
	 * Method to add an new Episode to a given JsonArray without map of attribute to
	 * be set in new episode
	 *
	 * @link #addEpisode(final JsonArray data, final int startYear, final int
	 *       startMonth, final int endYear, final int endMonth, final Map < String,
	 *       String > mods)
	 */
	public synchronized JsonObject addEpisode(final JsonArray data, final int startYear, final int startMonth,
			final int endYear, final int endMonth) throws Exception {
		return addEpisode(data, startYear, startMonth, endYear, endMonth, null);
	}
	/**
	 * [REVIEW] Method to add an new Episode to a given JsonArray
	 *
	 * @param data       the given JsonArray
	 * @param startYear  start year of new episode
	 * @param startMonth start month of new episode
	 * @param endYear    end year of new episode
	 * @param endMonth   end month of new episode
	 * @param mods       map of attribute to be set in new episode
	 * @return updated JsonArray incl. new episode, or null in case of data is null
	 *         or has type JsonNull [TODO] (cf 040222) hä? [/TODO]
	 * @ @throws Exception propagates common Exceptions like NullPointer,
	 *           NumberFormat, ...
	 */
	public synchronized JsonObject addEpisode(final JsonArray data, final int startYear, final int startMonth,
			final int endYear, final int endMonth, final Map<String, String> mods) throws Exception {
		if (data == null)
			return null;
		if (data.isJsonNull())
			return null;
		final JsonObject newEpisode = new JsonObject();
		newEpisode.add("id", new JsonPrimitive(maxEpisodeID(data)));
		newEpisode.add("state", new JsonPrimitive("new"));
		if (mods != null) {
			for (final Map.Entry<String, String> mod : mods.entrySet()) {
				newEpisode.add(mod.getKey(), new JsonPrimitive(mod.getValue()));
			}
		}
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final Calendar cal = new GregorianCalendar();
		cal.set(startYear, startMonth, 1, 0, 0, 0);
		newEpisode.add("startDate", new JsonPrimitive(stampFormat.format(cal.getTime())));
		cal.set(endYear, endMonth, lastDayOfMonth(endMonth, endYear), 23, 0, 0);
		newEpisode.add("endDate", new JsonPrimitive(formatDate(lastDayOfMonth(endMonth, endYear), endMonth, endYear)));
		return newEpisode;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param stamp
	 * @return
	 */
	public synchronized int getDayFromStamp(final String stamp) {
		if (stamp == null)
			return -1;
		if (stamp.trim().contentEquals(""))
			return -1;
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			final Date date = stampFormat.parse(stamp);
			final Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			return cal.get(Calendar.DAY_OF_MONTH);
		} catch (Exception e) {
			LOGGER.error("getDayFromStamp failed ", e);
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param stamp
	 * @param day
	 * @return
	 */
	public synchronized String setDayToStamp(final String stamp, final int day) {
		if (stamp == null)
			return "";
		if (stamp.trim().contentEquals(""))
			return "";
		if (day < 0)
			return stamp;
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			final Date date = stampFormat.parse(stamp);
			final Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_MONTH, day);
			return stampFormat.format(cal.getTime());
		} catch (Exception e) {
			LOGGER.error("setDayToStamp failed ", e);
		}
		return stamp;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param stamp1
	 * @param stamp2
	 * @return
	 */
	public synchronized boolean isDateBefore(final String stamp1, final String stamp2) {
		if (stamp1 == null)
			return false;
		if (stamp2 == null)
			return false;
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			final Date date1 = stampFormat.parse(stamp1);
			final Calendar cal1 = new GregorianCalendar();
			cal1.setTime(date1);
			final Date date2 = stampFormat.parse(stamp2);
			final Calendar cal2 = new GregorianCalendar();
			cal2.setTime(date2);
			return cal1.before(cal2);
		} catch (Exception e) {
			LOGGER.error("isDateBefore failed : Stamp1 : " + stamp1 + " Stamp2 : " + stamp2, e);
		}
		return false;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param stamp
	 * @return
	 */
	public synchronized int getMonthFromStamp(final String stamp) {
		if (stamp == null)
			return -1;
		if (stamp.trim().contentEquals(""))
			return -1;
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			final Date date = stampFormat.parse(stamp);
			final Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			final int back = cal.get(Calendar.MONTH);
			return back;
		} catch (Exception e) {
			LOGGER.error("getMonthFromStamp failed ", e);
		}
		return -1;
	}
	public synchronized int getMonthFromJson(final String stamp) {
		if (stamp == null)
			return -1;
		if (stamp.trim().contentEquals(""))
			return -1;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date parse = sdf.parse(stamp);
			Calendar c = Calendar.getInstance();
			c.setTime(parse);
			final int back = c.get(Calendar.MONTH);
			return back;
		} catch (Exception e) {
			LOGGER.error("getMonthFromStamp failed ", e);
		}
		return -1;
	}
	public synchronized String getMonthLabelFromJson(final String stamp) {
		if (stamp == null)
			return "";
		if (stamp.trim().contentEquals(""))
			return "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date parse = sdf.parse(stamp);
			Calendar c = Calendar.getInstance();
			c.setTime(parse);
			return "" + new SimpleDateFormat("MMM").format(c.getTime());
		} catch (Exception e) {
			LOGGER.error("getMonthFromStamp failed ", e);
		}
		return "";
	}
	public synchronized int getYearFromJson(final String stamp) {
		if (stamp == null)
			return -1;
		if (stamp.trim().contentEquals(""))
			return -1;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date parse = sdf.parse(stamp);
			Calendar c = Calendar.getInstance();
			c.setTime(parse);
			final int back = c.get(Calendar.YEAR);
			return back;
		} catch (Exception e) {
			LOGGER.error("getYearFromStamp failed ", e);
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param stamp
	 * @param month
	 * @return
	 */
	public synchronized String setMonthToStamp(final String stamp, final int month) {
		if (stamp == null)
			return null;
		if (month < 0)
			return stamp;
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			final Date date = stampFormat.parse(stamp);
			final Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.set(Calendar.MONTH, month);
			return stampFormat.format(cal.getTime());
		} catch (Exception e) {
			LOGGER.error("setMonthToStamp failed ", e);
		}
		return stamp;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param stamp
	 * @return
	 */
	public synchronized int getYearFromStamp(final String stamp) {
		if (stamp == null)
			return -1;
		if (stamp.trim().contentEquals(""))
			return -1;
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			final Date date = stampFormat.parse(stamp);
			final Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			return cal.get(Calendar.YEAR);
		} catch (Exception e) {
			LOGGER.error("getYearFromStamp failed ", e);
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param stamp
	 * @param year
	 * @return
	 */
	public synchronized String setYearToStamp(final String stamp, final int year) {
		if (stamp == null)
			return "";
		if (stamp.trim().contentEquals(""))
			return "";
		if (year < 0)
			return stamp;
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			final Date date = stampFormat.parse(stamp);
			final Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.set(Calendar.YEAR, year);
			return stampFormat.format(cal.getTime());
		} catch (Exception e) {
			LOGGER.error("setYearToStamp failed ", e);
		}
		return stamp;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param stopIndex
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized int beforeEpisodeIndex(final JsonArray data, final int stopIndex,
			final Map<String, String> filter) throws Exception {
		if (data == null)
			return -1;
		if (data.size() == 0)
			return -1;
		for (int index = Math.min(data.size() - 1, stopIndex); index >= 0; index--) {
			final JsonElement tmp = data.get(index);
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			boolean match = true;
			for (final Map.Entry<String, String> filterItem : filter.entrySet()) {
				if (!episode.has(filterItem.getKey())) {
					match = false;
					continue;
				}
				final JsonElement propertyElement = episode.get(filterItem.getKey());
				if (!(propertyElement.isJsonPrimitive() || propertyElement.isJsonArray())) {
					match = false;
					continue;
				}
				if (propertyElement.isJsonPrimitive()) {
					if (!propertyElement.getAsString().contentEquals(filterItem.getValue())) {
						match = false;
						continue;
					}
				}
				if (propertyElement.isJsonArray()) {
					final JsonPrimitive filterObj = new JsonPrimitive(filterItem.getValue());
					if (!propertyElement.getAsJsonArray().contains(filterObj)) {
						match = false;
						continue;
					}
				}
			}
			if (match)
				return index;
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param stopIndex
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized int beforeEpisodeIndex(final JsonArray data, final int stopIndex, final String property,
			final String value) throws Exception {
		if (data == null)
			return -1;
		if (data.size() == 0)
			return -1;
		if (property == null)
			return -1;
		if (value == null)
			return -1;
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		return beforeEpisodeIndex(data, stopIndex, filter);
	}
//	https://github.com/dzhw/slc_abs21/issues/40#issuecomment-1287039651
	public synchronized String displayEpisodesStuInt(final JsonArray arr,final String language, final int resultsIndex) {
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<>();
		filter.put("type", "stu");
		filter.put("state", "done");
		filter.put("stu021", "ao2");
		filterList.add(filter);
		final Map<String, String> filter2 = new HashMap<>();
		filter2.put("type", "int");
		filter2.put("state", "done");
		filter2.put("int009", "ao2");
		filterList.add(filter2);
		final ArrayList<String> additionalProperties = new ArrayList<String>();
		additionalProperties.add("stu025");
		additionalProperties.add("stu024");
		additionalProperties.add("int013");
		additionalProperties.add("int012");
		return displayEpisodesIndex(arr, 0, filterList, additionalProperties,language, resultsIndex);
	}
	public synchronized String displayEpisodesStu(final JsonArray arr,final String language) {
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<>();
		filter.put("type", "stu");
		filter.put("state", "done");
		filter.put("stu021", "ao2");
		filterList.add(filter);
		final ArrayList<String> additionalProperties = new ArrayList<String>();
		additionalProperties.add("stu025");
		additionalProperties.add("stu024");
		return displayEpisodes(arr, 0, filterList, additionalProperties,language);
	}
//	https://github.com/dzhw/slc_abs21/issues/40#issuecomment-1287039651
	public synchronized String displayEpisodesInt(final JsonArray arr,final String language) {
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<>();
		filter.put("type", "int");
		filter.put("state", "done");
		filter.put("int009", "ao2");
		filterList.add(filter);
		final ArrayList<String> additionalProperties = new ArrayList<String>();
		additionalProperties.add("int013");
		additionalProperties.add("int012");
		return displayEpisodes(arr, 0, filterList, additionalProperties,language);
	}
	/**
	 * @param arr                  Episodes
	 * @param startIndex           Index of Episode to start with
	 * @param property             single filter property
	 * @param value                Value of single filter property
	 * @param additionalProperties Properties to display additional to type
	 *                             startMonth, startYear, endMond and end Year
	 * @return String (additionals, type <br>
	 *         startMonth.startYear<br>
	 *         endMonth.endYear)
	 */
	public synchronized String displayEpisodes(final JsonArray arr, final int startIndex, final String property,
			final String value, final ArrayList<String> additionalProperties,final String language) {
		if (arr == null)
			return "";
		if (arr.size() == 0)
			return "";
		if (arr.size() <= startIndex)
			return "";
		if (property == null)
			return "";
		if (value == null)
			return "";
		final List<Map<String, String>> filterList = new ArrayList<>();
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		filterList.add(filter);
		return displayEpisodes(arr, startIndex, filterList, additionalProperties,language);
	}
	/**
	 * @param arr                  Episodes
	 * @param startIndex           Index of Episode to start with
	 * @param filterList           Map of filter Criteria (AND connected)
	 * @param additionalProperties Properties to display additional to type
	 *                             startMonth, startYear, endMond and end Year
	 * @return String (additionals, type <br>
	 *         startMonth.startYear<br>
	 *         endMonth.endYear)
	 */
	public synchronized String displayEpisodes(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList, final ArrayList<String> additionalProperties,final String language) {
		if (arr == null)
			return "";
		if (arr.size() == 0)
			return "";
		if (arr.size() <= startIndex)
			return "";
		if (filterList == null)
			return "";
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final int count = arr.size();
		final Map<JsonObject, Integer> indexMap = new LinkedHashMap<JsonObject, Integer>();
		final Map<Date, List<JsonObject>> startMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Date, List<JsonObject>> endMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Integer, List<JsonObject>> idMap = new LinkedHashMap<Integer, List<JsonObject>>();
		for (int index = Math.max(0, startIndex); index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			indexMap.put(episode, index);
			if (episode.has("startDate") && episode.get("startDate").isJsonPrimitive()) {
				final String dateStr = episode.get("startDate").getAsJsonPrimitive().getAsString();
				if (this.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (startMap.containsKey(date))
							tmp = startMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						startMap.put(date, tmp);
					}
				}
			}
			if (episode.has("endDate") && episode.get("endDate").isJsonPrimitive()) {
				final String dateStr = episode.get("endDate").getAsJsonPrimitive().getAsString();
				if (this.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (endMap.containsKey(date))
							tmp = endMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						endMap.put(date, tmp);
					}
				}
			}
			if (episode.has("id") && episode.get("id").isJsonPrimitive()) {
				final String idStr = episode.get("id").getAsJsonPrimitive().getAsString();
				Integer id = null;
				try {
					id = Integer.parseInt(idStr);
				} catch (Exception e) {
				}
				if (id != null) {
					List<JsonObject> tmp = null;
					if (idMap.containsKey(id))
						tmp = idMap.get(id);
					if (tmp == null)
						tmp = new ArrayList<JsonObject>();
					if (!tmp.contains(episode))
						tmp.add(episode);
					idMap.put(id, tmp);
				}
			}
		}
		final Map<Date, List<JsonObject>> sortedEndMap = new TreeMap<Date, List<JsonObject>>(endMap);
		final List<JsonObject> results = new ArrayList<JsonObject>();
		for (final Map.Entry<Date, List<JsonObject>> x : sortedEndMap.entrySet()) {
			final List<JsonObject> episodes = x.getValue();
			for (final JsonObject episode : episodes) {
				if (!episode.isJsonObject())
					continue;
				if (!jsonObjectMatchesFilter(episode, filterList))
					continue;
				results.add(episode);
			}
		}
		if (results.size() == 1) {
			return displayEpisodesHelper(results.get(0), additionalProperties,language);
		} else if (results.size() > 1) {
			final Map<Date, List<JsonObject>> sortedStartMap = new TreeMap<Date, List<JsonObject>>(startMap);
			final List<JsonObject> results1 = new ArrayList<JsonObject>();
			for (final Map.Entry<Date, List<JsonObject>> x : sortedStartMap.entrySet()) {
				final List<JsonObject> episodes1 = x.getValue();
				for (final JsonObject episode : episodes1) {
					if (results.contains(episode)) {
						results1.add(episode);
					}
				}
			}
			if (results1.size() == 1) {
				return displayEpisodesHelper(results1.get(0), additionalProperties,language);
			} else if (results1.size() > 1) {
				Map<Integer, List<JsonObject>> sortedIdMap = new TreeMap<Integer, List<JsonObject>>(idMap);
				final List<JsonObject> results2 = new ArrayList<JsonObject>();
				for (final Map.Entry<Integer, List<JsonObject>> x : sortedIdMap.entrySet()) {
					final List<JsonObject> episodes1 = x.getValue();
					for (final JsonObject episode : episodes1) {
						if (results1.contains(episode)) {
							results2.add(episode);
						}
					}
				}
				if (results2.size() == 1) {
					return displayEpisodesHelper(results2.get(0), additionalProperties,language);
				} else if (results2.size() > 1) {
					final StringBuffer back = new StringBuffer();
					boolean first = true;
					for(final JsonObject episode : results2) {
						if(!first) back.append("\n<br>");
						back.append(displayEpisodesHelper(episode, additionalProperties,language));
						first = false;
					}
					return back.toString();
				}
			}
		}
		return "";
	}
	/**
	 * @param arr                  Episodes
	 * @param startIndex           Index of Episode to start with
	 * @param filterList           Map of filter Criteria (AND connected)
	 * @param additionalProperties Properties to display additional to type
	 *                             startMonth, startYear, endMond and end Year
	 * @return String (additionals, type <br>
	 *         startMonth.startYear<br>
	 *         endMonth.endYear)
	 */
	public synchronized String displayEpisodesIndex(final JsonArray arr, final int startIndex,
													final List<Map<String, String>> filterList, final ArrayList<String> additionalProperties,
													final String language, final int resultsIndex) {
		if (arr == null)
			return "";
		if (arr.size() == 0)
			return "";
		if (arr.size() <= startIndex)
			return "";
		if (filterList == null)
			return "";
		final Map<JsonObject, Integer> indexMap = new LinkedHashMap<JsonObject, Integer>();
		final Map<Date, List<JsonObject>> startMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Date, List<JsonObject>> endMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Integer, List<JsonObject>> idMap = new LinkedHashMap<Integer, List<JsonObject>>();
		final JsonArray filteredArray = this.filterArray(arr, filterList);
		final JsonArray sortedArray = this.sortedLikeNextEpisodeIndex(filteredArray);
		if (resultsIndex < sortedArray.size()) {
			return displayEpisodesHelper(sortedArray.get(resultsIndex).getAsJsonObject(), additionalProperties,language);
		}
		return "";
	}
	private synchronized String displayEpisodesHelper(final JsonObject episode,
			final ArrayList<String> additionalProperties,final String language) {
		final JsonParser jsonParser = new JsonParser();
		final JsonObject config = (JsonObject) jsonParser.parse(
				"{'translation':{'de':{'shortmonth':{'1':'Jan', '2':'Feb', '3':'Mär', '4':'Apr', '5':'Mai', '6':'Jun', '7':'Jul', '8':'Aug', '9':'Sep', '10':'Okt', '11':'Nov', '12':'Dez'}, 'month':{'1':'Januar', '2':'Februar', '3':'März', '4':'April','5':'Mai', '6':'Juni', '7':'Juli', '8':'August', '9':'September',  '10':'Oktober', '11':'November', '12':'Dezember'}}, 'en':{'shortmonth':{'1':'Jan', '2':'Feb', '3':'Mar', '4':'Apr', '5':'May', '6':'Jun', '7':'Jul', '8':'Aug', '9':'Sept', '10':'Oct', '11':'Nov', '12':'Dec'}, 'month':{'1':'January', '2':'February', '3':'March', '4':'April','5':'May', '6':'June', '7':'July', '8':'August', '9':'September', '10':'October', '11':'November', '12':'December'}}}, 'actions':{'update':'episodeUpdateDispatcher','edit':'episodeEdit','delete':'delEpisode'}, 'details':{'SlotDefault':[{'property':'name','label': {'de': 'Bezeichnung', 'en': 'title'}}],'sco':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'sco008','label':{'de':'Schulart','en':'Type of school'}},{'property':'sco014','label':{'de':'Ort','en':'City'}},{'property':'sco015','label':{'de':'PLZ','en':'zip code'}},{'property':'sco015','label':{'de':'Land','en':'Country'}},{'property':'sco016','label':{'de':'Ort','en':'City'}}],'stu':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'stu008','label':{'de':'Studienabschluss','en':'Degree'}},{'property':'stu013','label':{'de':'Studiengang','en':'Study program'}},{'property':'stu014','label':{'de':'Hauptfach 1','en':'Main subject 1'}},{'property':'stu015','label':{'de':'Hauptfach 2','en':'Main subject 2'}},{'property':'stu020','label':{'de':'Hochschule','en':'Higher education institution'}},{'property':'stu021','label':{'de':'Ort','en':'City'}},{'property':'stu024','label':{'de':'Land','en':'Country'}},{'property':'stu025','label':{'de':'Ort','en':'City'}}],'voc':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'voc008','label':{'de':'Art der Ausbildung/Umschulung','en':'Type of vocational training'}},{'property':'voc009','label':{'de':'Ausbildungsberuf','en':'Training occupation'}},{'property':'voc014','label':{'de':'Ort','en':'City'}},{'property':'voc015','label':{'de':'PLZ','en':'zip code'}},{'property':'voc015','label':{'de':'Land','en':'Country'}},{'property':'voc016','label':{'de':'Ort','en':'City'}}],'int':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'int008','label':{'de':'Art des Praktikums','en':'Type of internship'}},{'property':'int010','label':{'de':'Ort','en':'City'}},{'property':'int011','label':{'de':'PLZ','en':'zip code'}},{'property':'int012','label':{'de':'Land','en':'Country'}},{'property':'int013','label':{'de':'Ort','en':'City'}}],'fed':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'fed008','label':{'de':'Art der Fort-/Weiterbildung','en':'Type of further education'}}],'job':[{'property':'name','label': {'de': 'Name', 'en': 'name'}}],'emp':[{'property':'name','label': {'de': 'Arbeitgeber', 'en': 'employer'}},{'property':'emp009','label':{'de':'Beruf ','en':'Occupation'}},{'property':'emp014','label':{'de':'Berufliche Stellung','en':'Occupational status'}},{'property':'emp015','label':{'de':'im Detail','en':'in detail'}},{'property':'emp020','label':{'de':'Ort','en':'City'}},{'property':'emp021','label':{'de':'PLZ','en':'zip code'}},{'property':'emp022','label':{'de':'Land','en':'Country'}},{'property':'emp023','label':{'de':'Ort','en':'City'}},{'property':'emp027','label':{'de':'Vertragsart','en':'Type of contract'}},{'property':'emp031','label':{'de':'Arbeitsverhältnis','en':'Employment relationship'}},{'property':'emp036','label':{'de':'Arbeitsumfang','en':'Work scope'}}],'sem':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'sem016','label':{'de':'Beruf ','en':'Occupation'}},{'property':'sem019','label':{'de':'Ort','en':'City'}},{'property':'sem020','label':{'de':'PLZ','en':'zip code'}},{'property':'sem021','label':{'de':'Land','en':'Country'}},{'property':'sem022','label':{'de':'Ort','en':'City'}}],'doc':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'doc011','label':{'de':'Promotionsfach','en':'Doctoral subject'}},{'property':'doc016','label':{'de':'Hochschule','en':'Higher education institution'}},{'property':'doc018','label':{'de':'Ort','en':'City'}},{'property':'doc020','label':{'de':'Land','en':'Country'}},{'property':'doc021','label':{'de':'Ort','en':'City'}}],'mpl':[{'property':'name','label': {'de': 'Name', 'en': 'name'}}],'fam':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'car008','label':{'de':'Häusliche Pflege','en':'Informal care'}}],'uem':[{'property':'name','label': {'de': 'Name', 'en': 'name'}}],'oth':[{'property':'name','label': {'de': 'Name', 'en': 'name'}},{'property':'oth008','label':{'de':'Art der sonstigen Tätigkeit','en':'Type of other activity'}},{'property':'oth011','label':{'de':'Ort','en':'City'}},{'property':'oth012','label':{'de':'PLZ','en':'zip code'}},{'property':'oth013','label':{'de':'Land','en':'Country'}},{'property':'oth014','label':{'de':'Ort','en':'City'}}]},'type':{'label': {'de':'Art','en':'Type'}, 'values' : [{'label': {'de': 'Bitte auswählen','en': 'please select'},'id': 'SlotDefault','value': 'SlotDefault','color': '#4b4b4b'},{'label': {'de': 'Schule (Abitur/Fachabitur)','en': 'School (higher education entrance qualification)'},'id': 'sco','value': 'Slot1','color': '#f69f26'},{'label': {'de': 'Studium','en': 'Study'},'id': 'stu','value': 'Slot2','color': '#0069b2'},{'label': {'de': 'Berufsausbildung, Umschulung, Volontariat','en': 'Vocational training, occupational retraining, traineeship (&#8220;Volontariat&#8221;)'},'id': 'voc','value': 'Slot3','color': '#097d95'},{'label': {'de': 'Praktikum','en': 'Internship'},'id': 'int','value': 'Slot7','color': '#CCE1F0'},{'label': {'de': 'Fort-, Weiterbildung (längerfristig, insgesamt mind. 70h)','en': 'Further education (long-term, all together at least 70h)'},'id': 'fed','value': 'Slot4','color': '#FCD8A7'},{'label': {'de': 'Jobben','en': 'Jobbing'},'id': 'job','value': 'Slot5','color': '#F9F391'},{'label': {'de': 'nichtselbständige Erwerbstätigkeit (auch Vorbereitungsdienste wie Referendariat o.Ä.)','en': 'Employment (also preparatory services such as traineeship or similar)'},'id': 'emp','value': 'Slot6','color': '#7ab52a'},{'label': {'de': 'Selbständigkeit (auch Honorar- und Werksverträge)','en': 'Self-employment'},'id': 'sem','value': 'Slot13','color': '#CFC60D'},{'label': {'de': 'Promotion','en': 'Doctorate'},'id': 'doc','value': 'Slot8','color': '#b5163f'},{'label': {'de': 'Elternzeit/Mutterschutz','en': 'Parental/maternity leave'},'id': 'mpl','value': 'Slot9','color': '#D16021'},{'label': {'de': 'Familientätigkeit, häusliche Pflege, Hausmann/-frau','en': 'Family work, care of relatives or acquaintances, househusband/-wife'},'id': 'fam','value': 'Slot10','color': '#79a0c0'},{'label': {'de': 'Arbeitslosigkeit','en': 'Unemployment'},'id': 'uem','value': 'Slot11','color': '#DCE9C4'},{'label': {'de': 'Sonstiges (z.B. Urlaub/Reisen, Krankheit, Studienvorbereitung, Freiwilligendienst)','en': 'Other activity (e.g. vacation/travel, disease, preparation for studies, voluntary service)'},'id': 'oth','value': 'Slot12','color': '#053E49'}], 'description': {'voc':{'de':'Nicht gemeint sind Weiterbildungen (z.B. Facharzt/-ärztin) oder Aufstiegsfortbildungen.','en': 'This does not include further training (e.g. medical specialist) or advanced further training.'},'emp':{'de':'Legen Sie für jede*n Arbeitgeber*in einen eigenen Zeitraum an. Tragen Sie den Namen des/der Arbeitgebers/Arbeitgeberin ein oder ein für Sie verständliches Synonym.','en': 'Please create a separate period for each employer. Enter the name of the employer or a synonym that you understand.'},'oth':{'de':'Geben Sie bitte für die folgenden Tätigkeiten getrennte Zeiträume an: Freiwilligendienst, AuPair, Work &amp; Travel, längere Krankheit, Urlaub, Sonstiges','en': 'Please indicate separate periods for the following activities: Volunteer service, AuPair, Work &amp; Travel, longer illness, vacation, other.'}}}, 'name':{'label':{'de':'','en':'' }, 'values':'Name','description':{'SlotDefault': {'de': 'Bezeichnung', 'en': 'title'},'sco': {'de': 'Name', 'en': 'name'},'stu': {'de': 'Name', 'en': 'name'},'voc': {'de': 'Name', 'en': 'name'},'int': {'de': 'Name', 'en': 'name'},'fed': {'de': 'Name', 'en': 'name'},'job': {'de': 'Name', 'en': 'name'},'emp': {'de': 'Arbeitgeber', 'en': 'employer'},'sem': {'de': 'Name', 'en': 'name'},'doc': {'de': 'Name', 'en': 'name'},'mpl': {'de': 'Name', 'en': 'name'},'fam': {'de': 'Name', 'en': 'name'},'uem': {'de': 'Name', 'en': 'name'},'oth': {'de': 'Name', 'en': 'name'}}}}");
		return this.displayEpisodesHelper(episode, additionalProperties, language, config);
	}
	private synchronized String displayEpisodesHelper(final JsonObject episode,
			final ArrayList<String> additionalProperties, final String language, final JsonObject config) {
		if (episode == null)
			return "";
		final String type = labelOfType(config.get("type").getAsJsonObject().get("values").getAsJsonArray(),
				episode.get("type").getAsString(), language);
		final String startMonth = config.get("translation").getAsJsonObject().get(language).getAsJsonObject()
				.get("shortmonth").getAsJsonObject()
				.get((getMonthFromJson(episode.get("startDate").getAsString()) + 1) + "").getAsString();
		final int startYear = getYearFromJson(episode.get("startDate").getAsString());
		final String endMonth = config.get("translation").getAsJsonObject().get(language).getAsJsonObject()
				.get("shortmonth").getAsJsonObject()
				.get((getMonthFromJson(episode.get("endDate").getAsString()) + 1) + "").getAsString();
		final int endYear = getYearFromJson(episode.get("endDate").getAsString());
		final Map<String, String> details = new LinkedHashMap<String, String>();
		for (final String detailVar : additionalProperties) {
			if (!hasJsonProperty(episode, detailVar)) {
			}
			else {
				details.put(detailVar, unQuoteCharJson(labelOf(detailVar, episode.get(detailVar).getAsString())));
			}
		}
		final StringBuffer back = new StringBuffer();
		for (final Map.Entry<String, String> detailVar : details.entrySet()) {
			if (back.length() > 0) back.append(", ");
			back.append(unescapeSpecialChars(detailVar.getValue()));
		}
		if (back.length() > 0) back.append(", ");
		back.append(unescapeSpecialChars(type) + ", ");
		if (startYear > 1970 && startYear < 4000) back.append(startMonth + ". " + startYear);
		else {
			if (language.equals("de")) back.append("unbekanntes Startdatum");
			else if (language.equals("en")) back.append("unknown start date");
		}
		back.append(" - ");
		if (endYear > 1970 && endYear < 4000) back.append(endMonth + ". " + endYear);
		else {
			if (language.equals("de")) back.append("unbekanntes Enddatum");
			else if (language.equals("en")) back.append("unknown end date");
		}
		return back.toString();
	}
	public synchronized JsonArray sortedLikeNextEpisodeIndex(final JsonArray arr, final List<Object> types) {
		if (arr == null)
			return null;
		if (arr.size() == 0)
			return arr;
		if (types == null)
			return sortedLikeNextEpisodeIndex(arr);
		if (types.size() == 0)
			return sortedLikeNextEpisodeIndex(arr);
		;
		final int count = arr.size();
		final Map<String, JsonArray> typeMap = new LinkedHashMap<String, JsonArray>();
		for (int index = 0; index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			if (episode.has("type") && episode.get("type").isJsonPrimitive()) {
				final String typeStr = episode.get("type").getAsJsonPrimitive().getAsString();
				if (typeStr != null) {
					JsonArray tmp = null;
					if (typeMap.containsKey(typeStr))
						tmp = typeMap.get(typeStr);
					if (tmp == null)
						tmp = new JsonArray();
					if (!tmp.contains(episode))
						tmp.add(episode);
					typeMap.put(typeStr, tmp);
				}
			}
		}
		final JsonArray back = new JsonArray();
		if((types != null)&&(!types.isEmpty())) {
			for (final Object type : types) {
				final String typeStr = (String)type;
				final JsonArray episodes = typeMap.get(typeStr);
				if(episodes == null)continue;
				final JsonArray sorted = sortedLikeNextEpisodeIndex(episodes);
				if(sorted == null)continue;
				back.addAll(sorted);
			}
		}
		return back;
	}
	public synchronized JsonArray sortedLikeNextEpisodeIndex(final JsonArray arr) {
		if (arr == null)
			return null;
		if (arr.size() == 0)
			return arr;
		ArrayList<JsonObject> array = new ArrayList<JsonObject>();
		for (int i = 0; i < arr.size(); i++) {
			array.add(arr.get(i).getAsJsonObject());
		}
		try {
			Collections.sort(array, new Comparator<JsonObject>() {
				@Override
				public int compare(JsonObject lhs, JsonObject rhs) {
					if (lhs.get("startDate").getAsString().compareTo(rhs.get("startDate").getAsString()) == 0) {
						if (lhs.get("endDate").getAsString().compareTo(rhs.get("endDate").getAsString()) == 0) {
							return lhs.get("id").getAsString().compareTo(rhs.get("id").getAsString());
						}
						else return lhs.get("endDate").getAsString().compareTo(rhs.get("endDate").getAsString());
					}
					else return lhs.get("startDate").getAsString().compareTo(rhs.get("startDate").getAsString());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return arr;
		}
		JsonArray jsonArray = new JsonArray();
		for (final JsonObject episode: array) {
			jsonArray.add(episode);
		}
		return jsonArray;
	}
	/**
	 * returns the index of the next json element that matches the criteria defined
	 * in filterList; tries to find the episode with earliest startDate; if more
	 * than one episode with the same lowest startDate is found, the index of the
	 * episode with the lowest endDate is returned; if there is more than one
	 * episode with the same lowest startDate and lowest endDate, the episode with
	 * the lowest id is returned.
	 *
	 * @param arr        JsonArray with episodes
	 * @param startIndex start index where we start to look for episodes
	 * @param filterList
	 * @return
	 * 
	 */
	public synchronized int nextEpisodeIndex(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList) {
		if (arr == null)
			return -1;
		if (arr.size() == 0)
			return -1;
		if (arr.size() <= startIndex)
			return -1;
		if (filterList == null)
			return -1;
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final int count = arr.size();
		final Map<JsonObject, Integer> indexMap = new LinkedHashMap<JsonObject, Integer>();
		final Map<Date, List<JsonObject>> startMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Date, List<JsonObject>> endMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Integer, List<JsonObject>> idMap = new LinkedHashMap<Integer, List<JsonObject>>();
		for (int index = Math.max(0, startIndex); index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			indexMap.put(episode, index);
			if (episode.has("startDate") && episode.get("startDate").isJsonPrimitive()) {
				final String dateStr = episode.get("startDate").getAsJsonPrimitive().getAsString();
				if (this.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (startMap.containsKey(date))
							tmp = startMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						startMap.put(date, tmp);
					}
				}
			}
			if (episode.has("endDate") && episode.get("endDate").isJsonPrimitive()) {
				final String dateStr = episode.get("endDate").getAsJsonPrimitive().getAsString();
				if (this.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (endMap.containsKey(date))
							tmp = endMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						endMap.put(date, tmp);
					}
				}
			}
			if (episode.has("id") && episode.get("id").isJsonPrimitive()) {
				final String idStr = episode.get("id").getAsJsonPrimitive().getAsString();
				Integer id = null;
				try {
					id = Integer.parseInt(idStr);
				} catch (Exception e) {
				}
				if (id != null) {
					List<JsonObject> tmp = null;
					if (idMap.containsKey(id))
						tmp = idMap.get(id);
					if (tmp == null)
						tmp = new ArrayList<JsonObject>();
					if (!tmp.contains(episode))
						tmp.add(episode);
					idMap.put(id, tmp);
				}
			}
		}
		final Map<Date, List<JsonObject>> sortedEndMap = new TreeMap<Date, List<JsonObject>>(endMap);
		final List<JsonObject> results = new ArrayList<JsonObject>();
		for (final Map.Entry<Date, List<JsonObject>> x : sortedEndMap.entrySet()) {
			final List<JsonObject> episodes = x.getValue();
			for (final JsonObject episode : episodes) {
				if (!episode.isJsonObject())
					continue;
				if (!jsonObjectMatchesFilter(episode, filterList))
					continue;
				results.add(episode);
			}
		}
		if (results.size() == 1) {
			return indexMap.get(results.get(0));
		} else if (results.size() > 1) {
			final Map<Date, List<JsonObject>> sortedStartMap = new TreeMap<Date, List<JsonObject>>(startMap);
			final List<JsonObject> results1 = new ArrayList<JsonObject>();
			for (final Map.Entry<Date, List<JsonObject>> x : sortedStartMap.entrySet()) {
				final List<JsonObject> episodes1 = x.getValue();
				for (final JsonObject episode : episodes1) {
					if (results.contains(episode)) {
						results1.add(episode);
					}
				}
			}
			if (results1.size() == 1) {
				return indexMap.get(results1.get(0));
			} else if (results1.size() > 1) {
				Map<Integer, List<JsonObject>> sortedIdMap = new TreeMap<Integer, List<JsonObject>>(idMap);
				final List<JsonObject> results2 = new ArrayList<JsonObject>();
				for (final Map.Entry<Integer, List<JsonObject>> x : sortedIdMap.entrySet()) {
					final List<JsonObject> episodes1 = x.getValue();
					for (final JsonObject episode : episodes1) {
						if (results1.contains(episode)) {
							results2.add(episode);
						}
					}
					if (!results2.isEmpty())
						break;
				}
				if (results2.size() == 1) {
					return indexMap.get(results2.get(0));
				} else if (results2.size() > 1) {
					System.err.println("there are multiple episodes with the same id : " + results2);
				}
			}
		}
		return -1;
	}
	/**
	 * returns the index of the next json element that matches the criteria defined
	 * in filterList; tries to find the episode with latest endDate; if more than
	 * one episode with the same latest endDate are found, the index of the episode
	 * with the latest startDate is returned; if there is more than one episode with
	 * the same latest endDate and latest startDate, the episode with the highest id
	 * is returned.
	 *
	 * @param arr        JsonArray with episodes
	 * @param startIndex start index where we start to look for episodes
	 * @param filterList
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndexReversed(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList) {
		if (arr == null)
			return -1;
		if (arr.size() == 0)
			return -1;
		if (arr.size() <= startIndex)
			return -1;
		if (filterList == null)
			return -1;
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final int count = arr.size();
		final Map<JsonObject, Integer> indexMap = new LinkedHashMap<JsonObject, Integer>();
		final Map<Date, List<JsonObject>> startMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Date, List<JsonObject>> endMap = new LinkedHashMap<Date, List<JsonObject>>();
		final Map<Integer, List<JsonObject>> idMap = new LinkedHashMap<Integer, List<JsonObject>>();
		for (int index = Math.max(0, startIndex); index < count; index++) {
			final JsonElement element = arr.get(index);
			if (!element.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) element;
			indexMap.put(episode, index);
			if (episode.has("startDate") && episode.get("startDate").isJsonPrimitive()) {
				final String dateStr = episode.get("startDate").getAsJsonPrimitive().getAsString();
				if (this.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (startMap.containsKey(date))
							tmp = startMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						startMap.put(date, tmp);
					}
				}
			}
			if (episode.has("endDate") && episode.get("endDate").isJsonPrimitive()) {
				final String dateStr = episode.get("endDate").getAsJsonPrimitive().getAsString();
				if (this.isTimeStampParseable(dateStr)) {
					Date date = null;
					try {
						date = simpleDateFormat.parse(dateStr);
					} catch (Exception e) {
					}
					if (date != null) {
						List<JsonObject> tmp = null;
						if (endMap.containsKey(date))
							tmp = endMap.get(date);
						if (tmp == null)
							tmp = new ArrayList<JsonObject>();
						if (!tmp.contains(episode))
							tmp.add(episode);
						endMap.put(date, tmp);
					}
				}
			}
			if (episode.has("id") && episode.get("id").isJsonPrimitive()) {
				final String idStr = episode.get("id").getAsJsonPrimitive().getAsString();
				Integer id = null;
				try {
					id = Integer.parseInt(idStr);
				} catch (Exception e) {
				}
				if (id != null) {
					List<JsonObject> tmp = null;
					if (idMap.containsKey(id))
						tmp = idMap.get(id);
					if (tmp == null)
						tmp = new ArrayList<JsonObject>();
					if (!tmp.contains(episode))
						tmp.add(episode);
					idMap.put(id, tmp);
				}
			}
		}
		Map<Date, List<JsonObject>> sortedStartMap = new TreeMap<Date, List<JsonObject>>(startMap).descendingMap();
		final List<JsonObject> results = new ArrayList<JsonObject>();
		for (final Map.Entry<Date, List<JsonObject>> x : sortedStartMap.entrySet()) {
			final Date startDate = x.getKey();
			final List<JsonObject> episodes = x.getValue();
			for (final JsonObject episode : episodes) {
				if (!episode.isJsonObject())
					continue;
				if (!jsonObjectMatchesFilter(episode, filterList))
					continue;
				results.add(episode);
			}
		}
		if (results.size() == 1) {
			return indexMap.get(results.get(0));
		} else if (results.size() > 1) {
			Map<Date, List<JsonObject>> sortedEndMap = new TreeMap<Date, List<JsonObject>>(endMap).descendingMap();
			final List<JsonObject> results1 = new ArrayList<JsonObject>();
			for (final Map.Entry<Date, List<JsonObject>> x : sortedEndMap.entrySet()) {
				final Date endDate = x.getKey();
				final List<JsonObject> episodes1 = x.getValue();
				for (final JsonObject episode : episodes1) {
					if (results.contains(episode)) {
						results1.add(episode);
					}
				}
				if (!results1.isEmpty())
					break;
			}
			if (results1.size() == 1) {
				return indexMap.get(results1.get(0));
			} else if (results1.size() > 1) {
				Map<Integer, List<JsonObject>> sortedIdMap = new TreeMap<Integer, List<JsonObject>>(idMap)
						.descendingMap();
				final List<JsonObject> results2 = new ArrayList<JsonObject>();
				for (final Map.Entry<Integer, List<JsonObject>> x : sortedIdMap.entrySet()) {
					final Integer id = x.getKey();
					final List<JsonObject> episodes1 = x.getValue();
					for (final JsonObject episode : episodes1) {
						if (results1.contains(episode)) {
							results2.add(episode);
						}
					}
					if (!results2.isEmpty())
						break;
				}
				if (results2.size() == 1) {
					return indexMap.get(results2.get(0));
				} else if (results2.size() > 1) {
					System.err.println("there are multiple episodes width the same id : " + results2);
				}
			}
		}
		return -1;
	}
	/**
	 * return the index of the next json element that matches the criteria defined
	 * in filterList
	 *
	 * @param arr        JsonArray with episodes
	 * @param filterList
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndex(final JsonArray arr, final List<Map<String, String>> filterList) {
		return nextEpisodeIndex(arr, 0, filterList);
	}
	/**
	 * return the index of the next json element that matches the criteria defined
	 * in filterList
	 *
	 * @param arr        JsonArray with episodes
	 * @param filterList
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndexReversed(final JsonArray arr, final List<Map<String, String>> filterList) {
		return nextEpisodeIndexReversed(arr, 0, filterList);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param startIndex
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized int nextEpisodeIndex(final JsonArray arr, final int startIndex, final String property,
			final String value) throws Exception {
		if (arr == null)
			return -1;
		if (arr.size() == 0)
			return -1;
		if (arr.size() <= startIndex)
			return -1;
		if (property == null)
			return -1;
		if (value == null)
			return -1;
		final List<Map<String, String>> filterList = new ArrayList<>();
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		filterList.add(filter);
		return nextEpisodeIndex(arr, startIndex, filterList);
	}
	/**
	 *
	 * @param episode
	 * @param filterList
	 * @return
	 */
	private boolean jsonObjectMatchesFilter(final JsonObject episode, final List<Map<String, String>> filterList) {
		if (episode == null)
			return false;
		if (filterList == null)
			return false;
		if (filterList.isEmpty())
			return false;
		boolean listMatch = false;
		for (final Map<String, String> filterEntry : filterList) {
			boolean entryMatch = true;
			for (final Map.Entry<String, String> filterItem : filterEntry.entrySet()) {
				if (!episode.has(filterItem.getKey())) {
					entryMatch = false;
					break;
				}
				if (filterItem.getValue() == null)
					continue;
				final JsonElement propertyElement = episode.get(filterItem.getKey());
				if (!(propertyElement.isJsonPrimitive() || propertyElement.isJsonArray())) {
					entryMatch = false;
					break;
				}
				if (propertyElement.isJsonPrimitive()) {
					if (!propertyElement.getAsString().contentEquals(filterItem.getValue())) {
						entryMatch = false;
						break;
					}
				}
				if (propertyElement.isJsonArray()) {
					if (!propertyElement.getAsJsonArray().contains(new JsonPrimitive(filterItem.getValue()))) {
						entryMatch = false;
						break;
					}
				}
			}
			if (entryMatch)
				listMatch = true;
		}
		return listMatch;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray filterArray(final JsonArray data, final String rangeStart, final String rangeStop,
			final List<Map<String, String>> filter) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return back;
		if (data.size() == 0)
			return back;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (rangeStartDate != null) {
				if (!episode.has("startDate"))
					continue;
				Date episodeStartDate = format.parse(episode.get("startDate").getAsString());
				if (episodeStartDate.before(rangeStartDate))
					continue;
			}
			if (rangeEndDate != null) {
				if (!episode.has("endDate"))
					continue;
				Date episodeEndDate = format.parse(episode.get("endDate").getAsString());
				if (episodeEndDate.after(rangeEndDate))
					continue;
			}
			if (jsonObjectMatchesFilter(episode, filter))
				back.add(episode);
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray filterArray(final JsonArray data, final List<Map<String, String>> filter) {
		final JsonArray back = new JsonArray();
		if (data == null)
			return back;
		if (data.size() == 0)
			return back;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (jsonObjectMatchesFilter(episode, filter))
				back.add(episode);
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray filterArray(final JsonArray data, final String rangeStart, final String rangeStop,
			final String property, final String value) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return null;
		if (data.size() == 0)
			return back;
		if (property == null)
			return back;
		if (value == null)
			return back;
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new LinkedHashMap<>();
		filter.put(property, value);
		filterList.add(filter);
		return filterArray(data, rangeStart, rangeStop, filterList);
	}
	/**
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String rangeStart, final String rangeStop,
			final Map<String, String> filter) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return back;
		if (data.size() == 0)
			return back;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date rangeStartDate = null;
		Date rangeEndDate = null;
		if (rangeStart != null)
			rangeStartDate = format.parse(rangeStart);
		if (rangeStop != null)
			rangeEndDate = format.parse(rangeStop);
		final List<Map<String, String>> filterList = new ArrayList<>();
		filterList.add(filter);
		final Iterator<JsonElement> it = data.iterator();
		while (it.hasNext()) {
			final JsonElement tmp = it.next();
			if (!tmp.isJsonObject())
				continue;
			final JsonObject episode = (JsonObject) tmp;
			if (rangeStartDate != null) {
				if (!episode.has("startDate"))
					continue;
				Date episodeStartDate = format.parse(episode.get("startDate").getAsString());
				if (episodeStartDate.before(rangeStartDate))
					continue;
			}
			if (rangeEndDate != null) {
				if (!episode.has("endDate"))
					continue;
				Date episodeEndDate = format.parse(episode.get("endDate").getAsString());
				if (episodeEndDate.after(rangeEndDate))
					continue;
			}
			boolean match = jsonObjectMatchesFilter(episode, filterList);
			if (match)
				back.add(episode);
		}
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param rangeStart
	 * @param rangeStop
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String rangeStart, final String rangeStop,
			final String property, final String value) throws Exception {
		final JsonArray back = new JsonArray();
		if (data == null)
			return null;
		if (data.size() == 0)
			return back;
		if (property == null)
			return back;
		if (value == null)
			return back;
		final Map<String, String> filter = new LinkedHashMap<String, String>();
		filter.put(property, value);
		return hasEpisodes(data, rangeStart, rangeStop, filter);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param serializedData input string of a json array, e.g.: [{"key1":"val1"},
	 *                       {"key1":"val2"}]
	 * @return a parsed json array
	 */
	public synchronized final JsonArray str2jsonArr(String serializedData) {
		if (serializedData == null)
			return null;
		if (serializedData.contentEquals(""))
			return new JsonArray();
		try {
			String decoded = URLDecoder.decode(serializedData, StandardCharsets.UTF_8.toString());
			decoded = decoded.replaceAll("<prct>", "%");
			decoded = decoded.replaceAll("<pls>", "+");
			JsonArray jsonArray = new JsonParser().parse(decoded).getAsJsonArray();
			return jsonArray;
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}
	private static final Map<String, String> QUOTE_CHARS;
	static {
		QUOTE_CHARS = new HashMap<String, String>();
		QUOTE_CHARS.put("'", "#27");
		QUOTE_CHARS.put("{", "#7B");
		QUOTE_CHARS.put("}", "#7D");
		QUOTE_CHARS.put("[", "#5B");
		QUOTE_CHARS.put("]", "#5D");
		QUOTE_CHARS.put(":", "#3A");
		QUOTE_CHARS.put(",", "#2c");
		QUOTE_CHARS.put("\\", "#5C");
	}
	public String quoteCharJson(final String str) {
		String jsonStr = str;
		for (final Entry<String, String> entry : QUOTE_CHARS.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			jsonStr = jsonStr.replaceAll(Pattern.quote(key), Matcher.quoteReplacement(value));
		}
		return jsonStr;
	}
	public String unQuoteCharJson(final String str) {
		String jsonStr = str;
		for (final Entry<String, String> entry : QUOTE_CHARS.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			jsonStr = jsonStr.replaceAll(Pattern.quote(value), Matcher.quoteReplacement(key));
		}
		return jsonStr;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	public synchronized final JsonArray asJson(final List<AbstractAnswerBean> vars) throws Exception {
		return str2jsonArr(defrac(vars));
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param vars
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public synchronized final JsonElement asJson(final List<AbstractAnswerBean> vars, final int index)
			throws Exception {
		return getJson(asJson(vars), index);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @return
	 */
	public synchronized final String jsonArr2str(final JsonArray data) {
		if (data == null)
			return "";
		String serializedData = data.toString();
		serializedData = serializedData.replaceAll(Pattern.quote("%"), "<prct>");
		serializedData = serializedData.replaceAll(Pattern.quote("+"), "<pls>");
		return serializedData;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized final JsonElement createJson() {
		return new JsonArray();
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @return
	 */
	public synchronized final JsonElement createJsonArray() {
		return new JsonArray();
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @deprecated
	 * @return
	 */
	public synchronized final JsonElement createJsonObject() {
		return new JsonObject();
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param index
	 * @return
	 */
	public synchronized final JsonElement getJson(JsonArray arr, final int index) {
		if (arr == null)
			return JsonNull.INSTANCE;
		if (index < 0)
			return JsonNull.INSTANCE;
		if (index >= arr.size())
			return JsonNull.INSTANCE;
		return arr.get(index);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param index
	 * @return
	 */
	public synchronized final JsonElement getOrCreateJson(JsonArray arr, final int index) {
		if (arr == null)
			return JsonNull.INSTANCE;
		if (index < 0)
			return JsonNull.INSTANCE;
		if (index >= arr.size())
			return new JsonObject();
		return arr.get(index);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param data
	 * @param index
	 * @return
	 */
	public synchronized final JsonArray insertJson(JsonArray arr, final JsonElement data, final Integer index) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		if (index < 0)
			arr.add(data);
		else if (arr.size() <= index)
			arr.add(data);
		else {
			JsonArray newArr = new JsonArray();
			final int size = arr.size();
			for (Integer a = 0; a < size; a++) {
				final JsonElement tmp = arr.get(a);
				if (a == index)
					newArr.add(data);
				newArr.add(tmp);
			}
			return newArr;
		}
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param data
	 * @return
	 */
	public synchronized final JsonArray addJson(JsonArray arr, final JsonElement data) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		arr.add(data);
		return arr;
	}
	/**
	 * [REVIEW][TODO] PERFORMACE TEST
	 *
	 * @param arr
	 * @param data
	 * @return
	 */
	public synchronized final JsonArray addJsonDeepCopy(JsonArray arr, final JsonElement data) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		final JsonArray back = arr.deepCopy();
		back.add(data);
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param data
	 * @param index
	 * @return
	 */
	public synchronized final JsonArray addOrReplaceJson(JsonArray arr, final JsonElement data, final int index) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		if (index < 0)
			arr.add(data);
		else if (arr.size() <= index)
			arr.add(data);
		else
			arr.set(index, data);
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param index
	 * @return
	 */
	public synchronized final JsonArray delJson(final JsonArray arr, final int index) {
		if (arr == null)
			return new JsonArray();
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		arr.remove(index);
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param data
	 * @return
	 */
	public synchronized final JsonArray delJson(JsonArray arr, final JsonElement data) {
		if (arr == null)
			arr = new JsonArray();
		if (data == null)
			return arr;
		if (arr.contains(data))
			arr.remove(data);
		return arr;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param element
	 * @return
	 */
	public synchronized final int indexOfJson(final JsonArray arr, final JsonElement element) {
		if (arr == null)
			return -1;
		if (element == null)
			return -1;
		JsonObject elementObj = null;
		if (element.isJsonObject()) {
			elementObj = (JsonObject) element;
		}
		final int size = arr.size();
		for (int a = 0; a < size; a++) {
			final JsonElement tmp = arr.get(a);
			boolean match = false;
			if ((elementObj != null) && (elementObj.has("id"))) {
				if (tmp.isJsonObject()) {
					final JsonObject tmpObj = (JsonObject) tmp;
					if (tmpObj.has("id")) {
						match = (tmpObj.get("id").getAsString().contentEquals((elementObj.get("id").getAsString())));
					}
				}
			}
			if (match) {
				return a;
			}
		}
		return -1;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param arr
	 * @param oldData
	 * @param newData
	 * @return
	 */
	public synchronized final JsonArray replaceJson(JsonArray arr, final JsonElement oldData,
			final JsonElement newData) {
		if (arr == null)
			arr = new JsonArray();
		if (oldData == null)
			return arr;
		int index = -1;
		final int size = arr.size();
		for (int a = 0; a < size; a++) {
			final JsonElement tmp = arr.get(a);
			if (tmp.equals(oldData)) {
				index = a;
				break;
			}
		}
		if (index >= 0)
			arr.set(index, newData);
		return arr;
	}
	public synchronized final ArrayList<String> sortSplitStackIndex(final JsonObject unsortedSplitStack,
			final SimpleDateFormat format) {
		if (unsortedSplitStack == null)
			return null;
		final ArrayList<Date> sortIndex = new ArrayList<Date>();
		for (final Map.Entry<String, JsonElement> property : unsortedSplitStack.entrySet()) {
			final String dateStr = property.getKey();
			Date date = null;
			try {
				date = format.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (date != null)
				sortIndex.add(date);
		}
		Collections.sort(sortIndex);
		final ArrayList<String> back = new ArrayList<String>();
		for (final Date date : sortIndex) {
			back.add(format.format(date));
		}
		return back;
	}
	public synchronized final JsonArray setCurrentSplitType(JsonArray arr, final int index,
			final String currentSplitType) {
		return setJsonPrimitiveArrayEntry(arr, index, "currentSplit", currentSplitType);
	}
	public synchronized final JsonObject setCurrentSplitType(JsonElement episode, final String currentSplitType) {
		return setJsonPrimitiveArrayEntry(episode, "currentSplit", currentSplitType);
	}
	public synchronized final JsonArray setFlag(JsonArray arr, final int index, final String flag) {
		return setJsonPrimitiveArrayEntry(arr, index, "flags", flag);
	}
	public synchronized final JsonObject setFlag(JsonElement episode, final String flag) {
		return setJsonPrimitiveArrayEntry(episode, "flags", flag);
	}
	private synchronized final JsonArray setJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		if (arr == null)
			return new JsonArray();
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		final JsonArray back = arr.deepCopy();
		final JsonObject tmp = setJsonPrimitiveArrayEntry(back.get(index), jsonPropertyArrayName, arrayEntryName);
		if (tmp != null)
			back.set(index, tmp);
		return back;
	}
	private synchronized final JsonObject setJsonPrimitiveArrayEntry(final JsonElement episode,
			String jsonPropertyArrayName, String arrayEntryName) {
		final JsonObject back = ((JsonObject) episode).deepCopy();
		if (episode == null)
			return back;
		if (!episode.isJsonObject())
			return back;
		if (jsonPropertyArrayName == null)
			return back;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return back;
		if (arrayEntryName == null)
			return back;
		arrayEntryName = arrayEntryName.trim();
		if (arrayEntryName.contentEquals(""))
			return back;
		JsonArray new_array = new JsonArray();
		new_array.add(arrayEntryName);
		if (back.has(jsonPropertyArrayName)) {
			final JsonElement tmp = back.get(jsonPropertyArrayName);
			if (tmp == null) {
				back.add("flags", new_array);
				return back;
			}
			if (!tmp.isJsonArray()) {
				back.add("flags", new_array);
				return back;
			}
			final JsonArray jsonPropertyArray = tmp.getAsJsonArray();
			if (jsonPropertyArray.size() == 0) {
				back.add("flags", new_array);
				return back;
			}
			final JsonPrimitive arrayEntryNameObj = new JsonPrimitive(arrayEntryName);
			if (!jsonPropertyArray.contains(arrayEntryNameObj))
				jsonPropertyArray.add(arrayEntryNameObj);
			back.add(jsonPropertyArrayName, jsonPropertyArray);
		} else {
			back.add("flags", new_array);
		}
		return back;
	}
	public synchronized final JsonArray deleteCurrentSplitType(JsonArray arr, final int index,
			final String currentSplitType) {
		return deleteJsonPrimitiveArrayEntry(arr, index, "currentSplit", currentSplitType);
	}
	public synchronized final JsonArray deleteCurrentSplitType(JsonArray arr, final int index,
			final List<Object> currentSplitTypeList) {
		return deleteJsonPrimitiveArrayEntry(arr, index, "currentSplit", currentSplitTypeList);
	}
	public synchronized final JsonObject deleteCurrentSplitType(JsonElement episode, final String currentSplitType) {
		return deleteJsonPrimitiveArrayEntry(episode, "currentSplit", currentSplitType);
	}
	public synchronized final JsonObject deleteCurrentSplitType(JsonElement episode,
			final List<Object> currentSplitTypeList) {
		return deleteJsonPrimitiveArrayEntry(episode, "currentSplit", currentSplitTypeList);
	}
	public synchronized final JsonArray deleteFlag(JsonArray arr, final int index, final String flag) {
		return deleteJsonPrimitiveArrayEntry(arr, index, "flags", flag);
	}
	public synchronized final JsonObject deleteFlag(JsonElement episode, final String flag) {
		return deleteJsonPrimitiveArrayEntry(episode, "flags", flag);
	}
	private synchronized final JsonArray deleteJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		if (arr == null)
			return new JsonArray();
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		final JsonArray back = arr.deepCopy();
		final JsonObject tmp = deleteJsonPrimitiveArrayEntry(back.get(index), jsonPropertyArrayName, arrayEntryName);
		if (tmp != null)
			back.set(index, tmp);
		return back;
	}
	private synchronized final JsonArray deleteJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			String jsonPropertyArrayName, List<Object> arrayEntryNamesList) {
		if (arr == null)
			return null;
		if (index < 0)
			return arr;
		if (index >= arr.size())
			return arr;
		final JsonArray back = arr.deepCopy();
		final JsonObject tmp = deleteJsonPrimitiveArrayEntry(back.get(index), jsonPropertyArrayName,
				arrayEntryNamesList);
		if (tmp != null)
			back.set(index, tmp);
		return back;
	}
	private synchronized final JsonObject deleteJsonPrimitiveArrayEntry(final JsonElement episode,
			String jsonPropertyArrayName, List<Object> listOfArrayEntryNames) {
		if (episode == null)
			return null;
		if (!episode.isJsonObject())
			return null;
		if (jsonPropertyArrayName == null)
			return null;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return null;
		boolean listContainsAtLeastOneName = false;
		for (Object arrayEntryNameObj : listOfArrayEntryNames) {
			String arrayEntryName = arrayEntryNameObj.toString();
			if (arrayEntryName != null) {
				arrayEntryName = arrayEntryName.trim();
				if (!arrayEntryName.contentEquals(""))
					listContainsAtLeastOneName = true;
			}
		}
		if (listOfArrayEntryNames.isEmpty())
			return (JsonObject) episode;
		if (!listContainsAtLeastOneName)
			return (JsonObject) episode;
		final JsonObject back = ((JsonObject) episode).deepCopy();
		if (back.has(jsonPropertyArrayName)) {
			final JsonElement tmp = back.get(jsonPropertyArrayName);
			if (tmp == null)
				return null;
			if (!tmp.isJsonArray())
				return null;
			final JsonArray jsonPropertyArray = tmp.getAsJsonArray();
			if (jsonPropertyArray.size() == 0)
				return null;
			for (Object arrayEntryNameStrObj : listOfArrayEntryNames) {
				String arrayEntryName = (String) arrayEntryNameStrObj;
				arrayEntryName = arrayEntryName.trim();
				final JsonPrimitive arrayEntryNameObj = new JsonPrimitive(arrayEntryName);
				if (jsonPropertyArray.contains(arrayEntryNameObj))
					jsonPropertyArray.remove(arrayEntryNameObj);
				back.add(jsonPropertyArrayName, jsonPropertyArray);
			}
		}
		return back;
	}
	private synchronized final JsonObject deleteJsonPrimitiveArrayEntry(final JsonElement episode,
			String jsonPropertyArrayName, String arrayEntryName) {
		if (episode == null)
			return null;
		if (!episode.isJsonObject())
			return null;
		if (jsonPropertyArrayName == null)
			return null;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return null;
		if (arrayEntryName == null)
			return null;
		arrayEntryName = arrayEntryName.trim();
		if (arrayEntryName.contentEquals(""))
			return null;
		final JsonObject back = ((JsonObject) episode).deepCopy();
		if (back.has(jsonPropertyArrayName)) {
			final JsonElement tmp = back.get(jsonPropertyArrayName);
			if (tmp == null)
				return null;
			if (!tmp.isJsonArray())
				return null;
			final JsonArray jsonPropertyArray = tmp.getAsJsonArray();
			if (jsonPropertyArray.size() == 0)
				return null;
			final JsonPrimitive arrayEntryNameObj = new JsonPrimitive(arrayEntryName);
			if (jsonPropertyArray.contains(arrayEntryNameObj))
				jsonPropertyArray.remove(arrayEntryNameObj);
			back.add(jsonPropertyArrayName, jsonPropertyArray);
		}
		return back;
	}
	public synchronized final boolean hasFlag(JsonArray arr, final int index, final String flag) {
		return hasJsonPrimitiveArrayEntry(arr, index, "flags", flag);
	}
	public synchronized final boolean hasFlag(JsonElement episode, final String flagName) {
		return hasJsonPrimitiveArrayEntry(episode, "flags", flagName);
	}
	public synchronized final boolean hasCurrentSplitType(JsonArray arr, final int index, final String splitType) {
		return hasJsonPrimitiveArrayEntry(arr, index, "currentSplit", splitType);
	}
	/**
	 * returns "true" if ANY of the given strings in the list can be found in the
	 * "currentSplit"-array of the episode at the given json array index. returns
	 * "false" if NONE of the given strings in the list can be found.
	 *
	 * @param arr           a json array with episode json objects
	 * @param index         the index of the episode in question
	 * @param splitTypeList a list of split type name strings
	 * @return true if ANY of the given strings in the list can be found in
	 *         "currentSplit"; false if NONE can be found.
	 */
	public synchronized final boolean hasCurrentSplitType(JsonArray arr, final int index,
			final List<Object> splitTypeList) {
		return hasJsonPrimitiveArrayEntry(arr, index, "currentSplit", splitTypeList);
	}
	/**
	 * checks whether a split should occur, given the whole json array, an
	 * episode_index and a list of split types. returns "false" if the current
	 * episode has no property "currentSplit", if the list of split types is empty
	 * or if at least one of the given split types is still present within the
	 * "currentSplit" array (is used to check whether a split type later in the
	 * order of split types is still present in "currentSplit" and yet to be
	 * processed)
	 *
	 * @param arr           a json array
	 * @param index         the current episode_index
	 * @param splitTypeList a list of split type strings
	 * @return "false" if the current episode has no property "currentSplit", if the
	 *         list of split types is empty or if at least one of the given split
	 *         types is still present within the "currentSplit" array; "true" iff
	 *         the current episode has "currentSplit" and none of the split types
	 *         given in splitTypeList can be found within the "currentSplit" array.
	 */
	public synchronized final boolean doSplitOnEndPageCandidate(JsonArray arr, final int index,
			final List<Object> splitTypeList) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		JsonObject episode = (JsonObject) arr.get(index);
		if (!episode.has("currentSplit"))
			return false;
		if (splitTypeList.size() == 0)
			return true;
		return !hasCurrentSplitType(arr, index, splitTypeList);
	}
	public synchronized final boolean hasCurrentSplitType(JsonElement episode, final String splitType) {
		return hasJsonPrimitiveArrayEntry(episode, "currentSplit", splitType);
	}
	/**
	 * returns "true" if ANY of the given strings in the list can be found in the
	 * "currentSplit"-array of the episode. returns "false" if NONE of the given
	 * strings in the list can be found.
	 *
	 * @param episode       a json object (an episode)
	 * @param splitTypeList a list of split type name strings
	 * @return true if ANY of the given strings in the list can be found in
	 *         "currentSplit"; false if NONE can be found.
	 */
	public synchronized final boolean hasCurrentSplitType(JsonElement episode, final List<Object> splitTypeList) {
		return hasJsonPrimitiveArrayEntry(episode, "currentSplit", splitTypeList);
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonElement episode, String jsonPropertyArrayName,
			String arrayEntryName) {
		if (episode == null)
			return false;
		if (!episode.isJsonObject())
			return false;
		if (jsonPropertyArrayName == null)
			return false;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return false;
		if (arrayEntryName == null)
			return false;
		arrayEntryName = arrayEntryName.trim();
		if (arrayEntryName.contentEquals(""))
			return false;
		final JsonObject episodeObject = (JsonObject) episode;
		if (episodeObject.has(jsonPropertyArrayName)) {
			final JsonElement property = episodeObject.get(jsonPropertyArrayName);
			if (property.isJsonArray()) {
				final JsonArray tmp = property.getAsJsonArray();
				final JsonPrimitive splitTypeObj = new JsonPrimitive(arrayEntryName);
				return (tmp.contains(splitTypeObj));
			}
		}
		return false;
	}
	/**
	 * returns "true" if ANY of the given strings in the list can be found in the of
	 * the episode with the key=jsonPropertyArrayName. returns "false" if NONE of
	 * the given strings in the list can be found.
	 *
	 * @param episode               a json object (an episode)
	 * @param listOfArrayEntryNames the key/name of the json property where to look
	 *                              in
	 * @return true if ANY of the given strings in the list can be found in the
	 *         given property; false if NONE can be found.
	 */
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonElement episode, String jsonPropertyArrayName,
			List<Object> listOfArrayEntryNames) {
		if (episode == null)
			return false;
		if (!episode.isJsonObject())
			return false;
		if (jsonPropertyArrayName == null)
			return false;
		jsonPropertyArrayName = jsonPropertyArrayName.trim();
		if (jsonPropertyArrayName.contentEquals(""))
			return false;
		boolean listContainsAtLeastOneName = false;
		for (Object arrayEntryNameObj : listOfArrayEntryNames) {
			String arrayEntryName = arrayEntryNameObj.toString();
			if (arrayEntryName != null) {
				arrayEntryName = arrayEntryName.trim();
				if (!arrayEntryName.contentEquals(""))
					listContainsAtLeastOneName = true;
				break;
			}
		}
		if (listContainsAtLeastOneName) {
			for (Object arrayEntryNameObj : listOfArrayEntryNames) {
				String arrayEntryName = arrayEntryNameObj.toString();
				if (arrayEntryName == null)
					continue;
				arrayEntryName = arrayEntryName.trim();
				if (arrayEntryName.contentEquals(""))
					continue;
				final JsonObject episodeObject = (JsonObject) episode;
				if (episodeObject.has(jsonPropertyArrayName)) {
					final JsonElement property = episodeObject.get(jsonPropertyArrayName);
					if (property.isJsonArray()) {
						final JsonArray tmp = property.getAsJsonArray();
						final JsonPrimitive splitTypeObj = new JsonPrimitive(arrayEntryName);
						if (tmp.contains(splitTypeObj))
							return true;
					}
				}
			}
		}
		return false;
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		return hasJsonPrimitiveArrayEntry(arr.get(index), jsonPropertyArrayName, arrayEntryName);
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final List<Object> arrayEntryNamesList) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		return hasJsonPrimitiveArrayEntry(arr.get(index), jsonPropertyArrayName, arrayEntryNamesList);
	}
	private synchronized JsonArray removePropertyFromAllEpisodes(JsonArray arr, final String property) {
		return removePropertyFromOtherEpisodes(arr, -1, property);
	}
	private synchronized JsonArray removePropertyFromOtherEpisodes(final JsonArray arr, final int currentIndex,
			final String property) {
		if (arr == null) return null;
		if (property == null) return arr;
		if (property.contentEquals("")) return arr;
		if (currentIndex >= arr.size())
			return arr;
		final JsonArray copy_arr = arr.deepCopy();
		final int startIndex = 0;
		final int size = copy_arr.size();
		for (int index = startIndex; index < size; index++) {
			if (index == currentIndex)
				continue;
			final JsonElement episode = copy_arr.get(index);
			if (!episode.isJsonObject())
				continue;
			final JsonObject episodeObj = episode.getAsJsonObject();
			if (!episodeObj.has(property))
				continue;
			final String newPropertyName = property + "_old";
			if ((JsonObject.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.add(newPropertyName, episodeObj.get(property).getAsJsonObject());
			}
			else if ((JsonArray.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.add(newPropertyName, episodeObj.get(property).getAsJsonArray());
			}
			else if ((JsonPrimitive.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.add(newPropertyName, episodeObj.get(property).getAsJsonPrimitive());
			}
			else if ((String.class).isAssignableFrom(episodeObj.get(property).getClass())) {
				episodeObj.addProperty(newPropertyName, episodeObj.get(property).getAsString());
			}
			episodeObj.remove(property);
		}
		return copy_arr;
	}
	public synchronized final boolean hasJsonProperty(final JsonElement data, final String property) {
		if (data == null)
			return false;
		if (!data.isJsonObject())
			return false;
		final JsonObject jsonObj = data.getAsJsonObject();
		if (!jsonObj.has(property))
			return false;
		final JsonElement back = jsonObj.get(property);
		if (back.isJsonNull())
			return false;
		if ((JsonPrimitive.class).isAssignableFrom(back.getClass())) {
			if (back.getAsString().contentEquals("null"))
				return false;
		}
		if ((JsonElement.class).isAssignableFrom(back.getClass())) {
			if (back.isJsonNull())
				return false;
		}
		return true;
	}
	public synchronized final boolean hasEpisodeDetail(final JsonElement typeDetails, final String property) {
		if (typeDetails == null)
			return false;
		if (!typeDetails.isJsonArray())
			return false;
		final JsonArray details = typeDetails.getAsJsonArray();
		Iterator<JsonElement> it = details.iterator();
		while (it.hasNext()) {
			final JsonElement detail = it.next();
			if (!detail.isJsonObject())
				continue;
			final JsonObject jsonDetail = detail.getAsJsonObject();
			if (jsonDetail.has("property") && (jsonDetail.get("property").getAsString().contentEquals(property)))
				return true;
		}
		return false;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param properties
	 */
	public synchronized final void getJsonProperties(final JsonElement data, final List<String> properties) {
		for (final String property : properties) {
			AbstractAnswerBean variable = JsfUtility.getInstance().evaluateValueExpression(
					FacesContext.getCurrentInstance(), "#{" + property + "}", AbstractAnswerBean.class);
			String valueStr = getJsonProperty(data, property).toString();
			valueStr = unQuoteCharJson(valueStr);
			valueStr = valueStr.replaceAll(Pattern.quote("\\") + "{2,}", Matcher.quoteReplacement("\\"));
			valueStr = valueStr.replaceAll(Pattern.quote("\\"), Matcher.quoteReplacement("\\\\"));
			setVariableValue(variable, valueStr);
		}
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param property
	 * @return
	 */
	public synchronized final Object getJsonProperty(final JsonElement data, final String property) {
		if (data == null)
			return JsonNull.INSTANCE;
		if (!data.isJsonObject())
			return JsonNull.INSTANCE;
		final JsonObject jsonObj = data.getAsJsonObject();
		if (!jsonObj.has(property))
			return "";
		final JsonElement back = jsonObj.get(property);
		if (back.isJsonPrimitive())
			return back.getAsString();
		return back;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param objectName
	 * @param obj
	 * @param properties
	 */
	public synchronized final void setJsonProperties(final String objectName, final JsonElement obj,
			final Map<String, Object> properties) {
		for (final Map.Entry<String, Object> property : properties.entrySet()) {
			assign(objectName, setJsonProperty(obj, property.getKey(), property.getValue()));
		}
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param objectName
	 * @param obj
	 * @param properties
	 * @param notQuoted
	 */
	public synchronized final void setJsonProperties(final String objectName, final JsonElement obj,
			final Map<String, Object> properties, final Set<String> notQuoted) {
		for (final Map.Entry<String, Object> property : properties.entrySet()) {
			assign(objectName, setJsonProperty(obj, property.getKey(), property.getValue(), notQuoted));
		}
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param properties
	 */
	public synchronized final void resetVars(final List<String> properties) {
		for (final String property : properties) {
			resetVar(property);
		}
	}
	/**
	 * [REVIEW]
	 *
	 * @param varName
	 */
	private void resetVar(final String varName) {
		if (varName == null)
			return;
		final String cleaned = varName.replaceAll("^[ \t]+|[ \t]+$", "");
		if (cleaned.contentEquals(""))
			return;
		AbstractAnswerBean var = JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(),
				"#{" + cleaned + "}", AbstractAnswerBean.class);
		if (var == null)
			return;
		if ((BooleanValueTypeBean.class).isAssignableFrom(var.getClass())) {
			setVariableValue(var, "false");
		}
		if ((NumberValueTypeBean.class).isAssignableFrom(var.getClass())) {
			setVariableValue(var, "");
		}
		if ((StringValueTypeBean.class).isAssignableFrom(var.getClass())) {
			setVariableValue(var, "");
		}
		if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(var.getClass())) {
			setVariableValue(var, "");
		}
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param property
	 * @param value
	 * @return
	 */
	public synchronized final JsonElement setJsonProperty(final JsonElement data, final String property,
			final Object value) {
		final Set<String> notQuoted = new HashSet<String>();
		notQuoted.add("startDate");
		notQuoted.add("endDate");
		return this.setJsonProperty(data, property, value, notQuoted);
	}
	public synchronized final JsonElement setJsonProperty(final JsonElement data, final String property,
			final Object value, final Set<String> notQuoted) {
		JsonElement back = JsonNull.INSTANCE;
		if (data == null)
			back = new JsonObject();
		else if (data.isJsonNull())
			back = new JsonObject();
		else
			back = data;
		if (back.isJsonObject()) {
			if ((data != null) && (data.isJsonObject()) && (data.getAsJsonObject().has(property)))
				((JsonObject) back).remove(property);
			if (value == null) {
				((JsonObject) back).remove(property);
			} else if ((JsonElement.class).isAssignableFrom(value.getClass()))
				((JsonObject) back).add(property, (JsonElement) value);
			else if ((String.class).isAssignableFrom(value.getClass())) {
				if (!((String) value).contentEquals("")) {
					if (!notQuoted.contains(property)) {
						if (this.isWellFormedTimestamp((String) value)) {
							((JsonObject) back).add(property, new JsonPrimitive((String) value));
						} else {
							((JsonObject) back).add(property, new JsonPrimitive(quoteCharJson((String) value)));
						}
					} else
						((JsonObject) back).add(property, new JsonPrimitive((String) value));
				}
			} else if ((Boolean.class).isAssignableFrom(value.getClass()))
				((JsonObject) back).add(property, new JsonPrimitive((Boolean) value));
			else if ((Number.class).isAssignableFrom(value.getClass()))
				((JsonObject) back).add(property, new JsonPrimitive((Number) value));
			else {
				LOGGER.warn("unhandled json property type found : {} !! converted to String", value.getClass());
				((JsonObject) back).add(property, new JsonPrimitive(value + ""));
			}
		}
		return back;
	}
	/**
	 * Convenient method to output a JsonElement as formatted String
	 *
	 * @param data    JsonElement to be stringified (example
	 *                {'property1':'bla','complexProperty2':{'property1':'blub','property2':'bla'}})
	 * @param pattern String with containt placeholders for Json-Properties ("This
	 *                is Property1: [property1] or this :
	 *                [complexProperty2*property2]")
	 * @return formatted stringified JsonElement
	 */
	public synchronized final String printJson(final JsonElement data, final String pattern) {
		if (data == null)
			return "";
		if (!(JsonObject.class).isAssignableFrom(data.getClass()))
			return "";
		final Pattern placeholderPattern = Pattern.compile(
				"(" + Pattern.quote("[") + ")(((?!" + Pattern.quote("]") + ").)*)(" + Pattern.quote("]") + ")");
		final Matcher matcher = placeholderPattern.matcher(pattern);
		String back = pattern;
		final FacesContext fc = FacesContext.getCurrentInstance();
		while (matcher.find()) {
			final String found = matcher.group();
			String property = matcher.group(2);
			String subProperty = null;
			final int subIndex = property.indexOf('*');
			if (subIndex >= 0) {
				subProperty = property.substring(subIndex + 1);
				property = property.substring(0, subIndex);
			}
			String value = null;
			JsonElement valueObj = null;
			final Object tmp = getJsonProperty((JsonObject) data, property);
			if ((tmp != null) && ((JsonElement.class).isAssignableFrom(tmp.getClass()))) {
				valueObj = (JsonElement) tmp;
			}
			if (valueObj != null) {
				if (valueObj.isJsonPrimitive())
					value = ((JsonPrimitive) valueObj).getAsString();
				else if (valueObj.isJsonObject()) {
					if (subProperty != null) {
						value = printJson((JsonObject) valueObj, "[" + subProperty + "]");
					}
				}
			}
			if ((tmp != null) && (value == null))
				value = tmp + "";
			if (value == null)
				value = "";
			if ((value != null) && (!value.contentEquals(""))) {
				AbstractAnswerBean variable = JsfUtility.getInstance().evaluateValueExpression(fc,
						"#{" + property + "}", AbstractAnswerBean.class);
				if (variable != null) {
					if ((SingleChoiceAnswerOptionTypeBean.class).isAssignableFrom(variable.getClass())) {
						final SingleChoiceAnswerOptionTypeBean scVar = (SingleChoiceAnswerOptionTypeBean) variable;
					}
				}
			}
			back = back.replace(found, value);
		}
		final String evaluatedBack = JsfUtility.getInstance().evaluateValueExpression(fc, "#{" + back + "}",
				String.class);
		return evaluatedBack;
	}
	public synchronized String prettyPrintJson(final JsonElement json) {
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final String back = gson.toJson(json);
		return back;
	}
	public synchronized String prettyPrintJsonHtml(final JsonElement json) {
		final String prettyString = prettyPrintJson(json);
		return prettyString.replaceAll("\\n", "<br>").replaceAll("\\s", "&nbsp;");
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param date
	 * @return
	 */
	public synchronized final String formatDate(final String date) {
		final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
		try {
			return outputFormat.format(inputFormat.parse(date));
		} catch (ParseException e) {
			return "";
		}
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	public synchronized final String formatDate(final int day, final int month, final int year) {
		final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return outputFormat.format(cal.getTime());
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param month
	 * @param year
	 * @return
	 */
	public synchronized final int lastDayOfMonth(final int month, final int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	/**
	 * Convenient method to set variable's value
	 *
	 * @param variable Variable Bean
	 * @param value    Value to be set
	 */
	public synchronized final void setVariableValue(final IAnswerBean variable, final String value) {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final String evaluatedValue = JsfUtility.getInstance().evaluateValueExpression(fc, "#{\"" + value + "\"}",
				String.class);
		try {
			variable.setStringValue(evaluatedValue);
		} catch (Exception e) {
			LOGGER.info("Fehler : {} {}", variable, value);
			e.printStackTrace();
		}
	}
	/**
	 *
	 * Redirector for JS-free operations like edit and delete on Episodes Before
	 * reaching target Page the episode_index will be set to the selected episode
	 *
	 *
	 * @param variable   Containing Episode Array
	 * @param episodeId  ID of the selected episode, that shall be edited or deleted
	 * @param targetExpr Jump Target
	 * @param action     Action message used for triggering processAction in
	 *                   SessionController
	 * @return target page name (without leading / or suffixes like .html or .xhtml)
	 */
	public synchronized final String actionEpisode(IAnswerBean variable, String episodeId, final String targetExpr,
			final String action) {
		final FacesContext context = FacesContext.getCurrentInstance();
		final SessionController sessionController = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{sessionController}", SessionController.class);
		final NavigatorBean navigatorBean = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{navigatorBean}", NavigatorBean.class);
		persistandreload(sessionController, navigatorBean);
		final String target = JsfUtility.getInstance().evaluateValueExpression(context, "#{" + targetExpr + "}",
				String.class);
		final JsonArray episodes = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{zofar.str2jsonArr('" + variable.getStringValue() + "')}", JsonArray.class);
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<String, String>();
		filter.put("id", episodeId);
		filterList.add(filter);
		int episodeIndex = -1;
		try {
			episodeIndex = nextEpisodeIndex(episodes, -1, filterList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (episodeIndex > -1) {
			final IAnswerBean episodeIndexVar = JsfUtility.getInstance().evaluateValueExpression(context,
					"#{episode_index}", IAnswerBean.class);
			this.setVariableValue(episodeIndexVar, episodeIndex + "");
		}
		final HtmlCommandButton sourceBt = new HtmlCommandButton();
		sourceBt.setId("sendBt");
		ExpressionFactory factory = context.getApplication().getExpressionFactory();
		Class[] classList = new Class[1];
		classList[0] = ActionEvent.class;
		MethodExpression actionCall = factory.createMethodExpression(context.getELContext(), action, null, classList);
		sourceBt.setActionExpression(actionCall);
		ActionEvent actionEvent = new ActionEvent(sourceBt);
		sessionController.processAction(actionEvent);
		return "/" + target + ".xhtml";
	}
	public synchronized final String actionEpisode(final String variableValue, String episodeId, final String targetExpr,
			final String action) {
		final FacesContext context = FacesContext.getCurrentInstance();
		final SessionController sessionController = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{sessionController}", SessionController.class);
		final NavigatorBean navigatorBean = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{navigatorBean}", NavigatorBean.class);
		persistandreload(sessionController, navigatorBean);
		final String target = JsfUtility.getInstance().evaluateValueExpression(context, "#{" + targetExpr + "}",
				String.class);
		final JsonArray episodes = JsfUtility.getInstance().evaluateValueExpression(context,
				"#{zofar.str2jsonArr('" + variableValue + "')}", JsonArray.class);
		final List<Map<String, String>> filterList = new LinkedList<>();
		final Map<String, String> filter = new HashMap<String, String>();
		filter.put("id", episodeId);
		filterList.add(filter);
		int episodeIndex = -1;
		try {
			episodeIndex = nextEpisodeIndex(episodes, -1, filterList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (episodeIndex > -1) {
			final IAnswerBean episodeIndexVar = JsfUtility.getInstance().evaluateValueExpression(context,
					"#{episode_index}", IAnswerBean.class);
			this.setVariableValue(episodeIndexVar, episodeIndex + "");
		}
		final HtmlCommandButton sourceBt = new HtmlCommandButton();
		sourceBt.setId("sendBt");
		ExpressionFactory factory = context.getApplication().getExpressionFactory();
		Class[] classList = new Class[1];
		classList[0] = ActionEvent.class;
		MethodExpression actionCall = factory.createMethodExpression(context.getELContext(), action, null, classList);
		sourceBt.setActionExpression(actionCall);
		ActionEvent actionEvent = new ActionEvent(sourceBt);
		sessionController.processAction(actionEvent);
		return "/" + target + ".xhtml";
	}
	/**
	 *
	 * Edit Operation for JS-free use of Episode Table
	 *
	 * @param episodes     JsonArray of all episodes
	 * @param episodeIndex index of focused episode
	 * @param mods         properties to patch the existing focused episode
	 * @return the modified JsonArray (in case the focused episode doesn't exist the
	 *         original JsonArray will be returned unmodified)
	 */
	public synchronized final JsonArray editEpisode(final JsonArray episodes, final int episodeIndex,
			final Map<String, Object> mods) {
		JsonElement currentEpisode = null;
		if (episodeIndex > -1) {
			currentEpisode = episodes.get(episodeIndex).getAsJsonObject();
		}
		if (currentEpisode == null)
			return episodes;
		for (final Map.Entry<String, Object> mod : mods.entrySet()) {
			currentEpisode = this.setJsonProperty(currentEpisode, mod.getKey(), mod.getValue());
		}
		return this.addOrReplaceJson(episodes, currentEpisode, episodeIndex);
	}
	public synchronized final List<JsonElement> asEpisodeList(final JsonArray episodes) {
		if (episodes == null)
			return null;
		final List<JsonElement> back = new ArrayList<JsonElement>();
		if (episodes.isJsonNull())
			return back;
		final Iterator<JsonElement> it = episodes.iterator();
		while (it.hasNext())
			back.add(it.next());
		return back;
	}
	/**
	 *
	 * Delete Operation for JS-free use of Episode Table
	 *
	 * @param episodes     JsonArray of all episodes
	 * @param episodeIndex index of focused episode
	 * @return the modified JsonArray (in case the focused episode doesn't exist the
	 *         original JsonArray will be returned unmodified)
	 */
	public synchronized final JsonArray deleteEpisode(final JsonArray episodes, final int episodeIndex) {
		final JsonArray updatedEpisodes = this.delJson(episodes, episodeIndex);
		return updatedEpisodes;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param sessionController
	 * @param navigator
	 */
	public synchronized final void persistandreload(final SessionController sessionController,
			final NavigatorBean navigator) {
		persitandreload(sessionController, navigator);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param sessionController
	 * @param navigator
	 */
	public synchronized final void persitandreload(final SessionController sessionController,
			final NavigatorBean navigator) {
		final FacesContext context = FacesContext.getCurrentInstance();
		final String currentViewId = context.getViewRoot().getViewId();
		final String cleanedId = currentViewId.substring(1, currentViewId.length() - 6);
		final HtmlCommandButton sourceBt = new HtmlCommandButton();
		sourceBt.setId("reloadBt");
		ExpressionFactory factory = context.getApplication().getExpressionFactory();
		Class[] classList = new Class[1];
		classList[0] = ActionEvent.class;
		MethodExpression action = factory.createMethodExpression(context.getELContext(), "same", null, classList);
		sourceBt.setActionExpression(action);
		ActionEvent actionEvent = new ActionEvent(sourceBt);
		sessionController.processAction(actionEvent);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 */
	public void nothing() {
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param message
	 * @param participant
	 * @return
	 */
	public boolean log(final String message, final Participant participant) {
		LOGGER.info("[MESSAGE] ({}) {}", participant.getToken(), message);
		return true;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param message1
	 * @param message2
	 * @param participant
	 * @return
	 */
	public boolean log(final String message1, final String message2, final Participant participant) {
		LOGGER.info("[MESSAGE] ({}) {}", participant.getToken(), message1 + message2);
		return true;
	}
	public synchronized JsonObject systemState() {
		final MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
		final MemoryUsage heapMemoryUsage = memBean.getHeapMemoryUsage();
		final JsonObject status = new JsonObject();
		final JsonObject mem = new JsonObject();
		mem.add("max", new JsonPrimitive(heapMemoryUsage.getMax()));
		mem.add("committed", new JsonPrimitive(heapMemoryUsage.getCommitted()));
		mem.add("used", new JsonPrimitive(heapMemoryUsage.getUsed()));
		mem.add("init", new JsonPrimitive(heapMemoryUsage.getInit()));
		status.add("heap", mem);
		return status;
	}
	public synchronized JsonObject episodesState(JsonArray episodes) {
		final JsonObject status = new JsonObject();
		if (episodes == null)
			return status;
		if (episodes.isJsonNull())
			return status;
		status.add("count", new JsonPrimitive(episodes.size()));
		return status;
	}
	public synchronized String arrayDateMin(final JsonArray arr, final String propertyName, final String rangeMin,
			final String rangeMax, final List<Map<String, String>> filterMapArray) throws Exception {
		return arrayDateMinMax(arr, "min", propertyName, rangeMin, rangeMax, filterMapArray);
	}
	public synchronized String arrayDateMax(final JsonArray arr, final String propertyName, final String rangeMin,
			final String rangeMax, final List<Map<String, String>> filterMapArray) throws Exception {
		return arrayDateMinMax(arr, "max", propertyName, rangeMin, rangeMax, filterMapArray);
	}
	/**
	 *
	 * [REVIEWED CM]
	 *
	 * Returns the minimum or maximum value within the json array of all property
	 * field with the key of propertyName, over all episodes within the json array.
	 * rangeMin <= result <= rangeMax, if rangeMin and rangeMax are given; rangeMin
	 * <= result, if only rangeMin is given; result <= rangeMax, if only rangeMax is
	 * given;
	 *
	 * @param arr          the json array
	 * @param mode         "min" or "max"
	 * @param propertyName the key name of the property, e.g. "startDate" or
	 *                     "endDate"
	 * @param rangeMin     the minimum value
	 * @param rangeMax
	 * @param filter
	 * @return lexicographically smallest string in the property with the given
	 *         propertyName within the given range
	 */
	private synchronized String arrayDateMinMax(final JsonArray arr, final String mode, final String propertyName,
			final String rangeMin, final String rangeMax, final List<Map<String, String>> filter) throws Exception {
		if (arr == null)
			return null;
		if (mode == null)
			return null;
		if (!Objects.equals(mode, "min") && !Objects.equals(mode, "max"))
			return null;
		if (propertyName == null)
			return null;
		String rangeMinCopy = null;
		String rangeMaxCopy = null;
		if (isWellFormedTimestamp(rangeMin))
			rangeMinCopy = rangeMin;
		if (isWellFormedTimestamp(rangeMax))
			rangeMaxCopy = rangeMax;
		JsonArray copyJsonArray = null;
		if (filter != null) {
			copyJsonArray = this.filterArray(arr, null, null, filter);
		} else {
			copyJsonArray = arr.deepCopy();
		}
		final List<String> resultsList = new ArrayList<>();
		for (final JsonElement currentEpisodeElement : copyJsonArray) {
			final JsonObject currentEpisodeObject = (JsonObject) currentEpisodeElement;
			if (currentEpisodeObject.has(propertyName) && !currentEpisodeObject.get(propertyName).isJsonNull()) {
				final String resultString = currentEpisodeObject.get(propertyName).getAsString();
				boolean matchCandidate = true;
				if (!isWellFormedTimestamp(resultString))
					continue;
				if (rangeMinCopy != null && (resultString.compareTo(rangeMinCopy) < 0))
					matchCandidate = false;
				if (rangeMaxCopy != null && (resultString.compareTo(rangeMaxCopy) > 0))
					matchCandidate = false;
				if (matchCandidate) {
					resultsList.add(resultString);
				}
				if (!isWellFormedTimestamp(resultString))
					continue;
				if (rangeMinCopy != null && (resultString.compareTo(rangeMinCopy) < 0))
					matchCandidate = false;
				if (rangeMaxCopy != null && (resultString.compareTo(rangeMaxCopy) > 0))
					matchCandidate = false;
				if (matchCandidate) {
					resultsList.add(resultString);
				}
			}
		}
		if (resultsList.size() == 1) {
			return resultsList.get(0);
		}
		String resultString = null;
		for (final String timestamp : resultsList) {
			if (resultString == null) {
				resultString = timestamp;
			} else {
				if (((mode.equals("min")) && (resultString.compareTo(timestamp) > 0))
						|| (mode.equals("max")) && (resultString.compareTo(timestamp) < 0)) {
					resultString = timestamp;
				}
			}
		}
		return resultString;
	}
	/**
	 * determine whether a timestamp string is a well formed timestamp string (via
	 * regular expression)
	 *
	 * @param inputTimestamp a string that should contain a timestamp in the form of
	 *                       "1971-01-01T01:00:00.000Z"
	 * @return true if the timestamp is well-formed, false if not.
	 */
	public boolean isWellFormedTimestamp(final String inputTimestamp) {
		if (inputTimestamp == null)
			return false;
		Pattern pattern = Pattern.compile(
				"^(((197|198|199)[0-9])|(20[0-9]{2})|(4000))-((0[0-9])|(1[012]))-(([012][0-9])|(3[01]))T(2[0-3]|[01][0-9]):[012345][0-9]:[012345][0-9].[0-9]{3}Z$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputTimestamp);
		return matcher.find();
	}
	private JsonArray removeEpisodeFromArrayViaId(final JsonArray arr, final JsonElement episode) {
		JsonArray copyJsonArr = arr.deepCopy();
		if (!episode.getAsJsonObject().has("id"))
			return arr;
		String idRefEpisodeStr = episode.getAsJsonObject().get("id").getAsString();
		JsonArray newJsonArray = new JsonArray();
		for (JsonElement episodeElement : copyJsonArr) {
			JsonObject episodeObject = episodeElement.getAsJsonObject();
			if (episodeObject.has("id") && !episodeObject.get("id").isJsonNull()) {
				String idEpisodeStr = episodeObject.get("id").getAsString();
				if (!idRefEpisodeStr.equals(idEpisodeStr)) {
					newJsonArray.add(episodeElement);
				}
			}
		}
		return newJsonArray;
	}
	public boolean isMissingDateValue(final String inputTimestamp) {
		return !isWellFormedTimestamp(inputTimestamp) || inputTimestamp.compareTo("1970-01-01T01:00:00.000Z") <= 0
				|| inputTimestamp.compareTo("4000-01-01T01:00:00.000Z") >= 0;
	}
	/**
	 * helper function for checking if a specified property value within the json
	 * episode is a missing value
	 * 
	 * @param arr      the whole json array
	 * @param index    the index of the episode in question
	 * @param property the key / name string of the property value in question
	 * @return true if the property name is present and a parseable date could be
	 *         found and the date is missing, false if else
	 */
	private boolean nonMissingDateProperty(final JsonArray arr, final int index, final String property) {
		if (arr == null)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		JsonElement episode = arr.get(index);
		return nonMissingDateProperty(episode, property);
	}
	/**
	 * helper function for checking if a specified property value within the json
	 * episode is a missing value
	 * 
	 * @param episode  the index of the episode in question
	 * @param property the key / name string of the property value in question
	 * @return true if the property name is present and a parseable date could be
	 *         found and the date is missing, false if else
	 */
	private boolean nonMissingDateProperty(final JsonElement episode, final String property) {
		JsonObject episodeObj = (JsonObject) episode;
		if (hasJsonProperty(episodeObj, property)) {
			String testDate = (String) this.getJsonProperty(episodeObj, property);
			if (this.isTimeStampParseable(testDate))
				return !this.isMissingDateValueParseable(testDate);
		}
		return false;
	}
	/**
	 * function to determine whether endDate value of an episode within a json array
	 * is non-missing
	 * 
	 * @param arr   the whole json array
	 * @param index the index of the episode in question
	 * @return true if the property "endDate" is present and a parseable date could
	 *         be found and the date is non-missing, false if else
	 */
	public boolean nonMissingEndDate(final JsonArray arr, final int index) {
		return this.nonMissingDateProperty(arr, index, "endDate");
	}
	/**
	 * function to determine whether endDate value of a given episode is non-missing
	 * 
	 * @param episode in question
	 * @return true if the property "endDate" is present and a parseable date could
	 *         be found and the date is non-missing, false if else
	 */
	public boolean nonMissingEndDate(final JsonElement episode) {
		return this.nonMissingDateProperty(episode, "endDate");
	}
	/**
	 * function to determine whether startDate value of a given episode is
	 * non-missing
	 * 
	 * @param episode in question
	 * @return true if the property "startDate" is present and a parseable date
	 *         could be found and the date is non-missing, false if else
	 */
	public boolean nonMissingStartDate(final JsonElement episode) {
		return this.nonMissingDateProperty(episode, "startDate");
	}
	/**
	 * function to determine whether startDate value of an episode within a json
	 * array is non-missing
	 * 
	 * @param arr   the whole json array
	 * @param index the index of the episode in question
	 * @return true if the property "startDate" is present and a parseable date
	 *         could be found and the date is non-missing, false if else
	 */
	public boolean nonMissingStartDate(final JsonArray arr, final int index) {
		return this.nonMissingDateProperty(arr, index, "startDate");
	}
	public boolean timestampWithinTimestampsOld(final String timestamp1, final String timestamp2,
			final String timestampInQuestion) {
		if (isWellFormedTimestamp(timestamp1) && isWellFormedTimestamp(timestamp2)
				&& isWellFormedTimestamp(timestampInQuestion) && !isMissingDateValue(timestamp1)
				&& !isMissingDateValue(timestamp2) && !isMissingDateValue(timestampInQuestion)) {
			if (timestamp1.compareTo(timestamp2) <= 0) {
				return (timestamp1.compareTo(timestampInQuestion) <= 0)
						&& (timestampInQuestion.compareTo(timestamp2) <= 0);
			} else {
				return (timestamp1.compareTo(timestampInQuestion) >= 0)
						&& (timestampInQuestion.compareTo(timestamp2) >= 0);
			}
		}
		return false;
	}
	public boolean timestampWithinTimestamps(final String timestamp1, final String timestamp2,
			final String timestampInQuestion) {
		if (isWellFormedTimestamp(timestamp1) && isWellFormedTimestamp(timestamp2)
				&& isWellFormedTimestamp(timestampInQuestion) && !isMissingDateValue(timestamp1)
				&& !isMissingDateValue(timestamp2) && !isMissingDateValue(timestampInQuestion)) {
			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try {
				final Calendar cal1 = new GregorianCalendar();
				cal1.setTime(df.parse(timestamp1));
				final Calendar cal2 = new GregorianCalendar();
				cal2.setTime(df.parse(timestamp2));
				final Calendar calQuestion = new GregorianCalendar();
				calQuestion.setTime(df.parse(timestampInQuestion));
				if (cal1.before(cal2)) {
					return ((cal1.before(calQuestion) || (cal1.equals(calQuestion)))
							&& (calQuestion.before(cal2) || calQuestion.equals(cal2)));
				} else if (cal2.before(cal1)) {
					return ((cal2.before(calQuestion) || cal2.equals(calQuestion))
							&& (calQuestion.before(cal1) || calQuestion.equals(cal1)));
				} else if (cal2.equals(cal1)) {
					return (cal2.equals(calQuestion) && calQuestion.equals(cal1));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * Check whether two timestamps (order does not matter) are both well-formed and
	 * non-missing timestamp values.
	 *
	 * @param timestamp1 one timestamp string
	 * @param timestamp2 the other timestamp string
	 * @return true if both timestamps are well-formed and non-missing values; false
	 *         if they are not.
	 */
	public boolean comparableTimestamps(final String timestamp1, final String timestamp2) {
		if (isWellFormedTimestamp(timestamp1) && isWellFormedTimestamp(timestamp2) && !isMissingDateValue(timestamp1)
				&& isMissingDateValue(timestamp2)) {
			return true;
		}
		return false;
	}
	/**
	 * [REVIEWED CM] Uses a deep copy of the given json array and also of the given
	 * reference episode. Checks whether a given referenceEpisode overlaps with any
	 * of the (other) episodes from the given json array. If the reference episode
	 * exhibits a missing or non-well-formed startDate or endDate timestamp, the
	 * return value will be "false". If all of the episodes from the json array
	 * exhibit either a missing or non-well-formed startDate or endDate timestamp,
	 * the return value will be "false". The reference epsiode must have an "id"
	 * property. Using this property, any episode within the json array that shows
	 * the same "id" value (String comparison) will be removed from the array prior
	 * to the overlap-checking.
	 *
	 * @param arr              a json array that may or may not contain the
	 *                         referenceEpisode (identified via "id" property)
	 * @param referenceEpisode the reference episode that shall be tested if it
	 *                         overlaps with the json array
	 * @return true if the reference episode overlaps; false if it does not
	 */
	public boolean overlapEpisodeWithArray(final JsonArray arr, final JsonElement referenceEpisode) {
		if (arr == null)
			return false;
		if (referenceEpisode == null)
			return false;
		if (!referenceEpisode.isJsonObject())
			return false;
		final JsonObject episodeObject = referenceEpisode.deepCopy().getAsJsonObject();
		if (!(episodeObject.has("id"))) {
			return false;
		}
		final JsonArray cleanedJsonArray = removeEpisodeFromArrayViaId(arr.deepCopy(), referenceEpisode);
		if (!episodeObject.has("startDate") || !episodeObject.has("endDate")) {
			return false;
		}
		if (episodeObject.get("startDate").isJsonNull() || episodeObject.get("endDate").isJsonNull()) {
			return false;
		}
		final String refEpisodeStartDate = episodeObject.get("startDate").getAsString();
		final String refEpisodeEndDate = episodeObject.get("endDate").getAsString();
		if (!isWellFormedTimestamp(refEpisodeStartDate) || !isWellFormedTimestamp(refEpisodeEndDate)) {
			return false;
		}
		if (isMissingDateValue(refEpisodeStartDate) || isMissingDateValue(refEpisodeEndDate)) {
			return false;
		}
		for (final JsonElement currentEpisodeElement : cleanedJsonArray) {
			final String currentEpisodeStartDate = (String) getJsonProperty(currentEpisodeElement, "startDate");
			final String currentEpisodeEndDate = (String) getJsonProperty(currentEpisodeElement, "endDate");
			if ((isWellFormedTimestamp(currentEpisodeStartDate) || isWellFormedTimestamp(currentEpisodeEndDate)) &&
					(!isMissingDateValue(currentEpisodeStartDate) && !isMissingDateValue(currentEpisodeEndDate)) &&
					(timestampWithinTimestamps(currentEpisodeStartDate, currentEpisodeEndDate, refEpisodeStartDate)
							|| timestampWithinTimestamps(currentEpisodeStartDate, currentEpisodeEndDate,
									refEpisodeEndDate)
							|| timestampWithinTimestamps(refEpisodeStartDate, refEpisodeEndDate,
									currentEpisodeStartDate)
							|| timestampWithinTimestamps(refEpisodeStartDate, refEpisodeEndDate,
									currentEpisodeEndDate))) {
				return true;
			}
		}
		return false;
	}
	/*
	 * [REVIEWED CM]
	 */
	public boolean overlapEpisodeWithArray(final JsonArray filteredArr, final JsonArray inputArr,
			final int inputIndex) {
		if (filteredArr == null)
			return false;
		if (inputArr == null)
			return false;
		if ((filteredArr.isJsonNull()) || (inputArr.isJsonNull())
				|| (!(inputIndex >= 0) && (inputIndex <= inputArr.size() - 1))) {
			return false;
		}
		return overlapEpisodeWithArray(filteredArr, inputArr.get(inputIndex));
	}
	public String timestamp2monthpicker(String input_timestamp) {
		if (!isWellFormedTimestamp(input_timestamp) || isMissingDateValue(input_timestamp))
			return "1970,01,01";
		String yearString = input_timestamp.substring(0, 4);
		String monthString = input_timestamp.substring(5, 7);
		String dayString = input_timestamp.substring(8, 10);
		return yearString + "," + monthString + "," + dayString;
	}
	public String yearStr2monthpickerYearStart(String input_year) {
		return timestamp2monthpicker(input_year + "-01-01T01:00:00.000Z");
	}
	public String monthPickerIncreaseByMonths(String inputMonthpickerTS, int months) {
		if (months == 0)
			return inputMonthpickerTS;
		String timestamp = monthpicker2timestamp(inputMonthpickerTS);
		return timestamp2monthpicker(timestampTimeDelta(timestamp, 0, months, 0, 0, 0, 0, 0));
	}
	public boolean isTimestampValid(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
		final String timeStampStr = this.asTimestamp(year, month, day, hour, minute, second, millisecond);
		try {
			final Date timestamp = stampFormat.parse(timeStampStr);
			final Calendar calendar = new GregorianCalendar();
			calendar.setTime(timestamp);
			if (year != calendar.get(Calendar.YEAR))
				return false;
			if (month != calendar.get(Calendar.MONTH))
				return false;
			if (day != calendar.get(Calendar.DAY_OF_MONTH))
				return false;
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	public String asTimestamp(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		try {
			final Calendar cal = new GregorianCalendar();
			cal.set(year, month, day, hour, minute, second);
			final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
			return stampFormat.format(cal.getTime());
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		return "1970-01-01T01:00:00.000Z";
	}
	public String timestampSetDatetime(String inputTimestamp, int years, int months, int days, int hours, int minutes,
			int seconds, int milliseconds) {
		try {
			int year = Integer.parseInt(inputTimestamp.substring(0, 4));
			int month = Integer.parseInt(inputTimestamp.substring(5, 7)) - 1;
			int day = Integer.parseInt(inputTimestamp.substring(8, 10));
			int hour = Integer.parseInt(inputTimestamp.substring(11, 13));
			int minute = Integer.parseInt(inputTimestamp.substring(14, 16));
			int second = Integer.parseInt(inputTimestamp.substring(17, 19));
			int millisecond = Integer.parseInt(inputTimestamp.substring(20, 23));
			final Calendar cal = new GregorianCalendar();
			cal.set(year, month, day, hour, minute, second);
			final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
			if (milliseconds >= 0)
				cal.set(Calendar.MILLISECOND, milliseconds);
			if (seconds >= 0)
				cal.set(Calendar.SECOND, seconds);
			if (minutes >= 0)
				cal.set(Calendar.MINUTE, minutes);
			if (hours >= 0)
				cal.set(Calendar.HOUR_OF_DAY, hours);
			if (days >= 0)
				cal.set(Calendar.DAY_OF_MONTH, days);
			if (months >= 0)
				cal.set(Calendar.MONTH, months);
			if (years >= 0)
				cal.set(Calendar.YEAR, years);
			return stampFormat.format(cal.getTime());
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		return "1970-01-01T01:00:00.000Z";
	}
	public String timestampTimeDelta(String inputTimestamp, int years, int months, int days, int hours, int minutes,
			int seconds, int milliseconds) {
		try {
			int year = Integer.parseInt(inputTimestamp.substring(0, 4));
			int month = Integer.parseInt(inputTimestamp.substring(5, 7)) - 1;
			int day = Integer.parseInt(inputTimestamp.substring(8, 10));
			final Calendar cal = new GregorianCalendar();
			cal.set(year, month, day, 1, 0, 0);
			final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
			if (milliseconds != 0)
				cal.add(Calendar.MILLISECOND, milliseconds);
			if (seconds != 0)
				cal.add(Calendar.SECOND, seconds);
			if (minutes != 0)
				cal.add(Calendar.MINUTE, minutes);
			if (hours != 0)
				cal.add(Calendar.HOUR, hours);
			if (days != 0)
				cal.add(Calendar.DAY_OF_MONTH, days);
			if (months != 0)
				cal.add(Calendar.MONTH, months);
			if (years != 0)
				cal.add(Calendar.YEAR, years);
			return stampFormat.format(cal.getTime());
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return "1970-01-01T01:00:00.000Z";
		}
	}
	public String yearStr2monthpickerYearEnd(String input_year) {
		return timestamp2monthpicker(input_year + "-12-31T01:00:00.000Z");
	}
	public String monthpicker2timestamp(String input_timestamp) {
		if (input_timestamp == null || input_timestamp.trim() == "")
			return "1970-01-01T01:00:00.000Z";
		String[] timestamp_list = input_timestamp.split(",");
		if (timestamp_list.length != 3)
			return "1970-01-01T01:00:00.000Z";
		String yearString = timestamp_list[0];
		String monthString = timestamp_list[1];
		if (monthString.length() == 1)
			monthString = "0" + monthString;
		String dayString = timestamp_list[2];
		if (dayString.length() == 1)
			dayString = "0" + dayString;
		String returnString = yearString + "-" + monthString + "-" + dayString + "T01:00:00.000Z";
		if (!isWellFormedTimestamp(returnString) || isMissingDateValue(returnString))
			return "1970-01-01T01:00:00.000Z";
		return returnString;
	}
	public synchronized final String decideSplitTarget(final JsonArray arr, final int index,
			final JsonObject split_type_dict, final JsonArray split_type_order, final String fallbackTarget) {
		if (arr == null) {
			return fallbackTarget;
		}
		if (index < 0)
			return fallbackTarget;
		if (index >= arr.size())
			return fallbackTarget;
		final JsonObject episode = arr.get(index).getAsJsonObject();
		return this.decideSplitTarget(episode, split_type_dict, split_type_order, fallbackTarget);
	}
	public synchronized final String decideSplitTarget(final JsonElement episode, final JsonObject split_type_dict,
			final JsonArray split_type_order, final String fallbackTarget) {
		final FunctionProvider zofar = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{zofar}", FunctionProvider.class);
		if (!zofar.hasJsonProperty(episode, "currentSplit"))
			return fallbackTarget;
		final Object currentSplit = zofar.getJsonProperty(episode, "currentSplit");
		if (currentSplit == null)
			return fallbackTarget;
		if ((JsonArray.class).isAssignableFrom(currentSplit.getClass())) {
			final JsonArray currentSplitArray = (JsonArray) currentSplit;
			Iterator<JsonElement> it = split_type_order.iterator();
			while (it.hasNext()) {
				final JsonElement type = it.next();
				if (!currentSplitArray.contains(type))
					continue;
				if (!zofar.hasJsonProperty(split_type_dict, type.getAsString()))
					continue;
				final JsonElement typedSplitInfo = split_type_dict.get(type.getAsString());
				if (typedSplitInfo == null)
					continue;
				if (!typedSplitInfo.isJsonObject())
					continue;
				final JsonObject typedSplitInfoObj = typedSplitInfo.getAsJsonObject();
				if (!zofar.hasJsonProperty(typedSplitInfoObj, "START_PAGE"))
					continue;
				return typedSplitInfo.getAsJsonObject().get("START_PAGE").getAsString();
			}
		}
		return fallbackTarget;
	}
	public boolean isMissingDateValueParseable(final String timestamp) {
		if (timestamp != null && !timestamp.isEmpty() && isTimeStampParseable(timestamp)) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Date valMinMissing = simpleDateFormat.parse("1970-12-31T23:59:59.999Z");
				Date valMaxMissing = simpleDateFormat.parse("4000-01-01T00:00:00.000Z");
				Date val = simpleDateFormat.parse(timestamp);
				if (valMinMissing.compareTo(val) >= 0 || valMaxMissing.compareTo(val) <= 0) {
					return true;
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
	public boolean isTimeStampParseable(final String timestamp) {
		if (timestamp != null && !timestamp.isEmpty()) {
			try {
				final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				simpleDateFormat.parse(timestamp);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}
	/**
	 * compare two timestamps. Prior to that, that, it also checks via
	 * comparableTimestamps whether the two timestmap strings are parseable to
	 * defined Timestamp and have a non-missing value.
	 *
	 * @param timestamp1 first timestamp
	 * @param timestamp2 second timestamp
	 * @return -1 if timestamp1 is smaller than timestamp2; 0 if both timestamp
	 *         strings are identical; +1 if timestamp1 is larger than timestamp2;
	 *         for all other cases -2
	 */
	public int compareJsonTimestamps(final String timestamp1, final String timestamp2) {
		int compareResult = -2;
		if ((isTimeStampParseable(timestamp1) && !isMissingDateValueParseable(timestamp1))
				&& (isTimeStampParseable(timestamp2) && !isMissingDateValueParseable(timestamp1))) {
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			try {
				final Date d1 = simpleDateFormat.parse(timestamp1);
				final Date d2 = simpleDateFormat.parse(timestamp2);
				if (d1.before(d2)) {
					compareResult = -1;
				} else if (d1.after(d2)) {
					compareResult = 1;
				} else if (d1.compareTo(d2) == 0) {
					compareResult = 0;
				}
			} catch (Exception e) {
			}
		}
		return compareResult;
	}
	private JsonArray removeBrokenJsonElements(final JsonArray jsonArray) throws Exception {
		List<Map<String, String>> filterList = new ArrayList<>();
		Map<String, String> filter = new HashMap<>();
		filter.put("state", null);
		filter.put("startDate", null);
		filter.put("endDate", null);
		filter.put("id", null);
		filter.put("type", null);
		filterList.add(filter);
		JsonArray jsonArrayCleaned = this.filterArray(jsonArray, filterList);
		JsonArray jsonArrayCleanedSound = new JsonArray();
		if (jsonArrayCleaned.size() == 0)
			return jsonArrayCleaned;
		for (JsonElement episodeElement : jsonArrayCleaned) {
			JsonObject episodeObj = (JsonObject) episodeElement;
			if (episodeObj.has("startDate")) {
				String startDate = episodeObj.get("startDate").getAsString();
				if (!this.isTimeStampParseable(startDate))
					continue;
			}
			if (episodeObj.has("endDate")) {
				String endDate = episodeObj.get("endDate").getAsString();
				if (!this.isTimeStampParseable(endDate))
					continue;
			}
			if (episodeObj.has("state")) {
				String state = episodeObj.get("state").getAsString();
				if (!state.equals("new") && !state.equals("done"))
					continue;
			}
			jsonArrayCleanedSound.add(episodeObj);
		}
		return jsonArrayCleanedSound;
	}
	public String stringNull() {
		return null;
	}
	/**
	 * Helper function to ensure that the returned json array is free from empty
	 * json objects
	 *
	 * @param serializedData input string data
	 * @return a "cleaned" json array without empty json objects as elements
	 */
	public synchronized final JsonArray str2jsonArrNoEmpty(final String serializedData) throws Exception {
		JsonArray jsonArray = this.str2jsonArr(serializedData);
		return removeBrokenJsonElements(this.removeEmptyJsonElementsFromArray(jsonArray));
	}
	/**
	 * removes empty json objects "{}" from the json array; e.g.: [{}] -> [];
	 * [{"k":"v"},{},{"u":"w"}] -> [{"k":"v"},{"u":"w"}]
	 *
	 * @param arr a json array
	 * @return the json array with all empty json objects {} removed from the array
	 */
	public JsonArray removeEmptyJsonElementsFromArray(final JsonArray arr) {
		JsonArray json_array = new JsonArray();
		for (JsonElement episode : arr) {
			if (episode.getAsJsonObject().size() > 0)
				json_array.add(episode.deepCopy());
		}
		return json_array;
	}
	public synchronized String indexedName(final String currentName, final List<String> names) {
		if (currentName == null)
			return null;
		if (names == null)
			return currentName;
		if (names.isEmpty())
			return currentName;
		final String cleanedCurrentName = currentName.trim();
		final Map<String, Integer> indexMap = new HashMap<String, Integer>();
		final String pattern = "^(.*)_\\(([0-9]*)\\)$";
		for (final String name : names) {
			final String cleaned = name.trim();
			final String indexStr = findInString(pattern, cleaned, 2).trim();
			int foundIndex = 0;
			if ((indexStr != null) && (!indexStr.contentEquals("")))
				foundIndex = Integer.parseInt(indexStr);
			String foundTitle = cleaned;
			if (foundIndex > 0)
				foundTitle = findInString(pattern, cleaned, 1).trim();
			int storedIndex = 0;
			if (indexMap.containsKey(foundTitle))
				storedIndex = indexMap.get(foundTitle);
			int maxIndex = Math.max(foundIndex, storedIndex);
			indexMap.put(foundTitle, maxIndex);
		}
		final String currentIndexStr = findInString(pattern, cleanedCurrentName, 2).trim();
		int currentFoundIndex = -1;
		if ((currentIndexStr != null) && (!currentIndexStr.contentEquals("")))
			currentFoundIndex = Integer.parseInt(currentIndexStr);
		String currentFoundTitle = cleanedCurrentName;
		if (currentFoundIndex >= 0)
			currentFoundTitle = findInString(pattern, cleanedCurrentName, 1).trim();
		int storedIndex = -1;
		if (indexMap.containsKey(currentFoundTitle))
			storedIndex = indexMap.get(currentFoundTitle);
		int maxIndex = Math.max(currentFoundIndex, storedIndex);
		if (maxIndex < 0)
			return currentFoundTitle;
		return currentFoundTitle + "_(" + (maxIndex + 1) + ")";
	}
	public synchronized String findInString(final String pattern, final String content, final int groupIndex) {
		Pattern modulePattern = null;
		modulePattern = Pattern.compile(pattern);
		final Matcher matcher = modulePattern.matcher(content);
		if (matcher.find()) {
			final String found = matcher.group(groupIndex);
			return found;
		}
		return "";
	}
	public synchronized JsonArray typeEpisodeConversion(JsonArray episodes, java.lang.Integer index,
			final String episode_oldType) {
		return episodes;
	}
	public synchronized String getMonthLabel(final String varname, final String monthStr) {
		AbstractAnswerBean variable = JsfUtility.getInstance().evaluateValueExpression(
				FacesContext.getCurrentInstance(), "#{" + varname + "}", AbstractAnswerBean.class);
		if (variable == null)
			return "";
		return this.labelOf(varname, "ao" + monthStr);
	}
	public synchronized String getLowestDate(final JsonArray episodes, final String property) {
		return getLowestDate(episodes, property, "1970-12-31T23:59:59.999Z");
	}
	public synchronized String getLowestDate(final JsonArray episodes, final String property, final String limit) {
		if (episodes == null)
			return "0000-00-00T00:00:00.000Z";
		if (episodes.size() == 0)
			return "0000-00-00T00:00:00.000Z";
		if (property == null)
			return "0000-00-00T00:00:00.000Z";
		final List<Date> sortedList = new ArrayList<Date>();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (final JsonElement episode : episodes) {
			if (!episode.isJsonObject())
				continue;
			final JsonObject episodeObj = episode.getAsJsonObject();
			if (!episodeObj.has(property))
				continue;
			final JsonElement propertyValue = episodeObj.get(property);
			if (!propertyValue.isJsonPrimitive())
				continue;
			try {
				if (this.compareJsonTimestamps(propertyValue.getAsString(), limit) > 0) {
					sortedList.add(df.parse(propertyValue.getAsString()));
				}
			} catch (Exception e) {
				continue;
			}
		}
		if (!sortedList.isEmpty()) {
			Collections.sort(sortedList);
			return df.format(sortedList.get(0));
		}
		return "0000-00-00T00:00:00.000Z";
	}
	public synchronized String getHighestDate(final JsonArray episodes, final String property) {
		return getHighestDate(episodes, property, "4000-01-01T00:00:00.000Z");
	}
	public synchronized String getHighestDate(final JsonArray episodes, final String property, final String limit) {
		if (episodes == null)
			return "0000-00-00T00:00:00.000Z";
		if (episodes.size() == 0)
			return "0000-00-00T00:00:00.000Z";
		if (property == null)
			return "0000-00-00T00:00:00.000Z";
		final List<Date> sortedList = new ArrayList<Date>();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for (final JsonElement episode : episodes) {
			if (!episode.isJsonObject())
				continue;
			final JsonObject episodeObj = episode.getAsJsonObject();
			if (!episodeObj.has(property))
				continue;
			final JsonElement propertyValue = episodeObj.get(property);
			if (!propertyValue.isJsonPrimitive())
				continue;
			try {
				if (this.compareJsonTimestamps(propertyValue.getAsString(), limit) < 0) {
					sortedList.add(df.parse(propertyValue.getAsString()));
				}
			} catch (Exception e) {
				continue;
			}
		}
		if (!sortedList.isEmpty()) {
			Collections.sort(sortedList);
			return df.format(sortedList.get(sortedList.size() - 1));
		}
		return "0000-00-00T00:00:00.000Z";
	}
	public synchronized boolean isSplittable(final JsonArray arr, final int index) {
		if (arr == null)
			return false;
		if (arr.size() == 0)
			return false;
		if (index < 0)
			return false;
		if (index >= arr.size())
			return false;
		final JsonObject episode = arr.get(index).getAsJsonObject();
		if (!episode.has("startDate")) {
			return false;
		}
		if (!episode.has("endDate")) {
			return false;
		}
		final String startDate = episode.get("startDate").getAsString();
		final String endDate = episode.get("endDate").getAsString();
		final String startYear = startDate.substring(0, 4);
		final String startMonth = startDate.substring(5, 7);
		final String endYear = endDate.substring(0, 4);
		final String endMonth = endDate.substring(5, 7);
		return !startYear.equals(endYear) || !startMonth.equals(endMonth);
	}
	/**
	 * Checks whether the year and month part of the "endDate" property of a give
	 * episode (a json element) is identical to the given corresponding strings
	 * calEndYear and calEndMonth.
	 * 
	 * @param episode     episode object
	 * @param calEndYear  the year to check the "endDate" timestamp for
	 * @param calEndMonth the month to check the "endDate" timestamp for
	 * @return "true" if the years and months are identical, "false" if not.
	 */
	public synchronized boolean isRecentEpisode(final JsonElement episode, final String calEndYear,
			String calEndMonth) {
		if (episode == null)
			return false;
		if (calEndMonth == null)
			return false;
		if (calEndYear == null)
			return false;
		try {
			Integer.parseInt(calEndMonth);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
		if (Integer.parseInt(calEndMonth) < 1 || Integer.parseInt(calEndMonth) > 12)
			return false;
		if (calEndMonth.length() == 1) {
			calEndMonth = "0".concat(calEndMonth);
		}
		if (!episode.isJsonObject())
			return false;
		final JsonObject episodeObj = episode.getAsJsonObject();
		if (!episodeObj.has("endDate"))
			return false;
		final JsonElement propertyValue = episodeObj.get("endDate");
		if (!propertyValue.isJsonPrimitive())
			return false;
		final String timestamp = propertyValue.getAsString();
		if (this.isWellFormedTimestamp(timestamp)) {
			final String endYear = timestamp.substring(0, 4);
			final String endMonth = timestamp.substring(5, 7);
			return (endYear.equals(calEndYear) && endMonth.equals(calEndMonth));
		}
		return false;
	}
	public synchronized boolean hasRecentEpisode(final JsonArray arr, final String calEndYear, String calEndMonth) {
		if (arr == null)
			return false;
		if (calEndMonth == null)
			return false;
		if (calEndYear == null)
			return false;
		if (Integer.parseInt(calEndMonth) < 1 || Integer.parseInt(calEndMonth) > 12)
			return false;
		if (calEndMonth.length() == 1) {
			calEndMonth = "0".concat(calEndMonth);
		}
		for (final JsonElement episode : arr) {
			if (isRecentEpisode(episode, calEndYear, calEndMonth))
				return true;
		}
		return false;
	}
	public synchronized String getCurrentDateTimestamp() {
		final Calendar currentCal = Calendar.getInstance();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return df.format(currentCal.getTime());
	}
	public synchronized final JsonObject getIfExists(final JsonArray episodes,final int index) {
		if (episodes == null)
			return null;
		if (index < 0)
			return null;
		if (episodes.size() < (index + 1))
			return null;
		final JsonElement back = episodes.get(index);
		if(back.isJsonObject())return back.getAsJsonObject();
		return null;
	}
	public synchronized JsonArray sortEpisodesByStartDate(final JsonArray arr) {
		return this.sortEpisodesByDate(arr, "startDate");
	}
	public synchronized JsonArray sortEpisodesByEndDate(final JsonArray arr) {
		return this.sortEpisodesByDate(arr, "endDate");
	}
	public synchronized JsonArray sortEpisodesByDate(final JsonArray arr,final String field) {
		if (arr == null)
			return null;
		if (arr.size() == 0)
			return arr;
		final ArrayList<JsonObject> array = new ArrayList<JsonObject>();
		final int size = arr.size();
		for (int i = 0; i < size; i++) {
			array.add(arr.get(i).getAsJsonObject());
		}
		try {
			Collections.sort(array, new Comparator<JsonObject>() {
				@Override
				public int compare(final JsonObject episode1, final JsonObject episode2) {
					String episode1EndDate = null;
					String episode2EndDate = null;
					if(episode1.has(field)) episode1EndDate = episode1.get(field).getAsString();
					if(episode2.has(field)) episode2EndDate = episode2.get(field).getAsString();
					if(!comparableTimestamps(episode1EndDate, episode2EndDate))return 0;
					return compareJsonTimestamps(episode1EndDate, episode2EndDate);
				}
			});
		} catch (final Exception e) {
			e.printStackTrace();
			return arr;
		}
		final JsonArray jsonArray = new JsonArray();
		for (final JsonObject episode : array) {
			jsonArray.add(episode);
		}
		return jsonArray;
	}
}
