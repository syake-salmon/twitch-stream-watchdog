package com.syakeapps.twe.filter.web;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = "/*")
public class HttpDumpFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpDumpFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // NOP
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        MultiReadHttpServletRequest httpRequest = new MultiReadHttpServletRequest((HttpServletRequest) request);

        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n----------------------------REQUEST---------------------------");
            sb.append("\n               URI=" + httpRequest.getRequestURI());
            sb.append("\n CharacterEncoding=" + httpRequest.getCharacterEncoding());
            sb.append("\n     ContentLength=" + httpRequest.getContentLength());
            sb.append("\n       ContentType=" + httpRequest.getContentType());
            Collections.list(httpRequest.getHeaderNames()).stream().forEach(name -> {
                sb.append("\n            Header=" + name + "=" + httpRequest.getHeader(name));
            });
            sb.append("\n              Body=" + httpRequest.getReader().lines().collect(Collectors.joining()));
            sb.append("\n            Locale=" + httpRequest.getLocalName());
            sb.append("\n            Method=" + httpRequest.getMethod());
            sb.append("\n          Protocol=" + httpRequest.getProtocol());
            sb.append("\n       QueryString=" + httpRequest.getQueryString());
            sb.append("\n     RemoteAddress=" + httpRequest.getRemoteAddr());
            sb.append("\n        RemoteHost=" + httpRequest.getRemoteHost());
            sb.append("\n            Schema=" + httpRequest.getScheme());
            sb.append("\n              Port=" + httpRequest.getServerPort());
            sb.append("\n--------------------------------------------------------------");
            LOGGER.debug(sb.toString());
        }

        chain.doFilter(httpRequest, response);

        if (LOGGER.isDebugEnabled()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            StringBuilder sb = new StringBuilder();
            sb.append("\n----------------------------RESPONSE---------------------------");
            sb.append("\n       ContentType=" + httpResponse.getContentType());
            httpResponse.getHeaderNames().forEach(name -> {
                sb.append("\n            Header=" + name + "=" + httpResponse.getHeader(name));
            });
            sb.append("\n            Status=" + httpResponse.getStatus());
            sb.append("\n-------------------------------------------------------------- ");
            LOGGER.debug(sb.toString());
        }
    }

    @Override
    public void destroy() {
        // NOP
    }
}
