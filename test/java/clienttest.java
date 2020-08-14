import WTCPclient.WTCPclient;
import WTCPclient.WST;
public class clienttest {
    public static void main(String[] args) {
        WTCPclient c1 = new WTCPclient() {

            @Override
            public void onReceive(WST st, String s) {
                System.out.println("Client1 Receive: " + s);
            }

            @Override
            public void onDisconnect(WST st) {
                System.out.println("Client1 Disconnect");
            }

            @Override
            public void onConnect(WST transceiver) {
                System.out.println("Client1 Connect");
            }

            @Override
            public void onConnectFailed() {
                System.out.println("Client1 Connect Failed");
            }
        };
        WTCPclient c2 = new WTCPclient() {

            @Override
            public void onReceive(WST st, String s) {
                System.out.println("Client2 Receive: " + s);
            }

            @Override
            public void onDisconnect(WST st) {
                System.out.println("Client2 Disconnect");
            }

            @Override
            public void onConnect(WST transceiver) {
                System.out.println("Client2 Connect");
            }

            @Override
            public void onConnectFailed() {
                System.out.println("Client2 Connect Failed");
            }
        };
        c1.connect("127.0.0.1", 2000);
        c2.connect("127.0.0.1", 2000);
        delay();
        while (true) {
            if (c1.isConnected()) {
                c1.getTransceiver().send("Hello1");
            } else {
                break;
            }
            delay();
            if (c2.isConnected()) {
                c2.getTransceiver().send("Hello2");
            } else {
                break;
            }
            delay();
        }
    }

    static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
