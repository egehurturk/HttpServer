/*
 * All rights reserved
 */

package com.egehurturk.example;


import com.egehurturk.BaseServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer extends BaseServer {

    protected static Logger logger = LogManager.getLogger(TCPServer.class);

    public TCPServer(int serverPort) throws UnknownHostException {
        super(serverPort);
    }

    public TCPServer() {}

    public TCPServer(String configFilePath) {
        configureServer(configFilePath);
    }


    @Override
    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(500);
        logger.info("Server started on port " + this.serverPort);
        ServerSocket sv = new ServerSocket(this.serverPort, this.backlog, this.serverHost);
        while (true) {
            Socket cli = sv.accept();
            ConnectionManager manager = new ConnectionManager(cli);
            logger.info("Connection established with " + cli + "");
            pool.execute(manager);
        }
    }

    @Override
    public void stop() throws IOException {
        this.server.close();

    }

    @Override
    public void restart() {

    }

    @Override
    public void reload() {

    }
}