package br.ufpe.cin.android.rss;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.prof.rssparser.Article;

import java.util.List;

@Entity(tableName = "noticias")
public class Noticia {
    @PrimaryKey @NonNull
    private String link;
    private String titulo;
    private String descricao;
    @TypeConverters(Converter.class)
    private List<String> categorias;
    private String data;
    private String imagem;

    public Noticia(@NonNull String link, String titulo, String descricao, List<String> categorias, String data, String imagem) {
        this.link = link;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categorias = categorias;
        this.data = data;
        this.imagem = imagem;
    }

    public Noticia(@NonNull Article news) {
        this.link = news.getLink();
        this.titulo = news.getTitle();
        this.descricao = news.getDescription();
        this.categorias = news.getCategories();
        this.data = news.getPubDate();
        this.imagem = news.getImage();
    }

    @NonNull
    public String getLink() {
        return link;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    @TypeConverters(Converter.class)
    public List<String> getCategorias() {
        return categorias;
    }

    public String getData() {
        return data;
    }

    public String getImagem() {
        return imagem;
    }
}
