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
package eu.dzhw.zofar.testing.condition.components;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import de.his.zofar.presentation.surveyengine.AbstractAnswerBean;
import de.his.zofar.presentation.surveyengine.StringValueTypeBean;
import eu.dzhw.zofar.testing.condition.term.elements.Element;
import model.ParticipantEntity;
import support.elements.conditionEvaluation.VariableBean;
public class FunctionProvider extends Element implements Serializable {
	private static final long serialVersionUID = 986132842770991084L;
	private static FunctionProvider INSTANCE = null;
	final Map<String, Object> properties;
	private FunctionProvider(final Map<String, Object> properties) {
		super("Zofar Dummy");
		this.properties = properties;
	}
	public static FunctionProvider getInstance(final Map<String, Object> properties) {
		if (INSTANCE == null)
			INSTANCE = new FunctionProvider(properties);
		return INSTANCE;
	}
	public Integer asNumber(String obj) {
		if (obj == null)
			return Integer.MIN_VALUE;
		if (obj.contentEquals("null"))
			return Integer.MIN_VALUE;
		if (obj != null) {
			try {
				return Integer.getInteger(obj + "");
			} catch (NumberFormatException e) {
				return Integer.MIN_VALUE;
			}
		}
		return Integer.MIN_VALUE;
	}
	public Integer asNumber(Object obj) {
		if (obj == null)
			return Integer.MIN_VALUE;
		if ((eu.dzhw.zofar.testing.condition.term.elements.Element.class).isAssignableFrom(obj.getClass())) {
			obj = ((eu.dzhw.zofar.testing.condition.term.elements.Element) obj).getValue();
		}
		if ((obj + "").contentEquals("null"))
			return Integer.MIN_VALUE;
		if ((obj + "").contentEquals("''"))
			return Integer.MIN_VALUE;
		if (obj != null) {
			try {
				return Integer.getInteger(obj + "");
			} catch (NumberFormatException e) {
				return Integer.MIN_VALUE;
			}
		}
		return Integer.MIN_VALUE;
	}
	public Integer asNumber(Integer obj) {
		if (obj != null) {
			try {
				return Integer.getInteger(obj + "");
			} catch (NumberFormatException e) {
				return Integer.MIN_VALUE;
			}
		}
		return Integer.MIN_VALUE;
	}
	public Double asNumber(Double obj) {
		if (obj != null) {
			try {
				return Double.valueOf(obj + "");
			} catch (NumberFormatException e) {
				return Double.MIN_VALUE;
			}
		}
		return Double.MIN_VALUE;
	}
	public Object isMissing(final Object obj) {
		return false;
	}
	public Boolean isMissing(final Boolean var) {
		if (var == null)
			return true;
		boolean back = !var;
		return back;
	}
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
		}
		return 0;
	}
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
	private List<Object> listHelper(final Object... objects) {
		return (List<Object>) Arrays.asList(objects);
	}
	public synchronized Integer isSetCounter(final List<VariableBean> vars, final ParticipantEntity participant) {
		if (vars != null) {
			int back = 0;
			for (Object var : vars) {
				if (((VariableBean.class).isAssignableFrom(var.getClass()))
						&& (isBooleanSet(((VariableBean) var).getVariableName(), participant)))
					back = back + 1;
			}
			return back;
		}
		return 0;
	}
	public synchronized Boolean isBooleanSetOld(final VariableBean var, final ParticipantEntity participant) {
		if (var == null)
			return false;
		if (participant == null)
			return false;
		if (participant.getSurveyData().containsKey(var.getVariableName()) && (var.getValue() + "").equals("true")) {
			return true;
		}
		return false;
	}
	public synchronized Boolean isBooleanSet(final String var, final ParticipantEntity participant) {
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
	public synchronized Object valueOf(final eu.dzhw.zofar.testing.condition.term.elements.Element var) {
		return null;
	}
	public synchronized Integer valueOf(final Boolean var) {
		return null;
	}
	public synchronized Object labelOf(eu.dzhw.zofar.testing.condition.term.elements.Element var) {
		return null;
	}
	public synchronized Object labelOfDropDown(support.elements.conditionEvaluation.VariableBean var) {
		if (var != null)
			return ((VariableBean) var).getValue();
		return null;
	}
	public synchronized Object isMobile() {
		return false;
	}
	public synchronized Object baseUrl() {
		return "";
	}
	public synchronized Object getTimeStamp() {
		return "";
	}
	public synchronized Object retrieveElementByID(java.lang.String str) {
		return "";
	}
	public synchronized Object decodeEpisodes(java.lang.Object obj) {
		return "";
	}
	public synchronized List<Object> explode(final String toSplit, final String splitBy) {
		return listHelper((Object[]) toSplit.split(splitBy));
	}
	public synchronized Integer countTilesWithEpisodesInRange(java.lang.String str, java.util.ArrayList<Object> data,
			java.lang.Integer val0, java.lang.Integer val1) {
		return 0;
	}
	public synchronized Object getEpisodesHTML5(java.lang.String var1, java.util.ArrayList<Object> var2,
			java.util.ArrayList<Object> var3) {
		return "";
	}
	public synchronized Boolean hasEpisodes(java.lang.String var1, java.util.ArrayList<Object> var2) {
		return false;
	}
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String rangeStart, final String rangeStop,
			final String property, final String value) throws Exception {
		return new JsonArray();
	}
	public synchronized Object ifthenelse(final Boolean condition, final Object thenElement, final Object elseElement) {
		if (condition)
			return thenElement;
		return elseElement;
	}
	public synchronized final boolean hasCurrentSplitType(JsonArray arr, final int index, final String splitType) {
		return false;
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
		return false;
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonElement episode, String jsonPropertyArrayName,
			String arrayEntryName) {
		return false;
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonElement episode, String jsonPropertyArrayName,
			List<Object> listOfArrayEntryNames) {
		return false;
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final String arrayEntryName) {
		return true;
	}
	private synchronized boolean hasJsonPrimitiveArrayEntry(final JsonArray arr, final int index,
			final String jsonPropertyArrayName, final List<Object> arrayEntryNamesList) {
		return true;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param vars
	 * @return
	 * @throws Exception
	 */
	public synchronized String defrac(final List<?> vars) {
		return "{}";
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param vars
	 * @param data
	 * @throws Exception
	 */
	public synchronized void frac(final List<?> vars, final String data) {
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param stamp
	 * @return
	 */
	public synchronized int getMonthFromStamp(final String stamp) {
		return -1;
	}
	public synchronized int getMonthFromJson(final String stamp) {
		return -1;
	}
	public synchronized String getMonthLabelFromJson(final String stamp) {
		return "";
	}
	public synchronized int getYearFromJson(final String stamp) {
		return -1;
	}
	/**
	 *
	 * @param episode
	 * @param filterList
	 * @return
	 */
	private boolean jsonObjectMatchesFilter(final JsonObject episode, final List<Map<String, String>> filterList) {
		return true;
	}
	/**
	 * [REVIEW][TODO]
	 *
	 * @param data
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public synchronized JsonArray filterArray(final JsonArray data, final List<Map<String, String>> filter)
			throws Exception {
		return new JsonArray();
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
		return new JsonArray();
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param serializedData input string of a json array, e.g.: [{"key1":"val1"},
	 *                       {"key1":"val2"}]
	 * @return a parsed json array
	 */
	public synchronized final JsonArray str2jsonArr(String serializedData) {
		return new JsonArray();
	}
	public void nothing() {
	}
	public synchronized final void assign(final String varname, final Object expression) {
		this.properties.put(varname, expression);
	}
	public synchronized final String urlencode(final String data) throws UnsupportedEncodingException {
		return URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
	}
	public synchronized final String jsonArr2str(final JsonArray data) {
		if (data == null)
			return "";
		String serializedData = data.toString();
		serializedData = serializedData.replaceAll(Pattern.quote("%"), "<prct>");
		serializedData = serializedData.replaceAll(Pattern.quote("+"), "<pls>");
		return serializedData;
	}
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
	public synchronized Map<String, String> map(final String toParse, final String pairSplit, final String itemSplit) {
		return new HashMap<String, String>();
	}
	public synchronized JsonObject episodesState(JsonArray episodes) {
		final JsonObject status = new JsonObject();
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
		return "";
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
				"^(((197|198|199)[0-9])|(20[0-9]{2})|(4000))-((0[0-9])|(1[012]))-(([012][0-9])|(3[01]))T(2[0-3]|[01]?[0-9]):00:00.000Z$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputTimestamp);
		return matcher.find();
	}
	private JsonArray removeEpisodeFromArrayViaId(final JsonArray arr, final JsonElement episode) {
		return arr;
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
		return true;
	}
	public synchronized final boolean hasJsonProperty(final JsonElement data, final String property) {
		return true;
	}
	/**
	 * [REVIEW][TODO]
	 * 
	 * @param data
	 * @param properties
	 */
	public synchronized final void getJsonProperties(final JsonElement data, final List<String> properties) {
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
	 * @param properties
	 */
	public synchronized final void resetVars(final List<String> properties) {
	}
	/**
	 * [REVIEW]
	 * 
	 * @param varName
	 */
	private void resetVar(final String varName) {
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
		return data;
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
		return "";
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
			JsonElement propertyValue = episodeObj.get(property);
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
			JsonElement propertyValue = episodeObj.get(property);
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
	public synchronized final void setVariableValue(final Element variable, final String value) {
	}
	public boolean log(final Object message1, final Object message2, final Object participant) {
		return true;
	}
	public boolean log(final String message, final Object participant) {
		return true;
	}
	public synchronized final float countEpisodesRatio(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		return -1;
	}
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String property, final String value)
			throws Exception {
		return new JsonArray();
	}
	public synchronized JsonArray hasEpisodes(final JsonArray data, final String rangeStart, final String rangeStop,
			final Map<String, String> filter) throws Exception {
		return new JsonArray();
	}
	public synchronized String getFromMap(final Map<String, String> map, final String key) {
		return "";
	}
	public synchronized String getFromMap(final Map<String, String> map,
			final eu.dzhw.zofar.testing.condition.term.elements.Element key) {
		return "";
	}
	public synchronized final JsonElement createJsonObject() {
		return new JsonObject();
	}
	public synchronized final JsonArray setFlag(JsonArray arr, final int index, final String flag) {
		return new JsonArray();
	}
	public synchronized final JsonObject setFlag(JsonElement episode, final String flag) {
		return new JsonObject();
	}
	public synchronized Boolean isSet(final String var, final Object participant) {
		return false;
	}
	public synchronized JsonObject parseJsonObj(final String toJson) {
		return new JsonObject();
	}
	public synchronized final Map<String, Object> splitEpisode(JsonArray arr, final int index,
			final JsonElement split_type_dict) {
		return this.splitEpisode(arr, index, split_type_dict, null, -1);
	}
	public synchronized final Map<String, Object> splitEpisode(JsonArray arr, final int index,
			final JsonElement split_type_dict, final int limit) {
		return this.splitEpisode(arr, index, split_type_dict, null, limit);
	}
	public synchronized final Map<String, Object> splitEpisode(JsonArray arr, final int index,
			final JsonElement split_type_dict, final List<String> blacklist) {
		return this.splitEpisode(arr, index, split_type_dict, blacklist, -1);
	}
	public synchronized final Map<String, Object> splitEpisode(JsonArray arr, final int index,
			final JsonElement split_type_dict, final List<String> blacklist, final int limit) {
		return new HashMap<String, Object>();
	}
	public synchronized final JsonElement getOrCreateJson(JsonArray arr, final int index) {
		return new JsonObject();
	}
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
	public synchronized int getYearFromStamp(final String stamp) {
		return -1;
	}
	public String asTimestamp(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second,
			Integer millisecond) {
		return "1970-01-01T01:00:00.000Z";
	}
	public synchronized JsonElement mergeEpisodes1(final JsonElement episodes1, final JsonElement episodes2,
			final String key) throws Exception {
		return new JsonArray();
	}
	public synchronized int nextEpisodeIndex(final JsonArray arr, final int startIndex,
			final List<Map<String, String>> filterList) {
		return -1;
	}
	public synchronized int nextEpisodeIndex(final JsonArray arr, final List<Map<String, String>> filterList) {
		return -1;
	}
	public synchronized int nextEpisodeIndex(final JsonArray arr, final int startIndex, final String property,
			final String value) throws Exception {
		return -1;
	}
	public synchronized int maxEpisodeID(final JsonArray data) throws Exception {
		return -1;
	}
	public synchronized final String formatDate(final int day, final int month, final int year) {
		return "1970-01-01T01:00:00.000Z";
	}
    public synchronized int getMonth(final eu.dzhw.zofar.testing.condition.term.elements.Element var) throws ParseException {
        return -1;
    }
	public synchronized final boolean isEpisodesComplete(final JsonArray data, final String rangeStart,
			final String rangeStop) throws ParseException {
		return true;
	}
	public synchronized final int lastDayOfMonth(final int month, final int year) {
		return -1;
	}
	public synchronized final JsonArray setCurrentSplitType(JsonArray arr, final int index,
			final String currentSplitType) {
		return new JsonArray();
	}
	public synchronized final JsonObject setCurrentSplitType(JsonElement episode, final String currentSplitType) {
		return new JsonObject();
	}
	public synchronized final boolean doSplitOnEndPageCandidate(JsonArray arr, final int index,
			final List<Object> splitTypeList) {
		return false;
	}
	public synchronized JsonElement mergeEpisodes(final JsonElement episodes1, final JsonElement episodes2,
			final String key) throws Exception {
		return new JsonArray();
	}
	public synchronized final JsonArray deleteCurrentSplitType(JsonArray arr, final int index,
			final String currentSplitType) {
		return new JsonArray();
	}
	public synchronized final JsonArray deleteCurrentSplitType(JsonArray arr, final int index,
			final List<Object> currentSplitTypeList) {
		return new JsonArray();
	}
	public synchronized final JsonObject deleteCurrentSplitType(JsonElement episode, final String currentSplitType) {
		return new JsonObject();
	}
	public synchronized final JsonObject deleteCurrentSplitType(JsonElement episode,
			final List<Object> currentSplitTypeList) {
		return new JsonObject();
	}
    public synchronized final boolean hasFlag(JsonArray arr, final int index, final String flag) {
        return true;
    }
    public synchronized final boolean hasFlag(JsonElement episode, final String flagName) {
        return true;
    }
    public synchronized final JsonArray deleteEpisode(final JsonArray episodes, final int episodeIndex) {
    	return new JsonArray();
    }
}
