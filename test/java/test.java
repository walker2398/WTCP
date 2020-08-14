import WTCPserver.WST;
import WTCPserver.WTCP;

public class test {
    public static void main(String[] args) {
        int port = 2000;
        WTCP server = new WTCP(port) {

            @Override
            public void onConnect(WST client) {
                printInfo(client, "Connect");
            }

            @Override
            public void onConnectFailed() {
                System.out.println("Client Connect Failed");
            }

            @Override
            public void onReceive(WST client, String s) {
                printInfo(client, "Send Data: " + s);
                client.send(s);
            }

            @Override
            public void onDisconnect(WST client) {
                printInfo(client, "Disconnect");
            }

            @Override
            public void onServerStop() {
                System.out.println("--------Server Stopped--------");
            }
        };
        System.out.println("--------Server Started--------");
        server.start();
    }

    static void printInfo(WST st, String msg) {
        System.out.println("Client " + st.getInetAddress().getHostAddress());
        System.out.println("  " + msg);
    }
}
