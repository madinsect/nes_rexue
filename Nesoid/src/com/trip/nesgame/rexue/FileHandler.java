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
			long total_byte = 0; // ������������
			int times = 0; // ����Ҫ�ֿ�������
			int left_byte = 0; // �ļ�ʣ�µ��ַ���
			byte[] inOutb; // byte��������ļ�������
			// ͨ��available����ȡ����������ַ���
			total_byte = in_stream.available();
			// ȡ�����ļ���Ҫ�ֿ�������
			times = (int) Math.floor(total_byte / MAX_BYTE);
			// �ֿ��ļ�֮��,ʣ�������
			left_byte = (int) total_byte % MAX_BYTE;
			
			// �ļ�����������60Mbʱ����ѭ��{
			for (int i = 0; i < times; ++i) {
				inOutb = new byte[MAX_BYTE];
					// ������,������byte����
				in_stream.read(inOutb, 0, MAX_BYTE);
				out_stream.write(inOutb); // д����
				out_stream.flush(); // ����д���Ľ��
			}
			// д��ʣ�µ�������
			inOutb = new byte[left_byte];
			in_stream.read(inOutb, 0, left_byte);
			out_stream.write(inOutb);
			out_stream.flush();
			in_stream.close();
			out_stream.close();
			
		} catch (IOException localIOException) {
			Toast.makeText(context, "����rom����", 0).show();
			return "";
		}
		return str_dir;
	}
}