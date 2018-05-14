package mazad.mazad.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Map;

import mazad.mazad.models.ChatModel;
import mazad.mazad.models.CityModel;
import mazad.mazad.models.CommentModel;
import mazad.mazad.models.DepartmentModel;
import mazad.mazad.models.MessageModel;
import mazad.mazad.models.NewDetailModel;
import mazad.mazad.models.NewModel;
import mazad.mazad.models.PresentedItemModel;
import mazad.mazad.models.ProductModel;
import mazad.mazad.models.SubCategoryModel;
import mazad.mazad.models.UserModel;

/**
 * Created by Abdelrahman Hesham on 3/13/2018.
 */

public class Connector {

    private Context mContext;
    private LoadCallback mLoadCallback;
    private ErrorCallback mErrorCallback;
    private RequestQueue mQueue;
    private Map<String, String> mMap;
    private DepartmentModel mDepartmentModel;


    public interface LoadCallback {

        void onComplete(String tag, String response);

    }

    public interface ErrorCallback {

        void onError(VolleyError error);

    }

    public Connector(Context mContext, LoadCallback mLoadCallback, ErrorCallback mErrorCallback) {
        this.mContext = mContext;
        this.mLoadCallback = mLoadCallback;
        this.mErrorCallback = mErrorCallback;
    }


    public void getRequest(final String tag, final String url) {
        Helper.writeToLog(url);
        String response = "";
        if (isOnline(mContext)) {
            mQueue = Volley.newRequestQueue(mContext);
            StringRequest mStringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Helper.writeToLog(response);
                            mLoadCallback.onComplete(tag, response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    mErrorCallback.onError(error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return mMap;
                }
            };
            mStringRequest.setTag(tag);
            mQueue.add(mStringRequest);
        } else {
            mErrorCallback.onError(new NoConnectionError());
        }


    }

    public void cancelAllRequests(final String tag) {
        if (mQueue != null) {
            mQueue.cancelAll(tag);
        }
    }


