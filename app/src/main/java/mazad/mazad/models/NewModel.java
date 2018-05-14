package mazad.mazad.models;

import java.io.Serializable;

public class NewModel implements Serializable {

    String id;
    String time;
    String notification;
    String type;
    String description;
    String image;
    String categoryId;
    String category;
    String name;

    public NewModel(String id, String image, String categoryId, String category, String name) {
        this.id = id;
        this.image = image;
        this.categoryId = categoryId;
        this.category = category;
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public String getNotification() {
        return notification;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }


    public String getId() {
        return id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
