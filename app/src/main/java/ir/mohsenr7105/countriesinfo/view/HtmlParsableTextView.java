package ir.mohsenr7105.countriesinfo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;

import ir.mohsenr7105.countriesinfo.R;

public class HtmlParsableTextView extends AppCompatTextView {
    public HtmlParsableTextView(Context context) {
        super(context);
    }

    public HtmlParsableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public HtmlParsableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        style(context, attrs);
    }

    private void style(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HtmlParsableTextView);
        setHtmlText(typedArray.getString(R.styleable.HtmlParsableTextView_htmlText));
        typedArray.recycle();
    }

    public void setHtmlText(String htmlText){
        Spanned result = Html.fromHtml(htmlText);
        setText(result);
    }
}
