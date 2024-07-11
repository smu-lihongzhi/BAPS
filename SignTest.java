/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.reencryption;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

/**
 *
 * @author lihongzhi
 */
public class SignTest {
    
    public static Pairing bp;
    public static Field Zr;
    public static Element ng;
    public static Element pk_a=null;  //Alice public key 
    public static Element pk_b=null; // Bob public key 
    public static Element Sigma = null;
    
    
    /**
     * 
     * @return 
     */
    public static Pairing getPairing(){
        Pairing bp = PairingFactory.getPairing("D:\\实验代码\\jpbc-2.0.0\\params\\curves\\a.properties");
        SignTest.bp =bp;
        Field Zr = bp.getZr();
        SignTest.Zr=Zr;
        Element ng =bp.getG1().newRandomElement().getImmutable();
        SignTest.ng = ng;
        return bp;
    }
    
    /**
     * 
     */
    public static void AgentA(String msg){
        Element x_a = Zr.newRandomElement();
        Element g_x_a = ng.powZn(x_a);
        pk_a = g_x_a;
        Element Sigma = genSigma(x_a,msg);
        SignTest.Sigma = Sigma;
    }
    
    
    public static void AgentB(String msg){
        Element x_b = Zr.newRandomElement();
        Element g_x_b = ng.powZn(x_b);
        pk_b = g_x_b;
       verifySigma(pk_a,msg);
        
    }
    
    
    
    public static Element genSigma(Element privk, String msg){
        byte[] m_hash = msg.getBytes();
        Element hash_e = Zr.newElementFromHash(m_hash, 0, m_hash.length);
        Element mi = privk.mul(hash_e);
        Element Sigma = ng.powZn(mi);
        return Sigma;
    }
    
    
    public static boolean verifySigma(Element pubk,String msg){
        byte[] m_hash = msg.getBytes();
        Element egg = bp.pairing(SignTest.Sigma,ng);
        System.out.println("egg"+egg);
      
        Element hash_e = Zr.newElementFromHash(m_hash, 0, m_hash.length);
        //Element egg_pow = egg.duplicate().powZn(mul_ret);
        Element egg_f = bp.pairing(pubk,ng.powZn(hash_e));
        System.out.println("egg_f"+egg_f);
        return false;
    }
    
    
    public static void main(String[] args){
        getPairing();
        String msg = "Hello world,Hello world,Hello world,Hello world,Hello world,Hello world,Hello world";
        
        AgentA(msg);
        long stime = System.currentTimeMillis();
        //for(int i = 0;i<100;i++){
        AgentB(msg);
        //}
        long etime = System.currentTimeMillis();
        System.out.printf("cost：%d (ms).\n", (etime - stime));
    }
    
}
