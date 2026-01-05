package ktb.week4.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /** server **/
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알수없는 서버 에버가 발생했습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다."),

    /** user **/
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을수 없습니다."),
    //FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_RESOURCE_OWNER(HttpStatus.FORBIDDEN, "리소스 소유자가 아니므로 접근할 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),


    /** post **/
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    /** postView **/
    POST_VIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글 뷰를 찾을 수 없습니다."),


    /** comment **/
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),


    /** postImage **/
    POST_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글 이미지를 찾을 수 없습니다."),


    /** image **/
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다."),
    INVALID_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "허용되지 않은 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기가 제한(10MB)을 초과했습니다."),
    INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "잘못된 파일 경로입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    /** userPostLike **/
    USER_POST_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "유저의 좋아요를 찾을 수 없습니다."),
    SELF_LIKE_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "본인게시글에 좋아요를 누를수없습니다.");


    private final HttpStatus status;
    private final String message;




}
