package br.ufpe.cin.android.rss;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemRssViewHolder extends RecyclerView.ViewHolder {
    TextView titulo = null;
    // Adicionado outros elementos
    TextView dataPublicacao;
    ImageView imagem;

    public ItemRssViewHolder(@NonNull View v) {
        super(v);
        this.titulo = v.findViewById(R.id.titulo);
        this.dataPublicacao = v.findViewById(R.id.dataPublicacao);
        this.imagem = v.findViewById(R.id.imagem);
    }
}
