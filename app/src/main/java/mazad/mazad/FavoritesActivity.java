package mazad.mazad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
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

public class FavoritesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.items)
    RecyclerView mItems;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    Connector mConnector;
    Connector mConnectorDeleteFavorite;

    ArrayList<PresentedItemModel> mPresentedItemModels;

    UserModel mUserModel = null;

    Intent mIntent;

    Map<String, String> mMap;
    Map<String, String> mMapDeleteFavorite;

    private static final String TAG = "FavoritesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
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


        mPresentedItemModels = new ArrayList<>();

        mIntent = getIntent();

        if (mIntent != null) {
            if (mIntent.hasExtra("user") && mIntent.getSerializableExtra("user") != null) {
                mUserModel = (UserModel) mIntent.getSerializableExtra("user");
            }
        }

        final PresentedItemAdapter adapter = new PresentedItemAdapter(FavoritesActivity.this, mPresentedItemModels, new PresentedItemAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                startActivity(new Intent(FavoritesActivity.this,AdDetailsActivity.class).putExtra("product",mPresentedItemModels.get(position)));
            }
        });


        mMap = new HashMap<>();

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    mPresentedItemModels.clear();
                    mPresentedItemModels.addAll(Connector.getProductsJson(response));
                    for (int i = 0; i < mPresentedItemModels.size(); i++) {
                        mPresentedItemModels.get(i).setFavorite(1);
                    }
                    adapter.notifyDataSetChanged();
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mPresentedItemModels.clear();
                    adapter.notifyDataSetChanged();
                    Helper.showSnackBarMessage("لا يوجد مفضلة", FavoritesActivity.this);
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mItems.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", FavoritesActivity.this);
            }
        });


        mItems.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        if (mUserModel != null) {
            mMap.put("user_id", mUserModel.getId());
            mConnector.setMap(mMap);
        }
        mConnector.getRequest(TAG, Connector.createMyFavoriteUrl());


        mItems.setAdapter(adapter);


        mItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        mConnectorDeleteFavorite = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    Helper.showSnackBarMessage("تم الحذف من المفضلة بنجاح", FavoritesActivity.this);
                    if (mUserModel != null) {
                        mMap.put("user_id", mUserModel.getId());
                        mConnector.setMap(mMap);
                    }
                    mConnector.getRequest(TAG, Connector.createMyFavoriteUrl());
                } else {
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", FavoritesActivity.this);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", FavoritesActivity.this);
            }
        });


        mMapDeleteFavorite = new HashMap<>();


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
                mMapDeleteFavorite.put("product_id",productId);
                mMapDeleteFavorite.put("user_id",mUserModel.getId());
                mConnectorDeleteFavorite.setMap(mMapDeleteFavorite);
                mConnectorDeleteFavorite.getRequest(TAG, Connector.createAddToFavoriteUrl());
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

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
        mConnectorDeleteFavorite.cancelAllRequests(TAG);
    }
}
