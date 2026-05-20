
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class AppChatClientV2 extends JFrame {

    private JTextArea taChat;
    private JTextField tfMessage;
    private JTextField tfRecipient;
    private JButton btnSend;
    private JButton btnShowUsers; // Novo botão para mostrar usuários
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverAddress = "192.168.1.10"; // Endereço do servidor
    private int port = 12345; // Porta do servidor

    public AppChatClientV2() {
// Configurações da janela
        setTitle("Chat Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        taChat = new JTextArea();
        taChat.setEditable(false);
        add(new JScrollPane(taChat), BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
// Adicionando o label e o campo de texto para o destinatário
        JPanel recipientPanel = new JPanel();
        recipientPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel lblRecipient = new JLabel("Destinatário:");
        recipientPanel.add(lblRecipient);
        tfRecipient = new JTextField(15);
        recipientPanel.add(tfRecipient);
        panel.add(recipientPanel, BorderLayout.NORTH);
// Adicionando o label e o campo de texto para a mensagem
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel lblMessage = new JLabel("Mensagem:");
        messagePanel.add(lblMessage);
        tfMessage = new JTextField(20);
        messagePanel.add(tfMessage);
        panel.add(messagePanel, BorderLayout.CENTER);
// Botão de envio
        btnSend = new JButton("Enviar");
        panel.add(btnSend, BorderLayout.EAST);
// Novo botão para mostrar usuários conectados
        btnShowUsers = new JButton("Mostrar Usuários");
        panel.add(btnShowUsers, BorderLayout.WEST);
        add(panel, BorderLayout.SOUTH);
// Ação do botão de enviar
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
// Ação do botão de mostrar usuários
        btnShowUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUserList();
            }
        });
// Conectar ao servidor
        connectToServer();
// Iniciar a thread de recebimento de mensagens
        new Thread(new MessageReceiver()).start();
    }

    private void connectToServer() {
        try {
// Selecionar o Servidor
            String iphost = JOptionPane.showInputDialog("Digite o ip do servidor:");
            serverAddress = iphost;
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
// Enviar o nome do cliente
            String name = JOptionPane.showInputDialog("Digite seu nome:");
            setTitle("Chat Client - " + name);
            out.println(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String recipient = tfRecipient.getText();
        String message = tfMessage.getText();
        if (!message.isEmpty() && !recipient.isEmpty()) {
            out.println("/send " + recipient + " " + message); // Envia a mensagem para o servidor
            taChat.append("Você (para " + recipient + "): " + message + "\n");
            tfMessage.setText("");
        }
    }

    private void showUserList() {
        out.println("/list"); // Envia o comando para solicitar a lista de usuários
    }

    private class MessageReceiver implements Runnable {

        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("Usuários conectados:")) {
                        taChat.append(message + "\n");
                    } else {
                        taChat.append(message + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AppChatClientV2().setVisible(true);
            }
        });
    }
}
