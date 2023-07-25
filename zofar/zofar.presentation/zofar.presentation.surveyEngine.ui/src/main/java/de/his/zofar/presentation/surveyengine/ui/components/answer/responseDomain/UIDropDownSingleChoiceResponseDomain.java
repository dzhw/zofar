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
/**
 *
 */
package de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.UIAttachedOpenQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * renders single choice options as a drop down list. missing options will be
 * rendered as radio input fields by default. the same goes for answer option
 * that contains attached open questions.
 *
 * this component only works properly if JavaScript is enabled.
 *
 * @author le
 *
 */
@FacesComponent(value = "org.zofar.DropDownSingleChoiceResponseDomain")
public class UIDropDownSingleChoiceResponseDomain extends UIInput implements
        Identificational {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(UIDropDownSingleChoiceResponseDomain.class);
    private static final String SEPARATED_ITEMS = "separated";
    private static final String CSS_CLASS_DELIM = ",";
    private final List<SingleOption> separatedOptions = new ArrayList<SingleOption>();
    private final List<String> attachedOpenQuestionIds = new ArrayList<String>();
    private String[] rowClasses;
    private boolean isInMatrix = false;
    public UIDropDownSingleChoiceResponseDomain() {
        super();
        this.setRendererType(null);
    }
    @Override
    public boolean getRendersChildren() {
        return true;
    }
    @Override
    public void decode(final FacesContext context) {
        final Map<String, String> requestMap = context.getExternalContext()
                .getRequestParameterMap();
        final String clientId = this.getClientId(context);
        Object submittedValue;
        if (this.isMissingPriority()) {
            submittedValue = requestMap.get(clientId + ":" + SEPARATED_ITEMS);
            if (submittedValue == null && requestMap.get(clientId) != null) {
                submittedValue = requestMap.get(clientId);
            }
        } else {
            submittedValue = requestMap.get(clientId);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("submitted value: {}", submittedValue);
        }
        this.setSubmittedValue(submittedValue);
    }
    @Override
    public void encodeBegin(final FacesContext context) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        this.findAttachedOpenQuestions(this.getChildren());
        if (this.isInMatrix) {
            writer.startElement("td", this);
        }
        writer.startElement("select", this);
        writer.writeAttribute("id", this.getClientId(), null);
        writer.writeAttribute("name", this.getClientId(), null);
        writer.writeAttribute("class",
                "zofar-question-singlechoice-responsedomaincombo", null);
        writer.writeAttribute("size", 1, null);
        String onchange = "deselectInputs('" + this.getClientId() + ":"
                + SEPARATED_ITEMS + "')";
        onchange += this.createClearTextField();
        writer.writeAttribute("onchange", onchange.toString(), null);
    }
    /**
     * @param onchange
     */
    private String createClearTextField() {
        final StringBuffer sb = new StringBuffer();
        for (final String id : this.attachedOpenQuestionIds) {
            sb.append(";clearTextField('").append(id).append("')");
        }
        return sb.toString();
    }
    /**
     *
     */
    private void findAttachedOpenQuestions(final List<UIComponent> children) {
        for (final UIComponent child : children) {
            if (SingleOption.class.isAssignableFrom(child.getClass())) {
                if (((SingleOption) child).hasAttachedOpenQuestion()) {
                    final UIAttachedOpenQuestion aoq = ((SingleOption) child)
                            .getAttachedOpenQuestion();
                    this.attachedOpenQuestionIds.add(aoq.getCompleteInputId());
                }
            } else if (Section.class.isAssignableFrom(child.getClass())) {
                this.findAttachedOpenQuestions(((Section) child).getContent());
            } else {
                this.findAttachedOpenQuestions(child.getChildren());
            }
        }
    }
    @Override
    public void encodeChildren(final FacesContext context) throws IOException {
        this.encodeChildrenRecursively(context, this.getChildren());
    }
    private void encodeChildrenRecursively(final FacesContext context,
            final List<UIComponent> children) throws IOException {
        for (final UIComponent child : children) {
            if (SingleOption.class.isAssignableFrom(child.getClass())) {
                this.encodeOption(context, (SingleOption) child);
            } else if (Section.class.isAssignableFrom(child.getClass())) {
                this.encodeUnit(context, (Section) child);
            } else if (UISort.class.isAssignableFrom(child.getClass())) {
                this.encodeChildrenRecursively(context,
                        ((UISort) child).sortChildren());
            } else {
                this.encodeChildrenRecursively(context, child.getChildren());
            }
        }
    }
    /**
     * @param context
     * @param unit
     */
    private void encodeUnit(final FacesContext context, final Section unit)
            throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final String unitHeader = this.getUnitHeader(unit);
        if (!unitHeader.isEmpty()) {
            writer.startElement("optgroup", this);
            writer.writeAttribute("label", this.getUnitHeader(unit), null);
        }
        this.encodeChildrenRecursively(context, unit.getContent());
        if (!unitHeader.isEmpty()) {
            writer.endElement("optgroup");
        }
    }
    public String getUnitHeader(final Section unit) {
        String sectionTitle = "";
        final UIComponent facet = unit.getFacet("header");
        if (facet != null && UIText.class.isAssignableFrom(facet.getClass())) {
            sectionTitle = JsfUtility.getInstance().evaluateValueExpression(
                    this.getFacesContext(), ((UIText) facet).getContent(),
                    String.class);
        }
        return sectionTitle;
    }
    /**
     * @param context
     * @param child
     */
    private void encodeOption(final FacesContext context,
            final SingleOption child) throws IOException {
        if (child.isMissing() && this.isMissingSeparated()) {
            this.separatedOptions.add(child);
            return;
        }
        if (child.hasAttachedOpenQuestion()) {
            this.separatedOptions.add(child);
            return;
        }
        final ResponseWriter writer = context.getResponseWriter();
        writer.startElement("option", this);
        writer.writeAttribute("value", child.getId(), null);
        if (this.getValue() != null && !((String) this.getValue()).isEmpty()
                && child.getId().endsWith((String) this.getValue())) {
            writer.writeAttribute("selected", "selected", null);
        }
        if (this.isShowValues()) {
            writer.write(child.getValue() + " ");
        }
        writer.write(JsfUtility.getInstance().evaluateValueExpression(context,
                child.getLabel(), String.class));
        writer.endElement("option");
    }
    @Override
    public void encodeEnd(final FacesContext context) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        writer.endElement("select");
        if (this.isInMatrix) {
            writer.endElement("td");
        }
        this.encodeSeparatedOptions(context);
    }
    /**
     * @param context
     */
    private void encodeSeparatedOptions(final FacesContext context)
            throws IOException {
        if (!this.separatedOptions.isEmpty()) {
            final ResponseWriter writer = context.getResponseWriter();
            if (!this.isInMatrix) {
                writer.startElement("table", this);
                writer.writeAttribute("class",
                        "zo-singlechoice-dropdown-table", null);
            }
            int rowIndex = 0;
            final String[] classes = this.getRowClazzes();
            for (final UIComponent child : this.separatedOptions) {
                if (this.isInMatrix) {
                    writer.startElement("td", this);
                } else {
                    writer.startElement("tr", this);
                    if (this.separatedOptions.size() > 1 && classes.length > 0) {
                        writer.writeAttribute(
                                "class",
                                this.getRowClazzes()[rowIndex++
                                        % this.getRowClazzes().length], null);
                    }
                }
                this.encodeRadio(context, (SingleOption) child);
                if (this.isInMatrix) {
                    writer.endElement("td");
                } else {
                    writer.endElement("tr");
                }
            }
            if (!this.isInMatrix) {
                writer.endElement("table");
            }
        }
    }
    /**
     * @param context
     * @param child
     * @throws IOException
     */
    private void encodeRadio(final FacesContext context,
            final SingleOption child) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final String id = child.getClientId();
        if (!this.isInMatrix) {
            writer.startElement("td", this);
        }
        writer.startElement("input", this);
        if (this.getValue() != null && !((String) this.getValue()).isEmpty()
                && child.getId().endsWith((String) this.getValue())) {
            writer.writeAttribute("checked", "checked", null);
        }
        writer.writeAttribute("name", this.getClientId() + ":"
                + SEPARATED_ITEMS, null);
        writer.writeAttribute("type", "radio", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("value", child.getId(), null);
        String onchange = "deselectSelect('" + this.getClientId() + "')";
        if (!child.hasAttachedOpenQuestion()) {
            onchange += this.createClearTextField();
        }
        writer.writeAttribute("onchange", onchange, null);
        writer.endElement("input");
        if (!this.isInMatrix) {
            writer.endElement("td");
            writer.startElement("td", this);
        }
        this.encodeLabel(context, child);
        if (!this.isInMatrix) {
            writer.endElement("td");
        }
    }
    private void encodeLabel(final FacesContext context,
            final SingleOption child) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        writer.startElement("label", this);
        writer.writeAttribute("for", child.getClientId(), null);
        String label = "";
        if (this.isShowValues()) {
            label += child.getValue() + " ";
        }
        label += JsfUtility.getInstance().evaluateValueExpression(context,
                child.getLabel(), String.class);
        writer.write(label);
        if (child.hasAttachedOpenQuestion()) {
            child.getAttachedOpenQuestion().encodeAll(context);
        }
        writer.endElement("label");
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.zofar.presentation.surveyengine.uicomponents.common.Identificational
     * #getUID()
     */
    @Deprecated
    @Override
    public String getUID() {
    	return this.getId();
    }
    public Boolean isMissingSeparated() {
        final Boolean missingSeparated = (Boolean) this.getAttributes().get(
                "missingSeparated");
        return missingSeparated;
    }
    /**
     * We do not want to participate in state saving.
     */
    @Override
    public boolean isTransient() {
        return true;
    }
    public Boolean isShowValues() {
        final Boolean showValues = (Boolean) this.getAttributes().get(
                "showValues");
        return showValues;
    }
    /**
     * if true and JavaScript is disabled, prioritize missing values otherwise
     * prioritize the values in the drop down list.
     *
     * @return whether to prioritize separated missing values and attached open
     *         questions or the values in the drop down list.
     */
    public Boolean isMissingPriority() {
        return true;
    }
    public String[] getRowClazzes() {
        if (this.rowClasses != null) {
            return this.rowClasses.clone();
        } else {
            String[] rowClasses = null;
            final String classes = (String) this.getAttributes().get(
                    "rowClasses");
            if (classes != null) {
                final String[] tmp = classes.split(CSS_CLASS_DELIM);
                rowClasses = new String[tmp.length];
                for (int i = 0; i < tmp.length; i++) {
                    rowClasses[i] = tmp[i].trim();
                }
            } else {
                rowClasses = new String[0];
            }
            return rowClasses;
        }
    }
    public void setIsInMatrix(final boolean b) {
        this.isInMatrix = b;
    }
}
