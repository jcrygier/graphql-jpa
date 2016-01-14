package org.crygier.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionResult
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@CompileStatic
class GraphQlController {

    @Autowired
    private GraphQLExecutor graphQLExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(path = '/graphql', method = RequestMethod.POST)
    ExecutionResult graphQl(@RequestBody final GraphQLInputQuery query) {
        Map<String, Object> variables = query.getVariables() ? objectMapper.readValue(query.getVariables(), Map) : null;

        return graphQLExecutor.execute(query.getQuery(), variables);
    }

    public static final class GraphQLInputQuery {
        String query;
        String variables;
    }

}