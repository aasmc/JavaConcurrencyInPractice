package ru.aasmc.javaconcurrency.chapter_03.immutable_holder;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

public class VolatileCacheFactorizer implements Servlet {

    /**
     * Mutable volatile reference to immutable class ensures that our [VolatileCacheFactorizer] class
     * is Thread-safe without locking.
     */
    private volatile OneValueCache cache =
            new OneValueCache(null, null);

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = cache.getFactors(i);
        if (factors == null) {
            factors = factor(i);
            cache = new OneValueCache(i, factors);
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
