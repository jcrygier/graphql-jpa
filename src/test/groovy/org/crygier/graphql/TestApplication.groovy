package org.crygier.graphql

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
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
    @CompileDynamic
    @ConditionalOnClass(name = ["org.h2.tools.Server"])
    public Object h2WebServer() {
        String[] realArguments = [ "-webPort", "8082", "-webAllowOthers" ] as String[]
        String[][] arguments = [ realArguments ] as String[][]

        try {
            Object server = Class.forName("org.h2.tools.Server").getDeclaredMethod("createWebServer", String[]).invoke(null, arguments);
            server.start()

            return server;
        } catch (Exception ignored) {}
    }

}
