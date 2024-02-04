/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.reencryption;

import it.unisa.dia.gas.jpbc.Element;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author lihongzhi
 */
public class CSServer extends UnicastRemoteObject implements inCSServ{
    private static Map<String,byte[]> keyRegister=new HashMap<>(); //用以存储
    ReentrantLock lock = new ReentrantLock();
    
     @Override
    public void registPubk(String name, byte[] Pubk){
        lock.lock();
        try{
            System.out.println("name="+name);
            keyRegister.put(name, Pubk);
        }finally {
            lock.unlock();
        }
    }
    
    public byte[] getPubkByName(String name)throws RemoteException{
        byte[] Pubk = keyRegister.get(name);
        
        return Pubk;
    }
    
     public CSServer() throws RemoteException {
        //this.name = name;
    }
    
    public static void Invoke() throws RemoteException, NamingException{
        System.out.println("CSServer Server Initing...");
        inCSServ CSServer = new CSServer(); 
        System.setProperty("java.rmi.server.hostname", "127.0.0.1"); 
        LocateRegistry.createRegistry(8828); 
        Context namingContext = new InitialContext(); 
        namingContext.bind("rmi://127.0.0.1:8828/CSServer", CSServer);
        System.out.println("Name Server is OK!");
    }
    
    
    public static void main(String[] args) throws RemoteException, NamingException { 
        try{
            Invoke();
        }catch(Exception e){
            System.out.println("注册远程对象失败，错误信息：" + e.getMessage());
            
        }    
   }
    
    
}
