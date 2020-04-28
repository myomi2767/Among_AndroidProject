package com.example.among.function;

import java.util.ArrayList;
import java.util.List;

public class ComItem {
    String data;

    public ComItem(String data){
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ComItem{" +
                "data='" + data + '\'' +
                '}';
    }
    public static List<ComItem> createList(){
        ArrayList<ComItem> recycler_data = new ArrayList<ComItem>();
         for (int i =1;i<=10;i++){
             ComItem item = new ComItem("content"+i);
             recycler_data.add(item);
    }
    return recycler_data;
    }

}
