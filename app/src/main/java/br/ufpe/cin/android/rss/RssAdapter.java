package br.ufpe.cin.android.rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prof.rssparser.Article;

import java.util.List;

public class RssAdapter extends RecyclerView.Adapter<ItemRssViewHolder> {

    List<Article> noticias;
    Context c;

    public RssAdapter(Context c, List<Article> noticias) {
        this.c = c;
        this.noticias = noticias;
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    public Object getItem(int i) {
        return noticias.get(i);
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
        Article notice = noticias.get(position);
        viewHolder.titulo.setText(notice.getTitle());
        viewHolder.dataPublicacao.setText(notice.getPubDate());
        /*
        Picasso.get().load(notice.getImage())
                .placeholder(R.drawable.ic_image_unloaded)
                .error(R.drawable.ic_image_unloaded_error)
                .resize(140, 140)
                .centerCrop()
                .into(viewHolder.thumb);*/

    }
}
