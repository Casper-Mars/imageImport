package org.r.tool.importimage.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author casper
 * @date 20-1-16 下午4:43
 **/
public class StreamTool {


    public static void StreamCopy(InputStream inputStream, OutputStream outputStream) throws IOException {

        byte[] buf = new byte[1024];
        int i = inputStream.read(buf);
        while (i != -1) {
            outputStream.write(buf, 0, i);
            i = inputStream.read(buf);
        }
    }


}
