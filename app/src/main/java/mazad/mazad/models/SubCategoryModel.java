package mazad.mazad.models;

import java.util.ArrayList;

public class SubCategoryModel {

    String id;
    String name;
    ArrayList<SubCategoryModel> children;

    public SubCategoryModel(String id, String name, ArrayList<SubCategoryModel> children) {
        this.id = id;
        this.name = name;
        this.children = children;
    }

    public SubCategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<SubCategoryModel> getChildren() {
        return children;
    }
}
