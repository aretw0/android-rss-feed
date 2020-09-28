package br.ufpe.cin.android.rss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.concurrent.atomic.AtomicReference;

public class PreferenciasActivity extends AppCompatActivity {

    private String APP_TAG;
    private String RSS_FEED = "rssfeed";
    // Para ser usado entre threads
    private AtomicReference<String> URL_FEED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);
        APP_TAG = getString(R.string.app_name).concat(" (PreferenciasActivity)");

        Log.d(APP_TAG, "onCreate");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        URL_FEED = new AtomicReference<>(prefs.getString(RSS_FEED, getString(R.string.feed_padrao)));

        //Após criar o fragmento, use o código abaixo para exibir
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings,new PrefsFragment((sharedPreferences, key) -> {
                    Log.d(APP_TAG, "OnSharedPreferenceChangeListener");
                    // listener
                    if (key.equals(RSS_FEED)){
                        // Write your code here
                        String URL_NOW = sharedPreferences.getString(key, getString(R.string.feed_padrao));

                        // se a URL realmente mudou apague o banco
                        if (URL_FEED.get() != URL_NOW) {
                            URL_FEED.set(URL_NOW);
                            new Thread(
                                () -> {
                                    NoticiasDB db = NoticiasDB.getInstance(getApplicationContext());
                                    NoticiasDAO dao = db.obterDAO();
                                    dao.nukeTable();
                                }
                            ).start();
                        }
                    }
                }))
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(APP_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(APP_TAG, "onResume");
    }

    @Override
    protected void onPause() {
        Log.d(APP_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(APP_TAG, "onStop");
        super.onStop();
    }
}