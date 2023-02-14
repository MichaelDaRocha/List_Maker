package com.example.list_maker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.list_tracker.BuildConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MongoClient {
    private static String className = "MongoClient";
    private static OkHttpClient client = new OkHttpClient().newBuilder().build();
    private static MediaType json = MediaType.parse("application/json");

    private MongoClient(){}

    public static void mongoFind(String android_id, Callback func){
        String jsonStr = "{\"collection\":\"Items\",\"database\":\"Android-List-App\",\"dataSource\":\"Cluster0\",\"filter\":{\"id\":\""+ android_id +"\"}}";
        RequestBody body = RequestBody.create(json, jsonStr);
        Request req = new Request.Builder()
                .url("https://data.mongodb-api.com/app/data-utqsm/endpoint/data/v1/action/find")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Access-Control-Request-Headers", "*")
                .addHeader("api-key", BuildConfig.apiKey)
                .build();
        client.newCall(req).enqueue(func);
    }

    public static void mongoInsertMany(ArrayList<Item> added) throws IOException {
        if(added.size() == 0)
            return;

        String jsonStr = insertManyStringFmt(added);
        Log.v(className+"/mongoInsertMany", jsonStr);

        RequestBody body = RequestBody.create(json, jsonStr.getBytes(StandardCharsets.UTF_8));
        Request req = new Request.Builder().get()
                .url("https://data.mongodb-api.com/app/data-utqsm/endpoint/data/v1/action/insertMany")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Access-Control-Request-Headers", "*")
                .addHeader("api-key", BuildConfig.apiKey)
                .build();
        client.newCall(req).enqueue(new PrintCallback(added));
    }

    public static void mongoDeleteMany(ArrayList<Item> deleted) throws IOException {
        if(deleted.size() == 0)
            return;

        String jsonStr = deleteManyStringFmt(deleted);
        Log.v(className+"/mongoDeleteMany", jsonStr);

        RequestBody body = RequestBody.create(json, jsonStr.getBytes(StandardCharsets.UTF_8));
        Request req = new Request.Builder().get()
                .url("https://data.mongodb-api.com/app/data-utqsm/endpoint/data/v1/action/deleteMany")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Access-Control-Request-Headers", "*")
                .addHeader("api-key", BuildConfig.apiKey)
                .build();
        client.newCall(req).enqueue(new PrintCallback(deleted));
    }

    private static String insertManyStringFmt(ArrayList<Item> added){
        String start = "{\"dataSource\":\"Cluster0\",\"database\":\"Android-List-App\",\"collection\":\"Items\",\"documents\":[";

        for(Item item : added){
            start += item.toJsonString() + ",";
        }

        return start.substring(0, start.length()-1) + "]}";
    }

    private static String deleteManyStringFmt(ArrayList<Item> deleted){
        String start = "{\"dataSource\":\"Cluster0\",\"database\":\"Android-List-App\",\"collection\":\"Items\",\"filter\":{\"_id\":{\"$in\": [";

        for(Item item : deleted){
            start += "{\"$oid\": \"" + item.getMongo_id() + "\"},";
        }

        return start.substring(0, start.length()-1) + "]}}}";
    }

    private static class PrintCallback implements Callback {
        private static String className = "PrintCallback";
        private ArrayList<Item> ref;

        public PrintCallback(ArrayList<Item> ref){
            super();
            this.ref = ref;
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e(className, e.toString());
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            Log.d("TEST", call.request().headers().toString());
            if(!response.isSuccessful()) {
                onFailure(call, new IOException(response.body().string()));
            }

            Log.v(className, "onResponse Success");
            ref.clear();
        }
    }
}
