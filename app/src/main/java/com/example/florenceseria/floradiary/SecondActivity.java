package com.example.florenceseria.floradiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by FlorenceSeria on 30/11/2015.
 */
public class SecondActivity extends AppCompatActivity {
    ArrayList<String> diaryList;
    String selectedTxt;
    Button btnQload, btnCancel;
    String strResource(@StringRes int str){
        String result = getResources().getString(str);
        return result;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        setTitle(strResource(R.string.QuickLoadList));
        btnQload=(Button)findViewById(R.id.btnquickload);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        diaryList = new ArrayList<String>();
        Intent inIntent = getIntent();
        final Intent outIntent = new Intent(getApplicationContext(),MainActivity.class);
        final String strPath = inIntent.getStringExtra("txtDir");
        //String selectedTxt;
        File[] listFiles = new File(strPath).listFiles();   //MainActivity에서 온 diary파일이 들어있는 경로의 list를 나타냅니댜.
        String fileName, extName;

        for(File file : listFiles){
            fileName = file.getName();
            extName = fileName.substring(fileName.length()-3);
            if(extName.equals((String)"txt"))
                diaryList.add(fileName);
        }
try {
    ListView listViewDiary = (ListView) findViewById(R.id.listView1);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, diaryList);
    listViewDiary.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    listViewDiary.setAdapter(adapter);
    listViewDiary.setItemChecked(0, true);
    selectedTxt = diaryList.get(0);
    listViewDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedTxt = diaryList.get(position);
        }
    });//yyyy-mm-dd
    btnQload.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            outIntent.putExtra("txtFile", selectedTxt);
            String year = selectedTxt.substring(0, 4);
            String month = selectedTxt.substring(5, 7);
            String day = selectedTxt.substring(8, 10);
            outIntent.putExtra("year", year);
            outIntent.putExtra("month", month);
            outIntent.putExtra("day", day);
            setResult(RESULT_OK, outIntent);
            finish();
        }
    });
    btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_CANCELED, outIntent);
            finish();
        }
    });
}catch(Exception e){
    setResult(RESULT_CANCELED, outIntent);
    finish();
}
    }
}
