package com.msf.filter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;

/**
 * LESS compile {@link Filter}.
 */
public class LessFilter implements Filter {
    private ServletContext servletContext;
    private LessEngine engine;
    protected Map<String, Long> lastModifieds = new HashMap<String, Long>();
    protected Map<String, String> cache = new HashMap<String, String>();
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException{
        servletContext = filterConfig.getServletContext();
        engine = new LessEngine();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException{
        try{
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        }catch(Exception e){
            e.printStackTrace();
            chain.doFilter(request, response);
        }
    }
    
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws Exception{
        String css = compile(request.getServletPath());
        if(css != null){
            response.setContentType("text/css;charset=UTF-8");
            response.getWriter().write(css);
        }else{
            chain.doFilter(request, response);
        }
    }
    
    protected String compile(String path) throws LessException{
        path = servletContext.getRealPath(path.replace(".css", ".less"));
        File file = new File(path);
        if(file.exists()){
            Long lastModified = lastModifieds.get(path);
            if(lastModified == null || lastModified < file.lastModified()){
                lastModifieds.put(path, file.lastModified());
                cache.put(path, engine.compile(file, true));
            }
            return cache.get(path);
        }
        return null;
    }
    
    @Override
    public void destroy(){
    }
}
