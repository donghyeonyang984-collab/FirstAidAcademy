package com.emergency.user.web.form;

/**
 * 로그인 폼 DTO
 * login.html 의 th:object="${loginForm}" 와 매핑
 */
public class LoginForm {

    // 아이디 (users.username)
    private String username;

    // 비밀번호 (users.password_plain)
    private String password;

    public LoginForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
