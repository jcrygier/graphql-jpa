package org.crygier.graphql;

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
            this.graphQL = new GraphQL(new GraphQLSchemaBuilder(entityManager).getGraphQLSchema());
    }

    @Transactional
    public ExecutionResult execute(String query) {
        return graphQL.execute(query);
    }

    @Transactional
    public ExecutionResult execute(String query, Map<String, Object> arguments) {
        if (arguments == null)
            return graphQL.execute(query);
        else
            return graphQL.execute(query, (Object) null, arguments);
    }

}
