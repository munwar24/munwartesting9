package com.ii.mobile.payload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

public enum Compress {
	INSTANCE;

	public byte[] compress(String string) throws IOException {
		// string = Dictionary.INSTANCE.substitute(string);
		// L.out("compressed: " + string);
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes());
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}

	public String decompress(byte[] compressed) throws IOException {
		final int BUFFER_SIZE = 32;
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			string.append(new String(data, 0, bytesRead));
		}
		gis.close();
		is.close();
		return string.toString();
	}

	public String compressString(String string, boolean unitTest, boolean printout) {
		String before = string;
		if (printout)
			L.out("\n\nStep 1: Generate test package: " + before.length());
		if (printout)
			PrettyPrint.prettyPrint(before);
		if (printout)
			L.out("\n\nStep 2: Generate dictionary: ");
		Dictionary.INSTANCE.getDictionary();
		if (printout)
			L.out("\n\nStep 3: Pack String: \n" + before + "\n size: " + before.length());
		if (printout)
			L.out("\n\nStep 4: Substitute Compress:");
		string = Dictionary.INSTANCE.substitute(string);
		if (printout) {
			L.out("\n\n    Substitute compressed: \n" + string + "\n size: " + string.length());
			L.out("\n\n    Bytes size change from: " + before.length() + " -> " + string.length());
			L.out("\n\n    Compression ratio: " + ((float) string.length()) / before.length());
			L.out("\n\n    Result: " + (int) ((float) before.length() / string.length() + .5) + " times!");
		}
		String after = string;
		if (!unitTest)
			return string;
		try {
			L.out("\n\nStep 5: Gzip Compress:");
			byte[] gzipped = compress(string);
			L.out("\n\n    Gzip compressed: \n" + "<unprintable>" + "\n size: " + gzipped.length);
			L.out("\n\n    Bytes size change from: " + string.length() + " -> " + gzipped.length);
			L.out("\n\n    Compression ratio: " + ((float) gzipped.length / string.length()));
			L.out("\n\n    Result: " + (int) ((float) string.length() / gzipped.length + .5) + " times!");

		} catch (Exception e) {
			L.out("error: " + e);
		}
		if (printout)
			L.out("\n\nStep 6: Substitute Uncompress:");
		after = Dictionary.INSTANCE.unSubstitute(after);
		if (printout) {
			L.out("\n\n    Substitute uncompressed: \n" + after + "\n size: " + after.length());
			L.out("\n\nStep 7: Show test package:");
			PrettyPrint.prettyPrint(after);
		}
		// L.out("\n\nBefore reference compressed: \n" + before + "\n size: " +
		// before.length());
		// Role.prettyPrint(before);
		return string;
	}
}