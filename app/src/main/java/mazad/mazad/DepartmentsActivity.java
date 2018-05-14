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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.adapters.DepartmentAdapter;
import mazad.mazad.adapters.NewsAdapter;
import mazad.mazad.models.DepartmentModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DepartmentsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.departments)
    RecyclerView mDepartments;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    Connector mConnector;

    UserModel mUserModel = null;

    ArrayList<DepartmentModel> mDepartmentModels;

    private static final String TAG = "DepartmentsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departements);


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

        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null){
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDepartmentModels = new ArrayList<>();

        final DepartmentAdapter adapter = new DepartmentAdapter(this, mDepartmentModels, new DepartmentAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                startActivity(new Intent(DepartmentsActivity.this,AddAdvertisingActivity.class).putExtra("category",mDepartmentModels.get(position)).putExtra("user",mUserModel));
            }
        });



        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mDepartmentModels.addAll(Connector.getDepartmentsJson(response));
                    adapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mDepartments.setVisibility(View.VISIBLE);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",DepartmentsActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mDepartments.setVisibility(View.VISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mDepartments.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",DepartmentsActivity.this);
            }
        });





        mDepartments.setAdapter(adapter);


        mDepartments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));



        mDepartments.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mConnector.getRequest(TAG, Connector.createGetCategoryUrl());








    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop(){
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }
}
