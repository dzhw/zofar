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
package de.his.hiob.persistence.valuetype.services;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import de.his.hiob.model.common.models.PageDTO;
import de.his.hiob.model.valuetype.model.BooleanValueType;
import de.his.hiob.model.valuetype.model.NumberValueType;
import de.his.hiob.model.valuetype.model.StringValueType;
import de.his.hiob.model.valuetype.model.ValueType;
import de.his.hiob.model.valuetype.model.ValueTypeQueryDTO;
import de.his.hiob.model.valuetype.model.possiblevalues.PossibleBooleanValue;
import de.his.hiob.model.valuetype.model.possiblevalues.PossibleNumberValue;
import de.his.hiob.persistence.common.services.PersistenceService;
/**
 * this class is a mock up of the ValueTypeInternalService and must be replaced
 * by the actual implementation of the service.
 *
 * @author le
 *
 */
@Service
public class ValueTypePersistenceService extends PersistenceService {
    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ValueTypePersistenceService.class);
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.valuetype.internal.ValueTypeInternalService#searchAll
     * (de.his.hiob.service.valuetype.external.dtos.ValueTypeQueryDTO)
     */
    public PageDTO<ValueType> searchAll(final ValueTypeQueryDTO query) {
        final List<ValueType> result = new ArrayList<ValueType>();
        result.addAll(valueTypes.values());
        final PageDTO<ValueType> page = new PageDTO<ValueType>();
        page.setContent(result);
        return page;
    }
    /*
     * (non-Javadoc)
     *
     * @see de.his.hiob.service.valuetype.internal.ValueTypeInternalService#
     * loadByIdentifier(java.lang.String)
     */
    public ValueType loadByIdentifier(final String identifier) {
        if (!valueTypes.containsKey(identifier)) {
            throw new IllegalArgumentException("No value type with id: "
                    + identifier);
        }
        return valueTypes.get(identifier);
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.valuetype.internal.ValueTypeInternalService#save(
     * de.his.hiob.service.valuetype.external.dtos.ValueTypeDTO)
     */
    public ValueType save(final ValueType valueType) {
        valueTypes.put(valueType.getIdentifier(), valueType);
        return valueTypes.get(valueType.getIdentifier());
    }
    /**
     * mockup of a persistence layer aka db for the value type.
     */
    private Map<String, ValueType> valueTypes;
    /**
     * set up the fake db.
     */
    @PostConstruct
    private void init() {
        LOGGER.warn("You are using a MOCK!");
        valueTypes = new HashMap<String, ValueType>();
        createBooleanValueType();
        createDefaltNotEmptyStringValueType();
        createYesNoMaybeValueType();
        createSeasonValueType();
        createDefaultEmptyStringValueType();
        createScaleInteger1To5ValueType();
        createSimpleYesNoUnknownValueType();
        createGenderValueType();
    }
    /**
     * creates the default boolean value type.
     */
    private void createBooleanValueType() {
        final BooleanValueType valueType = new BooleanValueType();
        final String identifier = "defaultbooleantype";
        valueType.setIdentifier(identifier);
        valueType
                .setDescription("Default boolean value type. yes = true, no = false");
        valueType.addPossibleValue(createPossibleBooleanValue("yes", true));
        valueType.addPossibleValue(createPossibleBooleanValue("no", false));
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates the default cannot be empty value type for open questions.
     */
    private void createDefaltNotEmptyStringValueType() {
        final StringValueType valueType = new StringValueType();
        final String identifier = "defaultnotemptystring";
        valueType.setIdentifier(identifier);
        valueType.setDescription("The default string value type. "
                + "Max 255 characters and can not be empty.");
        valueType.setCanBeEmpty(false);
        valueType.setLength(255);
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates a value type yes, no and maybe.
     */
    private void createYesNoMaybeValueType() {
        final NumberValueType valueType = new NumberValueType();
        final String identifier = "yesnomaybe";
        valueType.setIdentifier(identifier);
        valueType.setDescription("Yes: 1, No: 2, Maybe: -97");
        valueType.addPossibleValue(createPossibleNumberValue("yes", 1, false,
                valueType));
        valueType.addPossibleValue(createPossibleNumberValue("no", 2, false,
                valueType));
        valueType.addPossibleValue(createPossibleNumberValue("maybe", -97,
                true, valueType));
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates a value type for the season (time of year).
     */
    private void createSeasonValueType() {
        final NumberValueType valueType = new NumberValueType();
        final String identifier = "seasons";
        valueType.setIdentifier(identifier);
        valueType.setDescription("Seasons (Time of year)");
        valueType.addPossibleValue(createPossibleNumberValue("spring", 1,
                false, valueType));
        valueType.addPossibleValue(createPossibleNumberValue("summer", 2,
                false, valueType));
        valueType.addPossibleValue(createPossibleNumberValue("fall", 3, false,
                valueType));
        valueType.addPossibleValue(createPossibleNumberValue("winter", 4,
                false, valueType));
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates a the default empty value type for open questions.
     */
    private void createDefaultEmptyStringValueType() {
        final StringValueType valueType = new StringValueType();
        final String identifier = "defaultemptystring";
        valueType.setIdentifier(identifier);
        valueType.setDescription("The default string value type. "
                + "Max 255 characters and can be empty.");
        valueType.setCanBeEmpty(true);
        valueType.setLength(255);
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates a scale value type with integer values between 1 and 5.
     */
    private void createScaleInteger1To5ValueType() {
        final NumberValueType valueType = new NumberValueType();
        final String identifier = "integerscale1to5";
        valueType.setIdentifier(identifier);
        valueType.setDescription("Integer scale with values between 1 and 5");
        valueType.setMinimum(Long.valueOf(1));
        valueType.setMaximum(Long.valueOf(5));
        valueType.setDecimalPlaces(0);
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates a value type for questions with yes, no or unknown values.
     */
    private void createSimpleYesNoUnknownValueType() {
        final NumberValueType valueType = new NumberValueType();
        final String identifier = "yesnounknown";
        valueType.setIdentifier(identifier);
        valueType.setDescription("Yes: 1, No: 2, Unknown: -97");
        valueType.addPossibleValue(createPossibleNumberValue("yes", 1, false,
                valueType));
        valueType.addPossibleValue(createPossibleNumberValue("no", 2, false,
                valueType));
        valueType.addPossibleValue(createPossibleNumberValue("unknown", -97,
                true, valueType));
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates a value type for questions about the gender.
     */
    private void createGenderValueType() {
        final NumberValueType valueType = new NumberValueType();
        final String identifier = "gender";
        valueType.setIdentifier(identifier);
        valueType.setDescription("Representing the qender");
        valueType.addPossibleValue(createPossibleNumberValue("female", 1,
                false, valueType));
        valueType.addPossibleValue(createPossibleNumberValue("male", 2, false,
                valueType));
        valueType.addPossibleValue(createPossibleNumberValue("unknown", -97,
                true, valueType));
        valueTypes.put(identifier, valueType);
    }
    /**
     * creates a possible number value.
     *
     * @param label
     *            the label of the possible value
     * @param value
     *            the value of the possible value
     * @param isMissing
     *            is the possible value a missing
     * @param valueType
     *            the value type that the possible value belongs to
     * @return the created possible value
     */
    private PossibleNumberValue createPossibleNumberValue(
            final String label, final Integer value, final boolean isMissing,
            final NumberValueType valueType) {
        final PossibleNumberValue possibleValue = new PossibleNumberValue();
        possibleValue.addLabel(label);
        possibleValue.setValue(Long.valueOf(value));
        possibleValue.setValueType(valueType);
        possibleValue.setIsMissing(isMissing);
        return possibleValue;
    }
    /**
     * creates a possible boolean value.
     *
     * @param label
     *            the label to set
     * @param value
     *            the value to set
     * @return the created possible value
     */
    private PossibleBooleanValue createPossibleBooleanValue(
            final String label, final Boolean value) {
        final PossibleBooleanValue possibleValue = new PossibleBooleanValue();
        possibleValue.addLabel(label);
        possibleValue.setValue(value);
        return possibleValue;
    }
}
