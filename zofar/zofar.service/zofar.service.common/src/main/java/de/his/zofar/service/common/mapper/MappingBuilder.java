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
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import de.his.zofar.service.common.mapper.annotations.DTOEntityMapping;
import de.his.zofar.service.common.mapper.annotations.UniDirectionalMapping;
/**
 * The MappingBuilder spring bean which scans for all classes annotated with
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
 * @author Reitmann
 */
public class MappingBuilder extends BeanMappingBuilder {
    private static Logger LOGGER = LoggerFactory
            .getLogger(MappingBuilder.class);
    private final String basePackage;
    public MappingBuilder(final String basePackage) {
        this.basePackage = basePackage;
    }
    private void addBiDirectionalMappings() throws ClassNotFoundException {
        final List<Class<?>> classes = findAllAnnotatedClasses(
                this.basePackage, DTOEntityMapping.class, true, true, true);
        for (final Class<?> clazz : classes) {
            final Class<?> dest = getEntity(clazz);
            LOGGER.info("Adding bidirectional mapping for {} <-> {}", clazz,
                    dest);
            mapping(clazz, dest);
        }
    }
    private void addUniDirectionalMappings() throws ClassNotFoundException {
        final List<Class<?>> classes = findAllAnnotatedClasses(
                this.basePackage, UniDirectionalMapping.class, true, true, false);
        for (final Class<?> clazz : classes) {
            final Class<?> source = getSource(clazz);
            final Class<?> destination = getDestination(clazz);
            if (source == null ^ destination == null) {
                if (source == null) {
                    final Class<?> factory = getDestinationBeanFactory(clazz);
                    if (factory != null) {
                        LOGGER.info(
                                "Adding unidirectional mapping for {} -> {}",
                                clazz, destination);
                        mapping(clazz, destination,
                                TypeMappingOptions.oneWay(),
                                TypeMappingOptions.beanFactory(factory
                                        .toString()));
                    } else {
                        ensureConcreteDestination(destination);
                        LOGGER.info(
                                "Adding unidirectional mapping for {} -> {}",
                                clazz, destination);
                        mapping(clazz, destination, TypeMappingOptions.oneWay());
                    }
                } else {
                    ensureConcreteDestination(clazz);
                    LOGGER.info("Adding unidirectional mapping for {} -> {}",
                            source, clazz);
                    mapping(source, clazz, TypeMappingOptions.oneWay());
                }
            } else {
                throw new RuntimeException(
                        "Either source xor destination must be set!");
            }
        }
    }
    /*
     * (non-Javadoc)
     *
     * @see org.dozer.loader.api.BeanMappingBuilder#configure()
     */
    @Override
    protected void configure() {
        try {
            addBiDirectionalMappings();
            addUniDirectionalMappings();
        } catch (final Exception e) {
            LOGGER.error("Unable to create mappings!", e);
            throw new RuntimeException(e);
        }
    }
    private void ensureConcreteDestination(final Class<?> destination) {
        if (Modifier.isAbstract(destination.getModifiers())) {
            throw new RuntimeException(
                    "Destination class in mapping must not be abstract!");
        }
    }
    private List<Class<?>> findAllAnnotatedClasses(final String basePackage,
            final Class<? extends Annotation> annotation,
            final boolean skipEnums, final boolean skipInterfaces,
            final boolean skipAbstract) throws ClassNotFoundException {
        LOGGER.debug("Start scanning classpath for classes annotated with @{}",
                annotation.getSimpleName());
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        final Set<BeanDefinition> components = provider
                .findCandidateComponents(basePackage);
        final List<Class<?>> classes = new ArrayList<Class<?>>(
                components.size());
        for (final BeanDefinition component : components) {
            final Class<?> clazz = Class.forName(component.getBeanClassName());
            if ((skipInterfaces && clazz.isInterface())
                    || (skipAbstract && Modifier.isAbstract(clazz
                            .getModifiers())) || (skipEnums && clazz.isEnum())) {
                LOGGER.debug("Skipping {}", clazz.toString());
            } else {
                LOGGER.debug("Found class {}", clazz.toString());
                classes.add(clazz);
            }
        }
        LOGGER.debug(
                "Finished scanning classpath for classes annotated with @{}",
                annotation.getSimpleName());
        return classes;
    }
    private Class<?> getDestination(final Class<?> clazz) {
        final UniDirectionalMapping map = clazz
                .getAnnotation(UniDirectionalMapping.class);
        if (map.destination().equals(void.class)) {
            return null;
        }
        return map.destination();
    }
    private Class<?> getDestinationBeanFactory(final Class<?> clazz) {
        final UniDirectionalMapping map = clazz
                .getAnnotation(UniDirectionalMapping.class);
        if (map.destinationFactory().equals(
                UniDirectionalMapping.DummyFactory.class)) {
            return null;
        }
        return map.destinationFactory();
    }
    private Class<?> getEntity(final Class<?> clazz) {
        final DTOEntityMapping map = clazz
                .getAnnotation(DTOEntityMapping.class);
        return map.entity();
    }
    private Class<?> getSource(final Class<?> clazz) {
        final UniDirectionalMapping map = clazz
                .getAnnotation(UniDirectionalMapping.class);
        if (map.source().equals(void.class)) {
            return null;
        }
        return map.source();
    }
}
