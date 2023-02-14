package com.example.list_maker;

public class Item {
    private String title;
    private String mongo_id;
    private static String android_id;
    private Boolean checked;

    public Item(String title, String mongo_id){
        this.title = title;
        this.mongo_id = mongo_id;
        this.checked = false;
    }

    public Item(String title){
        this.title = title;
        this.mongo_id = "";
        this.checked = false;
    }

    public String getTitle(){return title;}
    public Boolean getChecked(){return checked;}
    public String getMongo_id(){return mongo_id;}


    public void setTitle(String title){this.title = title;}
    public void setChecked(Boolean checked){this.checked = checked;}
    public static void setAndroid_id(String android_id){Item.android_id = android_id;}


    @Override
    public String toString(){
        return "List Item: " + title;
    }

    public String toJsonString(){
        if(mongo_id.isEmpty())
            return "{\"item\": \"" + this.title + "\", \"id\": \"" + android_id + "\"}";
        return "{\"_id\": \"" + mongo_id + "\", \"item\": \"" + this.title + "\", \"id\": \"" + android_id + "\"}";
    }
}
