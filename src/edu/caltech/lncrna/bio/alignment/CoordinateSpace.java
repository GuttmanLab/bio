package edu.caltech.lncrna.bio.alignment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

/**
 * This class represents the coordinate-ranges of a genome assembly.
 * <p>
 * Currently, the primary use of the class is to construct headers for SAM and
 * BAM files.
 */
public final class CoordinateSpace {

    public final static CoordinateSpace MM9 = new CoordinateSpace(GenomeSize.MM9);
    public final static CoordinateSpace MM10 = new CoordinateSpace(GenomeSize.MM10);
    public final static CoordinateSpace HG19 = new CoordinateSpace(GenomeSize.HG19);

    private final Map<String, Integer> refSizes;

    /**
     * Constructs an instance of a <code>CoordinateSpace</code> with the given
     * chromosome-to-size mapping.
     * @param sizes - a mapping from chromosome-name to size-in-bp
     */
    public CoordinateSpace(Map<String, Integer> sizes) {
        this.refSizes = sizes;
    }
    
    /**
     * Extracts the header from the given BAM file and constructs a <code>
     * CoordinateSpace</code> from it.
     * @param bamFilePath - the <code>Path</code> to the BAM file
     * @throws RuntimeException if an I/O operation fails or is interrupted
     */
    public CoordinateSpace(Path bamFilePath) {
        SamReaderFactory samReaderFactory = SamReaderFactory.makeDefault()
                .validationStringency(ValidationStringency.SILENT);
        try (SamReader samReader = samReaderFactory.open(bamFilePath.toFile())) {
            this.refSizes = getRefSeqLengthsFromSamHeader(samReader.getFileHeader());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Constructs a <code>CoordinateSpace</code> from a htsjdk
     * <code>SAMFileHeader</code> object.
     * @param fileHeader - the SAM file header
     */
    public CoordinateSpace(SAMFileHeader fileHeader) {
        this.refSizes = getRefSeqLengthsFromSamHeader(fileHeader);
    }
    
    private Map<String, Integer> getRefSeqLengthsFromSamHeader(SAMFileHeader header) {
        Map<String, Integer> rtrn = new TreeMap<String, Integer>();
        List<SAMSequenceRecord> records = header.getSequenceDictionary().getSequences();
        if (records.size() > 0) {
            for (SAMSequenceRecord rec : header.getSequenceDictionary().getSequences()) {
                String chr = rec.getSequenceName();
                int size = rec.getSequenceLength();
                rtrn.put(chr, size);
                }
            }
        return rtrn;
    }
    
    /**
     * Returns the htsjdk <code>SAMFileHeader</code> object represented by this
     * coordinate space.
     */
    public SAMFileHeader getSAMFileHeader() {
        SAMFileHeader header = new SAMFileHeader();

        for (Map.Entry<String, Integer> entry : refSizes.entrySet()) {
            int size = entry.getValue();
            SAMSequenceRecord seq = new SAMSequenceRecord(entry.getKey(), size);
            header.addSequence(seq);
        }

        header.setSortOrder(SAMFileHeader.SortOrder.coordinate);
                
        return header;
    }
}