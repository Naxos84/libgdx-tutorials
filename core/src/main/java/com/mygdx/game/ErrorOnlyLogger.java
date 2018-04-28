package com.mygdx.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.ApplicationLogger;

public class ErrorOnlyLogger implements ApplicationLogger {

    Logger log = LogManager.getLogger(ErrorOnlyLogger.class.getName());

    @Override
    public void log(final String tag, final String message) {
        log.error(tag + " - " + message);
    }

    @Override
    public void log(final String tag, final String message, final Throwable exception) {
        // TODO Auto-generated method stub

    }

    @Override
    public void error(final String tag, final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void error(final String tag, final String message, final Throwable exception) {
        // TODO Auto-generated method stub

    }

    @Override
    public void debug(final String tag, final String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void debug(final String tag, final String message, final Throwable exception) {
        // TODO Auto-generated method stub

    }

}
