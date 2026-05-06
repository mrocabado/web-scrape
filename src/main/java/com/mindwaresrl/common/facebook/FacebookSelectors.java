package com.mindwaresrl.common.facebook;

public final class FacebookSelectors {

    private FacebookSelectors() {}

    // --- AJAX ---
    public static final String[] AJAX_URL_PATTERNS = {
            "api/graphql", "ajax/bz", "reaction/profile", "ufi/reaction", "comet_dialog"
    };
    public static final String[] BODY_KEYWORDS = {
            "feedback", "comment", "reaction", "like_count", "top_reactions"
    };


    public static final String POST_SCOPE          = "div[role='main'], div[data-pagelet='MediaViewerPhoto']";


    public static final String POST_REACTIONS_LABEL = "div[aria-label*=': '], span[aria-label*=': ']";
    public static final String POST_REACTIONS_TOTAL = "span:has-text('Todas las reacciones')";


    public static final String COMMENT_ARTICLE =
            "div[role='article'][aria-label*='Comentario'], " +
                    "div[role='article'][aria-label*='Respuesta']";
    public static final String LOAD_MORE_COMMENTS  = "div[role='button']:has-text('Ver más comentarios'), div[role='button']:has-text('Ver más')";
    public static final String SHOW_REPLIES        = "div[role='button']:has-text('respuesta')";


    public static final String COMMENT_AUTHOR_LINK = "a[role='link'][href*='/']";
    public static final String COMMENT_TEXT_DIVS   = "div[dir='auto']";
    public static final String COMMENT_TIME_LINK   = "a[href*='comment_id']";
    public static final String COMMENT_REACTIONS   = "span[aria-label*='reacci'], div[aria-label*='reacci']";


    public static final String REPLY_TEXT_REGEX    = "Ver (las )?\\d+ respuesta(s)?";

    public static final String POST_REACTION_REGEX = "^(Me gusta|Me encanta|Me divierte|Me asombra|Me entristece|Me enoja|Me importa)\\s*:\\s*\\d+.*";


    public static final int    REACTION_WAIT_MS    = 8000;
    public static final int    AFTER_LOAD_MS       = 2500;
    public static final int    AFTER_REPLY_MS      = 1500;
    public static final int    MAX_LOAD_CLICKS     = 20;
}