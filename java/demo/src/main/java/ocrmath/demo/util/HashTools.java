package ocrmath.demo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Hash算法（消息摘要算法）工具类
public class HashTools {
	private static MessageDigest digest; //消息摘要对象
    //私有化的构造方法
	private HashTools() {
	}

	public static String getAppSign(String appKey,String appSecret,Long timestamp) throws NoSuchAlgorithmException {
		return digestByMD5(appKey + appSecret + timestamp);
	}

    //按照MD5进行消息摘要计算（哈希计算）
	public static String digestByMD5(String source) throws NoSuchAlgorithmException {
		digest= MessageDigest.getInstance("MD5");
		return handler(source);
	}

    //通过消息再要对象，处理加密内容
	public static String handler(String souce) {
		digest.update(souce.getBytes()); //调用update()输入数据
		byte[] bytes=digest.digest();
		String ret=bytesToHex(bytes);
		return ret;
	}
    //将字节数组转换为十六进制字符串
	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb=new StringBuilder();
		for(byte b:bytes) {
			sb.append(String.format("%02x", b)); //将字节值转换为2为十六进制字符串
		}
		return sb.toString();
	}
}