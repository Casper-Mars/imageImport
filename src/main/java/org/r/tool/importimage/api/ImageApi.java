package org.r.tool.importimage.api;

import org.r.tool.importimage.pojo.ApiReuslt;
import org.r.tool.importimage.service.ImportService;
import org.r.tool.importimage.service.SpyImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author casper
 * @date 20-1-16 下午4:26
 **/
@CrossOrigin
@RestController
@RequestMapping("/v1/image/import")
public class ImageApi {


    @Autowired
    private ImportService importService;
    @Autowired
    private SpyImageService spyImageService;


    @RequestMapping("/normal")
    public ApiReuslt<String> importNorlmal(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        String mode = request.getParameter("mode");
        importService.importNormal(file, mode);
        return new ApiReuslt<>();
    }

    @RequestMapping("/spec")
    public ApiReuslt<String> importSpec(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        importService.importSpec(file);
        return new ApiReuslt<>();
    }

    @RequestMapping("/spy")
    public ApiReuslt<String> spyImage(String url) {

        if (StringUtils.isEmpty(url)) {
            return ApiReuslt.error("url不能为空");
        }
        String filename = null;
        ApiReuslt<String> result = ApiReuslt.success();
        try {
            filename = spyImageService.getImageFromUrl(url);
            result.setData(filename);
        } catch (Exception e) {
            e.printStackTrace();
            result = ApiReuslt.error(e.getMessage());
        }
        return result;
    }

    @RequestMapping("/spy/download")
    public void getSpyImage(String filename, HttpServletResponse response) {
        spyImageService.downLoad(response, filename);
    }


}
