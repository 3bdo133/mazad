package mazad.mazad;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.register_button)
    Button mRegisterButton;
    @BindView(R.id.user_name)
    EditText mUserNameEditText;
    @BindView(R.id.email)
    EditText mEmailEditText;
    @BindView(R.id.password)
    EditText mPasswordEditText;
    @BindView(R.id.mobile_number)
    EditText mMobileNumberEditText;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.root_layout)
    View mParentLayout;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.terms)
    TextView mTermsTextView;

    String mUserName;
    String mEmail;
    String mPassword;
    String mMobileNumber;

    Connector mConnector;
    Connector mConnectorEditProfile;

    UserModel mUserModel = null;

    Map<String,String> mMap;
    Map<String,String> mMapEditProfile;

    boolean login = false;

    private final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null){
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
            login = true;
            setData();
        } else {
            login = false;
        }


        mMap = new HashMap<>();


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!login) {
                    mEmail = mEmailEditText.getText().toString();
                    mPassword = mPasswordEditText.getText().toString();
                    mMobileNumber = mMobileNumberEditText.getText().toString();
                    mUserName = mUserNameEditText.getText().toString();

                    if (!Helper.validateEmail(mEmail) && !Helper.validateFields(mPassword) && !Helper.validateMobile(mMobileNumber) && !Helper.validateFields(mUserName))
                        Helper.showSnackBarMessage("من فضلك. ادخل بيانات صحيحة", RegisterActivity.this);
                    else if (!Helper.validateEmail(mEmail))
                        Helper.showSnackBarMessage("البريد الالكتروني يجب ان يكون صحيح", RegisterActivity.this);
                    else if (!Helper.validateFields(mPassword))
                        Helper.showSnackBarMessage("من فضلك. ادخل كلمة المرور", RegisterActivity.this);
                    else if (!Helper.validateMobile(mMobileNumber))
                        Helper.showSnackBarMessage("من فضلك. ادخل رقم الجوال يجب ان يكون 10 ارقام", RegisterActivity.this);
                    else if (!Helper.validateFields(mUserName))
                        Helper.showSnackBarMessage("من فضلك. ادخل اسم المستخدم", RegisterActivity.this);
                    else {
                        mMap.put("name", mUserName);
                        mMap.put("email", mEmail);
                        mMap.put("password", mPassword);
                        mMap.put("mobile", mMobileNumber);
                        Helper.hideKeyboard(RegisterActivity.this, v);
                        mConnector.setMap(mMap);
                        mParentLayout.setVisibility(View.INVISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mConnector.getRequest(TAG, Connector.createRegisterUrl());
                    }
                } else {
                    mEmail = mEmailEditText.getText().toString();
                    mMobileNumber = mMobileNumberEditText.getText().toString();
                    mUserName = mUserNameEditText.getText().toString();

                    if (!Helper.validateEmail(mEmail) && !Helper.validateMobile(mMobileNumber) && !Helper.validateFields(mUserName))
                        Helper.showSnackBarMessage("من فضلك. ادخل بيانات صحيحة", RegisterActivity.this);
                    else if (!Helper.validateEmail(mEmail))
                        Helper.showSnackBarMessage("البريد الالكتروني يجب ان يكون صحيح", RegisterActivity.this);
                    else if (!Helper.validateMobile(mMobileNumber))
                        Helper.showSnackBarMessage("من فضلك. ادخل رقم الجوال يجب ان يكون 10 ارقام", RegisterActivity.this);
                    else if (!Helper.validateFields(mUserName))
                        Helper.showSnackBarMessage("من فضلك. ادخل اسم المستخدم", RegisterActivity.this);
                    else {
                        mMapEditProfile.put("name", mUserName);
                        mMapEditProfile.put("email", mEmail);
                        mMapEditProfile.put("id",mUserModel.getId());
                        mMapEditProfile.put("mobile", mMobileNumber);
                        Helper.hideKeyboard(RegisterActivity.this, v);
                        mConnectorEditProfile.setMap(mMapEditProfile);
                        mParentLayout.setVisibility(View.INVISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mConnectorEditProfile.getRequest(TAG, Connector.createEditProfileUrl());
                    }
                }
            }
        });


        mConnector = new Connector(RegisterActivity.this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    clearTextView();
                    Helper.SaveToSharedPreferences(RegisterActivity.this,Connector.registerAndLoginJson(response));
                    startActivity(new Intent(RegisterActivity.this,HomeActivity.class).putExtra("user",Connector.registerAndLoginJson(response)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    Helper.showSnackBarMessage("البريد الالكتروني او رقم الجوال موجود مسبقا",RegisterActivity.this);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",RegisterActivity.this);
            }
        });


        mMapEditProfile = new HashMap<>();

        mConnectorEditProfile = new Connector(RegisterActivity.this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mUserModel.setEmail(mEmail);
                    mUserModel.setName(mUserName);
                    mUserModel.setMobile(mMobileNumber);
                    setData();
                    Helper.SaveToSharedPreferences(RegisterActivity.this,mUserModel);
                    Helper.showSnackBarMessage("تم التعديل بنجاح",RegisterActivity.this);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",RegisterActivity.this);
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
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",RegisterActivity.this);
            }
        });
    }

    private void clearTextView(){
        mEmailEditText.setText("");
        mMobileNumberEditText.setText("");
        mUserNameEditText.setText("");
        mPasswordEditText.setText("");
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
        mConnectorEditProfile.cancelAllRequests(TAG);
    }

    private void setData(){
        mUserNameEditText.setText(mUserModel.getName());
        mEmailEditText.setText(mUserModel.getEmail());
        mMobileNumberEditText.setText(mUserModel.getMobile());
        mPasswordEditText.setVisibility(View.GONE);
        mRegisterButton.setText("تعديل");
        mToolbarTitle.setText("حسابي الشخصي");
        mTermsTextView.setVisibility(View.GONE);
    }

}
