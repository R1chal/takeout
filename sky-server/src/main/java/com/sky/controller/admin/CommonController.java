package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Value("${sky.path.base-url}")
    private String baseUrl;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传: {}", file);
        
        if(file == null || file.isEmpty()){
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
        
        try {
            // 原始文件名
            String originalFileName = file.getOriginalFilename();
            // 获取文件扩展名 - 修复这里的错误，之前用0，应该用.
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 构造新文件名
            String objectName = UUID.randomUUID().toString() + extension;
            
            // 本地存储路径
            String localDir = "/Users/richal/upload/";
            File dir = new File(localDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 如果目录不存在则创建
            }

            // 文件本地保存路径
            String localPath = localDir + objectName;
            // 保存文件到本地
            file.transferTo(new File(localPath));
            
            // 返回可访问的URL，而不是本地路径
            String accessUrl = baseUrl + "/files/" + objectName;
            log.info("文件上传成功，访问地址：{}", accessUrl);
            
            return Result.success(accessUrl);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
