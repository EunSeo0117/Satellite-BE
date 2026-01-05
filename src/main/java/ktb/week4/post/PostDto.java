package ktb.week4.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ktb.week4.comment.Comment;
import ktb.week4.post.postImage.PostImage;
import ktb.week4.post.postView.PostView;
import ktb.week4.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PostDto {
    public record PostRequest(
            @NotBlank(message = "제목은 필수입니다.")
            @Size(max = 26, message = "제목은 100자 이내로 입력해주세요.")
            String postTitle,
            @NotBlank(message = "제목, 내용을 모두 작성해주세요.")
            @Size(max = 5000, message = "본문은 5000자 이내로 입력해주세요.")
            String postContent,
            List<MultipartFile> files
    ) {}

    public record PostOverviewResponse(
            Long postId,
            AuthorResponse user,
            String title,
            Integer likeCount,
            Integer commentCount,
            Integer viewCount,
            Instant createdAt
    ) {
        public static PostOverviewResponse of(Post post, PostView postView) {
            return new PostOverviewResponse(
                    post.getId(),
                    AuthorResponse.of(post.getUser()),
                    post.getPostTitle(),
                    postView.getLikeCount(),
                    postView.getCommentCount(),
                    postView.getViewCount(),
                    post.getCreatedAt()
            );
        }
    }

    public record PostDetailResponse(
            Long postId,
            AuthorResponse user,
            String title,
            String content,
            List<PostImageResponse> postImages,
            List<CommentResponse> comments,
            Integer viewCount,
            Integer commentCount,
            Integer likesCount,
            Instant createdAt
    ) {
        public static PostDetailResponse of(Post post, PostView postView, List<PostImageResponse> postImagesResponses, List<CommentResponse> commentResponses) {
            return new PostDetailResponse(
                    post.getId(),
                    AuthorResponse.of(post.getUser()),
                    post.getPostTitle(),
                    post.getPostContent(),
                    postImagesResponses,
                    commentResponses,
                    postView.getViewCount(),
                    postView.getCommentCount(),
                    postView.getLikeCount(),
                    post.getCreatedAt()
            );
        }
    }

    public record PostImageResponse(
            Integer sortOrder,
            String imageUrl
    ) {
        public static PostImageResponse of(PostImage postImage) {
            return new PostImageResponse(
                    postImage.getSortOrder(),
                    postImage.getImage().getFileUrl()
            );
        }
    }

    public record CommentResponse(
            Long commentId,
            AuthorResponse user,
            String commentContent,
            Instant createdAt
    ) {
        public static CommentResponse of(Comment comment) {
            return new CommentResponse(
                    comment.getId(),
                    AuthorResponse.of(comment.getUser()),
                    comment.getCommentContent(),
                    comment.getCreatedAt()
            );
        }
    }

    public record AuthorResponse(
            Long id,
            String nickName,
            String imageUrl
    ) {
        public static AuthorResponse of(User user) {
            return new AuthorResponse(
                    user.getId(),
                    user.getNickName(),
                    user.getProfileImg().getFileUrl()
            );
        }
    }
}
