package mazad.mazad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.midi.MidiManager;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.adapters.PresentedItemAdapter;
import mazad.mazad.adapters.SpecializedSearchAdapter;
import mazad.mazad.models.CityModel;
import mazad.mazad.models.DepartmentModel;
import mazad.mazad.models.PresentedItemModel;
import mazad.mazad.models.SearchModel;
import mazad.mazad.models.SubCategoryModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SpecializedSearchActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.items)
    RecyclerView mItems;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.search_items)
    RecyclerView mSearchItems;

    ArrayList<SearchModel> mSearchModels;
    ArrayList<DepartmentModel> mDepartmentModels;

    Connector mConnectorGetProducts;

    ArrayList<PresentedItemModel> mPresentedItemModels;

    Connector mConnector;

    UserModel mUserModel = null;

    Intent mIntent;

    SpecializedSearchAdapter adapter;

    Map<String,String> mMap;

    boolean back = false;


    private static final String TAG = "SpecializedSearchActivity";

    private final int REQUEST_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialized_search);

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

        try {
            mIntent = getIntent();
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }


        if (mIntent != null){
            if (mIntent.hasExtra("user") && mIntent.getSerializableExtra("user") != null){
                mUserModel = (UserModel) mIntent.getSerializableExtra("user");
            }
        }


        mPresentedItemModels = new ArrayList<>();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (back){
                    mItems.setVisibility(View.INVISIBLE);
                    mSearchItems.setVisibility(View.VISIBLE);
                    back = false;
                } else {
                    finish();
                }
            }
        });


        mSearchModels = new ArrayList<>();
        mDepartmentModels = new ArrayList<>();

        adapter = new SpecializedSearchAdapter(this, mSearchModels, new SpecializedSearchAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {

            }
        }, new SpecializedSearchAdapter.OnSearchClick() {

            @Override
            public void setOnSearchClick(int position, int selectedCategory, int selectedChildren, boolean isChecked, String modelSelected, CityModel cityModel) {
                if (cityModel.getName().equals("المدينة")) {
                    Helper.showSnackBarMessage("من فضلك اختار المدينه", SpecializedSearchActivity.this);

                } else if (modelSelected.equals("الموديل")){
                    Helper.showSnackBarMessage("من فضلك اختار الموديل", SpecializedSearchActivity.this);

                } else {
                    if (selectedCategory != -1) {
                        mSearchModels = adapter.getItems();
                        Helper.writeToLog(mSearchModels.get(position).getDepartmentModel().getName());
                        Helper.writeToLog(mSearchModels.get(position).getSubCategoryModels().get(selectedCategory).getName());
                        Helper.writeToLog(cityModel.getName());
                        Helper.writeToLog(modelSelected);
                        if (!mSearchModels.get(position).getSubCategoryModels().get(selectedCategory).getChildren().isEmpty() && isChecked) {
                            Helper.writeToLog(mSearchModels.get(position).getSubCategoryModels().get(selectedCategory).getChildren().get(selectedChildren).getName());
                            Map<String, String> map = new HashMap<>();
                            map.put("category_id[0]", mSearchModels.get(position).getDepartmentModel().getId());
                            map.put("subcategory_id[0]", mSearchModels.get(position).getSubCategoryModels().get(selectedCategory).getId());
                            map.put("subcategory_id[1]", mSearchModels.get(position).getSubCategoryModels().get(selectedCategory).getChildren().get(selectedChildren).getId());
                            map.put("city_id",cityModel.getId());
                            if (!modelSelected.equals("-1")){
                                map.put("model_id",modelSelected);
                            }
                            mConnectorGetProducts.setMap(map);
                            Helper.writeToLog("true");
                            back = true;
                            mSearchItems.setVisibility(View.INVISIBLE);
                            mProgressBar.setVisibility(View.VISIBLE);
                            mConnectorGetProducts.getRequest(TAG, Connector.createGetProductsUrl());
                        } else {
                            Map<String, String> map = new HashMap<>();
                            map.put("category_id[0]", mSearchModels.get(position).getDepartmentModel().getId());
                            map.put("city_id",cityModel.getId());
                            if (!modelSelected.equals("-1")){
                                map.put("model_id",modelSelected);
                            }
                            map.put("subcategory_id[0]", mSearchModels.get(position).getSubCategoryModels().get(selectedCategory).getId());
                            //map.put("subcategory[1]",mSearchModels.get(position).getSubCategoryModels().get(selectedCategory).getChildren().get(selectedChildren).getId());
                            mConnectorGetProducts.setMap(map);
                            back = true;
                            mSearchItems.setVisibility(View.INVISIBLE);
                            mProgressBar.setVisibility(View.VISIBLE);
                            mConnectorGetProducts.getRequest(TAG, Connector.createGetProductsUrl());
                        }


                    } else {
                        Helper.showSnackBarMessage("من فضلك اختار القسم الفرعي", SpecializedSearchActivity.this);
                    }
                }
            }
        });

        mSearchItems.setAdapter(adapter);


        mSearchItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mMap = new HashMap<>();

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mDepartmentModels.addAll(Connector.getDepartmentsJson(response));
                    for (int i = 0 ;i < mDepartmentModels.size();i++){
                        mSearchModels.add(new SearchModel(mDepartmentModels.get(i),new ArrayList<SubCategoryModel>()));
                    }
                    adapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSearchItems.setVisibility(View.VISIBLE);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",SpecializedSearchActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSearchItems.setVisibility(View.VISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mSearchItems.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",SpecializedSearchActivity.this);
            }
        });


        mSearchItems.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mConnector.getRequest(TAG, Connector.createGetCategoryUrl());


        mPresentedItemModels = new ArrayList<>();


        final PresentedItemAdapter adapter = new PresentedItemAdapter(SpecializedSearchActivity.this, mPresentedItemModels, new PresentedItemAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                startActivityForResult(new Intent(SpecializedSearchActivity.this, AdDetailsActivity.class).putExtra("product",mPresentedItemModels.get(position)).putExtra("user",mUserModel),REQUEST_ID);
            }
        });


        mConnectorGetProducts = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mPresentedItemModels.clear();
                    mPresentedItemModels.addAll(Connector.getProductsJson(response));
                    adapter.notifyDataSetChanged();
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mPresentedItemModels.clear();
                    adapter.notifyDataSetChanged();
                    Helper.showSnackBarMessage("لا يوجد نتائج",SpecializedSearchActivity.this);
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mItems.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",SpecializedSearchActivity.this);
            }
        });


        mItems.setAdapter(adapter);


        mItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));



    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onBackPressed() {
        if (back){
            mItems.setVisibility(View.INVISIBLE);
            mSearchItems.setVisibility(View.VISIBLE);
            back = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK){
            if (data.hasExtra("chat") && data.getSerializableExtra("chat") != null){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chat",data.getSerializableExtra("chat"));
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
        mConnectorGetProducts.cancelAllRequests(TAG);
    }
}
