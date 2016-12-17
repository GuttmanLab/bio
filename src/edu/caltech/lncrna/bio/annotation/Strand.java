package edu.caltech.lncrna.bio.annotation;

/**
 * An enumeration of strands or orientations.
 */
public enum Strand {

    POSITIVE('+') {
        public Strand reverse() {
            return NEGATIVE;
        }
        
        public Strand intersect(Strand other) {
            if (other == null) {
                return Strand.INVALID;
            }
            switch(other) {
            case POSITIVE:
                return Strand.POSITIVE;
            case NEGATIVE:
                return Strand.INVALID;
            case BOTH:
                return Strand.POSITIVE;
            case INVALID:
                return Strand.INVALID;
            default:
                return Strand.INVALID;
            }
        }
    },

    NEGATIVE('-') {
        public Strand reverse() {
            return POSITIVE;
        }
        
        public Strand intersect(Strand other) {
            if (other == null) {
                return Strand.INVALID;
            }
            switch(other) {
            case POSITIVE:
                return Strand.INVALID;
            case NEGATIVE:
                return Strand.NEGATIVE;
            case BOTH:
                return Strand.NEGATIVE;
            case INVALID:
                return Strand.INVALID;
            default:
                return Strand.INVALID;
            }
        }
    },

    BOTH('.') {
        public Strand reverse() {
            return BOTH;
        }
        
        public Strand intersect(Strand other) {
            if (other == null) {
                return Strand.INVALID;
            }
            switch(other) {
            case POSITIVE:
                return Strand.POSITIVE;
            case NEGATIVE:
                return Strand.NEGATIVE;
            case BOTH:
                return Strand.BOTH;
            case INVALID:
                return Strand.INVALID;
            default:
                return Strand.INVALID;
            }
        }
    },
    
    INVALID('!') {
        public Strand reverse() {
            return INVALID;
        }
        
        public Strand intersect(Strand other) {
            return Strand.INVALID;
        }
    };
    
    private char value;
    
    private Strand(char value) {
        this.value = value;
    }

    /**
     * Returns a <code>Strand</code> enum corresponding to a given
     * <code>String</code>.
     * <ul><code>
     * <li>"+" - Strand.POSITIVE
     * <li>"-" - Strand.NEGATIVE
     * <li>"." - Strand.BOTH
     * </code></ul>
     * @param s - the <code>String</code> corresponding to a <code>Strand</code>
     * @throws IllegalArgumentException if passed a <code>String</code> which
     * does not correspond to a <code>Strand</code>
     */
    public static Strand fromString(String s) {
        switch(s) {
        case "+":
            return Strand.POSITIVE;
        case "-":
            return Strand.NEGATIVE;
        case ".":
            return Strand.BOTH;
        default:
            throw new IllegalArgumentException("String " + s + " does not " +
                    "correspond to a known Strand");
        }
    }
    
    @Override
    public String toString() {
        return "" + value;
    }

    /**
     * Gets the reverse this <code>Strand</code>.
     * <ul><code>
     * <li>POSITIVE.reverse() == NEGATIVE
     * <li>NEGATIVE.reverse() == POSITIVE
     * <li>BOTH.reverse() == BOTH
     * </code></ul>
     */
    public abstract Strand reverse();
    
    /**
     * Gets the intersection of this <code>Strand</code> with another
     * <p>
     * This method is mainly used in calculating interval operations in other
     * classes. There are too many combinations to enumerate, but the general
     * behavior is described by the following:
     * <ul><code>
     * <li>POSITIVE.intersect(POSITIVE) == POSITIVE
     * <li>POSITIVE.intersect(NEGATIVE) == INVALID
     * <li>POSITIVE.intersect(BOTH) == POSITIVE
     * </code></ul>
     * The intersect operation is symmetric.
     * @param s - the other strand
     */
    public abstract Strand intersect(Strand s);
}