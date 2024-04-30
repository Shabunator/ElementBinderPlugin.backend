package element.binder.plugin.backend.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;

    private String refreshToken;
}
