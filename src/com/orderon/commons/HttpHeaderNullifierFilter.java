package com.orderon.commons;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class HttpHeaderNullifierFilter implements Filter {

/**
 * init property name defining the headers names and values to set to null
 */
public static final String HEADERS_PROPERTY = "headers";

/**
 * the names/values separator in the HEADERS_PROPERTY property
 */
public static final String HEADERS_SEPARATOR_PROPERTY = ",";

/**
 * the key-value separator in the HEADERS_PROPERTY property
 */
public static final String HEADERS_KEY_VALUE_SEPARATOR_PROPERTY = "=";

/**
 * the origin-header's names/values to set to null
 */
private Map<String,Set<String>> headersNamesValuesToNullify;

/**
 * the request wrapper. override the specified fields with a null value
 */
public class CustomServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * constructor: wrap the request
     * 
     * @param request
     */
    public CustomServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Check the header value: if the header-names'list contain the
     * specified header, null is returned
     */
    public String getHeader(String headerName) {
        String result = super.getHeader(headerName);
        if (headersNamesValuesToNullify.containsKey(headerName)) {
            if(result != null && headersNamesValuesToNullify.get(headerName).contains(result)){
                return null;
            }
        }
        return result;
    }
}

@Override
public void destroy() {
    // TODO Auto-generated method stub
}

@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
    if (!(servletRequest instanceof HttpServletRequest)
            || !(servletResponse instanceof HttpServletResponse)) {
        throw new ServletException("no HTTP request");
    }
    CustomServletRequestWrapper requestWrapper = new CustomServletRequestWrapper(
            (HttpServletRequest) servletRequest);
    // Forward the request down the filter chain.
    filterChain.doFilter(requestWrapper, servletResponse);
}

@Override
public void init(FilterConfig filterConfig) throws ServletException {
    headersNamesValuesToNullify = new HashMap<String,Set<String>>();
    if (filterConfig != null) {
        String configAllowedOrigins = filterConfig.getInitParameter(HEADERS_PROPERTY);
        if (configAllowedOrigins != null && configAllowedOrigins.length() > 0) {
            if (configAllowedOrigins.indexOf(HEADERS_SEPARATOR_PROPERTY) > 0) {
                for (String value : configAllowedOrigins.split(HEADERS_SEPARATOR_PROPERTY)) {
                    addKeyValueToMap(value, headersNamesValuesToNullify);
                }
            } else {
                addKeyValueToMap(configAllowedOrigins, headersNamesValuesToNullify);
            }
        }
    }
}

/**
 * add the key-value par to the map
 * @param keyValueStringValue the key-pair as one value
 * @param map the map to add the key-pair value
 */
private void addKeyValueToMap(String keyValueStringValue, Map<String, Set<String>> map){
    if(keyValueStringValue != null && keyValueStringValue.indexOf(HEADERS_KEY_VALUE_SEPARATOR_PROPERTY) > 0){
        String[] keyValueSplit = keyValueStringValue.split(HEADERS_KEY_VALUE_SEPARATOR_PROPERTY);
        String key = keyValueSplit[0];
        String value = keyValueSplit[1];
        if(! map.containsKey(key)){
            map.put(key, new HashSet<String>());
        }
        map.get(key).add(value);
    }
}
}
