package mazad.mazad;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    @BindView(R.id.search_text)
    EditText mSearchText;
    @BindView(R.id.cancel)
    TextView mCancelButton;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.normal_text)
    TextView mNormalText;
    @BindView(R.id.products)
    RecyclerView mProducts;

    Connector mConnector;

    UserModel mUserModel;

    Intent mIntent;

    ArrayList<PresentedItemModel> mPresentedItemModels;

    Map<String,String> mMap;

    private final String TAG = "SearchFragment";

    private final int REQUEST_ID = 1;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, v);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {
                ViewCompat.setLayoutDirection(getActivity().findViewById(R.id.parent_layout), ViewCompat.LAYOUT_DIRECTION_RTL);
            }
        }

        mPresentedItemModels = new ArrayList<>();


        final PresentedItemAdapter adapter = new PresentedItemAdapter(getActivity(), mPresentedItemModels, new PresentedItemAdapter.OnItemClick() {
            @Override
            public void setOnItemClick(int position) {
                getActivity().startActivityForResult(new Intent(getActivity(), AdDetailsActivity.class).putExtra("product",mPresentedItemModels.get(position)).putExtra("user",mUserModel),REQUEST_ID);
            }
        });


        try {
            if (getActivity() != null) {
                mIntent = getActivity().getIntent();
            }
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }


        if (mIntent != null){
            if (mIntent.hasExtra("user") && mIntent.getSerializableExtra("user") != null){
                mUserModel = (UserModel) mIntent.getSerializableExtra("user");
            }
        }


        mConnector = new Connector(getActivity(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mPresentedItemModels.clear();
                    mPresentedItemModels.addAll(Connector.getProductsJson(response));
                    adapter.notifyDataSetChanged();
                    mProducts.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mPresentedItemModels.clear();
                    adapter.notifyDataSetChanged();
                    mNormalText.setVisibility(View.VISIBLE);
                    Helper.showSnackBarMessage("لا نتائج",(AppCompatActivity) getActivity());
                    mProducts.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mPresentedItemModels.clear();
                adapter.notifyDataSetChanged();
                mProducts.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",(AppCompatActivity) getActivity());
            }
        });

        mMap = new HashMap<>();


        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mMap.put("search", s.toString());
                    mNormalText.setVisibility(View.INVISIBLE);
                    mProducts.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mConnector.setMap(mMap);
                    mConnector.getRequest(TAG, Connector.createGetProductsUrl());
                } else {
                    mPresentedItemModels.clear();
                    adapter.notifyDataSetChanged();
                    mNormalText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

        mProducts.setAdapter(adapter);


        mProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));



        return v;
    }


    @Override
    public void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }
}
