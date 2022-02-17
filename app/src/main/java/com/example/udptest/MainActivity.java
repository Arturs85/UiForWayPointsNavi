package com.example.udptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {


    private TextView mTextViewReplyFromServer;
    // private TextView mTextViewGyro;
    private TextView deltaYawTextView;
    private EditText mEditTextSendMessage;
    private TabLayout.Tab tabAuto;
    private TabLayout.Tab tabManual;
    TabLayout.Tab tabStepResponse;
    TabLayout.Tab tabFilesList;

    private TabLayout tabLayout;
    FrameLayout frameLayout;
    volatile boolean isReceiving = false;
    DatagramSocket ds = null;
    ControlerView controlerView;
    PidTuneView pidTuneView;
    WaypointsListView waypointsListView;
    static int screenWidth;
    static int screenHeight;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        deltaYawTextView = new TextView(this);
        deltaYawTextView.setText("deltaYaw");
        actionBar.setCustomView(deltaYawTextView);
        actionBar.setDisplayShowCustomEnabled(true);
        Button buttonSend = (Button) findViewById(R.id.btn_send);
        Button buttonSave = (Button) findViewById(R.id.buttonSavePoints);
        Button buttonAdd = (Button) findViewById(R.id.buttonAddPoint);
        Button buttonAddFoto = (Button) findViewById(R.id.buttonAddFotoPoint);
        // mEditTextSendMessage = (EditText) findViewById(R.id.edt_send_message);
        mTextViewReplyFromServer = (TextView) findViewById(R.id.tv_reply_from_server);
        mTextViewReplyFromServer.setMovementMethod(new ScrollingMovementMethod());
        // mTextViewGyro = findViewById(R.id.textView);
        // tabItemAuto = findViewById(R.id.tabAuto);
        // tabItemManual.view = findViewById(R.id.tabManual);
        tabLayout = findViewById(R.id.tabs);
        buttonSend.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonAddFoto.setOnClickListener(this);
        initSocket();
        tabLayout.addOnTabSelectedListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        Log.d("main", "screen: " + screenWidth + " x " + screenHeight);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        controlerView = new ControlerView(this);
        pidTuneView = new PidTuneView(this);
        waypointsListView = new WaypointsListView(this);
        frameLayout = findViewById(R.id.frame);

//tabItemManual.view
        tabAuto = tabLayout.newTab();
        tabAuto.setText("AUTO");
        tabManual = tabLayout.newTab();
        tabManual.setText("MANUAL");
        tabStepResponse = tabLayout.newTab();
        tabStepResponse.setText("STEP");
        tabFilesList = tabLayout.newTab();
        tabFilesList.setText("FL");
        tabLayout.addTab(tabAuto);
        tabLayout.addTab(tabManual);
        tabLayout.addTab(tabStepResponse);
        tabLayout.addTab(tabFilesList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isReceiving = false;
        controlerView.stopSending();
        if (ds != null) ds.close();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_send:
                if (((Button) v).getText().equals("RESUME")) {
                    sendMessage("RESUME");
                    ((Button) v).setText("PAUSE");
                } else {
                    sendMessage("PAUSE");
                    ((Button) v).setText("RESUME");
                }
                break;
            case R.id.buttonAddPoint:
                sendMessage("ADD_WAYPOINT");
                break;
            case R.id.buttonSavePoints:
showFileNameInput();
                //waypointsListView.addFileName("dummy");
                break;
            case R.id.buttonAddFotoPoint:
                sendMessage("ADD_FOTOPOINT");
                break;
        }
    }

    // method to inflate the options menu when
    // the user opens the menu for the first time
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shutdown:
                sendMessage("SHUTDOWN");

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }

    private void initSocket() {
        try {
            ds = new DatagramSocket(55556);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("main", "ds.isCon: " + ds.isConnected() + " ds.isBound: " + ds.isBound());


        startReceiving();
    }

    private void startReceiving() {
        {
            isReceiving = true;
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    String s = (String) msg.obj;
                    if (s != null) {

                        mTextViewReplyFromServer.append(s);
                        mTextViewReplyFromServer.append("\n");
                    }

                    super.handleMessage(msg);
                }
            };
            class UiRunnable implements Runnable {
                String stringData1;

                UiRunnable(String data) {
                    this.stringData1 = data;
                }

                @Override
                public void run() {

                    // String s = mTextViewReplyFromServer.getText().toString();
                    if (stringData1 == null) return;
                    if (stringData1.trim().length() != 0) {
                        String[] parts = stringData1.split(",");
                        if (parts.length < 2) return;
                        if (parts[0].equals("GYRO_DIR")) {
                            // mTextViewGyro.setText(parts[1]);
                            return;
                        } else if (parts[0].equals("DELTA_YAW")) {
                            //   deltaYawTextView.setText(parts[1]);
                            return;
                        } else if (parts[0].equals("STATE")) {
                            deltaYawTextView.setText(parts[1]);
                            return;
                        } else if (parts[0].equals("FILE")) {
                            waypointsListView.addFileName(parts[1]);
                            return;
                        }
                        mTextViewReplyFromServer.append(stringData1);// default prefix is "TEXT", show it in ui log
                        mTextViewReplyFromServer.append("\n");
                    }

                }
            }
            Thread thread = new Thread(new Runnable() {

                String stringData;

                @Override
                public void run() {

                    while (isReceiving) {
                        try {
                            //  ds = new DatagramSocket(8889);

                            // IP Address below is the IP address of that Device where server socket is opened.
                            InetAddress serverAddr = InetAddress.getByName("arturs-Vostro-3590");//"xxx.xxx.xxx.xxx");
                            DatagramPacket dp;

                            byte[] lMsg = new byte[1000];
                            dp = new DatagramPacket(lMsg, lMsg.length);
                            if (ds.isClosed()) continue;
                            ds.receive(dp);
                            stringData = new String(lMsg, 0, dp.getLength());
                            //  Message msg = handler.obtainMessage();
                            //   msg.obj = stringData;
                            //  handler.sendMessage(msg);
                            Log.d("main", "received: " + stringData);
                        } catch (IOException e) {
                            // e.printStackTrace();
                        }


                        //    handler.post
                        runOnUiThread(new UiRunnable(stringData));
                    }
                }
            });

            thread.start();
        }


    }

    public void sendMessage(final String message) {

        //final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {

            //    String stringData;

            @Override
            public void run() {


                try {
                    //  ds = new DatagramSocket(8889);
                    Log.d("main", "sending udp packet: " + message);

                    // IP Address below is the IP address of that Device where server socket is opened.
                    InetAddress serverAddr = InetAddress.getByName("rasp16");//"xxx.xxx.xxx.xxx");

                    DatagramPacket dp;

                    dp = new DatagramPacket(message.getBytes(), message.length(), serverAddr, 55555);
                    ds.send(dp);
                    //  Log.d("main", "sent udp packet: " + message + " to adress: " + serverAddr.toString());

                } catch (Exception e) {
                    //  e.printStackTrace();
                }


            }
        });

        thread.start();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d("main", "onTabSelected: " + tab.getId() + " name: " + tab.getText());

        if (tab.equals(tabAuto)) {// why getId returns -1 ?
            frameLayout.addView(pidTuneView);
            sendMessage("MODE_AUTO");

        } else if (tab.equals(tabManual)) {
            frameLayout.addView(controlerView);
            controlerView.startSending();
            sendMessage("MODE_MANUAL");

        } else if (tab.equals(tabFilesList)) {
            sendMessage("SEND_NAMES"); // request available filenames for the list
            frameLayout.addView(waypointsListView);
        } else if (tab.equals(tabStepResponse)) {
            sendMessage("STEP_RESPONSE");

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.d("main", "onTabUUUUnselected: " + tab.getId() + " name: " + tab.getText());
        frameLayout.removeAllViews();
        if (tab.equals(tabManual)) {
            controlerView.stopSending();
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.d("main", "onTabRRRRReselected: " + tab.getId() + " name: " + tab.getText());

    }
void showFileNameInput(){
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final EditText edittext = new EditText(this);
    edittext.setText("waypoints");
    edittext.setSelection(0);
    alert.setMessage("Enter Name");
    alert.setTitle("New Route");

    alert.setView(edittext);

    alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {

            String text = edittext.getText().toString();
            sendMessage("SAVE_WAYPOINTS,"+text);

        }
    });

    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            // what ever you want to do with No option.
        }
    });

    alert.show();
}

}
