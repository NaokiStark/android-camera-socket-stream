package com.example.alvin.camerasource;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    public MyCameraView mPreview;
    public TextView serverStatus;
    public static String SERVERIP = "localhost";
    public static final int SERVERPORT = 9191;
    private Handler handler = new Handler();
    public static MainActivity ctx = null;

    public MainActivity(){
        ctx = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverStatus = (TextView) findViewById(R.id.textView);
        SERVERIP = getLocalIpAddress();
        mCamera = getCameraInstance();
        mPreview = new MyCameraView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        Thread cThread = new Thread(new MyServerThread(this,SERVERIP,SERVERPORT,handler));
        cThread.start();
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
