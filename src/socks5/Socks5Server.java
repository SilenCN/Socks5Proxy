package socks5;

import socks5.encryption.Encryption;
import socks5.encryption.methods.XorEncryption;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.Semaphore;


public class Socks5Server {
    private static final int DEFAULT_PORT=1081;
    private static final int MAX_CONNECTION=100;
    public static final byte PROTOCOL_SOCKS5=0x05;
    public static final byte SUPPORT_METHODS[]={0x00,0x02};
    private static boolean FLAG=true;
    public static final String[][] SUPPORT_USER={{"test","test"}};

    private static Encryption encryption=new XorEncryption((byte)0x02);

    public static void startServer() throws InterruptedException, IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        FLAG=true;
        ServerSocket serverSocket=getSSLServerSocket();
        Semaphore semaphore=new Semaphore(MAX_CONNECTION);
        while (FLAG){
            semaphore.acquire();
            Socket client=serverSocket.accept();
            System.out.println("\n\n收到Socket连接！");
            new ClientSocketThread(client,semaphore,encryption).start();
        }
    }
    public static void stopServer(){
        FLAG=false;
    }
    public static void main(String[] args) throws InterruptedException, IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        Socks5Server.startServer();
    }
    private static ServerSocket getSSLServerSocket() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, CertificateException {
        SSLContext ctx = SSLContext.getInstance("SSL");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore tks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("src/socks5/kserver.keystore"), "qianxin.com".toCharArray());
        tks.load(new FileInputStream("src/socks5/tserver.keystore"), "qianxin.com".toCharArray());
        kmf.init(ks, "qianxin.com".toCharArray());
        tmf.init(tks);
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        SSLServerSocket serverSocket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(DEFAULT_PORT);
        serverSocket.setNeedClientAuth(true);
        return serverSocket;
    }
}
