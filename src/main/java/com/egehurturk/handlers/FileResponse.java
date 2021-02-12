package com.egehurturk.handlers;

import com.egehurturk.exceptions.FileSizeOverflowException;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.StatusEnum;
import com.egehurturk.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Encapsulates everything concerning file handling and/or HTML file
 * returning. This class is a result of my approaches to beautify and
 * improve the design & implementation of http response protocol
 */
public class FileResponse implements ResponseType {

    protected Logger logger = LogManager.getLogger(FileResponse.class);
    /**
     * Path representing output file path
     * Everything will be based on this
     */
    private final String path;
    /**
     * {@link PrintWriter} necessary for {@link #toHttpResponse()}
     * method
     */
    private final PrintWriter writer;
    /**
     * Status necessary for {@link #toHttpResponse()}
     * method
     */
    private StatusEnum status;
    public final String BAD_REQ = "400.html";
    public final String INDEX = "index.html";
    public final String _404_NOT_FOUND = "404.html";
    public final String _NOT_IMPLEMENTED = "501.html";


    /**
     * Constructor that verifies path
     * @param path:                     request path, e.g. "/hello"
     * @param writer:                   Response writer
     */
    public FileResponse(String path, PrintWriter writer) throws FileNotFoundException {
        this.path = path;
        this.writer = writer;
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
     *              FileResponseHandler file = new FileResponseHandler("www/index.html", response.getStream(), "www");
     *              return file.toHttpResponse();
     *          }
     *      }
     *
     * </code>
     *
     * @return Http response object ready to being send in {@link Handler} interfaces
     */
    public HttpResponse toHttpResponse() {
        File outputFile = prepareOutput();
        byte[] buffer = memoryAllocateForFile(outputFile);

        ZonedDateTime now = ZonedDateTime.now();
        String dateHeader = now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                ZoneId.of("GMT")
                )
        );
        String contentLang = "en_US", mimeType = null;
        try {
            mimeType = Files.probeContentType(outputFile.toPath());
        } catch (IOException e) {
            this.logger.error("Cannot determine the MIME type of file");
            e.printStackTrace();
        }

        return new HttpResponseBuilder().factory("HTTP/1.1", this.status.STATUS_CODE, this.status.MESSAGE, buffer, this.writer,
                mimeType, dateHeader, "Banzai", contentLang, buffer.length
        );
    }

    public HttpResponse toHttpResponse(StatusEnum status, PrintWriter writer) {
        File outputFile = prepareOutput();
        byte[] buffer = memoryAllocateForFile(outputFile);

        ZonedDateTime now = ZonedDateTime.now();
        String dateHeader = now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                ZoneId.of("GMT")
                )
        );
        String contentLang = "en_US", mimeType = null;
        try {
            mimeType = Files.probeContentType(outputFile.toPath());
        } catch (IOException e) {
            this.logger.error("Cannot determine the MIME type of file");
            e.printStackTrace();
        }

        return new HttpResponseBuilder().factory("HTTP/1.1", status.STATUS_CODE, status.MESSAGE, buffer, writer,
                mimeType, dateHeader, "Banzai", contentLang, buffer.length
        );
    }

    /**
     * Prepare the HTML file given GET request
     * @return                      - {@link File} output file
     */
    private File prepareOutput() {
        File outputFile;
        String resolvedFilePathUrl;
        // resolve the file and get the file that is stored in www/${resolvedFilePathUrl}
        outputFile = new File(this.path);
        // if the file does not exists throw 404
        if (!outputFile.exists()) {
            this.status = StatusEnum._404_NOT_FOUND;
            outputFile = new File("www", _404_NOT_FOUND);
        } else {
            if (outputFile.isDirectory()) {
                // /file -> index.html
                outputFile = new File(outputFile, INDEX);
            }
            if (outputFile.exists()) {
                this.status = StatusEnum._200_OK;
            } else {
                this.status = StatusEnum._404_NOT_FOUND;
                outputFile = new File("www", _404_NOT_FOUND);
            }
        }
        return outputFile;
    }

    private byte[] memoryAllocateForFile(File file) {
        byte[] bodyByte = null;
        try {
            bodyByte = Utility.readFile_IO(file);
        } catch (IOException | FileSizeOverflowException e) {
            this.logger.error("File size is too large");
            return null;
        }
        return bodyByte;
    }

}
