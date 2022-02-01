package com.sbr.attendme;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StuDiscActivity extends AppCompatActivity {
    private WifiManager manager;
    private WifiP2pManager p2pmanager;
    private WifiP2pManager.Channel channel;
    private RippleBackground studentRipple;
    private ListView stuTeaList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arr;
    private Student student;
    final HashMap<String,Map> teacherMap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_disc);
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        p2pmanager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        studentRipple =(RippleBackground) findViewById(R.id.stu_ripple);
        stuTeaList=(ListView)findViewById(R.id.stu_tea_list);
        arr=new ArrayList<>();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);
        stuTeaList.setAdapter(adapter);
        channel = p2pmanager.initialize(this, getMainLooper(), null);
        check();
    }
    public void check() {
        if(manager.isWifiEnabled()) {
            proceed();
        }
        else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                Toast.makeText(this,"Please turn on WiFi",Toast.LENGTH_SHORT).show();
                Intent panel=new Intent(Settings.Panel.ACTION_WIFI);
                //startActivityForResult(panel,1);
                wifiOpener.launch(panel);
            }
            else {
                manager.setWifiEnabled(true);
            }
        }
    }

    ActivityResultLauncher<Intent> wifiOpener= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 0) {
                        Intent data = result.getData();
                        check();
                    }
                }
            });

    public void proceed() {
        Toast.makeText(this,"Searching for class",Toast.LENGTH_SHORT).show();
        startRegistration();
        discoverService();
        /*RippleBackground rp=(RippleBackground) findViewById(R.id.rippletest);
        ImageView imageView=(ImageView)findViewById(R.id.search_bud);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rp.isRippleAnimationRunning())
                    rp.stopRippleAnimation();
                else
                    rp.startRippleAnimation();
            }
        });*/
    }
    private void startRegistration() {
        student=MainActivity.db.getStudents().get(0);
        Map<String,String> record=new HashMap<>();
        record.put("listenport",Integer.toString(MainActivity.SERVER_PORT));
        record.put("appname",""+R.string.app_name);
        record.put("type",""+R.string.student);
        record.put("student",""+student.getName());
        record.put("student_id",student.getId());
        record.put("student_roll",""+student.getRoll());
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);
        p2pmanager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }
    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String s, Map<String, String> map, WifiP2pDevice wifiP2pDevice) {
                if(map.get("appname").equals(R.string.app_name)&&map.get("listenport").equals(Integer.toString(MainActivity.SERVER_PORT))&&map.get("type").equals(R.string.teacher)) {
                    teacherMap.put(wifiP2pDevice.deviceAddress,map);
                }
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {

            @Override
            public void onDnsSdServiceAvailable(String s, String s1, WifiP2pDevice wifiP2pDevice) {
                if(teacherMap.containsKey(wifiP2pDevice.deviceAddress)) {
                    arr.add((String) ((Map)teacherMap.get(wifiP2pDevice.deviceAddress)).get("teacher"));
                    adapter.notifyDataSetChanged();
                }
            }
        };
        p2pmanager.setDnsSdResponseListeners(channel,servListener,txtRecordListener);
        ((ImageView)findViewById(R.id.search_bud1)).setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .textColor(Color.WHITE)
                .endConfig().buildRound(""+student.getName().charAt(0), Color.BLACK));
        studentRipple.startRippleAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(studentRipple.isRippleAnimationRunning())
            studentRipple.stopRippleAnimation();
    }
}