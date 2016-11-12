package org.crygier.graphql;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class JavaScalars {
    static final Logger log = LoggerFactory.getLogger(JavaScalars.class);

    public static GraphQLScalarType GraphQLDate = new GraphQLScalarType("Date", "Date type", new Coercing() {
        @Override
        public Object serialize(Object input) {
            if (input instanceof String) {
                return parseStringToDate((String) input);
            } else if (input instanceof Date) {
                return input;
            } else if (input instanceof Long) {
                return new Date(((Long) input).longValue());
            } else if (input instanceof Integer) {
                return new Date(((Integer) input).longValue());
            }
            return null;
        }

        @Override
        public Object parseValue(Object input) {
            return serialize(input);
        }

        @Override
        public Object parseLiteral(Object input) {
            if (input instanceof StringValue) {
                return parseStringToDate(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                BigInteger value = ((IntValue) input).getValue();
                return new Date(value.longValue());
            }
            return null;
        }

        private Date parseStringToDate(String input) {
            try {
                return DateFormat.getInstance().parse(input);
            } catch (ParseException e) {
                log.warn("Failed to parse Date from input: " + input, e);
                return null;
            }
        }
    });
}
