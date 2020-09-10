import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    private static Socket clientSocket; //сокет для общения
    private static Scanner sc; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private static String name;
    private static Boolean work = true;
    private static Thread w, r;

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            try {
                sc = new Scanner(System.in);
                System.out.print("enter server: ");
                String buf = sc.next();
                System.out.print("enter port: ");
                clientSocket = new Socket(buf, sc.nextInt()); 
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.print("enter name: ");
                name = sc.next();

                sendMessage(new Message("init", name, "@", "hello world"));


                r = new Thread(){
                    private Message msg = new Message();
        
                    public void run(){
                        try {
                            msg.parseMessage(in.readLine());
                        } catch (IOException ignore) {}
                        printMessage(msg);
                    }
                };r.start();

                w = new Thread(){
                    private Message msg = new Message();
        
                    public void run(){
                        sc = new Scanner(System.in);
                        while(work){
                            if(!sc.hasNext()) continue;
                            String str = sc.nextLine();
                            if( str.length() == 0) continue;
                            Scanner sc = new Scanner(str);
                            String word = sc.next();
                            if (word.equals("@name")) {
                                name = str.substring(6);
                                try {
                                    sendMessage(new Message("init", name, "server", ")"));
                                } catch (IOException e) {}
                            } else if (word.equals("@quit")) {
                                work = false;
                                try {
                                    sendMessage(new Message("stop", name, "server", "bye"));
                                } catch (IOException e) {}
                            } else if(str.codePointAt(0) == '@') {
                                try {
                                    sendMessage(new Message("msg", name, word.substring(1), sc.nextLine()));
                                } catch (IOException ignore) {}
                            } else {
                                try {
                                    sendMessage(new Message("msg", name, "@", str));
                                } catch (IOException ignore) {}
                            }
                            sc.close();
                        }
                        printMessage(msg);
                    }
                };w.start();
                w.join();
                r.interrupt();

            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("client close...");
                w.interrupt();
                r.interrupt();
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public static void printMessage(Message msg) {
        if(msg.cmd.equals("info")){
            System.out.println("["+msg.from+"]: "+msg.text);
        }
        if(msg.to.equals("@")){
            System.out.println("("+msg.from+"): "+msg.text);
        }
        System.out.println("{"+msg.from+" for "+ msg.to +"}(DM): " + msg.text);
    }
    public static void sendMessage(Message msg) throws IOException {
        out.write(msg.toString()); // отправляем сообщение на сервер
        out.flush();
    }
}
