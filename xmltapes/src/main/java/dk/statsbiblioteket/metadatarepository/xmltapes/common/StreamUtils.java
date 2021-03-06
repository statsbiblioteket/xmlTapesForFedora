package dk.statsbiblioteket.metadatarepository.xmltapes.common;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.CountingOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static void copy(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[10*1024*1024]; // 10MB
            // As the outputstream is sync'ed with the storage medium, a big buffer speeds up the copy A LOT
            IOUtils.copyLarge(in, out,buffer);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Copy a file to an outputstream. The destination will be closed
     * @param file the file to read
     * @param destination the outputstream to write to
     * @throws java.io.IOException
     */
    public static void copy(File file, OutputStream destination) throws IOException {
        final InputStream input = new FileInputStream(file);
        copy(input,destination);
    }

    /**
     * Compress and copy the file to the destination
     * @param file the file to compress and copy
     * @param destination the outputstream to write to
     * @throws java.io.IOException
     */
    public static void compress(File file, OutputStream destination) throws IOException {
        GzipCompressorOutputStream compressorOutputStream = new GzipCompressorOutputStream(destination);
        copy(file,compressorOutputStream);
    }

    /**
     * Compress the file and count the number of bytes of the compressed data
     * @param file the file to count
     * @return the number of bytes in the file after compression
     * @throws java.io.IOException
     */
    public static long compressAndCountBytes(File file) throws IOException {
        CountingOutputStream counter = new CountingOutputStream(new NullOutputStream());
        GzipCompressorOutputStream compressor = new GzipCompressorOutputStream(counter);
        final InputStream input = new FileInputStream(file);
        copy(input,compressor);
        return counter.getBytesWritten();
    }

    /**
     * Uncompress the file and count the number of bytes
     * @param file the file to count
     * @return the number of bytes in the file after uncompressing
     * @throws java.io.IOException
     */
    public static long uncompressAndCountBytes(File file) throws FileNotFoundException, IOException {
        CountingOutputStream counter = new CountingOutputStream(new NullOutputStream());
        final InputStream uncompressor = new GzipCompressorInputStream(new FileInputStream(file));
        copy(uncompressor,counter);
        return counter.getBytesWritten();
    }

    /**
     * Read the stream to the end, counting the bytes
     * @param stream the stream to read
     * @return the number of bytes read
     * @throws java.io.IOException
     */
    public static long countBytesDirect(InputStream stream) throws IOException {
        CountingOutputStream counter = new CountingOutputStream(new NullOutputStream());
        copy(stream,counter);
        return counter.getBytesWritten();
    }
}
