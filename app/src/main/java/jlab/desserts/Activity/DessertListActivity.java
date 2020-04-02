package jlab.desserts.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import jlab.desserts.Activity.Utils.Dessert;
import jlab.desserts.Activity.Utils.Utils;
import jlab.desserts.Activity.View.GridDessertsView;
import jlab.desserts.R;

/*
 * Created by Javier on 22/03/2020.
 */

public class DessertListActivity extends AppCompatActivity implements Utils.IRunOnUIThread {

    private GridDessertsView gridDessertsView;
    private LayoutInflater inflater;
    private SwipeRefreshLayout srlRefresh;
    private SearchView searchView;
    private FloatingActionButton fbSearch;
    private int difficulty = -1;
    private GridDessertsView.OnDessertClickListener onDessertClickListener = new GridDessertsView.OnDessertClickListener() {
        @Override
        public void onClick(Dessert dessert) {
            Intent intent = new Intent(DessertListActivity.this, DessertDetailsActivity.class);
            intent.putExtra(Utils.ID_KEY, dessert.getId());
            intent.putExtra(Utils.DESSERTS_ID_KEY, dessert.getDessertId());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.runnerOnUIThread = this;
        setContentView(R.layout.activity_dessert_list);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        this.gridDessertsView = (GridDessertsView) findViewById(android.R.id.list);
        this.gridDessertsView.setOnGetSetViewListener(inflater, getIconSize(), onDessertClickListener);
        this.srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        this.srlRefresh.setColorSchemeResources(R.color.colorAccent);
        this.searchView = (SearchView) findViewById(R.id.svSearch);
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onResume();
                return false;
            }
        });
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
                onResume();
            }
        });
        AdView adView = (AdView) findViewById(R.id.adView0);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    public int getIconSize () {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        boolean isPortrait = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
        int numColumns = (isPortrait ? 1 : 2);
        this.gridDessertsView.setNumColumns(numColumns);
        return (displayMetrics.widthPixels / numColumns);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.gridDessertsView.setOnGetSetViewListener(inflater, getIconSize(), onDessertClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.srlRefresh.setRefreshing(true);
        this.gridDessertsView.loadContent(this.searchView.getQuery().toString(), difficulty);
        this.srlRefresh.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnRateApp:
                try {
                    Utils.rateApp(this);
                } catch (Exception | OutOfMemoryError ignored) {
                    ignored.printStackTrace();
                }
                break;
            case R.id.mnAllDifficulty:
                difficulty = -1;
                onResume();
                break;
            case R.id.mnEasyDifficulty:
                difficulty = 0;
                onResume();
                break;
            case R.id.mnMiddleDifficulty:
                difficulty = 1;
                onResume();
                break;
            case R.id.mnMiddleHardDifficulty:
                difficulty = 2;
                onResume();
                break;
            case R.id.mnHardDifficulty:
                difficulty = 3;
                onResume();
                break;
            case R.id.mnAbout:
                Utils.showAboutDialog(this, this.gridDessertsView);
                break;
            case R.id.mnClose:
                onBackPressed();
                break;
        }
        return true;
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
}
