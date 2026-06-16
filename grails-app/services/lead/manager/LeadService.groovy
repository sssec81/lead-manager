package lead.manager

import grails.gorm.transactions.Transactional

@Transactional
class LeadService {
    Lead cleanLead(String fName,String lName,String rawEmail){
        String cleanFirstName = fName?.strip();
        String cleanLastName = lName?.strip();
        String cleanRawEmail = rawEmail?.strip()?.toLowerCase();

        if (!cleanFirstName || !cleanLastName || !cleanRawEmail){
            log.error("the required params are not present");
            return null;
        }

        // if email contains personal mail like gmail or yahoo return null. business rules
        if (cleanRawEmail.endsWith("@gmail.com") || cleanRawEmail.endsWith(("@yahoo.com")) ){
            return null;
        }
        Lead existingLead = Lead.findByEmail(cleanRawEmail);

        // if that lead already exists then return existing lead object.
        if(existingLead){
            log.info("the lead already exists ok ${cleanRawEmail}");
            return existingLead;
        }

        // create lead from params
        Lead newLead = new Lead(firstName: cleanFirstName,lastName: cleanLastName,email: cleanRawEmail,status: "NEW");

        // create Note object and add pk to fk to lead and note
        Note systemNote = new Note(content: "Lead automatically ingested via background batch CSV pipeline.");
        newLead.addToNotes(systemNote);

        if(newLead.validate()){
            return newLead.save(flush:true);

        }
        else{
            log.error("the lead couldn't be saved ${newLead.errors}");
            return  null;
        }


    }

}
