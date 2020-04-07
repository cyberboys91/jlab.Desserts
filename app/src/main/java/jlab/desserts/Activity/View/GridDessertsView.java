package jlab.desserts.Activity.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import jlab.desserts.Activity.Utils.Dessert;
import jlab.desserts.Activity.Utils.DessertManager;
import jlab.desserts.Activity.Utils.Utils;
import jlab.desserts.R;

/*
 * Created by Javier on 22/03/2020.
 */

public class GridDessertsView extends GridView implements AbsListView.OnScrollListener {

    private int last, first, antFirst;
    public boolean scrolling = false;
    private DessertAdapter mAdapter;
    private DessertManager dessertManager;
    private String query;

    public GridDessertsView(Context context) {
        super(context);
        dessertManager = new DessertManager(context);
        mAdapter = new DessertAdapter(context);
        setAdapter(mAdapter);
    }

    public GridDessertsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dessertManager = new DessertManager(context);
        mAdapter = new DessertAdapter(context);
        setAdapter(mAdapter);
    }

    public GridDessertsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dessertManager = new DessertManager(context);
        mAdapter = new DessertAdapter(context);
        setAdapter(mAdapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE)
            scrolling = false;
        scrolling = scrollState == SCROLL_STATE_FLING;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        antFirst = first != firstVisibleItem ? first : antFirst;
        first = firstVisibleItem;
        last = firstVisibleItem + visibleItemCount - 1;
    }

    public void setOnGetSetViewListener (final LayoutInflater inflater, final int iconSize,
                                         final OnDessertClickListener onDessertClickListener) {
        this.mAdapter.setonGetSetViewListener(new DessertAdapter.OnGetSetViewListener() {
            @Override
            public View getView(ViewGroup parent, Dessert resource, int position) {
                return inflater.inflate(R.layout.grid_details_dessert, parent, false);
            }

            @Override
            public void setView(final View view, final Dessert resource, int position) {
                ((TextView) view.findViewById(R.id.tvDessertName)).setText(resource.getName());
                ((TextView) view.findViewById(R.id.tvDessertComment)).setText(resource.getCountIngredients()
                        + " " + getContext().getString(R.string.ingredients_word));
                ((TextView) view.findViewById(R.id.tvDifficulty)).setText(getResources()
                        .getText(Utils.getDifficultyResourceText(resource.getDifficulty())));
                view.findViewById(R.id.tvDifficulty).setBackground(getResources()
                        .getDrawable(Utils.getDifficultyResourceColor(resource.getDifficulty())));
                ViewGroup.LayoutParams newParams = view.getLayoutParams();
                newParams.height = iconSize;
                newParams.width = iconSize;
                ((TextView) view.findViewById(R.id.tvDessertDescription)).setText(resource.getDescription());
                final ImageView ivFavorite = (ImageView) view.findViewById(R.id.ivIsFavorite);
                Utils.setFavoriteView(ivFavorite, resource.isFavorite());
                ivFavorite.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(dessertManager.setFavoriteDessert(resource.getDessertId(), !resource.isFavorite()) > 0) {
                            resource.setFavorite(!resource.isFavorite());
                            Utils.setFavoriteView(ivFavorite, resource.isFavorite());
                        }
                    }
                });

                ivFavorite.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Utils.showSnackBar(resource.isFavorite()
                                ? R.string.remove_from_favorite
                                : R.string.add_to_favorite, view);
                        return true;
                    }
                });
                if(query != null && query.length() > 0) {
                    BackgroundColorSpan colorSpan = new BackgroundColorSpan(getResources().getColor(R.color.transparent_accent));
                    SpannableStringBuilder textBd = new SpannableStringBuilder(resource.getName());
                    int index = resource.getName().toLowerCase().indexOf(query);
                    textBd.setSpan(colorSpan, index, index + query.length(), 0);
                    Selection.selectAll(textBd);
                    ((TextView) view.findViewById(R.id.tvDessertName)).setText(textBd);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView imageView = (ImageView) view.findViewById(R.id.ivDessertListImage);
                        final Bitmap image = dessertManager.getImage(resource.getDessertId());
                        Utils.runnerOnUIThread.run(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(image);
                            }
                        });
                    }
                }).start();

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDessertClickListener.onClick(resource);
                    }
                });
            }
        });
    }

    public void loadContent(String query, int difficulty) {
<<<<<<< HEAD
        mAdapter.clear();
        this.query = query;
        mAdapter.addAll(dessertManager.getAllDetails(query, difficulty));
    }

    public void loadFavorites(String query) {
        mAdapter.clear();
        this.query = query;
        mAdapter.addAll(dessertManager.getFavorites(query));
=======
        mAdapter.clear();
        this.query = query;
        mAdapter.addAll(dessertManager.getAllDetails(query, difficulty));
>>>>>>> e4563d8c19c387551752ee3c668f3fc0569c5457
    }

    @Override
    public int getFirstVisiblePosition() {
        return first;
    }

    public interface OnDessertClickListener {
        void onClick (Dessert dessert);
    }
}