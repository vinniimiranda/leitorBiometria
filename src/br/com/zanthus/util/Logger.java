package br.com.zanthus.util;

public class Logger {

    private ActivityLogger logger;
    public Logger(ActivityLogger logger){
        this.logger = logger;
    }

    public void log(String message){
        logger.logAction(message);
        
    }

}