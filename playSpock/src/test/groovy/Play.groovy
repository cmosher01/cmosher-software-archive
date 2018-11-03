import spock.lang.Specification

class Play extends Specification {
    def nominal() {
        given:
        def uut = new SomeThing(y: 6, x: 7)
        expect:
        uut.x == 7
        uut.y == 6
    }

    def "cannot set immutable property"() {
        given:
        def uut = new SomeThing(y: 6, x: 7)
        when:
        ++uut.x
        then:
        thrown ReadOnlyPropertyException
    }
}
