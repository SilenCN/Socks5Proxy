package socks5.encryption;

import socks5.encryption.methods.NoEncryption;

import java.io.IOException;
import java.io.OutputStream;

public class EncryptionOutputStream extends OutputStream {
    private OutputStream outputStream;
    private Encryption encryption;
    public EncryptionOutputStream(OutputStream outputStream,Encryption encryption) {
        this.outputStream=outputStream;
        if (encryption != null)
            this.encryption = encryption;
        else
            this.encryption=new NoEncryption();
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b,0,b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i=0;i<len;i++){
            b[i]=encryption.encrypt(b[i]);
        }
        outputStream.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write((int)encryption.encrypt((byte)b));
    }
}
