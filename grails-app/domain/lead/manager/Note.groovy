package lead.manager

class Note {
    String content;
    Date dateCreated;
    static belongsTo = [lead:Lead];

    static constraints = {
        content blank:false,maxSize:5000;
    }
}
