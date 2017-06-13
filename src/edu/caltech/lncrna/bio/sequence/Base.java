package edu.caltech.lncrna.bio.sequence;

public enum Base {
    A, C, G, N, T, CONFLICTING, INVALID;

    public static Base of(char c) {
        switch (c) {
        case 'A': return A;
        case 'C': return C;
        case 'G': return G;
        case 'N': return N;
        case 'T': return T;
        default: return INVALID;
        }
    }
}
