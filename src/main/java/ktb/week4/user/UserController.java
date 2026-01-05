package ktb.week4.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ktb.week4.util.exception.CustomException;
import ktb.week4.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static ktb.week4.user.UserDto.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> signup(@Valid @ModelAttribute SignUpRequest request) {

        Long userId = userService.signUp(request);
        return ResponseEntity.ok(userId);
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUser(@CurrentUser User user) {
        UserResponse response = userService.getUsers(user);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<?> updateProfileImage(@RequestParam("file") MultipartFile file,
                                                @CurrentUser User user) {
        userService.updateProfileImage(file, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/nickname")
    public ResponseEntity<?> updateNickname(@Valid @RequestBody nickNameUpdateRequest request,
                                            @CurrentUser User user) {
        userService.updateNickname(request, user);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody passwordUpdateRequest request,
                                            @CurrentUser User user) {

        userService.updatePassword(request, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@CurrentUser User user) {
        userService.deleteUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletResponse response) {
        if (refreshToken == null) {
            userService.deleteCookies(response);
            response.setStatus(401);
            return Map.of("error", "Refresh Token Missing");
        }

        try {
            var toeknResponse = userService.refreshToken(refreshToken, response);

            if (toeknResponse == null) {
                response.setStatus(401);
                return Map.of("error", "Refresh token invalid or expired");
            }

            return Map.of(
                    "accessToken", toeknResponse.accessToken(),
                    "refreshToken", toeknResponse.refreshToken()
            );
        } catch (ResponseStatusException exception) {
            response.setStatus(401);
            return Map.of("error", "Refresh token invalid or expired");
        }
    }


}
