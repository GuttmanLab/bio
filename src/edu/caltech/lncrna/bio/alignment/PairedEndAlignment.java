package edu.caltech.lncrna.bio.alignment;

import java.util.Objects;
import java.util.Optional;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.annotation.Annotation;
import edu.caltech.lncrna.bio.annotation.Strand;
import edu.caltech.lncrna.bio.sequence.Base;
import htsjdk.samtools.SAMFileWriter;

/**
 * This class represents a successfully aligned paired-end fragment.
 */
public final class PairedEndAlignment
extends Annotation implements Alignment, PairedSamRecord {
    
    private final SingleReadAlignment read1;
    private final SingleReadAlignment read2;

    public static PairedEndAlignment newInstance(SingleReadAlignment read1,
            SingleReadAlignment read2) {
        Objects.requireNonNull(read1, "Attempted to construct a PairedEndAlignment " +
                "with a null read1");
        Objects.requireNonNull(read2, "Attempted to construct a PairedEndAlignment " +
                "with a null read2");
        
        if (read2.isFirstInPair()) {
            SingleReadAlignment tmp = read1;
            read1 = read2;
            read2 = tmp;
        }

        String ref1 = read1.getReferenceName();
        String ref2 = read2.getReferenceName();
        if (!ref1.equals(ref2)) {
            throw new IllegalArgumentException("Attemped to construct a " +
                    "PairedEndAlignment with reads that align to different " +
                    "references: " + ref1 + ", " + ref2);
        }
        
        PairOrientation po = PairOrientation.getPairOrientation(read1, read2);
        Strand strand = po.getStrand();
        Annotation annot = (new AnnotationBuilder())
                .addAnnotationFromCigar(read1.samRecord.getCigar(), ref1,
                        read1.getStart(), strand)
                .addAnnotationFromCigar(read2.samRecord.getCigar(), ref2,
                        read2.getStart(), strand)
                .build();
        assert annot.getEnd() == Math.max(read1.getEnd(), read2.getEnd()) :
            "Annotation is not consistant with SAMRecord.";
        
        return new PairedEndAlignment(annot, read1, read2);
    }
    
    private PairedEndAlignment(Annotation annot, SingleReadAlignment align1,
            SingleReadAlignment align2) {
        super(annot);
        read1 = align1;
        read2 = align2;
    }
    
    /**
     * Returns the <code>PairOrientation</code> of this
     * <code>PairedEndAlignment</code>.
     */
    public PairOrientation getPairOrientation() {
        return PairOrientation.getPairOrientation(read1, read2);
    }
    
    @Override
    public String getName() {
        return read1.getName();
    }
    
    @Override
    public SingleReadAlignment getFirstReadInPair() {
        return read1;
    }

    @Override
    public SingleReadAlignment getSecondReadInPair() {
        return read2;
    }

    @Override
    public void writeTo(SAMFileWriter writer) {
        read1.writeTo(writer);
        read2.writeTo(writer);
    }
    
    /**
     * Returns the insert size of this <code>PairedEndAlignment</code>.
     * <p>
     * If the reads overlap, this method returns 0.
     */
    public int getInsertSize() {
        Optional<Annotated> insert = getInsert();

        if (insert.isPresent()) {
            return insert.get().getSize();
        }
        
        return 0;
    } 
    
    /**
     * Returns the insert of this <code>PairedEndAlignment</code>.
     * <p>
     * Returns an <code>Annotated</code> object representing the insert of
     * this, if it exists, wrapped in an <code>Optional</code>. Otherwise,
     * returns an empty <code>Optional</code> instance.
     */
    public Optional<Annotated> getInsert() {
        int read1Start = read1.getStart();
        int read2Start = read2.getStart();
        int read1End = read1.getEnd();
        int read2End = read2.getEnd();
        
        if (read1Start <= read2End && read2Start <= read1End) {
            return Optional.empty();
        }

        return Optional.of(new Annotation(getReferenceName(),
                               Math.min(read1End, read2End),
                               Math.max(read1Start, read2Start),
                               getStrand()));
    }
    
    @Override
    public Base getReadBaseFromReferencePosition(int pos) {
        Base base1 = read1.getReadBaseFromReferencePosition(pos);
        Base base2 = read2.getReadBaseFromReferencePosition(pos);
        
        if (base1.equals(base2)) {
            return base1;
        }
        
        if (base1.equals(Base.INVALID)) {
            return base2;
        }
        
        if (base2.equals(Base.INVALID)) {
            return base1;
        }
        
        return Base.CONFLICTING;
    }
    
    @Override
    public boolean isSpliced() {
        return read1.isSpliced() || read2.isSpliced();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof PairedEndAlignment)) {
            return false;
        }
        
        PairedEndAlignment other = (PairedEndAlignment) o;
        
        return read1.equals(other.read1) &&
               read2.equals(other.read2) &&
               super.equals(other);
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 37 * hashCode + read1.hashCode();
        hashCode = 37 * hashCode + read2.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
