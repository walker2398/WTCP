package WTCPserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Project WTCP
 *
 * @author WALKER2398
 * @since 2020-7-1
 */
public abstract class WTCP implements Runnable {


    private int port;
    private boolean runFlag;
    private List<WST> clients = new ArrayList<WST>();

    /**
     * INSTANTIATION
     *
     * @param port port to listen for
     */
    public WTCP(int port) {
        this.port = port;
    }

    /**
     * start the server
     * <p>
     * @code onServerStop()}would be called back if the server didn't start properly
     */
    public void start() {
        runFlag = true;
        new Thread(this).start();
    }

    /**
     * stop the server
     * <p>
     * {@code onServerStop()}would be called after the server is stopped
     */
    public void stop() {
        runFlag = false;
    }

    /**
     * 监听端口，接受客户端连接(新线程中运行)
     */
    @Override
    public void run() {
        try {
            final ServerSocket server = new ServerSocket(port);
            while (runFlag) {
                try {
                    final Socket socket = server.accept();
                    startClient(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    this.onConnectFailed();
                }
            }
            try {
                for (WST client : clients) {
                    client.stop();
                }
                clients.clear();
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.onServerStop();
    }

    /**
     * start sending and receiving
     *
     * @param socket
     */
    private void startClient(final Socket socket) {
        WST client = new WST(socket) {

            @Override
            public void onReceive(InetAddress addr, String s) {
                WTCP.this.onReceive(this, s);
            }

            @Override
            public void onDisconnect(InetAddress addr) {
                clients.remove(this);
                WTCP.this.onDisconnect(this);
            }
        };
        client.start();
        clients.add(client);
        this.onConnect(client);
    }

    /**
     * client:connection is started
     * <p>
     * mind:this callback is executed in a new thread
     *
     * @param client object SocketTransceiver
     */
    public void onConnect(WST client){

    }

    /**
     * client：connection failed
     * <p>
     * mind:this callback is executed in a new thread
     */
    public void onConnectFailed(){

    }

    /**
     * client:received a string
     * <p>
     * mind:this callback is executed in a new thread
     *
     * @param client SocketTransceiver object
     * @param s received string
     */
    public abstract void onReceive(WST client, String s);

    /**
     * client: connection is stopped
     * <p>
     * mind:this callback is executed in a new thread
     *
     * @param client SocketTransceiver object
     */
    public void onDisconnect(WST client){

    }

    /**
     * the server is stopped
     * <p>
     * mind:this callback is executed in a new thread
     */
    public void onServerStop(){
        
    }
}
