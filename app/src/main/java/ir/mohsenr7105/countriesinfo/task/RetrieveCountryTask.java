package ir.mohsenr7105.countriesinfo.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

import ir.mohsenr7105.countriesinfo.model.Country;

public class RetrieveCountryTask extends AsyncTask<String, Void, String> {
    private static final String LOG = RetrieveCountryTask.class.getSimpleName();

    private Context mContext;
    private OnTaskCompleted mListener;
    private ProgressDialog mProgressDialog;

    public RetrieveCountryTask(Context context, OnTaskCompleted listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("downloading country details");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... alpha2Code) {
        try {
            URL url = new URL("https://restcountries.eu/rest/v2/alpha/" + alpha2Code[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            Log.e(LOG, ex.getMessage());
            return null;
        }

    }

    @Override
    protected void onPostExecute(String response) {
        if (response == null) {
            mProgressDialog.dismiss();

            return;
        }
        Country country = new Country();
        try {
            JSONObject countryJson = (JSONObject) new JSONTokener(response).nextValue();
            country.setName(countryJson.getString("name"));
            country.setNativeName(countryJson.getString("nativeName"));
            country.setAlpha2Code(countryJson.getString("alpha2Code"));
            country.setAlpha3Code(countryJson.getString("alpha3Code"));
            country.setCapitalNativeName(countryJson.getString("capital"));
            country.setPopulation(countryJson.getLong("population"));
            country.setArea(countryJson.getLong("area"));
            // get calling codes array and join with comma
            country.setCallingCodes(TextUtils.join(", ", jsonToList(countryJson.getJSONArray("callingCodes"))));
            // get currencies
            country.setCurrencies(getCurrencies(countryJson.getJSONArray("currencies")));
            // get languages
            country.setLanguages(getLanguages(countryJson.getJSONArray("languages")));
            // get region and subregion then join it
            country.setRegion(countryJson.getString("region") + " , " + countryJson.getString("subregion"));
            // get time zones array and join
            country.setTimeZones(TextUtils.join(", ", jsonToList(countryJson.getJSONArray("timezones"))));
        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            mProgressDialog.dismiss();
            mListener.onTaskCompleted(country);
        }
    }

    /**
    * reorder currencies information in string
     **/
    private String getCurrencies(JSONArray currencies) {
        if (currencies.length() > 0) {
            List<String> currenciesList = new ArrayList<>();
            for (int i = 0; i < currencies.length() ; i++) {
                try {
                    JSONObject currency = currencies.getJSONObject(i);
                    currenciesList.add(currency.getString("name") + "(" + currency.getString("symbol") + ")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return TextUtils.join(",", currenciesList);
        }
        return "";
    }

    /**
     *    reorder languages information in string
     */
    private String getLanguages(JSONArray languages) {
        if (languages.length() > 0){
            List<String> languagesList = new ArrayList<>();
            for (int i = 0; i < languages.length(); i++){
                try {
                    languagesList.add(languages.getJSONObject(i).getString("name"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return TextUtils.join(",", languagesList);
        }
        return "";
    }

    private List<String> jsonToList(JSONArray jsonArray) {
        ArrayList list = new ArrayList();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(jsonArray.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(Country country);
    }
}
