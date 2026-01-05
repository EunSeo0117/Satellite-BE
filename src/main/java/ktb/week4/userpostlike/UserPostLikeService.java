package ktb.week4.userpostlike;

import ktb.week4.post.Post;
import ktb.week4.post.PostRepository;
import ktb.week4.post.PostService;
import ktb.week4.post.postView.PostViewService;
import ktb.week4.user.User;
import ktb.week4.user.UserService;
import ktb.week4.util.exception.CustomException;
import ktb.week4.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.LongPredicate;

@Service
@RequiredArgsConstructor
public class UserPostLikeService {
    private final UserPostLikeRepository userPostLikeRepository;
    private final PostViewService postViewService;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Boolean getUserPostLike(Long postId, User user) {
        UserPostLikeId userPostLikeId = new UserPostLikeId(user.getId(), postId);
        UserPostLike userPostLike = userPostLikeRepository.findById(userPostLikeId)
                .orElse(null);

        return userPostLike != null;
    }
    @Transactional
    public void addLike(Long postId, User user) {
        Post post = getPostById(postId);

        validateOwner(user, post.getUser());

        createUserPostLike(user, post);
        postViewService.updateLikeCount(postId, false);
    }


    @Transactional
    public void removeLike(Long postId, User user) {
        Post post = getPostById(postId);

        validateOwner(user, post.getUser());

        userPostLikeRepository.deleteByUserAndPost(user, post);
        postViewService.updateLikeCount(postId, true);
    }

    private void validateOwner(User user, User postUser) {
        if (user.getId().equals(postUser.getId())) {
            throw new CustomException(ErrorCode.SELF_LIKE_NOT_ALLOWED);
        }
    }

    private void createUserPostLike(User user, Post post) {
        UserPostLike userPostLike = UserPostLike.builder()
                .user(user)
                .post(post)
                .build();
        userPostLikeRepository.save(userPostLike);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
