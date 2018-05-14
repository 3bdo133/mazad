package mazad.mazad;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.user_name)
    EditText mEmailEditText;
    @BindView(R.id.password)
    EditText mPasswordEditText;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.root_layout)
    View mParentLayout;

    Connector mConnector;

    String mEmail;
    String mPassword;

    Map<String,String> mMap;

    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ViewCompat.setLayoutDirection(findViewById(R.id.parent_layout), ViewCompat.LAYOUT_DIRECTION_RTL);
        }


        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mMap = new HashMap<>();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mEmailEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                if (!Helper.validateEmail(mEmail) && !Helper.validateFields(mPassword))
                    Helper.showSnackBarMessage("من فضلك. ادخل بيانات صحيحة",LoginActivity.this);
                else if (!Helper.validateFields(mPassword))
                    Helper.showSnackBarMessage("من فضلك. ادخل كلمة المرور",LoginActivity.this);
                else if (!Helper.validateEmail(mEmail))
                    Helper.showSnackBarMessage("البريد الالكتروني يجب ان يكون صحيح",LoginActivity.this);
                else {
                    String token = Helper.getTokenFromSharedPreferences(LoginActivity.this);
                    mMap.put("username",mEmail);
                    mMap.put("password",mPassword);
                    mMap.put("token",token);
                    Helper.writeToLog(Helper.getTokenFromSharedPreferences(LoginActivity.this));
                    Helper.hideKeyboard(LoginActivity.this,v);
                    mConnector.setMap(mMap);
                    mParentLayout.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mConnector.getRequest(TAG, Connector.createLoginUrl());
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(LoginActivity.this);
            }
        });


        mConnector = new Connector(LoginActivity.this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    clearTextView();
                    Helper.SaveToSharedPreferences(LoginActivity.this,Connector.registerAndLoginJson(response));
                    Helper.SavePasswordToSharedPreferences(LoginActivity.this,mPassword);
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class).putExtra("user",Connector.registerAndLoginJson(response)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Helper.showSnackBarMessage(Connector.getMessage(response),LoginActivity.this);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",LoginActivity.this);
            }
        });



    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    private void clearTextView(){
        mEmailEditText.setText("");
        mPasswordEditText.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }

    public void forgetPassword(View view) {
        startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
    }
}
