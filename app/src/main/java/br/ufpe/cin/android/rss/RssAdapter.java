package br.ufpe.cin.android.rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.prof.rssparser.Article;

import java.util.List;

public class RssAdapter extends RecyclerView.Adapter<ItemRssViewHolder> {

    List<Noticia> noticias;
    Context c;

    public RssAdapter(Context c, List<Noticia> noticias) {
        this.c = c;
        this.noticias = noticias;
    }

    public RssAdapter(Context c) {
        this.c = c;
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    public Object getItem(int i) {
        return noticias.get(i);
    }

    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }

    @NonNull
    @Override
    public ItemRssViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemRssViewHolder(
            LayoutInflater.from(c).inflate(R.layout.linha, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRssViewHolder viewHolder, int position) {
        Noticia notice = noticias.get(position);
        viewHolder.titulo.setText(notice.getTitulo());
        viewHolder.dataPublicacao.setText(notice.getData());

        // Configuração do glide para carregamento das imagens
        RequestOptions requestOption = new RequestOptions()
                .fallback(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_cloud_off_24)
                .placeholder(R.drawable.ic_baseline_cloud_download_24)
                .centerCrop();

        Glide.with(this.c)
                .load(notice.getImagem())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOption)
                .into(viewHolder.imagem);
    }
}
