package br.ufpe.cin.android.rss;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.prof.rssparser.Article;
import com.prof.rssparser.Channel;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String APP_TAG;
    private String RSS_FEED = "rssfeed";

    private String URL_FEED;

    // Declarando recycler view
    RecyclerView conteudoRSS;
    RssAdapter adapter;
    List<Article> noticias;
    ShimmerFrameLayout shimmerFrameLayout;

    ActionBar aT;
    Toolbar toolbar;

    // ponto-3 shared preferences
    private SharedPreferences prefs;
    private SharedPreferences.Editor e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APP_TAG = getString(R.string.app_name);

        Log.d(APP_TAG, "onCreate");

        // Capturando SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Criando editor
        e = prefs.edit();
        e.putString(RSS_FEED, "https://www.ahnegao.com.br/feed");
        e.apply();

        URL_FEED = prefs.getString(RSS_FEED, getString(R.string.feed_padrao));

        // Preparando recycler view
        conteudoRSS = findViewById(R.id.conteudoRSS);
        conteudoRSS.setHasFixedSize(true);
        conteudoRSS.setLayoutManager(new LinearLayoutManager(this));
        conteudoRSS.addItemDecoration(
                new DividerItemDecoration(
                        getApplicationContext(),
                        ((LinearLayoutManager) conteudoRSS.getLayoutManager()).getOrientation()
                )
        );
        adapter = new RssAdapter(getApplicationContext());
        conteudoRSS.setAdapter(adapter);

        // Shimmer element para efeito shimmer
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerFrameLayout);

        // ponto 4: adicionando toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rss_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(APP_TAG, String.format("onOptionsItemSelected: %s", item.getTitle()));
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(APP_TAG, "onStart");
        loadFeed(URL_FEED);
      /*
      new Thread(
              () -> {
                  try {
                      String conteudo = getRssFeed(RSS_FEED);
                      //precisa rodar de uma UI thread
                      runOnUiThread(
                              () -> conteudoRSS.setText(conteudo)
                      );

                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
      ).start();
      */
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(APP_TAG, "onResume");
        // Referente ao shimmer effect
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        Log.d(APP_TAG, "onPause");
        // Referente ao shimmer effect
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    private void loadFeed(String url) {
        Log.d(APP_TAG, "loadFeed");
        Parser p = new Parser.Builder().build();
        p.onFinish(
                new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Channel channel) {
                        Log.d(APP_TAG, "loadFeed: onTaskCompleted");
                        noticias = channel.getArticles();
                        runOnUiThread(
                                () -> {
                                    adapter.setNoticias(noticias);
                                    adapter.notifyDataSetChanged();
                                    // Alterando layout para mostrar os dados
                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                    conteudoRSS.setVisibility(View.VISIBLE);
                                }
                        );

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(APP_TAG, String.format("loadFeed: onError%s", e.getMessage()));
                        // Referente ao shimmer effect
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }
        );
        p.execute(url);
    }

    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}