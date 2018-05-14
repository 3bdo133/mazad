package mazad.mazad;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.adapters.PresentedItemAdapter;
import mazad.mazad.models.ChatModel;
import mazad.mazad.models.PresentedItemModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.items)
    RecyclerView mItems;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;

    Connector mConnector;

    ArrayList<PresentedItemModel> mPresentedItemModels;

    UserModel mUserModel = null;

    Intent mIntent;

    private final String TAG = "HomeFragment";
    private final int REQUEST_ID = 1;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

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
            mIntent = getActivity().getIntent();
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
                    mPresentedItemModels.addAll(Connector.getProductsJson(response));
                    adapter.notifyDataSetChanged();
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    Helper.showSnackBarMessage("لا يوجد اتصال بالأنترنت",(AppCompatActivity) getActivity());
                    mItems.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mItems.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله",(AppCompatActivity) getActivity());
            }
        });


        mItems.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mConnector.getRequest(TAG, Connector.createGetProductsUrl());

        mItems.setAdapter(adapter);


        mItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }







}
