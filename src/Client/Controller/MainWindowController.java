package Client.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.*;
import java.io.*;

public class MainWindowController implements Initializable {

    @FXML
    private AnchorPane AuthForm;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private boolean isAutorized;

    @FXML
    private Button MySendButton;
    @FXML
    private Button AuthButton;
    @FXML
    private TextArea MyTextArea;
    @FXML
    private TextField MyTextField;

    @FXML
    private TextField loginTextField;
    @FXML
    private TextField passwordTextField;





    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if(str.startsWith("/authok")) {
                            setAuthorized(true);
                            MyTextArea.appendText("Вы вошли в чат" + "\n");
                            break;
                        }
                        MyTextArea.appendText(str + "\n");
                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            break;
                        }
                        MyTextArea.appendText(str + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthorized(false);
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setAuthorized(boolean status){
        isAutorized = status;
        AuthForm.setVisible(!status);
        MySendButton.setDisable(!status);

    }
    public void onAuthClick() {
        try {
            out.writeUTF("/auth " + loginTextField.getText() + " " + passwordTextField.getText());
            loginTextField.clear();
            passwordTextField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendMsg() {
        try {
            out.writeUTF(MyTextField.getText());
            MyTextField.setText("");
        } catch (IOException e) {
            System.out.println("Ошибка отправки сообщения");
        }
    }
}
