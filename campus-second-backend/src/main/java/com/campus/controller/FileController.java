package com.campus.controller;

import com.campus.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传接口
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload-path:upload/}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            return Result.error("文件名无效");
        }
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            ext = originalName.substring(dot);
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String dirPath = System.getProperty("user.dir") + "/" + uploadPath;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir, fileName));
            return Result.success(uploadPath + fileName);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
