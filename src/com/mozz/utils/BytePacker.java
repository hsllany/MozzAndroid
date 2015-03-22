package com.mozz.utils;

import java.nio.ByteBuffer;

public class BytePacker {
	public final static byte[] smallFloatToByte(float floatNum) {

		if (floatNum > -128.254 && floatNum < 128.254) {
			byte[] floatByte = new byte[2];
			byte[] floatRawByte = ByteBuffer.allocate(4).putFloat(floatNum)
					.array();
		}

		return null;
	}
}
