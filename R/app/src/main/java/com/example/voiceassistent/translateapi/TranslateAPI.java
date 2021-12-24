package com.example.voiceassistent.translateapi;

import android.util.Log;

import androidx.core.util.Consumer;

import com.example.voiceassistent.AI;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.content.ContentValues.TAG;


public class TranslateAPI {

    static final String ERROR = AI.TRANSLATE_ERROR;

    public static void getTranslate(String langFrom, String langTo, String text, final Consumer<String> callback){
        Observable.fromCallable(() -> {
            String resp = null;
            try {
                String url = "https://translate.googleapis.com/translate_a/single?" + "client=gtx&" + "sl=" +
                        langFrom + "&tl=" + langTo + "&dt=t&q=" + URLEncoder.encode(text, "UTF-8");
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer respons = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    respons.append(inputLine);
                }
                in.close();
                resp = respons.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String temp = "";
            if (resp == null) {
                return ERROR;
            } else {
                try {
                    JSONArray main = new JSONArray(resp);
                    JSONArray total = (JSONArray) main.get(0);
                    for (int j = 0; j < total.length(); j++) {
                        JSONArray currentLine = (JSONArray) total.get(j);
                        temp = temp + currentLine.get(0).toString();
                    }
                    Log.d(TAG, "onPostExecute: " + temp);

                    if (temp.length() > 2) {
                        return temp;
                    } else {
                        return ERROR;
                    }
                } catch (JSONException e) {
                    return ERROR;
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResult -> {
                    callback.accept(String.join(", ", listResult));
                });
    }

}