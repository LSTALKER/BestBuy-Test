package com.bestbuy.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectStreamUtilities<T> {
	public void writeObject(T object, String filename) {
		ObjectOutputStream out = null;

		try {
			out = new ObjectOutputStream(new FileOutputStream(filename));
			out.writeObject(object);
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(out != null) {

				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public T readObject(String fileName) {
		File f = new File(fileName);

		if(f.exists()) {
			ObjectInputStream in = null;

			try {
				in = new ObjectInputStream(new FileInputStream(fileName));
				return (T)in.readObject();
			} catch(Exception e) {
				throw new RuntimeException(e);
			} finally {
				if(in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			return null;
		}
	}
}
