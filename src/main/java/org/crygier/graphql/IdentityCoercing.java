package org.crygier.graphql;

import graphql.schema.Coercing;

public class IdentityCoercing implements Coercing{

    @Override
    public Object serialize(Object input) {
        return input;
    }

    @Override
    public Object parseValue(Object input) {
        return input;
    }

    @Override
    public Object parseLiteral(Object input) {
        return input;
    }

}
