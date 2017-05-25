package mkz.mkz_semestralka.core.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import mkz.mkz_semestralka.core.Logger;
import mkz.mkz_semestralka.core.error.ReceivingException;
import mkz.mkz_semestralka.core.message.MessageBuilder;
import mkz.mkz_semestralka.core.message.received.AbstractReceivedMessage;
import mkz.mkz_semestralka.core.message.received.AliveReceivedMessage;

/**
 * Class used for communication with senet server.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class TcpClient {

    public static final Logger logger = Logger.getLogger(TcpClient.class);

    /**
     * Max time to wait for new turn message.
     */
    public static final int MAX_WAITING_TIMEOUT = 1000;

    public static final int MAX_ALIVE_TIMEOUT = 10000;

    public static final int NO_TIMEOUT = -1;

    public static final int MAX_ATTEMPTS = 10;

    /**
     * Max timeout, after this is_alive will be sent
     */
    public static final int MAX_TIMEOUT = 120000;

    public static final int INF_ATTEMPTS = -1;

    private LoginData lastSuccessfulConnection;
    private Socket socket;
    private AbstractReceiver receiver;

    public TcpClient() {
        receiver = new AbstractReceiver();
    }

    /**
     * Tries to connect to the address and port.
     *
     * @return Socket with connection or null if unsuccessful.
     */
    public Socket connect(LoginData loginData) throws IOException {
        logger.d("Connecting to "+loginData.getAddress()+":"+loginData.getPort());

        Socket s = new Socket();
        s.connect(new InetSocketAddress(loginData.getAddress(), loginData.getPort()));
        if(!s.isConnected()) {
            throw new ConnectException("Socket not connected!");
        }
        lastSuccessfulConnection = loginData;

        return s;
    }

    /**
     * Sends login message to the socket. If the socket is not active, tries to connect.
     *
     */
    public void sendLoginMessage(LoginData loginData) throws IOException {
        logger.d("Sending login message: "+loginData);
        if(socket == null || !socket.isConnected()) {
            socket = connect(loginData);
        }

        if(socket != null) {
            DataOutputStream dos = null;
            dos = new DataOutputStream(socket.getOutputStream());
            dos.write(MessageBuilder.createNickMessage(loginData.getNick()).toBytes());
            logger.d("Message "+MessageBuilder.createNickMessage(loginData.getNick())+ " sent");
        } else {
            logger.w("Not connected!");
        }
    }

    /**
     * Sends OK message to the socket.
     */
    public void sendOkMessage() throws IOException {
        logger.d("Sending OK message");
        if(socket != null) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write(MessageBuilder.createOKMessage().toBytes());
        }
    }

    /**
     * Sends end turn message to the socket.
     */
    public void sendEndTurnMessage(int[] p1TurnWord, int[] p2TurnWord) throws IOException {
        logger.d("Sending end turn message");
        if(socket != null) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.write(MessageBuilder.createEndTurnMessage(p1TurnWord, p2TurnWord).toBytes());
        }
    }

    /**
     * Listens for new messages from server.
     * Keeps responding OK to ALIVE messages.
     *
     * @return Received message or null if socket is null.
     */
    public AbstractReceivedMessage receiveMessage() throws IOException, ReceivingException {
        if(socket != null) {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            AbstractReceivedMessage msg = receiver.receiveMessage(dis);
            while(msg instanceof AliveReceivedMessage) {
                sendOkMessage();
                msg = receiver.receiveMessage(dis);
            }

            return msg;
        }

        return null;
    }

    /**
     * Disconnects and nulls the socket.
     */
    public void disconnect() throws IOException {
        if(socket != null) {
            socket.close();
            socket = null;
        }
    }

    /**
     * Returns true if the socket is active.
     * @return
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public Socket getSocket() {
        return socket;
    }

}
