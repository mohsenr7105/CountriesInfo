package ir.mohsenr7105.countriesinfo.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ir.mohsenr7105.countriesinfo.R;
import ir.mohsenr7105.countriesinfo.model.Country;
import ir.mohsenr7105.countriesinfo.task.RetrieveCountryTask;
import ir.mohsenr7105.countriesinfo.view.HtmlParsableTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetDetailsFragment extends BottomSheetDialogFragment {

    LinearLayout boxMessage;
    RelativeLayout boxResult;
    HtmlParsableTextView htmlTextNames, htmlTextDetails, htmlTextMessage;
    ImageView imageFlag;
    ProgressBar progressLoading;

    public BottomSheetDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_details, container, false);
        // binding views
        boxMessage = view.findViewById(R.id.box_messages);
        boxResult = view.findViewById(R.id.box_result);
        htmlTextMessage = view.findViewById(R.id.html_text_message);
        progressLoading = view.findViewById(R.id.progress_loading_country);
        htmlTextNames = view.findViewById(R.id.text_country_names);
        htmlTextDetails = view.findViewById(R.id.text_country_details);
        imageFlag = view.findViewById(R.id.image_country_flag);
        // set loading message
        htmlTextMessage.setHtmlText(getString(R.string.html_message_loading_country, getArguments().getString("name")));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new RetrieveCountryTask(
                new RetrieveCountryTask.OnTaskListener() {
                    @Override
                    public void onTaskStarted() {
                        boxMessage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onTaskSucceed(Country country) {
                        boxMessage.setVisibility(View.GONE);
                        setDetails(country);
                        boxResult.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onTaskFailed() {
                        progressLoading.setVisibility(View.GONE);
                        htmlTextMessage.setHtmlText(getString(R.string.html_message_loading_country_failed));
                    }
                }
        ).execute(getArguments().getString("alpha2Code"));
    }

    private void setDetails(Country country){
        // country names
        List<String> countryNames = new ArrayList<>();
        countryNames.add(getString(R.string.html_country_name, country.getName()));
        if (!country.getName().equals(country.getNativeName())){
            countryNames.add(getString(R.string.html_country_native_name, country.getNativeName()));
        }
        String countryNamesString = TextUtils.join("<br>", countryNames);
        // country details
        List<String> countryDetails = new ArrayList<>();
        countryDetails.add(getString(R.string.html_country_alpha_codes, country.getAlpha2Code(), country.getAlpha3Code()));
        if (!TextUtils.isEmpty(country.getCapitalNativeName())){
            countryDetails.add(getString(R.string.html_country_capital, country.getCapitalNativeName()));
        }
        if (country.getPopulation() > 0){
            countryDetails.add(getString(R.string.html_country_population, country.getPopulation().toString()));
        }
        if (country.getArea() > 0){
            countryDetails.add(getString(R.string.html_country_area, country.getArea().toString()));
        }
        if (!TextUtils.isEmpty(country.getCallingCodes())){
            countryDetails.add(getString(R.string.html_country_calling_codes, country.getCallingCodes()));
        }
        if (!TextUtils.isEmpty(country.getRegion())){
            countryDetails.add(getString(R.string.html_country_region, country.getRegion()));
        }
        if (!TextUtils.isEmpty(country.getTimeZones())){
            countryDetails.add(getString(R.string.html_country_timezones, country.getTimeZones()));
        }
        if (!TextUtils.isEmpty(country.getCurrencies())){
            countryDetails.add(getString(R.string.html_country_currencies, country.getCurrencies()));
        }
        if (!TextUtils.isEmpty(country.getLanguages())){
            countryDetails.add(getString(R.string.html_country_languages, country.getLanguages()));
        }
        String countryDetailsString = TextUtils.join("<br>", countryDetails);
        htmlTextNames.setHtmlText(countryNamesString);
        htmlTextDetails.setHtmlText(countryDetailsString);
        String resName = "ic_list_" + getArguments().getString("alpha2Code").toLowerCase();
        int resId = getResources().getIdentifier(
                resName, "drawable", getContext().getPackageName()
        );
        if (resId == 0) {
            resId = getResources().getIdentifier(
                    "ic_list_unknown", "drawable", getContext().getPackageName()
            );
        }
        imageFlag.setImageResource(resId);
    }
}
