package Model;

public class Doctor {

    private String name, id, qual, URL, email;

    public Doctor(){

    }
    public Doctor(String name, String id, String qual, String URL, String email) {
        this.name = name;
        this.id = id;
        this.qual = qual;
        this.URL = URL;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQual() {
        return qual;
    }

    public void setQual(String qual) {
        this.qual = qual;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


//        NOTE:     Add qualification in the database for doctors +++++++++++++++++++++++++++++++++++++