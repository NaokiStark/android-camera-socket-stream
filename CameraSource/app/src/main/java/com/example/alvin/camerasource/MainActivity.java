package com.example.alvin.camerasource;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    public MyCameraView mPreview;
    public TextView serverStatus;
    public static String SERVERIP = "localhost";
    public static final int SERVERPORT = 4085;
    private Handler handler = new Handler();
    public static MainActivity ctx = null;
    public WsServer wsServer;
    public CameraFramer _cameraFramer;
    public WebServer webServer;
    public MainActivity(){
        ctx = this;
    }
    public String ip = "0.0.0.0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");*/

        WifiManager wm = (WifiManager) ((Context)this).getSystemService(Context.WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        try {
            wsServer = new WsServer(new InetSocketAddress(InetAddress.getByName(ip), SERVERPORT), this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        setContentView(R.layout.activity_main);
        serverStatus = (TextView) findViewById(R.id.textView);

        SERVERIP = getLocalIpAddress();
        mCamera = getCameraInstance();
        mPreview = new MyCameraView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // First start websocket server
        wsServer.start();
        //wsServer.run();

        // Then start framing
        _cameraFramer = new CameraFramer(this);

        //Start webserver

        webServer = new WebServer(8080, ip, this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            wsServer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            wsServer.start();
        }catch (Exception e){

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            wsServer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get local ip address of the phone
     * @return ipAddress
     */
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    /**
     * Get camera instance
     * @return
     */
    public static Camera getCameraInstance()
    {
        if(!checkCameraPermission()){
            return null;
        }
        Camera c = null;
        try{

            c=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        }catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }

    /**
     * Check for camera permission
     */
    public static boolean checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //ask for authorisation
            ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.CAMERA}, 50);
            checkCameraPermission();

        }
        else {
            return true;
        }

        return false;
    }
}
