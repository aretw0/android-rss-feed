package br.ufpe.cin.android.rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.prof.rssparser.Article;

import java.util.List;

public class RssAdapter extends BaseAdapter {

    List<Article> noticias;
    Context c;

    public RssAdapter(Context c, List<Article> noticias) {
        this.c = c;
        this.noticias = noticias;
    }

    @Override
    public int getCount() {
        return noticias.size();
    }

    @Override
    public Object getItem(int i) {
        return noticias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        View v;
        ItemRssViewHolder viewHolder;
        if (view==null) {
            v = LayoutInflater.from(c).inflate(R.layout.item,parent,false);
            viewHolder = new ItemRssViewHolder(v);
            v.setTag(viewHolder);
        }
        else {
            v = view;
            viewHolder = (ItemRssViewHolder) v.getTag();
        }

        Article noticia = noticias.get(i);
        viewHolder.titulo.setText(noticia.getTitle());

        return v;
    }
}
