package mazad.mazad;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.lang.reflect.Method;
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
    @BindView(R.id.images_parent)
    LinearLayout mImagesParent;
    @BindView(R.id.scroll_parent)
    ScrollView mScrollView;

    Connector mConnector;
    Connector mConnectorAddLike;

    NewModel mNewModel;

    NewDetailModel mNewDetailModel;

    Map<String,String> mMap;
    Map<String,String> mMapAddLike;

    UserModel mUserModel;

    WebView mWebView;

    private boolean mIsPaused = false;

    private final String TAG = "NewDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_details);
        ButterKnife.bind(this);
        mWebView = findViewById(R.id.webView);
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        params.setMargins(0,15,0,0);
        mBodyTextView.setText(mNewDetailModel.getBody());
        mTileTextView.setText(mNewDetailModel.getName());
        mTimeTextView.setText(mNewDetailModel.getCreated());
        mToolbarTitleTextView.setText(mNewDetailModel.getName());
        mLikeTextView.setText(mNewDetailModel.getLike());
        mDisLikeTextView.setText(mNewDetailModel.getDisLike());
        String BASE_IMAGE_URL = "http://Mazad-sa.net/mazad/new_img/";
        if (!mNewDetailModel.getMainImage().equals("")) {
            Picasso.get().load(BASE_IMAGE_URL + mNewDetailModel.getMainImage()).into(mNewImage);
        } else {
            mNewImage.setVisibility(View.GONE);
        }
        final ArrayList<String> realImages = new ArrayList<>();
        realImages.add(BASE_IMAGE_URL + mNewDetailModel.getMainImage());
        for (int i =0;i<mNewDetailModel.getImages().size();i++){
            RoundedImageView roundedImageView = new RoundedImageView(this);
            roundedImageView.setCornerRadius(15);
            roundedImageView.setLayoutParams(params);
            roundedImageView.setBorderWidth((float) 10.0);
            roundedImageView.setFocusable(false);
            roundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            roundedImageView.setBorderColor(Color.parseColor("#9e9e9e"));

            Picasso.get().load(BASE_IMAGE_URL + mNewDetailModel.getImages().get(i)).into(roundedImageView);
            realImages.add(BASE_IMAGE_URL + mNewDetailModel.getImages().get(i));
            final ImageViewer.Builder builder = new ImageViewer.Builder(NewDetailsActivity.this, realImages);
            builder.setStartPosition(i+1);
            roundedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.show();
                }
            });
            mImagesParent.addView(roundedImageView);
        }
        mNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(NewDetailsActivity.this, realImages)
                        .setStartPosition(0)
                        .show();
            }
        });


        Helper.writeToLog(mNewDetailModel.getVideo());
        if (!mNewDetailModel.getVideo().trim().equals("")){
            showVideo();
        } else {
            mWebView.setVisibility(View.GONE);
        }

        mScrollView.fullScroll(ScrollView.FOCUS_UP);
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


    public void showVideo(){

        String media_url = "http://www.youtube.com/embed/"+mNewDetailModel.getVideo();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels-900;
        int width = displayMetrics.widthPixels-430;
        mWebView.getSettings().setJavaScriptEnabled(true);
        //String playVideo= "<html><body><iframe class=\"youtube-player\" type=\"text/html\" width=\"640\" height=\"360\" src=\"http://www.youtube.com/embed/"+mNewDetailModel.getVideo()+"\" frameborder=\"0\"></body></html>";
        String playVideo= "<style>\n" +
                ".video-container { \n" +
                "position: relative; \n" +
                "padding-bottom: 56.25%; \n" +
                "padding-top: 35px; \n" +
                "height: 0; \n" +
                "overflow: hidden; \n" +
                "}\n" +
                ".video-container iframe { \n" +
                "position: absolute; \n" +
                "top:0; \n" +
                "left: 0; \n" +
                "width: 100%; \n" +
                "height: 100%; \n" +
                "}\n" +
                "</style>\n" +
                "<div class=\"video-container\">\n" +
                "    <iframe src=\"http://www.youtube.com/embed/"+mNewDetailModel.getVideo()+"\" allowfullscreen=\"\" frameborder=\"0\">\n" +
                "    </iframe>\n" +
                "</div>";

        mWebView.loadData(playVideo, "text/html", "utf-8");
        mWebView.setFocusable(false);
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    //<iframe width="640" height="360" src="https://www.youtube.com/embed/kgXvebvaD6k" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>



}
