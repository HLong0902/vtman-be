package com.viettel.vtman.cms.utils;

import com.google.common.io.ByteStreams;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Common {

    private static final Logger LOGGER = LogManager.getLogger(Common.class);

    private static void deleteFile(String fileName, String absolutePath) {
        File file = new File(absolutePath + "/" + fileName);
        if (file.exists()) {
            if (file.delete()) {
                LOGGER.info(String.format("Đã xóa tệp tin thành công [%s]", fileName));
            } else {
                LOGGER.error(String.format("Xóa tệp tin thất bại [%s]", fileName));
            }
        }
    }

    private static String processFile(MultipartFile file,
                                      String absolutePath,
                                      String username)
            throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_");
        Calendar calendar = Calendar.getInstance();
        File uploadRootDir = new File(absolutePath);
        if (!uploadRootDir.exists()) uploadRootDir.mkdirs();
        String user = username != null ? username + "_" : "";
        String fileName = (simpleDateFormat
                .format(calendar.getTime()).toLowerCase() + user + file.getOriginalFilename())
                .replaceAll("\\s", "_");
        File iofile = new File(absolutePath, fileName);
        file.transferTo(iofile);
        return fileName;
    }

    public static String uploadFile(MultipartFile file,
                                    String folder,
                                    String username,
                                    String fileName,
                                    ApplicationConfigurationProp configurationProp) {
        try {
            if (file == null) return "";
            else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
                Calendar calendar = Calendar.getInstance();
                String absolutePath;
                if (configurationProp.getEnvironmentUploadFile().equalsIgnoreCase("LINUX")) {
                    absolutePath = configurationProp.getPathUploadFileExtention() + "/"
                            + folder + dateFormat.format(calendar.getTime()) + "/";
                } else {
                    absolutePath = System.getProperty("user.dir") + configurationProp.getPathUpload()
                            + folder + dateFormat.format(calendar.getTime()) + "/";
                }
                if (fileName != null)
                    deleteFile(fileName, absolutePath);
                return absolutePath + processFile(file, absolutePath, username);
            }
        } catch (Exception e) {
            LOGGER.error("UPLOAD FILE ERROR: " + e.getMessage());
            return "";
        }
    }

    public static String escapeStringForMySQL(String s) {
        return s.replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\b","\\b")
                .replaceAll("\n","\\n")
                .replaceAll("\r", "\\r")
                .replaceAll("\t", "\\t")
                .replaceAll("\\x1A", "\\Z")
                .replaceAll("\\x00", "\\0")
                .replaceAll("'", "\\'")
                .replaceAll("\"", "\\\"")
                .replaceAll("%", "\\\\%");
    }

    public static String escapeWildcardsForMySQL(String s) {
        return escapeStringForMySQL(s)
                .replaceAll("%", "\\%")
                .replaceAll("_","\\_");
    }


    public static Claims authenticate(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            try {
                Claims jwtPayload = Jwts.parser()
                        .setSigningKey(readPublicKey())
                        .parseClaimsJws(token.substring(7))
                        .getBody();

                if (Objects.nonNull(jwtPayload) && Objects.nonNull(jwtPayload.get("exp"))) {
                    if (jwtPayload.getExpiration().after(new Date())) {
                        return jwtPayload;
                    }
                }
                if (jwtPayload != null) {
                    return jwtPayload;
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                return null;
            }
        }
        return null;
    }

    public static RSAPublicKey readPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] bytes = ByteStreams.toByteArray(new ClassPathResource("sso_public_key/ssopublic.pem").getInputStream());
        String key = new String(bytes, Charset.defaultCharset());

        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static Date toBeginOfDate(Date date) {
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(date);
        calFrom.set(Calendar.MILLISECOND, 0);
        calFrom.set(Calendar.SECOND, 0);
        calFrom.set(Calendar.MINUTE, 0);
        calFrom.set(Calendar.HOUR_OF_DAY, 0);

        return calFrom.getTime();
    }

    public static Date toEndOfDate(Date date) {
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(date);
        calTo.set(Calendar.MILLISECOND, 999);
        calTo.set(Calendar.SECOND, 59);
        calTo.set(Calendar.MINUTE, 59);
        calTo.set(Calendar.HOUR_OF_DAY, 23);

        return calTo.getTime();
    }
}
