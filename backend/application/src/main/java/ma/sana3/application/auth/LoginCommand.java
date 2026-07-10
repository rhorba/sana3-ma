package ma.sana3.application.auth;

public record LoginCommand(String email, String rawPassword) {}
