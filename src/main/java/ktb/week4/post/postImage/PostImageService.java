package ktb.week4.post.postImage;

import ktb.week4.image.Image;
import ktb.week4.image.ImageService;
import ktb.week4.post.Post;
import ktb.week4.post.PostRepository;
import ktb.week4.post.PostService;
import ktb.week4.util.exception.CustomException;
import ktb.week4.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private final PostImageRepository postImageRepository;
    private final ImageService imageService;
    private final PostRepository postRepository;

    @Transactional
    public void createPostImages(Long postId, List<MultipartFile> postImages) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));

        int idx = 1;
        for (MultipartFile postImage : postImages) {
            Image image = imageService.uploadImage(postImage);
            createPostImage(post,image,idx);

            idx++;
        }
    }

    private void createPostImage(Post post, Image image, int sortOrder) {
        PostImage postImage = PostImage.builder()
                .post(post)
                .image(image)
                .sortOrder(sortOrder)
                .build();
        postImageRepository.save(postImage);
    }

    @Transactional
    public void updatePostImages(Long postId, List<MultipartFile> postImages) {
        List<PostImage> existImages = getPostImagesByPostId(postId);
        deletePostImages(existImages);

        createPostImages(postId, postImages);
    }

    private void deletePostImages(List<PostImage> existImages) {
        for (PostImage postImage : existImages) {
            Image image = postImage.getImage();
            imageService.updateIsDeleted(image);

            postImageRepository.delete(postImage);
        }
    }

    public List<PostImage> getPostImagesByPostId(Long postId) {
        return postImageRepository.findAllById_PostId(postId);
    }

}
