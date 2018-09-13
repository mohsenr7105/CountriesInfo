package ir.mohsenr7105.countriesinfo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.mohsenr7105.countriesinfo.Country;
import ir.mohsenr7105.countriesinfo.R;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesViewHolder> {
    private static final String LOG = CountriesAdapter.class.getSimpleName();

    private Context mContext;
    private OnItemClickListener mListener;

    private List mCountriesList;
    private List mCountriesFixedList;

    public class CountriesViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageFlag;
        public TextView textName, textFarsiName;

        public CountriesViewHolder(View itemView) {
            super(itemView);
            imageFlag = itemView.findViewById(R.id.image_country_flag);
            textName = itemView.findViewById(R.id.text_country_name);
            textFarsiName = itemView.findViewById(R.id.text_country_farsi_name);
        }

        public void bindListener(final Country country, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(country);
                }
            });
        }
    }

    public CountriesAdapter(Context context, List<Country> countries){
        mContext = context;
        mCountriesList = countries;
        mCountriesFixedList = countries;
    }

    @Override
    public CountriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country, parent, false);
        return new CountriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CountriesViewHolder holder, int position) {
        Country country = (Country) mCountriesList.get(position);
        String resName = "ic_list_" + country.getAlpha2Code().toLowerCase();
        Log.i(LOG, resName);
        int resId = mContext.getResources().getIdentifier(
                resName , "drawable", mContext.getPackageName()
        );
        holder.imageFlag.setImageResource(resId);
        holder.textName.setText(country.getName());
        holder.textFarsiName.setText(country.getFarsiName());
        holder.bindListener(country, mListener);
    }

    @Override
    public int getItemCount() {
        return mCountriesList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Country country);
    }
}
