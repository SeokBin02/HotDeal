package challenge18.hotdeal.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String userId;
    private String password;
    private boolean admin;
    private String adminToken;

    public SignupRequest(String userId, String password, boolean admin, String adminToken) {
        this.userId = userId;
        this.password = password;
        this.admin = admin;
        this.adminToken = adminToken;
    }
}
