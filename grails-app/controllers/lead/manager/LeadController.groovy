package lead.manager

import grails.converters.JSON;

class LeadController {

    def index() {
        def responseData =[
                status :"success",
                message:"lead API is up and running"
        ]
        render responseData as JSON;
    }
    def some_test(){
        def response = [status: "success",message: "testing new api"]
        render response as JSON;
    }
}
