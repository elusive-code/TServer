package com.elusive_code.tserver.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vladislav Dolgikh
 */
public class StreamRedirector implements Runnable {

    private static Logger log = Logger.getLogger(StreamRedirector.class.getName());

    private BufferedInputStream  from;
    private BufferedOutputStream to;

    public StreamRedirector(InputStream from, OutputStream to) {
        this.from = new BufferedInputStream(from);
        this.to = new BufferedOutputStream(to);
    }

    private boolean isOpened() {
        try {
            from.available();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                IOUtils.copyLarge(from,to);
            }
        } catch (EOFException ex){
        } catch (IOException ex){
            log.log(Level.SEVERE,"Error during stream redirect",ex);
        }finally {
            IOUtils.closeQuietly(to);
        }

    }
}
