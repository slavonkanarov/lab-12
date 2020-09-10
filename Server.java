import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


class Server{


    static class ServerClient extends Thread {
        private Socket socket;
        private BufferedReader in;
        private BufferedWriter out;
        private String name = "";
        private Boolean init;
    
        public ServerClient(Socket socket) throws IOException {
            this.socket = socket;
            init = false;
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            start();
        }
    
        public void run() {
            try {
                while (true) {
                    
                    Message msg = new Message(in.readLine());
                    if(msg.cmd.equals("stop")) {
                        if(!init)continue;
                        init = false;
                        msgSender(new Message("info", "server", "@", "@" + name + " disconnected"));
                        this.interrupt();
                    } else if (msg.cmd.equals("init")) {
                        if (init) {
                            msgSender(new Message("info", "server", "@", "@" + name + " disconnected"));
                        }
                        init = true;
                        name = msg.from;
                        msgSender(new Message("info", "server", "@", "@" + name + " connected"));
                    } else if (msg.cmd.equals("msg")) {
                        msgSender(msg);
                    }

                }

            } catch (IOException ignored) {
            }
        }
    }

    public static LinkedList<ServerClient> serverList = new LinkedList<ServerClient>(); // список всех нитей

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(4444);
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerClient(socket)); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }

    public static void msgSender(Message msg) {
        System.out.println(msg.toString());
        for (ServerClient vr : serverList) {
            if(!msg.to.equals("@")&&!msg.to.equals(vr.name))continue;
            if(!vr.init)continue;
            try {
                vr.out.write(msg.toString());
                vr.out.flush();
            } catch (IOException ignored) {}
        }
    }

}