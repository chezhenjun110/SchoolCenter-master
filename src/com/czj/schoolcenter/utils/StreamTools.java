package com.czj.schoolcenter.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by czj on 2016/3/14.
 */
public class StreamTools {

	/**
	 * 
	 *
	 * @param is
	 * @return
	 */
	public static String streamToStr(InputStream is) {
		try {

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			int len = 0;

			byte buffer[] = new byte[1024];

			while ((len = is.read(buffer)) != -1) {

				os.write(buffer, 0, len);
			}

			is.close();
			os.close();

			byte data[] = os.toByteArray();

			return new String(data, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
