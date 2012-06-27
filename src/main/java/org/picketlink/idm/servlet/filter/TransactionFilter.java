/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.picketlink.idm.servlet.filter;

import javax.servlet.*;
import org.apache.log4j.Logger;
import org.picketlink.idm.servlet.processor.IdmProcessor;
import org.picketlink.idm.servlet.processor.IdmProcessorFactory;

/**
 *
 * @author vrockai
 */
public class TransactionFilter implements Filter {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(TransactionFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        IdmProcessor idmProc = IdmProcessorFactory.getIdmProcessor();
        log.trace("do filter");
        try {
            idmProc.beginTransaction();
            filterChain.doFilter(request, response);
            idmProc.commitTransaction();            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            log.trace("end filter");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.trace("filter init");
    }

    @Override
    public void destroy() {
        log.trace("filter destroy");
    }
}
