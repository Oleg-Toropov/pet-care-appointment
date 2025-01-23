package com.olegtoropoff.petcareappointment.dto;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * A utility class for converting entities to their corresponding DTO (Data Transfer Object) representations.
 * This class utilizes {@link ModelMapper} to perform object mapping between entities and DTOs.
 *
 * @param <T> the type of the entity to be converted.
 * @param <D> the type of the DTO to map the entity to.
 */
@Component
@RequiredArgsConstructor
public class EntityConverter<T, D> {

    /**
     * Instance of {@link ModelMapper} used for mapping entities to DTOs.
     */
    private final ModelMapper modelMapper;

    /**
     * Converts an entity to its corresponding DTO representation.
     *
     * @param entity   the entity to be converted.
     * @param dtoClass the class of the DTO to map the entity to.
     * @return the DTO representation of the given entity.
     */
    public D mapEntityToDto(T entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }
}
