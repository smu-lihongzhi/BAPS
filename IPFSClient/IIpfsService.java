/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.ipfsclient;

import java.io.IOException;

public interface IIpfsService {


    /**
     * 指定path+文件名称,上传知ipfs
     * @param filePath
     * @return
     * @throws IOException
     */
    public  String uploadToIpfs(String filePath) throws IOException;

    /**
     * 将byte格式的数据,上传至ipfs
     * @param data
     * @return
     * @throws IOException
     */
    public  String uploadToIpfs(byte[] data) throws IOException;

    /**
     * 根据Hash值,从ipfs下载内容,返回byte数据格式
     * @param hash
     * @return
     */
    public  byte[] downFromIpfs(String hash);

    /**
     * 根据Hash值,从ipfs下载内容,并写入指定文件destFilePath
     * @param hash
     * @param destFilePath
     */
    public void downFromIpfs(String hash, String destFilePath);
}

