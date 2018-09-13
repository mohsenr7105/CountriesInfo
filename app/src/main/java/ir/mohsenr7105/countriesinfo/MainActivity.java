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
import android.widget.Toast;

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

import ir.mohsenr7105.countriesinfo.task.RetrieveCounteiesTask;
import ir.mohsenr7105.countriesinfo.task.RetrieveCountryTask;


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
                new RetrieveCountryTask(MainActivity.this,
                        new RetrieveCountryTask.OnTaskCompleted() {
                            @Override
                            public void onTaskCompleted(Country country) {
                                showCountryDetailsAlert(country);
                            }
                        }).execute(country.getAlpha2Code());
            }
        });

        new RetrieveCounteiesTask(MainActivity.this,
                new RetrieveCounteiesTask.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(List<Country> countries) {
                        countriesList.addAll(countries);
                        countriesAdapter.notifyDataSetChanged();
                    }
                }).execute();
    }

    private void showCountryDetailsAlert(Country country){
        String info = getString(R.string.html_country_more_info,
                country.getName(), country.getNativeName(), country.getFarsiName(),
                country.getAlpha2Code(), country.getAlpha3Code(), country.getCapitalNativeName(),
                country.getPopulation(), country.getArea());
        Spanned htmlText = Html.fromHtml(info);
        new AlertDialog.Builder(MainActivity.this)
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

}