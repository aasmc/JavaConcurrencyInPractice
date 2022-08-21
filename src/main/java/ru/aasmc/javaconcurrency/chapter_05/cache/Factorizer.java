package ru.aasmc.javaconcurrency.chapter_05.cache;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

public class Factorizer implements Servlet {

    private final Computable<BigInteger, BigInteger[]> c =
            this::factor;

    private final Computable<BigInteger, BigInteger[]> cache =
            new Memoizer<>(c);

    private BigInteger[] factor(BigInteger num) {
        return new BigInteger[]{
                BigInteger.ONE
        };
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            BigInteger i = extractFromRequest(req);
            encodeIntoResponse(res, cache.compute(i));
        } catch (InterruptedException e) {
            encodeError(res, "Factorization interrupted");
        }
    }

    private void encodeError(ServletResponse res, String reason) {

    }

    private void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {

    }

    private BigInteger extractFromRequest(ServletRequest request) {
        return BigInteger.ONE;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
