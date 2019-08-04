package socks5.encryption.methods;

import socks5.encryption.Encryption;

public class XorEncryption  extends Encryption {
    private byte key;
    public XorEncryption(byte key) {
        this.key=key;
    }

    @Override
    public byte encrypt(byte content) {
        return (byte)(content^key);
    }

    @Override
    public byte decrypt(byte content) {
        return (byte)(content^key);
    }
}
