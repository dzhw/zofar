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
package de.his.zofar.generator.maven.plugin.generator.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.xml.questionnaire.CalendarType;
import de.his.zofar.xml.questionnaire.ComparisonType;
import de.his.zofar.xml.questionnaire.DisplayType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.MatrixDoubleType;
import de.his.zofar.xml.questionnaire.MatrixMultipleChoiceType;
import de.his.zofar.xml.questionnaire.MatrixQuestionMixedType;
import de.his.zofar.xml.questionnaire.MatrixQuestionOpenType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.MultipleChoiceType;
import de.his.zofar.xml.questionnaire.QuestionOpenType;
import de.his.zofar.xml.questionnaire.QuestionPretestType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.SectionType;

/**
 * Factory for all XHTML creator classes.
 * 
 * @author le
 * 
 */
final class CreatorFactory {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreatorFactory.class);

	private CreatorFactory() {
		super();
	}

	/**
	 * Returns the creator instance responsible for the given
	 * {@link IdentificationalType}. Returns null if there is no creator for the
	 * given type.
	 * 
	 * @param type
	 *            the type for which the creator instance is responsible for
	 * @return
	 */
	synchronized static IXhtmlCreator newCreator(final IdentificationalType type) {
		final Class<? extends IdentificationalType> typeClass = type.getClass();

		IXhtmlCreator creator = null;

		if (QuestionOpenType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlQuestionOpenCreator();
		}
		else if (QuestionPretestType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlQuestionPretestCreator();
		} else if (QuestionSingleChoiceType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlQuestionSingleChoiceCreator();
		} else if (MultipleChoiceType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlMultipleChoiceCreator();
		} else if (MatrixQuestionOpenType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlMatrixOpenCreator();
		} else if (MatrixQuestionSingleChoiceType.class
				.isAssignableFrom(typeClass)) {
			creator = new XhtmlMatrixSingleChoiceCreator();
		} else if (MatrixMultipleChoiceType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlMatrixMultipleChoiceCreator();
		} else if (MatrixDoubleType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlMatrixDoubleSingleChoiceCreator();
		} else if (MatrixQuestionMixedType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlMatrixQuestionMixedCreator();
		} else if (ComparisonType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlComparisonQuestionCreator();
		}else if (SectionType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlSectionCreator();
		}
		else if (DisplayType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlDisplayCreator();
		}
		else if (CalendarType.class.isAssignableFrom(typeClass)) {
			creator = new XhtmlCalendarCreator();
		}
		else{
			LOGGER.info("Unkown Class Type : {}",typeClass.getName());
		}

		return creator;
	}

}
