package mazad.mazad;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Path;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.models.CityModel;
import mazad.mazad.models.DepartmentModel;
import mazad.mazad.models.SubCategoryModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import mazad.mazad.utils.PathUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddAdvertisingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.add_photo)
    ImageView mAddPhotoButton;
    @BindView(R.id.department)
    EditText mDepartment;
    @BindView(R.id.images_parent)
    LinearLayout mImagesParent;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.parent_layout_ad)
    View mParentLayout;
    @BindView(R.id.continue_button)
    Button mContinueButton;
    @BindView(R.id.city_spinner)
    Spinner mCitySpinner;
    @BindView(R.id.sub_category_spinner)
    Spinner mSubCategorySpinner;
    @BindView(R.id.sub_sub_category_spinner)
    Spinner mSubSubCategorySpinner;
    @BindView(R.id.model_spinner)
    Spinner mModelSpinner;
    @BindView(R.id.title)
    EditText mAdTitleEditText;
    @BindView(R.id.body)
    EditText mAdBodyEditText;
    @BindView(R.id.mobile)
    EditText mAdMobileEditText;
    @BindView(R.id.terms)
    CheckBox mTermsCheckBox;


    String mAdTitle;
    String mAdBody;
    String mAdMobile;

    ArrayList<String> mImagesStrings;

    Map<String, String> mImageMap;

    Bitmap mBitmap;

    Thread thread;

    Connector mImageConnector;
    Connector mCityConnector;
    Connector mSubCategoryConnector;
    Connector mConnector;

    DepartmentModel mDepartmentModel;

    Map<String, String> mMap;

    UserModel mUserModel;

    ArrayList<CityModel> cityModels;
    ArrayList<SubCategoryModel> subCategoryModels;
    ArrayList<SubCategoryModel> subSubCategoryModels;

    SubCategoryModel mSubCategoryModelSelected = null;
    SubCategoryModel mSubSubCategoryModelSelected = null;
    CityModel mCityModelSelected = null;

    String mModelSelected = "الموديل";

    static final int REQUEST_IMAGE_GET = 1;
    static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 2;


    private static final String TAG = "AddAdvertisingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advertising);

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

        mAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (getIntent().hasExtra("user") && getIntent().getSerializableExtra("user") != null) {
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
        }


        if (getIntent().hasExtra("category") && getIntent().getSerializableExtra("category") != null) {
            mDepartmentModel = (DepartmentModel) getIntent().getSerializableExtra("category");
        }

        mDepartment.setText(mDepartmentModel.getName());
        mDepartment.setEnabled(false);

        mImagesStrings = new ArrayList<>();

        mImageConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkImages(response)) {
                    ArrayList<String> imagePaths = Connector.getImages(response);
                    for (int i = 0; i < imagePaths.size(); i++) {
                        mMap.put("images[" + i + "]", imagePaths.get(i));
                    }
                    mConnector.getRequest(TAG, Connector.createAddProductUrl());
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mParentLayout.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
            }
        });


        mImageMap = new HashMap<>();


        cityModels = new ArrayList<>();

        final ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());


        cityModels.add(new CityModel("-1", "المدينة"));

        adapter.add(cityModels.get(0).getName());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCitySpinner.setAdapter(adapter);

        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCityModelSelected = cityModels.get(position);
                Helper.writeToLog(mCityModelSelected.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mCityConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    cityModels.addAll(Connector.getCitiesJson(response));
                    for (int i = 1; i < cityModels.size(); i++) {
                        adapter.add(cityModels.get(i).getName());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mParentLayout.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
            }
        });


        subCategoryModels = new ArrayList<>();
        subSubCategoryModels = new ArrayList<>();

        final ArrayAdapter<CharSequence> subCategoryAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());


        subCategoryModels.add(new SubCategoryModel("-1", "القسم الفرعي"));

        subSubCategoryModels.add(new SubCategoryModel("-1", "القسم الفرعي الثاني"));

        subCategoryAdapter.add(subCategoryModels.get(0).getName());

        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSubCategorySpinner.setAdapter(subCategoryAdapter);


        final ArrayAdapter<CharSequence> subSubCategoryAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());

        subSubCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        subSubCategoryAdapter.add(subSubCategoryModels.get(0).getName());

        mSubSubCategorySpinner.setAdapter(subSubCategoryAdapter);

        mSubCategoryConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    subCategoryModels.addAll(Connector.getSubCategoryJson(response));
                    for (int i = 1; i < subCategoryModels.size(); i++) {
                        subCategoryAdapter.add(subCategoryModels.get(i).getName());
                    }
                    subCategoryAdapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mParentLayout.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
            }
        });


        mParentLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mCityConnector.getRequest(TAG, Connector.createGetCitiesUrl());


        Map<String, String> map = new HashMap<>();

        map.put("category_id", mDepartmentModel.getId());

        mSubCategoryConnector.setMap(map);
        mSubCategoryConnector.getRequest(TAG, Connector.createGetSubCategoriesUrl());

        final ArrayAdapter<CharSequence> adapterModel =
                new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());

        if (mDepartment.getText().toString().equals("سيارات")) {
            adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterModel.add("الموديل");
            for (int i = 1970; i <= 2018; i++) {
                adapterModel.add(String.valueOf(i));
            }
            mModelSpinner.setAdapter(adapterModel);
            mModelSpinner.setVisibility(View.VISIBLE);
        } else {
            mModelSpinner.setVisibility(View.GONE);
        }


        mSubCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubSubCategorySpinner.setVisibility(View.GONE);
                mSubCategoryModelSelected = subCategoryModels.get(position);
                if (subCategoryModels.get(position).getChildren() != null) {
                    if (!subCategoryModels.get(position).getChildren().isEmpty()) {
                        subSubCategoryModels.clear();
                        subSubCategoryAdapter.clear();
                        subSubCategoryModels.add(new SubCategoryModel("-1", "القسم الفرعي الثاني"));
                        subSubCategoryModels.addAll(subCategoryModels.get(position).getChildren());
                        subSubCategoryAdapter.notifyDataSetChanged();
                        subSubCategoryAdapter.add(subSubCategoryModels.get(0).getName());
                        for (int i = 0; i < subCategoryModels.get(position).getChildren().size(); i++) {
                            subSubCategoryAdapter.add(subCategoryModels.get(position).getChildren().get(i).getName());
                        }
                        mSubSubCategorySpinner.setVisibility(View.VISIBLE);

                    }
                    Helper.writeToLog(mSubCategoryModelSelected.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mModelSelected = (String) adapterModel.getItem(position);
                Helper.writeToLog(mModelSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSubSubCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubSubCategoryModelSelected = subSubCategoryModels.get(position);
                Helper.writeToLog(mSubSubCategoryModelSelected.getId() + mSubSubCategoryModelSelected.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (response.contains("true")) {
                    Helper.showSnackBarMessage("تم اضافة الاعلان بنجاح", AddAdvertisingActivity.this);
                    startActivity(new Intent(AddAdvertisingActivity.this, MyAdsActivity.class).putExtra("user", mUserModel));
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                    clearData();
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mParentLayout.setVisibility(View.VISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mParentLayout.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
            }
        });


        mMap = new HashMap<>();


        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(AddAdvertisingActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddAdvertisingActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Helper.showSnackBarMessage("من فضلك وافق علي تصريح الوصول للملفات", AddAdvertisingActivity.this);
                        ActivityCompat.requestPermissions(AddAdvertisingActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_STORAGE);
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(AddAdvertisingActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    boolean visible = false;
                    boolean visible2 = false;
                    mAdTitle = mAdTitleEditText.getText().toString();
                    mAdBody = mAdBodyEditText.getText().toString();
                    mAdMobile = mAdMobileEditText.getText().toString();
                    if (mSubSubCategorySpinner.getVisibility() == View.VISIBLE) {
                        if (mSubSubCategoryModelSelected.getId().equals("-1"))
                            visible = false;
                        else
                            visible = true;
                    } else {
                        visible = true;
                    }

                    if (mModelSpinner.getVisibility() == View.VISIBLE) {
                        if (mModelSelected.equals("الموديل"))
                            visible2 = false;
                        else
                            visible2 = true;
                    } else {
                        visible2 = true;
                    }

                    if (!Helper.validateFields(mAdTitle) && !Helper.validateFields(mAdBody) && !Helper.validateMobile(mAdMobile) && mSubCategoryModelSelected.getId().equals("-1") && mCityModelSelected.getId().equals("-1") && !mTermsCheckBox.isChecked() && mImagesStrings.isEmpty()) {
                        Helper.showSnackBarMessage("ادخل البيانات المطلوبه", AddAdvertisingActivity.this);
                    } else if (!mTermsCheckBox.isChecked()) {
                        Helper.showSnackBarMessage("من فضلك وافق علي التعهد", AddAdvertisingActivity.this);
                    } else if (!Helper.validateFields(mAdTitle)) {
                        Helper.showSnackBarMessage("من فضلك ادخل عنوان الاعلان", AddAdvertisingActivity.this);
                    } else if (!Helper.validateFields(mAdBody)) {
                        Helper.showSnackBarMessage("من فضلك ادخل نص الاعلان", AddAdvertisingActivity.this);
                    } else if (TextUtils.isEmpty(mAdMobile)) {
                        Helper.showSnackBarMessage("من فضلك ادخل  وسيلة الاتصال", AddAdvertisingActivity.this);
                    } else if (mSubCategoryModelSelected.getId().equals("-1")) {
                        Helper.showSnackBarMessage("من فضلك اختار القسم الفرعي", AddAdvertisingActivity.this);
                    } else if (!visible) {
                        Helper.showSnackBarMessage("من فضلك اختار القسم الفرعي الثاني", AddAdvertisingActivity.this);
                    } else if (!visible2) {
                        Helper.showSnackBarMessage("من فضلك اختار الموديل", AddAdvertisingActivity.this);
                    }
                    else if (mCityModelSelected.getId().equals("-1")) {
                        Helper.showSnackBarMessage("من فضلك اختار المدينه", AddAdvertisingActivity.this);
                    } else if (mImagesStrings.isEmpty()) {
                        Helper.showSnackBarMessage("من ادخل صورة واحده للاعلان علي الاقل", AddAdvertisingActivity.this);
                    } else {
                        mMap.put("name", mAdTitle);
                        mMap.put("subcategory_id", mSubCategoryModelSelected.getId());
                        mMap.put("mobile", mAdMobile);
                        mMap.put("body", mAdBody);
                        mMap.put("user_id", mUserModel.getId());
                        mMap.put("category_id", mDepartmentModel.getId());
                        mMap.put("model_id",mModelSelected);
                        if (mSubSubCategorySpinner.getVisibility() == View.VISIBLE) {
                            mMap.put("subsubcategory_id", mSubSubCategoryModelSelected.getId());
                        }
                        mMap.put("city_id", mCityModelSelected.getId());
                        mConnector.setMap(mMap);
                        for (int i = 0; i < mImagesStrings.size(); i++) {
                            mImageMap.put("image[" + i + "]", "data:image/jpeg;base64," + mImagesStrings.get(i));
                        }
                        mParentLayout.setVisibility(View.INVISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        Builders.Any.B b = Ion.with(AddAdvertisingActivity.this)
                                .load(Connector.createUploadImageUrl());
                        for (int i = 0; i < mImagesStrings.size(); i++) {
                            String extension = mImagesStrings.get(i).substring(mImagesStrings.get(i).lastIndexOf(".") + 1);
                            b.setMultipartFile("parameters[" + i + "]", "image/" + extension, new File(mImagesStrings.get(i)));
                        }
                        b.asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        Helper.writeToLog(result);
                                        if (Connector.checkImages(result)) {
                                            ArrayList<String> imagePaths = Connector.getImages(result);
                                            for (int i = 0; i < imagePaths.size(); i++) {
                                                mMap.put("images[" + i + "]", imagePaths.get(i));
                                            }
                                            mConnector.getRequest(TAG, Connector.createAddProductUrl());
                                        } else {
                                            Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            mParentLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            final Uri fullPhotoUri = data.getData();


            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    170
            );
            params.setMargins(0, 10, 0, 0);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImagesParent.removeView(v);
                    try {
                        mImagesStrings.remove(PathUtil.getPath(AddAdvertisingActivity.this, fullPhotoUri));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });
            imageView.setContentDescription(getString(R.string.content_description));
            imageView.setImageURI(fullPhotoUri);
            mImagesParent.addView(imageView);
            try {
                mImagesStrings.add(PathUtil.getPath(AddAdvertisingActivity.this, fullPhotoUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }


    private void clearData() {
        mAdBodyEditText.setText("");
        mAdMobileEditText.setText("");
        mAdTitleEditText.setText("");
        mSubCategorySpinner.setSelection(0);
        mCitySpinner.setSelection(0);
        mTermsCheckBox.setChecked(false);
        mImagesParent.removeAllViews();
        mImagesStrings.clear();
        mMap.clear();
        mImageMap.clear();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean visible = false;
                    boolean visible2 = false;
                    mAdTitle = mAdTitleEditText.getText().toString();
                    mAdBody = mAdBodyEditText.getText().toString();
                    mAdMobile = mAdMobileEditText.getText().toString();
                    if (mSubSubCategorySpinner.getVisibility() == View.VISIBLE) {
                        if (mSubSubCategoryModelSelected.getId().equals("-1"))
                            visible = false;
                        else
                            visible = true;
                    } else {
                        visible = true;
                    }

                    if (mModelSpinner.getVisibility() == View.VISIBLE) {
                        if (mModelSelected.equals("الموديل"))
                            visible2 = false;
                        else
                            visible2 = true;
                    } else {
                        visible2 = true;
                    }

                    if (!Helper.validateFields(mAdTitle) && !Helper.validateFields(mAdBody) && !Helper.validateMobile(mAdMobile) && mSubCategoryModelSelected.getId().equals("-1") && mCityModelSelected.getId().equals("-1") && !mTermsCheckBox.isChecked() && mImagesStrings.isEmpty()) {
                        Helper.showSnackBarMessage("ادخل البيانات المطلوبه", AddAdvertisingActivity.this);
                    } else if (!mTermsCheckBox.isChecked()) {
                        Helper.showSnackBarMessage("من فضلك وافق علي التعهد", AddAdvertisingActivity.this);
                    } else if (!Helper.validateFields(mAdTitle)) {
                        Helper.showSnackBarMessage("من فضلك ادخل عنوان الاعلان", AddAdvertisingActivity.this);
                    } else if (!Helper.validateFields(mAdBody)) {
                        Helper.showSnackBarMessage("من فضلك ادخل نص الاعلان", AddAdvertisingActivity.this);
                    } else if (TextUtils.isEmpty(mAdMobile)) {
                        Helper.showSnackBarMessage("من فضلك ادخل  وسيلة الاتصال", AddAdvertisingActivity.this);
                    } else if (mSubCategoryModelSelected.getId().equals("-1")) {
                        Helper.showSnackBarMessage("من فضلك اختار القسم الفرعي", AddAdvertisingActivity.this);
                    } else if (!visible) {
                        Helper.showSnackBarMessage("من فضلك اختار القسم الفرعي الثاني", AddAdvertisingActivity.this);
                    } else if (!visible2){
                        Helper.showSnackBarMessage("من فضلك اختار القسم الموديل", AddAdvertisingActivity.this);
                    } else if (mCityModelSelected.getId().equals("-1")) {
                        Helper.showSnackBarMessage("من فضلك اختار المدينه", AddAdvertisingActivity.this);
                    } else if (mImagesStrings.isEmpty()) {
                        Helper.showSnackBarMessage("من ادخل صورة واحده للاعلان علي الاقل", AddAdvertisingActivity.this);
                    } else {
                        mMap.put("name", mAdTitle);
                        mMap.put("subcategory_id", mSubCategoryModelSelected.getId());
                        if (mSubSubCategorySpinner.getVisibility() == View.VISIBLE) {
                            mMap.put("subsubcategory_id", mSubSubCategoryModelSelected.getId());
                        }
                        mMap.put("mobile", mAdMobile);
                        mMap.put("body", mAdBody);
                        mMap.put("user_id", mUserModel.getId());
                        mMap.put("category_id", mDepartmentModel.getId());
                        mMap.put("city_id", mCityModelSelected.getId());
                        mMap.put("model_id",mModelSelected);
                        mConnector.setMap(mMap);
                        for (int i = 0; i < mImagesStrings.size(); i++) {
                            mImageMap.put("image[" + i + "]", "data:image/jpeg;base64," + mImagesStrings.get(i));
                        }
                        mParentLayout.setVisibility(View.INVISIBLE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        Builders.Any.B b = Ion.with(AddAdvertisingActivity.this)
                                .load(Connector.createUploadImageUrl());
                        for (int i = 0; i < mImagesStrings.size(); i++) {
                            String extension = mImagesStrings.get(i).substring(mImagesStrings.get(i).lastIndexOf(".") + 1);
                            b.setMultipartFile("parameters[" + i + "]", "image/" + extension, new File(mImagesStrings.get(i)));
                        }
                        b.asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        Helper.writeToLog(result);
                                        if (Connector.checkImages(result)) {
                                            ArrayList<String> imagePaths = Connector.getImages(result);
                                            for (int i = 0; i < imagePaths.size(); i++) {
                                                mMap.put("images[" + i + "]", imagePaths.get(i));
                                            }
                                            mConnector.getRequest(TAG, Connector.createAddProductUrl());
                                        } else {
                                            Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", AddAdvertisingActivity.this);
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            mParentLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                    }
                } else {
                    Helper.showSnackBarMessage("من فضلك وافق علي تصريح الوصول للملفات", AddAdvertisingActivity.this);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mImageConnector.cancelAllRequests(TAG);
        mCityConnector.cancelAllRequests(TAG);
        mSubCategoryConnector.cancelAllRequests(TAG);
        mConnector.cancelAllRequests(TAG);
    }
}

