package ir.mohsenr7105.countriesinfo.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ir.mohsenr7105.countriesinfo.R;
import ir.mohsenr7105.countriesinfo.view.HtmlParsableTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetDetailsFragment extends BottomSheetDialogFragment {


    public BottomSheetDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_details, container, false);
        HtmlParsableTextView textNames = view.findViewById(R.id.text_country_names);
        HtmlParsableTextView textDetails = view.findViewById(R.id.text_country_details);
        ImageView imageFlag = view.findViewById(R.id.image_country_flag);
        textNames.setHtmlText(getArguments().getString("names"));
        textDetails.setHtmlText(getArguments().getString("details"));
        String resName = "ic_list_" + getArguments().getString("alpha2Code").toLowerCase();
        int resId = getResources().getIdentifier(
                resName , "drawable", getContext().getPackageName()
        );
        if (resId == 0){
            resId = getResources().getIdentifier(
                    "ic_list_unknown", "drawable", getContext().getPackageName()
            );
        }
        imageFlag.setImageResource(resId);
        return view;
    }

}
