package lead.manager

import grails.gorm.transactions.Transactional

@Transactional
class LeadService {
    Map cleanLead(String fName,String lName,String rawEmail){
        String cleanFirstName = fName?.strip();
        String cleanLastName = lName?.strip();
        String cleanRawEmail = rawEmail?.strip()?.toLowerCase();

        if (!cleanFirstName || !cleanLastName || !cleanRawEmail){
            log.error("the required params are not present");
            return [status: "INVALID",lead:null,message: "params not enough"];
        }

        // if email contains personal mail like gmail or yahoo return null. business rules
        if (cleanRawEmail.endsWith("@gmail.com") || cleanRawEmail.endsWith(("@yahoo.com")) ){
            log.info("this ${cleanRawEmail} skipped")
            return [status: "SKIPPED",lead:null,message: "PERSONAL EMAIL SKIPPED"];
        }
        Lead existingLead = Lead.findByEmail(cleanRawEmail);

        // if that lead already exists then return existing lead object.
        if(existingLead){
            log.info("the lead already exists ok ${cleanRawEmail}");
            return [status: "DUPLICATE",lead: existingLead,message: "lead already exists"];
        }

        // create lead from params
        Lead newLead = new Lead(firstName: cleanFirstName,lastName: cleanLastName,email: cleanRawEmail,status: "NEW");


        // create Note object and add pk to fk to lead and note
        Note systemNote = new Note(content: "Lead automatically ingested via background batch CSV pipeline.");
        newLead.addToNotes(systemNote);

        if(newLead.validate()){
            Lead savedLead = newLead.save(flush:true);
            if (!savedLead){
                return [status: "INVALID",lead:null,message:"LEAD DIDN'T SAVED"]
            }
            return [status: "SAVED",lead:newLead,message:"LEAD SAVED SUCCESSSFULLY"]

        }
        else{
            log.error("the lead couldn't be saved ${newLead.errors}");
            return  [status:"INVALID",lead:null,message:newLead.errors];
        }


    }

    Map importCsv(String csvText){
        Map summary = [
                totalRows:0,
                savedRows:0,
                duplicateRows:0,
                skippedPersonEmailRows:0,
                invalidRows:0
        ]

        if(!csvText?.trim()){
            return summary;
        }

        List<String> lines = csvText.readLines().findAll{it?.trim()}

        if(lines.size() <=1){
            return summary;
        }

        // remove header
        List<String> dataRows = lines.drop(1);

        // iterate through dataRows
        dataRows.forEach{String line ->
            summary.totalRows++;
            List<String> columns = line.split(",",-1).collect{it.trim()};
            if(columns.size()<3){
            summary.invalidRows++;
                return
            }
            String firstName = columns[0];
            String lastName = columns[1];
            String email = columns[2];

            Map result = cleanLead(firstName,lastName,email);

            if(result.status=="SAVED"){
                summary.savedRows++;
            }
            else if (result.status == "DUPLICATE"){
                summary.duplicateRows++;
            }
            else if (result.status=="SKIPPED"){
                summary.skippedPersonEmailRows++
            }
            else{
                summary.invalidRows++
            }



        }
        return summary;
    }

    Map updateLead(Lead lead){
    if(lead.validate()){
        Lead savedLead = lead.save(flush: true);
        if (!savedLead){
            return [status: "INVALID",lead:null,message: "something went wrong with saving in db"];
        }
        else{
            return [status: "SAVED",lead:lead,message: "Updated the db "];
        }
    }
        else{
        return [status: "INVALID",lead:null,message: "PLEASE PROVIDE CORRECT PARAMS"];
    }

    }

}
