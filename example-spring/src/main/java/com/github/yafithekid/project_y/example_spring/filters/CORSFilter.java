package com.github.yafithekid.project_y.example_spring.filters;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Message, Total-Count, Current-Page, Total-Pages, Content-Size, Content-Range, Total-Outstanding");
        response.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-type");
        filterChain.doFilter(request, response);
    }
}
