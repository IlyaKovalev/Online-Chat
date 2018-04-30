import java.io.*;
import java.net.*;
import java.util.ArrayList;

class SampleServer extends Thread {
    Socket s;
    int num;
    public static ArrayList<Socket>sockets;


    public static void main(String args[]) {
        sockets=new ArrayList<>();
        int i = 0;
        try {
            ServerSocket server = new ServerSocket(3128, 0,
                    InetAddress.getByName("192.168.1.47"));  // my ip address (but i have NAT)

            System.out.println("server is started");

            while(true) {
                new SampleServer(i, server.accept());
                i++;
            }
        }
        catch(Exception e)
        {System.out.println("init error: "+e+"   "+i);}
    }

    public SampleServer(int num, Socket s) {

        this.num = num;
        this.s = s;
        sockets.add(s);
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run() {
        try {
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();

            while(true){
                byte buf[] = new byte[64*1024];
            int r = is.read(buf);
                if(r!=-1){
                    for(int i=0;i<sockets.size();i++){
                OutputStream outputStream = sockets.get(i).getOutputStream();
                String data = new String(buf, 0, r);
                outputStream.write(data.getBytes());
                outputStream.flush();
                    }
                }

            }
        }
        catch(Exception e)
        {System.out.println("init error: "+e);}
    }
}