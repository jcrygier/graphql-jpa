package org.crygier.graphql;

import graphql.Scalars;
import graphql.schema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.*;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class GraphQLSchemaBuilder {

    public static final String PAGINATION_REQUEST_PARAM_NAME = "paginationRequest";
    private static final Logger log = LoggerFactory.getLogger(GraphQLSchemaBuilder.class);

    private EntityManager entityManager;

    public GraphQLSchemaBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public GraphQLSchema getGraphQLSchema() {
        GraphQLSchema.Builder schemaBuilder = GraphQLSchema.newSchema();
        schemaBuilder.query(getQueryType());

        return schemaBuilder.build();
    }

    private GraphQLObjectType getQueryType() {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("QueryType_JPA");
        queryType.fields(entityManager.getMetamodel().getEntities().stream().map(this::getQueryFieldDefinition).collect(Collectors.toList()));
        queryType.fields(entityManager.getMetamodel().getEntities().stream().map(this::getQueryFieldPageableDefinition).collect(Collectors.toList()));

        return queryType.build();
    }

    private GraphQLFieldDefinition getQueryFieldDefinition(EntityType<?> entityType) {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.getName())
                .type(new GraphQLList(getObjectType(entityType)))
                .dataFetcher(new JpaDataFetcher(entityManager, entityType))
                .argument(entityType.getAttributes().stream().filter(this::isValidInput).map(this::getArgument).collect(Collectors.toList()))
                .build();
    }

    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = GraphQLObjectType.newObject()
                .name(entityType.getName() + "Connection")
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalPages").type(JavaScalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("totalElements").type(JavaScalars.GraphQLLong).build())
                .field(GraphQLFieldDefinition.newFieldDefinition().name("content").type(new GraphQLList(getObjectType(entityType))).build())
                .build();

        return GraphQLFieldDefinition.newFieldDefinition()
                .name(entityType.getName() + "Connection")
                .type(pageType)
                .dataFetcher(new ExtendedJpaDataFetcher(entityManager, entityType))
                .argument(paginationArgument)
                .build();
    }

    private GraphQLArgument getArgument(Attribute attribute) {
        GraphQLType type = getAttributeType(attribute);

        if (type instanceof GraphQLInputType) {
            return GraphQLArgument.newArgument()
                    .name(attribute.getName())
                    .type((GraphQLInputType) type)
                    .build();
        }

        throw new IllegalArgumentException("Attribute " + attribute + " cannot be mapped as an Input Argument");
    }

    private GraphQLObjectType getObjectType(EntityType<?> entityType) {
        return GraphQLObjectType.newObject()
                .name(entityType.getName())
                .fields(entityType.getAttributes().stream().map(this::getObjectField).collect(Collectors.toList()))
                .build();
    }

    private GraphQLFieldDefinition getObjectField(Attribute attribute) {
        GraphQLType type = getAttributeType(attribute);

        if (type instanceof GraphQLOutputType) {
            return GraphQLFieldDefinition.newFieldDefinition()
                    .name(attribute.getName())
                    .type((GraphQLOutputType) type)
                    .argument(GraphQLArgument.newArgument().name("orderBy").type(orderByDirectionEnum).build())
                    .build();
        }

        throw new IllegalArgumentException("Attribute " + attribute + " cannot be mapped as an Output Argument");
    }

    private GraphQLType getAttributeType(Attribute attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            if (String.class.isAssignableFrom(attribute.getJavaType()))
                return Scalars.GraphQLString;
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(new GraphQLTypeReference(foreignType.getName()));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(foreignType.getName());
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION) {
            Type foreignType = ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(getTypeFromJavaType(foreignType.getJavaType()));
        }

        throw new UnsupportedOperationException("Attribute could not be mapped to GraphQL: " + attribute);
    }

    private boolean isValidInput(Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC;
    }

    private GraphQLType getTypeFromJavaType(Class clazz) {
        if (clazz.isEnum()) {
            GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum().name(clazz.getSimpleName());
            int ordinal = 0;
            for (Enum enumValue : ((Class<Enum>)clazz).getEnumConstants())
                enumBuilder.value(enumValue.name(), ordinal++);

            GraphQLType answer = enumBuilder.build();
            setIdentityCoercing(answer);

            return answer;
        }

        return null;
    }

    /**
     * A bit of a hack, since JPA will deserialize our Enum's for us...we don't want GraphQL doing it.
     *
     * @param type
     */
    private void setIdentityCoercing(GraphQLType type) {
        try {
            Field coercing = type.getClass().getDeclaredField("coercing");
            coercing.setAccessible(true);
            coercing.set(type, new IdentityCoercing());
        } catch (Exception e) {
            log.error("Unable to set coercing for " + type, e);
        }
    }

    private static final GraphQLArgument paginationArgument =
            GraphQLArgument.newArgument()
                    .name(PAGINATION_REQUEST_PARAM_NAME)
                    .type(GraphQLInputObjectType.newInputObject()
                                    .name("PaginationObject")
                                    .field(GraphQLInputObjectField.newInputObjectField().name("page").type(Scalars.GraphQLInt).build())
                                    .field(GraphQLInputObjectField.newInputObjectField().name("size").type(Scalars.GraphQLInt).build())
                                    .build()
                    ).build();

    private static final GraphQLEnumType orderByDirectionEnum =
            GraphQLEnumType.newEnum()
                    .name("OrderByDirection")
                    .value("ASC", 0, "Ascending")
                    .value("DESC", 1, "Descending")
                    .build();


}
