
package com.mindwaresrl.model.facebook;

public record CommentData(
        String author,
        String text,
        String time,
        String reactionCount,
        boolean isReply
) {

    public CommentData(String author, String text, String time, String reactionCount) {
        this(author, text, time, reactionCount, false);
    }
}