/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.reencryption;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.lang.System.Logger.Level;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;



/**
 *
 * @author lihongzhi
 */
public class DecryptClient {
    public static Pairing bp;
    public static Element ng;
    public static Element pk_a;  //Alice public key 
    public static Element pk_b; // Bob public key 
    public static Element rsk;  // ReEncrypt key
    public static Element C2;
    public static byte[] C1; //原密文
    public static Element C4;
    public static byte[] C3; //原密文
    
    
    public static Pairing getPairing(){
        Pairing bp = PairingFactory.getPairing("D:\\实验代码\\jpbc-2.0.0\\params\\curves\\a.properties");
        Element ng = bp.getG1().newRandomElement().getImmutable();
        DecryptClient.ng = ng;
        DecryptClient.bp =bp;
        return bp;
    }
    
    public static Element initClient(){
       Field Zr = bp.getZr();
       Element x_b = Zr.newRandomElement().getImmutable();  //私钥
       Element pk_b = ng.duplicate().powZn(x_b); //公钥
       DecryptClient.pk_b = pk_b;
       return x_b;
    }
    
    
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NamingException, RemoteException {
        
       getPairing();
       Element x_b = initClient();
       String url =  "rmi://127.0.0.1:8828/";
       Context namingContext = new InitialContext();
       inCSServ CSServer = (inCSServ)namingContext.lookup(url+"CSServer");
        try {
                CSServer.registPubk("ClientB", pk_b.toBytes());
                System.out.println(pk_b);
                
            } catch (RemoteException ex) {
                java.util.logging.Logger.getLogger(DecryptClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } 
       
       
       
       
       
    
    }
    
    
}
