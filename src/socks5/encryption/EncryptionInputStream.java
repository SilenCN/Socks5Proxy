package socks5.encryption;

import socks5.encryption.methods.NoEncryption;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EncryptionInputStream extends InputStream {
    private InputStream inputStream;
    private Encryption encryption;

    public EncryptionInputStream(InputStream inputStream, Encryption encryption) {
        super();
        this.inputStream = inputStream;
        if (encryption != null)
            this.encryption = encryption;
        else
            this.encryption=new NoEncryption();
    }

    @Override
    public int read() throws IOException {
        int i = inputStream.read();
        if (i == -1) {
            return i;
        } else {
            return encryption.decrypt((byte) i);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = inputStream.read(b, off, len);
        for (int i = 0; i < result; i++) {
            b[i] = encryption.decrypt(b[i]);
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }
}
