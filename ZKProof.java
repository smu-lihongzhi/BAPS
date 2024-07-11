/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package zkproof;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Base64;
import java.util.logging.Level;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
/**
 *
 * @author lihongzhi
 */
public class ZKProof {
    public static Pairing bp;
    public static Field Zr;
    public static Element ng;
    public static Element pk_a=null;  //Alice public key 
    public static Element pk_b=null; // Bob public key 
    public static Element rsk;  // ReEncrypt key
    public static Element C2;
    public static BigInteger p;
    public static BigInteger q;
    public static BigInteger a;
    public static BigInteger h;
    public static BigInteger e;
    public static BigInteger y;
    public static BigInteger r;
    public static Element hv;
    
    
    public static Pairing getPairing(){
        Pairing bp = PairingFactory.getPairing("D:\\实验代码\\jpbc-2.0.0\\params\\curves\\a.properties");
        //QualityScratchpad qualityScratchpad = new QualityScratchpad(pairing, 1000000, 1 << 20);
        Field zr = bp.getZr();
        Element ng = zr.newRandomElement().getImmutable();
        BigInteger q = zr.getOrder();
        ZKProof.ng = ng;
        ZKProof.bp =bp;
        ZKProof.q = q;  
        // 示例操作：生成一个在 zr 上的随机元
        Element r = zr.newRandomElement().getImmutable();
       // System.out.println("Random Element in zr: " + r);
        BigInteger qi = r.toBigInteger();
        boolean isPrime = qi.isProbablePrime(0);
         while (!isPrime) {
            qi = qi.add(BigInteger.ONE);
            isPrime = qi.isProbablePrime(0);
        }
        ZKProof.p = qi;
        return bp;
    }
    
    // m is the input message 
    public static Element generateH(String msg) throws NoSuchAlgorithmException{
        byte[] m_hash = msg.getBytes();
        MessageDigest  md= MessageDigest.getInstance("SHA-256");
        md.update(m_hash);
        byte[] hashValue = md.digest();
        Element hv = bp.getZr().newElementFromHash(hashValue, 0, hashValue.length).getImmutable(); //hash 
       // System.out.println("hv="+hv);
        ZKProof.hv = hv;       
        Element tmp = ng.powZn(ZKProof.hv);
        //BigInteger h = (tmp.toBigInteger().mod(p));
       // System.out.println("h="+tmp);
        ZKProof.h = tmp.toBigInteger().mod(p);
        return null;
    }
    
    public static Element generateA(){
        Element r = bp.getZr().newRandomElement().getImmutable();
        ZKProof.r = r.toBigInteger();
        Element tmp = ng.powZn(r).getImmutable();
       // System.out.println("a="+tmp);
        ZKProof.a = tmp.toBigInteger().mod(p);
        return null;
    }
    
    public static Element generateY() throws NoSuchAlgorithmException{
        Element e = bp.getZr().newElementFromBytes(h.xor(a).toByteArray()).getImmutable();
        ZKProof.e = e.toBigInteger();
        //System.out.println("hv="+hv);
        BigInteger mulq =  e.mul(hv).toBigInteger().mod(q);
        BigInteger yp = ZKProof.r.add(mulq) ;   //toBigInteger().add (e.mul(hv).toBigInteger());    //e.mul(hv); .add(e.mul(hv)); 
        ZKProof.y = yp;
        return null;
    }
    /**
     * @param args the command line arguments
     */
    public static void generateProof() throws NoSuchAlgorithmException{
        getPairing();
        generateH("hello world");
        generateA();
        generateY();
        System.out.println("proof is { a: "+a+"h:"+h+"e:"+e+"y:"+y+"}");
    }
    
    
    public static void verifyProof(){
        Element e1 = ng.pow(y);
        BigInteger le = e1.toBigInteger();
        //System.out.println("le "+le);
        
        Element h1 = bp.getZr().newElement(h);
        Element e2 = bp.getZr().newElement(e);
        Element r2 = h1.powZn(e2);
        BigInteger ri = r2.toBigInteger();
        BigInteger re = a.multiply(ri).mod(p);
       // System.out.println("re "+re);
        if(le == re){
            System.out.println("verify "+true);
        }else{
            System.out.println("verify "+false);
        }
        
    }
    
    
    public static void getMaxMinAvg( List arrayList1){
        
        long max = (long) arrayList1.get(0);
        long min = (long) arrayList1.get(0);
        long sum = 0;
        for(int i= 0;i<arrayList1.size();i++){
              long tmp = (long)arrayList1.get(i);
              if(tmp > max) max = tmp;
              if(tmp < min) min = tmp;
              sum = sum + tmp;
        }
        long avg = sum / arrayList1.size();
        System.out.println("Max:"+max+" Min:"+min+" Avg:"+avg);
    }
    
    
    
    
    
    
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // TODO code application logic here
        List arrayList1 = new ArrayList<Long>();
        List arrayList2 = new ArrayList<Long>();
        for(int i = 0; i<50;i++){
            long startTime = System.currentTimeMillis();
            generateProof();
            long endTime = System.currentTimeMillis();
            System.out.println("generateProof run time：" + (endTime - startTime));
            arrayList1.add(endTime - startTime);
            long startTime1 = System.currentTimeMillis();
            verifyProof();
            long endTime1 = System.currentTimeMillis();
            System.out.println("verifyProof run time：" + (endTime1 - startTime1));
            arrayList2.add(endTime1 - startTime1);
        }
        getMaxMinAvg(arrayList1);
        getMaxMinAvg(arrayList2);
    }
    
}
