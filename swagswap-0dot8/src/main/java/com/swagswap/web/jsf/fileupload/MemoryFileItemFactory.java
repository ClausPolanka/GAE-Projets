package com.swagswap.web.jsf.fileupload;
/*  Modified from http://code.google.com/p/file-upload-for-gae/  */

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.io.FileCleaningTracker;


/**
*/
public class MemoryFileItemFactory implements FileItemFactory {

    // ----------------------------------------------------- Manifest constants


    /**
     * The default threshold above which uploads will be rejected.
     */
    public static final int DEFAULT_SIZE_THRESHOLD = 4*1024*1024;  //4MB

    /**
     * The threshold above which uploads will be stored on disk.
     */
    private int sizeThreshold = DEFAULT_SIZE_THRESHOLD;


    /**
     * <p>The instance of {@link FileCleaningTracker}, which is responsible
     * for deleting temporary files.</p>
     * <p>May be null, if tracking files is not required.</p>
     */
    private FileCleaningTracker fileCleaningTracker;

    // ----------------------------------------------------------- Constructors


    /**
     * Constructs an unconfigured instance of this class. The resulting factory
     * may be configured by calling the appropriate setter methods.
     */
    public MemoryFileItemFactory() {
        this(DEFAULT_SIZE_THRESHOLD, null);
    }


    /**
     * Constructs a preconfigured instance of this class.
     *
     * @param sizeThreshold The threshold, in bytes, below which items will be
     *                      retained in memory and above which they will be
     *                      stored as a file.
     * @param repository    The data repository, which is the directory in
     *                      which files will be created, should the item size
     *                      exceed the threshold.
     */
    public MemoryFileItemFactory(int sizeThreshold, File repository) {
        this.sizeThreshold = sizeThreshold;
    }

    // ------------------------------------------------------------- Properties






    /**
     * Returns the size threshold beyond which files are written directly to
     * disk. The default value is 10240 bytes.
     *
     * @return The size threshold, in bytes.
     *
     * @see #setSizeThreshold(int)
     */
    public int getSizeThreshold() {
        return sizeThreshold;
    }


    /**
     * Sets the size threshold beyond which files are written directly to disk.
     *
     * @param sizeThreshold The size threshold, in bytes.
     *
     * @see #getSizeThreshold()
     *
     */
    public void setSizeThreshold(int sizeThreshold) {
        this.sizeThreshold = sizeThreshold;
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Create a new {@link MemoryFileItem}
     * instance from the supplied parameters and the local factory
     * configuration.
     *
     * @param fieldName   The name of the form field.
     * @param contentType The content type of the form field.
     * @param isFormField <code>true</code> if this is a plain form field;
     *                    <code>false</code> otherwise.
     * @param fileName    The name of the uploaded file, if any, as supplied
     *                    by the browser or other client.
     *
     * @return The newly created file item.
     */
    public FileItem createItem(String fieldName, String contentType,
            boolean isFormField, String fileName) {
        MemoryFileItem result = new MemoryFileItem(fieldName, contentType,
                isFormField, fileName, sizeThreshold);
        return result;
    }
}
