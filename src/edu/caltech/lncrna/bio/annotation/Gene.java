package edu.caltech.lncrna.bio.annotation;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * This class represents an <code>Annotation</code> with a name and an optional
 * coding region.
 * <p>
 * Most constructors for this class are not exposed. To construct a
 * <code>Gene</code>, use a {@link GeneBuilder}:
 * <pre>
 * <code>
 * Gene g = (new GeneBuilder())
 *     .addBlock(new Block("chr2", 1300, 1350, Strand.POSITIVE))
 *     .addBlock(new Block("chr2", 1400, 1450, Strand.POSITIVE))
 *     .addName("myGene")
 *     .addCodingRegion(1325, 1425)
 *     .build();
 * </code>
 * </pre>
 */
public class Gene extends Annotation {
    
    protected final String name;
    
    protected final int cdsStartPos;
    protected final int cdsEndPos;
    
    protected final static String EMPTY_NAME = ".";

    protected Gene(GeneBuilder b) {
        super(b);
        this.name = b.name;
        this.cdsStartPos = b.cdsStart;
        this.cdsEndPos = b.cdsEnd;
    }
    
    public Gene(Annotated a, String name, int cdsStartPos, int cdsEndPos) {
        super(a);
        this.name = name;
        this.cdsStartPos = cdsStartPos;
        this.cdsEndPos = cdsEndPos;
    }
    
    /**
     * Gets the name of this.
     * <p>
     * If this object doesn't have a name, this method returns the empty <code>String</code>
     */
    public String getName() {
        return name;
    }

    public boolean hasCodingRegion() {
        return cdsStartPos != cdsEndPos;
    }
    
    /**
     * Gets the coding region of this object.
     * <p>
     * @return the coding region of this object, if it exists, as an
     * <code>Annotation</code> wrapped in an <code>Optional</code>; otherwise,
     * an empty <code>Optional</code>
     */
    public Optional<Annotated> getCodingRegion() {
        if (cdsStartPos == cdsEndPos) {
            return Optional.empty();
        }
        
        Annotated cds = new Annotation(getReferenceName(), cdsStartPos, cdsEndPos, getStrand());
        return intersect(cds);
    }

    @Override
    public String toFormattedBedString(int numFields) {
        BedFileRecord.validateBedFieldNumber(numFields);
        
        // Required fields
        final StringBuilder sb = new StringBuilder();
        sb.append(ref + "\t");
        sb.append(getStart() + "\t");
        sb.append(getEnd());
        if (numFields == 3) return sb.toString();
        
        // Name
        sb.append("\t" + name);
        if (numFields == 4) return sb.toString();
        
        // Score
        sb.append("\t" + BedFileRecord.DEFAULT_SCORE);
        if (numFields == 5) return sb.toString();
        
        // Strand
        sb.append("\t" + strand.toString());
        if (numFields == 6) return sb.toString();
        
        // Thick
        sb.append("\t" + cdsStartPos + "\t" + cdsEndPos);
        if (numFields == 8) return sb.toString();
        
        Color c = BedFileRecord.DEFAULT_COLOR;
        sb.append("\t" + c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        if (numFields == 9) return sb.toString();
        
        sb.append("\t" + getNumberOfBlocks() + "\t");
        Iterator<Annotated> blocks = getBlockIterator();
        while (blocks.hasNext()) {
            Annotated block = blocks.next();
            // trailing comma after last block is OK
            sb.append(block.getSize() + ",");
        }
        sb.append("\t");
        blocks = getBlockIterator();
        while (blocks.hasNext()) {
            Annotated block = blocks.next();
            // trailing comma after last block is OK
            sb.append((block.getStart() - getStart()) + ",");
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }
    
    public static GeneBuilder builder() {
        return new GeneBuilder();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof Gene)) {
            return false;
        }
        
        Gene other = (Gene) o;
        
        return super.equals(other) &&
               name.equals(other.name) &&
               ((cdsStartPos == other.cdsStartPos && cdsEndPos == other.cdsEndPos) ||
                (cdsStartPos == cdsEndPos && other.cdsStartPos == other.cdsEndPos));
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 37 * hashCode + name.hashCode();
        hashCode = 37 * hashCode + cdsStartPos;
        hashCode = 37 * hashCode + cdsEndPos;

        return hashCode;
    }
    
