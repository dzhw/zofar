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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
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
 * @version 0.0.2
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
		random = new Random();
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
		final Date date = new Date();
		final String dateString = simpleDateFormat.format(date);
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
			final String dateString = ((StringValueTypeBean) var).getValue();
			try {
				final Date date = simpleDateFormat.parse(dateString);
				simpleDateFormat.applyPattern("M");
				month = Integer.parseInt(simpleDateFormat.format(date));
			} catch (final ParseException e) {
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
			final String dateString = ((StringValueTypeBean) var).getValue();
			final Date date = simpleDateFormat.parse(dateString);
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
			final String dateString = ((StringValueTypeBean) var).getValue();
			final Date date = simpleDateFormat.parse(dateString);
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
			final String dateString = ((StringValueTypeBean) var).getValue();
			final Date date = simpleDateFormat.parse(dateString);
			simpleDateFormat.applyPattern("YYYY");
			final int year = Integer.parseInt(simpleDateFormat.format(date));
			simpleDateFormat.applyPattern("M");
			final int month = Integer.parseInt(simpleDateFormat.format(date));
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
				final BooleanValueTypeBean tmp = (BooleanValueTypeBean) var;
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
			final Map<String, Object> labels = var.getLabels();
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
		final Map<String, String> back = new LinkedHashMap<String, String>();
		if (!toParse.contains(itemSplit)) {
			if (toParse.contains(pairSplit)) {
				final String[] pair = toParse.split(Pattern.quote(pairSplit));
				if ((pair != null) && (pair.length == 2)) {
					back.put(pair[0], pair[1]);
				}
			}
		} else {
			final String[] items = toParse.split(Pattern.quote(itemSplit));
			if (items != null) {
				for (final String item : items) {
					if (item.contains(pairSplit)) {
						final String[] pair = item.split(Pattern.quote(pairSplit));
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
	private List<Object> listHelper(final Object... objects) {
		return Arrays.asList(objects);
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
		return new ArrayList(stack);
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
	private String concatHelper(final String... objects) {
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
			final List<Object> valueList = new ArrayList<Object>(options.values());
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
		} catch (final NumberFormatException exp) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("keine Zahl {}", input);
			}
		}
		return 0D;
	}
	public synchronized Double asNumberNeu(final Object input, final SessionController session) throws Exception {
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
			} catch (final NumberFormatException exp) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("keine Zahl {}", input);
				}
			}
		} catch (final Exception e) {
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
	public synchronized Integer asInteger(final Object input, final SessionController session) throws Exception {
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
		} catch (final NumberFormatException exp) {
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
		} catch (final NumberFormatException exp) {
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
				} catch (final NumberFormatException exp) {
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
				} catch (final NumberFormatException exp) {
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
			final Object value = valueOf(var);
			if (value == null) {
				sum = sum + Double.parseDouble("0");
			}
			if (value != null && !value.toString().isEmpty()) {
				try {
					sum = sum + Double.parseDouble(value.toString());
				} catch (final NumberFormatException exp) {
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
			final Object value = valueOf(var);
			if (value == null) {
			}
			if (value != null && !value.toString().isEmpty()) {
				try {
					if (Integer.parseInt(value.toString()) > 0) {
						counter++;
					}
				} catch (final NumberFormatException exp) {
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
		} catch (final NumberFormatException exp) {
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
		final String userAgent = userAgent();
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
		final String back = httpServletRequest.getServerName();
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
		final String back = httpServletRequest.getServerPort() + "";
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
					return temperature;
				}
				@Override
				public void startElement(final String uri, final String localName, final String qName,
						final Attributes attributes) throws SAXException {
					super.startElement(uri, localName, qName, attributes);
					if (qName.equals("temperature")) {
						final String current = attributes.getValue("value");
						final String min = attributes.getValue("min");
						final String max = attributes.getValue("max");
						final String unit = attributes.getValue("unit");
						temperature = current;
					}
				}
			}
			final ContentHandler handler = new ContentHandler();
			sp.parse(completeUrl, handler);
			return handler.getTemperatur();
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param property
	 * @return
	 */
	private synchronized final Object getJsonProperty(final JsonElement data, final String property) {
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
			for (final Entry<String, JsonElement> item : tmpJSON.entrySet()) {
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
		} catch (final UnsupportedEncodingException e) {
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
	private String callURL1(final String myURL, final String username, final String password) {
		final WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
		final Resource resource = applicationContext.getResource(myURL);
		final StringBuffer result = new StringBuffer();
		try {
			final InputStream is = resource.getInputStream();
			final BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			br.close();
		} catch (final IOException e) {
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
	private synchronized String callURL(final String myURL) {
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
	private synchronized String callURL(final String myURL, final String username, final String password) {
		final StringBuilder sb = new StringBuilder();
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
			final URL url = new URL(myURL);
			urlConn = url.openConnection();
			if (urlConn != null) {
				((HttpURLConnection) urlConn).setInstanceFollowRedirects(true);
				urlConn.setReadTimeout(15000);
				if (authHeaderValue != null)
					urlConn.setRequestProperty("Authorization", authHeaderValue);
			}
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				final BufferedReader bufferedReader = new BufferedReader(in);
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
		} catch (final Exception e) {
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
		} catch (final IOException e) {
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
		final FacesContext context = FacesContext.getCurrentInstance();
		final UIViewRoot root = context.getViewRoot();
		final UIComponent[] found = new UIComponent[1];
		root.visitTree(new FullVisitContext(context), new VisitCallback() {
			@Override
			public VisitResult visit(final VisitContext context, final UIComponent component) {
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
		final UIComponent root = FacesContext.getCurrentInstance().getViewRoot();
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
	private UIComponent findComponent(final UIComponent root, final String id) throws Exception {
		UIComponent result = null;
		if (root.getId().equals(id))
			return root;
		for (final UIComponent child : root.getChildren()) {
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
						final UICalendarItem item = (UICalendarItem) sheetChild;
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
					for (final UICalendarItem item : back) {
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
				final UICalendarItem calendarItem = (UICalendarItem) item;
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
		} catch (final Exception e) {
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
		for (final Map.Entry<Integer, List<List<String>>> slotBeam : beams.entrySet()) {
			if (!first1)
				serializedMap.append(";");
			serializedMap.append(slotBeam.getKey() + "#");
			final StringBuffer tmp = new StringBuffer();
			for (final List<String> episode : slotBeam.getValue()) {
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
		final byte[] compressed = Snappy.compress(content.getBytes());
		String back = null;
		if (encoded) {
			back = new String(Hex.encodeHex(compressed));
		} else
			back = new String(compressed);
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
				final List<UICalendarItem> back = new ArrayList<UICalendarItem>();
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
			final Map<Integer, BooleanValueTypeBean> back = new HashMap<Integer, BooleanValueTypeBean>();
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
					for (final Map.Entry<Integer, BooleanValueTypeBean> slot : slots.entrySet()) {
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
			final int randomIndex = rand.nextInt(items.size());
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
			private final Deque<Map<String, Object>> stack = new ArrayDeque<Map<String, Object>>();
			public ContentHandler(final Map<String, Object> map) {
				super();
				this.map = map;
			}
			public Map<String, Object> getMap() {
				return map;
			}
			@Override
			public void startElement(final String namespaceURI, final String localName, final String qName,
					final Attributes atts) {
				final Map<String, Object> elemMap = new LinkedHashMap<String, Object>();
				elemMap.put("type", qName);
				final int attrLen = atts.getLength();
				final Map<String, Object> attrMap = new LinkedHashMap<String, Object>();
				for (int i = 0; i < attrLen; i++) {
					attrMap.put(atts.getQName(i), atts.getValue(i));
				}
				if (!attrMap.isEmpty())
					elemMap.put("attributes", attrMap);
				stack.push(elemMap);
			}
			@Override
			public void endElement(final String uri, final String localName, final String qName) throws SAXException {
				super.endElement(uri, localName, qName);
				final Map<String, Object> elemMap = stack.pop();
				List<Object> childs = (List<Object>) stack.peek().get("childs");
				if (childs == null)
					childs = new ArrayList<Object>();
				childs.add(elemMap);
				stack.peek().put("childs", childs);
			}
			@Override
			public void startDocument() throws SAXException {
				stack.push(map);
			}
			@Override
			public void endDocument() throws SAXException {
			}
			@Override
			public void characters(final char[] ch, final int start, final int length) {
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
		} catch (final Exception e) {
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
		} catch (final Exception e) {
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
		} catch (final Exception e) {
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
		} catch (final Exception e) {
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
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			final Date parse = sdf.parse(stamp);
			final Calendar c = Calendar.getInstance();
			c.setTime(parse);
			final int back = c.get(Calendar.MONTH);
			return back;
		} catch (final Exception e) {
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
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			final Date parse = sdf.parse(stamp);
			final Calendar c = Calendar.getInstance();
			c.setTime(parse);
			return "" + new SimpleDateFormat("MMM").format(c.getTime());
		} catch (final Exception e) {
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
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			final Date parse = sdf.parse(stamp);
			final Calendar c = Calendar.getInstance();
			c.setTime(parse);
			final int back = c.get(Calendar.YEAR);
			return back;
		} catch (final Exception e) {
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
		} catch (final Exception e) {
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
		} catch (final Exception e) {
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
		} catch (final Exception e) {
			LOGGER.error("setYearToStamp failed ", e);
		}
		return stamp;
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
	/**
	 * [REVIEW][TODO]
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public synchronized final JsonElement createJsonObject() {
		return new JsonObject();
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
			} catch (final ParseException e) {
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
		final AbstractAnswerBean var = JsfUtility.getInstance().evaluateValueExpression(
				FacesContext.getCurrentInstance(), "#{" + cleaned + "}", AbstractAnswerBean.class);
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
	 * @param date
	 * @return
	 */
	public synchronized final String formatDate(final String date) {
		final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		final SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
		try {
			return outputFormat.format(inputFormat.parse(date));
		} catch (final ParseException e) {
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
		final Calendar cal = Calendar.getInstance();
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
		final Calendar cal = Calendar.getInstance();
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
		} catch (final Exception e) {
			LOGGER.info("Fehler : {} {}", variable, value);
			e.printStackTrace();
		}
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
		final ExpressionFactory factory = context.getApplication().getExpressionFactory();
		final Class[] classList = new Class[1];
		classList[0] = ActionEvent.class;
		final MethodExpression action = factory.createMethodExpression(context.getELContext(), "same", null, classList);
		sourceBt.setActionExpression(action);
		final ActionEvent actionEvent = new ActionEvent(sourceBt);
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
		final Pattern pattern = Pattern.compile(
				"^(((197|198|199)[0-9])|(20[0-9]{2})|(4000))-((0[0-9])|(1[012]))-(([012][0-9])|(3[01]))T(2[0-3]|[01][0-9]):[012345][0-9]:[012345][0-9].[0-9]{3}Z$",
				Pattern.CASE_INSENSITIVE);
		final Matcher matcher = pattern.matcher(inputTimestamp);
		return matcher.find();
	}
	public boolean isMissingDateValue(final String inputTimestamp) {
		return !isWellFormedTimestamp(inputTimestamp) || inputTimestamp.compareTo("1970-01-01T01:00:00.000Z") <= 0
				|| inputTimestamp.compareTo("4000-01-01T01:00:00.000Z") >= 0;
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
			} catch (final ParseException e) {
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
	public boolean isTimestampValid(final int year, final int month, final int day, final int hour, final int minute,
			final int second, final int millisecond) {
		final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
		final String timeStampStr = asTimestamp(year, month, day, hour, minute, second, millisecond);
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
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	public String asTimestamp(final int year, final int month, final int day, final int hour, final int minute,
			final int second, final int millisecond) {
		try {
			final Calendar cal = new GregorianCalendar();
			cal.set(year, month, day, hour, minute, second);
			final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
			return stampFormat.format(cal.getTime());
		} catch (final NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		return "1970-01-01T01:00:00.000Z";
	}
	public String timestampSetDatetime(final String inputTimestamp, final int years, final int months, final int days,
			final int hours, final int minutes, final int seconds, final int milliseconds) {
		try {
			final int year = Integer.parseInt(inputTimestamp.substring(0, 4));
			final int month = Integer.parseInt(inputTimestamp.substring(5, 7)) - 1;
			final int day = Integer.parseInt(inputTimestamp.substring(8, 10));
			final int hour = Integer.parseInt(inputTimestamp.substring(11, 13));
			final int minute = Integer.parseInt(inputTimestamp.substring(14, 16));
			final int second = Integer.parseInt(inputTimestamp.substring(17, 19));
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
		} catch (final NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		return "1970-01-01T01:00:00.000Z";
	}
	public String timestampTimeDelta(final String inputTimestamp, final int years, final int months, final int days,
			final int hours, final int minutes, final int seconds, final int milliseconds) {
		try {
			final int year = Integer.parseInt(inputTimestamp.substring(0, 4));
			final int month = Integer.parseInt(inputTimestamp.substring(5, 7)) - 1;
			final int day = Integer.parseInt(inputTimestamp.substring(8, 10));
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
		} catch (final NumberFormatException nfe) {
			nfe.printStackTrace();
			return "1970-01-01T01:00:00.000Z";
		}
	}
	public boolean isMissingDateValueParseable(final String timestamp) {
		if (timestamp != null && !timestamp.isEmpty() && isTimeStampParseable(timestamp)) {
			try {
				final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				final Date valMinMissing = simpleDateFormat.parse("1970-12-31T23:59:59.999Z");
				final Date valMaxMissing = simpleDateFormat.parse("4000-01-01T00:00:00.000Z");
				final Date val = simpleDateFormat.parse(timestamp);
				if (valMinMissing.compareTo(val) >= 0 || valMaxMissing.compareTo(val) <= 0) {
					return true;
				}
			} catch (final Exception e) {
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
			} catch (final Exception e) {
			}
		}
		return false;
	}
	public String stringNull() {
		return null;
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
			final int maxIndex = Math.max(foundIndex, storedIndex);
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
		final int maxIndex = Math.max(currentFoundIndex, storedIndex);
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
	public synchronized String getMonthLabel(final String varname, final String monthStr) {
		final AbstractAnswerBean variable = JsfUtility.getInstance().evaluateValueExpression(
				FacesContext.getCurrentInstance(), "#{" + varname + "}", AbstractAnswerBean.class);
		if (variable == null)
			return "";
		return this.labelOf(varname, "ao" + monthStr);
	}
	public synchronized String getCurrentDateTimestamp() {
		final Calendar currentCal = Calendar.getInstance();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return df.format(currentCal.getTime());
	}
	public synchronized Float minFloatList(final List<Float> valueList) {
		return Collections.min(valueList);
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param serializedData input string of a json array, e.g.: [{"key1":"val1"},
	 *                       {"key1":"val2"}]
	 * @return a parsed json array
	 */
	@Deprecated
	public synchronized JsonArray str2jsonArr(final String serializedData) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.str2jsonArr(serializedData);
	}
	@Deprecated
	public synchronized JsonArray sortedLikeNextEpisodeIndex(final JsonArray arr) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.sortedLikeNextEpisodeIndex(arr);
	}
	@Deprecated
	public synchronized JsonArray sortedLikeNextEpisodeIndex(final JsonArray arr, final List<Object> types) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.sortedLikeNextEpisodeIndex(arr, types);
	}
	@Deprecated
	public synchronized final List<JsonElement> asEpisodeList(final JsonArray episodes) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.asEpisodeList(episodes);
	}
	@Deprecated
	public synchronized final String actionEpisode(final IAnswerBean variable, final String episodeId,
			final String targetExpr, final String action) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.actionEpisode(variable, episodeId, targetExpr, action);
	}
	@Deprecated
	public synchronized final String actionEpisode(final String variableValue, final String episodeId,
			final String targetExpr, final String action) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.actionEpisode(variableValue, episodeId, targetExpr, action);
	}
	@Deprecated
	public synchronized String labelOfType(final JsonArray types, final String type, final String language) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.labelOfType(types, type, language);
	}
	@Deprecated
	public synchronized final boolean hasEpisodeDetail(final JsonElement typeDetails, final String property) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.hasEpisodeDetail(typeDetails, property);
	}
	@Deprecated
	public synchronized final boolean hasFlag(final JsonElement episode, final String flagName) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.hasFlag(episode, flagName);
	}
	@Deprecated
	public synchronized final boolean hasFlag(final JsonArray arr, final int index, final String flag) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.hasFlag(arr, index, flag);
	}
	@Deprecated
	public synchronized final boolean hasJsonProperty(final JsonElement data, final String property) {
		final EpisodesProvider episodesProvider = JsfUtility.getInstance()
				.evaluateValueExpression(FacesContext.getCurrentInstance(), "#{episodes}", EpisodesProvider.class);
		return episodesProvider.hasJsonProperty(data, property);
	}
}
