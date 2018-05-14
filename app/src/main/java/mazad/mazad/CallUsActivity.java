package mazad.mazad;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CallUsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.user_name)
    EditText mUserNameEditText;
    @BindView(R.id.send_button)
    Button mSendButton;
    @BindView(R.id.message_text)
    EditText mMessageEditText;

    UserModel mUserModel = null;

    Connector mConnector;

    Map<String, String> mMap;

    private final String TAG = "CallUsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_us);
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


        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null) {
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
            mUserNameEditText.setText(mUserModel.getName());
            mUserNameEditText.setEnabled(false);
        }

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    Helper.showSnackBarMessage("تم الارسال بنجاح", CallUsActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSendButton.setEnabled(true);
                    mMessageEditText.setText("");
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", CallUsActivity.this);
                    mSendButton.setEnabled(true);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mSendButton.setEnabled(true);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", CallUsActivity.this);
            }
        });


        mMap = new HashMap<>();
        if (mUserModel != null) {
            mMap.put("user_id", mUserModel.getId());
        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();
                if (mUserModel == null) {
                    Helper.showSnackBarMessage("قم بتسجيل الدخول", CallUsActivity.this);
                } else if (!Helper.validateFields(message)) {
                    Helper.showSnackBarMessage("ادخل الرسالة", CallUsActivity.this);
                } else {
                    mMap.put("comment", message);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mConnector.setMap(mMap);
                    v.setEnabled(false);
                    mConnector.getRequest(TAG, Connector.createAddFeedBackUrl());
                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }
}
