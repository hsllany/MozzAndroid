package com.mozz.datacompress;

/**
 * In socket transfer process, a real Java boolean value takes up 1 byte to
 * store, which is too big for web transform. This class is due to replace
 * boolean value with the simple 1 or 0 bit.
 * 
 * @author Yang Tao <hsllany@163.com>
 * @version 1.0
 * 
 */
public final class ByteBoolean {
	/**
	 * the booleans using byte array
	 */
	private byte[] mByte;

	/**
	 * the names of each byte boolean
	 */
	private String[] mStatus;

	/**
	 * the size of mByte and mStatus
	 */
	private int mByteSize;

	/**
	 * 
	 */
	private int mStatusSize;

	/**
	 * constructor for encode boolean to bytes.
	 * 
	 * @param size
	 *            , how many bits to store the booleans. {@code int}
	 */
	public ByteBoolean(int size) {
		if (size > 0) {
			if (size % 8 == 0) {
				mByteSize = (size >> 3);
			} else {
				mByteSize = (size >> 3) + 1;
			}

			mStatusSize = mByteSize << 3;

			mByte = new byte[mByteSize];
			mStatus = new String[mStatusSize];
		} else {
			throw new IllegalArgumentException("size must be greater than 0");
		}
	}

	/**
	 * construct for decode
	 * 
	 * @param bytes
	 * @param status
	 */
	public ByteBoolean(byte[] bytes, String[] status) {
		if (status.length / bytes.length == 8
				&& status.length % bytes.length == 0) {
			mByte = bytes;
			mStatus = status;
			mByteSize = bytes.length;
			mStatusSize = status.length;
		} else {
			throw new IllegalArgumentException("can't match");
		}
	}

	public boolean getBoolean(int index) {
		if (index >= 0 && index < mStatusSize) {
			int idx = index >>> 3;
			int power = index % 8;

			byte leftByte = (byte) (1 << power);
			if (leftByte == -127) {

			}

			return (mByte[idx] & leftByte) != 0 ? true : false;
		} else {
			throw new IllegalArgumentException(
					"index must be greater or equal to 0, and less than size");
		}
	}

	public String getName(int index) {
		if (index >= 0 && index < mStatusSize) {
			return mStatus[index];
		} else {
			throw new IllegalArgumentException(
					"index must be greater or equal to 0, and less than size");
		}
	}

	public boolean getBoolean(String status, boolean defaultValue) {
		int i = 0;
		while (i < mStatusSize) {
			if (mStatus[i++].equals(status))
				break;
		}

		if (i != mStatusSize)
			return getBoolean(--i);
		else {
			return defaultValue;
		}
	}

	/**
	 * 
	 * @param index
	 * @param name
	 */
	public void setName(int index, String name) {
		if (index >= 0 && index < mStatusSize) {
			mStatus[index] = name;
		} else {
			throw new IllegalArgumentException(
					"index must be greater or equal to 0, and less than size");
		}

	}

	/**
	 * 
	 * @param index
	 * @param booleanValue
	 */
	public void setValue(int index, boolean booleanValue) {
		int idx = index >>> 3;
		int power = index % 8;

		byte leftByte = (byte) (1 << power);
		if (idx >= 0 && idx < mByteSize) {

			if (booleanValue) {
				mByte[idx] = (byte) (mByte[idx] | leftByte);
			} else {
				byte raw = mByte[idx];
				raw = (byte) ~raw;
				mByte[idx] = (byte) ~(raw | leftByte);
			}
		}
	}

	/**
	 * 
	 * @return {@code byte[]}
	 */
	public byte[] toByte() {
		byte[] returnByte = new byte[mByteSize];
		for (int i = 0; i < mByteSize; i++) {
			returnByte[i] = mByte[i];
		}

		return returnByte;
	}

	/**
	 * 
	 * @return Binary code as {@code String}
	 */
	public String toBinaryString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mByteSize; i++) {
			sb.append(byteToBinary(mByte[i]));
		}

		return sb.toString();
	}

	/**
	 * return the binary string of a single {@code byte}
	 * 
	 * @param byteValue
	 * @return binary string
	 */
	public static final String byteToBinary(byte byteValue) {
		int integer = toUnsignedInt(byteValue);
		StringBuilder sb = new StringBuilder();
		String binaryExp = Integer.toBinaryString(integer);

		for (int i = 0; i < 8 - binaryExp.length(); i++) {
			sb.append('0');
		}

		sb.append(binaryExp);

		return sb.toString();
	}

	/**
	 * Converts the argument to an {@code int} by an unsigned conversion. In an
	 * unsigned conversion to an {@code int}, the high-order 24 bits of the
	 * {@code int} are zero and the low-order 8 bits are equal to the bits of
	 * the {@code byte} argument.
	 * 
	 * Consequently, zero and positive {@code byte} values are mapped to a
	 * numerically equal {@code int} value and negative {@code byte} values are
	 * mapped to an {@code int} value equal to the input plus 2<sup>8</sup>.
	 * 
	 * @param x
	 *            the value to convert to an unsigned {@code int}
	 * @return the argument converted to {@code int} by an unsigned conversion
	 * @since 1.8
	 */
	public static int toUnsignedInt(byte x) {
		return ((int) x) & 0xff;
	}

}
