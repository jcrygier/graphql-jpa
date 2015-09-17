package org.crygier.graphql;

import graphql.GraphQLException;
import graphql.language.IntValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class JavaScalars {

    public static GraphQLScalarType GraphQLLong = new GraphQLScalarType("Long", "Built-in Long", new Coercing() {
        @Override
        public Object coerce(Object input) {
            if (input instanceof String) {
                return Long.parseLong((String) input);
            } else if (input instanceof Long) {
                return input;
            } else {
                throw new GraphQLException("");
            }
        }

        @Override
        public Object coerceLiteral(Object input) {
            if (!(input instanceof IntValue)) return null;
            return new Long(((IntValue) input).getValue());
        }
    });

}
