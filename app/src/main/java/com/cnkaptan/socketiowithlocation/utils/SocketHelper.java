package com.cnkaptan.socketiowithlocation.utils;

import android.location.Location;
import android.util.Log;

import com.cnkaptan.socketiowithlocation.model.LocationData;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketHelper {
    public static final String TAG = SocketHelper.class.getSimpleName();
    private Socket mSocket;
    private SocketStatusListener mSocketStatusListnener;
    public enum SocketStatus{
        CONNECTED,DISCONNECT,ERROR
    }
    private SocketHelper(){

    }

    public SocketHelper(Socket mSocket,SocketStatusListener socketStatusListener) {
        this.mSocket = mSocket;
        this.mSocketStatusListnener = socketStatusListener;
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
    }

    public void connect(){
        mSocket.connect();
    }

    public void disconnect(){
        mSocket.disconnect();
    }

    public Socket getSocket(){
        return mSocket;
    }
    public void sendLocation(Location location){
        mSocket.emit("send location", new LocationData(location.getLatitude(), location.getLongitude(), "Ready").toString());
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocketStatusListnener.updateStatus(SocketStatus.CONNECTED);
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocketStatusListnener.updateStatus(SocketStatus.DISCONNECT);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocketStatusListnener.updateStatus(SocketStatus.ERROR);
        }
    };


    public interface SocketStatusListener{
        void updateStatus(SocketStatus socketStatus);
    }

}
