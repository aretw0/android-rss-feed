package br.ufpe.cin.android.rss;

import android.view.View;
import android.widget.TextView;

public class ItemRssViewHolder {
    TextView titulo = null;

    public ItemRssViewHolder(View v) {
        this.titulo = v.findViewById(R.id.tituloNoticia);
    }
}
