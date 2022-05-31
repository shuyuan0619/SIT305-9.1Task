package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SugActivity extends AppCompatActivity implements View.OnClickListener, OnGetSuggestionResultListener {
    private SuggestionSearch s_search_res = null;
    private EditText city_edit_et = null;
    private AutoCompleteTextView et_auto_complete = null;
    private ListView s_sug_list;
    public List<HashMap<String, String>> suggest = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_sug);


        PoiSugSearch();
        initView();
        request();

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }
        suggest.clear();
        for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
            if (info.getKey() != null && info.getDistrict() != null && info.getCity() != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put("key",info.getKey());
                map.put("city",info.getCity());
                map.put("dis",info.getDistrict());
                map.put("addr",info.getAddress());
                suggest.add(map);
            }
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(),
                suggest,
                R.layout.item_layout,
                new String[]{"key", "city","dis"},
                new int[]{R.id.sug_key, R.id.sug_city, R.id.sug_dis});

        s_sug_list.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }


    private void PoiSugSearch(){
        s_search_res = SuggestionSearch.newInstance();
        s_search_res.setOnGetSuggestionResultListener(this);
    }


    private void initView(){
        s_sug_list = (ListView) findViewById(R.id.sug_list);
        et_auto_complete = (AutoCompleteTextView) findViewById(R.id.editText);
        city_edit_et = findViewById(R.id.edit_city);
        et_auto_complete.setThreshold(1);
        s_sug_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String addr = suggest.get(i).get("addr");
                Intent intent = new Intent();
                intent.putExtra("addr",addr);
                setResult(1,intent);
                finish();
            }
        });
    }

    private void request(){
        et_auto_complete.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                s_search_res.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city(city_edit_et.getText().toString()));
            }
        });
    }


}