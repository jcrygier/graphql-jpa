package org.crygier.graphql

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.orm.jpa.EntityScan
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
@EntityScan
@CompileStatic
class TestApplication {

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public GraphQLExecutor graphQLExecutor() {
        return new GraphQLExecutor();
    }

    @Bean
    public GraphQlController() {
        return new GraphQlController();
    }

}
