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
package de.his.zofar.service.common.mapper;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;
import de.his.zofar.service.common.mapper.annotations.DTOEntityMapping;
import de.his.zofar.service.common.mapper.annotations.UniDirectionalMapping;
/**
 * The DTOMappingBuilder spring bean which scans for all classes annotated with
 * {@link DTOEntityMapping} or {@link UniDirectionalMapping} in the given
 * basePackage.
 * For each concrete class annotated with {@link DTOEntityMapping} it adds a
 * bidirectional mapping to the dozer mapper. Abstract classes are skipped in
 * order to be able to leverage polymorphism in dozer.
 *
 * For each concrete class annotated with {@link UniDirectionalMapping} it adds a
 * unidirectional mapping to the dozer mapper including a factory bean if
 * desired.
 *
 * The logic is implemented in the {@link MappingBuilder} pojo.
 *
 * @author Reitmann
 */
public class DTOMappingBuilder {
    @Inject
    private Mapper mapper;
    private String dtoBasePackage;
    @PostConstruct
    private void addDefaultMappings() {
        final BeanMappingBuilder builder = new MappingBuilder(
                this.dtoBasePackage);
        ((DozerBeanMapper) this.mapper).addMapping(builder);
    }
    /**
     * @param dtoBasePackage
     *            The basePackage to be scanned. Wildcards are allowed!
     */
    public void setDtoBasePackage(final String dtoBasePackage) {
        this.dtoBasePackage = dtoBasePackage;
    }
}
