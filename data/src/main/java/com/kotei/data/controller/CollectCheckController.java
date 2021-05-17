package com.kotei.data.controller;

import com.kotei.data.tool.SM4Utils;
import com.kotei.data.tool.ZipFile;
import com.kotei.data.tool.ZipUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class CollectCheckController implements Initializable {

    @FXML
    private Button create;

    @FXML
    private Button update;

    @FXML
    private Button juhe;

    @FXML
    private TextArea logAppender;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logAppender.appendText("请输入Zip包的决定路径！"+ "\n");
    }

    //对Zip包加密
    public void juheCheck(ActionEvent event) {
        try {
            String text = logAppender.getText().trim();
            //解压
            int i = text.lastIndexOf("\\");
            int j = text.lastIndexOf(".");
            String substring = text.substring(0, i);
            String name = text.substring(i, j);
            ZipFile.zipDecompressing(text, substring);

            File sDir = new File(substring + name);
            if (!sDir.exists()) {
                logAppender.appendText("压缩文件有问题，请核查！");
                return;
            }

            File[] files = sDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] files1 = file.listFiles();
                    for (File file1 : files1) {
                        if (file1.isDirectory()) {
                            File[] files2 = file1.listFiles();
                            for (File file2 : files2) {
                                if (!file2.isDirectory()) {
                                    toJson(file2);
                                }
                            }
                        } else {
                            toJson(file1);
                        }
                    }
                } else {
                    toJson(file);
                }
            }

            //加密成功，删除原Zip包，生成一个新的Zip包
            ZipUtil zipUtil = new ZipUtil();
            zipUtil.zip(substring + name, substring + name + "-加密.zip");

            deleteDir(sDir);

            logAppender.appendText("加密成功！");
        } catch (Exception e) {
            logAppender.appendText("加密失败！");
            e.printStackTrace();
        }
    }

    private void toJson(File file1) {
        boolean exists = file1.exists();
        if (exists) {
            String content = readFileContent(file1);
            SM4Utils sm4Utils = new SM4Utils();
            String json = sm4Utils.encryptData_ECB(content);
            String path = file1.getPath();
            boolean delete = file1.delete();
            if (delete) {
                dataToFile(json, path);
            }
        }
    }

    private void dataToFile(String content, String path){
        try {
            File file = new File(path);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFileContent(File file) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    //删除了一个非空的目录
    private static void deleteDir(File dir){
        File[] files = dir.listFiles(); //列出了所有的子文件
        for(File file : files){
            if(file.isFile()){
                file.delete();
            }else if(file.isDirectory()){
                deleteDir(file);
            }
        }
        dir.delete();
    }

}
