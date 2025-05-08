package com.ssafy.daangn.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "posts")
public class Post {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isDeleted = false;

    public Post() {
    }

    public Post(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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
        if (!(o instanceof Post)) return false;
        Post other = (Post) o;
        return Objects.equals(postId, other.postId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }
    @Override
    public String toString() {
        return "Post{" +
               "postId=" + postId +
               ", user=" + (user != null ? user.getUserId() : null) +
               ", title='" + title + '\'' +
               ", content='" + content + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               ", isDeleted=" + isDeleted +
               '}';
    }
}
