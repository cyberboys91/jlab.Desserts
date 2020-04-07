package jlab.desserts.Activity.Utils;
/*
 * Created by Javier on 21/03/2020.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jlab.desserts.R;

public class Utils {
    public static final String DESSERTS_ID_KEY = "DESSERTS_ID_KEY";
    public static final String ID_KEY = "ID_KEY";
    public static final String DIFFICULTY_KEY = "DIFFICULTY_KEY";
    public static final String SEARCH_QUERY_KEY = "SEARCH_QUERY_KEY";
    public static final String FAVORITE_KEY = "FAVORITE_KEY";
    public static IRunOnUIThread runnerOnUIThread;

    public static int getDifficultyResourceText (int difficulty) {
        switch (difficulty) {
            case 0:
                return R.string.difficulty_0;
            case 1:
                return R.string.difficulty_1;
            case 2:
                return R.string.difficulty_2;
            default:
                return R.string.difficulty_3;
        }
    }

    public static int getDifficultyResourceColor (int difficulty) {
        switch (difficulty) {
            case 0:
                return R.drawable.easy_difficulty_background_v2;
            case 1:
                return R.drawable.middle_difficulty_background_v2;
            case 2:
                return R.drawable.middle_hard_difficulty_background_v2;
            default:
                return R.drawable.hard_difficulty_background_v2;
        }
    }

    public interface IRunOnUIThread {
        void run(Runnable runnable);
    }

    public static void rateApp(Context context) {
        Uri uri = Uri.parse(String.format("market://details?id=%s", context.getPackageName()));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (Exception | OutOfMemoryError ignored) {
            ignored.printStackTrace();
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s"
                            , context.getPackageName()))));
        }
    }

    public static void showAboutDialog(final Context context, final View viewForSnack) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.about)
                    .setMessage(R.string.about_content)
                    .setPositiveButton(R.string.accept, null)
                    .setNegativeButton(context.getString(R.string.contact), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                context.startActivity(new Intent(Intent.ACTION_SENDTO)
                                        .setData(Uri.parse(String.format("mailto:%s", context.getString(R.string.mail)))));
                            } catch (Exception | OutOfMemoryError ignored) {
                                ignored.printStackTrace();
                                Utils.showSnackBar(R.string.app_mail_not_found, viewForSnack);
                            }
                        }
                    })
                    .show();
        } catch (Exception | OutOfMemoryError ignored) {
            ignored.printStackTrace();
        }
    }

    private static Snackbar createSnackBar(int message, View viewForSnack) {
        if (viewForSnack == null)
            return null;
        return Snackbar.make(viewForSnack, message, Snackbar.LENGTH_LONG);
    }

    public static Snackbar createSnackBar(String message, View viewForSnack) {
        if (viewForSnack == null)
            return null;
        Snackbar result = Snackbar.make(viewForSnack, message, Snackbar.LENGTH_LONG);
        ((TextView) result.getView().findViewById(R.id.snackbar_text)).setTextColor(viewForSnack.getResources().getColor(R.color.white));
        return result;
    }

    public static void showSnackBar(int msg, View viewForSnack) {
        Snackbar snackbar = createSnackBar(msg, viewForSnack);
        if (snackbar != null) {
            ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(viewForSnack.getResources().getColor(R.color.white));
            snackbar.setActionTextColor(viewForSnack.getResources().getColor(R.color.colorAccent));
            snackbar.show();
        }
    }

    public static void showSnackBar(String msg, View viewForSnack) {
        Snackbar snackbar = Utils.createSnackBar(msg, viewForSnack);
        if (snackbar != null) {
            ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(viewForSnack.getResources().getColor(R.color.white));
            snackbar.show();
        }
    }

    public static void setFavoriteView(ImageView imageView, boolean favorite) {
        imageView.setImageResource(favorite
                ? R.drawable.img_favorite_checked
                : R.drawable.img_favorite_not_checked);
    }
}
