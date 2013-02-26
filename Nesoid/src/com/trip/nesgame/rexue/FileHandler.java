package com.trip.nesgame.rexue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

public class FileHandler {
	public static String copyFile2Rom(MainActivity context, String file_name) {
		String str_dir =  "/data/data/" + context.getPackageName() + "/roms/";
	    File localFile1 = new File(str_dir);
	    if (!localFile1.exists()){
	    	localFile1.mkdirs();
	    }
	    str_dir += file_name;
	    File rom_file = new File(str_dir);
		try {
			InputStream in_stream = context.getAssets().open(file_name, AssetManager.ACCESS_STREAMING);
			FileOutputStream out_stream = new FileOutputStream(rom_file);

			final int MAX_BYTE = 60000000;
			long total_byte = 0; // 接受流的容量
			int times = 0; // 流需要分开的数量
			int left_byte = 0; // 文件剩下的字符数
			byte[] inOutb; // byte数组接受文件的数据
			// 通过available方法取得流的最大字符数
			total_byte = in_stream.available();
			// 取得流文件需要分开的数量
			times = (int) Math.floor(total_byte / MAX_BYTE);
			// 分开文件之后,剩余的数量
			left_byte = (int) total_byte % MAX_BYTE;
			
			// 文件的容量大于60Mb时进入循环{
			for (int i = 0; i < times; ++i) {
				inOutb = new byte[MAX_BYTE];
					// 读入流,保存在byte数组
				in_stream.read(inOutb, 0, MAX_BYTE);
				out_stream.write(inOutb); // 写出流
				out_stream.flush(); // 更新写出的结果
			}
			// 写出剩下的流数据
			inOutb = new byte[left_byte];
			in_stream.read(inOutb, 0, left_byte);
			out_stream.write(inOutb);
			out_stream.flush();
			in_stream.close();
			out_stream.close();
			
		} catch (IOException localIOException) {
			Toast.makeText(context, "加载rom出错", 0).show();
			return "";
		}
		return str_dir;
	}
}