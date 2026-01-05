package ktb.week4;


import com.mysema.commons.lang.Assert;
import ktb.week4.image.Image;
import ktb.week4.image.ImageService;
import ktb.week4.user.User;
import ktb.week4.user.UserDto;
import ktb.week4.user.UserRepository;
import ktb.week4.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static ktb.week4.user.UserDto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock private UserRepository userRepository;

    @Mock private ImageService imageService;

    @Mock private PasswordEncoder passwordEncoder;

    // create test
    @Test
    @DisplayName("회원가입")
    public void createUser() {
        // Given
        String email = "test@naver.com";
        String nickname = "test";
        String password = "password@123";
        String confirmPassword = "password@123";
        MultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "".getBytes());
        Image image = Image.builder().build();
        SignUpRequest request = new SignUpRequest(email,password,confirmPassword,nickname, file);

        // When
        User user = userService.createUser(request, image);

        // Then
        assertThat(user.getEmail()).isEqualTo("test@naver.com");
        assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(password));
        assertThat(user.getNickName()).isEqualTo(nickname);


    }

    // changePassword
    @DisplayName("비밀번호 변경")
    @Test
    public void changePassword() {
        // given
        passwordUpdateRequest request = new UserDto.passwordUpdateRequest("change@123", "change@123");
        User user = User.builder()
                .email("test@naver.com")
                .password("password@123")
                .nickName("test")
                .build();
        // when
        given(passwordEncoder.encode("change@123")).willReturn("change@123");
        userService.updatePassword(request, user);

        // then
        assertThat(user.getPassword()).isEqualTo("change@123");
    }

}

