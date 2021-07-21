package cn.com.gmall.util;

import cn.com.gmall.constant.FastDfsConstants;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FastDfsUtil {
    static {
        String tempPath = FastDfsUtil.class.getResource("/").getPath() + FastDfsConstants.FDFS_NAME;
        try {
            tempPath = java.net.URLDecoder.decode(tempPath, "UTF-8");
            ClientGlobal.init(tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public static String upload(MultipartFile multipartFile) {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        // 截取后缀
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(i + 1);

        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            stringBuilder.append(FastDfsConstants.IMG_URI);
            String[] urlData = storageClient.upload_file(multipartFile.getBytes(), suffix, null);
            for (String string : urlData) {
                stringBuilder.append("/");
                stringBuilder.append(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
