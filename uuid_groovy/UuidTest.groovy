package com.surveysampling.apps.newRelicWebHook

import spock.lang.Specification
import spock.lang.Unroll

class UuidTest extends Specification {
    @Unroll("Uuid toString: #standardUuid")
    def "Uuid toString"() {
        given:
        Uuid uut = new Uuid(UUID.fromString(standardUuid))

        when:
        def actual = "${uut}"

        then:
        actual == expected

        where:
        standardUuid                           | expected
        "79c00400-a149-4e92-8a8a-d8daf203156d" | "79c00400a1494e928a8ad8daf203156d"
        "5d02b235-dc9a-4959-a979-b3adc6e71a65" | "5d02b235dc9a4959a979b3adc6e71a65"
    }

    def "is equal"() {
        given:
        Uuid a = new Uuid()
        Uuid b = new Uuid(a.javaUuid)

        expect:
        a.equals(b)
        b.equals(a)
        a == b
        b == a
        !a.is(b)
        !b.is(a)
    }

    def "is not equal"() {
        given:
        Uuid a = new Uuid()
        Uuid b = new Uuid()

        expect:
        !a.equals(b)
        !b.equals(a)
        a != b
        b != a
    }

    def "same hashCode"() {
        given:
        Uuid a = new Uuid()
        Uuid b = new Uuid(a.javaUuid)

        expect:
        a.hashCode() == b.hashCode()
    }
}
