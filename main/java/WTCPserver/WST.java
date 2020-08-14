package WTCPserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 @author walker2398
 @since 2020-7-1
 */
//TODO:2020-7-1: 1.Exceptions 2.Add Thread locks 3.top packaging
public abstract class WST implements Runnable{
    /*ST: SOCKET TRANSCEIVER
     START NEW THREADS TO LISTEN AND ACCEPT AND SEND
     */

    protected Socket socket;
    protected InetAddress addr;
    protected DataInputStream in;
    protected DataOutputStream out;
    private boolean runFlag;

    /**
       @param socket:socket already started
    */
    public WST(Socket socket) {
        this.socket = socket;
        this.addr = socket.getInetAddress();
    }

    /**
     * @return InetAddress object
     */
    public InetAddress getInetAddress() {
        return addr;
    }

    /**
     * start socket sending and receiving
     * <p>
     * {@code onDisconnect()}would be called back and the connection would be stopped if the socket didn't start properly
     */
    public void start() {
        runFlag = true;
        new Thread(this).start();
    }

    /**
     * disconnect(actively)
     * {@code onDisconnect()}would be called back after the disconnection
     */
    public void stop() {
        runFlag = false;
        try {
            socket.shutdownInput();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * send a string
     *
     * @param s is string
     * @return true would be returned if the sending is complete
     */
    public boolean send(String s) {
        if (out != null) {
            try {
                out.writeUTF(s);
                out.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * listen for the data received by the socket(runs in a new thread)
     */
    @Override
    public void run() {
        try {
            in = new DataInputStream(this.socket.getInputStream());
            out = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            runFlag = false;
        }
        while (runFlag) {
            try {
                final String s = in.readUTF();
                this.onReceive(addr, s);
            } catch (IOException e) {
                // the connection was closed(passively)
                runFlag = false;
            }
        }
        // disconnect
        try {
            in.close();
            out.close();
            socket.close();
            in = null;
            out = null;
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.onDisconnect(addr);
    }

    /**
     * on received
     * <p>
     * mind: this callback is executed in a new thread
     * @param addr is the IP address of the client
     * @param s is the received string
     */
    public void onReceive(InetAddress addr, String s){

    }

    /**
     * disconnected
     * mind: this callback is executed in a new thread
     * @param addr is the IP address of the client
     */
    public void onDisconnect(InetAddress addr){

    }
}
