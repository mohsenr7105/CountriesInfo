package ir.mohsenr7105.countriesinfo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.mohsenr7105.countriesinfo.adapter.CountriesAdapter;
import ir.mohsenr7105.countriesinfo.fragment.BottomSheetDetailsFragment;
import ir.mohsenr7105.countriesinfo.model.Country;
import ir.mohsenr7105.countriesinfo.task.RetrieveCountriesTask;
import ir.mohsenr7105.countriesinfo.view.HtmlParsableTextView;


public class MainActivity extends AppCompatActivity {
    final static String LOG = MainActivity.class.getSimpleName();

    List<Country> countriesList = new ArrayList<>();

    CountriesAdapter countriesAdapter;

    RecyclerView recyclerCountries;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout boxMessages;
    HtmlParsableTextView htmlTextMainMessage;
    AppCompatTextView textCacheMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        enableHttpResponseCache();

        bindViews();

        initCountriesRecyclerView();

        initSwipeRefresh();
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

    public void showSearchResultAlert(boolean show, CharSequence searchedText){
        if (show) {
            textCacheMessage.setVisibility(View.GONE);
            htmlTextMainMessage.setHtmlText(getString(R.string.html_message_no_result, searchedText));
            boxMessages.setVisibility(View.VISIBLE);
            return;
        }
        if (!show){
            boxMessages.setVisibility(View.GONE);
        }
    }

    private void bindViews(){
        recyclerCountries = findViewById(R.id.recycler_countries);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        boxMessages = findViewById(R.id.box_messages);
        htmlTextMainMessage = findViewById(R.id.text_message_main);
        textCacheMessage = findViewById(R.id.text_message_cache);
    }

    private void initCountriesRecyclerView(){
        countriesAdapter = new CountriesAdapter(this, countriesList);
        countriesAdapter.setOnItemClickListener(new CountriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Country country) {
                showCountryDetailsAlert(country);
            }
        });

        recyclerCountries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerCountries.setAdapter(countriesAdapter);
    }

    private void initSwipeRefresh(){
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCountries();
            }
        });
        swipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorAccent);
        swipeRefresh.setRefreshing(true);
        loadCountries();
    }

    private void loadCountries(){
        new RetrieveCountriesTask(
                new RetrieveCountriesTask.OnTaskListener() {
                    @Override
                    public void onTaskStarted() {
                        boxMessages.setVisibility(View.VISIBLE);
                        htmlTextMainMessage.setHtmlText(getString(R.string.html_message_loading_countries));
                        textCacheMessage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onTaskSucceed(List<Country> countries) {
                        textCacheMessage.setVisibility(View.GONE);
                        boxMessages.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                        swipeRefresh.setEnabled(false);
                        countriesList.addAll(countries);
                        countriesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onTaskFailed() {
                        htmlTextMainMessage.setHtmlText(getString(R.string.html_message_no_internet));
                        swipeRefresh.setRefreshing(false);
                    }

                }).execute();
    }

    private void showCountryDetailsAlert(Country country) {
        BottomSheetDetailsFragment bottomSheetDetailsFragment = new BottomSheetDetailsFragment();
        Bundle data = new Bundle();
        data.putString("alpha2Code", country.getAlpha2Code());
        data.putString("name", country.getName());
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