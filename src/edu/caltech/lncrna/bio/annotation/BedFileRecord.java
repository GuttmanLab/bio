package edu.caltech.lncrna.bio.annotation;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * This class represents a record contained in one line of a BED file.
 * <p>
 * Constructors for this class are not exposed. To construct a
 * <code>BedFileRecord</code>, use a {@link BedBuilder}:
 * <pre>
 * <code>
 * BedFileRecord b = (new BedBuilder())
 *     .addBlock(new Block("chr2", 1300, 1350, Strand.POSITIVE))
 *     .addBlock(new Block("chr2", 1400, 1450, Strand.POSITIVE))
 *     .addName("myBedName")
 *     .addCodingRegion(1325, 1425)  // This is drawn as a thick line
 *     .addColor(Color.LILAC)
 *     .addScore(0.54321)
 *     .build();
 * </code>
 * </pre>
 * Unspecified fields will have sensible defaults.
 */
public final class BedFileRecord extends Gene {

    private final double score;
    private final Color color;
    
    protected static final double DEFAULT_SCORE = 0;
    protected static final Color DEFAULT_COLOR = Color.BLACK;
    protected static final int[] VALID_NUM_FIELDS = new int[] {3, 4, 5, 6, 8, 9, 12};
    protected static final int MAX_FIELDS = 12;
    
    protected BedFileRecord(BedBuilder b) {
        super(b);
        this.score = b.score;
        this.color = b.color;
    }
    
    /**
     * Gets the score of this.
     */
    public double getScore() {
        return score;
    }
    
    /**
     * Gets the color of this.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Parses a BED-formatted <code>String</code> and returns the corresponding
     * annotation as a <code>BEDFileRecord</code>
     * @param s - the <code>String</code> to parse
     */
    public static BedFileRecord fromFormattedString(String s) {
        BedBuilder bb = new BedBuilder();
        String[] fields = s.trim().split("\\s+");
        int numFields = fields.length;
        
        if (IntStream.of(VALID_NUM_FIELDS).noneMatch(x -> x == numFields)) {
            throw new IllegalArgumentException("fromFormattedString() was passed a String with "
                    + numFields + " fields. A properly formatted BED String must have between three and"
                    + " twelve fields, and cannot have seven, ten or eleven fields.");
        }

        // Fields 0, 1, and 2 are guaranteed to be present; otherwise this isn't a valid BED file.
        String chrom = fields[0];
        int chromStart = Integer.parseInt(fields[1]) + 1;
        int chromEnd = Integer.parseInt(fields[2]) + 1;

        
        // All fields present. This is an Annotation with multiple blocks
        if (numFields == 12) {
            Strand strand = Strand.fromString(fields[5]);
            int blockCount = Integer.parseInt(fields[9]);
            int[] blockSizes = parseCommaSeparatedString(fields[10]);
            int[] blockStarts = parseCommaSeparatedString(fields[11]);
            
            if (blockStarts.length != blockCount || blockSizes.length != blockCount) {
                throw new IllegalArgumentException("Malformed BED String. blockCount = " + blockCount
                        + ", blockSizes = " + blockSizes.length + ", and blockStarts = " + blockStarts.length +
                        ". All should be equal.");
            }
            
            // TODO check consistency between field[2] and last block.
            
            for (int i = 0; i < blockCount; i++) {
                bb.addAnnotation(new Annotation(chrom, chromStart + blockStarts[i], chromStart + blockStarts[i] + blockSizes[i], strand));
            }
        
        // This is an Annotation with one block and strand information.
        } else if (numFields >= 6) {
            Strand strand = Strand.fromString(fields[5]);
            bb.addAnnotation(new Annotation(chrom, chromStart, chromEnd, strand));

        // This is an Annotation with one block and no strand information: default to Strand.BOTH.
        } else {
            bb.addAnnotation(new Annotation(chrom, chromStart, chromEnd, Strand.BOTH));
        }
        
        // Annotation has been constructed. Add the rest of the fields.

        // Add name
        if (numFields >= 4) {
            bb.addName(fields[3]);
        }
        
        // Add score
        if (numFields >= 5) {
            bb.addScore(Double.parseDouble(fields[4]));
        }
        
        // No need to add strand information. Should be contained in the blocks.
        
        // Add line thickness.
        if (numFields >= 8) {
            bb.addCodingRegion(Integer.parseInt(fields[6]) + 1, Integer.parseInt(fields[7]) + 1);
        }
        
        // Add color.
        if (numFields >= 9) {
            if (fields[8].equals(".") || fields[8].equals("0")) {
                bb.addColor(DEFAULT_COLOR);
            } else {
                int[] colorVals = parseCommaSeparatedString(fields[8]);
                if (colorVals.length != 3) {
                    throw new IllegalArgumentException("Formatted BED string has invalid color value: " + fields[8]);
                }
                bb.addColor(new Color(colorVals[0], colorVals[1], colorVals[2]));
            }
        }
        
        // All done. Return the BED record.
        return bb.build();
    }
    
