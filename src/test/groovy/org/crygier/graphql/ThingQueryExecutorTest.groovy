package org.crygier.graphql

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@Configuration
@ContextConfiguration(loader = SpringBootContextLoader, classes = TestApplication)
class ThingQueryExecutorTest extends Specification {

    @Autowired
    private GraphQLExecutor executor

    def 'Gets all things'() {
        given:
        def query = '''
        query AllThingsQuery {
          Thing {
            id
            type
          }
        }
        '''
        def expected = [
                Thing: [
                   [ id: UUID.fromString("2d1ebc5b-7d27-4197-9cf0-e84451c5bbb1"), type:'Thing1' ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Query for thing by id'() {
        given:
        def query = '''
        query ThingByIdQuery {
          Thing(id: "2d1ebc5b-7d27-4197-9cf0-e84451c5bbb1") {
            id
            type
          }
        }
        '''
        def expected = [
                Thing: [
                        [ id: UUID.fromString("2d1ebc5b-7d27-4197-9cf0-e84451c5bbb1"), type:'Thing1' ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Query with parameter'() {
        given:
        def query = '''
       query ThingByIdQuery($id: UUID) {
          Thing(id: $id) {
            id
            type
          }
        }
        '''
        def expected = [
                Thing: [
                        [ id: UUID.fromString("2d1ebc5b-7d27-4197-9cf0-e84451c5bbb1"), type:'Thing1' ]
                ]
        ]

        when:
        def result = executor.execute(query, [id: "2d1ebc5b-7d27-4197-9cf0-e84451c5bbb1"]).data

        then:
        result == expected
    }

    def 'Query with alias'() {
        given:
        def query = '''
        {
         t1:  Thing(id: "2d1ebc5b-7d27-4197-9cf0-e84451c5bbb1") {
            id
            type
          }
        }
        '''
        def expected = [
                t1: [
                        [ id: UUID.fromString("2d1ebc5b-7d27-4197-9cf0-e84451c5bbb1"), type:'Thing1' ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

}
