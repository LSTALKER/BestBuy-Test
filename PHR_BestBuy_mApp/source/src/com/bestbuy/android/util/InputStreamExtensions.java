package com.bestbuy.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamExtensions {
	
	public static String InputStreamToString(InputStream is) throws IOException {
		byte[] buffer = inputStreamToByteArray(is);
		is.close();
		return new String(buffer);
	}

	public static void inputStreamToOutputStream(InputStream in, OutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		while (true) {
			int bytesRead = in.read(buffer, 0, 1024);
			if (bytesRead == -1)
				break;
			out.write(buffer, 0, bytesRead);
		}
		out.flush();
	}

	public static byte[] inputStreamToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		inputStreamToOutputStream(in, out);
		return out.toByteArray();
	}

	public static InputStream stringToInputStream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}

}
