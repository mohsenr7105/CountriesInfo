package ir.mohsenr7105.countriesinfo.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ir.mohsenr7105.countriesinfo.model.Country;

public class RetrieveCountryTask extends AsyncTask<String, Void, String> {
    private static final String LOG = RetrieveCountryTask.class.getSimpleName();

    private Context mContext;
    private OnTaskCompleted mListener;
    private ProgressDialog mProgressDialog;

    public RetrieveCountryTask(Context context, OnTaskCompleted listener){
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
            country.setFarsiName(countryJson.getJSONObject("translations").getString("fa"));
            country.setAlpha2Code(countryJson.getString("alpha2Code"));
            country.setAlpha3Code(countryJson.getString("alpha3Code"));
            country.setCapitalNativeName(countryJson.getString("capital"));
            country.setPopulation(countryJson.getLong("population"));
            country.setArea(countryJson.getLong("area"));
        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            mProgressDialog.dismiss();
            mListener.onTaskCompleted(country);
        }
    }

    public interface OnTaskCompleted{
        void onTaskCompleted(Country country);
    }
}
