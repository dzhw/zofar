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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.AbstractAnswerBean;
import de.his.zofar.presentation.surveyengine.AbstractLabeledAnswerBean;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.util.JsfUtility;
/**
 * Bean to provide Layout - Support in ResourceBundle
 * 
 * @author meisner
 * 
 */
@ManagedBean(name = "layout")
@SessionScoped
public class LayoutProvider extends ResourceBundle implements Serializable {
	private static final long serialVersionUID = -5242684581220871561L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LayoutProvider.class);
	private Map<String, String> linkMap;
	private Map<String, String> reverseLinkMap;
	private Map<String, String[]> elMap;
	public LayoutProvider() {
		super();
		linkMap = new HashMap<String, String>();
		reverseLinkMap = new HashMap<String, String>();
		elMap = new HashMap<String, String[]>();
		LOGGER.debug("created");
	}
	@Override
	protected Object handleGetObject(String key) {
		String msg = JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(), "#{layoutbundle['" + key + "']}", String.class);
		if ((msg != null) && ((String.class).isAssignableFrom(msg.getClass()))) {
			msg = JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(), msg + "", String.class);
		}
		return msg;
	}
	private Object evaluate(Object key) {
		if (key == null)
			return null;
		Object tmp = null;
		if ((String.class).isAssignableFrom(key.getClass()))
			tmp = key;
		else if ((AbstractLabeledAnswerBean.class).isAssignableFrom(key.getClass())) {
			final AbstractLabeledAnswerBean bean = (AbstractLabeledAnswerBean) key;
			tmp = bean.toPlaceholder();
		} else if ((AbstractAnswerBean.class).isAssignableFrom(key.getClass())) {
			final AbstractAnswerBean bean = (AbstractAnswerBean) key;
			tmp = bean.toPlaceholder();
		} else if ((List.class).isAssignableFrom(key.getClass())) {
			final List mergeList = (List) key;
			final StringBuffer buffer = new StringBuffer();
			if ((mergeList != null) && (!mergeList.isEmpty())) {
				final Iterator it = mergeList.iterator();
				while (it.hasNext()) {
					buffer.append(evaluate(it.next()));
				}
			}
			tmp = buffer.toString();
		}
		return tmp;
	}
	private String getLinkId(final String href) {
		if (this.reverseLinkMap.containsKey(href))
			return this.reverseLinkMap.get(href);
		else {
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			this.reverseLinkMap.put(href,id);
			this.linkMap.put(id,href);
			return id;
		}
	}
	private String getLinkById(final String id) {
		return this.linkMap.get(id);
	}
	public Object hyperlink(final String href,final String label,final String target){
		return "<a hRef='"+href+"' target='"+target+"'class='zo-layout-link'>"+label+"</a>";
	}
	public Object link(final String href, final String label) {
		return link(href,label,"_blank",null,null);
	}
	public Object link(final String href, final String label,final String var,final String value) {
		return link(href,label,"_blank",var,value);
	}
	public Object link(final String href, final String label, final String target,final String var,final String value) {
		String url = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		String id = getLinkId(href);
		if((var != null)&&(value != null)){
			if(this.elMap == null)this.elMap = new HashMap<String, String[]>();
			this.elMap.put(id, new String[]{var,value});
		}
		return "<a hRef='" + url + "/link.html?id=" + id + "' target='" + target + "'class='zo-layout-link'>" + label + "</a>";
	}
	public String getLink(final String id){
		return this.getLinkById(id);
	}
	public boolean haveEl(final String id){
		if (this.elMap.containsKey(id)) {
			final String[] expression = this.elMap.get(id);
			if ((expression != null) && (expression.length == 2)) {
				return true;
			}
		}
		return false;
	}
	public IAnswerBean getVar(final String id){
		if (this.elMap.containsKey(id)) {
			final String[] expression = this.elMap.get(id);
			if ((expression != null) && (expression.length == 2)) {
				return JsfUtility.getInstance().evaluateValueExpression(FacesContext.getCurrentInstance(), "#{"+expression[0]+"}", IAnswerBean.class);
			}
		}
		return null;
	}
	public String getValue(final String id){
		if (this.elMap.containsKey(id)) {
			final String[] expression = this.elMap.get(id);
			if ((expression != null) && (expression.length == 2)) {
				return expression[1];
			}
		}
		return null;
	}
	public Object mail(final String adress, final String label) {
		return "<a hRef='mailto:" + adress + "'class='zo-layout-link zo-layout-mail'>" + label + "</a>";
	}
	public Object mail(final String adress, final String label, final String subject) {
		return "<a hRef='mailto:" + adress + "?subject=" + subject + "'class='zo-layout-link zo-layout-mail'>" + label + "</a>";
	}
	public Object mail(final String adress, final String label, final String subject, final String body) {
		return "<a hRef='mailto:" + adress + "?subject=" + subject + "&body=" + body + "'class='zo-layout-link zo-layout-mail'>" + label + "</a>";
	}
	public Object image(final String src, final String alternativ) {
		return "<img src='" + src + "' alt='" + alternativ + "' class='zo-layout-image'>";
	}
	public Object image(final String src, final String alternativ, final int width, final int height) {
		return "<img src='" + src + "' alt='" + alternativ + "' width='" + width + "' height='" + height + "' class='zo-layout-image'>";
	}
	public Object table(final List<Object> titles, final List<List<Object>> rows) {
		if (titles == null)
			return "";
		if (titles.isEmpty())
			return "";
		final StringBuffer back = new StringBuffer();
		back.append("<table>");
		back.append("<tr>");
		final Iterator<Object> it = titles.iterator();
		while (it.hasNext()) {
			back.append("<th>" + evaluate(it.next()) + "</th>");
		}
		back.append("</tr>");
		final Iterator<List<Object>> it1 = rows.iterator();
		while (it1.hasNext()) {
			final List<Object> row = it1.next();
			if (row == null)
				continue;
			if (row.isEmpty())
				continue;
			back.append("<tr>");
			final Iterator<Object> it2 = row.iterator();
			while (it2.hasNext()) {
				final Object item = it2.next();
				back.append("<td>" + evaluate(item) + "</td>");
			}
			back.append("</tr>");
		}
		back.append("</table>");
		return back.toString();
	}
	@Override
	public Enumeration<String> getKeys() {
		return null;
	}
}
