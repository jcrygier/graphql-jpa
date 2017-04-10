package org.crygier.graphql;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class JavaScalars {
    static final Logger log = LoggerFactory.getLogger(JavaScalars.class);

    public static GraphQLScalarType GraphQLLocalDateTime = new GraphQLScalarType("LocalDateTime", "Date type", new Coercing() {
        @Override
        public Object serialize(Object input) {
            if (input instanceof String) {
                return parseStringToLocalDateTime((String) input);
            } else if (input instanceof LocalDateTime) {
                return input;
            } else if (input instanceof Long) {
                return parseLongToLocalDateTime((Long) input);
            } else if (input instanceof Integer) {
                return parseLongToLocalDateTime((Integer) input);
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
                return parseStringToLocalDateTime(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                BigInteger value = ((IntValue) input).getValue();
                return parseLongToLocalDateTime(value.longValue());
            }
            return null;
        }

        private LocalDateTime parseLongToLocalDateTime(long input) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(input), TimeZone.getDefault().toZoneId());
        }

        private LocalDateTime parseStringToLocalDateTime(String input) {
            try {
                return LocalDateTime.parse(input);
            } catch (DateTimeParseException e) {
                log.warn("Failed to parse Date from input: " + input, e);
                return null;
            }
        }
    });

    public static GraphQLScalarType GraphQLLocalDate = new GraphQLScalarType("LocalDate", "Date type", new Coercing() {
        @Override
        public Object serialize(Object input) {
            if (input instanceof String) {
                return parseStringToLocalDate((String) input);
            } else if (input instanceof LocalDate) {
                return input;
            } else if (input instanceof Long) {
                return parseLongToLocalDate((Long) input);
            } else if (input instanceof Integer) {
                return parseLongToLocalDate((Integer) input);
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
                return parseStringToLocalDate(((StringValue) input).getValue());
            } else if (input instanceof IntValue) {
                BigInteger value = ((IntValue) input).getValue();
                return parseLongToLocalDate(value.longValue());
            }
            return null;
        }

        private LocalDate parseLongToLocalDate(long input) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(input), TimeZone.getDefault().toZoneId()).toLocalDate();
        }

        private LocalDate parseStringToLocalDate(String input) {
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                log.warn("Failed to parse Date from input: " + input, e);
                return null;
            }
        }
    });

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

    public static GraphQLScalarType GraphQLUUID = new GraphQLScalarType("UUID", "UUID type", new Coercing() {

        @Override
        public Object serialize(Object input) {
            if (input instanceof UUID) {
                return  input;
            }
            return null;
        }

        @Override
        public Object parseValue(Object input) {
           if (input instanceof String) {
                return parseStringToUUID((String) input);
            }
            return null;
        }

        @Override
        public Object parseLiteral(Object input) {
            if (input instanceof StringValue) {
                return parseStringToUUID(((StringValue) input).getValue());
            }
            return null;
        }

        private UUID parseStringToUUID(String input) {
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                log.warn("Failed to parse UUID from input: " + input, e);
                return null;
            }
        }
    });
}
