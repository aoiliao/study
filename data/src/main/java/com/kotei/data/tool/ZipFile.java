package com.kotei.data.tool;

import com.kotei.data.tool.DefaultPathDefine;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件压缩解压可以使用 net.lingala.zip4j.core.ZipFile
 */
@Log4j2
public class ZipFile {

    private ZipFile() {
        throw new IllegalStateException("ZipFile");
    }

    /**
     * 功能:压缩多个文件成一个zip文件
     *
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static void zipFiles(List<File> srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩  
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (File file : srcfile) {
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
                file.delete();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能:压缩多个文件成一个zip文件 不清除原始文件
     *
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static void zipFiles2(List<Pair<String, File>> srcfile, File zipfile) {
        zipFiles2(srcfile, zipfile, false);
    }

    /**
     * 功能:压缩多个文件成一个zip文件 根据条件判断是否清除原始文件
     *
     * @param srcfile
     * @param zipfile
     * @param deleteSrc
     */
    public static void zipFiles2(List<Pair<String, File>> srcfile, File zipfile, boolean deleteSrc) {
        byte[] buf = new byte[1024];
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (Pair<String, File> file : srcfile) {
                FileInputStream in = new FileInputStream(file.getValue());
                out.putNextEntry(new ZipEntry(file.getKey()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
                if (deleteSrc) {
                    file.getValue().delete();
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压缩【缺点：压缩文件大的解压失败】
     * @param zipPath 压缩文件路径
     * @param targetPath 压缩文件解压目录
     */
    public static void zipDecompressing(String zipPath, String targetPath){
        try{
            File zipFile = new File(zipPath);
            if(!zipFile.exists()){
                return;
            }
            ZipInputStream Zin=new ZipInputStream(new FileInputStream(zipPath), Charset.forName("gbk"));//输入源zip路径

            BufferedInputStream Bin=new BufferedInputStream(Zin);
            ZipEntry entry;
            while((entry = Zin.getNextEntry())!=null){
                if(entry.isDirectory()){
                    File dir = new File(targetPath + DefaultPathDefine.PATH_SPLIT_CHARTER + entry.getName());
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    continue;
                }
                File file=new File(targetPath,entry.getName());
                if(!file.exists()){
                    (new File(file.getParent())).mkdirs();
                }

                BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(file));
                int b;
                while((b=Bin.read())!=-1){
                    out.write(b);
                }
                out.flush();
                out.close();
            }
            Bin.close();
            Zin.close();

        }catch (Exception e){
            log.error("解压缩失败:"+zipPath);
            e.printStackTrace();
        }
    }

    /**
     * 将指定目录下的文件进行压缩
     * @param zipDir 压缩源文件目录
     * @param desZipPath 压缩文件路径
     */
    public static void zipCompress(String zipDir, String desZipPath, boolean deleteSrc){
        List<Pair<String, File>> srcfiles = new ArrayList<>();
        File zipDirFile = new File(zipDir);
        if (zipDirFile.exists() && zipDirFile.isDirectory()) {
            File[] list = zipDirFile.listFiles();
            for(File f : list){
                srcfiles.add(Pair.of(f.getName(),f));
            }
            zipFiles2(srcfiles,new File(desZipPath),deleteSrc);
            zipDirFile.delete();
        }
    }

    /**
     * ZipInputStream是逐个目录进行读取，所以只需要循环
     * @param outPath
     * @param inputStream
     * @throws IOException
     */
    public static void decompressionFile(String outPath, ZipInputStream inputStream) throws IOException {
        //读取一个目录
        ZipEntry nextEntry = inputStream.getNextEntry();
        //不为空进入循环
        while (nextEntry != null) {
            String name = nextEntry.getName();
            File file = new File(outPath+name);
            //如果是目录，创建目录
            if (name.endsWith("/")) {
                file.mkdir();
            } else {
                //文件则写入具体的路径中
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int n;
                byte[] bytes = new byte[1024];
                while ((n = inputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, n);
                }
                //关闭流
                bufferedOutputStream.close();
                fileOutputStream.close();
            }
            //关闭当前布姆
            inputStream.closeEntry();
            //读取下一个目录，作为循环条件
            nextEntry = inputStream.getNextEntry();
        }
    }

    /**
     * 提供给用户使用的解压工具
     * @param srcPath
     * @param outPath
     * @throws IOException
     */
    public static void decompressionFile(String srcPath, String outPath) throws IOException {
        //简单判断解压路径是否合法
        if (!new File(srcPath).isDirectory()) {
            //判断输出路径是否合法
            if (new File(outPath).isDirectory()) {
                if (!outPath.endsWith(File.separator)) {
                    outPath += File.separator;
                }
                //zip读取压缩文件
                FileInputStream fileInputStream = new FileInputStream(srcPath);
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                //解压文件
                decompressionFile(outPath, zipInputStream);
                //关闭流
                zipInputStream.close();
                fileInputStream.close();
            } else {
                throw new RuntimeException("输出路径不合法!");
            }
        } else {
            throw new RuntimeException("需要解压的文件不合法!");
        }
    }

}
