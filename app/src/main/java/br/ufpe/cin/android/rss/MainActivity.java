package br.ufpe.cin.android.rss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private String TAG;
    private String RSS_FEED = "rssfeed";

    // flag p/ banco vazio compartilhada entre threads
    private AtomicReference<Boolean> emptyDb;
    // flag p/ request sendo feito compartilhada entre threads
    private AtomicReference<Boolean> requestingFeed;
    // flag p/ checking do banco sendo feito compartilhada entre threads
    private AtomicReference<Boolean> checkingDB;
    // flag p/ checked do banco sendo feito compartilhada entre threads
    private AtomicReference<Boolean> checkedDB;

    private ServiceReceiver serviceReceiver;


    // Declarando recycler view
    RecyclerView conteudoRSS;
    RssAdapter adapter;
    ShimmerFrameLayout shimmerFrameLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    Toolbar toolbar;

    TextView errorMessage;
    CardView errorCard;

    // Service
    Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = getString(R.string.app_name).concat(" (MainActivity)");
        Log.d(TAG, "onCreate");

        emptyDb = new AtomicReference<>(true);
        requestingFeed = new AtomicReference<>(false);
        checkingDB = new AtomicReference<>(false);
        checkedDB = new AtomicReference<>(false);

        // para mostrar mensagens de erro
        errorCard = findViewById(R.id.errorCard);
        errorMessage = findViewById(R.id.errorMessage);
        Button errorButton = findViewById(R.id.botaoError);

        // listener do click do botão do card de error
        errorButton.setOnClickListener(
            v -> {
                shimmer(true);
                startRssService(ServiceConstants.DATA_REFRESH.getFlag());
            }
        );

        // pull to refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(
            () -> {
                startRssService(ServiceConstants.DATA_REFRESH.getFlag());
            }
        );
        // Preparando recycler view
        conteudoRSS = findViewById(R.id.conteudoRSS);
        conteudoRSS.setHasFixedSize(true);
        conteudoRSS.addItemDecoration(
            new DividerItemDecoration(
                getApplicationContext(),
                ((LinearLayoutManager) conteudoRSS.getLayoutManager()).getOrientation()
            )
        );
        adapter = new RssAdapter(getApplicationContext());
        conteudoRSS.setAdapter(adapter);

        // Shimmer element para efeito shimmer
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);

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
        Log.d(TAG, String.format("onOptionsItemSelected: %s", item.getTitle()));
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            startActivity(new Intent(getApplicationContext(),
                    PreferenciasActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        setError("", false);
        shimmer(true);
        // iniciando serviço
        startRssService(ServiceConstants.INIT_SERVICE.getFlag());
        checkDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (serviceReceiver == null) serviceReceiver = new ServiceReceiver();
        IntentFilter intentFilter = new IntentFilter(ServiceConstants.DATA_UPDATE.getFlag());
        intentFilter.addAction(ServiceConstants.DATA_ERROR.getFlag());
        intentFilter.addAction(ServiceConstants.XML_ERROR.getFlag());
        registerReceiver(serviceReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        // Referente ao shimmer effect
        super.onPause();
    }

    @Override
    protected void onStop() {
        shimmer(false);
        if (serviceReceiver != null) unregisterReceiver(serviceReceiver);
        super.onStop();
    }

    private void startRssService(String action) {
        requestingFeed.set(true);
        service = new Intent(this,RssService.class);
        service.setAction(action);
        startService(service);
    }

    private void checkDB() {
        Log.d(TAG, "checkDB");
        checkingDB.set(true);
        new Thread(
            () -> {
                NoticiasDB db = NoticiasDB.getInstance(getApplicationContext());
                NoticiasDAO dao = db.obterDAO();
                List<Noticia> noticias = dao.todasNoticias();
                emptyDb.set(noticias.isEmpty());
                checkingDB.set(false);
                if (!emptyDb.get()) {
                    populateRecycler(noticias);
                } else if(!requestingFeed.get()) {
                    // Alterando layout para mostrar os dados ou erro
                    if (checkedDB.get()) {
                        setError("Não há dados para serem mostrados",true);
                    } else {
                        checkedDB.set(true);
                        startRssService(ServiceConstants.DATA_REFRESH.getFlag());
                    }
                }
            }
        ).start();
    }

    private void populateRecycler(List<Noticia> noticias) {
        Log.d(TAG, "populateRecyler");
        runOnUiThread(
            () -> {
                adapter.setNoticias(noticias);
                adapter.notifyDataSetChanged();
                shimmer(false);
                setError("", false);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        );
    }

    private void showToast(String message, int duration) {
        runOnUiThread(
                () -> {
                    Toast.makeText(
                            getApplicationContext(),
                            message,
                            duration
                    ).show();
                }
        );
    }

    // Referente ao shimmer effect
    private void shimmer(boolean show) {
        runOnUiThread(
            () -> {
                if (show) {
                    errorCard.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmer();
                } else {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        );
    }

    private void setError(String message, boolean show) {
        runOnUiThread(
            () ->{
                if (show) {
                    shimmer(false);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    errorCard.setVisibility(View.VISIBLE);
                    errorMessage.setText(message);
                } else {
                    errorCard.setVisibility(View.GONE);
                }
            }
        );
    }

    private void handleBroadCastError(String text) {
        runOnUiThread(
            () -> {
                showToast(text, Toast.LENGTH_LONG);
                if (emptyDb.get() && !checkingDB.get()) {
                    setError(text, true);
                } else {
                    shimmer(false);
                    showToast("Mostrando dados offline", Toast.LENGTH_SHORT);
                }
            }
        );
    }

    private class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String payload = intent.getStringExtra("PAYLOAD");
            boolean state = intent.getBooleanExtra("STATE",false);;
            Log.d(TAG, String.format("onReceive: %s %s %b", action, payload, state));
            if (intent.getAction().equals(ServiceConstants.DATA_UPDATE.getFlag())) {
                // Do stuff - maybe update my view based on the changed DB contents
                requestingFeed.set(false);
                checkDB();
            } else if (intent.getAction().equals(ServiceConstants.DATA_ERROR.getFlag())) {
                requestingFeed.set(false);
                handleBroadCastError(intent.getStringExtra("PAYLOAD"));
            } else if (intent.getAction().equals(ServiceConstants.XML_ERROR.getFlag())) {
                requestingFeed.set(false);
                handleBroadCastError(intent.getStringExtra("PAYLOAD"));
            }
        }
    }

   /* private void updateDB(List<Article> news) {
        new Thread(() -> {
            Noticia[] nots = {};
            List<Noticia> noticias = new ArrayList<>(Arrays.asList(nots));
            // "Casting" de Article List to Noticia List
            for(Article n: news){
                noticias.add(new Noticia(n));
            }

            NoticiasDB db = NoticiasDB.getInstance(this);
            NoticiasDAO dao = db.obterDAO();
            List<Noticia> noticias_db = dao.todasNoticias();
            try {
                dao.inserirNoticias(noticias);
            } catch (Exception err) {
                runOnUiThread(
                    () -> {
                        showToast(err.getMessage(),Toast.LENGTH_LONG);
                    }
                );
            }
            noticias = dao.todasNoticias();
            emptyDb.set(noticias.isEmpty());
            populateRecycler(noticias);

            if (noticias_db.size() != noticias.size() && !emptyDb.get() && !noticias_db.isEmpty()) {
                runOnUiThread(
                    () -> {
                        showToast("Novos dados baixados", Toast.LENGTH_SHORT);
                    }
                );
            }
        }).start();
    }

    private void loadFeed(String url) {
        Log.d(TAG, String.format("loadFeed: %s %b", url, requestingFeed.get()));
        if (!requestingFeed.get()) {

            // Tirando qualquer erro da tela e ativando o efeito shimmer
            runOnUiThread(
                () -> {
                    setError("",false);
                    shimmer(emptyDb.get());
                }
            );
            Parser p = new Parser.Builder().build();
            p.onFinish(
                new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Channel channel) {
                        requestingFeed.set(false);
                        Log.d(TAG, "loadFeed: onTaskCompleted");
                        noticias = channel.getArticles();
                        updateDB(noticias);
                    }

                    @Override
                    public void onError(Exception e) {
                        requestingFeed.set(false);
                        String text = e.getCause().getMessage();
                        Log.e(TAG, String.format("loadFeed: onError %s", text));
                        // Colocando erros na tela caso o banco esteja vazio
                        runOnUiThread(
                            () -> {
                                shimmer(false);
                                swipeRefreshLayout.setRefreshing(false);
                                showToast(text, Toast.LENGTH_LONG);
                                if (emptyDb.get() && !checkingDB.get()) {
                                    setError(text, true);
                                } else {
                                    showToast("Mostrando dados offline", Toast.LENGTH_SHORT);
                                }
                            }
                        );

                    }
                }
            );
            requestingFeed.set(true);
            p.execute(url);
        }
    }*/
}