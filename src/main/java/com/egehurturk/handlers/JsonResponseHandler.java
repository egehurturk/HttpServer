package com.egehurturk.handlers;

import com.egehurturk.httpd.HttpRequest;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.Json;
import com.egehurturk.util.Pair;
import com.egehurturk.util.StatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Encapsulates everything concerning JSON response
 */
public class JsonResponseHandler {
    protected Logger logger = LogManager.getLogger(FileResponseHandler.class);

    /**
     * {@link PrintWriter} necessary for {@link #toHttpResponse()}
     * method
     */
    private final PrintWriter writer;
    private String body;
    private boolean valid = false;

    /**
     * Constructor that verifies path
     * @param writer:                   Response writer
     */
    public JsonResponseHandler(PrintWriter writer, String body) {
        this.writer = writer;
        this.body = body;
    }


    public void validate(HttpRequest req) {
        // FIXME: Note for future documentation: request headers are stored in lowercase and trimmed
        Pair<Boolean, String> pair = req.getHeader("Accept".toLowerCase());
        String accept = pair.getSecond();
        if (!pair.getFirst()) {
            this.valid = false;
            return;
        }
        this.valid = accept.contains("application/json") || accept.contains("*/*");
        System.out.println(this.valid);
    }


    public JsonResponseHandler(PrintWriter writer) {
        this.writer = writer;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * Converts File output to {@link HttpResponse} response
     * for future use in classes that implemenets {@link Handler} interface
     *
     * <p>Sample usage is explained below:
     *
     * <code>
     *
     *     class MyHandler implements Handler {
     *          @Override
     *          public HttpResponse handle(HttpRequest request, HttpResponse response) {
     *              JsonResponseHandler json = new JsonResponseHandler(response.getStream());
     *              json.setBody("{"name": "hello"}");
     *              return json.toHttpResponse();
     *          }
     *      }
     *
     * </code>
     *
     * @return Http response object ready to being send in {@link Handler} interfaces
     */
    public HttpResponse toHttpResponse() {
        ZonedDateTime now = ZonedDateTime.now();
        String dateHeader = now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                ZoneId.of("GMT")
                )
        );
        String contentLang = "en_US";
        String mimeType = "application/json";
        StatusEnum status = StatusEnum._200_OK;

        if (!this.valid) {
            status = StatusEnum._406_NOT_ACCEPTABLE;
            try {
                FileResponseHandler file = new FileResponseHandler("www/406.html", this.writer);
                return file.toHttpResponse(status, this.writer);
            } catch (FileNotFoundException ignored) {
            }
        }
        if (this.body == null) {
            logger.error("Body of JSON request is empty. Server automatically created JSON body.");
            this.body = Json.prettyPrintJSON("{\"Server Response\": {\"title\": \"Null Body\", \"body\": \"Body of JSON request is not set (This message is autogenerated by Banzai. Check logs from console)\"}}");
        }

//        HttpResponseBuilder builder = new HttpResponseBuilder();
//        HttpResponse response = builder
//                .scheme("HTTP/1.1")
//                .code(status.STATUS_CODE)
//                .message(status.MESSAGE)
//                .body(this.getBody().getBytes())
//                .setStream(this.writer)
//                .setHeader(HeaderEnum.CONTENT_TYPE.NAME, mimeType)
//                .setHeader(HeaderEnum.DATE.NAME, dateHeader)
//                .setHeader(HeaderEnum.SERVER.NAME, "Banzai")
//                .setHeader(HeaderEnum.CONTENT_LANGUAGE.NAME, contentLang)
//                .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+(this.getBody().getBytes().length))
//                .build();
        return new HttpResponseBuilder().factory("HTTP/1.1", status.STATUS_CODE, status.MESSAGE, this.getBody().getBytes(), this.writer,
                                                    mimeType, dateHeader, "Banzai", contentLang, this.getBody().getBytes().length
                                                );
    }


}


// TODO: CHANGE ALL `request.headers.get` to `request.getHeader()'