package socket;

import bean.Constant;
import bean.Message;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // 用于存放Socket，键是用户名
    private static volatile ConcurrentHashMap<String, Socket> clientSocket =
            new ConcurrentHashMap<>();

    static class ChatThread extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private volatile boolean isClose = false;
        private String name; // 用户昵称
        private String username; // 用户名
        private ExecutorService executor;

        ChatThread(Socket socket) {
            this.socket = socket;
            executor = Executors.newCachedThreadPool();
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()) );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



            private void receiveMessage() throws Exception {
            while (!isClose) {
                // 读取客户端发送过来的消息,对方以json格式发送数据过来
                String json = "";
                while ((json = reader.readLine()) != null && !"".equals(json)) {
                    System.out.println("接收到的json：" + json);
                    // 将json数据转化为实体类
                    Message m = parseMessageFormJson(json);
                    if (m != null) {
                        // 判断是否为下线消息，如果是，则将其socket进行移出
                        if (m.getMessage().equals("已下线")) {
                            System.out.println("移除用户名为：\"" + username + "\"的socket");
                            removeSocket();
                        }
                        // 将消息转发给其他用户
                        dispatchMessage(m);
                    }
                }
            }
        }

        private Message parseMessageFormJson(String json) throws Exception {
            if (!json.isEmpty()) { // json有数据
                // 解析json
                JSONObject jsonObject = JSON.parseObject(json);
                if (jsonObject.containsKey("type")) { // json格式正常
                    final Message message = JSON.parseObject(json, Message.class);
                    if (message != null) { // 转化为实体类
                        int type = message.getType(); // 消息类型
                        name = message.getName(); // 发送者的昵称
                        username = message.getUsername(); // 发送者的账号
                        clientSocket.put(username, socket);

                        if (type == Constant.VIEW_TYPE_RECEIVER) { // 1 客户端不可能发送这个类型
                            throw new Exception("客户端发来的消息不会是RECEIVER类型");
                        } else if (type == Constant.VIEW_TYPE_SENDER) { // 2 客户端发送来消息，转发给其他人
                            message.setType(Constant.VIEW_TYPE_RECEIVER);
                        } else if (type == Constant.VIEW_TYPE_SERVER) { // 3 此消息为系统提醒消息
                            message.setName(name);
                        }
                        return message;
                    } else new Exception("无法转化为实体类");
                } else throw new Exception("所传送的json数据有误");
            }
            return null;
        }

        // 转发信息
        public void dispatchMessage(final Message m) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // 将收到的消息转发给其他的socket
                    final String outJson = toJson(m);
                    System.out.println("转发给其他用户的json：" + outJson);
                    for (Map.Entry<String, Socket> entry : clientSocket.entrySet()){
                        if (!entry.getKey().equals(m.getUsername())) {
                            Socket s = entry.getValue();
                            System.out.println("转发给" + entry.getKey());
                            OutputStream output = null;
                            try {
                                output = s.getOutputStream();
                                output.write(outJson.getBytes());
                                output.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }

        // 监听socket关闭
        public void removeSocket() {
            clientSocket.remove(username);
            isClose = true;
        }

        private String toJson(Message m) {
            return JSON.toJSONString(m) + "\n";
        }

        @Override
        public void run() {
            try {
                receiveMessage();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(3000);
            if (serverSocket != null) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println(socket.getInetAddress().getHostAddress() + "进入聊天室");
                    // 开启线程
                    ChatThread ct = new ChatThread(socket);
                    ct.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
