package ir.mohsenr7105.countriesinfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    final static String LOG = MainActivity.class.getSimpleName();

    List<Country> countriesList = new ArrayList<>();

    CountriesAdapter countriesAdapter;

    ListView listCountries;
    SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableHttpResponseCache();

        search = findViewById(R.id.search_countries_list);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                countriesAdapter.getFilter().filter(newText);
                return false;
            }
        });

        listCountries = findViewById(R.id.list_countries);
        countriesAdapter = new CountriesAdapter(this, countriesList);
        listCountries.setAdapter(countriesAdapter);
        listCountries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Country country = (Country) adapterView.getItemAtPosition(i);
                new RetrieveCountryTask().execute(country.getAlpha2Code());
            }
        });

        new RetrieveCountriesTask().execute();
    }

    public void showMoreInfoAlert(Country country){
        String info = getString(R.string.html_country_more_info,
                country.getName(), country.getNativeName(), country.getFarsiName(),
                country.getAlpha2Code(), country.getAlpha3Code(), country.getCapitalNativeName(),
                country.getPopulation(), country.getArea());
        Spanned htmlText = Html.fromHtml(info);
        new AlertDialog.Builder(this)
                .setTitle("more info")
                .setMessage(htmlText)
                .setNeutralButton("ok", null)
                .show();
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 1 * 1024 * 1024; // 1 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d(LOG, "HTTP response cache is unavailable.");
        }
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
                progressDialog.dismiss();

                return;
            }
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
                progressDialog.dismiss();
                countriesAdapter.notifyDataSetChanged();
            }
        }
    }

    class RetrieveCountryTask extends AsyncTask<String, Void, String>{
        private ProgressDialog progressDialog;

        RetrieveCountryTask() {
            progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("downloading");
            progressDialog.show();
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
                progressDialog.dismiss();

                return;
            }
//            Log.i(LOG, response);
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
                progressDialog.dismiss();
                showMoreInfoAlert(country);
            }
        }
    }
}