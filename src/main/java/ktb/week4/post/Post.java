package ktb.week4.post;

import jakarta.persistence.*;
import ktb.week4.comment.Comment;
import ktb.week4.post.postImage.PostImage;
import ktb.week4.util.BaseEntity;
import ktb.week4.post.postView.PostView;
import ktb.week4.user.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 26)
    private String postTitle;

    @Column(length = 5000, columnDefinition = "TEXT")
    private String postContent;

    @OneToOne(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private PostView postView;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    @Builder
    private Post(User user, String postTitle, String postContent) {
        this.user = user;
        this.postTitle = postTitle;
        this.postContent = postContent;
    }

    protected void updatePostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    protected void updatePostContent(String postContent) {
        this.postContent = postContent;
    }
}
