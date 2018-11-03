import spock.lang.Specification
import spock.lang.Unroll

class HangTest extends Specification {
    @Unroll
    def "#name"() {
        expect:
        name

        where:
        name << ["name-that-ends-with-a-carriage-return\r"]
    }
}
