package socks5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Authentication {
    public static boolean auth(byte code, InputStream inputStream, OutputStream outputStream) throws IOException {
        boolean result=false;
        switch (code){
            case 0x00:return true;
            case 0x02:result=usernameAndPasswordAuth(inputStream,outputStream);
            break;
        }

        System.out.println(result?"认证成功！":"认证失败！");
        return result;
    }

    private static boolean usernameAndPasswordAuth(InputStream inputStream,OutputStream outputStream) throws IOException {
        boolean result=false;
        byte[] tmp=new byte[2];
        inputStream.read(tmp);
        byte var=tmp[0];
        if (tmp[0]==0x01){
            int len=tmp[1];
            tmp=new byte[len];
            inputStream.read(tmp);
            String username=new String(tmp);
            len=inputStream.read();
            tmp=new byte[len];
            inputStream.read(tmp);
            String password=new String(tmp);

            result=checkUsernameAndPassword(username,password);

        }
        outputStream.write(var);
        outputStream.write(result?0x00:0x01);
        outputStream.flush();
        return result;
    }
    private static boolean checkUsernameAndPassword(String username,String password){
        if (username==null&&username.equals("")&&password==null&&password.equals(""))
            return false;
        for (int i=0;i<Socks5Server.SUPPORT_USER.length;i++){
            if (username.equals(Socks5Server.SUPPORT_USER[i][0])&&password.equals(Socks5Server.SUPPORT_USER[i][1])){
                return true;
            }
        }
        return false;
    }
}
