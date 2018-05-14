package mazad.mazad.models;

import java.util.ArrayList;

public class ProductModel {

    String id;
    String name;
    String body;
    String cityId;
    String userId;
    String mobile;
    String created;
    ArrayList<String> images;
    String user;
    String city;
    boolean favorite;
    ArrayList<CommentModel> comments;

    public ProductModel(String id, String name, String body, String cityId, String userId, String mobile, String created, ArrayList<String> images, String user, String city, boolean favorite, ArrayList<CommentModel> comments) {
        this.id = id;
        this.name = name;
        this.body = body;
        this.cityId = cityId;
        this.userId = userId;
        this.mobile = mobile;
        this.created = created;
        this.images = images;
        this.user = user;
        this.city = city;
        this.favorite = favorite;
        this.comments = comments;
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

    public String getCityId() {
        return cityId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCreated() {
        return created;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getUser() {
        return user;
    }

    public String getCity() {
        return city;
    }

    public boolean getFavorite() {
        return favorite;
    }


    public ArrayList<CommentModel> getComments() {
        return comments;
    }


    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
