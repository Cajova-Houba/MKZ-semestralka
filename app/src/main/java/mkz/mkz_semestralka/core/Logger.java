package mkz.mkz_semestralka.core;


import android.util.Log;

/**
 * Wrapper of standard android logger.
 * Holds the logger name.
 *
 * Created on 23.03.2017.
 * @author Zdenek Vales
 */

public class Logger {

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String loggerName) {
        return new Logger(loggerName);
    }

    private final String loggerName;

    private Logger(String loggerName) {
        this.loggerName = loggerName;
    }

    public void v(String msg) {
        Log.v(loggerName, msg);
    }

    public void d(String msg) {
        Log.d(loggerName, msg);
    }

    public void i(String msg) {
        Log.i(loggerName, msg);
    }

    public void w(String msg) {
        Log.w(loggerName, msg);
    }

    public void e(String msg) {
        Log.e(loggerName, msg);
    }
}
