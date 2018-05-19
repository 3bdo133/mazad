package mazad.mazad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;

import mazad.mazad.models.UserModel;

/**
 * Created by Abdelrahman Hesham on 3/9/2018.
 */

public class Helper {

    public static void showLongTimeToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

    public static void writeToLog(String message) {

        Log.i("Helper Log", message);

    }


    public static boolean validateEmail(String email) {

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean validateFields(String name) {

        return !TextUtils.isEmpty(name);
    }

    public static boolean validateMobile(String string) {

        return !(TextUtils.isEmpty(string) || string.length() != 10);
    }

    public static void showSnackBarMessage(String message, AppCompatActivity activity) {

        if (activity.findViewById(android.R.id.content) != null) {

            Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void hideKeyboard(AppCompatActivity activity,View v){
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }


    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    public static void SaveToSharedPreferences(Context context, UserModel userModel){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id",userModel.getId());
        editor.putString("email",userModel.getEmail());
        editor.putString("mobile",userModel.getMobile());
        editor.putString("name",userModel.getName());
        editor.putString("role",userModel.getRole());
        editor.apply();
    }

    public static void SavePasswordToSharedPreferences(Context context, String password){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password",password);
        editor.apply();
    }

    public static void SaveNotificationToSharedPreferences(Context context, boolean reminder){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("reminder",reminder);
        editor.apply();
    }

    public static void SaveTokenToSharedPreferences(Context context,String token){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token",token);
        editor.apply();
    }

    public static String getTokenFromSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("token","null");
    }

    public static String getPasswordSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String password = preferences.getString("password",null);
        return password;

    }


    public static boolean getNotificationSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean reminder = preferences.getBoolean("reminder",true);
        return reminder;
    }

    public static boolean PreferencesContainsUser(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains("id");
    }

    public static UserModel getUserSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String id = preferences.getString("id",null);
        String email = preferences.getString("email",null);
        String mobile = preferences.getString("mobile",null);
        String name = preferences.getString("name",null);
        String role = preferences.getString("role",null);
        return new UserModel(name,email,mobile,role,id);

    }

    public static void removeUserFromSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("id");
        editor.remove("email");
        editor.remove("mobile");
        editor.remove("name");
        editor.remove("role");
        editor.remove("password");
        editor.apply();
    }



}
