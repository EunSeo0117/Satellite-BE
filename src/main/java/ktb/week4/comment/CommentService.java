package ktb.week4.comment;


import ktb.week4.post.Post;
import ktb.week4.post.PostRepository;
import ktb.week4.post.postImage.PostImageService;
import ktb.week4.post.postView.PostViewService;
import ktb.week4.user.User;
import ktb.week4.post.PostService;
import ktb.week4.user.UserService;
import ktb.week4.util.exception.CustomException;
import ktb.week4.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ktb.week4.comment.CommentDto.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostViewService postViewService;

    @Transactional
    public void uploadComment(Long postId, CommentCreateRequest request, User user) {
        Post post = getPostById(postId);
        createComment(post,user, request.content());

        postViewService.updateCommentCount(postId, false);
    }

    @Transactional
    public void updateComment(Long commentId, CommentUpdateRequest request, User user) {
        validateUser(commentId, user);

        Comment comment = getCommentById(commentId);
        comment.updateContent(request.content());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, User user) {
        validateUser(commentId, user);
        commentRepository.deleteById(commentId);

        postViewService.updateCommentCount(postId, true);

    }

    private void validateUser(Long commentId, User user) {
        Comment comment = getCommentById(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NOT_RESOURCE_OWNER);
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void createComment(Post post, User user, String content) {
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();
        commentRepository.save(comment);
    }
}
