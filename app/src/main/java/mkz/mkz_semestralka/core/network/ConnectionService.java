package mkz.mkz_semestralka.core.network;


/**
 * Service used for connecting to server.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class ConnectionService {//extends Service<Socket>{

    /**
     * Maximum waiting time for connection. In ms.
     */
//    public static final int MAX_TIMEOUT = 30000;
//
//    private final String address;
//    private final int port;
//
//    /**
//     * A task which will return a socket if the connection is successful.
//     */
//    private class ConnectionTask extends Task<Socket> {
//
//        private final String address;
//        private final int port;
//
//        public ConnectionTask(String address, int port) {
//            this.address = address;
//            this.port = port;
//        }
//
//        @Override
//        protected Socket call() throws Exception {
//            Socket s = new Socket();
//            s.connect(new InetSocketAddress(address, port), MAX_TIMEOUT);
//            return s;
//        }
//    }
//
//    public ConnectionService(String address, int port) {
//        this.address = address;
//        this.port = port;
//    }
//
//    @Override
//    protected Task<Socket> createTask() {
//        return new ConnectionTask(address, port);
//    }
}
