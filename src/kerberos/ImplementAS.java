/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerberos;

import database.Database;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;
import message.ASAckResponse;
import message.ASRequest;
import message.ASResponse;
import message.ASTicket;
import message.ClientRequest;
import utils.FileUtils;
import utils.HashUtils;
import utils.TimeUtils;

/**
 *
 * @author Vitor Tozzi
 */
public class ImplementAS extends UnicastRemoteObject implements InterfaceAS{

    Database database;
    
    public ImplementAS() throws RemoteException{
        super();
    }
    
    @Override
    public void doLogin(ASRequest request) {   
        
        database = new Database();
        
        /**
         * Recupera senha do cliente no banco de dados
         */
        String clientID = request.clientID;
        String clientPassword = database.map.get(clientID);
        
        /**
         * Descriptografa a mensagem enviada pelo cliente baseado em sua senha
         */
        FileUtils fileUtils = null;
        ClientRequest decrypted = null;
        try {
            String ASFilepath = "C:\\Kerberos\\AS\\clientRequest.des";
            fileUtils = new FileUtils(clientPassword);
            decrypted = (ClientRequest) fileUtils.readEncryptedObject(ASFilepath);
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(decrypted!=null){
            System.out.println("Requisição do cliente desencriptada:");
            System.out.println("***Passo 1***: AS recebe requisição do cliente");
            decrypted.print();
            
            //TODO Verificar validade do timestamp
            if(!TimeUtils.checkValidTimestamp(decrypted.timestamp)){
                JOptionPane.showMessageDialog(null, "O Ticket de requisição ao AS perdeu a validade.");
            }
            
            String sessionKey = "";
            try {
                sessionKey = HashUtils.generateSessionKey(clientID);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            ASAckResponse ackResponse = new ASAckResponse(sessionKey, decrypted.randomNumber, decrypted.timestamp, decrypted.serviceID);
            ASTicket ast = new ASTicket(clientID, decrypted.timestamp, sessionKey, decrypted.serviceID, decrypted.randomNumber);
            
            /**
             * Retorna ao client o ACK criptografado com a chave do cliente
             */
            String clientFilepath = "C:\\Kerberos\\Client\\ack.des";
            try {
                fileUtils.writeEncryptedObject(ackResponse, clientFilepath);
            } catch (IOException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            /**
             * Retorna ao cliente o Ticket para o TGS criptografado com a chave do TGS
             */
            String tgsPassword = database.map.get("tgs");
            clientFilepath = "C:\\Kerberos\\Client\\tgs_ticket.des"; 
            try {
                fileUtils = new FileUtils(tgsPassword);
                fileUtils.writeEncryptedObject(ast, clientFilepath);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchPaddingException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ImplementAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static void main(String[] args) throws RemoteException {
        int port = 9898;
        String thisAddress;
        try {
            thisAddress = (InetAddress.getLocalHost()).toString();
        } catch (Exception e) {
            throw new RemoteException("Não foi possível pegar o endereço.");
        }

        System.out.println("Endereço IP:" + thisAddress + " ---- Porta: " + port);
        try {
            // Cria o registro
            Registry registry = LocateRegistry.createRegistry(port);
            // Instancia o objeto das implementações do servidor
            ImplementAS interfaceAS = new ImplementAS();
            // Liga o servidor a TAG, para que o cliente possa encontra-lo
            registry.bind("HelloServer", interfaceAS);
            /**
             * Inicia banco de dados com senha dos usuários
             */
            
        } catch (Exception e) {
            System.out.println("Erro " + e.getMessage());
        }
    }

    @Override
    public String sayHello() throws RemoteException {
        return "O servidor mandou um oi =D";
    }
}
