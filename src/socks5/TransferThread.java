package socks5;

import java.io.InputStream;
import java.io.OutputStream;

public class TransferThread extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;
    public TransferThread(InputStream inputStream, OutputStream outputStream) {
        this.inputStream=inputStream;
        this.outputStream=outputStream;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer,0,len);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
        }catch (Exception e){
        }
    }
}
