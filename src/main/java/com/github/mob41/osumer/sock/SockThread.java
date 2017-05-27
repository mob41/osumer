package com.github.mob41.osumer.sock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DumpManager;

public class SockThread extends Thread {
    
    public static final int PORT = 46725;
    
    private boolean running = false;
    
    private ServerSocket sock = null;

    public SockThread() {
        
    }
    
    public static boolean testPortFree(int port){
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    @Override
    public void run(){
        if (!running){
            running = true;
            
            try {
                sock = new ServerSocket(PORT);
            } catch (IOException e1) {
                e1.printStackTrace();
                DebugDump dump = new DebugDump(null, null, "Opening osumer socket", null, "Could not open socket at 46725 for BG call. Another osumer application running?", false, e1);
                DumpManager.getInstance().addDump(dump);
                DebugDump.showDebugDialog(dump);
                System.exit(-1);
                return;
            }
            
            try {
                while(running){
                    Socket cs = sock.accept();
                    if (cs.getInetAddress().isLoopbackAddress()){
                        new ConnThread(cs).start();
                    } else {
                        cs.close();
                    }
                }
                sock.close();
            } catch (IOException e) {
                DebugDump dump = new DebugDump(null, null, "ServerSocket breaks at exception", null, "Unexpected ServerSocket break", false, e);
                DumpManager.getInstance().addDump(dump);
                DebugDump.showDebugDialog(dump);
                System.exit(-1);
                return;
            }
            
            running = false;
        }
    }

}
