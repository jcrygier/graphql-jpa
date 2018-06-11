package org.crygier.graphql;

import graphql.schema.GraphQLType;

import java.util.Optional;

/**
 * (Functional) Interface to map Classes to GraphQLTypes.
 */
@FunctionalInterface
public interface AttributeMapper {

    /**
     * Returns the GraphQLType for the given Class.  If this mapper doesn't know how to handle this particular class,
     * it MUST return an empty Optional.
     *
     * @param javaType
     * @return
     */
    Optional<GraphQLType> getBasicAttributeType(Class javaType);

}
