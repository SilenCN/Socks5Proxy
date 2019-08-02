package socks5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;


public class ClientSocketThread extends Thread {
    private Socket clientSocket;
    private Semaphore semaphore;
    private Socket targetSocket;
    private Byte protocal=0;
    public ClientSocketThread(Socket clientSocket, Semaphore semaphore) {
        this.clientSocket=clientSocket;
        this.semaphore=semaphore;
    }

    @Override
    public void run() {
        try {

            InputStream clientIn = clientSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();
            //处理握手
            if (!negotiation(clientIn, clientOut)) {
                //握手失败
                closeClient(clientIn, clientOut);
                return;
            }
            //处理客户端请求
            if (!request(clientIn, clientOut)) {
                //处理失败
                closeClient(clientIn, clientOut);
                return;
            }
            //传输数据转发
            Thread thread1 = new TransferThread(clientIn, targetSocket.getOutputStream());
            Thread thread2 = new TransferThread(targetSocket.getInputStream(), clientOut);
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
            clientSocket.close();
            targetSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            semaphore.release();
        }
    }

    public void closeClient(InputStream clientIn,OutputStream clientOut) throws IOException {
        clientIn.close();
        clientOut.flush();
        clientOut.close();
        clientSocket.close();
        semaphore.release();
    }

    /**
     * 握手阶段处理
     * @param inputStream
     * @param outputStream
     * @return
     */
    public boolean negotiation(InputStream inputStream,OutputStream outputStream){
        boolean result=false;
        byte[] tmp=new byte[2];
        try {
            if (inputStream.read(tmp)!=-1){
                switch (tmp[0]){
                    //协议版本
                    case Socks5Server.PROTOCOL_SOCKS5:
                        System.out.println("协议版本5");
                        protocal=Socks5Server.PROTOCOL_SOCKS5;
                        byte nmethods=tmp[1];
                        System.out.println("协议认证数量"+nmethods);
                        byte[] methods=new byte[nmethods];
                        inputStream.read(methods);
                        boolean has_methods=false;
                        for (int i=0;i<nmethods;i++){
                            if (methods[i]==Socks5Server.SUPPORT_METHODS){
                                has_methods=true;
                                break;
                            }
                        }
                        outputStream.write(protocal);
                        if (has_methods){
                            outputStream.write(Socks5Server.SUPPORT_METHODS);
                            result=true;
                        }else {
                            outputStream.write(0xFF);
                        }
                        break;
                        default:
                }
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean request(InputStream inputStream,OutputStream outputStream) throws IOException {
        boolean result=false;
        byte[] tmp=new byte[4];
        System.out.println(tmp[0]);
        if (inputStream.read(tmp)!=-1){
            if(tmp[0]==0x05){
                switch (tmp[1]){
                    case 0x01:
                        String host=getHost(tmp[3],inputStream);
                        tmp=new byte[2];
                        inputStream.read(tmp);
                        int port = ByteBuffer.wrap(tmp).asShortBuffer().get() & 0xFFFF;
                        targetSocket=new Socket(host,port);
                        outputStream.write(new byte[]{0x05,0x00,0x00,0x01});
                        outputStream.write(clientSocket.getLocalAddress().getAddress());
                        outputStream.write(tmp);
                        outputStream.flush();
                        result=true;
                        break;
                    case 0x02:
                        break;
                    case 0x03:
                        break;
                }
            }
        }
        return result;
    }
    private String getHost(int atyp,InputStream inputStream) throws IOException {
        String host=null;
        byte[] tmp=null;
        switch (atyp){
            case 0x01:
                //IPV4
                tmp=new byte[4];
                inputStream.read(tmp);
                host= InetAddress.getByAddress(tmp).getHostAddress();
                break;
            case 0x03:
                //域名
                int length=inputStream.read();
                tmp=new byte[length];
                inputStream.read(tmp);
                host=new String(tmp);
                break;
            case 0x04:
                tmp=new byte[16];
                inputStream.read(tmp);
                host=InetAddress.getByAddress(tmp).getHostAddress();
        }
        return host;
    }
}
