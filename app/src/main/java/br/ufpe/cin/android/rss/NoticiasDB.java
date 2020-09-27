package br.ufpe.cin.android.rss;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities={Noticia.class}, version = 1)
public abstract class NoticiasDB extends RoomDatabase {
    private static final String DB_NAME = "noticias.db";

    abstract NoticiasDAO obterDAO();

    private static volatile NoticiasDB INSTANCE;

    synchronized static NoticiasDB getInstance(Context c) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(c, NoticiasDB.class, DB_NAME).build();
        }
        return INSTANCE;
    }
}
