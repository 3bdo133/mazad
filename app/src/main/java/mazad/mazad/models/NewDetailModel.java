package mazad.mazad.models;

public class NewDetailModel {

    private String id;
    private String name;
    private String body;
    private String image;
    private String categoryId;
    private String created;
    private String status;
    private String category;
    private String like;
    private String disLike;

    public NewDetailModel(String id, String name, String body, String image, String categoryId, String created, String status, String category, String like, String disLike) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.image = image;
        this.categoryId = categoryId;
        this.created = created;
        this.status = status;
        this.category = category;
        this.like = like;
        this.disLike = disLike;
    }

    public String getLike() {
        return like;
    }

    public String getDisLike() {
        return disLike;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public String getImage() {
        return image;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCreated() {
        return created;
    }

    public String getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }
}
