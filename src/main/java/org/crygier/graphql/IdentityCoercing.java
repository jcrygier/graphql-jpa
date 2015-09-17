package org.crygier.graphql;

import graphql.schema.Coercing;

public class IdentityCoercing implements Coercing{

    @Override
    public Object coerce(Object input) {
        return input;
    }

    @Override
    public Object coerceLiteral(Object input) {
        return input;
    }

}
