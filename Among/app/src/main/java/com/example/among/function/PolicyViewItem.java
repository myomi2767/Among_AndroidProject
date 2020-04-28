package com.example.among.function;


public class PolicyViewItem {
    int image;
    String name;
    String pre;
    public PolicyViewItem(int image, String name,String pre){
        this.image = image;
        this.name = name;
        this.pre = pre;
    }


    public int getImage() {
        return image;
    }


    public String getName() {
        return name;
    }


    public String getPre() {
        return pre;
    }

    @Override
    public String toString() {
        return "PolicyViewItem{" +
                "image=" + image +
                ", name='" + name + '\'' +
                ", pre='" + pre + '\'' +
                '}';
    }

}
