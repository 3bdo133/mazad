package mazad.mazad;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @BindView(R.id.remove_favorite)
    Button mRemoveFavorite;
    @BindView(R.id.remove_search)
    Button mRemoveSearch;
    @BindView(R.id.share_app)
    Button mShareApp;
    @BindView(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.notification_reminder)
    Switch mNotificationReminder;

    Connector mConnector;
    Map<String,String> mMap;
    Map<String,String> mMapRemoveSearch;

    UserModel mUserModel = null;

    Connector mConnectorRemoveSearch;

    private final String TAG = "SettingsFragment";

    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getActivity() != null) {
                ViewCompat.setLayoutDirection(getActivity().findViewById(R.id.parent_layout), ViewCompat.LAYOUT_DIRECTION_RTL);
            }
        }

        mRemoveFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        mRemoveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogDeleteSearch();
            }
        });

        mShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText("تطبيق مزاد يسمح لك باضافة اعلانات وبيعها... والتجول في اعلانات باقي المستخدمين والتواصل معهم");
            }
        });


        mMap = new HashMap<>();
        mMapRemoveSearch = new HashMap<>();

        mConnector = new Connector(getActivity(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (getActivity() != null) {
                        Helper.showSnackBarMessage("تم حذف المفضلة بنجاح", ((AppCompatActivity) getActivity()));
                    }
                } else {
                    if (getActivity() != null) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Helper.showSnackBarMessage("خطأ من فضلك اعد المحاوله", ((AppCompatActivity) getActivity()));
                    }
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                if (getActivity() != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", ((AppCompatActivity) getActivity()));
                }
            }
        });


        mConnectorRemoveSearch = new Connector(getActivity(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (getActivity() != null) {
                        Helper.showSnackBarMessage(Connector.getMessage(response), ((AppCompatActivity) getActivity()));
                    }
                } else {
                    if (getActivity() != null) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Helper.showSnackBarMessage("خطأ من فضلك اعد المحاوله", ((AppCompatActivity) getActivity()));
                    }
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                if (getActivity() != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Helper.showSnackBarMessage("خطأ. من فضلك اعد المحاوله", ((AppCompatActivity) getActivity()));
                }
            }
        });

        if (getActivity() != null) {
            if (getActivity().getIntent() != null && getActivity().getIntent().getSerializableExtra("user") != null) {
                mUserModel = (UserModel) getActivity().getIntent().getSerializableExtra("user");
            }
        }


        mNotificationReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Helper.SaveNotificationToSharedPreferences(getActivity(),isChecked);
            }
        });


        mNotificationReminder.setChecked(Helper.getNotificationSharedPreferences(getActivity()));

        return v;
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder;
        Context context = getContext();
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            AlertDialog alertDialog = builder.setTitle("حذف المفضلة")
                    .setMessage("هل انت متأكد انك تريد حذف قائمة المفضلة")
                    .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (mUserModel != null) {
                                mMap.put("user_id", mUserModel.getId());
                                mConnector.setMap(mMap);
                                mProgressBar.setVisibility(View.VISIBLE);
                                mConnector.getRequest(TAG, Connector.createDeleteFavoriteUrl());
                            } else {
                                if (getActivity() != null) {
                                    Helper.showSnackBarMessage("من فضلك قم بتسجيل الدخول", ((AppCompatActivity) getActivity()));
                                }
                            }
                        }
                    })
                    .setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .create();

            ViewCompat.setLayoutDirection(alertDialog.getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);

            alertDialog.show();
        }
    }


    private void showAlertDialogDeleteSearch() {
        AlertDialog.Builder builder;
        Context context = getContext();
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            AlertDialog alertDialog = builder.setTitle("حذف سجل البحث")
                    .setMessage("هل انت متأكد انك تريد حذف سجل البحث")
                    .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (mUserModel != null) {
                                mMapRemoveSearch.put("user_id", mUserModel.getId());
                                mConnectorRemoveSearch.setMap(mMapRemoveSearch);
                                mProgressBar.setVisibility(View.VISIBLE);
                                mConnectorRemoveSearch.getRequest(TAG, Connector.createRemoveSearchUrl());
                            } else {
                                if (getActivity() != null) {
                                    Helper.showSnackBarMessage("من فضلك قم بتسجيل الدخول", ((AppCompatActivity) getActivity()));
                                }
                            }
                        }
                    })
                    .setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .create();

            ViewCompat.setLayoutDirection(alertDialog.getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);

            alertDialog.show();
        }
    }


    public void shareText(String text) {
        String mimeType = "text/plain";
        String title = "مشاركة تطبيق مزاد";
        if (getActivity() != null) {
            Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                    .setChooserTitle(title)
                    .setType(mimeType)
                    .setText(text)
                    .getIntent();
            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(shareIntent);
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mConnector.cancelAllRequests(TAG);
    }
}