    // TODO Look into using Pattern regex?
    // Helper method for fromFormattedString(). Handles parsing of the blockStarts, blockSizes and color BED fields.
    private static int[] parseCommaSeparatedString(String s) {
        String[] tmp = s.endsWith(",") ? s.substring(0, s.length() - 1).split(",") : s.split(",");
        int[] rtrn = new int[tmp.length];
        for (int i = 0; i < rtrn.length; i++) {
            rtrn[i] = Integer.parseInt(tmp[i]);
        }
        return rtrn;
    }
    
    @Override
    public String toFormattedBedString(int numFields) {
        return bedStringBuilder()
                .addCodingRegion(this.cdsStartPos, this.cdsEndPos)
                .addColor(this.color)
                .addName(this.name)
                .addScore(this.score)
                .build(numFields);
    }
    
    public static BedBuilder builder() {
        return new BedBuilder();
    }

    static void validateBedFieldNumber(int i) {
        if (IntStream.of(BedFileRecord.VALID_NUM_FIELDS)
                .noneMatch(x -> x == i)) {

            throw new IllegalArgumentException(invalidBedNumberMessage(i));
        }
    }
    
    private static String invalidBedNumberMessage(int i) {
        return "Attempted to create formatted BED string with invalid " +
                "number of fields: " + i + ". Number of fields must be one " +
                "of 3, 4, 5, 6, 8, 9, 12.";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof BedFileRecord)) {
            return false;
        }
        
        BedFileRecord other = (BedFileRecord) o;
        
        return super.equals(other) &&
               score == other.score &&
               color.equals(other.color);
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 37 * hashCode + Double.hashCode(score);
        hashCode = 37 * hashCode + color.hashCode();
        return hashCode;
    }
    
    /**
     * A builder class for constructing {@link BedFileRecord}s.
     * <p>
     * An object of this class can be loaded with <code>Block</code>s, and will
     * construct the corresponding <code>BedFileRecord</code> when its
     * <code>build()</code> method is invoked. Any disagreement (for example,
     * conflicting reference names among the blocks, or a coding region outside
     * the bounds of the annotation) will result in an exception being thrown.
     * <p>
     * Many fields in a BED file are optional. If these fields are not
     * explicitly provided to this builder, the following will be used as
     * defaults:
     * <ul>
     * <li>score: <code>0</code>
     * <li>name: <code>""</code>
     * <li>color: <code>Color.BLACK</code>
     * <li>coding region: the <code>BedFileRecord</code> will have no coding
     * region, and when output as a formatted <code>String</code>, the
     * thickStart and thickEnd fields will both default to the starting
     * reference coordinate
     * </ul> 
     */
    public static class BedBuilder extends GeneBuilder {
        
        private double score;
        private Color color;
        
        /**
         * Constructs an empty builder.
         * <p>
         * The score is initialized to <code>0</code> and the color is
         * initialized to <code>Color.BLACK</code>.
         */
        public BedBuilder() {
            super();
            score = DEFAULT_SCORE;
            color = DEFAULT_COLOR;
        }
        
        /**
         * Adds a score to this builder.
         * @param score - the score to add
         * @return this builder form method-chaining
         */
        public BedBuilder addScore(double score) {
            this.score = score;
            return this;
        }
        
        /**
         * Adds a color to this builder.
         * @param color - the color to add
         * @return this builder for method-chaining
         */
        public BedBuilder addColor(Color color) {
            this.color = color;
            return this;
        }
        
        /**
         * Adds a color, specified by its RGB values, to this builder.
         * @param r - the red value, between 0 and 255 inclusive
         * @param g - the green value, between 0 and 255 inclusive
         * @param b - the blue value, between 0 and 255 inclusive
         * @return this builder for method-chaining
         * @throws IllegalArgumentException if <code>r</code>, <code>g</code>
         * or <code>b</code> are outside the range 0 to 255 inclusive
         */
        public BedBuilder addColor(int r, int g, int b) {
            color = new Color(r, g, b);
            return this;
        }
        
        /**
         * Adds a color, specified by its combined RGB value, to this builder.
         * <p>
         * The value <code>rgb</code> should have its red value in bits 16-23,
         * its green value in bits 8-15, and its blue value in bits 0-7.
         * @param rgb - the combined RGB value
         * @return this builder for method-chaining
         */
        public BedBuilder addColor(int rgb) {
            this.color = new Color(rgb);
            return this;
        }
        
        @Override
        public BedBuilder addAnnotation(Annotated b) {
            return (BedBuilder) super.addAnnotation(b);
        }
        
        @Override
        public BedBuilder addAnnotations(Collection<Annotated> bs) {
            return (BedBuilder) super.addAnnotations(bs);
        }
        
        @Override
        public BedBuilder addName(String s) {
            return (BedBuilder) super.addName(s);
        }
        
        @Override
        public BedBuilder addCodingRegion(int cdsStart, int cdsEnd) {
            return (BedBuilder) super.addCodingRegion(cdsStart, cdsEnd);
        }
        
        @Override
        public BedFileRecord build() {
            
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

            return new BedFileRecord(this);
        }
    }
    
   public static final class BedStringBuilder {
        
       private final Annotated annot;
       private double score;
       private Color color;
       private String name;
       private int cdsStart;
       private int cdsEnd;
        
       public BedStringBuilder(Annotated annot) {
           this.annot = annot;
           score = DEFAULT_SCORE;
           color = DEFAULT_COLOR;
           name = EMPTY_NAME;
           cdsStart = annot.getStart();
           cdsEnd = annot.getEnd();
       }
        
       protected BedStringBuilder addScore(double score) {
           this.score = score;
           return this;
       }
        
       protected BedStringBuilder addColor(Color color) {
           this.color = color;
           return this;
       }
        
       protected BedStringBuilder addColor(int r, int g, int b) {
           color = new Color(r, g, b);
           return this;
       }
        
       protected BedStringBuilder addColor(int rgb) {
           this.color = new Color(rgb);
           return this;
       }
        
       protected BedStringBuilder addName(String s) {
           this.name = s;
           return this;
       }
        
       protected BedStringBuilder addCodingRegion(int cdsStart, int cdsEnd) {
           this.cdsStart = cdsStart;
           this.cdsEnd = cdsEnd;
           return this;
       }
       
       protected String build() {
           return build(BedFileRecord.MAX_FIELDS);
       }
       
       protected String build(int numFields) {
           validateBedFieldNumber(numFields);
           
           // Required fields
           final StringBuilder sb = new StringBuilder();
           sb.append(annot.getReferenceName() + "\t");
           sb.append(annot.getStart() + "\t");
           sb.append(annot.getEnd());
           if (numFields == 3) return sb.toString();
           
           // Name
           sb.append("\t" + name);
           if (numFields == 4) return sb.toString();
           
           // Score
           sb.append("\t" + score);
           if (numFields == 5) return sb.toString();
           
           // Strand
           sb.append("\t" + annot.getStrand().toString());
           if (numFields == 6) return sb.toString();
           
           // Thick
           sb.append("\t" + cdsStart + "\t" + cdsEnd);
           if (numFields == 8) return sb.toString();

           sb.append("\t" + color.getRed() + "," + color.getGreen() + "," + color.getBlue());
           if (numFields == 9) return sb.toString();
           
           sb.append("\t" + annot.getNumberOfBlocks() + "\t");
           Iterator<Annotated> blocks = annot.getBlockIterator();
           while (blocks.hasNext()) {
               Annotated block = blocks.next();
               // trailing comma after last block is OK
               sb.append(block.getSize() + ",");
           }
           sb.append("\t");
           blocks = annot.getBlockIterator();
           while (blocks.hasNext()) {
               Annotated block = blocks.next();
               // trailing comma after last block is OK
               sb.append((block.getStart() - annot.getStart()) + ",");
           }
           sb.append(System.lineSeparator());
           return sb.toString();
       }
    }
}