package com.example.alvin.camerasource;

import android.content.Context;
import android.graphics.Camera;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class CameraFramer {
    private MainActivity _ctx;
    private Camera _camera;
    private SurfaceHolder _holder;
    private Thread serverThread;

    public CameraFramer(MainActivity ctx){
        //super(_ctx);
        _ctx = ctx;
        startThread();
    }

    public ByteArrayOutputStream getFrame(){
        return _ctx.mPreview.mFrameBuffer;
    }

    public void startThread(){

        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                float sleepTime = 1000f/16f; //ToDo: Unhardcode this, 1000/FPS

                while(true){
                    try {
                        // Let sleep first
                        Thread.sleep((long) Math.ceil(sleepTime));

                        if(_ctx.wsServer.getClientsCount() < 1){
                            continue; //NO CLIENTS
                        }

                        ByteArrayOutputStream frame = getFrame();

                        // Todo: IDK if broadcasting is ok, but anyways
                        _ctx.wsServer.broadcast(frame.toByteArray());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        serverThread.start();
    }
}
