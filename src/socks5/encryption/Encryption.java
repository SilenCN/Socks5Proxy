package socks5.encryption;

public abstract class Encryption {

    /**
     * 加密
     * @param content
     * @return
     */
    public abstract byte encrypt(byte content);

    /**
     * 解密
     * @param content
     * @return
     */
    public abstract byte decrypt(byte content);
}
