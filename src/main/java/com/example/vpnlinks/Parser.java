package com.example.vpnlinks;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import org.json.JSONObject;
import org.json.JSONException;

public class Parser {

	public static String decrypt(String encryptedString, String key) throws Exception {
		String path = "/path/120306182525";
		Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
		byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
		SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
		byte[] bytes2 = key.getBytes(StandardCharsets.UTF_8);
		instance.init(2, secretKeySpec, new IvParameterSpec(bytes2));
		byte[] doFinal = instance.doFinal(Base64.getDecoder().decode(encryptedString));

		String url = new String(doFinal, StandardCharsets.UTF_8).split("://")[1].trim();

	 	byte [] base64Data = Base64.getDecoder().decode(url);
		String str = new String(base64Data, StandardCharsets.UTF_8);
		JSONObject jsonObject = new JSONObject(str);

		jsonObject.put("path", path);

		url = jsonObject.toString();
		base64Data = Base64.getEncoder().encode(url.getBytes());
		str = new String(base64Data, StandardCharsets.UTF_8);
		url = "vmess://" + str;

		return url;
    }

	public static void setClipboardString(String text) {
	// 获取系统剪贴板
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 封装文本内容
		Transferable trans = new StringSelection(text);
		// 把文本内容设置到系统剪贴板
		clipboard.setContents(trans, null);
	}

	public static void write(String text, String path){
		try{
			File file =new File(path);

			if(!file.exists()){
				file.createNewFile();
			}

//			FileWriter fileWriter = new FileWriter(file.getName());
			OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file,true), StandardCharsets.UTF_8);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.write(text);
			bufferWriter.close();

		 	System.out.println("Done");

		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static String read() throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get("output.txt"));
		return new String(encoded, StandardCharsets.UTF_8);
	}


   /**
     * 生成发送二维码方法
     *
     * @param text     二维码生成规则(二维码可以是任何英文字母加数字生成的二维码)
     * @param width    宽度
     * @param height   高度
     * @param filePath 输出图片地址
     */
    public static String getQRCodeImage(String text, int width, int height, String filePath, Long name) throws WriterException, IOException {
        //生成二维码类
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //生成的二维码
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        //生成图片唯一名称,加上.png格式
        String pat = name + ".png";
        //图片路劲加上图片名称  (输出地址)
        filePath += pat;
        Path path = FileSystems.getDefault().getPath(filePath);
        //输出二维码图片
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
		return filePath;
    }

	public static void main(String[] args) throws Exception {
//		getQRCodeImage("vmess://eyJhZGQiOiIxMzcuMTc1LjQwLjE3MiIsImFpZCI6IjY0IiwiaG9zdCI6Ind3dy40NTA0MzgzNC54eXoiLCJpZCI6IjQxODA0OGFmLWEyOTMtNGI5OS05YjBjLTk4Y2EzNTgwZGQyNCIsIm5ldCI6IndzIiwicGF0aCI6IlwvZm9vdGVycyIsInBvcnQiOjQ0MywicHMiOjYyNCwidGxzIjoidGxzIiwidHlwZSI6ImR0bHMiLCJ2IjoiMiJ9\n",300,300,"Image\\", System.currentTimeMillis());
		String s = decrypt("mq4s/zhZQcd3VPf8qNKVJJ1LroQzqWyHVom4mZej4mY9yldR02SpRJh1SyQcRa2DSQkxypaNamouTjZ3D5vg/3Q3HAAMdwaf943eSmsconiNhZ6btlOqbsSv4HGSMOB23K0EEzNCTWpvaqojC85p/KsfEzXTpoIWXm2GdGB1UUicctVHvJWZNNzyUR9iUG0vprgFEru46k8/1W0W/SGT7srOLpuvNUdt6m85ef67vN8iWJmVpX/uSGqkNsY/FPxdJSaENXZzbHxoW1abGAIfrQM9MLS3uTUmY6NnJGtonI0n8/cIOYT20W5XXbbAeaNMFr0ATt7M1ByY0cBXcUV6guBzQ4lcGMQ9SKzpokTq8Jc=", "ks9KUrbWJj46AftX");
		System.out.println(s);
//		String s = "eyJhZGQiOiAiMTQyLjQuMTA0LjIyOSIsICJhaWQiOiAiNjQiLCAiaG9zdCI6ICJ3d3cuNjk5NjE1NzMueHl6IiwgImlkIjogIjQxODA0OGFmLWEyOTMtNGI5OS05YjBjLTk4Y2EzNTgwZGQyNCIsICJuZXQiOiAid3MiLCAicGF0aCI6ICIvcGF0aC8xMjAzMDYxODI1MjUiLCAicG9ydCI6IDQ0MywgInBzIjogNDQ2LCAidGxzIjogInRscyIsICJ0eXBlIjogImR0bHMiLCAidiI6ICIyIn0=";
//		 byte [] base64Data = Base64.getDecoder().decode(s);
//		String str = new String(base64Data, StandardCharsets.UTF_8);
//		JSONObject jsonObject = new JSONObject(str);
//		jsonObject.put("path", )
	}

	public static void log(String s){
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		System.out.println(formatter.format(date));
		write(formatter.format(date) + "---" +s + "\n", "log.txt");
	}

}