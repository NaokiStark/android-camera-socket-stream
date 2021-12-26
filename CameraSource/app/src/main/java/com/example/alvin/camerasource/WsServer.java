package com.example.alvin.camerasource;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WsServer extends WebSocketServer
{

    private Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private MainActivity _ctx;
    public WsServer(int port, MainActivity ctx)
    {
        super(new InetSocketAddress(port));
        _ctx = ctx;
    }

    public WsServer(InetSocketAddress address, MainActivity ctx)
    {
        super(address);
        _ctx = ctx;
    }

    @Override
    public void onStart() {
        _ctx.serverStatus.setText("Listening on: " + getAddress().getHostName() + ":8080");
        setConnectionLostTimeout(0);
        //setConnectionLostTimeout(100);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        String uniqueID = UUID.randomUUID().toString();

        clients.put(uniqueID, conn);

        conn.send(Proto.envelop(uniqueID));
        conn.send(Proto.envelop("Connected!"));

        //broadcast((Proto.envelop("new connection: " + handshake.getResourceDescriptor()))); //This method sends a message to all clients connected
    }

    public int getClientsCount(){
        return clients.size();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        //broadcast(Proto.envelop(conn + " has left the room!"));
        clients.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
       // We don't need this at this moment
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

   /* @Override
    public void onStart()
    {
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }*/
}
