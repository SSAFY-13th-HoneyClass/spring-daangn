package com.ssafy.daangn.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comments")
public class Comment {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isDeleted = false;

    public Comment() {
    }

    public Comment(Post post, User user, Comment parent, String content) {
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.content = content;
    }

    public Long getCommentId() {
        return commentId;
    }
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    public Post getPost() {
        return post;
    }
    public void setPost(Post post) {
        this.post = post;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Comment getParent() {
        return parent;
    }
    public void setParent(Comment parent) {
        this.parent = parent;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment other = (Comment) o;
        return Objects.equals(commentId, other.commentId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }
    @Override
    public String toString() {
        return "Comment{" +
               "commentId=" + commentId +
               ", post=" + (post != null ? post.getPostId() : null) +
               ", user=" + (user != null ? user.getUserId() : null) +
               ", parent=" + (parent != null ? parent.getCommentId() : null) +
               ", content='" + content + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", isDeleted=" + isDeleted +
               '}';
    }
}
