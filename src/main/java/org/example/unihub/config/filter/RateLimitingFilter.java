package org.example.unihub.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, AtomicInteger> requestCountPerIpAddress = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 1000;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String clientIpAddress = httpServletRequest.getRemoteAddr();
        requestCountPerIpAddress.putIfAbsent(clientIpAddress, new AtomicInteger(0));
        AtomicInteger requestCount = requestCountPerIpAddress.get(clientIpAddress);

        int requests = requestCount.incrementAndGet();
        if (requests > MAX_REQUESTS_PER_MINUTE) {
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write("Too many requests. Please try again later.");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    public void resetCounts() {
        requestCountPerIpAddress.clear();
    }
} 