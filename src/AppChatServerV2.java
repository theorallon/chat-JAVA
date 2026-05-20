import java.io.*;
import java.net.*;
import java.util.*;

public class AppChatServerV2 {

private static final int PORT = 12345; // Alterado para a mesma porta do cliente
private static ServerSocket serverSocket;
private static Map<String, PrintWriter> clients = new HashMap<>();

public static void main(String[] args) {
try {
serverSocket = new ServerSocket(PORT);
System.out.println("Servidor aguardando conexões...");
while (true) {
Socket clientSocket = serverSocket.accept();
System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
// Criação de nova thread para cada cliente conectado
new Thread(new ClientHandler(clientSocket)).start();
}
} catch (IOException e) {
e.printStackTrace();
}
}
// Classe que gerencia a comunicação com o cliente
private static class ClientHandler implements Runnable {
private Socket socket;
private BufferedReader in;
private PrintWriter out;
private String clientName;
public ClientHandler(Socket socket) {
this.socket = socket;
try {
in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
out = new PrintWriter(socket.getOutputStream(), true);
} catch (IOException e) {
e.printStackTrace();
}
}
public void run() {
try {
// Receber o nome do cliente
out.println("Digite seu nome:");
clientName = in.readLine();
synchronized (clients) {
clients.put(clientName, out);
}
System.out.println(clientName + " entrou no chat.");
// Enviar a lista de usuários conectados ao cliente
sendUserList(); // Envia a lista de usuários após a conexão
// Enviar mensagens do cliente para o servidor
String message;
while ((message = in.readLine()) != null) {
if (message.startsWith("/send")) {
// Comando para enviar mensagem para outro cliente
String[] parts = message.split(" ", 3);
if (parts.length == 3) {
String target = parts[1];
String msg = parts[2];
sendMessageToClient(target, msg);
}
} else if (message.startsWith("/list")) {
// Comando para enviar a lista de usuários conectados
sendUserList();
} else {
System.out.println(clientName + ": " + message);
}
}
} catch (IOException e) {
e.printStackTrace();
} finally {
// Remover cliente da lista ao desconectar
synchronized (clients) {
clients.remove(clientName);
}
try {
socket.close();
} catch (IOException e) {
e.printStackTrace();
}
}
}
private void sendMessageToClient(String target, String message) {
PrintWriter targetOut = clients.get(target);
if (targetOut != null) {
targetOut.println(clientName + " diz: " + message);
} else {
out.println("Usuário " + target + " não encontrado.");
}
}
private void sendUserList() {
synchronized (clients) {
StringBuilder userList = new StringBuilder("Usuários conectados:\n");
for (String client : clients.keySet()) {
userList.append(client).append("\n");
}
out.println(userList.toString());
}
}
}
}