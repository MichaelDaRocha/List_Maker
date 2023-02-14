package com.example.list_maker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.list_tracker.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Callback {
    private final ListAdapter listAdapter = new ListAdapter(new ArrayList<>());
    private EditText editText;
    private final String ClassName = "MainActivity";

    private final ArrayList<Item> mongo_add = new ArrayList<>();
    private final ArrayList<Item> mongo_del = new ArrayList<>();
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText  = findViewById(R.id.etListEdit);

        mongo_add.clear();
        mongo_del.clear();


        this.android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Item.setAndroid_id(android_id);


        RecyclerView rvListItems = findViewById(R.id.rvListItems);
        rvListItems.setAdapter(listAdapter);
        rvListItems.setLayoutManager(new LinearLayoutManager(this));


        Button btAdd = findViewById(R.id.btAdd);
        Button btDel = findViewById(R.id.btDelete);
        btAdd.setOnClickListener(this);
        btDel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btAdd:
                addToggle();
                break;
            case R.id.btDelete:
                deleteToggle();
                break;
        }
    }

    private void addToggle(){
        final String title = editText.getText().toString();
        if(!title.isEmpty()){
            Item newItem = new Item(title);
            listAdapter.addItem(newItem);
            editText.getText().clear();
            mongo_add.add(newItem);
        }
    }

    private void deleteToggle(){
        ArrayList<Item> del = listAdapter.deleteItems();
        for(Item item : del){
            if(item.getMongo_id().isEmpty()){
                mongo_add.remove(item);
            } else{
                mongo_del.add(item);
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        Log.v(ClassName, "onFailure");
        Log.e(ClassName, e.toString());
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response){
        Log.v(this.getLocalClassName(), "onResponse");
        if(!response.isSuccessful()) {
            onFailure(call, new IOException(response.headers().toString()));
            return;
        }

        try {
            JSONObject json = new JSONObject(response.body().string());
            JSONArray docs = json.getJSONArray("documents");
            ArrayList<Item> added = new ArrayList<>();

            for(int i = 0, len = docs.length(); i < len; ++i){
                JSONObject itemJson = docs.getJSONObject(i);
                Item item = new Item(itemJson.getString("item"), itemJson.getString("_id"));
                added.add(item);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.setItemsSilent(new ArrayList<>());
                    listAdapter.addItems(added);
                }
            });
            Log.v(ClassName, "Successfully Found Items");
        } catch(Exception err){
            Log.e(ClassName, err.toString());
            return;
        }

        response.body().close();
    }

    @Override
    protected  void onResume(){
        Log.v("onResume", "resume");
        super.onResume();
        MongoClient.mongoFind(android_id, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            MongoClient.mongoInsertMany(mongo_add);
            MongoClient.mongoDeleteMany(mongo_del);
        } catch(IOException err){
            Log.e(ClassName, err.toString());
        }
    }
}