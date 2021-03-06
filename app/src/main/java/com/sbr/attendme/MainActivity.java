package com.sbr.attendme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CreateTeacher.DismissListener,CreateStudent.DismissListener{
    public static DBHelper db;
    private CreateTeacher ct;
    private CreateStudent cs;
    public static String MY_ID;
    public static final int SERVER_PORT = 9081;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MY_ID= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db=new DBHelper(this);
        checkPermission();
        if(db.tableExists(DBHelper.TEACHER_TABLE_NAME)) {
            Intent intent = new Intent(this, ListOfClasses.class);
            startActivity(intent);
            finish();
        }
        else if(db.tableExists(DBHelper.STUDENT_TABLE_NAME)) {
            Intent intent=new Intent(this,StuDiscActivity.class);
            startActivity(intent);
            finish();
        }
        ct=new CreateTeacher(db);
        ((Button)findViewById(R.id.createteacher)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ct.show(MainActivity.this.getSupportFragmentManager(),null);
            }
        });
        cs=new CreateStudent(db);
        ((Button)findViewById(R.id.createstudent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cs.show(MainActivity.this.getSupportFragmentManager(),null);
            }
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            checkPermission();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog,int i) {
        if(i==0) {
            Intent intent = new Intent(this, ListOfClasses.class);
            startActivity(intent);
            finish();
        }
        else if(i==1) {
            Intent intent=new Intent(this,StuDiscActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog,int i) {

    }
}