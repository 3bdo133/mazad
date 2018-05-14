package mazad.mazad;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.models.ChatModel;
import mazad.mazad.models.UserModel;
import mazad.mazad.utils.BottomNavigationViewHelper;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.register_parent)
    View mRegistrationParent;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.receive_name)
    View mReceiveName;
    @BindView(R.id.back)
    ImageView mBackButton;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    ActionBarDrawerToggle mToggle;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.news)
    ImageView mNewsButton;
    @BindView(R.id.login)
    ImageView mLoginButton;
    @BindView(R.id.user_name)
    TextView mUserName;

    Intent mChatIntent;
    boolean home = true;
    boolean login = false;

    Intent mIntent;
    UserModel mUserModel = null;
    ChatModel mChatModel = null;

    boolean changeFragment = false;
    private final int REQUEST_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mToggle.setDrawerSlideAnimationEnabled(false);
        mNavigationView.setNavigationItemSelectedListener(this);

        Menu menu = mNavigationView.getMenu();

        menu.findItem(R.id.nav_login).getIcon().setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);
        setTextColorForMenuItem(menu.findItem(R.id.nav_login), android.R.color.holo_green_light);

        menu.findItem(R.id.nav_logout).getIcon().setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
        setTextColorForMenuItem(menu.findItem(R.id.nav_logout), android.R.color.holo_red_dark);


        menu.findItem(R.id.nav_logout).setVisible(false);
        menu.findItem(R.id.nav_login).setVisible(true);

        mBottomNavigationView.setSelectedItemId(R.id.nav_home);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        home = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();


        mNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, NewsActivity.class).putExtra("user", mUserModel));

            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!login) {
                    startActivity(new Intent(HomeActivity.this, LoginRegisterActivity.class));
                } else {
                    startActivity(new Intent(HomeActivity.this,RegisterActivity.class).putExtra("user",mUserModel));
                }
            }
        });




        if (savedInstanceState != null && savedInstanceState.containsKey("user")) {
            login = true;
            mUserModel = (UserModel) savedInstanceState.getSerializable("user");
            mLoginButton.setImageResource(R.drawable.ic_user_registered);
            mUserName.setText(mUserModel.getName());
            mUserName.setVisibility(View.VISIBLE);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_login).setVisible(false);
        }

        mIntent = getIntent();

        if (mIntent.hasExtra("user")) {
            login = true;
            mUserModel = (UserModel) mIntent.getSerializableExtra("user");
            mLoginButton.setImageResource(R.drawable.ic_user_registered);
            mUserName.setText(mUserModel.getName());
            mUserName.setVisibility(View.VISIBLE);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_login).setVisible(false);
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home = false;
                mToggle.setDrawerIndicatorEnabled(false);
                mNewsButton.setVisibility(View.GONE);
                mReceiveName.setVisibility(View.GONE);
                mBackButton.setVisibility(View.GONE);
                mToolbarTitle.setVisibility(View.VISIBLE);
                mRegistrationParent.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new ChatFragment()).commit();

            }
        });


        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                overrideMenuFontsFonts(mNavigationView);
            }
        });


        if (savedInstanceState != null && savedInstanceState.containsKey("fragment")) {
            String fragment = savedInstanceState.getString("fragment");
            if (fragment != null && fragment.equals("settings")) {
                home = false;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new SettingsFragment()).commit();
            } else if (fragment != null && fragment.equals("home")) {
                home = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();
            } else if (fragment != null && fragment.equals("messages")) {
                if (mUserModel == null) {
                    startActivity(new Intent(HomeActivity.this, LoginRegisterActivity.class));
                } else {
                    home = false;
                    mToolbar.setVisibility(View.VISIBLE);
                    mToggle.setDrawerIndicatorEnabled(false);
                    mNewsButton.setVisibility(View.GONE);
                    mReceiveName.setVisibility(View.GONE);
                    mBackButton.setVisibility(View.GONE);
                    mToolbarTitle.setVisibility(View.VISIBLE);
                    mRegistrationParent.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new ChatFragment()).commit();
                }
            } else if (fragment != null && fragment.equals("search")) {
                home = false;
                mToolbar.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new SearchFragment()).commit();
            }
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_favorites) {
            if (mUserModel == null) {
                startActivity(new Intent(HomeActivity.this, LoginRegisterActivity.class));
            } else {
                startActivity(new Intent(HomeActivity.this, FavoritesActivity.class).putExtra("user", mUserModel));
            }
        } else if (id == R.id.nav_advertise_here) {
            if (mUserModel == null) {
                startActivity(new Intent(HomeActivity.this, LoginRegisterActivity.class));
            } else {
                startActivity(new Intent(HomeActivity.this, DepartmentsActivity.class).putExtra("user", mUserModel));
            }
        } else if (id == R.id.nav_search) {
            if (mUserModel == null) {
                startActivityForResult(new Intent(HomeActivity.this, SpecializedSearchActivity.class),REQUEST_ID);
            } else {
                startActivityForResult(new Intent(HomeActivity.this, SpecializedSearchActivity.class).putExtra("user", mUserModel),REQUEST_ID);
            }
        } else if (id == R.id.nav_my_ads) {
            if (mUserModel == null) {
                startActivity(new Intent(HomeActivity.this, LoginRegisterActivity.class));
            } else {
                startActivity(new Intent(HomeActivity.this, MyAdsActivity.class).putExtra("user", mUserModel));
            }
        } else if (id == R.id.nav_call_us) {
            startActivity(new Intent(HomeActivity.this, CallUsActivity.class).putExtra("user", mUserModel));
        } else if (id == R.id.nav_terms) {
            if (mUserModel == null) {
                startActivity(new Intent(HomeActivity.this, TermsAndConditionsActivity.class));
            } else {
                startActivity(new Intent(HomeActivity.this, TermsAndConditionsActivity.class).putExtra("user",mUserModel));
            }
        } else if (id == R.id.nav_settings) {
            home = false;
            mToolbar.setVisibility(View.VISIBLE);
            mToggle.setDrawerIndicatorEnabled(true);
            mReceiveName.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mNewsButton.setVisibility(View.VISIBLE);
            mToolbarTitle.setVisibility(View.GONE);
            mRegistrationParent.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new SettingsFragment()).commit();
        } else if (id == R.id.nav_home) {
            home = true;
            mToolbar.setVisibility(View.VISIBLE);
            mToggle.setDrawerIndicatorEnabled(true);
            mNewsButton.setVisibility(View.VISIBLE);
            mReceiveName.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mToolbarTitle.setVisibility(View.GONE);
            mRegistrationParent.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();
        } else if (id == R.id.nav_message) {
            if (mUserModel == null) {
                startActivity(new Intent(HomeActivity.this, LoginRegisterActivity.class));
            } else {
                home = false;
                mToolbar.setVisibility(View.VISIBLE);
                mToggle.setDrawerIndicatorEnabled(false);
                mNewsButton.setVisibility(View.GONE);
                mReceiveName.setVisibility(View.GONE);
                mBackButton.setVisibility(View.GONE);
                mToolbarTitle.setVisibility(View.VISIBLE);
                mRegistrationParent.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new ChatFragment()).commit();
            }
        } else if (id == R.id.nav_logout){
            Helper.removeUserFromSharedPreferences(HomeActivity.this);
            startActivity(new Intent(HomeActivity.this,HomeActivity.class));
            finish();
        } else if (id == R.id.nav_login){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        } else if (id == R.id.nav_search_normal){
            home = false;
            mToolbar.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new SearchFragment()).commit();
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }





    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (home) {
            showAlertDialog();
        } else if (mReceiveName.getVisibility() == View.VISIBLE) {
            home = false;
            mToggle.setDrawerIndicatorEnabled(false);
            mNewsButton.setVisibility(View.GONE);
            mReceiveName.setVisibility(View.GONE);
            mBackButton.setVisibility(View.GONE);
            mToolbarTitle.setVisibility(View.VISIBLE);
            mRegistrationParent.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new ChatFragment()).commit();

        } else {
            home = true;
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();
            mBottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int size = mNavigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }
        if (home) {
            mBottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
        if (Helper.PreferencesContainsUser(HomeActivity.this)){
            mUserModel = Helper.getUserSharedPreferences(HomeActivity.this);
            mUserName.setText(mUserModel.getName());
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int id = getSelectedItem(mBottomNavigationView);
        if (id == R.id.nav_settings) {
            outState.putString("fragment", "settings");
        } else if (id == R.id.nav_home) {
            outState.putString("fragment", "home");
        } else if (id == R.id.nav_search_normal){
            outState.putString("fragment", "search");
        } else if (id == R.id.nav_message){
            outState.putString("fragment", "messages");
        }
        if (login) {
            outState.putSerializable("user", mUserModel);
        }
    }


    private int getSelectedItem(BottomNavigationView bottomNavigationView) {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()) {
                return menuItem.getItemId();
            }
        }
        return 0;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (changeFragment) {
            Helper.writeToLog("true");
            home = false;
            mBottomNavigationView.setSelectedItemId(R.id.nav_message);
            mChatModel = (ChatModel) mChatIntent.getSerializableExtra("chat");
            findViewById(R.id.receive_name).setVisibility(View.VISIBLE);
            mToggle.setDrawerIndicatorEnabled(false);
            mNewsButton.setVisibility(View.GONE);
            mBackButton.setVisibility(View.VISIBLE);
            mReceiveName.setVisibility(View.VISIBLE);
            mToolbarTitle.setVisibility(View.GONE);
            mRegistrationParent.setVisibility(View.GONE);
            ((TextView) (findViewById(R.id.receiver_name))).setText(mChatModel.getName());
            MessagesFragment fragment = new MessagesFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("chat", mChatModel);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK){
            if (data.hasExtra("chat") && data.getSerializableExtra("chat") != null){
                mChatIntent = data;
                changeFragment = true;
            } else {
                changeFragment = false;
            }
        } else {
            changeFragment = false;
        }
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder;
        Context context = this;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            AlertDialog alertDialog = builder.setTitle("اغلاق التطبيق")
                    .setMessage("هل تود اغلاق التطبيق ؟")
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           HomeActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .create();
            if (alertDialog.getWindow() != null) {
                ViewCompat.setLayoutDirection(alertDialog.getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                alertDialog.getWindow().setLayout(600,400);
            }
            alertDialog.show();
    }


    public void overrideMenuFontsFonts(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    overrideMenuFontsFonts(vg.getChildAt(i));
                }
            } else if (v instanceof TextView) {
                CalligraphyUtils.applyFontToTextView(this, (TextView) v, "fonts/ArbFONTS-DroidArabicKufi.ttf");
            }
        } catch (Exception e) {
            //Log it, but ins't supposed to be here.
        }
    }



}
