package ru.aasmc.javaconcurrency.chapter_02;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

/**
 * A Thread safe class that uses intrinsic lock, associated with the instance of the class
 * to guard internal mutable state. Mutation of the state occurs in CachedFactorizer.service method
 * in two small synchronized blocks that ensure high level of concurrency, because operations
 * in the blocks are not expensive. We could have made the whole method synchronized, but this
 * would have made it impossible for any two threads to begin executing the method even though
 * shared state of the object is mutated only in a few places.
 */
public class CachedFactorizer implements Servlet {

    private BigInteger lastNumber;
    private BigInteger[] lastFactors;
    private long hits;
    private long cacheHits;

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = null;
        synchronized (this) {
            ++hits;
            if (i.equals(lastNumber)) {
                ++cacheHits;
                factors = lastFactors.clone();
            }
        }
        if (factors == null) {
            factors = factor(i);
            synchronized (this) {
                lastNumber = i;
                lastFactors = factors.clone();
            }
        }
        encodeIntoResponse(res, factors);
    }

    private BigInteger extractFromRequest(ServletRequest req) {
        return BigInteger.ONE;
    }

    private BigInteger[] factor(BigInteger from) {
        return new BigInteger[]{};
    }

    private void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }


}
