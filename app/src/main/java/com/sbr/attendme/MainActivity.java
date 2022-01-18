package com.sbr.attendme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CreateTeacher.DismissListener,CreateStudent.DismissListener{
    static DBHelper db;
    CreateTeacher ct;
    CreateStudent cs;
    public static String MY_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MY_ID= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db=new DBHelper(this);
        if(db.tableExists(DBHelper.TEACHER_TABLE_NAME)) {
            Toast.makeText(this,"teacher exist",Toast.LENGTH_SHORT).show();
        }
        else if(db.tableExists(DBHelper.STUDENT_TABLE_NAME)) {
            Toast.makeText(this,"student exist",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Toast.makeText(this,"going to hell",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}