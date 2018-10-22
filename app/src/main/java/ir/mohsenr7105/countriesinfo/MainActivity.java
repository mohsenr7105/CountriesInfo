package ir.mohsenr7105.countriesinfo;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.mohsenr7105.countriesinfo.adapter.CountriesAdapter;
import ir.mohsenr7105.countriesinfo.fragment.BottomSheetDetailsFragment;
import ir.mohsenr7105.countriesinfo.model.Country;
import ir.mohsenr7105.countriesinfo.task.RetrieveCounteiesTask;
import ir.mohsenr7105.countriesinfo.task.RetrieveCountryTask;


public class MainActivity extends AppCompatActivity {
    final static String LOG = MainActivity.class.getSimpleName();

    List<Country> countriesList = new ArrayList<>();

    CountriesAdapter countriesAdapter;

    RecyclerView recyclerCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        enableHttpResponseCache();

        recyclerCountries = findViewById(R.id.recycler_countries);

        countriesAdapter = new CountriesAdapter(this, countriesList);
        countriesAdapter.setOnItemClickListener(new CountriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Country country) {
                new RetrieveCountryTask(MainActivity.this,
                        new RetrieveCountryTask.OnTaskCompleted() {
                            @Override
                            public void onTaskCompleted(Country country) {
                                showCountryDetailsAlert(country);
                            }
                        }).execute(country.getAlpha2Code());
            }
        });

        recyclerCountries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerCountries.setAdapter(countriesAdapter);

        new RetrieveCounteiesTask(MainActivity.this,
                new RetrieveCounteiesTask.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(List<Country> countries) {
                        countriesList.addAll(countries);
                        countriesAdapter.notifyDataSetChanged();
                    }
                }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                countriesAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void showCountryDetailsAlert(Country country) {
        BottomSheetDetailsFragment bottomSheetDetailsFragment = new BottomSheetDetailsFragment();
        String alpha2code = country.getAlpha2Code();
        String countryNames = getString(R.string.html_country_names,
                country.getName(), country.getNativeName());
        String countryDetails = getString(R.string.html_country_details,
                country.getAlpha2Code(), country.getAlpha3Code(),
                country.getCapitalNativeName(), country.getCapitalNativeName(),
                country.getPopulation(), country.getArea(),
                country.getCallingCodes(), country.getRegion(),
                country.getTimeZones(), country.getCurrencies(),
                country.getLanguages());
        Bundle data = new Bundle();
        data.putString("alpha2Code", alpha2code);
        data.putString("names", countryNames);
        data.putString("details", countryDetails);
        bottomSheetDetailsFragment.setArguments(data);
        bottomSheetDetailsFragment.show(getSupportFragmentManager(), bottomSheetDetailsFragment.getTag());
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