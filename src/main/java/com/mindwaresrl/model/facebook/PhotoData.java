
package com.mindwaresrl.model.facebook;

import java.util.ArrayList;
import java.util.List;

public class PhotoData {
    private final List<ReactionData> reactions = new ArrayList<>();
    private final List<CommentData> comments = new ArrayList<>();
    private final List<String> rawAjaxBodies = new ArrayList<>();

    public void addReaction(ReactionData r) { reactions.add(r); }
    public void addComment(CommentData c) { comments.add(c); }
    public void addRawBody(String s) { rawAjaxBodies.add(s); }

    public List<ReactionData> getReactions() { return reactions; }
    public List<CommentData> getComments() { return comments; }
    public List<String> getRawAjaxBodies() { return rawAjaxBodies; }
}