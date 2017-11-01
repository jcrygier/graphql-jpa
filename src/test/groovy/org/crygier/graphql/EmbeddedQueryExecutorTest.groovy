package org.crygier.graphql

import javax.persistence.EntityManager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

import spock.lang.Ignore
import spock.lang.Specification

@Configuration
@ContextConfiguration(loader = SpringBootContextLoader, classes = TestApplication)
class EmbeddedQueryExecutorTest extends Specification {

	@Autowired
    private GraphQLExecutor executor;
	
	def 'Query Embedded Values'() {
		given:
		def query = '''
        {
            Spaceship (id: "1000"){
                name, created { user {id}}, modified {date}
            }
        }
        '''
		def expected = [
				Spaceship: [[name: "X-Wing", created:[user:[id:"1000"]], modified:null]]
		]

		when:
		def result = executor.execute(query).data

		then:
		result == expected
	}
}
