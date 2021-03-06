package edu.caltech.lncrna.bio.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import edu.caltech.lncrna.bio.annotation.Gene.GeneBuilder;
import edu.caltech.lncrna.bio.io.FormattableWithFields;

/**
 * This class represents an object corresponding to one line of a BEDPE file.
 * <p>
 * The BEDPE format was introduced by the developers of bedtools. Further
 * information can be found on the bedtools readthedocs page.
 * @see http://bedtools.readthedocs.io/en/latest/content/general-usage.html
 */
public final class BedpeFileRecord implements FormattableWithFields {

    private final Annotated block1;
    private final Annotated block2;
    private final String name;
    private final String score;
    private final String[] additionalFields;
    
    public final static String EMPTY_NAME = ".";
    // BEDPE files allow any string to be a score, not just numerics.
    public final static String DEFAULT_SCORE = "0";
    public final static int MIN_FIELDS = 6;
    public final static int NUM_STANDARD_FIELDS = 10;
    public final static String UNKNOWN_REFERENCE = ".";
    public final static int UNKNOWN_POSITION = -1;
    
    public BedpeFileRecord(BedpeBuilder b) {
        this.block1 = b.block1;
        this.block2 = b.block2;
        this.name = b.name == null ? EMPTY_NAME : b.name;
        this.score = b.score == null ? DEFAULT_SCORE : b.score;
        this.additionalFields = b.additionalFields
                .toArray(new String[b.additionalFields.size()]);
    }
    
    /**
     * Gets the first block in the BEDPE record, if it exists, wrapped in an
     * <code>Optional</code>; otherwise an empty <code>Optional</code>.
     */
    public Optional<Annotated> getBlock1() {
        return Optional.ofNullable(block1);
    }

    /**
     * Gets the second block in the BEDPE record, if it exists, wrapped in an
     * <code>Optional</code>; otherwise an empty <code>Optional</code>.
     */
    public Optional<Annotated> getBlock2() {
        return Optional.ofNullable(block2);
    }
    
    /**
     * Gets the feature represented by the two blocks in the BEDPE record, if
     * it exists, wrapped in an <code>Optional</code>.
     * <p>
     * If either block does not exist, or if the references that they align to
     * are not identical, this method will return an empty <code>Optional</code>.
     */
    public Optional<Gene> toGene() {
        if (block1 == null || block2 == null) {
            return Optional.empty();
        }

        String refName = block1.getReferenceName();
        if (!refName.equals(block2.getReferenceName())) {
            return Optional.empty();
        }
        
        GeneBuilder gb = new GeneBuilder();
        
        Strand strand = (block1.getStrand().equals(block2.getStrand()))
                      ? block1.getStrand()
                      : Strand.BOTH;
        
        int start = Math.min(block1.getStart(), block2.getStart());
        int end = Math.max(block1.getEnd(), block2.getEnd());
                      
        gb.addAnnotation(new Annotation(refName, start, end, strand));
        gb.addName(name);
        return Optional.of(gb.build());
    }
    
    /**
     * Gets the name of this BEDPE record.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the score of this BEDPE record.
     */
    public String getScore() {
        return score;
    }
    
    public Optional<String> getAdditionalField(int i) {
        if (i >= 0 && i < additionalFields.length) {
            return Optional.of(additionalFields[i]);
        }
        return Optional.empty();
    }
    
    @Override
    public String toFormattedString() {
        return toFormattedString(NUM_STANDARD_FIELDS + additionalFields.length);
    }

    @Override
    public String toFormattedString(int numFields) {
        validateBedpeFieldNumber(numFields);
        
        // Required fields
        final StringBuilder sb = new StringBuilder();

        if (block1 == null) {
            sb.append(UNKNOWN_REFERENCE + "\t" +
                      UNKNOWN_POSITION + "\t" +
                      UNKNOWN_POSITION + "\t");
        } else {
            sb.append(block1.getReferenceName() + "\t" +
                      block1.getStart() + "\t" +
                      block1.getEnd() + "\t");
        }
        
        if (block2 == null) {
            sb.append(UNKNOWN_REFERENCE + "\t" +
                      UNKNOWN_POSITION + "\t" +
                      UNKNOWN_POSITION);
        } else {
            sb.append(block2.getReferenceName() + "\t" +
                      block2.getStart() + "\t" +
                      block2.getEnd());
        }
        
        if (numFields == 6) return sb.toString();
        
        // Name
        sb.append("\t" + name);
        if (numFields == 7) return sb.toString();
        
        // Score
        sb.append("\t" + score);
        if (numFields == 8) return sb.toString();
        
        // Strands
        if (block1 == null) {
            sb.append("\t" + Strand.BOTH.toString());
        } else {
            sb.append("\t" + block1.getStrand()
                    .toString());
        }
        if (numFields == 9) return sb.toString();
        
        if (block2 == null) {
            sb.append("\t" + Strand.BOTH.toString());
        } else {
            sb.append("\t" + block2.getStrand()
                    .toString());
        }
        if (numFields == 10) return sb.toString();
        
        for (int i = 0; i < additionalFields.length; i++) {
            sb.append(additionalFields[i]);
            
            if (numFields == BedpeFileRecord.NUM_STANDARD_FIELDS + i + 1) {
                return sb.toString();
            }
        }
        
        return sb.toString();
    }

