package edu.caltech.lncrna.bio.alignment;

import edu.caltech.lncrna.bio.annotation.Annotated;
import edu.caltech.lncrna.bio.sequence.Base;

public interface Alignment extends Annotated, SamRecord {

    public Base getReadBaseFromReferencePosition(String chrom, int pos);
}