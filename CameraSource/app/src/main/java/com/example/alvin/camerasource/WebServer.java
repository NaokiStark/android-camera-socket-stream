package com.example.alvin.camerasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class WebServer {
    ServerSocket serverSocket;
    private MainActivity _ctx;
    private int port = 8080;
    private String address = "localhost";
    public WebServer(int port, String addr, MainActivity ctx){
        _ctx = ctx;
        address = addr;
        (new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        runThread();
                    }
                }
        )).start();
    }

    public void runThread(){

        try {

            serverSocket = new ServerSocket(port);
            while (true) {
                final Socket socket = serverSocket.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                            String request = is.readLine();
                            Scanner scanner = new Scanner(_ctx.getAssets().open("server.html"));
                            String response = scanner.useDelimiter("\\Z").next();
                            os.print("HTTP/1.0 200" + "\r\n");
                            os.print("Content type: text/html" + "\r\n");
                            os.print("Content length: " + response.length() + "\r\n");
                            os.print("\r\n");
                            os.print(response+ "<script>window.svrip='"+address+":4085'</script>"+ "\r\n");
                            os.flush();
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
