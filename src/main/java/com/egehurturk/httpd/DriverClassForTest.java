package com.egehurturk.httpd;


import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.MethodEnum;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

public class DriverClassForTest {
    public static void main(String[] args) throws IOException {
        Options options = generateOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException err) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("banzai", options);
            return;
        }
        HttpServer httpServer = new HttpServer();
        HttpHandler handler = null;

        if (cmd.hasOption("config")) {
            httpServer.setConfigPropFile(cmd.getOptionValue("config"));
            System.out.println(httpServer.getConfigPropFile());
            httpServer.configureServer();
            handler = new HttpHandler(httpServer.getConfig());
        } else if (cmd.hasOption("port") && cmd.hasOption("host") && cmd.hasOption("name") && cmd.hasOption("webroot") && cmd.hasOption("backlog")) {
            System.out.println("[DEBUG][DEBUG] port from cmd is -->> " + cmd.getOptionValue("port"));
            System.out.println("[DEBUG][DEBUG] host from cmd is -->> " + cmd.getOptionValue("host"));
            System.out.println("[DEBUG][DEBUG] backlog from cmd is -->> " + cmd.getOptionValue("backlog"));
            System.out.println("[DEBUG][DEBUG] name from cmd is -->> " + cmd.getOptionValue("name"));
            System.out.println("[DEBUG][DEBUG] webroot from cmd is -->> " + cmd.getOptionValue("webroot"));
            httpServer = new HttpServer(Integer.parseInt(cmd.getOptionValue("port")), InetAddress.getByName(cmd.getOptionValue("host")), Integer.parseInt(cmd.getOptionValue("backlog")), cmd.getOptionValue("name"), cmd.getOptionValue("webroot"));
            handler = new HttpHandler(httpServer.getWebRoot(), httpServer.getName());
        } else if (cmd.hasOption("port") && cmd.getArgs().length == 1) {
            httpServer = new HttpServer(Integer.parseInt(cmd.getOptionValue("port")));
            handler = new HttpHandler(httpServer.getWebRoot(), httpServer.getName());
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("banzai", options);
            return;
        }
        httpServer.allowCustomUrlMapping(true);
        httpServer.addHandler(MethodEnum.GET, "/*", handler);
        httpServer.addHandler(MethodEnum.GET, "/hello", new MyHandler());
        httpServer.addHandler(MethodEnum.POST, "/*", handler);
        httpServer.addHandler(MethodEnum.GET, "/thismynewserver", new MyNewHandler());
        httpServer.addHandler(MethodEnum.GET, "/cemhurturk", new MyHandler());

        httpServer.start();
    }

    static class MyHandler implements Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(200)
                    .message("OK")
                    .body("<h1>Hello</h1>".getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+("<h1>Hello</h1>".length()))
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }

    static class MyNewHandler implements Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(404)
                    .message("Not Found")
                    .body("<h1>404 Error</h1>".getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+("<h1>404 Error</h1>".length()))
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }

    private static Options generateOptions() {
        Options options = new Options();
        Option port = Option.builder()
                .longOpt("port")
                .argName("portNumber")
                .hasArg()
                .desc("bind server to a port. Default is 9090" )
                .build();
        Option name = Option.builder()
                .longOpt("name")
                .argName("name" )
                .hasArg()
                .desc("Use name for web server" )
                .build();
        Option host = Option.builder()
                .longOpt("host")
                .argName("host" )
                .hasArg()
                .desc("Bind server to host" )
                .build();
        Option webroot = Option.builder()
                .longOpt("webroot")
                .argName("webroot" )
                .hasArg()
                .desc("use given directory to store HTML/source files" )
                .build();
        Option backlog = Option.builder()
                .longOpt("backlog")
                .argName("backlog" )
                .hasArg()
                .desc("backlog for server" )
                .build();
        Option config = Option.builder()
                .longOpt("config")
                .argName("config" )
                .hasArg()
                .desc("Configuration system properties file for server" )
                .build();
        options.addOption(port);
        options.addOption(host);
        options.addOption(name);
        options.addOption(webroot);
        options.addOption(backlog);
        options.addOption(config);
        return options;
    }

}



// FIXME: POST REQUEST IS VERY VERY SLOW?
/* FIXME: When `--congig <>` is passed as CLA, the server works. However, when all arguments are passed in as seperate
    fields, then the server closes because of `Port is already in use` */

