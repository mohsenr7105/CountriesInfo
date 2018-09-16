package ir.mohsenr7105.countriesinfo.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ir.mohsenr7105.countriesinfo.model.Country;

public class RetrieveCounteiesTask extends AsyncTask<Void, Void, String> {
    private static final String LOG = RetrieveCounteiesTask.class.getSimpleName();

    private Context mContext;
    private OnTaskCompleted mListener;
    private ProgressDialog mProgressDialog;

    public RetrieveCounteiesTask(Context context, OnTaskCompleted listener){
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("downloading");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL("https://restcountries.eu/rest/v2/all?fields=name;translations;alpha2Code");
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
        List<Country> countriesList = new ArrayList<>();

        Log.i(LOG, response);
        try {
            JSONArray countries = (JSONArray) new JSONTokener(response).nextValue();
            for (int i = 0; i < countries.length(); i++) {
                JSONObject country = countries.getJSONObject(i);
                String countryName = country.getString("name");
                String countryFarsiName = country.getJSONObject("translations")
                        .getString("fa");
                String alpha2Code = country.getString("alpha2Code");
                countriesList.add(new Country(countryName, countryFarsiName, alpha2Code));
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            mProgressDialog.dismiss();
            mListener.onTaskCompleted(countriesList);
//            countriesAdapter.notifyDataSetChanged();
        }
    }

    public interface OnTaskCompleted{
        void onTaskCompleted(List<Country> countriesList);
    }
}
