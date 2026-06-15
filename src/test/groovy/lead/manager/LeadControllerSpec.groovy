package lead.manager

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class LeadControllerSpec extends Specification implements ControllerUnitTest<LeadController> {
    def "test index section returns json"(){
        when: "index is called"
        controller.index();
        then: "The response is parsed as JSON and values match"
        response.json.status == "success"
        response.json.message == "lead API is up and running"
    }
}
