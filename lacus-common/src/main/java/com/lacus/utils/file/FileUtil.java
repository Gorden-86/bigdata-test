package com.lacus.utils.file;

import com.lacus.common.constant.Constants;
import com.lacus.utils.OSUtils;
import com.lacus.utils.CommonPropertyUtils;
import com.lacus.utils.time.DateUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import static com.lacus.common.constant.Constants.DATA_BASEDIR_PATH;
import static com.lacus.utils.time.DateUtils.YYYYMMDDHHMMSS;

@UtilityClass
@Slf4j
public class FileUtil {

    public static final String DATA_BASEDIR = CommonPropertyUtils.getString(DATA_BASEDIR_PATH, System.getProperty("user.home") + "/tmp/lacus");

    public static final String APPINFO_PATH = "appInfo.log";

    public static final String KUBE_CONFIG_FILE = "config";

    private static final Set<PosixFilePermission> PERMISSION_755 = PosixFilePermissions.fromString("rwxr-xr-x");

    /**
     * get download file absolute path and name
     *
     * @param filename file name
     * @return download file name
     */
    public static String getDownloadFilename(String filename) {
        String fileName =
                String.format("%s/download/%s/%s", DATA_BASEDIR, DateUtils.getCurrentTime(YYYYMMDDHHMMSS), filename);

        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return fileName;
    }

    public static String getUploadFilename(String filename) {
        String fileName = String.format("%s/resources/%s", DATA_BASEDIR, filename);
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        return fileName;
    }

    /**
     * directory of process execution
     *
     * @param tenant               tenant
     * @param projectCode          project code
     * @param processDefineCode    process definition Code
     * @param processDefineVersion process definition version
     * @param processInstanceId    process instance id
     * @param taskInstanceId       task instance id
     * @return directory of process execution
     */
    public static String getTaskInstanceWorkingDirectory(String tenant,
                                                         long projectCode,
                                                         long processDefineCode,
                                                         int processDefineVersion,
                                                         int processInstanceId,
                                                         int taskInstanceId) {
        return String.format(
                "%s/exec/process/%s/%d/%d_%d/%d/%d",
                DATA_BASEDIR,
                tenant,
                projectCode,
                processDefineCode,
                processDefineVersion,
                processInstanceId,
                taskInstanceId);
    }

    /**
     * absolute path of kubernetes configuration file
     *
     * @param execPath
     * @return
     */
    public static String getKubeConfigPath(String execPath) {
        return String.format(Constants.FORMAT_S_S, execPath, KUBE_CONFIG_FILE);
    }

    /**
     * absolute path of appInfo file
     *
     * @param execPath directory of process execution
     * @return
     */
    public static String getAppInfoPath(String execPath) {
        return String.format("%s/%s", execPath, APPINFO_PATH);
    }

    /**
     * @return get suffixes for resource files that support online viewing
     */
    public static String getResourceViewSuffixes() {
        return CommonPropertyUtils.getString(Constants.RESOURCE_VIEW_SUFFIXES, Constants.RESOURCE_VIEW_SUFFIXES_DEFAULT_VALUE);
    }

    public static boolean writeToFile(String content, String filePath) {
        FileOutputStream fos = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists() && !distFile.getParentFile().mkdirs()) {
                log.error("mkdir parent failed");
                return false;
            }
            fos = new FileOutputStream(filePath);
            IOUtils.write(content, fos, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            IOUtils.closeQuietly(fos);
        }
        return true;
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param filename file name
     */
    public static void deleteFile(String filename) {
        org.apache.commons.io.FileUtils.deleteQuietly(new File(filename));
    }

    /**
     * Get Content
     *
     * @param inputStream input stream
     * @return string of input stream
     */
    public static String readFile2Str(InputStream inputStream) {

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return output.toString(Constants.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Check whether the given string type of path can be traversal or not, return true if path could
     * traversal, and return false if it is not.
     *
     * @param filename String type of filename
     * @return whether file path could be traversal or not
     */
    public static boolean directoryTraversal(String filename) {
        if (filename.contains(Constants.FOLDER_SEPARATOR)) {
            return true;
        }
        File file = new File(filename);
        try {
            File canonical = file.getCanonicalFile();
            File absolute = file.getAbsoluteFile();
            return !canonical.equals(absolute);
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Calculate file checksum with CRC32 algorithm
     *
     * @param pathName
     * @return checksum of file/dir
     */
    public static String getFileChecksum(String pathName) throws IOException {
        CRC32 crc32 = new CRC32();
        File file = new File(pathName);
        String crcString = "";
        if (file.isDirectory()) {
            // file system interface remains the same order
            String[] subPaths = file.list();
            StringBuilder concatenatedCRC = new StringBuilder();
            for (String subPath : subPaths) {
                concatenatedCRC.append(getFileChecksum(pathName + Constants.FOLDER_SEPARATOR + subPath));
            }
            crcString = concatenatedCRC.toString();
        } else {
            try (
                    FileInputStream fileInputStream = new FileInputStream(pathName);
                    CheckedInputStream checkedInputStream = new CheckedInputStream(fileInputStream, crc32);) {
                while (checkedInputStream.read() != -1) {
                }
            } catch (IOException e) {
                throw new IOException("Calculate checksum error.");
            }
            crcString = Long.toHexString(crc32.getValue());
        }

        return crcString;
    }

    public static void createFileWith755(@NonNull Path path) throws IOException {
        if (SystemUtils.IS_OS_WINDOWS) {
            Files.createFile(path);
        } else {
            Files.createFile(path);
            Files.setPosixFilePermissions(path, PERMISSION_755);
        }
    }

    public static void createDirectoryWith755(@NonNull Path path) throws IOException {
        if (path.toFile().exists()) {
            return;
        }
        if (OSUtils.isWindows()) {
            Files.createDirectories(path);
        } else {
            Path parent = path.getParent();
            if (parent != null && !parent.toFile().exists()) {
                createDirectoryWith755(parent);
            }

            try {
                Files.createDirectory(path);
                Files.setPosixFilePermissions(path, PERMISSION_755);
            } catch (FileAlreadyExistsException fileAlreadyExistsException) {
                // Catch the FileAlreadyExistsException here to avoid create the same parent directory in parallel
                log.debug("The directory: {} already exists", path);
            }

        }
    }

    public static void setFileTo755(File file) throws IOException {
        if (OSUtils.isWindows()) {
            return;
        }
        if (file.isFile()) {
            Files.setPosixFilePermissions(file.toPath(), PERMISSION_755);
            return;
        }
        Files.setPosixFilePermissions(file.toPath(), PERMISSION_755);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                setFileTo755(f);
            }
        }
    }

    public static void copyInputStreamToFile(MultipartFile file, String destFilename) {
        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(file.getInputStream(), new File(destFilename));
        } catch (IOException e) {
            log.error("failed to copy file , {} is empty file", file.getOriginalFilename(), e);
        }
    }

    public static Resource file2Resource(String filename) throws MalformedURLException {
        Path file = Paths.get(filename);

        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            log.error("File can not be read, fileName:{}", filename);
        }
        return null;
    }
}
