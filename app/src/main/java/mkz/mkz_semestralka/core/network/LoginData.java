package mkz.mkz_semestralka.core.network;

import java.io.Serializable;

/**
 * A simple class for login data - nick, address and port.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */

public class LoginData implements Serializable{
    private final String nick;
    private final String address;
    private final int port;

    public LoginData(String nick, String ip, int port) {
        this.nick = nick;
        this.address = ip;
        this.port = port;
    }

    public String getNick() {
        return nick;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "nick='" + nick + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
