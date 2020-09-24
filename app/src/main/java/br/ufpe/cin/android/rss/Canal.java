package br.ufpe.cin.android.rss;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "canais")
public class Canal {
    @PrimaryKey @NonNull
    String urlFeed;
    String titulo;
    String descricao;
    String linkSite;
    String imagemURL;
    int imagemLargura;
    int imagemAltura;
    List<Noticia> noticias;

    public Canal(String urlFeed, String titulo, String descricao, String link, String imagemURL, int imagemLargura, int imagemAltura, List<Noticia> noticias) {
        this.urlFeed = urlFeed;
        this.titulo = titulo;
        this.descricao = descricao;
        this.linkSite = link;
        this.imagemURL = imagemURL;
        this.imagemLargura = imagemLargura;
        this.imagemAltura = imagemAltura;
        this.noticias = noticias;
    }

    @Override
    public String toString() {
        return titulo + " => " + linkSite;
    }
}
