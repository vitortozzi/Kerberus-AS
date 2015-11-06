/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerberos;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import message.ASAckResponse;
import message.ASRequest;
import message.ASResponse;
import utils.RandomUtils;

/**
 *
 * @author Vitor Tozzi
 */
public class ImplementAS extends UnicastRemoteObject implements InterfaceAS{
    
    private static final String clientPassword = "vitor123";

    public ImplementAS() throws RemoteException{
        super();
    }
    
    @Override
    public ASResponse doLogin(ASRequest request) {   
        return null;
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
        } catch (Exception e) {
            System.out.println("Erro " + e.getMessage());
        }
    }

    @Override
    public String sayHello() throws RemoteException {
        return "O servidor mandou um oi =D";
    }
}
