package lead.manager

import grails.converters.JSON;

class LeadController {
    LeadService leadService;

    static allowedMethods = [index:"GET",some_test:"GET",clean_lead:"GET",csv_import:"POST",update_lead:"POST"]

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
        Map result = leadService.cleanLead(fname,lname,email);
        def apiResponse
        def leadObject = result.lead
        if (result.status=="SAVED" || result.status=="DUPLICATE"){
            apiResponse = [status:result.status, message: result.message,
                           lead  :[
                                id:leadObject?.id,
                                firstName:leadObject?.firstName,
                                lastName:leadObject?.lastName,
                                email:leadObject?.email,
                                status:leadObject?.status

                        ]
            ];
        }
        else{
            apiResponse = [status:result.status, message: result.message]
        }

        render apiResponse as JSON;
    }

    def csv_import ()
    {
        def file = request.getFile("file");

        if (!file || file.empty){
            response.status = 400;
            render ([status:"INVALID",message:"CSV file is required"] as JSON);
            return

        }
        String csv  = file.inputStream.getText("UTF-8");
        Map summary = leadService.importCsv(csv);

        render([status:"SUCCESS",summary:summary,message: "CSV successfully parsed"] as JSON);
    }

    def update_lead(){
        def body = request.JSON
        String id = params.id;
        Lead lead   = Lead.get(id);
        if(!lead){
            response.status = 400;
            render([status:"INVALID",message:"params not enough"] as JSON);
            return
        }
        // DEBUG: Is Grails seeing the changes?
        bindData(lead,body,include:["firstName","lastName","email","status"])
        log.info "Is lead dirty? ${lead.isDirty()}"
        log.info "Dirty properties: ${lead.dirtyPropertyNames}"
        Map result = leadService.updateLead(lead);
        render(result as JSON);
    }

}
