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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountriesAdapter extends ArrayAdapter implements Filterable{
    private static final String LOG = CountriesAdapter.class.getSimpleName();

    private List mCountriesList;
    private List mCountriesFixedList;

    private static class ViewHolder{
        ImageView imageCountryFlag;
        TextView textCountryName;
        TextView textCountryFarsiName;
    }

    CountriesAdapter(@NonNull Context context, @NonNull List countries) {
        super(context, 0);
        mCountriesList = countries;
        mCountriesFixedList = countries;
    }

    @Override
    public int getCount() {
        return mCountriesList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return mCountriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                // int results variable
                FilterResults results = new FilterResults();

                // validation
                if (charSequence != null && charSequence.length() > 0) {
                    charSequence = charSequence.toString().toLowerCase();
                    ArrayList<Country> filteredCountries = new ArrayList<>();

                    for (Country country: (ArrayList<Country>) mCountriesFixedList){
                        if (country.getName().toLowerCase().contains(charSequence)
                                || country.getFarsiName().contains(charSequence)
                                || country.getAlpha2Code().toLowerCase().contains(charSequence)){
                            filteredCountries.add(country);
                        }
                    }
                    results.count = filteredCountries.size();
                    results.values = filteredCountries;
                } else {
                    results.count = mCountriesFixedList.size();
                    results.values = mCountriesFixedList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mCountriesList = (ArrayList<Country>) filterResults.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
