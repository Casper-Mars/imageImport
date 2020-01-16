package org.r.tool.importimage.api;

import org.r.tool.importimage.pojo.ApiReuslt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author casper
 * @date 20-1-16 下午1:36
 **/
@RestController
@RequestMapping("/v1/file")
public class FileApi {


    /**
     * 上传文件
     *
     * @return
     */
    @CrossOrigin
    @RequestMapping("/upload")
    public ApiReuslt<String> fileUpload(@RequestPart("file") MultipartFile file, HttpServletRequest request) {


        return new ApiReuslt<>();
    }


}
