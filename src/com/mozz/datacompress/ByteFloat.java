package com.mozz.datacompress;

import java.nio.ByteBuffer;

/**
 * In our dairy world, there are many small floats such as temperature, weight
 * of man, speed of cars etc. These floats are not too big, also they don't need
 * high accuracy. This class is try to convert those small 32-bit float to
 * simply just 2 bytes, which will improve the performance during web
 * transformation.
 * 
 * @author Yang Tao <hsllany@163.com>
 * @version 1.0
 */
public final class ByteFloat {

	/**
	 * the bytes used for float
	 */
	private byte[] mByte;

	/**
	 * the float value
	 */
	private float mFloatValue;

	/**
	 * encode a float to bytes;
	 * 
	 * @param floatValue
	 */
	public ByteFloat(float floatValue) {
		mFloatValue = floatValue;

		if (mFloatValue >= 0) {
			int integer = (int) Math.floor(mFloatValue);
			int decimal = (int) Math.round((mFloatValue - integer) * 100);
			if (integer <= 127) {
				mByte = new byte[2];
				mByte[0] = (byte) (integer & 0xff);
				mByte[1] = (byte) (decimal & 0xff);
			}
		} else {
			int integer = (int) Math.ceil(mFloatValue);
			int decimal = (int) Math.round((-mFloatValue + integer) * 100);
			if (-integer <= 127) {
				mByte = new byte[2];
				mByte[0] = (byte) ((-integer) & 0xff);

				byte negativeByte = (byte) (1 << 7);
				mByte[0] = (byte) (mByte[0] | negativeByte);

				mByte[1] = (byte) ((decimal) & 0xff);
			}

		}
	}

	/**
	 * for decode bytes into float
	 * 
	 * @param bytes
	 */
	public ByteFloat(byte[] bytes) {
		if (bytes.length == 2) {
			byte negative = (byte) (bytes[0] & (1 << 7));

			if (negative != 0) {
				// is negative
				int integer = bytes[0] & 0x7f;
				float decimal = bytes[1] / 100f;

				mFloatValue = -integer - decimal;
			} else {
				int integer = bytes[0];
				float decimal = bytes[1] / 100f;
				mFloatValue = integer + decimal;
			}

		} else if (bytes.length == 4) {
			int intValue = (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8))
					| (0xff0000 & (bytes[2] << 16))
					| (0xff000000 & (bytes[3] << 24));

			mFloatValue = Float.intBitsToFloat(intValue);
		} else {
			throw new IllegalArgumentException(
					"the length of byte array must be either 2 or 4");
		}
	}

	/**
	 * 
	 * @return float value
	 */
	public float floatValue() {
		return mFloatValue;
	}

	/**
	 * 
	 * @return Binary code as {@code String}
	 */
	public String toBinaryString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mByte.length; i++) {
			sb.append(byteToBinary(mByte[i]));
		}

		return sb.toString();
	}

	/**
	 * get bytes representation
	 * 
	 * @return
	 */
	public byte[] toBytes() {
		if (mByte != null) {
			return mByte;
		}
		return ByteBuffer.allocate(4).putFloat(mFloatValue).array();
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
