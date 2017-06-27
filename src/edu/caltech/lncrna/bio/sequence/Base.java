package edu.caltech.lncrna.bio.sequence;

/**
 * An enumeration of bases.
 */
public enum Base {

    A {
        public Base complement() {
            return Base.T;
        }
    },
    
    C {
        public Base complement() {
            return Base.G;
        }
    },
    
    G {
        public Base complement() {
            return Base.C;
        }
    },
    
    N {
        public Base complement() {
            return this;
        }
    },
    
    T {
        public Base complement() {
            return Base.A;
        }
    },
    
    CONFLICTING {
        public Base complement() {
            return this;
        }
    },
    
    INVALID {
        public Base complement() {
            return this;
        }
    };
    
    /**
     * Returns the <code>Base</code> represented by the passed
     * <code>char</code>.
     * <p>
     * This method will return <code>Base.INVALID</code> if passed a
     * <code>char</code> which is not one of "<code>ACGTNacgtn</code>".
     * 
     * @param c - the <code>char</code> to convert
     * @return the corresponding <code>Base</code>
     */
    public static Base of(char c) {
        switch (c) {
        case 'A':
        case 'a':
            return A;
        case 'C':
        case 'c':
            return C;
        case 'G':
        case 'g':
            return G;
        case 'N':
        case 'n':
            return N;
        case 'T':
        case 't':
            return T;
        default: return INVALID;
        }
    }
    
    /**
     * Returns the complement of this base.
     * <p>
     * <ul>
     * <li><code>Base.A.complement() == Base.T</code>
     * <li><code>Base.C.complement() == Base.G</code>
     * <li><code>Base.G.complement() == Base.C</code>
     * <li><code>Base.T.complement() == Base.A</code>
     * <li><code>Base.N.complement() == Base.N</code>
     * <li><code>Base.CONFLICTING.complement() == Base.CONFLICTING</code>
     * <li><code>Base.INVALID.complement() == Base.INVALID</code>
     * </ul>
     * @return the complement of this base
     */
    public abstract Base complement();
}
