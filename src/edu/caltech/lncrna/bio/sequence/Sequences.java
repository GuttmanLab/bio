package edu.caltech.lncrna.bio.sequence;

public final class Sequences {
    
    private Sequences() { }
    
    public static String reverseComplement(String s) {
        return reverse(complement(s));
    }
    
    public static String reverse(String s) {
        return (new StringBuilder(s).reverse().toString());
    }
    
    public static String complement(String s) {
        char[] cs = s.toCharArray();
        char[] rtrn = new char[cs.length];
        for (int i = 0; i < cs.length; i++) {
            rtrn[i] = complement(cs[i]);
        }
        return String.valueOf(rtrn);
    }
    
    /**
     * Returns the complement of a base represented as a character.
     * <p>
     * This method is case sensitive. For example, <code>Sequences.complement('a') == 't'</code>.
     * This method only recognizes the bases A, C, G, T and N, as well as their lowercase counterparts.
     * @param c - the base to complement
     * @throws IllegalArgumentException if the base is not recognized.
     */
    public static char complement(char c) {
        switch (c) {
        case 'A': return 'T';
        case 'C': return 'G';
        case 'G': return 'C';
        case 'T': return 'A';
        case 'N': return 'N';
        case 'a': return 't';
        case 'c': return 'g';
        case 'g': return 'c';
        case 't': return 'a';
        case 'n': return 'n';
        default: throw new IllegalArgumentException("Unsupported base: " + c);
        }
    }
}