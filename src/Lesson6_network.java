import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Java. Level 2. Lesson 6.
 * @author Dmitry Pozdeyev
 * @version 11.02.2019
 */


/*
1. Написать консольный вариант клиент\серверного приложения, в котором пользователь может писать сообщения,
 как на клиентской стороне, так и на серверной. Т.е. если на клиентской стороне написать «Привет»,
 нажать Enter, то сообщение должно передаться на сервер и там отпечататься в консоли.
 Если сделать то же самое на серверной стороне, то сообщение передается клиенту и печатается у него в консоли.
 Есть одна особенность, которую нужно учитывать: клиент или сервер может написать несколько сообщений подряд.
 Такую ситуацию необходимо корректно обработать.

        Разобраться с кодом с занятия: он является фундаментом проекта-чата
        *ВАЖНО! * Сервер общается только с одним клиентом, т.е. не нужно запускать цикл,
         *  который будет ожидать второго/третьего/n-го клиентов.
*/


public class Lesson6_network {


        public static void main(String[] args) throws IOException {

            //создаем экземпляр сервера

            Server ser = new Server();
            ser.start();
            ser.connectClient();

            new Thread() {
                public void run() {
                    while (true) {
                        String txt = null;
                        try {
                            txt = ser.in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (txt != null) {
                            try {
                                ser.sendMessage(txt);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    try {
                        ser.writeConsole();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        }
    }

    class Server {
        BufferedReader in = null;
        PrintWriter out = null;
        ServerSocket serverSocket = null;
        Socket socket = null;
        String input;
        BufferedReader console = null;

        void start() {
            try {
                serverSocket = new ServerSocket(1234);
            } catch (IOException e) {
                System.out.println("Не могу открыть порт");
                System.exit(1);
            }
            System.out.print("Сервер запущен. Ожидаем подключения клиента...");
        }

        void connectClient() throws IOException {
            try {
                socket = serverSocket.accept();
                System.out.println("Клиент подключен");
            } catch (IOException e) {
                System.out.println("Подключение не получилось");
                System.exit(1);
            }

            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);

            System.out.println("Ожидаем сообщение...");
        }


        //Если пишем exit - выходим
        void sendMessage(String msg) throws IOException {
            if (msg.equalsIgnoreCase("exit")) close();
           out.println(msg);
           System.out.println(msg);
        }

        void close() throws IOException {
            out.close();
            in.close();
            socket.close();
            serverSocket.close();
        }


        void writeConsole() throws IOException {
            while (true) {
                console = new BufferedReader(new InputStreamReader(System.in));
                String txt = console.readLine();
                sendMessage(txt);
            }
        }
    }