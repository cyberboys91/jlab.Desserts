package jlab.desserts.Activity;

/*
 * Created by Javier on 22/03/2020.
 */

import jlab.desserts.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Surface;
import android.view.MenuItem;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.ads.AdView;
import jlab.desserts.Activity.Utils.Utils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import jlab.desserts.Activity.Utils.Dessert;
import com.google.android.gms.ads.AdRequest;
import android.support.v7.widget.SearchView;
import android.support.v4.view.GravityCompat;
import android.view.animation.AnimationUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import jlab.desserts.Activity.View.GridDessertsView;
import android.support.design.widget.FloatingActionButton;


public class DessertListActivity extends AppCompatActivity implements Utils.IRunOnUIThread,
        NavigationView.OnNavigationItemSelectedListener{

    private GridDessertsView gridDessertsView;
    private LayoutInflater inflater;
    private SwipeRefreshLayout srlRefresh;
    private SearchView searchView;
    private FloatingActionButton fbSearch;
    private DrawerLayout drawerLayout;
    private int difficulty = -1;
    private NavigationView navMenuExplorer;
    private Toolbar toolbar;
    private boolean loadFavorites;
    private GridDessertsView.OnDessertClickListener onDessertClickListener = new GridDessertsView.OnDessertClickListener() {
        @Override
        public void onClick(Dessert dessert) {
            Intent intent = new Intent(DessertListActivity.this, DessertDetailsActivity.class);
            intent.putExtra(Utils.ID_KEY, dessert.getId());
            intent.putExtra(Utils.DESSERTS_ID_KEY, dessert.getDessertId());
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.runnerOnUIThread = this;
        setContentView(R.layout.activity_dessert_list);
        this.inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        this.gridDessertsView = (GridDessertsView) findViewById(android.R.id.list);
        this.gridDessertsView.setOnGetSetViewListener(inflater, getListItemWidth(), getListItemHeight(), onDessertClickListener);
        this.srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        this.srlRefresh.setColorSchemeResources(R.color.colorAccent);
        this.searchView = (SearchView) findViewById(R.id.svSearch);
        this.navMenuExplorer = (NavigationView) findViewById(R.id.nvMenuExplorer);
        this.navMenuExplorer.setNavigationItemSelectedListener(this);
        this.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setVisibility(View.GONE);
                return false;
            }
        });
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadContent();
                return false;
            }
        });
        if(savedInstanceState != null) {
            this.difficulty = savedInstanceState.getInt(Utils.DIFFICULTY_KEY, -1);
            this.loadFavorites = savedInstanceState.getBoolean(Utils.FAVORITE_KEY);
            String query = savedInstanceState.getString(Utils.SEARCH_QUERY_KEY, "");
            if(!query.equals("")) {
                this.searchView.setVisibility(View.VISIBLE);
                this.searchView.setQuery(query, true);
            }
        }
        this.fbSearch = (FloatingActionButton) findViewById(R.id.fbSearch);
        this.fbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHideSearchView(true);
            }
        });
        this.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadContent();
            }
        });
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.toolbar = (Toolbar) findViewById(R.id.tbHeader);
        this.toolbar.setTitle(R.string.app_name);
        setSupportActionBar(this.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawerLayout, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        AdView adView = (AdView) findViewById(R.id.adView0);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    public int getListItemHeight () {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        boolean isPortrait = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
        int numColumns = (isPortrait ? 1 : 2);
        this.gridDessertsView.setNumColumns(numColumns);
        return (int)(displayMetrics.heightPixels / (isPortrait ? 2.5 : 1.5));
    }

    public int getListItemWidth () {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        boolean isPortrait = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
        int numColumns = (isPortrait ? 1 : 2);
        this.gridDessertsView.setNumColumns(numColumns);
        return displayMetrics.widthPixels / numColumns;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.gridDessertsView.setOnGetSetViewListener(inflater, getListItemWidth(), getListItemHeight(), onDessertClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContent();
    }

    private void loadContent() {
        this.srlRefresh.setRefreshing(true);
        if(this.loadFavorites)
            this.gridDessertsView.loadFavorites(this.searchView.getQuery().toString());
        else
            this.gridDessertsView.loadContent(this.searchView.getQuery().toString(), difficulty);
        this.navMenuExplorer.setCheckedItem(getMenuItemIdForContent(difficulty));
        this.srlRefresh.setRefreshing(false);
    }

    private int getMenuItemIdForContent(int difficulty) {
        if(this.loadFavorites)
            return R.id.mnFavorites;
        switch (difficulty) {
            case -1:
                return R.id.mnAllDifficulty;
            case 0:
                return R.id.mnEasyDifficulty;
            case 1:
                return R.id.mnMiddleDifficulty;
            case 2:
                return R.id.mnMiddleHardDifficulty;
            default:
                return R.id.mnHardDifficulty;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnFavorites:
                if(!this.loadFavorites) {
                    this.loadFavorites = true;
                    difficulty = -2;
                    loadContent();
                }
                break;
            case R.id.mnRateApp:
                try {
                    Utils.rateApp(this);
                } catch (Exception | OutOfMemoryError ignored) {
                    ignored.printStackTrace();
                }
                break;
            case R.id.mnAllDifficulty:
                if(difficulty != -1) {
                    difficulty = -1;
                    this.loadFavorites = false;
                    loadContent();
                }
                break;
            case R.id.mnEasyDifficulty:
                if(difficulty != 0) {
                    difficulty = 0;
                    this.loadFavorites = false;
                    loadContent();
                }
                break;
            case R.id.mnMiddleDifficulty:
                if(difficulty != 1) {
                    difficulty = 1;
                    this.loadFavorites = false;
                    loadContent();
                }
                break;
            case R.id.mnMiddleHardDifficulty:
                if(difficulty != 2) {
                    difficulty = 2;
                    this.loadFavorites = false;
                    loadContent();
                }
                break;
            case R.id.mnHardDifficulty:
                if(difficulty != 3) {
                    difficulty = 3;
                    this.loadFavorites = false;
                    loadContent();
                }
                break;
            case R.id.mnAbout:
                Utils.showAboutDialog(this, this.gridDessertsView);
                break;
            case R.id.mnClose:
                finish();
                break;
        }
        hideNavigationView();
        return true;
    }

    private boolean hideNavigationView() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!hideNavigationView()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Utils.DIFFICULTY_KEY, difficulty);
        outState.putString(Utils.SEARCH_QUERY_KEY, searchView.getQuery().toString());
        outState.putBoolean(Utils.FAVORITE_KEY, this.loadFavorites);
    }

    private void showOrHideSearchView(boolean show) {
        if (show) {
            searchView.setVisibility(View.VISIBLE);
            searchView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.up_in));
            searchView.onActionViewExpanded();
        } else {
            searchView.setVisibility(View.GONE);
            searchView.onActionViewCollapsed();
            if (!searchView.getQuery().toString().equals(""))
                searchView.setQuery("", false);
        }
    }

    @Override
    public void run(Runnable runnable) {
        runOnUiThread(runnable);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return onOptionsItemSelected(item);
    }
}
