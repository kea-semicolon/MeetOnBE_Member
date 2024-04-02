package semicolon.MeetOn.global.OAuth;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();

    String getProfileImage();
    OAuthProvider getOAuthProvider();
}
