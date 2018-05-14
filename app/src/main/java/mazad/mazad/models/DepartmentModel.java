package mazad.mazad.models;

import java.io.Serializable;

public class DepartmentModel implements Serializable {

    String id;
    String name;
    String image;


    public DepartmentModel(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
