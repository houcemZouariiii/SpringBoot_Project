package com.iit.projetjee.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper pour HttpServletRequest qui nettoie automatiquement les paramètres
 * pour prévenir les attaques XSS
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        
        String[] encodedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            encodedValues[i] = XssFilter.sanitize(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return XssFilter.sanitize(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return XssFilter.sanitize(value);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);
        if (headers == null) {
            return null;
        }
        
        return Collections.enumeration(
            Collections.list(headers).stream()
                .map(XssFilter::sanitize)
                .toList()
        );
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> originalMap = super.getParameterMap();
        Map<String, String[]> sanitizedMap = new HashMap<>();
        
        for (Map.Entry<String, String[]> entry : originalMap.entrySet()) {
            String[] values = entry.getValue();
            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = XssFilter.sanitize(values[i]);
            }
            sanitizedMap.put(entry.getKey(), sanitizedValues);
        }
        
        return Collections.unmodifiableMap(sanitizedMap);
    }
}
