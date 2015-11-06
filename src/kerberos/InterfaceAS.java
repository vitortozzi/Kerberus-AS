/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerberos;

import message.ASResponse;
import message.ASRequest;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Vitor Tozzi
 */
public interface InterfaceAS extends Remote{
    
    public void doLogin(ASRequest request) throws RemoteException;
    public String sayHello() throws RemoteException;
}
