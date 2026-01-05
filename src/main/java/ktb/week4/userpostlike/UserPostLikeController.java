package ktb.week4.userpostlike;

import ktb.week4.user.CurrentUser;
import ktb.week4.user.User;
import ktb.week4.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userlikes/{postId}")
@RequiredArgsConstructor
public class UserPostLikeController {
    private final UserPostLikeService userPostLikeService;

    @GetMapping
    public ResponseEntity<Boolean> getUserPostLike(@PathVariable Long postId,
                                                   @CurrentUser User user) {
        Boolean isPostLike = userPostLikeService.getUserPostLike(postId, user);
        return ResponseEntity.ok(isPostLike);
    }

    @PostMapping
    public ResponseEntity<?> addLike(@PathVariable Long postId,
                                                @CurrentUser User user) {

        userPostLikeService.addLike(postId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> removeLike(@PathVariable Long postId,
                                                @CurrentUser User user) {
        userPostLikeService.removeLike(postId, user);
        return ResponseEntity.ok().build();
    }


}
