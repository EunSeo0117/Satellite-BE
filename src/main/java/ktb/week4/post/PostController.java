package ktb.week4.post;


import jakarta.validation.Valid;
import ktb.week4.user.CurrentUser;
import ktb.week4.post.postImage.PostImageService;
import ktb.week4.post.postView.PostViewService;
import ktb.week4.user.User;
import ktb.week4.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ktb.week4.post.PostDto.*;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostImageService postImageService;

    @GetMapping
    public ResponseEntity<Page<PostOverviewResponse>> getAllPosts(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostOverviewResponse> responsePage = postService.getAllPosts(pageable);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId) {

        PostDetailResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Long> uploadPost(@Valid @ModelAttribute PostRequest request,
                                           @CurrentUser User user) {

        Long postId = postService.uploadPost(request, user);
        return ResponseEntity.ok(postId);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @Valid @ModelAttribute PostRequest request,
                                        @CurrentUser User user) {

        Long response = postService.updatePost(postId, request, user);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @CurrentUser User user) {

        postService.deletePost(postId, user);
        return ResponseEntity.ok().build();
    }
}

