package jlab.desserts.Activity.View;

import android.view.View;
import java.util.Collection;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import jlab.desserts.Activity.Utils.Dessert;

/*
 * Created by Javier on 1/10/2016.
 */
public class DessertAdapter extends ArrayAdapter<Dessert> {

    private OnGetSetViewListener monGetSetViewListener;

    public DessertAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public void add(Dessert elem) {
        super.add(elem);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(@NonNull Collection<? extends Dessert> resources) {
        super.addAll(resources);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        super.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            Dessert resource = getItem(position);
            convertView = this.monGetSetViewListener.getView(parent, resource, position);
            this.monGetSetViewListener.setView(convertView, resource, position);
        } catch (Exception | OutOfMemoryError ignored) {
            ignored.printStackTrace();
        }
        return convertView;
    }

    public void setonGetSetViewListener(OnGetSetViewListener monGetSetViewListener) {
        this.monGetSetViewListener = monGetSetViewListener;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public interface OnGetSetViewListener {
        View getView(ViewGroup parent, Dessert resource, int position);

        void setView(View view, Dessert resource, int position);
    }
}
