package org.crygier.graphql

import org.crygier.graphql.model.starwars.Episode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager

@Configuration
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
class StarwarsQueryExecutorTest extends Specification {

    @Autowired
    private GraphQLExecutor executor;

    def 'Gets just the names of all droids'() {
        given:
        def query = '''
        query HeroNameQuery {
          Droid {
            name
          }
        }
        '''
        def expected = [
                Droid: [
                        [ name: 'C-3PO' ],
                        [ name: 'R2-D2' ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Query for droid by name'() {
        given:
        def query = '''
        {
          Droid(name: "C-3PO") {
            name
            primaryFunction
          }
        }
        '''
        def expected = [
                Droid: [
                        [ name: 'C-3PO', primaryFunction: 'Protocol' ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'ManyToOne Join by ID'() {
        given:
        def query = '''
        {
            Human(id: "1000") {
                name
                homePlanet
                favoriteDroid {
                    name
                }
            }
        }
        '''
        def expected = [
                Human: [
                        [name:'Luke Skywalker', homePlanet:'Tatooine', favoriteDroid:[name:'C-3PO']]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'OneToMany Join by ID'() {
        given:
        def query = '''
        {
            Human(id: "1000") {
                name
                homePlanet
                friends {
                    name
                }
            }
        }
        '''
        def expected = [
                Human: [
                        [name: 'Luke Skywalker', homePlanet: 'Tatooine', friends: [[name: 'Han Solo'], [name: 'Leia Organa'], [name: 'C-3PO'], [name: 'R2-D2']]]
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
        query humanQuery($id: String!) {
            Human(id: $id) {
                name
                homePlanet
            }
        }
        '''
        def expected = [
                Human: [
                        [name: 'Darth Vader', homePlanet: 'Tatooine']
                ]
        ]

        when:
        def result = executor.execute(query, [id: "1001"]).data

        then:
        result == expected
    }

    def 'Query with alias'() {
        given:
        def query = '''
        {
            luke: Human(id: "1000") {
                name
                homePlanet
            }
            leia: Human(id: "1003") {
                name
            }
        }
        '''
        def expected = [
                luke: [
                        [name: 'Luke Skywalker', homePlanet: 'Tatooine'],
                ],
                leia: [
                        [name: 'Leia Organa']
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Allows us to use a fragment to avoid duplicating content'() {
        given:
        def query = """
        query UseFragment {
            luke: Human(id: "1000") {
                ...HumanFragment
            }
            leia: Human(id: "1003") {
                ...HumanFragment
            }
        }
        fragment HumanFragment on Human {
            name
            homePlanet
        }
        """
        def expected = [
                luke: [
                        [name: 'Luke Skywalker', homePlanet: 'Tatooine'],
                ],
                leia: [
                        [name: 'Leia Organa', homePlanet: 'Alderaan']
                ]
        ]
        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Deep nesting'() {
        given:
        def query = '''
        {
            Droid(id: "2001") {
                name
                friends {
                    name
                    appearsIn
                    friends {
                        name
                    }
                }
            }
        }
        '''
        def expected = [
                Droid:[
                        [
                                name:'R2-D2',
                                friends:[
                                        [ name:'Luke Skywalker', appearsIn:['A_NEW_HOPE', 'EMPIRE_STRIKES_BACK', 'RETURN_OF_THE_JEDI', 'THE_FORCE_AWAKENS'], friends:[['name:Han Solo'], [name:'Leia Organa'], [name:'C-3PO'], [name:'R2-D2']]],
                                        [ name:'Han Solo', appearsIn:['A_NEW_HOPE', 'EMPIRE_STRIKES_BACK', 'RETURN_OF_THE_JEDI', 'THE_FORCE_AWAKENS'], friends:[[name:'Luke Skywalker'], [name:'Leia Organa'], [name:'R2-D2']]],
                                        [ name:'Leia Organa', appearsIn:['A_NEW_HOPE', 'EMPIRE_STRIKES_BACK', 'RETURN_OF_THE_JEDI', 'THE_FORCE_AWAKENS'], friends:[[name:'Luke Skywalker'], [name:'Han Solo'], [name:'C-3PO'], [name:'R2-D2']]]
                                ]
                        ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result.toString() == expected.toString()
    }

    def 'Pagination at the root'() {
        given:
        def query = '''
        {
            HumanConnection(paginationRequest: { page: 1, size: 2 }) {
                totalPages
                totalElements
                content {
                    name
                }
            }
        }
        '''
        def expected = [
                HumanConnection: [
                        totalPages: 3,
                        totalElements: 5,
                        content: [
                                [ name: 'Darth Vader' ],
                                [ name: 'Luke Skywalker' ]
                        ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Pagination without content'() {
        given:
        def query = '''
        {
            HumanConnection(paginationRequest: { page: 1, size: 2}) {
                totalPages
                totalElements
            }
        }
        '''

        def expected = [
                HumanConnection: [
                        totalPages: 3,
                        totalElements: 5
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Ordering Fields'() {
        given:
        def query = '''
        {
            Human {
                name(orderBy: DESC)
                homePlanet
            }
        }
        '''
        def expected = [
                Human: [
                    [ name: 'Wilhuff Tarkin', homePlanet: null],
                    [ name: 'Luke Skywalker', homePlanet: "Tatooine"],
                    [ name: 'Leia Organa', homePlanet: "Alderaan"],
                    [ name: 'Han Solo', homePlanet: null],
                    [ name: 'Darth Vader', homePlanet: "Tatooine"]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected
    }

    def 'Query by Collection of Enums at root level'() {
        // Semi-proper JPA: select distinct h from Human h join h.appearsIn ai where ai in (:episodes)

        given:
        def query = '''
        {
          Human(appearsIn: [THE_FORCE_AWAKENS]) {
            name
            appearsIn
          }
        }
        '''
        def expected = [
                Human: [
                    [ name: 'Leia Organa', appearsIn: [Episode.A_NEW_HOPE, Episode.EMPIRE_STRIKES_BACK, Episode.RETURN_OF_THE_JEDI, Episode.THE_FORCE_AWAKENS] ],
                    [ name: 'Luke Skywalker', appearsIn: [Episode.A_NEW_HOPE, Episode.EMPIRE_STRIKES_BACK, Episode.RETURN_OF_THE_JEDI, Episode.THE_FORCE_AWAKENS]],
                    [ name: 'Han Solo', appearsIn: [Episode.A_NEW_HOPE, Episode.EMPIRE_STRIKES_BACK, Episode.RETURN_OF_THE_JEDI, Episode.THE_FORCE_AWAKENS] ]
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected;
    }

    def 'Query by restricting sub-object'() {
        given:
        def query = '''
        {
          Human {
            name
            gender(code: "Male") {
              description
            }
          }
        }
        '''
        def expected = [
                Human: [
                        [ name: 'Darth Vader', gender: [ description: "Male" ] ],
                        [ name: 'Wilhuff Tarkin', gender: [ description: "Male" ] ],
                        [ name: 'Han Solo', gender: [ description: "Male" ] ],
                        [ name: 'Luke Skywalker', gender: [ description: "Male" ]],
                        [ name: 'Leia Organa', gender: [ description: 'Female' ]]                     // TODO: Why is this coming back?
                ]
        ]

        when:
        def result = executor.execute(query).data

        then:
        result == expected;
    }

    @Autowired
    private EntityManager em;

    @Transactional
    def 'JPA Sample Tester'() {
        when:
        //def query = em.createQuery("select h.id, h.name, h.gender.description from Human h where h.gender.code = 'Male'");
        def query = em.createQuery("select h, h.friends from Human h");
        //query.setParameter(1, Episode.THE_FORCE_AWAKENS);
        //query.setParameter("episodes", EnumSet.of(Episode.THE_FORCE_AWAKENS));
        def result = query.getResultList();
        //println JsonOutput.prettyPrint(JsonOutput.toJson(result));

        then:
        result;
    }

}