    /**
     * Parses a BEDPE-formatted <code>String</code> and returns the corresponding
     * annotation as a <code>BedpeFileRecord</code>
     * @param s - the <code>String</code> to parse
     */
    public static BedpeFileRecord fromFormattedString(String s) {
        BedpeBuilder bb = new BedpeBuilder();
        String[] fields = s.trim().split("\\s+");
        
        if (fields.length < MIN_FIELDS) {
            throw new IllegalArgumentException("A BEDPE record must have at least " +
                    MIN_FIELDS + "fields. fromFormattedString(s) was " +
                    "passed a string with " + fields.length + " fields.");
        }

        // Fields 0 through 5 are guaranteed to be present.
        // Otherwise this isn't a valid BEDPE file.
        String ref1 = fields[0];
        int start1 = Integer.parseInt(fields[1]);
        int end1 = Integer.parseInt(fields[2]);
        Strand strand1 = Strand.BOTH;
        
        String ref2 = fields[3];
        int start2 = Integer.parseInt(fields[4]);
        int end2 = Integer.parseInt(fields[5]);
        Strand strand2 = Strand.BOTH;
        
        if (fields.length >= NUM_STANDARD_FIELDS) {
            strand1 = Strand.fromString(fields[8]);
            strand2 = Strand.fromString(fields[9]);
        }
        
        bb.addBlock1(new Annotation(ref1, start1, end1, strand1));
        bb.addBlock2(new Annotation(ref2, start2, end2, strand2));

        if (fields.length >= 7) {
            bb.addName(fields[6]);
        }
        
        if (fields.length >= 8) {
            bb.addScore(fields[7]);
        }
        
        if (fields.length > NUM_STANDARD_FIELDS) {
            for (int i = NUM_STANDARD_FIELDS; i < fields.length; i++) {
                bb.addAdditionalField(fields[i]);
            }
        }

        return bb.build();
    }
    
    public static BedpeBuilder builder() {
        return new BedpeBuilder();
    }
    
    private void validateBedpeFieldNumber(int i) {
        if (i < BedpeFileRecord.MIN_FIELDS) {
            throw new IllegalArgumentException(invalidBedpeNumberMessage(i));
        }
    }
    
    private String invalidBedpeNumberMessage(int i) {
        return "Attempted to create formatted BEDPE string with invalid " +
                "number of fields: " + i + ". Number of fields must be " +
                "greater than or equal to " + BedpeFileRecord.MIN_FIELDS + ".";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof BedpeFileRecord)) {
            return false;
        }
        
        BedpeFileRecord other = (BedpeFileRecord) o;
        
        boolean block1equal = block1 == null
                            ? other.block1 == null
                            : block1.equals(other.block1);
        
        boolean block2equal = block2 == null
                ? other.block2 == null
                : block2.equals(other.block2);
        
        return block1equal &&
               block2equal &&
               name.equals(other.name) &&
               score.equals(other.score) &&
               Arrays.equals(additionalFields, other.additionalFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 37 * hashCode + block1.hashCode();
        hashCode = 37 * hashCode + block2.hashCode();
        hashCode = 37 * hashCode + name.hashCode();
        hashCode = 37 * hashCode + score.hashCode();
        hashCode = 37 * hashCode + Arrays.hashCode(additionalFields);;
        return hashCode;
    }
    
    /**
     * A builder class for constructing {@link BedpeFileRecord}s.
     */
    public static class BedpeBuilder {
        
        private Annotated block1;
        private Annotated block2;
        private String name;
        private String score;
        private List<String> additionalFields;
        
        public BedpeBuilder() {
            additionalFields = new ArrayList<String>();
        }
        
        public BedpeBuilder addBlock1(Annotated b) {
            block1 = b;
            return this;
        }
        
        public BedpeBuilder addBlock2(Annotated b) {
            block2 = b;
            return this;
        }
        
        public BedpeBuilder addScore(String score) {
            this.score = score;
            return this;
        }
                
        public BedpeBuilder addName(String s) {
            name = s;
            return this;
        }
        
        public BedpeBuilder addAdditionalField(String s) {
            additionalFields.add(s);
            return this;
        }
        
        public BedpeFileRecord build() {
            return new BedpeFileRecord(this);
        }
    }
}