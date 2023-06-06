package challenge18.hotdeal.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    private String userId;
    private String password;

    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
