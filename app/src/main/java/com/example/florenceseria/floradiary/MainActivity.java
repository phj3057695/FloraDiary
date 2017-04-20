
package com.example.florenceseria.floradiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView txtDate,toastText;
    Button btnSave;
    EditText edtContent;
    View toastView;
    ImageView toastIcon, toastIcon2;
    LinearLayout toastLayout;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
    Calendar cal = Calendar.getInstance();
    File myDir;
    String fileName, dir;
    class MydlgClass implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            new DatePickerDialog(MainActivity.this,dp,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
        }
    }
    public void setFileDir(int year, int month, int day){ //
        fileName=Integer.toString(year)+"_"+String.format("%02d",month+1)+"_"+String.format("%02d",day)+".txt";
        dir = myDir.toString()+"/"+fileName;
        String content = readDiary(dir);
        edtContent.setText(content);
    }
    public void myToast(String str, @DrawableRes int res, @ColorInt int colour){
        Toast toast = new Toast(MainActivity.this);
        toastView = View.inflate(MainActivity.this,R.layout.custom_toast,null);
        toastText=(TextView)toastView.findViewById(R.id.toastText);
        toastIcon=(ImageView)toastView.findViewById(R.id.toastIcon);
        toastIcon2=(ImageView)toastView.findViewById(R.id.toastIcon2);
        toastLayout=(LinearLayout)toastView.findViewById(R.id.customToast);
        toastText.setText(str);
        toastLayout.setBackgroundColor(colour);
        toastIcon.setImageResource(res);
        toastIcon2.setImageResource(res);
        toast.setView(toastView);
        toast.show();
    }
    String strResource(@StringRes int str){
        String result = getResources().getString(str);
        return result;
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
    MydlgClass buttonlistener =new MydlgClass();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            String txtFile = data.getStringExtra("txtFile");
            Integer iYear = Integer.parseInt(data.getStringExtra("year"));
            Integer iMonth = Integer.parseInt(data.getStringExtra("month"))-1;
            Integer iDay = Integer.parseInt(data.getStringExtra("day"));
            cal.set(Calendar.YEAR,iYear);
            cal.set(Calendar.MONTH,iMonth);
            cal.set(Calendar.DAY_OF_MONTH,iDay);
            txtDate.setText(sdf.format(cal.getTime()));
            setFileDir(iYear, iMonth, iDay);
            myToast(txtFile+strResource(R.string.reloaded), R.drawable.diary_64, Color.CYAN);
        }
        else if(resultCode==RESULT_CANCELED){
            myToast(strResource(R.string.Quickload_cancel),R.drawable.diary_64,Color.CYAN);
        }
    }

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
        txtDate.setText(sdf.format(cal.getTime()));
        txtDate.setOnClickListener(buttonlistener);

        final String strSDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        myDir = new File(strSDPath+"/mydiary");
        if(!myDir.exists()){
            myDir.mkdir();
            myToast(strResource(R.string.dir_make), R.drawable.diary_64, Color.CYAN);
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
                    myToast(fileName + strResource(R.string.diary_saved), R.drawable.floppy_disk,Color.CYAN);
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
           edtContent.setHint(strResource(R.string.nodiary));
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
        final String content = readDiary(dir);
        switch(item.getItemId()){
            case R.id.action_reload:
                if(content!=null) {
                    edtContent.setText(content);
                    myToast(fileName + strResource(R.string.reloaded), R.drawable.diary_64, Color.CYAN);
                }else{
                    myToast(fileName +" "+ strResource(R.string.no_file_exist), R.drawable.diary_64, Color.CYAN);
                }
                return true;
            case R.id.action_deldiary:
                if(content!=null) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle(strResource(R.string.confirm));
                    date = sdf.format(cal.getTime());
                    dlg.setMessage(date +" " + strResource(R.string.delete_target)+" "+strResource(R.string.delete_question));
                    dlg.setPositiveButton(strResource(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File target = new File(dir);
                            target.delete();
                            myToast(strResource(R.string.deleted),R.drawable.del_icon,Color.CYAN);
                            edtContent.setText(null);
                        }
                    });
                    dlg.setNegativeButton(strResource(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myToast(strResource(R.string.cancel),R.drawable.del_icon,Color.CYAN);
                        }
                    });
                    dlg.show();
                }else{
                    myToast(strResource(R.string.nodiary),R.drawable.floppy_disk,Color.CYAN);
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
            case R.id.action_quickload:
                try {
                    Intent intent = new Intent(getApplication(), SecondActivity.class);
                    intent.putExtra("txtDir",myDir.toString());
                    startActivityForResult(intent, 0);
                }catch(Exception e){
                    myToast(strResource(R.string.nodiary),R.drawable.floppy_disk,Color.CYAN);
                }
        }
        return false;
    }
}
