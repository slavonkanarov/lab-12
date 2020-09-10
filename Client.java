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

    public static void main(String[] args) throws IOException {
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
                

                Thread r = new Thread(){
                    private Message msg = new Message();
        
                    public void run(){
                        try {
                            msg.parseMessage(in.readLine());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        printMessage(msg);
                    }
                };r.start();


            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("client close...");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public static void printMessage(Message msg) {
        
    }
    public static void sendMessage(Message msg) throws IOException {
        out.write(msg.toString()); // отправляем сообщение на сервер
        out.flush();
    }
}