package com.example.udptest;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class WaypointsListView extends LinearLayout {
    ArrayList<String> files = new ArrayList<String>();
    Set<String> filesSet = new HashSet<>(); // set only used for dealing with possible duplicates
    ArrayAdapter adapter;
    ListView listView;
    String TAG = "WaypointsListView";
    MainActivity ma;

    WaypointsListView(Context context) {
        super(context);
        ma = (MainActivity) context;
        listView = new ListView(context);
        adapter = new ArrayAdapter<String>(context, R.layout.activity_listview, files);
        files.add("test 1");
        files.add("test 2");
        files.add("test 3");

        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);

                Log.d(TAG, "onItemLongClick: i " + i + " l " + l + " string: " + selected);
                ma.sendMessage("OPEN_FILE," + selected);

                return false;
            }
        });
        addView(listView);
    }

    void addFileName(String name) {
        int count = filesSet.size();
        filesSet.add(name);
        int diff = filesSet.size() - count;
        if (diff > 0) {
            files.add(name);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "adding to files list: " + name);
        }
    }
}
