package mkz.mkz_semestralka.core.message.received;

import mkz.mkz_semestralka.core.error.Error;
import mkz.mkz_semestralka.core.error.ErrorCode;
import mkz.mkz_semestralka.core.message.MessageType;

/**
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */
public class ErrorReceivedMessage extends AbstractReceivedMessage<Error> {

    private Error content;

    public ErrorReceivedMessage(Error content) {
        this.content = content;
    }

    public ErrorReceivedMessage(ErrorCode errorCode) {
        this.content = new Error(errorCode,"");
    }

    @Override
    public Error getContent() {
        return content;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ERR;
    }
}
