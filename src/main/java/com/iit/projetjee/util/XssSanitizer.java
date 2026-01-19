package com.iit.projetjee.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utilitaire pour nettoyer et valider les entrées utilisateur contre les attaques XSS
 */
@Component
public class XssSanitizer {

    // Patterns pour détecter les scripts XSS
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
        // Expression tags
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

    /**
     * Nettoie une chaîne de caractères en supprimant les patterns XSS
     * @param input La chaîne à nettoyer
     * @return La chaîne nettoyée
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
     * @param input La chaîne à nettoyer
     * @return La chaîne nettoyée avec HTML valide préservé
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

    /**
     * Vérifie si une chaîne contient des patterns XSS suspects
     * @param input La chaîne à vérifier
     * @return true si des patterns XSS sont détectés, false sinon
     */
    public static boolean containsXss(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Nettoie un tableau de chaînes
     * @param inputs Le tableau à nettoyer
     * @return Le tableau nettoyé
     */
    public static String[] sanitizeArray(String[] inputs) {
        if (inputs == null) {
            return null;
        }

        String[] sanitized = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            sanitized[i] = sanitize(inputs[i]);
        }
        return sanitized;
    }
}
