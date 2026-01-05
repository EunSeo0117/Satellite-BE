package ktb.week4.post;

import ktb.week4.comment.Comment;
import ktb.week4.comment.CommentRepository;
import ktb.week4.comment.CommentService;
import ktb.week4.image.Image;
import ktb.week4.post.postImage.PostImage;
import ktb.week4.post.postImage.PostImageRepository;
import ktb.week4.post.postImage.PostImageService;
import ktb.week4.post.postView.PostView;
import ktb.week4.post.postView.PostViewRepository;
import ktb.week4.post.postView.PostViewService;
import ktb.week4.user.User;
import ktb.week4.image.ImageRepository;
import ktb.week4.user.UserService;
import ktb.week4.util.exception.CustomException;
import ktb.week4.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ktb.week4.post.PostDto.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;
    private final CommentRepository commentRepository;
    private final PostImageRepository postImageRepository;
    private final PostImageService postImageService;
    private final PostViewService postViewService;

    @Transactional(readOnly = true)
    public Page<PostOverviewResponse> getAllPosts(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        List<Long> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();

        List<PostView> postViews = postViewRepository.findAllById(postIds);

        Map<Long, PostView> postViewMap = postViews.stream()
                .collect(Collectors.toMap(pv -> pv.getPost().getId(), pv -> pv));

        List<PostOverviewResponse> responseList = postPage.getContent().stream()
                .map(post -> {
                    PostView postView = postViewMap.get(post.getId());
                    return PostOverviewResponse.of(post, postView);
                })
                .toList();


        return new PageImpl<>(responseList, pageable, postPage.getTotalElements());

    }

    @Transactional
    public PostDetailResponse getPost(Long postId) {
        Post post = getPostById(postId);
        PostView postView = postViewRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<PostImage> postImages = postImageRepository.findAllById_PostId(postId);
        List<PostImageResponse> postImagesResponses = new ArrayList<>();
        for (PostImage postImage : postImages) {
            PostImageResponse response = PostImageResponse.of(postImage);
            postImagesResponses.add(response);
        }

        List<Comment> comments = commentRepository.findAllByPost_Id(postId);
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse respone = CommentResponse.of(comment);
            commentResponses.add(respone);
        }

        postViewService.updateViewsCount(postId);
        return PostDetailResponse.of(post, postView, postImagesResponses, commentResponses);
    }

    @Transactional
    public Long uploadPost(PostRequest request, User user) {
        Post post = createPost(user, request.postTitle(), request.postContent());
        createPostView(post);

        if (request.files() != null) {
            postImageService.createPostImages(post.getId(), request.files());
        }

        return post.getId();

    }

    @Transactional
    public Long updatePost(Long postId, PostRequest request, User user) {
        Post post = getPostById(postId);
        validateUser(post, user);

        // 이미지 수정
        if (request.files() != null &&  !request.files().isEmpty()) {
            postImageService.updatePostImages(postId, request.files());
        }

        // 제목 본문 수정
        if (request.postTitle() != null) {
            post.updatePostTitle(request.postTitle());
        }

        if (request.postContent() != null) {
            post.updatePostContent(request.postContent());
        }

        postRepository.save(post);

        return post.getId();
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = getPostById(postId);
        validateUser(post, user);

        postRepository.delete(post); // cascade
    }

    private Post createPost(User user, String title, String content) {
        Post post = Post.builder()
                .user(user)
                .postTitle(title)
                .postContent(content)
                .build();
        postRepository.save(post);
        return post;
    }

    private void createPostView(Post post) {
        PostView postView = PostView.builder()
                .post(post)
                .build();
        postViewRepository.save(postView);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void validateUser(Post post, User user) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NOT_RESOURCE_OWNER);
        }
    }

}
