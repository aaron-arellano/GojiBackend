package goji.common;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/** Creates Logger for class by utilizing global LogManager
 *
 *  @author Aaron
 *  @version 2020.10.27
 */
public class GojiLogManagement {

    private GojiLogManagement() {}

    /**
     * Place a description of your method here.
     * @param loggerName
     * @return the logger requested by an object
     */
    public static Logger createLogger(String loggerName) {
        LogManager logManager = LogManager.getLogManager();
        Logger logger = Logger.getLogger(loggerName);
        logManager.addLogger(logger);

        return logger;
    }

}
