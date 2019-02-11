import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


//Консольный клиент
public class Client_console {

    public static void main(String[] args) throws IOException {

        //Создаем новый экземпляр клиента
        Client client = new Client(); //
        System.out.println("Клиент запущен");

        //Новый поток на чтение
        new Thread() {
            public void run() {
                try {
                    client.readMеssage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //Новый поток на отправку сообщения
        new Thread() {
            public void run() {
                try {
                    client.sendMеssage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

//Класс Client
class Client {
    Socket socket = null; //Объявляем
    BufferedReader in = null;
    PrintWriter out= null;
    BufferedReader console = null;
    String userMessage, serverMessage;


    public Client() throws IOException {
        socket = new Socket("localhost",1234);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),true);
        console = new BufferedReader(new InputStreamReader(System.in));
    }

    //Метод для отправки сообщения
    void sendMеssage() throws IOException {
        while (true) {
            if ((userMessage = console.readLine()) != null) {
                out.println(userMessage);

                //Выходим если пользователь ввел сообщения: закрыть, выход, close, exit
                if (userMessage.equalsIgnoreCase("закрыть") ||
                        userMessage.equalsIgnoreCase("выход") ||
                        userMessage.equalsIgnoreCase("close") ||
                        userMessage.equalsIgnoreCase("exit")) break;
            }
        }
    }

    //Метод для чтения сообщения
    void readMеssage() throws IOException {
        while (true) {
            if ((serverMessage = in.readLine()) != null){
                System.out.println(serverMessage);
            }
        }
    }

    //Закрытие
    void close() throws IOException {
        out.close();
        in.close();
        console.close();
        socket.close();
    }
}

