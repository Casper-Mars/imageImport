package org.r.tool.importimage.api;

import org.r.tool.importimage.pojo.ApiReuslt;
import org.r.tool.importimage.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author casper
 * @date 20-1-16 下午3:32
 **/
@CrossOrigin
@RestController
@RequestMapping("/v1/image/import")
public class ImageImportApi {

    @Autowired
    private ImportService service;


    @RequestMapping("/normal")
    public ApiReuslt<String> importNorlmal(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        String mode = request.getParameter("mode");

        service.importNormal(file, mode);

        return new ApiReuslt<>();
    }


}
