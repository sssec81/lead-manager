package lead.manager

class Lead {
    String firstName;
    String lastName;
    String email;
    String status = "New";
    static constraints = {
        firstName blank: false,maxSize:50
        lastName blank:false,mazSize:50
        email blank:false,email:true,unique:true
        status inList: ["NEW","CONTACTED","QUALIFIED","REJECT"]
    }
}
