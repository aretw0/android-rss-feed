package br.ufpe.cin.android.rss;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "noticias")
public class Noticia {
    @PrimaryKey @NonNull
    String link;
    String titulo;
    String descricao;
    List<String> categorias;
    String data;

    public Noticia(@NonNull String link, String titulo, String descricao, List<String> categorias, String data) {
        this.link = link;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categorias = categorias;
        this.data = data;
    }
}
