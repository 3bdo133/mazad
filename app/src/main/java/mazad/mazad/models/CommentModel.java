package mazad.mazad.models;

public class CommentModel {

    private String name;
    private String user;
    private String report;
    private String date;
    private String id;

    public CommentModel(String name, String user, String report, String date, String id) {
        this.name = name;
        this.user = user;
        this.report = report;
        this.date = date;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getReport() {
        return report;
    }

    public String getDate() {
        return date;
    }


    public String getId() {
        return id;
    }
}
