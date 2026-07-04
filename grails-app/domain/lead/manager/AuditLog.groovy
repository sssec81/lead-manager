package lead.manager

class AuditLog {
    String action;
    String fieldName;
    String oldValue;
    String newValue;
    Long leadId;
    Date dateCreated;


    static constraints = {
        fieldName nullable:true
        oldValue nullable:true
        newValue nullable:true

    }
}
