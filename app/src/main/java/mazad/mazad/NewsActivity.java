package mazad.mazad;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.adapters.NewsAdapter;
import mazad.mazad.models.NewModel;
import mazad.mazad.models.PresentedItemModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.items)
    RecyclerView mItems;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    Connector mConnector;

    private final String TAG = "NewsActivity";

    ArrayList<NewModel> mNews;

    UserModel mUserModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
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


        mNews = new ArrayList<>();

        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null){
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
        }

        final NewsAdapter newsAdapter = new NewsAdapter(this, mNews, new NewsAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                startActivity(new Intent(NewsActivity.this,NewDetailsActivity.class).putExtra("new",mNews.get(position)).putExtra("user",mUserModel));
            }
        });



        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mNews.addAll(Connector.getNewsJson(response));
                    newsAdapter.notifyDataSetChanged();
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",NewsActivity.this);
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mItems.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",NewsActivity.this);
            }
        });


        mItems.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mConnector.getRequest(TAG, Connector.createNewsUrl());


        mItems.setAdapter(newsAdapter);


        mItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }


    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
