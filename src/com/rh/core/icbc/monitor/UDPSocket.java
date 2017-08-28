package com.rh.core.icbc.monitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class UDPSocket {
private byte[] buffer = new byte[1024];    
    
    private DatagramSocket ds = null;    
    
    /**  
     * 构造函数，创建UDP客户端  
     * @throws Exception  
     */    
    public UDPSocket() throws Exception {    
        ds = new DatagramSocket();    
    }    
        
    /**  
     * 设置超时时间，该方法必须在bind方法之后使用.  
     * @param timeout 超时时间  
     * @throws Exception  
     */    
    public final void setSoTimeout(final int timeout) throws Exception {    
        ds.setSoTimeout(timeout);    
    }    
    
    /**  
     * 获得超时时间.  
     * @return 返回超时时间  
     * @throws Exception  
     */    
    public final int getSoTimeout() throws Exception {    
        return ds.getSoTimeout();    
    }    
    
    public final DatagramSocket getSocket() {    
        return ds;    
    }    
    
    /**  
     * 向指定的服务端发送数据信息.  
     * @param host 服务器主机地址  
     * @param port 服务端端口  
     * @param bytes 发送的数据信息  
     * @return 返回构造后俄数据报  
     * @throws IOException  
     */    
    public final DatagramPacket send(final String host, final int port,    
            final byte[] bytes) throws IOException {    
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress    
                .getByName(host), port);    
        ds.send(dp);    
        return dp;    
    }    
    
    /**  
     * 接收从指定的服务端发回的数据.  
     * @param lhost 服务端主机  
     * @param lport 服务端端口  
     * @return 返回从指定的服务端发回的数据.  
     * @throws Exception  
     */    
    public final String receive(final String lhost, final int lport)    
            throws Exception {    
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);    
        ds.receive(dp);    
        String info = new String(dp.getData(), 0, dp.getLength());    
        return info;    
    }    
    
    /**  
     * 关闭udp连接.  
     */    
    public final void close() {    
        try {    
            ds.close();    
        } catch (Exception ex) {    
            ex.printStackTrace();    
        }    
    }   

//以下为流 的形式发送
    public void sendMessage(final String host, final int port,String msg,MulticastSocket socket) throws IOException{  
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();  
        DataOutputStream dataStream = new DataOutputStream(ostream);  
        dataStream.writeUTF(msg);  
        dataStream.close();  
         
        byte[] data = ostream.toByteArray();            
        InetAddress address = InetAddress.getByName(host);  
        socket.joinGroup(address);  
        DatagramPacket dp = new DatagramPacket(data, data.length, address,port);  
        socket.send(dp);  
    }  
      
    public void getMessage(final String lhost, final int lport,MulticastSocket socket) throws IOException{  
        byte[] bs = new byte[1000];  
        DatagramPacket packet = new DatagramPacket(bs, bs.length);  
        socket.receive(packet);  
          
        DataInputStream istream = new DataInputStream(new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength()));  
          
        String msg = istream.readUTF();  
          
        System.out.println(msg);  
    }  
}
