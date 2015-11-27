
package com.example.florenceseria.floradiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    View dialogView;
    TextView txtDate;
    Button btnSave;
    EditText edtContent;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
    Calendar cal = Calendar.getInstance();
    File myDir;
//    int cYear;
//    int cMonth;
//    int cDay;
    String fileName, dir;
//    class dlgbtnClass implements DialogInterface.OnClickListener{
//        public static final int BUTTON_POSITIVE = -1;
//
//        /**
//         * The identifier for the negative button.
//         */
//        public static final int BUTTON_NEGATIVE = -2;
//        File target;
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            if(which==BUTTON_POSITIVE){
//                target = new File(dir);
//                target.delete();
//                Toast.makeText(getApplicationContext(),sdf.format(cal.getTime())+" Diary file has been deleted,",Toast.LENGTH_SHORT).show();
//                edtContent.setText(null);
//            }
//            else{
//                //nothing;
//            }
//        }
//    }
    class MydlgClass implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            new DatePickerDialog(MainActivity.this,dp,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
        }
    }
    public void setFileDir(int year, int month, int day){
        fileName=Integer.toString(year)+"_"+Integer.toString(month+1)+"_"+Integer.toString(day)+".txt";
        dir = myDir.toString()+"/"+fileName;
        String content = readDiary(dir);
        edtContent.setText(content);
    }

    DatePickerDialog.OnDateSetListener dp = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            txtDate.setText(sdf.format(cal.getTime()));
            setFileDir(year, monthOfYear, dayOfMonth);
        }
    };
    MydlgClass buttonlistener;
    //dlgbtnClass dlgbuttonlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDate=(TextView)findViewById(R.id.txtDate);
        btnSave=(Button)findViewById(R.id.btnSave);
        edtContent=(EditText)findViewById(R.id.edtContent);
        buttonlistener = new MydlgClass();
        txtDate.setText(sdf.format(cal.getTime()));
        txtDate.setOnClickListener(buttonlistener);

        final String strSDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        myDir = new File(strSDPath+"/mydiary");
        if(!myDir.exists()){
            myDir.mkdir();
            Toast.makeText(getApplicationContext(),"mydiary Directory has been added",Toast.LENGTH_SHORT).show();
        }
        setFileDir(cYear, cMonth, cDay);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    FileOutputStream fos = new FileOutputStream(dir);
                    String str = edtContent.getText().toString();
                    fos.write(str.getBytes());
                    fos.close();
                    Toast.makeText(getApplicationContext(),fileName+" is saved.",Toast.LENGTH_SHORT).show();
                }catch(IOException e){}
            }
        });
    }

   String readDiary(String dir) {
       String diaryStr = null;
       FileInputStream inFs;
       try{
           inFs = new FileInputStream(dir);
           byte[] txt = new byte[inFs.available()];
           inFs.read(txt);
           diaryStr = (new String(txt)).trim();
           inFs.close();
       }catch(Exception e) {
           edtContent.setHint("No Diary");

       }
       return diaryStr;
   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String date;
        String content = readDiary(dir);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_reload:
                if(content!=null) {
                    edtContent.setText(content);
                }else{
                    Toast.makeText(getApplicationContext(),"Diary is empty...",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_deldiary:
                if(content!=null) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("Confirm");
                    date = sdf.format(cal.getTime());
                    dlg.setMessage("Are you sure to delete " + date + " diary?");
                    dlg.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File target = new File(dir);
                            target.delete();
                            Toast.makeText(getApplicationContext(),sdf.format(cal.getTime())+" Diary file has been deleted,",Toast.LENGTH_SHORT).show();
                            edtContent.setText(null);
                        }
                    });
                    dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                }else{
                    Toast.makeText(getApplicationContext(),"Diary is empty...",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_sizelarge:
                edtContent.setTextSize(40);
                return true;
            case R.id.action_sizemedium:
                edtContent.setTextSize(20);
                return true;
            case R.id.action_sizesmall:
                edtContent.setTextSize(10);
                return true;
        }
        return false;
    }
}
