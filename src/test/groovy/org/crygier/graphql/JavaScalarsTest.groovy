package org.crygier.graphql

import graphql.schema.Coercing
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class JavaScalarsTest extends Specification {

    def 'Long to LocalDateTime'() {
        given:
        Coercing coercing = JavaScalars.GraphQLLocalDateTime.getCoercing()

        long input = LocalDateTime.of(2017,02,02,12,30,15)
                .toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()))

        when:
        def result = coercing.serialize(input)

        then:
        result instanceof LocalDateTime

        result.dayOfMonth == 2
        result.month == Month.FEBRUARY
        result.year == 2017
        result.hour == 12
        result.minute == 30
        result.second == 15
    }

    def 'String to LocalDateTime'() {
        given:
        Coercing coercing = JavaScalars.GraphQLLocalDateTime.getCoercing()
        final String input = "2017-02-02T12:30:15"

        when:
        def result = coercing.serialize(input)

        then:
        result instanceof LocalDateTime

        result.dayOfMonth == 2
        result.month == Month.FEBRUARY
        result.year == 2017
        result.hour == 12
        result.minute == 30
        result.second == 15
    }

    def 'Long to LocalDate'() {
        given:
        Coercing coercing = JavaScalars.GraphQLLocalDate.getCoercing()
        long input = LocalDateTime.of(2017,02,02,0,0,0)
                .toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()))

        when:
        def result = coercing.serialize(input)

        then:
        result instanceof LocalDate

        result.dayOfMonth == 2
        result.month == Month.FEBRUARY
        result.year == 2017
    }

    def 'String to LocalDate'() {
        given:
        Coercing coercing = JavaScalars.GraphQLLocalDate.getCoercing()
        final String input = "2017-02-02"

        when:
        def result = coercing.serialize(input)

        then:
        result instanceof LocalDate

        result.dayOfMonth == 2
        result.month == Month.FEBRUARY
        result.year == 2017
    }
}
