package org.r.tool.importimage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
@Slf4j
public class ScriptService {
    public static final int MOD_FULL = 1;
    public static final int MOD_EXCEPT_DETAIL = 2;
    public static final String TMP_PATH = "/tmp";
    public static final String SCRIPT_PATH = "/opt/share/git/image-import";

    @Async
    public void runImport(String zipPath, int mod) {
        if (mod!=MOD_FULL && mod!=MOD_EXCEPT_DETAIL){
            log.error("mod参数错误");
            return;
        }
        long now = System.currentTimeMillis();
        String target = TMP_PATH + File.separator + now + File.separator;

        decompressZip(zipPath, target);

        Runtime runtime = Runtime.getRuntime();
        log.info("开始处理: " + zipPath);
        try {
            String sh = "python3 "+SCRIPT_PATH+File.separator+"Main.py --base='"+target+"' --mod=" + mod;
            log.info("运行: " + sh);
            Process process = runtime.exec(sh);
            String s = StreamUtils.copyToString(process.getInputStream(), Charset.defaultCharset());
            String s2 = StreamUtils.copyToString(process.getErrorStream(), Charset.defaultCharset());
            log.info(s);
            log.error(s2);
            process.waitFor();
            log.info("app exit with "+process.exitValue());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void decompressZip(String zipPath, String descDir) {
        File zipFile = new File(zipPath);
        File pathFile = new File(descDir);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
            for(Enumeration entries = zip.entries(); entries.hasMoreElements();){
                ZipEntry entry = (ZipEntry)entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                //指定解压后的文件夹+当前zip文件的名称
                String outPath = (descDir+zipEntryName).replace("/", File.separator);
                //判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                if(!file.exists()){
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if(new File(outPath).isDirectory()){
                    continue;
                }
                //保存文件路径信息（可利用md5.zip名称的唯一性，来判断是否已经解压）
                System.err.println("当前zip解压之后的路径为：" + outPath);
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[2048];
                int len;
                while((len=in.read(buf1))>0){
                    out.write(buf1,0,len);
                }
                in.close();
                out.close();
            }
            //必须关闭，要不然这个zip文件一直被占用着，要删删不掉，改名也不可以，移动也不行，整多了，系统还崩了。
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
