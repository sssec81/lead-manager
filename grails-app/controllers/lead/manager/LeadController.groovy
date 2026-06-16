package lead.manager

import grails.converters.JSON;

class LeadController {
    LeadService leadService;

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
    def clean_lead(){
        String fname = params.firstName;
        String lname = params.lastName;
        String email = params.email;
        Lead lead = leadService.cleanLead(fname,lname,email);
        def response
        if (lead){
            response = [status: "success",message:"Lead successfully processed",data:[
                    id:lead.id,
                    firstName:lead.firstName,
                    lastName:lead.lastName,
                    email:lead.email,
                    status:lead.status
            ]];

        }
        else{
            response = [status: "error",message:"Something went wrong"];
        }

        render response as JSON;
    }
}
