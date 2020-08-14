package WTCPclient;

import java.net.InetAddress;
import java.net.Socket;
import WTCPclient.WST;
public abstract class WTCPclient implements Runnable {
    private int port;
    private String hostIP;
    private boolean connect = false;
    private WST transceiver;

    /**
     * start connection
     * <p>
     * the build of new connection will be executed in a new thread
     * <p>
     * {@code onConnect()}would be called back if the connection is started successfully
     * <p>
     * {@code onConnectFailed()}would be called back if the connection is started unsuccessfully
     *
     * @param hostIP the server's ip
     * @param port   the sever's port
     */
    public void connect(String hostIP, int port) {
        this.hostIP = hostIP;
        this.port = port;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(hostIP, port);
            transceiver = new WST(socket) {

                @Override
                public void onReceive(InetAddress addr, String s) {
                    WTCPclient.this.onReceive(this, s);
                }

                @Override
                public void onDisconnect(InetAddress addr) {
                    connect = false;
                    WTCPclient.this.onDisconnect(this);
                }
            };
            transceiver.start();
            connect = true;
            this.onConnect(transceiver);
        } catch (Exception e) {
            e.printStackTrace();
            this.onConnectFailed();
        }
    }

    /**
     * the connection is stopped
     * <p>
     * @code onDisconnect()}would be called back if the connection ended
     */
    public void disconnect() {
        if (transceiver != null) {
            transceiver.stop();
            transceiver = null;
        }
    }

    /**
     * check if the connection is going
     *
     * @return TRUE would be returned if the connection is going
     */
    public boolean isConnected() {
        return connect;
    }

    /**
     * get the current WST
     *
     * @return null would be return if there's no connection
     */
    public WST getTransceiver() {
        return isConnected() ? transceiver : null;
    }

    /**
     * the connection is started
     *
     * @param transceiver WST object
     */
    public void onConnect(WST transceiver){

    }

    /**
     * the connection failed to start
     */
    public void onConnectFailed(){

    }

    /**
     * this callback would be executed if WST received anything
     * <p>
     * mind: this callback is executed in a new thread
     *
     * @param transceiver WST object
     * @param s           string
     */
    public abstract void onReceive(WST transceiver, String s);

    /**
     * the connection is lost
     * <p>
     * mind: this callback is executed in a new thread
     *
     * @param transceiver WST object
     */
    public void onDisconnect(WST transceiver){

    }
}
