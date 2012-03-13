package org.springframework.roo.support.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Simple utility methods for file and stream copying. All copy methods use a block size of 4096 bytes, and close all affected streams when done.
 *
 * <p>
 * Mainly for use within the framework, but also useful for application code.
 *
 * @author Juergen Hoeller
 * @since 1.0
 */
public final class FileCopyUtils {
	
	public static final int BUFFER_SIZE = 4096;

	// ---------------------------------------------------------------------
	// Copy methods for java.io.File
	// ---------------------------------------------------------------------

	/**
	 * Copy the contents of the given input File to the given output File.
	 *
	 * @param in the file to copy from
	 * @param out the file to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(final File in, final File out) throws IOException {
		Assert.notNull(in, "No input File specified");
		Assert.notNull(out, "No output File specified");
		return copy(new BufferedInputStream(new FileInputStream(in)), new BufferedOutputStream(new FileOutputStream(out)));
	}

	/**
	 * Copy the contents of the given byte array to the given output File.
	 *
	 * @param bytes the byte array to copy from
	 * @param file the file to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy(final byte[] bytes, final File file) throws IOException {
		Assert.notNull(bytes, "No input byte array specified");
		Assert.notNull(file, "No output File specified");
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		copy(in, out);
	}

	/**
	 * Copy the contents of the given input File into a new byte array.
	 *
	 * @param in the file to copy from
	 * @return the new byte array that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static byte[] copyToByteArray(final File in) throws IOException {
		Assert.notNull(in, "No input File specified");
		return copyToByteArray(new BufferedInputStream(new FileInputStream(in)));
	}

	// ---------------------------------------------------------------------
	// Copy methods for java.io.InputStream / java.io.OutputStream
	// ---------------------------------------------------------------------

	/**
	 * Copy the contents of the given InputStream to the given OutputStream. Closes both streams when done.
	 *
	 * @param in the stream to copy from
	 * @param out the stream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(InputStream in, OutputStream out) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		Assert.notNull(out, "No OutputStream specified");
		if (!(in instanceof BufferedInputStream)) {
			in = new BufferedInputStream(in);
		}
		if (!(out instanceof BufferedOutputStream)) {
			out = new BufferedOutputStream(out);
		}
		try {
			int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		} finally {
			IOUtils.closeQuietly(in, out);
		}
	}

	/**
	 * Copy the contents of the given byte array to the given OutputStream. Closes the stream when done.
	 *
	 * @param bytes the byte array to copy from
	 * @param out the OutputStream to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy(final byte[] bytes, final OutputStream out) throws IOException {
		Assert.notNull(bytes, "No input byte array specified");
		Assert.notNull(out, "No OutputStream specified");
		try {
			out.write(bytes);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * Copy the contents of the given InputStream into a new byte array. Closes the stream when done.
	 *
	 * @param in the stream to copy from
	 * @return the new byte array that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static byte[] copyToByteArray(final InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		copy(in, out);
		return out.toByteArray();
	}

	// ---------------------------------------------------------------------
	// Copy methods for java.io.Reader / java.io.Writer
	// ---------------------------------------------------------------------

	/**
	 * Copy the contents of the given Reader to the given Writer. Closes both when done.
	 *
	 * @param in the Reader to copy from
	 * @param out the Writer to copy to
	 * @return the number of characters copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(Reader in, Writer out) throws IOException {
		Assert.notNull(in, "No Reader specified");
		Assert.notNull(out, "No Writer specified");
		if (!(in instanceof BufferedReader)) {
			in = new BufferedReader(in);
		}
		if (!(out instanceof BufferedWriter)) {
			out = new BufferedWriter(out);
		}
		try {
			int byteCount = 0;
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		} finally {
			IOUtils.closeQuietly(in, out);
		}
	}

	/**
	 * Copy the contents of the given String to the given output Writer. Closes the writer when done.
	 *
	 * @param in the String to copy from
	 * @param out the Writer to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy(final String in, Writer out) throws IOException {
		Assert.notNull(in, "No input String specified");
		Assert.notNull(out, "No Writer specified");
		if (!(out instanceof BufferedWriter)) {
			out = new BufferedWriter(out);
		}
		try {
			out.write(in);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * Copy the contents of the given Reader into a String. Closes the reader when done.
	 *
	 * @param in the reader to copy from
	 * @return the String that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static String copyToString(final Reader in) throws IOException {
		StringWriter out = new StringWriter();
		copy(in, out);
		return out.toString();
	}

	/**
	 * Returns the contents of the given File as a String.
	 * <p>
	 * Consider using {@link FileUtils#read(File)} instead if any
	 * {@link IOException}s would be unrecoverable.
	 *
	 * @param file the file to read from
	 * @return the contents
	 * @throws IOException in case of I/O errors
	 */
	public static String copyToString(final File file) throws IOException {
		return copyToString(new BufferedReader(new FileReader(file)));
	}
	
	/**
	 * Constructor is private to prevent instantiation
	 */
	private FileCopyUtils() {}
}
