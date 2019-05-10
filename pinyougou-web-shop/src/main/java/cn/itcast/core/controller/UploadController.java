package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传图片
 */
@RestController
@RequestMapping("/upload")
public class UploadController {


    //获取properties文件中的Value值
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;



    //上传商品图片  Springmvc 上传图片
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){


        //接收图片  打印图片的原始名称
        try {
            System.out.println(file.getOriginalFilename());
            //把图片上传到分布式文件系统FastDFS上去 并返回路径  工具类
            //  创建上传实现类                             //classpath:fastDFS/fdfs_client.conf
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //上传图片
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            //路径
            String path = fastDFSClient.uploadFile(file.getBytes(), ext, null);

            return new Result(true,FILE_SERVER_URL + path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }



}
