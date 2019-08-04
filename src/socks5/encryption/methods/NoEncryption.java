package socks5.encryption.methods;

import socks5.encryption.Encryption;

public class NoEncryption extends Encryption {
    @Override
    public byte encrypt(byte content) {
        return content;
    }

    @Override
    public byte decrypt(byte content) {
        return content;
    }
}
