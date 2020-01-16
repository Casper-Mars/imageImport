package org.r.tool.importimage.service;

import org.r.tool.importimage.util.StreamTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author casper
 * @date 20-1-16 下午3:55
 **/
@Service
public class ImportService {


    @Value("${file.zip.path}")
    private String zipPath;
    @Autowired
    private ScriptService scriptService;


    public void importNormal(MultipartFile file, String mode) {

        /*保存压缩包文件*/
        try {
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            filename = zipPath + filename;
            File zipFile = new File(filename);
            OutputStream outputStream = new FileOutputStream(zipFile);
            StreamTool.StreamCopy(inputStream, outputStream);
            outputStream.close();
            scriptService.runImport(filename, Integer.valueOf(mode));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void importSpec(MultipartFile file) {


    }


}
