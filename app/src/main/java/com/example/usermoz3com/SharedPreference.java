package com.example.usermoz3com;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SharedPreference {

    public static final String PREFS_NAME = "GIFT_APP";
    public static final String FAVORITES  = "GIFTS";

    public SharedPreference() {
        super();
    }

    // THIS FOUR METHODS ARE USED FOR MAINTAINING FAVORITES.
    public void saveFavorite(Context context, List<HashMap<String,Object>> item) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(item);
        editor.putString(FAVORITES, jsonFavorites);
        editor.apply();
    }

    public void addFavorite(Context context, HashMap<String,Object> item) {
        List<HashMap<String,Object>> favorites = getFavorites(context);
        if (favorites == null) {
            favorites = new ArrayList<>();
            favorites.add(item);
            saveFavorite(context, favorites);
        }
        else {
            favorites.add(item);
            saveFavorite(context,favorites);
        }
    }

    public void removeFavorite(Context context, int index) {
        ArrayList<HashMap<String,Object>> favorites = getFavorites(context);
        if (favorites != null && favorites.size()>0 && index >= 0) {
            //favorites = new ArrayList<>();
            favorites.remove(index);
            saveFavorite(context, favorites);
        }
    }

    public void removeFavorite(Context context, HashMap<String,Object> item) {
        ArrayList<HashMap<String,Object>> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(item);
            saveFavorite(context, favorites);

        }
    }

    public void removeallFavorite(Context context) {
        ArrayList<HashMap<String,Object>> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.clear();
            saveFavorite(context, favorites);
        }
    }
    public int getFavoritewithname(Context context, String name) {
        ArrayList<HashMap<String,Object>> favorites = getFavorites(context);
        for (int i = 0 ; i<favorites.size();i++){
            if (favorites.get(i).get("name").toString().equals(name)){
                return i;
            }
        }
        return -1;
    }



    public ArrayList<HashMap<String,Object>> getFavorites(Context context) {
        SharedPreferences settings;
        List<HashMap<String,Object>> favorites;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            HashMap<String,Object>[] favoriteItems = gson.fromJson(jsonFavorites, HashMap[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else {
            return new ArrayList<>();
        }
        return (ArrayList<HashMap<String,Object>>) favorites;
    }
}