package cc.novoline.utils.Microsoft.oauth;

public interface LoginCallback {
    void run(String username, String uuid, String access_token, boolean success);
}