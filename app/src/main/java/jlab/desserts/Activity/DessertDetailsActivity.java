package jlab.desserts.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jlab.desserts.Activity.Utils.Dessert;
import jlab.desserts.Activity.Utils.DessertManager;
import jlab.desserts.Activity.Utils.Utils;
import jlab.desserts.R;

public class DessertDetailsActivity extends AppCompatActivity {

    private Dessert dessert;
    private int dessertId = 1, id = 1;
    private DessertManager dessertManager;
    private TextView tvDescription, tvIngredients, tvPrepDescription, tvDifficulty;
    private RelativeLayout rlDescription, rlDessertImage;
    private ImageView ivImage;
    private ArrayList<Bitmap> bitmapImages;
    private ActionBar actionBar;
    private Thread imageAnim = new Thread(new Runnable() {
        @Override
        public void run() {
            final int[] index = {0};
            while(true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_out));
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
                                        ivImage.setImageBitmap(bitmapImages.get(index[0]));
                                        ivImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_in));
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

        // Load an ad into the AdMob banner view.
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
        this.ivImage = (ImageView) findViewById(R.id.ivDessert);
        this.rlDessertImage = (RelativeLayout) findViewById(R.id.rlDessertImage);
        this.rlDessertImage.setAnimation(AnimationUtils.loadAnimation(this, R.anim.up_in));
        this.rlDescription = (RelativeLayout) findViewById(R.id.rlDescription);
        this.rlDescription.setAnimation(AnimationUtils.loadAnimation(this, R.anim.down_in));
        this.bitmapImages = dessertManager.getBitmapImages(this.dessertId);
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayShowHomeEnabled(true);
        this.actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dessert = dessertManager.getDessert(id);
        tvDescription.setText(dessert.getDescription());
        tvIngredients.setText(dessert.getIngredients());
        tvPrepDescription.setText(dessert.getPrepDescription());
        tvDifficulty.setText(getResources()
                .getText(Utils.getDifficultyResourceText(dessert.getDifficulty())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            tvDifficulty.setBackground(getDrawable(Utils.getDifficultyResourceColor(dessert.getDifficulty())));
        else
            tvDifficulty.setBackground(getResources().getDrawable(Utils.getDifficultyResourceColor(dessert.getDifficulty())));
        setTitle(dessert.getName());
        if(!this.imageAnim.isAlive())
            this.imageAnim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
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
