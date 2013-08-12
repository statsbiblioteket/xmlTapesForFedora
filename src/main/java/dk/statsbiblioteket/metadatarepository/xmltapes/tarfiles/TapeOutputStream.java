package dk.statsbiblioteket.metadatarepository.xmltapes.tarfiles;

import dk.statsbiblioteket.metadatarepository.xmltapes.common.TapeUtils;
import dk.statsbiblioteket.metadatarepository.xmltapes.common.index.Entry;
import dk.statsbiblioteket.metadatarepository.xmltapes.common.index.Index;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarHeader;
import org.kamranzafar.jtar.TarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a special outputstream. This class ensures that bytes written are buffered in memory, and only written to
 * the archive when the stream is closed. This is because the entry needs to know the length before writing.
 * When created, will lock the store for writing. Will unlock the store when closed. This is to ensure a that
 * the sequence of tapes is not disturbed.
 * When the stream is closed and have written the record, the index is updated with the new location.
 */
public class TapeOutputStream extends TarOutputStream {

    private static final Logger log = LoggerFactory.getLogger(TapeOutputStream.class);


    private static final int CAPACITY = 1 * 1024 * 1024;
    private Entry entry;
    private final URI id;
    private final Index index;
    private ReentrantLock writeLock;

    boolean closing = false;
    boolean closed = false;

    private LinkedList<ByteBuffer> buffer;

    private ByteBuffer lastBuffer;
    private TapeArchive tapeArchive;

    /**
     * Create a new TapeOutputStream
     * @param delegate the outputstream to delegate the writing to
     * @param entry the index entry
     * @param id the fedora ID
     * @param index the index instance
     * @param writeLock the writelock object
     * @param estimatedSize

     */
    public TapeOutputStream(OutputStream delegate,
                            Entry entry,
                            URI id,
                            Index index,
                            ReentrantLock writeLock,
                            long estimatedSize)  {
        super(delegate);
        this.writeLock = writeLock;

        log.trace("Opening an outputstream for the id {} and trying to acquire lock",id);

        this.entry = entry;
        this.id = id;
        this.index = index;
        buffer = new LinkedList<ByteBuffer>();
        buffer.add(ByteBuffer.allocate((int) Math.min(Math.max(estimatedSize, CAPACITY), Integer.MAX_VALUE)));
        lastBuffer = buffer.getLast();

        log.trace("Store write lock acquired for id {}",id);

    }




    protected void checkWriting(int len) throws IOException {
        if (closed){
            throw new IOException("Stream closed");
        }
        if (closing){
            return;
        }

        if (len > lastBuffer.remaining()){ //close this buffer and allocate a new one fitting the size min CAPACITY

            int size = (len > CAPACITY ? len : CAPACITY);
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            buffer.add(byteBuffer);
            lastBuffer = byteBuffer;
            log.debug("Allocating a new buffer of size {} for record {}",size,id);
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        checkWriting(1);
        if (closing){
            super.write(b);
            return;
        }
        lastBuffer.put((byte) b);
    }


    @Override
    public synchronized void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * Write a set of  bytes to the stream.
     * If the stream is open, put the bytes in the buffer, if nessesary allocate more buffers
     * If the stream is closing, write the bytes to super
     * if the stream is closed, throw IOException
     * @param b the byte to write
     * @throws IOException
     */
    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        checkWriting(len);
        if (closing){
            super.write(b, off, len);
            return;
        }
        lastBuffer.put(b,off,len);


    }

    @Override
    public  void close() throws IOException {
        log.trace("Closing the record {}",id);

        closing = true;//From now on, writes go the the delegate, not the buffer
        long timestamp = System.currentTimeMillis();
        ByteBuffer output = zipTheBuffer();

        long size = output.capacity();


        TarHeader tarHeader = TarHeader.createHeader(TapeUtils.toFilenameGZ(id),size,timestamp/1000,false);
        TarEntry entry = new TarEntry(tarHeader);
        putNextEntry(entry);
        IOUtils.write(output.array(),this);
        closeCurrentEntry();
        out.close();
        index.addLocation(id, this.entry); //Update the index to the newly written entry
        closed = true; //Now we cannot write anymore
        writeLock.unlock(); //unlock the storage system, we are done

    }

    private ByteBuffer zipTheBuffer() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GzipCompressorOutputStream zipped = new GzipCompressorOutputStream(output);
        for (ByteBuffer byteBuffer : buffer) {
            if (byteBuffer.hasArray()){
                IOUtils.copyLarge(new ByteArrayInputStream(byteBuffer.array(), 0, byteBuffer.position())
                        , zipped);
            } else {
                //TODO
                byte[] temp = new byte[4*1024];

                int remaining = byteBuffer.limit(byteBuffer.position()).rewind().remaining();
                while (remaining > 0){
                    int length = (remaining > temp.length ? remaining : temp.length);
                    byteBuffer.get(temp,0,length);
                    write(temp,0,length);
                    remaining = byteBuffer.remaining();
                }

            }
        }
        zipped.close();
        return ByteBuffer.wrap(output.toByteArray());
    }

    /**
     * Calc the size of the buffers
     * @return
     */
    private long calcSizeOfBuffers() {
        long size = 0;
        for (ByteBuffer byteBuffer : buffer) {
            size += byteBuffer.position();
        }
        return size;
    }


    /**
     * Close the stream and mark this entry as a "delete" record.
     */
    public void delete() throws IOException {
        closing = true;//From now on, writes go the the delegate, not the buffer
        TarHeader tarHeader = TarHeader.createHeader(TapeUtils.toDeleteFilename(id),0,System.currentTimeMillis()/1000,false);
        TarEntry entry = new TarEntry(tarHeader);
        putNextEntry(entry);
        closeCurrentEntry();
        out.close();
        index.remove(id);
        closed = true; //Now we cannot write anymore
        writeLock.unlock(); //unlock the storage system, we are done
    }

}