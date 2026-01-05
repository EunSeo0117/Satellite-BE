package ktb.week4.comment;

import ktb.week4.user.CurrentUser;
import ktb.week4.post.postView.PostViewService;
import ktb.week4.user.User;
import ktb.week4.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ktb.week4.comment.CommentDto.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public void createComment(@PathVariable Long postId,
                              @RequestBody CommentCreateRequest request,
                              @CurrentUser User user) {

        commentService.uploadComment(postId, request, user);
    }

    @PatchMapping("/comments/{commentId}")
    public void updateComment(@PathVariable Long commentId,
                              @RequestBody CommentUpdateRequest request,
                              @CurrentUser User user) {

        commentService.updateComment(commentId, request, user);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public void deleteComment(@PathVariable Long postId,
                              @PathVariable Long commentId,
                              @CurrentUser User user) {

        commentService.deleteComment(postId, commentId, user);
    }
}
