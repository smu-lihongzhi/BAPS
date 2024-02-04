/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.reencryption;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author lihongzhi
 */
public class EncryptClient {
    
    public static Pairing bp;
    public static Field Zr;
    public static Element ng;
    public static Element pk_a=null;  //Alice public key 
    public static Element pk_b=null; // Bob public key 
    public static Element rsk;  // ReEncrypt key
    public static Element C2;
    public static byte[] C1; //原密文
    public static Element C4;
    public static byte[] C3; //原密文
    
    
    public static Pairing getPairing(){
        Pairing bp = PairingFactory.getPairing("D:\\实验代码\\jpbc-2.0.0\\params\\curves\\a.properties");
        Element ng = bp.getG1().newRandomElement().getImmutable();
        EncryptClient.ng = ng;
        EncryptClient.bp =bp;
        return bp;
    }
    
    
    public static void Encrypt(Element x_a,String msg){
        String Message=msg;
        byte[] m_hash = Message.getBytes();
        Field Zr = bp.getZr();
        EncryptClient.Zr=Zr;
        Field G1 = bp.getG1();
        Element ra = Zr.newRandomElement().getImmutable(); //随机数ra
         //
        
        System.out.println("ra=");
        System.out.println(ra);
        Element egg = bp.pairing(EncryptClient.ng.duplicate(), EncryptClient.ng.duplicate());
          
        Element R1 = egg.duplicate().powZn(ra);  //参数R1
        System.out.println("R1=");
        System.out.println(R1);
        
        byte[] HR1 =  R1.toBytes();
        byte[] C1 = new byte[m_hash.length]; //哈希函数计算
        for (int i = 0;i<m_hash.length;i++){
            C1[i] = (byte)(m_hash[i] ^ HR1[i]);
        }
        System.out.println("C1=");
        System.out.println( Base64.getEncoder().encodeToString(C1));
        EncryptClient.C1 = C1;
        
        
        Element pr = x_a.duplicate().div(ra);
        Element lg= ng.duplicate().powZn(Zr.newOneElement()).mul(pk_b.duplicate());
        
        
        Element C2 = lg.powZn(pr);
        System.out.println("C2=");
        System.out.println(C2);
        EncryptClient.C2 = C2;
        //
        
        genReEnkey(pk_b,x_a,ra);  //定向生成重加密密钥
    } 
    
    public static Element genReEnkey(Element pk_b,Element x_a,Element r_a){
         Element ra_d_xa = r_a.duplicate().div(x_a);
         Element rsk = pk_b.duplicate().powZn(ra_d_xa);
         EncryptClient.rsk = rsk;
         return rsk;
    }
    
     public static void reEncrypt(Element rsk,byte[] C1,Element C2 ){
        Element  ap = bp.pairing(rsk, C2);
        byte[] HAP =  ap.toBytes();
        byte[] C3 = new byte [C1.length]; 
        for(int i = 0; i< C1.length;i++){
            C3[i] = (byte)(C1[i] ^ HAP[i]);
        }
        
        EncryptClient.C3 = C3;
        System.out.println("C3=");
        System.out.println(C3);
        
        Element C4 = bp.pairing(rsk.duplicate(), EncryptClient.pk_a);
        EncryptClient.C4 = C4;
        System.out.println("C4=");
        System.out.println(C4);
    }
    
    public static void Decrypt(Element x_b,byte[] C3,Element C4){
        Field Zr = bp.getZr();
        Element add_ret = Zr.newOneElement().add(x_b.duplicate());  
        Element mul_ret = x_b.duplicate().mul(add_ret);
        Element egg = bp.pairing(EncryptClient.ng.duplicate(), EncryptClient.ng.duplicate());
        Element egg_pow = egg.duplicate().powZn(mul_ret);
        
        byte[] HEGG =  egg_pow.toBytes();
        byte[] C1_t = new byte [C3.length] ;
        for(int i = 0;i<C3.length; i++){
            C1_t[i] = (byte)(C3[i] ^ HEGG[i]);
        }
        System.out.println("C1_t=");
        System.out.println(C1_t[1]);
        
        Element r2 =Zr.newOneElement().div(x_b);
        Element t_R1 = C4.duplicate().powZn(r2.duplicate());
        byte[] HR1 =  t_R1.toBytes(); 
        byte[] MT = new byte[C1_t.length];
        for (int i =0;i<C1_t.length;i++){
            MT[i] = (byte)(C1_t[i] ^ HR1[i]);
        }
        System.out.println("m=");
        System.out.println( new String (MT));
    }
     
    
    
    public static Element initClientA(){  //客户端A
        Field Zr = bp.getZr();
        Element x_a= Zr.newRandomElement().getImmutable();  //私钥
        Element pk_a = ng.duplicate().powZn(x_a); //公钥
        EncryptClient.pk_a = pk_a;
        System.out.println("pk_a"+pk_a);
        return x_a;
    }
    
     public static Element initClientB(){
       Field Zr = bp.getZr();
       Element x_b = Zr.newRandomElement().getImmutable();  //私钥
       Element pk_b = ng.duplicate().powZn(x_b); //公钥
       EncryptClient.pk_b = pk_b;
       return x_b;
    }
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NamingException {
       getPairing();
       Element x_a = initClientA();
       Element x_b = initClientB();
       String msg = "This is a test message";
       Encrypt(x_a,msg);//加密
       reEncrypt(EncryptClient.rsk ,EncryptClient.C1,EncryptClient.C2);
       Decrypt(x_b, EncryptClient.C3,EncryptClient.C4);
       
    } 
}
