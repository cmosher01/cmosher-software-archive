import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import groovy.util.logging.Slf4j
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import org.slf4j.LoggerFactory
import spock.lang.Specification

@Slf4j
class NewRelicApi extends Specification {
    def setup() {
        (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.INFO
    }

    def test() {
        given:
        log.trace "test"
        RESTClient newrelic = new RESTClient("https://api.newrelic.com/")

        when:
        println "Application,Apdex Threshold,Error Threshold"
        def applications = newrelic.get(path: "/api/v1/accounts/66998/applications.xml", headers: ["X-Api-Key": "ce41b0a6947b9813d5d26fd68daeec48ffc4c2adce89b69", Accept: 'application/xml'], contentType: ContentType.XML).data
        applications.application.each { application ->
            def application_setting = newrelic.get(path: "/api/v1/accounts/66998/application_settings/${application.id}.xml", headers: ["X-Api-Key": "ce41b0a6947b9813d5d26fd68daeec48ffc4c2adce89b69", Accept: 'application/xml'], contentType: ContentType.XML).data
            if (application_setting["alerts-enabled"]=="true") {
                def app = application.name
                def thresholds = newrelic.get(path: "/api/v1/accounts/66998/applications/${application.id}/thresholds.xml", headers: ["X-Api-Key": "ce41b0a6947b9813d5d26fd68daeec48ffc4c2adce89b69", Accept: 'application/xml'], contentType: ContentType.XML).data
                def apdex, errorRate
                thresholds.threshold.each { threshold ->
                    def cv = threshold["critical-value"]
                    def type = threshold.type
                    if (type == "Apdex") {
                        apdex = cv
                    } else if (type == "ErrorRate") {
                        errorRate = cv
                    }
                }
                println "${app},${apdex},${errorRate}"
            }
        }

        then:
        1 == 1
    }
}
