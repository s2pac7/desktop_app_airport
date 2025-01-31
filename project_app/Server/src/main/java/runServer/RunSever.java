package runServer;

public class RunSever {
    public static final int PORT_WORK = 1337;

    public static void main(String[] args) {
        Server server = new Server(PORT_WORK);
        new Thread(server).start();
    }
}