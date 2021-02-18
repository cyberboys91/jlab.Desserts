package jlab.desserts.Activity;

import jlab.desserts.R;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import java.util.ArrayList;
import android.view.Surface;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import androidx.appcompat.app.ActionBar;
import com.google.android.gms.ads.AdView;
import androidx.appcompat.widget.Toolbar;
import android.content.res.Configuration;
import jlab.desserts.Activity.Utils.Utils;
import com.google.android.gms.ads.AdRequest;
import android.view.animation.AnimationUtils;
import jlab.desserts.Activity.Utils.Dessert;
import androidx.appcompat.app.AppCompatActivity;
import jlab.desserts.Activity.Utils.DessertManager;

public class DessertDetailsActivity extends AppCompatActivity {

    private Dessert dessert;
    private int dessertId = 1, id = 1;
    private DessertManager dessertManager;
    private TextView tvDescription, tvIngredients, tvPrepDescription, tvDifficulty;
    private RelativeLayout rlDescription, rlDessertImage;
    private ImageView ivDessertImage, ivFavorite;
    private ArrayList<Bitmap> bitmapImages;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private Thread imageAnim = new Thread(new Runnable() {
        @Override
        public void run() {
            final int[] index = {0};
            while(true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivDessertImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_out));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ivDessertImage.setImageBitmap(bitmapImages.get(index[0]));
                                        ivDessertImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_in));
                                        index[0] = (index[0] + 1) % bitmapImages.size();
                                    }
                                });
                            }
                        }).start();
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desserts);
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        AdView adView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView2.loadAd(adRequest2);

        this.id = getIntent().getIntExtra(Utils.ID_KEY, 1);
        this.dessertId = getIntent().getIntExtra(Utils.DESSERTS_ID_KEY, 1);
        this.dessertManager = new DessertManager(this);
        this.tvDescription = (TextView) findViewById(R.id.tvDessertDescription);
        this.tvIngredients = (TextView) findViewById(R.id.tvDessertIngredients);
        this.tvPrepDescription = (TextView) findViewById(R.id.tvDessertPrepDescription);
        this.tvDifficulty = (TextView) findViewById(R.id.tvDifficulty);
        this.ivDessertImage = (ImageView) findViewById(R.id.ivDessert);
        loadImageHeight();
        this.ivFavorite = (ImageView) findViewById(R.id.ivIsFavorite);
        this.rlDessertImage = (RelativeLayout) findViewById(R.id.rlDessertImage);
        this.rlDessertImage.setAnimation(AnimationUtils.loadAnimation(this, R.anim.up_in));
        this.rlDescription = (RelativeLayout) findViewById(R.id.rlDescription);
        this.rlDescription.setAnimation(AnimationUtils.loadAnimation(this, R.anim.down_in));
        this.bitmapImages = dessertManager.getBitmapImages(this.dessertId);
        this.toolbar = (Toolbar) findViewById(R.id.tbHeader);
        this.toolbar.setTitle(R.string.app_name);
        setSupportActionBar(this.toolbar);
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayShowHomeEnabled(true);
        this.actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadImageHeight() {
        ViewGroup.LayoutParams lp = this.ivDessertImage.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        boolean isPortrait = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
        lp.height = displayMetrics.heightPixels / (isPortrait ? 3 : 2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadImageHeight();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dessert = dessertManager.getDessert(id);
        Utils.setFavoriteView(this.ivFavorite, dessert.isFavorite());
        this.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dessertManager.setFavoriteDessert(dessert.getDessertId(), !dessert.isFavorite()) > 0) {
                    dessert.setFavorite(!dessert.isFavorite());
                    Utils.setFavoriteView(ivFavorite, dessert.isFavorite());
                }
            }
        });
        this.ivFavorite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.showSnackBar(dessert.isFavorite()
                        ? R.string.remove_from_favorite
                        : R.string.add_to_favorite, rlDescription);
                return true;
            }
        });
        tvDescription.setText(dessert.getDescription());
        tvIngredients.setText(dessert.getIngredients());
        tvPrepDescription.setText(dessert.getPrepDescription());
        tvDifficulty.setText(getResources()
                .getText(Utils.getDifficultyResourceText(dessert.getDifficulty())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tvDifficulty.setBackground(getDrawable(Utils.getDifficultyResourceColor(dessert.getDifficulty())));
        else
            tvDifficulty.setBackground(getResources().getDrawable(Utils.getDifficultyResourceColor(dessert.getDifficulty())));
        this.toolbar.setTitle(dessert.getName());
        if(!this.imageAnim.isAlive())
            this.imageAnim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.removeItem(R.id.mnDifficulty);
        menu.removeItem(R.id.mnFavorites);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.mnRateApp:
                try {
                    Utils.rateApp(this);
                } catch (Exception | OutOfMemoryError ignored) {
                    ignored.printStackTrace();
                }
                break;
            case R.id.mnAbout:
                Utils.showAboutDialog(this, this.tvPrepDescription);
                break;
            case R.id.mnClose:
                onBackPressed();
                break;
        }
        return true;
    }
}
