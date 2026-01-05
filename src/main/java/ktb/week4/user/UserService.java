package ktb.week4.user;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ktb.week4.Login.Cookie.CookieUtil;
import ktb.week4.Login.Jwt.JwtUtil;
import ktb.week4.Login.RefreshToken.RefreshToken;
import ktb.week4.Login.RefreshToken.RefreshTokenRepository;
import ktb.week4.Login.RefreshToken.TokenResponse;
import ktb.week4.image.Image;
import ktb.week4.image.ImageService;
import ktb.week4.util.exception.CustomException;
import ktb.week4.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Arrays;

import static ktb.week4.user.UserDto.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final int ACCESS_TOKEN_EXPIRATION = 15 * 60; // 15분
    private static final int REFRESH_TOKEN_EXPIRATION = 14 * 24 * 3600; // 14일
    private final CookieUtil cookieUtil;
    private final Environment environment;


    @Transactional
    public Long signUp(SignUpRequest request) {
        validateEmail(request.email());
        validateNickname(request.nickName());
        validatePassword(request.password(), request.confirmPassword());

        Image image = imageService.uploadImage(request.profileImage());
        User user = createUser(request, image);

        return user.getId();
    }

    @Transactional
    public UserResponse getUsers(User user) {
        return UserResponse.from(user);
    }

    @Transactional
    public void updateProfileImage(MultipartFile file, User user) {
        Image existImage = user.getProfileImg();
        imageService.updateIsDeleted(existImage);

        Image image = imageService.uploadImage(file);
        user.updateProfileImage(image);

        userRepository.save(user);
    }

    @Transactional
    public void updateNickname(nickNameUpdateRequest request, User user) {
        validateNickname(request.nickName());

        user.updateNickName(request.nickName());
        userRepository.save(user);

        log.info("닉네임 변경 완료.");
    }

    @Transactional
    public void updatePassword(passwordUpdateRequest request, User user) {
        validatePassword(request.password(), request.confirmPassword());

        user.updatePassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        log.info("비밀번호 변경 완료.");
    }

    @Transactional
    public void deleteUser(User user) {
        user.updateIsDeleted();
    }


    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void validateNickname(String nickname) {
        if (userRepository.existsByNickName(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    private void validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    public User createUser(SignUpRequest request, Image image) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickName(request.nickName())
                .profileImg(image)
                .role("USER")
                .build();
        userRepository.save(user);
        return user;
    }

    public TokenResponse refreshToken(String refreshToken, HttpServletResponse response) {
        var parsedRefreshToken = jwtUtil.parse(refreshToken);

        RefreshToken entity = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken)
                .orElse(null);

        if (entity == null || entity.getExpiresAt().isBefore(Instant.now())) {
            deleteCookies(response);
            return null;
        }

        Long userId = Long.valueOf(parsedRefreshToken.getBody().getSubject());
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        // refresh token은 유지하고 access token만 새로 발급
        String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());

        // access token 쿠키만 갱신
        ResponseCookie accessCookie = cookieUtil.addCookie("accessToken", newAccessToken, ACCESS_TOKEN_EXPIRATION);
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return new TokenResponse(newAccessToken, refreshToken);
    }

    public void deleteCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = cookieUtil.deleteCookie("accessToken");
        ResponseCookie clearRefresh = cookieUtil.deleteCookie("refreshToken");
        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
    }


}
