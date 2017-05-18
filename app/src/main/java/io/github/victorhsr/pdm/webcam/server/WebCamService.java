package io.github.victorhsr.pdm.webcam.server;

/**
 * Created by victor on 14/05/17.
 */

public interface WebCamService {

    public static final int RECORD_CAM_COMMAND = 1;
    public static final int LIVE_STREAMING_COMMAND = 2;
    public static final int REQUEST_RECORD_COMMAND = 3;
    public static final int REQUEST_LIVE_STREAMING_COMMAND = 4;
    public static final int SERVER_PORT = 3000;
    public static final String SERVER_IP = "192.168.2.106";
    public static final String RESOURCE_URI = "http://192.168.2.106:8080/ws/records";
    public static final int NOT_FOUND_CODE = 404;
    public static final int SUCCESS_CODE = 200;
    public static final int END_OF_STREAM_CODE = 201;
    public static final int CONTINUE_STREAM_CODE = 206;

}
