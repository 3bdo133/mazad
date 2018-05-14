package mazad.mazad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.adapters.PresentedItemAdapter;
import mazad.mazad.adapters.RepliesAdapter;
import mazad.mazad.models.ChatModel;
import mazad.mazad.models.CommentModel;
import mazad.mazad.models.PresentedItemModel;
import mazad.mazad.models.ProductModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.back)
    TextView mBackButton;
    @BindView(R.id.replies)
    RecyclerView mItems;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.parent_layout_product)
    View mParentLayout;
    @BindView(R.id.toolbar_parent)
    View mParentToolbar;
    @BindView(R.id.person_name)
    TextView mPersonNameTextView;
    @BindView(R.id.location)
    TextView mLocationTextView;
    @BindView(R.id.time)
    TextView mTimeTextView;
    @BindView(R.id.body)
    TextView mBodyTextView;
    @BindView(R.id.mobile)
    TextView mMobileTextView;
    @BindView(R.id.name)
    TextView mNameTextView;
    @BindView(R.id.images_parent)
    LinearLayout mImagesParent;
    @BindView(R.id.parent_layout)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.add_reply_button)
    TextView mReplyButton;
    @BindView(R.id.reply)
    EditText mReplyEditText;
    @BindView(R.id.favorite)
    ImageView mFavorite;
    @BindView(R.id.detail_parent)
    View mDetailParent;
    @BindView(R.id.send_message)
    TextView mSendMessageButton;
    @BindView(R.id.share_whatsapp)
    ImageView mShareWhatsApp;
    @BindView(R.id.share_twitter)
    ImageView mShareTwitter;

    Connector mConnector;

    Connector mConnectorReply;

    Connector mConnectorAddLove;

    Connector mConnectorSendMessage;

    Connector mConnectorReportSpam;

    PresentedItemModel mPresentedItemModel;

    ArrayList<CommentModel> replyModels;

    Map<String, String> mMap;
    Map<String, String> mMapReply;
    Map<String, String> mMapLove;
    Map<String, String> mMapSendMessage;
    Map<String,String> mMapReportSpam;

    ProductModel mProductModel;
    UserModel mUserModel;
    ChatModel mChatModel = null;

    RepliesAdapter mRepliesAdapter;

    private final String TAG = "AdDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        Fresco.initialize(this);

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

        replyModels = new ArrayList<>();

        mConnectorReportSpam = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    Helper.showSnackBarMessage("تم بنجاح",AdDetailsActivity.this);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mParentToolbar.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mNestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",AdDetailsActivity.this);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mParentToolbar.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mParentLayout.setVisibility(View.VISIBLE);
                mParentToolbar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",AdDetailsActivity.this);
            }
        });

        mMapReportSpam = new HashMap<>();

        mRepliesAdapter = new RepliesAdapter(this, replyModels, new RepliesAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        }, new RepliesAdapter.OnSpamClick() {
            @Override
            public void setOnItemClick(int position) {
                if (mUserModel != null) {
                    mMapReportSpam.put("user_id", mUserModel.getId());
                    mMapReportSpam.put("comment_id", replyModels.get(position).getId());
                    mConnectorReportSpam.setMap(mMapReportSpam);
                    mParentLayout.setVisibility(View.INVISIBLE);
                    mParentToolbar.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mConnectorReportSpam.getRequest(TAG, Connector.createReportSpamUrl());
                } else {
                    Helper.showSnackBarMessage("من فضلك قم بتسجيل الدخول",AdDetailsActivity.this);
                }
            }
        });


        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    mProductModel = Connector.getProductJson(response);
                    setData();
                    mParentLayout.setVisibility(View.VISIBLE);
                    mParentToolbar.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mNestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",AdDetailsActivity.this);
                    mParentLayout.setVisibility(View.VISIBLE);
                    mParentToolbar.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mParentLayout.setVisibility(View.VISIBLE);
                mParentToolbar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",AdDetailsActivity.this);
            }
        });


        mMap = new HashMap<>();

        if (getIntent().hasExtra("product")) {
            mPresentedItemModel = (PresentedItemModel) getIntent().getSerializableExtra("product");
            mMap.put("id", mPresentedItemModel.getmId());
        }

        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null) {
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
        }

        if (mUserModel != null) {
            mMap.put("user_id", mUserModel.getId());
            Helper.writeToLog(mUserModel.getId());
        }

        mConnector.setMap(mMap);
        mParentLayout.setVisibility(View.INVISIBLE);
        mParentToolbar.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mConnector.getRequest(TAG, Connector.createGetProductUrl());


        mConnectorReply = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    Helper.showSnackBarMessage("تم اضافة الرد بنجاح", AdDetailsActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mReplyEditText.setText("");
                    mReplyButton.setEnabled(true);
                    mNestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                    mConnector.getRequest(TAG, Connector.createGetProductUrl());
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AdDetailsActivity.this);
                    mReplyButton.setEnabled(true);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mReplyButton.setEnabled(true);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AdDetailsActivity.this);
            }
        });


        mMapReply = new HashMap<>();

        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null) {
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
            mMapReply.put("product_id", mPresentedItemModel.getmId());
            mMapReply.put("user_id", mUserModel.getId());
        }

        mProgressBar.setVisibility(View.VISIBLE);


        mReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserModel != null) {
                    String comment = mReplyEditText.getText().toString();
                    if (!Helper.validateFields(comment)) {
                        Helper.showSnackBarMessage("من فضلك ادخل الرد", AdDetailsActivity.this);
                    } else {
                        mMapReply.put("comment", comment);
                        mConnectorReply.setMap(mMapReply);
                        v.setEnabled(false);
                        mConnectorReply.getRequest(TAG, Connector.createAddCommentUrl());
                    }
                } else {
                    Helper.showSnackBarMessage("من فضلك قم بتسجيل الدخول", AdDetailsActivity.this);
                }
            }
        });

        mItems.setAdapter(mRepliesAdapter);


        mItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        mItems.setFocusable(false);


        mNestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);


        mConnectorAddLove = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    if (!mProductModel.getFavorite()) {
                        mProductModel.setFavorite(true);
                        mFavorite.setImageResource(R.drawable.im_heart);
                        Helper.showSnackBarMessage("تم الاضافه الي المفضلة بنجاح", AdDetailsActivity.this);
                    } else {
                        mFavorite.setImageResource(R.mipmap.ic_love);
                        Helper.showSnackBarMessage("تم الحذف من المفضلة بنجاح", AdDetailsActivity.this);
                        mProductModel.setFavorite(false);
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mNestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AdDetailsActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AdDetailsActivity.this);
            }
        });


        mMapLove = new HashMap<>();


        mReplyEditText.clearFocus();
        mItems.setNestedScrollingEnabled(false);
        mNestedScrollView.smoothScrollBy(0, 0);

        mParentToolbar.requestFocus();

        mMapSendMessage = new HashMap<>();
        mConnectorSendMessage = new Connector(AdDetailsActivity.this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    mChatModel = Connector.getChatModelJson(response,mProductModel.getUser(),mProductModel.getUserId(),mUserModel.getId());
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chat",mChatModel);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                } else {
                    Helper.showSnackBarMessage("خطأ من فضلك اعد المحاوله", AdDetailsActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ من فضلك اعد المحاوله", AdDetailsActivity.this);
            }
        });

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserModel != null) {
                    if (!mUserModel.getId().equals(mProductModel.getUserId())) {
                        mMapSendMessage.put("message", "");
                        mMapSendMessage.put("user_id", mUserModel.getId());
                        mMapSendMessage.put("to_id", mProductModel.getUserId());
                        mConnectorSendMessage.setMap(mMapSendMessage);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mConnectorSendMessage.getRequest(TAG, Connector.createStartChatUrl());
                    } else {
                        Helper.showSnackBarMessage("لايمكنك مراسلة نفسك", AdDetailsActivity.this);
                    }
                } else {
                    Helper.showSnackBarMessage("برجاء تسجيل الدخول", AdDetailsActivity.this);
                }
            }
        });

        mMobileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(mMobileTextView.getText().toString());
            }
        });

        mShareWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                StringBuilder share = new StringBuilder(mProductModel.getName() + "\n" + mProductModel.getBody());
                for (int i = 0;i<mProductModel.getImages().size();i++){
                    share.append("\n").append(mProductModel.getImages().get(i));
                }
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, share.toString());
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AdDetailsActivity.this,"Whatsapp have not been installed.",Toast.LENGTH_LONG).show();
                }
            }
        });

        mShareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTwitter(mProductModel.getName() + "\n" + mProductModel.getBody());
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void loveClicked(View view) {
        if (mUserModel != null && mProductModel != null) {
            mMapLove.put("user_id", mUserModel.getId());
            mMapLove.put("product_id", mProductModel.getId());
            mConnectorAddLove.setMap(mMapLove);
            mConnectorAddLove.getRequest(TAG, Connector.createAddToFavoriteUrl());

        } else {
            Helper.showSnackBarMessage("من فضلك قم بتسجيل الدخول", AdDetailsActivity.this);
        }



    }


    private void setData() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        params.setMargins(5,50,5,5);

        mPersonNameTextView.setText(mProductModel.getUser());
        mBodyTextView.setText(mProductModel.getBody());
        mLocationTextView.setText(mProductModel.getCity());
        mMobileTextView.setText(mProductModel.getMobile());
        mNameTextView.setText(mProductModel.getName());
        mTimeTextView.setText(mProductModel.getCreated());
        for (int i = 0; i < mProductModel.getImages().size(); i++) {
            if (!mProductModel.getImages().get(i).equals("") && !mProductModel.getImages().get(i).equals(" ")) {
                final ArrayList<String> realImages = new ArrayList<>();
                String BASE_IMAGE_URL = "http://Mazad-sa.net/mazad/prod_img/";
                for (int j = 0; j < mProductModel.getImages().size(); j++){
                    realImages.add(BASE_IMAGE_URL + mProductModel.getImages().get(j));
                }
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(params);
                final ImageViewer.Builder builder = new ImageViewer.Builder(AdDetailsActivity.this, realImages);
                builder.setStartPosition(i);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.show();
                    }
                });
                imageView.setAdjustViewBounds(true);
                imageView.setContentDescription(getString(R.string.content_description));
                Picasso.get().load(BASE_IMAGE_URL + mProductModel.getImages().get(i)).into(imageView);
                mImagesParent.addView(imageView);
            }

        }
        if (mProductModel.getFavorite()) {
            mFavorite.setImageResource(R.drawable.im_heart);
        } else {
            mFavorite.setImageResource(R.mipmap.ic_love);
        }
        replyModels.clear();
        if (mProductModel.getComments() != null) {
            replyModels.addAll(mProductModel.getComments());
        }
        mRepliesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
        mConnectorReply.cancelAllRequests(TAG);
        mConnectorAddLove.cancelAllRequests(TAG);
        mConnectorReportSpam.cancelAllRequests(TAG);
        mConnectorSendMessage.cancelAllRequests(TAG);
    }


    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void shareTwitter(String message) {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(tweetIntent);
        } else {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, message);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(message)));
            startActivity(i);
            Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show();
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }

}
