import geb.spock.GebSpec
import org.openqa.selenium.By

class StackTraceTest extends GebSpec {
    def stackTraceTest() {
        when:
//        to SurefireHomePage
        browser.driver.findElement(By.id("throw_NoSuchElementException"))

        then:
        true
    }
}
