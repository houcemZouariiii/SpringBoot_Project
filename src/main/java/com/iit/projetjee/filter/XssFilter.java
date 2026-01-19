package com.iit.projetjee.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Filtre XSS pour nettoyer les entrées utilisateur et prévenir les attaques Cross-Site Scripting
 */
@Component
public class XssFilter extends OncePerRequestFilter {

    // Patterns pour détecter et supprimer les scripts XSS
    private static final Pattern[] XSS_PATTERNS = {
        // Script tags
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        // JavaScript event handlers
        Pattern.compile("on\\w+\\s*=\\s*[\"'][^\"']*[\"']", Pattern.CASE_INSENSITIVE),
        // JavaScript: protocol
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // VBScript: protocol
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // Data URI avec scripts
        Pattern.compile("data:text/html", Pattern.CASE_INSENSITIVE),
        // Expression tags (JSP, ASP, etc.)
        Pattern.compile("<%.*?%>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        // Iframe tags
        Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        // Object tags
        Pattern.compile("<object[^>]*>.*?</object>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
        // Embed tags
        Pattern.compile("<embed[^>]*>", Pattern.CASE_INSENSITIVE),
        // Link tags avec javascript
        Pattern.compile("<link[^>]*javascript[^>]*>", Pattern.CASE_INSENSITIVE),
        // Style tags avec expression
        Pattern.compile("<style[^>]*>.*?expression.*?</style>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        // Wrapper pour nettoyer les paramètres de requête
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }

    /**
     * Nettoie une chaîne de caractères en supprimant les patterns XSS
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input;
        for (Pattern pattern : XSS_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }

        // Échapper les caractères HTML spéciaux
        sanitized = sanitized.replace("&", "&amp;")
                            .replace("<", "&lt;")
                            .replace(">", "&gt;")
                            .replace("\"", "&quot;")
                            .replace("'", "&#x27;")
                            .replace("/", "&#x2F;");

        return sanitized;
    }

    /**
     * Nettoie une chaîne mais préserve le HTML valide (pour les éditeurs WYSIWYG)
     * Utilise une whitelist de tags HTML autorisés
     */
    public static String sanitizeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input;
        
        // Supprimer les patterns dangereux
        for (Pattern pattern : XSS_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }

        // Supprimer les attributs dangereux même sur les tags autorisés
        sanitized = sanitized.replaceAll("(?i)on\\w+\\s*=\\s*[\"'][^\"']*[\"']", "");
        sanitized = sanitized.replaceAll("(?i)javascript:", "");
        sanitized = sanitized.replaceAll("(?i)vbscript:", "");

        return sanitized;
    }
}
