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
/*
 * 
 */
package de.his.zofar.presentation.surveyengine.ui.renderer.comparison;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.MultipleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.RadioButtonSingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.composite.comparison.UIComparisonItem;
import de.his.zofar.presentation.surveyengine.ui.components.composite.comparison.UIComparisonResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.composite.comparison.UIComparisonUnit;
import de.his.zofar.presentation.surveyengine.ui.components.question.UIQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixResponseDomainRenderer;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = UIComparisonResponseDomain.COMPONENT_FAMILY, rendererType = ZofarComparisonResponseDomainRenderer.RENDERER_TYPE)
public class ZofarComparisonResponseDomainRenderer extends ZofarMatrixResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.ComparisonResponseDomain";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarComparisonResponseDomainRenderer.class);
	public ZofarComparisonResponseDomainRenderer() {
		super();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeBegin(context, component, "zo-comparison-rdc");
	}
	@Override
	protected void renderHeader(final FacesContext context, final UIComponent component) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		if ((UIComparisonResponseDomain.class).isAssignableFrom(component.getClass())) {
			final UIComparisonResponseDomain rdc = (UIComparisonResponseDomain) component;
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", "zo-comparison-header-title zo-comparison-header-blank", null);
			writer.endElement("td");
			int lft1 = 0;
			for (final UIComponent item : rdc.getChildren()) {
				if ((UIComparisonItem.class).isAssignableFrom(item.getClass())) {
					final UIComparisonItem tmp = (UIComparisonItem) item;
					int lft2 = 0;
					for (final UIComponent question : tmp.getChildren()) {
						if ((UIQuestion.class).isAssignableFrom(question.getClass())) {
							if (!question.isRendered())continue;
							final UIQuestion tmp1 = (UIQuestion) question;
							writer.startElement("td", component);
							writer.writeAttribute("class", "zo-comparison-header-title zo-comparison-header-title-"+lft1+" zo-comparison-header-title-question-"+lft1+"-"+lft2, null);
							this.encodeQuestionHeaderFacet(context, writer, tmp1);
							writer.endElement("td");
							lft2 = lft2 + 1;
						}
					}
					lft1 = lft1 + 1;
				}
			}
			writer.endElement("tr");
		}
	}
	/**
	 * encodes the header facet and wraps it with a DIV block.
	 * 
	 * @param context
	 * @param component
	 * @throws IOException
	 */
	private void encodeQuestionHeaderFacet(final FacesContext context, final ResponseWriter writer, final UIQuestion component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final UIComponent header = component.getFacet("header");
		if (header != null) {
			header.encodeAll(context);
		}
	}
	@Override
	protected void renderScaleHeader(final FacesContext context, final UIComponent component) throws IOException {
	}
	@Override
	protected void renderMissingHeader(final FacesContext context, final UIComponent component) throws IOException {
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComparisonResponseDomain rdc = (UIComparisonResponseDomain) component;
		final UIComponent header = component.getFacet("header");
		final UIComponent missings = component.getFacet("missingHeader");
		final List<UIComponent> allTitles = new ArrayList<UIComponent>();
		if (header != null)
			allTitles.addAll(this.retrieveTitles(header));
		if (missings != null)
			allTitles.addAll(this.retrieveTitles(missings));
		final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping = this.retrieveMapping(rdc, allTitles);
		final int rowIndex = 1;
		final String itemClassesAttribute = (String) component.getAttributes().get("itemClasses");
		final String[] rowClasses = this.itemClassesToArray(itemClassesAttribute);
		this.addItems(context, rdc, header, writer, mapping,rowIndex,rowClasses);
		this.addItems(context, rdc, missings, writer, mapping,rowIndex,rowClasses);
	}
	private List<UIComponent> retrieveTitles(final UIComponent parent) {
		if (parent == null)
			return null;
		final List<UIComponent> back = new ArrayList<UIComponent>();
		final List<UIComponent> childs = parent.getChildren();
		for (final UIComponent child : childs) {
			if ((UIComparisonUnit.class).isAssignableFrom(child.getClass())) {
				back.addAll(this.retrieveTitles(child));
			} else if ((UISort.class).isAssignableFrom(child.getClass())) {
				back.addAll(this.retrieveTitles(child));
			} else if ((UIText.class).isAssignableFrom(child.getClass())) {
				back.add(child);
			}
		}
		return back;
	}
	private Map<UIComponent, Map<UIQuestion, UIComponent>> retrieveMapping(final UIComparisonResponseDomain rdc, final List<UIComponent> allTitles) {
		final Map<UIComponent, Map<UIQuestion, UIComponent>> back = new LinkedHashMap<UIComponent, Map<UIQuestion, UIComponent>>();
		for (final UIComponent title : allTitles) {
			final Map<UIQuestion, UIComponent> itemMap = new LinkedHashMap<UIQuestion, UIComponent>();
			final int index = allTitles.indexOf(title);
			final int count = rdc.getChildCount();
			for (int a = 0; a < count; a++) {
				final UIQuestion question = this.getQuestion(rdc, a);
				if (question == null)continue;
				if (!question.isRendered())continue;
				final UIComponent ao = this.getAnswerOption(question, index);
				itemMap.put(question, ao);
			}
			back.put(title, itemMap);
		}
		return back;
	}
	private int addItems(final FacesContext context, final UIComparisonResponseDomain rdc, final UIComponent titleContainer, final ResponseWriter writer, final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping,final int rowIndex,final String[] rowClasses) throws IOException {
		if (context == null)
			return rowIndex;
		if (rdc == null)
			return rowIndex;
		if (titleContainer == null)
			return rowIndex;
		final int count = this.getItemCount(rdc);
		int back = rowIndex;
		for (final UIComponent child : titleContainer.getChildren()) {
			if ((UIComparisonUnit.class).isAssignableFrom(child.getClass())) {
				final UIComparisonUnit tmp = (UIComparisonUnit) child;
				back = this.addItemsHelperUnit(context, writer, rdc, count, tmp, mapping, 0,back,rowClasses);
			} else if ((UIText.class).isAssignableFrom(child.getClass())) {
				final UIText tmp = (UIText) child;
				back = this.addItemsHelperItem(context, writer, rdc, mapping, tmp, 0,back,rowClasses);
			}
		}
		return back;
	}
	private int addItemsHelperUnit(final FacesContext context, final ResponseWriter writer, final UIComparisonResponseDomain rdc, final int count, final UIComparisonUnit unit, final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping, final int indent,final int rowIndex,final String[] rowClasses) throws IOException {
		if (context == null)
			return rowIndex;
		if (writer == null)
			return rowIndex;
		if (rdc == null)
			return rowIndex;
		if (unit == null)
			return rowIndex;
		int back = rowIndex;
		if (unit.isRendered()) {
			writer.startElement("tr", rdc);
			writer.writeAttribute("class", "zo-comparison-unit-row"+rowIndex, null);
			writer.startElement("td", rdc);
			writer.writeAttribute("class", "zo-comparison-unit-title zo-comparison-unit-indent" + indent, null);
			writer.writeAttribute("colspan", count + 1, null);
			final UIComponent header = unit.getFacet("header");
			if (header != null)
				header.encodeAll(context);
			writer.endElement("td");
			writer.endElement("tr");
		}
		for (final UIComponent child : unit.getChildren()) {
			if ((UIComparisonUnit.class).isAssignableFrom(child.getClass())) {
				final UIComparisonUnit tmp = (UIComparisonUnit) child;
				if (!unit.isRendered())
					tmp.setRendered(false);
				back = this.addItemsHelperUnit(context, writer, rdc, count, tmp, mapping, indent + 1,back,rowClasses);
			} else if ((UIText.class).isAssignableFrom(child.getClass())) {
				final UIText tmp = (UIText) child;
				if (!unit.isRendered())
					tmp.setRendered(false);
				back = this.addItemsHelperItem(context, writer, rdc, mapping, tmp, indent + 1,back,rowClasses);
			} else if ((UISort.class).isAssignableFrom(child.getClass())) {
				final UISort tmp = (UISort) child;
				for (final UIComponent sortChild : tmp.sortChildren()) {
					if ((UIComparisonUnit.class).isAssignableFrom(sortChild.getClass())) {
						final UIComparisonUnit sortTmp = (UIComparisonUnit) sortChild;
						if (!tmp.isRendered())
							sortTmp.setRendered(false);
						back = this.addItemsHelperUnit(context, writer, rdc, count, sortTmp, mapping, indent + 1,back,rowClasses);
					} else if ((UIText.class).isAssignableFrom(sortChild.getClass())) {
						final UIText sortTmp = (UIText) sortChild;
						if (!tmp.isRendered())
							sortTmp.setRendered(false);
						back = this.addItemsHelperItem(context, writer, rdc, mapping, sortTmp, indent + 1,back,rowClasses);
					}
				}
			}
		}
		return back;
	}
	private int addItemsHelperItem(final FacesContext context, final ResponseWriter writer, final UIComparisonResponseDomain rdc, final Map<UIComponent, Map<UIQuestion, UIComponent>> mapping, final UIComponent title, final int indent,final int rowIndex,final String[] rowClasses) throws IOException {
		if (context == null)
			return rowIndex;
		if (writer == null)
			return rowIndex;
		if (rdc == null)
			return rowIndex;
		if (title == null)
			return rowIndex;
		if (title.isRendered()) {
			writer.startElement("tr", rdc);
			writer.writeAttribute("class", "zo-comparison-item-row"+rowIndex, null);
			writer.startElement("td", rdc);
			String titleClasses = "zo-comparison-unit-item zo-comparison-unit-item-indent" + indent;
			if (rowClasses.length > 0) {
				titleClasses += " "+rowClasses[(rowIndex+1) % rowClasses.length];
			}
			writer.writeAttribute("class", titleClasses, null);
			title.encodeAll(context);
			writer.endElement("td");
			final Map<UIQuestion, UIComponent> map = mapping.get(title);
			for (final Map.Entry<UIQuestion, UIComponent> item : map.entrySet()) {
				final UIComponent ao = item.getValue();
				writer.startElement("td", rdc);
				String styleClass = "zo-comparison-item";
				if (rowClasses.length > 0) {
					styleClass += " "+rowClasses[(rowIndex+1) % rowClasses.length];
				}
				writer.writeAttribute("class", styleClass, null);
				if (ao != null) {
					ao.encodeAll(context);
				}
				writer.endElement("td");
			}
			writer.endElement("tr");
			return rowIndex + 1;
		}
		return rowIndex;
	}
	private int getItemCount(final UIComponent component) {
		int count = 0;
		if ((UIComparisonResponseDomain.class).isAssignableFrom(component.getClass())) {
			final UIComparisonResponseDomain rdc = (UIComparisonResponseDomain) component;
			for (final UIComponent item : rdc.getChildren()) {
				if ((UIComparisonItem.class).isAssignableFrom(item.getClass())) {
					final UIComparisonItem tmp = (UIComparisonItem) item;
					for (final UIComponent question : tmp.getChildren()) {
						if ((UIQuestion.class).isAssignableFrom(question.getClass())) {
							if(!question.isRendered())continue;
							count = count + 1;
						}
					}
				}
			}
		}
		return count;
	}
	private UIComponent getAnswerOption(final UIQuestion question, final int index) {
		if (question == null)
			return null;
		if (question.getChildCount() > 0) {
			for (final UIComponent child : question.getChildren()) {
				if ((MultipleChoiceResponseDomain.class).isAssignableFrom(child.getClass())) {
					final MultipleOption ao = (MultipleOption) child.getChildren().get(index);
					return ao;
				}
				if ((RadioButtonSingleChoiceResponseDomain.class).isAssignableFrom(child.getClass())) {
					final SingleOption ao = (SingleOption) child.getChildren().get(index);
					return ao;
				}
			}
		}
		return null;
	}
	private UIQuestion getQuestion(final UIComparisonResponseDomain component, final int index) {
		if (component == null)
			return null;
		if (component.getChildCount() > index) {
			final UIComponent item = component.getChildren().get(index);
			if ((UIComparisonItem.class).isAssignableFrom(item.getClass())) {
				for (final UIComponent child : item.getChildren()) {
					if (!child.isRendered())continue;
					if ((UIQuestion.class).isAssignableFrom(child.getClass())){
						return (UIQuestion) child;
					}
				}
			}
		}
		return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeEnd(context, component);
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
