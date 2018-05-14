package mazad.mazad.models;

import java.util.ArrayList;

public class SearchModel {

    private DepartmentModel departmentModel;
    private ArrayList<SubCategoryModel> subCategoryModels;


    public SearchModel(DepartmentModel departmentModel, ArrayList<SubCategoryModel> subCategoryModels) {
        this.departmentModel = departmentModel;
        this.subCategoryModels = subCategoryModels;
    }


    public DepartmentModel getDepartmentModel() {
        return departmentModel;
    }

    public ArrayList<SubCategoryModel> getSubCategoryModels() {
        return subCategoryModels;
    }

    public void setSubCategoryModels(ArrayList<SubCategoryModel> subCategoryModels) {
        this.subCategoryModels = subCategoryModels;
    }
}
