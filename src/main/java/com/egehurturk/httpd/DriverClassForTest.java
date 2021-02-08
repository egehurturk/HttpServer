package com.egehurturk.httpd;


import com.egehurturk.exceptions.ConfigurationException;
import com.egehurturk.handlers.FileResponseHandler;
import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.handlers.JsonResponseHandler;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.MethodEnum;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;




public class DriverClassForTest {

    /**
     * Note that configuration should not be as path, it only needs to be the name of the properties file
     */


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
            try {
                httpServer.configureServer();
            } catch (ConfigurationException e) {
                System.err.println("Configuration exception: Check configuraton property file path");
                return;
            }
            handler = new HttpHandler(httpServer.getConfig());
        } else if (cmd.hasOption("port") && cmd.hasOption("host") && cmd.hasOption("name") && cmd.hasOption("webroot") && cmd.hasOption("backlog")) {
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
        httpServer.addHandler(MethodEnum.GET, "/filehandling", new MyFileHandler());
        httpServer.addHandler(MethodEnum.GET, "/jsontest", new Json());

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

    static class MyFileHandler implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = null;
            try {
                FileResponseHandler fil = new FileResponseHandler("www/custom.html", response.getStream());
                res = fil.toHttpResponse("Banzai");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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

    static class Json implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = null;
            JsonResponseHandler json = new JsonResponseHandler(response.getStream());
            json.setBody("{\n" +
                    "    \"glossary\": {\n" +
                    "        \"title\": \"example glossary\",\n" +
                    "\t\t\"GlossDiv\": {\n" +
                    "            \"title\": \"S\",\n" +
                    "\t\t\t\"GlossList\": {\n" +
                    "                \"GlossEntry\": {\n" +
                    "                    \"ID\": \"SGML\",\n" +
                    "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
                    "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                    "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
                    "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
                    "\t\t\t\t\t\"GlossDef\": {\n" +
                    "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
                    "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
                    "                    },\n" +
                    "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            res = json.toHttpResponse();
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
// TODO: debug mode (server.properties)

