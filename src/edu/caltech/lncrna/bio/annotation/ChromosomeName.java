package edu.caltech.lncrna.bio.annotation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A convenience class to convert between b37 reference names (1, 2, ..., MT)
 * and UCSC reference names (chr1, chr2, ..., chrM).
 * <p>
 * This class does not handle unlocalized sequences, unplaced sequences, or
 * alternate loci.
 */
public final class ChromosomeName {

    /**
     * A bi-directional mapping between b37 reference names and UCSC reference
     * names.
     */
    public static final Map<String, String> MAPPING;
    
    /**
     * A mapping from b37 reference names to UCSC reference names.
     */
    public static final Map<String, String> B37_TO_UCSC;
    
    /**
     * A mapping from UCSC reference names to b37 reference names.
     */
    public static final Map<String, String> UCSC_TO_B37;
    
    private ChromosomeName() { }
    
    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("1", "chr1");
        tmp.put("2", "chr2");
        tmp.put("3", "chr3");
        tmp.put("4", "chr4");
        tmp.put("5", "chr5");
        tmp.put("6", "chr6");
        tmp.put("7", "chr7");
        tmp.put("8", "chr8");
        tmp.put("9", "chr9");
        tmp.put("10", "chr10");
        tmp.put("11", "chr11");
        tmp.put("12", "chr12");
        tmp.put("13", "chr13");
        tmp.put("14", "chr14");
        tmp.put("15", "chr15");
        tmp.put("16", "chr16");
        tmp.put("17", "chr17");
        tmp.put("18", "chr18");
        tmp.put("19", "chr19");
        tmp.put("20", "chr20");
        tmp.put("21", "chr21");
        tmp.put("MT", "chrM");
        tmp.put("X", "chrX");
        tmp.put("Y", "chrY");
        tmp.put("chr1", "1");
        tmp.put("chr2", "2");
        tmp.put("chr3", "3");
        tmp.put("chr4", "4");
        tmp.put("chr5", "5");
        tmp.put("chr6", "6");
        tmp.put("chr7", "7");
        tmp.put("chr8", "8");
        tmp.put("chr9", "9");
        tmp.put("chr10", "10");
        tmp.put("chr11", "11");
        tmp.put("chr12", "12");
        tmp.put("chr13", "13");
        tmp.put("chr14", "14");
        tmp.put("chr15", "15");
        tmp.put("chr16", "16");
        tmp.put("chr17", "17");
        tmp.put("chr18", "18");
        tmp.put("chr19", "19");
        tmp.put("chr20", "20");
        tmp.put("chr21", "21");
        tmp.put("chrM", "MT");
        tmp.put("chrX", "X");
        tmp.put("chrY", "Y");
        MAPPING = Collections.unmodifiableMap(tmp);
    }
    
    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("1", "chr1");
        tmp.put("2", "chr2");
        tmp.put("3", "chr3");
        tmp.put("4", "chr4");
        tmp.put("5", "chr5");
        tmp.put("6", "chr6");
        tmp.put("7", "chr7");
        tmp.put("8", "chr8");
        tmp.put("9", "chr9");
        tmp.put("10", "chr10");
        tmp.put("11", "chr11");
        tmp.put("12", "chr12");
        tmp.put("13", "chr13");
        tmp.put("14", "chr14");
        tmp.put("15", "chr15");
        tmp.put("16", "chr16");
        tmp.put("17", "chr17");
        tmp.put("18", "chr18");
        tmp.put("19", "chr19");
        tmp.put("20", "chr20");
        tmp.put("21", "chr21");
        tmp.put("MT", "chrM");
        tmp.put("X", "chrX");
        tmp.put("Y", "chrY");
        B37_TO_UCSC = Collections.unmodifiableMap(tmp);
    }
    
    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("chr1", "1");
        tmp.put("chr2", "2");
        tmp.put("chr3", "3");
        tmp.put("chr4", "4");
        tmp.put("chr5", "5");
        tmp.put("chr6", "6");
        tmp.put("chr7", "7");
        tmp.put("chr8", "8");
        tmp.put("chr9", "9");
        tmp.put("chr10", "10");
        tmp.put("chr11", "11");
        tmp.put("chr12", "12");
        tmp.put("chr13", "13");
        tmp.put("chr14", "14");
        tmp.put("chr15", "15");
        tmp.put("chr16", "16");
        tmp.put("chr17", "17");
        tmp.put("chr18", "18");
        tmp.put("chr19", "19");
        tmp.put("chr20", "20");
        tmp.put("chr21", "21");
        tmp.put("chrM", "MT");
        tmp.put("chrX", "X");
        tmp.put("chrY", "Y");
        UCSC_TO_B37 = Collections.unmodifiableMap(tmp);
    }
}