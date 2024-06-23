package cc.ccake.forumApp.service;

public interface AuthService {
    String login(String username, String password) throws Exception;

    String getCurrentUsername();
}
