package com.mozz.nio.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.mozz.utils.SDCard;

/**
 * NIO file utils
 * 
 * @author yang tao
 * 
 */
public class NioFileUtils {

	public static final File newFileOnSD(String path, String fileNameWithSuffix)
			throws IOException {
		File file = new File(SDCard.sdCardDir() + File.pathSeparator + "path"
				+ File.pathSeparator + fileNameWithSuffix);
		file.createNewFile();
		return file;
	}

	public static final void writeString(String toWrite, File toFile)
			throws FileNotFoundException {
		FileInputStream toIn = new FileInputStream(toFile);
		FileChannel toChannel = toIn.getChannel();

		FileLock fileLock = null;
		try {
			fileLock = toChannel.lock();

			ByteBuffer byteBuffer = ByteBuffer.wrap(toWrite.getBytes());
			toChannel.position(toChannel.size());

			while (toChannel.write(byteBuffer) > 0) {
			}

			toChannel.force(true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileLock.release();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				toChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					toIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static final void copy(File fromFile, File toFile)
			throws FileNotFoundException {
		copy(new FileOutputStream(fromFile), toFile);
	}

	public static final void copy(FileOutputStream fromFile, File toFile)
			throws FileNotFoundException {

		FileChannel fromChannel = fromFile.getChannel();

		FileInputStream toIn = new FileInputStream(toFile);
		FileChannel toChannel = toIn.getChannel();

		FileLock lock = null;
		try {
			lock = toChannel.lock();

			fromChannel.transferTo(0, fromChannel.size(), toChannel);

			/**
			 * while (true) { readBuffer.clear(); if
			 * (fromChannel.read(readBuffer) < 0) break;
			 * 
			 * readBuffer.flip(); // make sure that channel writes all buffers
			 * while (toChannel.write(readBuffer) > 0) { } }
			 */

			toChannel.force(true);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (lock != null)
					lock.release();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					fromChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						toChannel.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							toIn.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}
