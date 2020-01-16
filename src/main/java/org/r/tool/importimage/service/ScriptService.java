package org.r.tool.importimage.service;

import lombok.extern.slf4j.Slf4j;
import org.r.tool.importimage.thread.DealProcessSream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.*;

@Service
@Slf4j
public class ScriptService {
    public static final int MOD_FULL = 1;
    public static final int MOD_EXCEPT_DETAIL = 2;
    public static final String TMP_PATH = "/tmp";


    @Value("${file.script.path}")
    private String SCRIPT_PATH;



    public void runSpy(String path, String filename, String url) {
        String targetPath = path + File.separator + filename;
        String sh = String.format("python3 %s --targetPath='%s' --url='%s'", SCRIPT_PATH + File.separator + "GetImage.py", targetPath, url);
        runScript(sh);
        try {
            compress(targetPath, targetPath + ".zip");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("压缩图片出错");
        }
    }


    @Async
    public void runImport(String zipPath, int mod) {
        if (mod != MOD_FULL && mod != MOD_EXCEPT_DETAIL) {
            log.error("mod参数错误");
            return;
        }
        long now = System.currentTimeMillis();
        String target = TMP_PATH + File.separator + now + File.separator;

        decompressZip(zipPath, target);

        log.info("开始处理: " + zipPath);
        String sh = "python3 " + SCRIPT_PATH + File.separator + "Main.py --base='" + target + "' --mod=" + mod;
        runScript(sh);
    }


    public void runScript(String sh) {

        try {
            Runtime runtime = Runtime.getRuntime();
            log.info("运行: " + sh);
            Process process = runtime.exec(sh);
            //新启两个线程
            new DealProcessSream(process.getInputStream()).start();
            new DealProcessSream(process.getErrorStream()).start();
//            String s = StreamUtils.copyToString(process.getInputStream(), Charset.defaultCharset());
//            String s2 = StreamUtils.copyToString(process.getErrorStream(), Charset.defaultCharset());
//            if(!StringUtils.isEmpty(s)){
//                log.info(s);
//            }
//            if(!StringUtils.isEmpty(s2)){
//                log.error(s2);
//            }
            process.waitFor();
            log.info("app exit with " + process.exitValue());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("导入图片脚本运行出错");
        }
    }

    /**
     * 压缩文件夹
     *
     * @param dirPath 文件夹路径
     * @param descDir 目标文件路径
     */
    public void compressZip(String dirPath, String descDir) {
        try {
            compress(dirPath, descDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压缩zip
     *
     * @param zipPath
     * @param descDir
     */
    private void decompressZip(String zipPath, String descDir) {
        File zipFile = new File(zipPath);
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = null;
        try {
            zip = new ZipFile(zipFile);
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                //指定解压后的文件夹+当前zip文件的名称
                String outPath = (descDir + zipEntryName).replace("/", File.separator);
                //判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                if (!file.exists()) {
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                //保存文件路径信息（可利用md5.zip名称的唯一性，来判断是否已经解压）
                System.err.println("当前zip解压之后的路径为：" + outPath);
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[2048];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
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

    static final int BUFFER = 8192;

    private void compress(String srcPath, String dstPath) throws IOException {
        File srcFile = new File(srcPath);
        File dstFile = new File(dstPath);
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcPath + "不存在！");
        }

        FileOutputStream out = null;
        ZipOutputStream zipOut = null;
        try {
            out = new FileOutputStream(dstFile);
            CheckedOutputStream cos = new CheckedOutputStream(out, new CRC32());
            zipOut = new ZipOutputStream(cos);
            String baseDir = "";
            compress(srcFile, zipOut, baseDir);

        } finally {
            if (null != zipOut) {
                zipOut.close();
            }

            if (null != out) {
                out.close();
            }
        }
    }

    private void compress(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (file.isDirectory()) {
            compressDirectory(file, zipOut, baseDir);
        } else {
            compressFile(file, zipOut, baseDir);
        }
    }

    /**
     * 压缩一个目录
     */
    private void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            compress(files[i], zipOut, baseDir + dir.getName() + "/");
        }
    }

    /**
     * 压缩一个文件
     */
    private void compressFile(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (!file.exists()) {
            return;
        }

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zipOut.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }

        } finally {
            if (null != bis) {
                bis.close();
            }
        }
    }
}
