package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Utilities {
	public static int max(int ... nums) {
		int max = nums[0];
		for (int i = 1; i < nums.length; i++) {
			if (nums[i] > max) {
				max = nums[i];
			}
		}
		return max;
	}
	
	public static boolean isInteger(String str) {
		return str.matches("-?\\d+");
	}
	
	public static void compressGzipFile(File input, File output) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(input);
		FileOutputStream fos = new FileOutputStream(output);
		GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = fis.read(buffer)) != -1) {
			gzipOS.write(buffer, 0, len);
		}
		// close resources
		gzipOS.close();
		fos.close();
		fis.close();
	}
	
	public static void decompressGzipFile(File input, File output) throws IOException {
		FileInputStream fis = new FileInputStream(input);
		GZIPInputStream gis = new GZIPInputStream(fis);
		FileOutputStream fos = new FileOutputStream(output);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = gis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}
		// close resources
		fos.close();
		gis.close();
	}
}