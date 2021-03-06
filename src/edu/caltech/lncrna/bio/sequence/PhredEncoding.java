package edu.caltech.lncrna.bio.sequence;

/**
 * An enumeration of Phred quality-score encodings.
 */
public enum PhredEncoding {

    SANGER(33),
    ILLUMINA_13(64);
    
    private final int offset;
    
    private PhredEncoding(int offset) {
        this.offset = offset;
    }
    
    /**
     * The offset value of this <code>PhredEncoding</code>.
     * <p>
     * As an example,
     * <code>PhredEncoding.SANGER.offset()</code> evaluates to 33, because
     * the Sanger encoding uses the "!" character, which has an ASCII-value of
     * 33, to represent a quality of 0. Likewise, the Sanger encoding uses the
     * "I" character, which has an ASCII of 73, to represent a quality of 40.
     */
    public final int offset() {
        return this.offset;
    }
    
    /**
     * Converts an array of bytes to a String using this <code>PhredEncoding</code>'s
     * offset value.
     * @param bs - the array of bytes to convert
     * @return the String resulting from the conversion
     */
    public final String phredToString(byte[] bs) {
        char[] cs = new char[bs.length];
        for (int i = 0; i < bs.length; i++) {
            cs[i] = (char) (bs[i] + offset);
        }
        return String.valueOf(cs);
    }
    
    /**
     * Converts a String to an array of bytes using this <code>PhredEncoding</code>'s
     * offset value.
     * @param s - the String to convert
     * @return the resulting byte array
     */
    public final byte[] stringToPhred(String s) {
        char[] cs = s.toCharArray();
        byte[] bs = new byte[cs.length];
        for (int i = 0; i < cs.length; i++) {
            bs[i] = (byte) (cs[i] - offset);
        }
        return bs;
    }
}