package ir.mohsenr7105.countriesinfo;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    final static String LOG = MainActivity.class.getSimpleName();
    List<Country> listCountries = new ArrayList<>();

    CountriesAdapter adapterCountries;

    ListView listViewCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewCountries = findViewById(R.id.list_countries);
        adapterCountries = new CountriesAdapter(this, listCountries);
        listViewCountries.setAdapter(adapterCountries);

        new RetrieveCountriesTask().execute();
    }

    class RetrieveCountriesTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;

        RetrieveCountriesTask() {
            progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("downloading");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("https://restcountries.eu/rest/v2/all");
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
                progressDialog.dismiss();

                return;
            }
            Log.i(LOG, response);
            try {
                JSONArray countries = (JSONArray) new JSONTokener(response).nextValue();
                for (int i = 0; i < countries.length(); i++) {
                    JSONObject country = countries.getJSONObject(i);
                    String countryName = country.getJSONObject("translations")
                            .getString("fa");
                    String alpha2Code = country.getString("alpha2Code");
                    listCountries.add(new Country(countryName, alpha2Code));
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            } finally {
                progressDialog.dismiss();
                Log.d(LOG, listCountries.toString());
                adapterCountries.notifyDataSetChanged();
            }
        }
    }


}
