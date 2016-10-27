package org.crygier.graphql;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class JavaScalars {
    public static GraphQLScalarType GraphQLDate = new GraphQLScalarType("Date", "Date type", new Coercing() {
        @Override
        public Object serialize(Object input) {
            if (input instanceof String) {
                try {
                    return DateFormat.getInstance().parse((String)input);
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            } else if (input instanceof Date) {
                return input;
            } else if (input instanceof Long) {
                return new Date(((Long) input));
            } else if (input instanceof Integer) {
                return new Date(((Integer) input).longValue());
            } else {
                return null;
            }
        }

        @Override
        public Object parseValue(Object input) {
            return serialize(input);
        }

        @Override
        public Object parseLiteral(Object input) {
            if (input instanceof StringValue) {
                try {
                    return DateFormat.getInstance().parse(((StringValue) input).getValue());
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            } else if (input instanceof IntValue) {
                BigInteger value = ((IntValue) input).getValue();
                return new Date(value.longValue());
            }
            return null;
        }
    });
}
