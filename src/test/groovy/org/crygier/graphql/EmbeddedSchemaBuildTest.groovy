package org.crygier.graphql

import graphql.schema.GraphQLObjectType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.metamodel.EntityType

@Configuration
@ContextConfiguration(loader = SpringBootContextLoader, classes = TestApplication)
class EmbeddedSchemaBuildTest extends Specification {

    @Autowired
    private EntityManager entityManager;

    private GraphQLSchemaBuilder builder;

    void setup() {
        builder = new GraphQLSchemaBuilder(entityManager);
    }

    def 'Correctly flattens embedded keys into two distinct fields'() {
        when:
        def embeddingEntity = entityManager.getMetamodel().getEntities().stream().filter { e -> e.name == "EmbeddingTest"}.findFirst().get()
        def graphQlObject = builder.getObjectType(embeddingEntity)

        then:
        graphQlObject.fieldDefinitions.size() == 2
    }

}
