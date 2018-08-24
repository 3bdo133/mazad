package mazad.mazad.models;

import java.util.ArrayList;

public class NewDetailModel {

    private String id;
    private String name;
    private String body;
    private ArrayList<String> images;
    private String categoryId;
    private String created;
    private String status;
    private String category;
    private String like;
    private String disLike;
    private String mainImage;
    private String video;

    public NewDetailModel(String id, String name, String body, ArrayList<String> images, String categoryId, String created, String status, String category, String like, String disLike) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.images = images;
        this.categoryId = categoryId;
        this.created = created;
        this.status = status;
        this.category = category;
        this.like = like;
        this.disLike = disLike;
    }

    public NewDetailModel(String id, String name, String body, ArrayList<String> images, String categoryId, String created, String status, String category, String like, String disLike, String mainImage, String video) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.images = images;
        this.categoryId = categoryId;
        this.created = created;
        this.status = status;
        this.category = category;
        this.like = like;
        this.disLike = disLike;
        this.mainImage = mainImage;
        this.video = video;
    }

    public String getMainImage() {
        return mainImage;
    }

    public String getVideo() {
        return video;
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

    public ArrayList<String> getImages() {
        return images;
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
