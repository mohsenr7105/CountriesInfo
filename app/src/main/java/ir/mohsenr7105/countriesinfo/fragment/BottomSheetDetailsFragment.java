package ir.mohsenr7105.countriesinfo.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
                        String countryNames = getString(R.string.html_country_names,
                                country.getName(), country.getNativeName());
                        String countryDetails = getString(R.string.html_country_details,
                                country.getAlpha2Code(), country.getAlpha3Code(),
                                country.getCapitalNativeName(),
                                country.getPopulation(), country.getArea(),
                                country.getCallingCodes(), country.getRegion(),
                                country.getTimeZones(), country.getCurrencies(),
                                country.getLanguages());
                        htmlTextNames.setHtmlText(countryNames);
                        htmlTextDetails.setHtmlText(countryDetails);
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
}
