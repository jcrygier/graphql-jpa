package org.crygier.graphql

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.persistence.EntityManager

import static graphql.Scalars.GraphQLString

@Configuration
@ContextConfiguration(loader = SpringBootContextLoader, classes = TestApplication)
class MutableSchemaBuildTest extends Specification {

    @Autowired
    private EntityManager entityManager;

    private GraphQLSchema schema;

    private final GraphQLObjectType droidMutation = GraphQLObjectType.newObject()
            .name("CreateDroidMutation")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .name("name")
                .type(GraphQLString))
            .build()

    void setup() {
        schema = new GraphQLSchemaBuilder(entityManager).mutation(droidMutation).build()
    }

    def 'Can add a schema mutation'() {
        expect:
        schema.mutationType == droidMutation
    }

}
