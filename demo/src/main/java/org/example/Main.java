package org.example;

import org.vertx.start.VertxApplication;
import org.vertx.web.annotations.rpc.Server;

//@Server(serverName = "hello", host = "127.0.0.1", port = 8866)
public class Main {

    public static void main(String[] args) {
        VertxApplication.start(80, args, "spring.xml");
    }
}