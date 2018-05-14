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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.models.NewDetailModel;
import mazad.mazad.models.NewModel;
import mazad.mazad.models.PresentedItemModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.parent_layout_new)
    View mParentLayout;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitleTextView;
    @BindView(R.id.description)
    TextView mTileTextView;
    @BindView(R.id.body)
    TextView mBodyTextView;
    @BindView(R.id.time)
    TextView mTimeTextView;
    @BindView(R.id.image)
    ImageView mNewImage;
    @BindView(R.id.like_text)
    TextView mLikeTextView;
    @BindView(R.id.dislike)
    TextView mDisLikeTextView;

    Connector mConnector;
    Connector mConnectorAddLike;

    NewModel mNewModel;

    NewDetailModel mNewDetailModel;

    Map<String,String> mMap;
    Map<String,String> mMapAddLike;

    UserModel mUserModel;

    private final String TAG = "NewDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_details);
        ButterKnife.bind(this);
        Fresco.initialize(this);

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
                NavUtils.navigateUpFromSameTask(NewDetailsActivity.this);
            }
        });


        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mNewDetailModel = Connector.getNewJson(response);
                    setData();
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    //mNestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",NewDetailsActivity.this);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",NewDetailsActivity.this);
            }
        });


        mMap = new HashMap<>();

        if (getIntent().hasExtra("new")){
            mNewModel = (NewModel) getIntent().getSerializableExtra("new");
            mMap.put("id",mNewModel.getId());
        }


        mConnector.setMap(mMap);
        mParentLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mConnector.getRequest(TAG, Connector.createGetNewUrl());


        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null){
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
        }

        mMapAddLike = new HashMap<>();

        mConnectorAddLike = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    Helper.showSnackBarMessage("شكرا علي تقييمك",NewDetailsActivity.this);
                    mConnector.getRequest(TAG,Connector.createGetNewUrl());
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",NewDetailsActivity.this);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mParentLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",NewDetailsActivity.this);
            }
        });

    }

    private void setData(){
        mBodyTextView.setText(mNewDetailModel.getBody());
        mTileTextView.setText(mNewDetailModel.getName());
        mTimeTextView.setText(mNewDetailModel.getCreated());
        mToolbarTitleTextView.setText(mNewDetailModel.getName());
        mLikeTextView.setText(mNewDetailModel.getLike());
        mDisLikeTextView.setText(mNewDetailModel.getDisLike());
        String BASE_IMAGE_URL = "http://Mazad-sa.net/mazad/new_img/";
        Picasso.get().load(BASE_IMAGE_URL + mNewDetailModel.getImage()).into(mNewImage);
        final ArrayList<String> realImages = new ArrayList<>();
        realImages.add(BASE_IMAGE_URL + mNewDetailModel.getImage());
        mNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(NewDetailsActivity.this, realImages)
                        .setStartPosition(0)
                        .show();
            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop(){
        super.onStop();
        mConnector.cancelAllRequests(TAG);
        mConnectorAddLike.cancelAllRequests(TAG);
    }

    public void react(View view) {
        int id = view.getId();
        if (id == R.id.like){
            if (mUserModel == null){
                Helper.showSnackBarMessage("برجاء تسجيل الدخول",NewDetailsActivity.this);
            } else {
                mMapAddLike.put("news_id",mNewDetailModel.getId());
                mMapAddLike.put("status","1");
                mMapAddLike.put("user_id",mUserModel.getId());
                mConnectorAddLike.setMap(mMapAddLike);
                mProgressBar.setVisibility(View.VISIBLE);
                mConnectorAddLike.getRequest(TAG, Connector.createAddLikeNewUrl());
            }
        } else {
            if (mUserModel == null){
                Helper.showSnackBarMessage("برجاء تسجيل الدخول",NewDetailsActivity.this);
            } else {
                mMapAddLike.put("news_id",mNewDetailModel.getId());
                mMapAddLike.put("status","0");
                mMapAddLike.put("user_id",mUserModel.getId());
                mConnectorAddLike.setMap(mMapAddLike);
                mProgressBar.setVisibility(View.VISIBLE);
                mConnectorAddLike.getRequest(TAG, Connector.createAddLikeNewUrl());
            }
        }
    }


}
