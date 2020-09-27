package br.ufpe.cin.android.rss;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converter {
    @TypeConverter
    public String fromCategoriasList(List<String> categorias) {
        if (categorias == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        String json = gson.toJson(categorias, type);
        return json;
    }

    @TypeConverter
    public List<String> toCategoriasList(String categoria) {
        if (categoria == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> categorias = gson.fromJson(categoria, type);
        return categorias;
    }
}