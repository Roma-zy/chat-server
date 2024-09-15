package ru.otus.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;
    private static int userCount = 0;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        userCount++;
        username = "user" + userCount;
        new Thread(() -> {
            try {
                System.out.println("Клиент подключился ");
                while (true) {
                    boolean isBreak = false;
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        String[] messageArray = message.trim().split(" ");
                        String command = messageArray[0];
                        switch (command) {
                            case "/exit":
                                sendMessage("exit");
                                isBreak = true;
                                break;
                            case "/w":
                                if (messageArray.length <= 2) {
                                    sendMessage("Введите сообщеение!");
                                    break;
                                }
                                String messageWithoutCommand = String.join(" ", Arrays.copyOfRange(messageArray, 2, messageArray.length));

                                sendMessage("[private_to]" + messageArray[1] + " : " + messageWithoutCommand);
                                server.sendByUserNameMessage(
                                    messageArray[1],
                                    this,
                                    username + "[private_from] : " + messageWithoutCommand
                                );
                                break;
                            default:
                                sendMessage("нет такой команды!");
                                break;
                        }
                    } else {
                        server.broadcastMessage(username + " : " + message);
                    }

                    if (isBreak) break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
