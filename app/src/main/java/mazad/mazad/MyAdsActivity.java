package mazad.mazad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.adapters.PresentedItemAdapter;
import mazad.mazad.models.PresentedItemModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyAdsActivity extends AppCompatActivity {

    @BindView(R.id.items)
    RecyclerView mItems;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    Connector mConnector;
    Connector mConnectorRemoveProduct;

    ArrayList<PresentedItemModel> mPresentedItemModels;

    Map<String,String> mMap;
    Map<String,String> mMapRemoveProduct;

    UserModel mUserModel;

    private final String TAG = "MyAdsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
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


        if (getIntent().hasExtra("user")){
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
            Helper.writeToLog(mUserModel.getId());
        }

        mPresentedItemModels = new ArrayList<>();

        final PresentedItemAdapter adapter = new PresentedItemAdapter(this, mPresentedItemModels, new PresentedItemAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                startActivity(new Intent(MyAdsActivity.this,AdDetailsActivity.class).putExtra("product",mPresentedItemModels.get(position)).putExtra("user",mUserModel));
            }
        });



        mConnector = new Connector(this, new Connector.LoadCallback() {
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
                    Helper.showSnackBarMessage("لا يوجد اعلانات", MyAdsActivity.this);
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mItems.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",MyAdsActivity.this);
            }
        });


        mMap = new HashMap<>();

        if (getIntent().hasExtra("user")){
            mUserModel = (UserModel) getIntent().getSerializableExtra("user");
            mMap.put("user_id",mUserModel.getId());
            Helper.writeToLog(mUserModel.getId());
        }

        mConnector.setMap(mMap);


        mItems.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mConnector.getRequest(TAG, Connector.createGetProductsUrl());


        mItems.setAdapter(adapter);


        mItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mItems.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                String productId = String.valueOf(viewHolder.itemView.getTag());
                mMapRemoveProduct.put("id",productId);
                mMapRemoveProduct.put("user_id",mUserModel.getId());
                mConnectorRemoveProduct.setMap(mMapRemoveProduct);
                mConnectorRemoveProduct.getRequest(TAG, Connector.createRemoveProductUrl());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Paint p = new Paint();
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX/4, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        p.setColor(Color.parseColor("#FFFFFF"));
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft() + dX/4, (float) itemView.getTop(), (float) itemView.getLeft(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + 2 * width, (float) itemView.getTop() + width, (float) itemView.getLeft() + width, (float) itemView.getBottom() - width);
                        p.setColor(Color.parseColor("#FFFFFF"));
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mItems);


        mMapRemoveProduct = new HashMap<>();

        mConnectorRemoveProduct = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    Helper.showSnackBarMessage("تم الحذف بنجاح",MyAdsActivity.this);
                    mConnector.getRequest(TAG, Connector.createGetProductsUrl());
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",MyAdsActivity.this);
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mItems.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",MyAdsActivity.this);
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
        mConnectorRemoveProduct.cancelAllRequests(TAG);
    }

}
