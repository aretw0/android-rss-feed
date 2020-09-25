package br.ufpe.cin.android.rss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

  // ponto-3 shared preferences
  private SharedPreferences prefs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Log.d(APP_TAG, "onCreate");

    prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    //ATUALIZANDO A PREFERENCE
    SharedPreferences.Editor e = prefs.edit();
    e.putString(RSS_FEED, "https://www.ahnegao.com.br/feed");
    e.commit();


    URL_FEED = prefs.getString(RSS_FEED, getString(R.string.feed_padrao));

    APP_TAG = getString(R.string.app_name);

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
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Referente ao shimmer effect
    shimmerFrameLayout.startShimmer();
  }

  @Override
  protected void onPause() {
    // Referente ao shimmer effect
    shimmerFrameLayout.stopShimmer();
    super.onPause();
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




  private void loadFeed(String url) {
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
          Log.e(APP_TAG, "loadFeed: onError" + e.getMessage());
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