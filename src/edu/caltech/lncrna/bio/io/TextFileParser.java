package edu.caltech.lncrna.bio.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.zip.GZIPInputStream;

public abstract class TextFileParser<T> extends FileParser<T> {
    
    protected final BufferedReader br;
    protected T next;
    
    public TextFileParser(Path p) {
        super(p);
        try {
            PathMatcher matcher =
                    FileSystems.getDefault().getPathMatcher("glob:**.gz");
            if (matcher.matches(p)) {
                InputStream fis = Files.newInputStream(p);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                InputStreamReader isr =
                        new InputStreamReader(gzis, StandardCharsets.US_ASCII);
                br = new BufferedReader(isr);
            } else {
                br = Files.newBufferedReader(p, StandardCharsets.US_ASCII);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T next() {
        T rtrn = next;
        findNext();
        return rtrn;
    }
    
    @Override
    public boolean hasNext() {
        return next != null;
    }
    
    @Override
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected abstract void findNext();
}