package com.imooc.service;

import org.springframework.web.multipart.MultipartFile;

public interface FdfsService {

    String upload(MultipartFile file, String fileExtName) throws Exception;
}
