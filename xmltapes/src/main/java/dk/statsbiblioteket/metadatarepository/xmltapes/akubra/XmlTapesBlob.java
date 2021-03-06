package dk.statsbiblioteket.metadatarepository.xmltapes.akubra;

import dk.statsbiblioteket.metadatarepository.xmltapes.common.AkubraCompatibleArchive;
import dk.statsbiblioteket.metadatarepository.xmltapes.common.StreamUtils;
import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.impl.AbstractBlob;
import org.akubraproject.impl.StreamManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 5/16/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlTapesBlob extends AbstractBlob {

    private static final Logger log = LoggerFactory.getLogger(XmlTapesBlob.class);


    private final AkubraCompatibleArchive archive;
    private StreamManager streamManager;

    public XmlTapesBlob(BlobStoreConnection owner, URI id, AkubraCompatibleArchive archive, StreamManager streamManager) {
        super(owner, id);
        this.archive = archive;
        this.streamManager = streamManager;
    }

    @Override
    public InputStream openInputStream() throws IOException, MissingBlobException {
        ensureOpen();
        try {
            InputStream inputStream = archive.getInputStream(getId());
            return streamManager.manageInputStream(getConnection(),inputStream);
        } catch (FileNotFoundException e){
            throw new MissingBlobException(getId());
        }
    }

    @Override
    public OutputStream openOutputStream(long estimatedSize, boolean overwrite) throws IOException, DuplicateBlobException {
        ensureOpen();
        if (! overwrite && exists()){
            throw new DuplicateBlobException(getId());
        }
        OutputStream aNew = archive.createNew(getId(), estimatedSize);
        return streamManager.manageOutputStream(getConnection(),aNew);
    }

    @Override
    public long getSize() throws IOException {
        ensureOpen();
        try {
            return archive.getSize(getId());
        } catch (FileNotFoundException e){
            throw new MissingBlobException(getId());
        }
    }

    @Override
    public boolean exists() throws IOException {
        ensureOpen();
        return archive.exist(getId());
    }

    @Override
    public void delete() throws IOException {
        ensureOpen();
        archive.remove(getId());
    }

    @Override
    public Blob moveTo(URI blobId, Map<String, String> hints) throws DuplicateBlobException, IOException, MissingBlobException {
        ensureOpen();
        if (blobId == null){
            throw new UnsupportedOperationException("Cannot support ID generation, please supply an ID");
        }
        XmlTapesBlob newBlob = new XmlTapesBlob(getConnection(), blobId, archive, streamManager);
        if (newBlob.exists()){
            throw new DuplicateBlobException(blobId,"New blob already exists");
        }
        if ( ! this.exists()){
            throw new MissingBlobException(this.getId());
        }



        OutputStream them = newBlob.openOutputStream(this.getSize(), true);
        InputStream my = openInputStream();
        StreamUtils.copy(my,them);
        this.delete();
        return newBlob;
    }

    public String toString(){
        return getId().toString();
    }
}
