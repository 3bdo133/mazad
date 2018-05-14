package mazad.mazad.models;

public class CommentModel {

    private String name;
    private String user;
    private String report;
    private String date;

    public CommentModel(String name, String user, String report, String date) {
        this.name = name;
        this.user = user;
        this.report = report;
        this.date = date;
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
}
