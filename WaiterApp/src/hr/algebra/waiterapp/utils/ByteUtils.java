package hr.algebra.waiterapp.utils;

public class ByteUtils {

    private ByteUtils() {}

    public static int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | ((bytes[3] & 0xFF));
    }
}
