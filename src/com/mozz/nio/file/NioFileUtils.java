package com.mozz.nio.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

	/**
	 * create new file on sdcard, will block if the file is locked by another
	 * process
	 * 
	 * @param path
	 *            , folders path
	 * @param fileNameWithSuffix
	 * @return file, otherwise null
	 * @throws IOException
	 */
	public static final File newFileOnSD(String path, String fileNameWithSuffix)
			throws IOException {
		if (SDCard.isSDCardMounted()) {
			File dir = new File(SDCard.sdCardDir() + File.separator + path);
			if (!dir.exists()) {
				dir.mkdirs();

			}

			File file = new File(SDCard.sdCardDir() + File.separator + path
					+ File.separator + fileNameWithSuffix);
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		} else {
			return null;
		}
	}

	/**
	 * write string to files, will block if the file is locked by another
	 * process
	 * 
	 * @param toWrite
	 *            , String to write
	 * @param toFile
	 *            , destination file to writein
	 * @param append
	 *            , if true, then bytes will be written to the end of the file
	 *            rather than the beginning
	 * @throws FileNotFoundException
	 */
	public static final void writeString(String toWrite, File toFile,
			boolean append) throws FileNotFoundException {
		FileOutputStream toOut = new FileOutputStream(toFile, append);

		FileChannel toChannel = toOut.getChannel();

		FileLock fileLock = null;
		try {
			fileLock = toChannel.lock();

			ByteBuffer byteBuffer = ByteBuffer.wrap(toWrite.getBytes());

			if (append) {
				toChannel.position(toChannel.size());
			}

			while (toChannel.write(byteBuffer) > 0) {
			}

			toChannel.force(true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileLock != null)
					fileLock.release();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					toChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						toOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static final void copy(String fromFile, String toFile)
			throws FileNotFoundException {
		copy(new File(fromFile), new File(toFile));
	}

	public static final void copy(File fromFile, File toFile)
			throws FileNotFoundException {
		copy(new FileInputStream(fromFile), toFile);
	}

	public static final void copy(FileInputStream fromFile, File toFile)
			throws FileNotFoundException {

		FileChannel fromChannel = fromFile.getChannel();

		FileOutputStream toIn = new FileOutputStream(toFile);
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
