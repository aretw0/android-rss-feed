package br.ufpe.cin.android.rss;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prof.rssparser.Article;

public class ItemRssViewHolder extends RecyclerView.ViewHolder {
    TextView titulo;
    // Adicionado outros elementos
    TextView dataPublicacao;
    ImageView imagem;

    public ItemRssViewHolder(@NonNull View item) {
        super(item);
        this.titulo = item.findViewById(R.id.titulo);
        this.dataPublicacao = item.findViewById(R.id.dataPublicacao);
        this.imagem = item.findViewById(R.id.imagem);

        // ponto 2 - intent implícita para o browser ao clicar em algum item
        item.setOnClickListener(
                this::onClick
        );
    }

    private void onClick(View v) {
        // Capturando a recyclerView
        RecyclerView conteudoRSS = (RecyclerView) v.getParent();
        // Capturando o Adapter
        RssAdapter rssAdapter = (RssAdapter) conteudoRSS.getAdapter();
        // Capturando a posição do item no Adapter
        int position = getAdapterPosition();
        // O adapter guarda objetos do tipo noticia, posso pegar a noticia da posição equivalente.
        Noticia n = (Noticia) rssAdapter.getItem(position);

        //Tendo acesso a um objeto noticia, podemos chamar qualquer método disponível.
        // Preparando intent
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_BROWSABLE);
        i.setData(Uri.parse(n.getLink()));
        v.getContext().startActivity(i);
    }
}
