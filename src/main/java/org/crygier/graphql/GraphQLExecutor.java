package org.crygier.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Map;

public class GraphQLExecutor {

    @Resource
    private EntityManager entityManager;
    private GraphQL graphQL;

    protected GraphQLExecutor() {}
    public GraphQLExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
        createGraphQL();
    }

    @PostConstruct
    protected void createGraphQL() {
        if (entityManager != null)
            this.graphQL = GraphQL.newGraphQL(new GraphQLSchemaBuilder(entityManager).getGraphQLSchema()).build();
    }

    @Transactional
    public ExecutionResult execute(String query) {
        return graphQL.execute(query);
    }

    @Transactional
    public ExecutionResult execute(String query, Map<String, Object> arguments) {
        if (arguments == null)
            return graphQL.execute(query);
        return graphQL.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build());
    }

}
