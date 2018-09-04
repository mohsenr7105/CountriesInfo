package ir.mohsenr7105.countriesinfo;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CountriesAdapter extends ArrayAdapter {
    private static final String LOG = CountriesAdapter.class.getSimpleName();

    private static class ViewHolder{
        ImageView imageCountryFlag;
        TextView textCountryName;
        TextView textCountryFarsiName;
    }

    CountriesAdapter(@NonNull Context context, @NonNull List countries) {
        super(context, 0, countries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Country country = (Country) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_countries, parent, false);

            viewHolder.textCountryName = convertView.findViewById(R.id.text_country_name);
            viewHolder.textCountryFarsiName = convertView.findViewById(R.id.text_country_farsi_name);
            viewHolder.imageCountryFlag = convertView.findViewById(R.id.image_country_flag);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textCountryName.setText(country.getName());
        viewHolder.textCountryFarsiName.setText(country.getFarsiName());
        String resName = "ic_list_" + country.getAlpha2Code().toLowerCase();
        Log.i(LOG, resName);
        int resId = getContext().getResources().getIdentifier(
               resName , "drawable", getContext().getPackageName()
        );
        Log.i(LOG, String.valueOf(resId));
        viewHolder.imageCountryFlag.setImageResource(resId);

        return convertView;
    }
}
