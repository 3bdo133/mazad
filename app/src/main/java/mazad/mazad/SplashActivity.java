package mazad.mazad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;

public class SplashActivity extends AppCompatActivity {

    UserModel mUserModel = null;
    Connector mConnector;
    Map<String,String> mMap;

    private final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Helper.PreferencesContainsUser(SplashActivity.this)){
            mUserModel = Helper.getUserSharedPreferences(SplashActivity.this);
        }

        mMap = new HashMap<>();




        mConnector = new Connector(SplashActivity.this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    Helper.SaveToSharedPreferences(SplashActivity.this,Connector.registerAndLoginJson(response));
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class).putExtra("user",Connector.registerAndLoginJson(response)));
                    finish();
                    } else {
                    Helper.showSnackBarMessage("خطأ بالبريد الالكتروني او كلمة المرور",SplashActivity.this);
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                    finish();
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                Helper.showSnackBarMessage("لا يوجد اتصال بالانترنت",SplashActivity.this);

            }
        });

        if (mUserModel != null) {
            mMap.put("username",mUserModel.getEmail());
            mMap.put("password",Helper.getPasswordSharedPreferences(SplashActivity.this));
            mMap.put("token",Helper.getTokenFromSharedPreferences(SplashActivity.this));
            mConnector.setMap(mMap);
            mConnector.getRequest(TAG, Connector.createLoginUrl());
        } else {
            startActivity(new Intent(SplashActivity.this,HomeActivity.class));
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }
}