    /**
     * A builder class for constructing {@link Gene}s.
     * <p>
     * An object of this class can be loaded with <code>Block</code>s, and will
     * construct the corresponding <code>Gene</code> when its
     * <code>build()</code> method is invoked. Any disagreement among the
     * <code>Block</code>s (for example, conflicting reference names) will
     * result in an exception being thrown.
     * <p>
     * Building a <code>Gene</code> with no name will cause the name to default
     * to the empty <code>String</code>. A <code>Gene</code>'s coding region is
     * also optional.
     */
    public static class GeneBuilder extends AnnotationBuilder {
        
        protected String name = "";
        protected int cdsStart;
        protected int cdsEnd;
        protected boolean newCds = false;
        
        /**
         * Constructs a new builder containing no <code>Block</code>s.
         */
        public GeneBuilder() {
            super();
        }

        /**
         * Adds a name to this builder.
         * <p>
         * Calling this method a second time will simply overwrite the name
         * that was passed the first time.
         * @param name - the name to add
         * @return this builder for method-chaining
         */
        public GeneBuilder addName(String name) {
            this.name = name;
            return this;
        }
        
        /**
         * Adds a coding region to this builder.
         * <p>
         * The values passed to this method are not checked for consistency
         * until the <code>build()</code> method is invoked. Calling this
         * method a second time will simply overwrite the values that were
         * passed the first time.
         * @param cdsStart - the start coordinate of the coding region
         * @param cdsEnd - the end coordinate of the coding region
         * @return this builder for method-chaining
         */
        public GeneBuilder addCodingRegion(int cdsStart, int cdsEnd) {
            this.cdsStart = cdsStart;
            this.cdsEnd = cdsEnd;
            newCds = true;
            return this;
        }
        
        @Override
        public GeneBuilder addAnnotation(Annotated annot) {
            return (GeneBuilder) super.addAnnotation(annot);
        }
        
        @Override
        public GeneBuilder addAnnotations(Collection<Annotated> annots) {
            return (GeneBuilder) super.addAnnotations(annots);
        }
     
        @Override
        public Gene build() {
            
            if (blocks.isEmpty()) {
                throw new IllegalArgumentException("Attempted to build an " +
                        "Annotation with no blocks.");
            }

            if (blocks.size() > 1) {
                checkBlockConsistency();
                mergeBlockListAndUpdateMemberVariables();
            } else {
                updateMemberVariablesWithSingleBlock();
            }
            
            if (newCds) {
                checkNewCdsConsistency();
            } else {
                setDefaultCdsValues();
            }

            return new Gene(this);
        }
        
        /**
         * Checks if this builder's coding sequence coordinates are valid.
         * @throws IllegalArgumentException if this builder's cdsStart
         * is not less than its cdsEnd
         * @throws IllegalArgumentException if this builder's cdsStart
         * coordinate is less than its start coordinate
         * @throws IllegalArgumentException if this builder's cdsEnd
         * coordinate is greater than its end coordinate
         */
        protected void checkNewCdsConsistency() {
            if (cdsStart > cdsEnd) {
                throw new IllegalArgumentException("Attempted to build an " +
                        "Annotation with an invalid coding region. " +
                        "cdsStart must be less than or equal to cdsEnd. " +
                        "cdsStart: " + cdsStart + ", cdsEnd: " + cdsEnd);
            }
            if (cdsStart < blockBoundaries[0]) {
                throw new IllegalArgumentException("Attempted to build an " +
                        "Annotation with an invalid coding region. " +
                        "cdsStart must be greater than or equal to " +
                        "the starting reference position. cdsStart: " + 
                        cdsStart + ", refStart: " + blockBoundaries[0]);
            }
            if (cdsEnd > blockBoundaries[blockBoundaries.length - 1]) {
                throw new IllegalArgumentException("Attempted to build an " +
                        "Annotation with an invalid coding region. " +
                        "cdsStart must be less than or equal to " +
                        "the ending reference position. cdsStart: " + 
                        cdsStart + ", refEnd: " +
                        blockBoundaries[blockBoundaries.length - 1]);
            }
        }
        
        /**
         * Sets this builder's CDS coordinates to their default values.
         * <p>
         * Default values are the same as the default thick-start and
         * thick-end values in a BED file: the builder's start-coordinate.
         */
        protected void setDefaultCdsValues() {
            cdsStart = blockBoundaries[0];
            cdsEnd = cdsStart;
        }
    }
}