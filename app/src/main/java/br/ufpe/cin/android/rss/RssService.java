package br.ufpe.cin.android.rss;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.prof.rssparser.Article;
import com.prof.rssparser.Channel;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RssService extends Service {
    private final String TAG = "RssService";
    private String RSS_FEED = "rssfeed";

    // flag p/ request sendo feito compartilhada entre threads
    private AtomicReference<Boolean> requestingFeed;

    private final IBinder mBinder = new RssBinder();

    public class RssBinder extends Binder {
        RssService getService() {
            // retorna a instancia do Service, para que clientes chamem metodos publicos
            return RssService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        requestingFeed = new AtomicReference<>(false);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.d(TAG, "onStartCommand: ".concat(action));
        if (action!=null) {
            if (action.equals("stop")) {
                stopSelf();
            } else if(action.equals(ServiceConstants.DATA_REFRESH.getFlag())) {
                loadFeed();
            }
        }
        return START_STICKY;
    }

    private void updateDB(List<Article> news) {
        new Thread(() -> {
            Noticia[] nots = {};
            List<Noticia> noticias = new ArrayList<>(Arrays.asList(nots));
            // "Casting" de Article List to Noticia List
            for(Article n: news){
                noticias.add(new Noticia(n));
            }

            NoticiasDB db = NoticiasDB.getInstance(this);
            NoticiasDAO dao = db.obterDAO();;
            try {
                dao.inserirNoticias(noticias);
                doBroadCast(ServiceConstants.DATA_UPDATE.getFlag(),"",true);
            } catch (Exception err) {
                String text = err.getMessage();
                Log.d(TAG, "updateDB: ".concat(text));
                doBroadCast(ServiceConstants.DATA_ERROR.getFlag(),text,false);
            }
        }).start();
    }

    public void loadFeed() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = prefs.getString(RSS_FEED, getString(R.string.feed_padrao));
        Log.d(TAG, String.format("loadFeed: %s %b", url, requestingFeed.get()));
        if (!requestingFeed.get()) {
            Parser p = new Parser.Builder().build();
            p.onFinish(
                new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Channel channel) {
                        Log.d(TAG, "loadFeed: onTaskCompleted");
                        updateDB(channel.getArticles());
                    }

                    @Override
                    public void onError(Exception e) {
                        String text = e.getCause().getMessage();
                        Log.e(TAG, String.format("loadFeed: onError %s", text));
                        doBroadCast(ServiceConstants.XML_ERROR.getFlag(),text,false);
                    }
                }
            );
            doBroadCast(ServiceConstants.XML_REQUEST.getFlag(),"",true);
            p.execute(url);
        }
    }

    private void doBroadCast(String action, String payload, boolean state) {
        Log.d(TAG, String.format("doBroadCast: %s %s %b",action, payload, state));
        Intent to_send = new Intent(action);
        to_send.putExtra("PAYLOAD", payload);
        to_send.putExtra("STATE", state);
        sendBroadcast(to_send);
    }
}
