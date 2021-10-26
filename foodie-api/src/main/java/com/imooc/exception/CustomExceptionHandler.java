package com.imooc.exception;

import com.imooc.utils.IMOOCJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {

    //上传文件超过500K的异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public IMOOCJSONResult handlerMaxUploadFileException(MaxUploadSizeExceededException exception){
        return IMOOCJSONResult.errorMsg("文件上传大小不能超过500K!");
    }
}
