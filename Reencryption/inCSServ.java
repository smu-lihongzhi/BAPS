/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.reencryption;
import it.unisa.dia.gas.jpbc.Element;
import java.math.BigInteger;
import java.rmi.Remote; 
import java.rmi.RemoteException; 
import java.util.Map;

/**
 *
 * @author lihongzhi
 */
public interface inCSServ extends Remote{
    public void registPubk(String name, byte[] Pubk) throws RemoteException; ;
    public byte[] getPubkByName(String name)throws RemoteException;
    
}

