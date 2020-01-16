package org.r.tool.importimage.service;

import org.r.tool.importimage.util.IdTool;
import org.r.tool.importimage.util.StreamTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author casper
 * @date 20-1-16 下午4:12
 **/
@Service
public class SpyImageService {


    @Value("${file.spy.path}")
    private String spyPath;
    @Autowired
    private ScriptService scriptService;


    public String getImageFromUrl(String url) {
        String name = getName(url);
        if (StringUtils.isEmpty(name)) {
            name = String.valueOf(IdTool.next());
        }
        scriptService.runSpy(spyPath, name, url);
        return name;
    }

    public void downLoad(HttpServletResponse response, String filename) {

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
        filename = spyPath + File.separator + filename + ".zip";
        File target = new File(filename);
        if (!target.exists()) {
            throw new RuntimeException("文件不存在");
        }
        try (InputStream inputStream = new FileInputStream(target)) {
            ServletOutputStream outputStream = response.getOutputStream();
            StreamTool.StreamCopy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getName(String url) {

        int indexWhy = url.lastIndexOf('?');

        if (indexWhy <= 0) {
            indexWhy = url.length();
        }
        int indexC = url.lastIndexOf('/');

        String name = url.substring(indexC + 1, indexWhy);
        name = name.replace(".html", "");
        return name;
    }


}
