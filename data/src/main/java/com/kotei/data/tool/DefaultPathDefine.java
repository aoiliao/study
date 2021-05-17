package com.kotei.data.tool;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author ：jinzhuz
 * @Date ：Created in 17:34 2020/2/20
 * @Description：
 * @Modified By：
 */
@Data
public class DefaultPathDefine implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PATH_SPLIT_CHARTER = "/";

    /**
     * 临时文件交换区路径
     */
    public static final String SWAP_SPACE_PATHNAME = "SWAP" + PATH_SPLIT_CHARTER;

    /**
     * 数据对接目录
     */
    public static final String ATTACHMENT_MDB_PATH = "DOCKING" +PATH_SPLIT_CHARTER;

    /**
     * 缩略图文件目录名
     */
    public static final String THUMBNAIL_SPACE_PATHNAME = "THUMBNAIL" + PATH_SPLIT_CHARTER;

    /**
     * 附件存储目录名
     */
    public static final String ATTACHMENT_SPACE_PATHNAME = "ATTACHMENTS" + PATH_SPLIT_CHARTER;

    /**
     * 解压区
     */
    public static final String UNZIP = "UNZIP" + PATH_SPLIT_CHARTER;

    /**
     * PDF文件目录名
     */
    public static final String PDF = "PDF" + PATH_SPLIT_CHARTER;

    /**
     * 模板文件目录名
     */
    public static final String TEMPLATES = "TEMPLATES" + PATH_SPLIT_CHARTER;

    /**
     * excel导入的文件目录名
     */
    public static final String EXCELCONFIRM = "EXCELCONFIRM" + PATH_SPLIT_CHARTER;

    /**
     * nginx全局文件库别名
     */
    public static final String GLOBALFILES = "GlobalFiles" + PATH_SPLIT_CHARTER;

    /**
     * 生成下载附件的目录
     */
    public static final String DOWNLOAD_ATTACHMENNT_PATH = "DownloadAttachment"+ PATH_SPLIT_CHARTER;

}
