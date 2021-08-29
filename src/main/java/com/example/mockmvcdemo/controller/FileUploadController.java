package com.example.mockmvcdemo.controller;

import com.example.mockmvcdemo.vo.FileVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传
 *
 * @author one
 * @version 1.0.0
 * @since 2021/08/22
 */
@RestController
public class FileUploadController {

    @PostMapping("/doc")
    public FileVO doc(MultipartFile file) throws IOException {
        return new FileVO(new String(file.getBytes()));
    }
}