    public static String createRegisterUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.SIGN_UP_PATH);

        return builder.toString();

    }

    public static String createGetProductsUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_PRODUCTS_PATH);

        return builder.toString();

    }

    public static String createLoginUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.LOGIN_PATH);

        return builder.toString();

    }

    public static String createNewsUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_NEWS);

        return builder.toString();
    }

    public static String createGetProductUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_PRODUCT);

        return builder.toString();
    }

    public static String createGetNewUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_NEW);

        return builder.toString();
    }

    public static String createAddCommentUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.ADD_COMMENT);

        return builder.toString();
    }

    public static String createAddFeedBackUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.ADD_FEEDBACK);

        return builder.toString();
    }

    public static String createAddToFavoriteUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.ADD_TO_FAVORITE);

        return builder.toString();
    }

    public static String createAddLikeNewUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.ADD_NEW_LIKE);

        return builder.toString();
    }

    public static String createMyFavoriteUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_FAVORITES);

        return builder.toString();
    }

    public static String createUploadImageUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.UPLOAD_IMAGE);

        return builder.toString();
    }

    public static String createGetCategoryUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_CATEGORIES);

        return builder.toString();
    }

    public static String createGetCitiesUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_CITIES);

        return builder.toString();
    }

    public static String createGetSubCategoriesUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_SUB_CATEGORIES);

        return builder.toString();
    }


    public static String createAddProductUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.ADD_PRODUCT);

        return builder.toString();
    }

    public static String createGetChatUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_CHAT);

        return builder.toString();
    }

    public static String createGetChatMessagesUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.GET_CHAT_MESSAGES);

        return builder.toString();
    }

    public static String createSendMessageUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.SEND_MESSAGE);

        return builder.toString();
    }

    public static String createStartChatUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.START_CHAT);

        return builder.toString();
    }

    public static String createDeleteFavoriteUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.DELETE_FAVOURITE);

        return builder.toString();
    }

    public static String createEditProfileUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.EDIT_PROFILE);

        return builder.toString();
    }

    public static String createRemoveProductUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.REMOVE_PRODUCT);

        return builder.toString();
    }


    public static String createDeleteChatUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.DELETE_CHAT);

        return builder.toString();
    }

    public static String createReportSpamUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.REPORT_SPAM);

        return builder.toString();
    }

    public static String createForgetPasswordUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.FORGET_PASSWORD);

        return builder.toString();
    }

    public static String createChangePasswordUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.CHANGE_PASSWORD);

        return builder.toString();
    }

    public static String createBankAccountUrl() {
        Uri.Builder builder = Uri.parse(Constants.MAZAD_API_URL).buildUpon()
                .appendPath(Constants.BANK_ACCOUNT);

        return builder.toString();
    }


    public static boolean checkStatus(String response) {
        boolean status = false;
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                status = jsonObject.getBoolean("status");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return status;
    }

    public static String getMessage(String response) {
        String message = null;
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                message = jsonObject.getString("message");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return message;
    }


    public static boolean checkImages(String response) {
        boolean status = false;
        JSONArray images;
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                images = jsonObject.getJSONArray("images");
                status = true;
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return status;
    }

    public static ArrayList<String> getImages(String response) {
        ArrayList<String> imagesPaths = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray images = jsonObject.getJSONArray("images");
                for (int i=0 ; i<images.length();i++){
                    imagesPaths.add(images.getString(i));
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return imagesPaths;
    }

    public static UserModel registerAndLoginJson(String response) {
        UserModel userModel = null;
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject user = jsonObject.getJSONObject("user");
                String name = user.getString("name");
                String email = user.getString("username");
                String mobile = user.getString("mobile");
                String role = String.valueOf(user.getInt("role"));
                String id = user.getString("id");
                userModel = new UserModel(name, email, mobile, role, id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return userModel;
    }

    public static ArrayList<PresentedItemModel> getProductsJson(String response) {
        ArrayList<PresentedItemModel> list = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray products = jsonObject.getJSONArray("products");
                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);
                    String id = product.getString("id");
                    String name = product.getString("name");
                    String date = product.getString("date");
                    String categoryId = product.getString("category_id");
                    String category = product.getString("category");
                    String userId = product.getString("user_id");
                    String user = product.getString("user");
                    String image = product.getString("image");
                    String location = product.getString("city");
                    list.add(new PresentedItemModel(name, date, id, categoryId, category, userId, user, image, location));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static ArrayList<NewModel> getNewsJson(String response) {
        ArrayList<NewModel> list = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray products = jsonObject.getJSONArray("news");
                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);
                    String id = product.getString("id");
                    String name = product.getString("name");
                    String categoryId = product.getString("category_id");
                    String category = product.getString("category");
                    String image = product.getString("image");
                    list.add(new NewModel(id, image, categoryId, category, name));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static ArrayList<DepartmentModel> getDepartmentsJson(String response) {
        ArrayList<DepartmentModel> list = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray products = jsonObject.getJSONArray("categories");
                for (int i = 0; i < products.length(); i++) {
                    JSONObject category = products.getJSONObject(i);
                    String id = category.getString("id");
                    String name = category.getString("name");
                    String image = category.getString("image");
                    list.add(new DepartmentModel(id,name,image));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static ArrayList<CityModel> getCitiesJson(String response) {
        ArrayList<CityModel> list = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray cities = jsonObject.getJSONArray("cities");
                for (int i = 0; i < cities.length(); i++) {
                    JSONObject city = cities.getJSONObject(i);
                    String id = city.getString("id");
                    String name = city.getString("name");
                    list.add(new CityModel(id,name));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static ArrayList<ChatModel> getChatJson(String response) {
        ArrayList<ChatModel> list = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray chats = jsonObject.getJSONArray("chats");
                for (int i = 0; i < chats.length(); i++) {
                    JSONObject chat = chats.getJSONObject(i);
                    String chatId = chat.getString("chat_id");
                    String lastMessage = chat.getString("last_message");
                    String name = chat.getString("name");
                    String toId = chat.getString("to_id");
                    String email = chat.getString("username");
                    String messageSenderId = chat.getString("message_sender_id");
                    list.add(new ChatModel(chatId,lastMessage,"false",name,toId,email,messageSenderId));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static ChatModel getChatModelJson(String response,String sellerName,String sellerId,String userId){
        ChatModel chatModel = null;
        if (Helper.isJSONValid(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                String chatId = jsonObject.getString("chat_id");
                chatModel = new ChatModel(chatId,null,null,sellerName,sellerId,null,userId);
                return chatModel;
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return chatModel;
    }

    public static ArrayList<MessageModel> getChatMessagesJson(String response,UserModel userModel) {
        ArrayList<MessageModel> list = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray messages = jsonObject.getJSONArray("chats");
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject message = messages.getJSONObject(i);
                    String chatId = message.getString("chat_id");
                    String messageText = message.getString("message");
                    String toId = message.getString("to_id");
                    String fromId = message.getString("from_id");
                    String date = message.getString("date");
                    if (userModel.getId().equals(fromId)) {
                        list.add(new MessageModel(chatId, toId, fromId, date, messageText,true));
                    } else {
                        list.add(new MessageModel(chatId, toId, fromId, date, messageText,false));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static ArrayList<SubCategoryModel> getSubCategoryJson(String response) {
        ArrayList<SubCategoryModel> list = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray subCategories = jsonObject.getJSONArray("subcategories");
                for (int i = 0; i < subCategories.length(); i++) {
                    ArrayList<SubCategoryModel> children = new ArrayList<>();
                    JSONObject subCategory = subCategories.getJSONObject(i);
                    String id = subCategory.getString("id");
                    String name = subCategory.getString("name");
                    JSONArray childrenJson = subCategory.optJSONArray("children");
                    if (childrenJson != null) {
                        for (int j = 0; j < childrenJson.length(); j++) {
                            JSONObject childrenJsonObject = childrenJson.getJSONObject(j);
                            String nameChildren = childrenJsonObject.getString("name");
                            String idChildren = childrenJsonObject.getString("id");
                            children.add(new SubCategoryModel(idChildren, nameChildren));
                        }
                    }
                    list.add(new SubCategoryModel(id,name,children));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static ProductModel getProductJson(String response) {
        ProductModel productModel = null;
        ArrayList<CommentModel> commentsArrayList = new ArrayList<>();
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                boolean favorite = jsonObject.getBoolean("favourite");
                JSONArray comments = jsonObject.optJSONArray("comments");
                if (comments != null) {
                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject comment = comments.getJSONObject(i);
                        String name = comment.getString("name");
                        String user = comment.getString("user");
                        String report = comment.getString("report");
                        String date = comment.getString("date");
                        String id = comment.getString("id");
                        commentsArrayList.add(new CommentModel(name, user, report, date,id));
                    }
                }
                JSONObject product = jsonObject.getJSONObject("product");
                String id = product.getString("id");
                String name = product.getString("name");
                String body = product.getString("body");
                String cityId = product.getString("city");
                String userId = product.getString("user_id");
                String mobile = product.getString("mobile");
                String created = product.getString("created");
                JSONArray imagesJson = product.getJSONArray("images");
                ArrayList<String> images = new ArrayList<>();
                for (int i = 0; i < imagesJson.length(); i++) {
                    String imageString = imagesJson.getString(i);
                    images.add(imageString);
                }
                String user = product.getString("user");
                String city = product.getString("city");
                productModel = new ProductModel(id, name, body, cityId, userId,  mobile, created, images, user, city, favorite, commentsArrayList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productModel;
    }


    public static NewDetailModel getNewJson(String response) {
        NewDetailModel newDetailModel = null;
        if (Helper.isJSONValid(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject product = jsonObject.getJSONObject("news");
                String id = product.getString("id");
                String name = product.getString("name");
                String body = product.getString("body");
                String image = product.getString("image");
                String categoryId = product.getString("category_id");
                String created = product.getString("created");
                String status = product.getString("status");
                String like = String.valueOf(jsonObject.getInt("like"));
                String disLike = String.valueOf(jsonObject.getInt("dislike"));
                String category = product.getString("category");
                newDetailModel = new NewDetailModel(id, name, body, image, categoryId, created, status, category,like,disLike);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newDetailModel;
    }


    private static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } else {
            return false;
        }
    }


    public void setMap(Map<String, String> mMap) {
        this.mMap = mMap;
    }

    public void setmDepartmentModel(DepartmentModel mDepartmentModel) {
        this.mDepartmentModel = mDepartmentModel;
    }

    public DepartmentModel getmDepartmentModel() {
        return mDepartmentModel;
    }
}
