package org.crygier.graphql

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static graphql.Scalars.GraphQLString

@Configuration
@ContextConfiguration(loader = SpringBootContextLoader, classes = TestApplication)
class MutableQueryExecutorTest extends Specification {

    @Autowired
    private GraphQLExecutor executor;

    private final GraphQLObjectType droidMutation = GraphQLObjectType.newObject()
            .name("CreateDroidMutation")
            .field(GraphQLFieldDefinition.newFieldDefinition()
            .name("name")
            .type(GraphQLString))
            .build()

    def 'Can add a schema mutation'() {
        when:
        GraphQLSchema.Builder builder = executor.getBuilder().mutation(droidMutation)
        executor.updateSchema(builder)

        then:
        executor.getSchema().mutationType == droidMutation
    }

}
