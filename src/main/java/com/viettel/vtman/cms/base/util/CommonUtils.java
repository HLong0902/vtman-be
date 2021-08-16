package com.viettel.vtman.cms.base.util;

import net.sf.jxls.transformer.Workbook;
import net.sf.jxls.transformer.XLSTransformer;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CommonUtils {
    public CommonUtils() {
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        return new Partition(list, size);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    /**
     * Lay gia tri tu file config.properties.
     *
     * @param key Khoa
     * @return Gia tri
     */
    public static String getConfig(String key) {
        ResourceBundle rb = ResourceBundle.getBundle("config");
        return rb.getString(key);
    }


   /* public static InputStream getInputStreamByFileName(HttpServletRequest req, String fileName) {
        try {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(fileName) && fileName.contains(".")) {
                String templatePath = com.viettel.utils.FileUtils.getSafePath(CommonUtils.getConfig("folder.export.template") + File.separator + fileName);
                InputStream inputStream = new FileInputStream(com.viettel.utils.FileUtils.getSafePath(req.getRealPath(templatePath)));
                return inputStream;
            } else {
                return null;
            }
        } catch (IOException ioE) {
            return null;
        }
    }*/
   /* public static ResponseEntity<InputStreamResource> exportExcel(HttpServletRequest req, List<?> listData, String templateName, String fileName, Logger LOGGER){
        ByteArrayOutputStream byteArrayOutputStream = null;
        InputStream is = null;
        FileOutputStream fos = null;
        InputStreamResource resource = null;
        File file = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            Map beans = new HashMap();
            beans.put("lstData", listData);
            is = CommonUtils.getInputStreamByFileName(req, templateName);
            XLSTransformer transformer = new XLSTransformer();
            Workbook rsWorkbook = transformer.transformXLS(is, beans);
            rsWorkbook.write(byteArrayOutputStream);
            try {
                String fullPathFile = CommonUtils.getFullPathFileNameReport(fileName);
                fos = new FileOutputStream(fullPathFile);
                fos.write(byteArrayOutputStream.toByteArray());
                file = new File(fullPathFile);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }finally {
                if (fos != null) fos.close();
            }
            byte[] data = FileUtils.readFileToByteArray(file);
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InvalidFormatException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (is != null) is.close();
                if (byteArrayOutputStream != null) byteArrayOutputStream.close();
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

        }
        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file == null ? "" :file.getName())
                // Content-Type
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                // Contet-Length
                .contentLength(file.length()) //
                .body(resource);
    }*/
}
