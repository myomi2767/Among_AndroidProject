package com.example.among.function;


public class NurseViewItem {
    String name;

    public NurseViewItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NurseViewItem{" +
                "name='" + name + '\'' +
                '}';
    }
}
