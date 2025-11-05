package com.example.btljavafx.services;

import com.example.btljavafx.utils.dao.SettingsDAO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetVietQR {
    public static SettingsDAO settings = new SettingsDAO();
    public static String bankId = settings.get("PAYMENT_BANK");
    public static String accountNo = settings.get("PAYMENT_ACCOUNT");
    public static String template = "compact2";
    public static String accountName = "";

    public static File generate(Double total, String description) {
        String urlString = String.format(
                "https://img.vietqr.io/image/%s-%s-%s.png?amount=%s&addInfo=%s&accountName=%s",
                bankId, accountNo, template, total.toString(),
                java.net.URLEncoder.encode(description, java.nio.charset.StandardCharsets.UTF_8),
                java.net.URLEncoder.encode(accountName, java.nio.charset.StandardCharsets.UTF_8)
        );

        System.out.println("🔗 URL gọi tới: " + urlString); // <--- thêm dòng này để debug

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType(); // <--- thêm dòng này
            System.out.println("📡 HTTP " + responseCode + " | Content-Type: " + contentType);

            if (responseCode == 200) {
                BufferedImage image = ImageIO.read(connection.getInputStream());
                if (image == null) {
                    System.out.println("❌ Lỗi: Server trả về không phải ảnh hợp lệ (image == null)");
                    return null;
                }

                File outFile = new File("src/main/resources/com/example/btljavafx/view/assets/vietqr.png");
                outFile.getParentFile().mkdirs();
                ImageIO.write(image, "png", outFile);
                System.out.println("✅ Ảnh QR đã được tải và lưu thành công!");
                return outFile;
            } else {
                System.out.println("❌ Lỗi khi tải ảnh QR: HTTP " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        generate(100.0, "HIHIIHI");
//    }
}
