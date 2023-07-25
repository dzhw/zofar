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
package de.his.zofar.generator.maven.plugin.generator.transition;
import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import com.sun.java.xml.ns.javaee.FacesConfigApplicationResourceBundleType;
import com.sun.java.xml.ns.javaee.FacesConfigApplicationType;
import com.sun.java.xml.ns.javaee.FacesConfigDocument;
import com.sun.java.xml.ns.javaee.FacesConfigFactoryType;
import com.sun.java.xml.ns.javaee.FacesConfigFromViewIdType;
import com.sun.java.xml.ns.javaee.FacesConfigIfType;
import com.sun.java.xml.ns.javaee.FacesConfigLifecycleType;
import com.sun.java.xml.ns.javaee.FacesConfigNavigationCaseType;
import com.sun.java.xml.ns.javaee.FacesConfigNavigationRuleType;
import com.sun.java.xml.ns.javaee.FacesConfigType;
import com.sun.java.xml.ns.javaee.FacesConfigVersionType;
import com.sun.java.xml.ns.javaee.FullyQualifiedClassType;
import de.his.zofar.generator.maven.plugin.generator.AbstractGenerator;
/**
 * this class provides methods to create transitions of a zofar survey.
 *
 * @author le
 *
 */
public class TransitionGenerator extends AbstractGenerator<FacesConfigDocument> {
    private static final String FILE_NAME = "faces-config.xml";
    private static final String SPRING_BEAN_FACES_RESOLVER = "org.springframework.web.jsf.el.SpringBeanFacesELResolver";
    private static final String SPRING_DELEGATING_PHASE_LISTENER = "de.his.zofar.presentation.surveyengine.listener.CustomDelegatingPhaseListenerMulticaster";
    private static final String ZOFAR_CACHE_LISTENER = "de.his.zofar.presentation.surveyengine.listener.CacheListener";
    private static final String ZOFAR_SECURITY_LISTENER = "de.his.zofar.presentation.surveyengine.listener.RequestSecurityListener";
    private static final String ZOFAR_EXCEPTION_HANDLER_FACTORY = "de.his.zofar.presentation.surveyengine.listener.errorHandling.ZofarExceptionHandlerFactory";
    private static final String ZOFAR_CONSTANTS_LAYOUT_NAME = "layoutbundle";
    private static final String ZOFAR_CONSTANTS_LAYOUT_CLASS = "de.his.zofar.constants.Layout";
    private static final String ZOFAR_CONSTANTS_SURVEY_NAME = "surveyConstants";
    private static final String ZOFAR_CONSTANTS_SURVEY_CLASS = "de.his.zofar.constants.Survey";
    private static final String ZOFAR_MESSAGES_NAME = "msgbundle";
    private static final String ZOFAR_MESSAGES_CLASS = "de.his.zofar.messages.text";
    private static final String ZOFAR_TO_VIEW_ID = "navigatorBean.sendViewID('/%s.xhtml')";
    private static final String ZOFAR_FROM_VIEW_ID = "/%s.xhtml";
    private static final String ZOFAR_BACKWARD = "navigatorBean.backwardViewID";
    private static final String ZOFAR_SAME = "navigatorBean.sameViewID";
    private static final String ZOFAR_JUMP = "jumperBean.target";
    private static final String ZOFAR_DEFAULT_OUTCOME = "next";
    private static final String ZOFAR_BACKWARD_OUTCOME = "backward";
    private static final String ZOFAR_SAME_OUTCOME = "same";
    private static final String ZOFAR_JUMP_OUTCOME = "jump";
    private static final String JSF_EL_EXPRESSION = "#{%s}";
    /**
     * implement the singleton pattern.
     */
    public TransitionGenerator() {
        super(FacesConfigDocument.Factory.newInstance());
    }
    /**
     * creates a new faces-config.xml document.
     *
     * @return the created document
     */
    public final void createDocument() {
        final FacesConfigType facesConfig = this.getDocument()
                .addNewFacesConfig();
        final XmlCursor cursor = this.getDocument().newCursor();
        if (cursor.toFirstChild()) {
            cursor.setAttributeText(
                    new QName("http://www.w3.org/2001/XMLSchema-instance",
                            "schemaLocation"),
                    "http://java.sun.com/xml/ns/javaee "
                            + "http://java.sun.com/xml/ns/javaee/web-facesconfig_2_1.xsd");
        }
        facesConfig.setVersion(FacesConfigVersionType.X_2_1);
        final FacesConfigApplicationType application = facesConfig
                .addNewApplication();
        final FullyQualifiedClassType springBeanFacesResolver = application
                .addNewElResolver();
        springBeanFacesResolver.setStringValue(SPRING_BEAN_FACES_RESOLVER);
        final FacesConfigApplicationResourceBundleType resourceBundle_Layout = application
                .addNewResourceBundle();
        final FullyQualifiedClassType baseName = resourceBundle_Layout
                .addNewBaseName();
        baseName.setStringValue(ZOFAR_CONSTANTS_LAYOUT_CLASS);
        final com.sun.java.xml.ns.javaee.String name = resourceBundle_Layout
                .addNewVar();
        name.setStringValue(ZOFAR_CONSTANTS_LAYOUT_NAME);
        final FacesConfigApplicationResourceBundleType resourceBundle_Survey = application
                .addNewResourceBundle();
        final FullyQualifiedClassType surveybaseName = resourceBundle_Survey
                .addNewBaseName();
        surveybaseName.setStringValue(ZOFAR_CONSTANTS_SURVEY_CLASS);
        final com.sun.java.xml.ns.javaee.String surveyname = resourceBundle_Survey
                .addNewVar();
        surveyname.setStringValue(ZOFAR_CONSTANTS_SURVEY_NAME);
        final FacesConfigApplicationResourceBundleType resourceBundle = application
                .addNewResourceBundle();
        final FullyQualifiedClassType resourceBundleBaseName = resourceBundle
                .addNewBaseName();
        resourceBundleBaseName.setStringValue(ZOFAR_MESSAGES_CLASS);
        final com.sun.java.xml.ns.javaee.String resourceBundleName = resourceBundle
                .addNewVar();
        resourceBundleName.setStringValue(ZOFAR_MESSAGES_NAME);
        final FacesConfigLifecycleType lifecycle = facesConfig
                .addNewLifecycle();
        final FullyQualifiedClassType delegatingListener = lifecycle
                .addNewPhaseListener();
        delegatingListener.setStringValue(SPRING_DELEGATING_PHASE_LISTENER);
        final FullyQualifiedClassType nocacheListener = lifecycle
                .addNewPhaseListener();
        nocacheListener.setId("nocache");
        nocacheListener.setStringValue(ZOFAR_CACHE_LISTENER);
        final FullyQualifiedClassType sessionSecurityListener = lifecycle
                .addNewPhaseListener();
        sessionSecurityListener.setId("sessionsecurity");
        sessionSecurityListener.setStringValue(ZOFAR_SECURITY_LISTENER);
        final FacesConfigFactoryType factory = facesConfig.addNewFactory();
        final FullyQualifiedClassType zofarExceptionHandlerFactory = factory
                .addNewExceptionHandlerFactory();
        zofarExceptionHandlerFactory
                .setStringValue(ZOFAR_EXCEPTION_HANDLER_FACTORY);
        final FacesConfigNavigationCaseType jump = this.createNavigationCase(
                ZOFAR_JUMP_OUTCOME, null,
                String.format(JSF_EL_EXPRESSION, ZOFAR_JUMP));
        this.addNavigationRule(null, jump);
        final FacesConfigNavigationCaseType backward = this
                .createNavigationCase(ZOFAR_BACKWARD_OUTCOME, null,
                        String.format(JSF_EL_EXPRESSION, ZOFAR_BACKWARD));
        final FacesConfigNavigationCaseType same = this.createNavigationCase(
                ZOFAR_SAME_OUTCOME, null,
                String.format(JSF_EL_EXPRESSION, ZOFAR_SAME));
        final FacesConfigNavigationCaseType onLoadError = this
                .createNavigationCase("constraintsOnLoadError", null,
                        "/error/constraints1.xhtml");
        final FacesConfigNavigationCaseType onExitError = this
                .createNavigationCase("constraintsOnExitError", null,
                        "/error/constraints2.xhtml");
        this.addNavigationRule("*", backward, same, onLoadError, onExitError);
    }
    /**
     * saves the document in the path as faces-config.xml.
     *
     * @param path
     *            the path in which the document will be saved. file name is
     *            'faces-config.xml'
     * @throws IOException
     */
    public final void saveDocument(final String path) throws IOException {
        final XmlOptions options = new XmlOptions();
        options.setSavePrettyPrint();
        options.setUseDefaultNamespace();
        this.saveDocument(path, FILE_NAME, options);
    }
    /**
     * adds a navigation rule to the faces-config.xml document.
     *
     * @param fromViewId
     * @param cases
     *            the navigation cases which will be added to the navigation
     *            rule
     */
    public final void addNavigationRule(final String fromViewId,
            final FacesConfigNavigationCaseType... cases) {
        final FacesConfigNavigationRuleType rule = this.getDocument()
                .getFacesConfig().addNewNavigationRule();
        if(fromViewId != null){
            final FacesConfigFromViewIdType fromViewIdType = rule
                    .addNewFromViewId();
            fromViewIdType.setStringValue(String.format(ZOFAR_FROM_VIEW_ID,
                    fromViewId));
        }
        rule.setNavigationCaseArray(cases);
    }
    /**
     * creates on navigation case to be used as parameter for the
     * addNavigationRule() method.
     *
     * Convenient method.
     *
     * @param condition
     *            the condition whether redirect the participant to next page or
     *            not. can be null
     * @param next
     *            the next page to which the participant will be redirected
     * @return the configured navigation case
     */
    public final FacesConfigNavigationCaseType createNavigationCase(
            final String condition, final String next) {
        return this.createNavigationCase(
                ZOFAR_DEFAULT_OUTCOME,
                condition,
                String.format(JSF_EL_EXPRESSION,
                        String.format(ZOFAR_TO_VIEW_ID, next)));
    }
    /**
     * creates on navigation case to be used as parameter for the
     * addNavigationRule() method.
     *
     * @param outcome
     * @param condition
     *            the condition whether redirect the participant to next page or
     *            not. can be null
     * @param next
     *            the next page
     * @return the configured navigation case
     */
    private FacesConfigNavigationCaseType createNavigationCase(
            final String outcome, final String condition, final String next) {
        final FacesConfigNavigationCaseType navigationCase = FacesConfigNavigationCaseType.Factory
                .newInstance();
        final com.sun.java.xml.ns.javaee.String outcomeType = navigationCase
                .addNewFromOutcome();
        outcomeType.setStringValue(outcome);
        if (condition != null && !condition.isEmpty()) {
            final FacesConfigIfType ifType = navigationCase.addNewIf();
            ifType.setStringValue(String.format(JSF_EL_EXPRESSION, condition));
        }
        navigationCase.setToViewId(next);
        navigationCase.addNewRedirect();
        return navigationCase;
    }
}
