package hwbbs;

import java.net.ServerSocket;
import java.net.Socket;

public class HWBBS {

    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(54321);
            while (true) {
                Socket incoming = s.accept();
                new ClientThread(incoming).start();
            }
        } catch (Exception e) {
        }
    }
    
}
