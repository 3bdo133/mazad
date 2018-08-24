package mazad.mazad.models;

import java.io.Serializable;

public class PresentedItemModel implements Serializable {

    String mName;
    int mImageTest;
    String mDescription;
    String mLocation;
    String mDate;
    String mId;
    String mCategoryId;
    String mCategory;
    String mUserId;
    String mUser;
    String mImage;
    int favorite;
    int star;

    public PresentedItemModel(String mName, int mImage, String mDescription, String mLocation, int favorite) {
        this.mName = mName;
        this.mImageTest = mImage;
        this.mDescription = mDescription;
        this.mLocation = mLocation;
        this.favorite = favorite;
    }

    public PresentedItemModel(String mName, String mDate, String mId, String mCategoryId, String mCategory, String mUserId, String mUser, String mImage,String mLocation) {
        this.mName = mName;
        this.mDate = mDate;
        this.mId = mId;
        this.mCategoryId = mCategoryId;
        this.mCategory = mCategory;
        this.mUserId = mUserId;
        this.mUser = mUser;
        this.mImage = mImage;
        this.mLocation = mLocation;
    }


    public PresentedItemModel(String mName, String mDate, String mId, String mCategoryId, String mCategory, String mUserId, String mUser, String mImage, int favorite) {
        this.mName = mName;
        this.mDate = mDate;
        this.mId = mId;
        this.mCategoryId = mCategoryId;
        this.mCategory = mCategory;
        this.mUserId = mUserId;
        this.mUser = mUser;
        this.mImage = mImage;
        this.favorite = favorite;
    }


    public PresentedItemModel(String mName, String mDate, String mId, String mCategoryId, String mCategory, String mUserId, String mUser, String mImage,String mLocation,int star) {
        this.mName = mName;
        this.mDate = mDate;
        this.mId = mId;
        this.mCategoryId = mCategoryId;
        this.mCategory = mCategory;
        this.mUserId = mUserId;
        this.mUser = mUser;
        this.mImage = mImage;
        this.mLocation = mLocation;
        this.star = star;
    }

    public int isStar() {
        return star;
    }

    public String getmImage() {
        return mImage;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmId() {
        return mId;
    }

    public String getmCategoryId() {
        return mCategoryId;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmUser() {
        return mUser;
    }

    public String getmName() {
        return mName;
    }

    public int getmImageTest() {
        return mImageTest;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmLocation() {
        return mLocation;
    }

    public int getFavorite() {
        return favorite;
    }


    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
