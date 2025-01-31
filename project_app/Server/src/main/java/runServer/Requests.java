package runServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Requests implements Runnable {
    protected Socket clientSocket = null;
    ObjectInputStream sois;
    ObjectOutputStream soos;

    public Requests(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            sois = new ObjectInputStream(clientSocket.getInputStream());
            soos = new ObjectOutputStream(clientSocket.getOutputStream());

            RequestFacade requestFacade = new RequestFacade();

            while (true) {
                System.out.println("Получение команды от клиента...");
                String command = sois.readObject().toString();
                System.out.println("Команда получена: " + command);

                requestFacade.processRequest(command, sois, soos);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при взаимодействии с клиентом: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Ошибка в сериализации объекта: " + e.getMessage());
        }
    }

}
