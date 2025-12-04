package com.emergency.user.service;

import com.emergency.user.domain.TermsAgreement;
import com.emergency.user.domain.User;
import com.emergency.user.repository.TermsAgreementRepository;
import com.emergency.user.repository.UserRepository;
import com.emergency.user.web.dto.UserDto;
import com.emergency.user.web.form.JoinForm;
import com.emergency.user.web.form.UserProfileForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TermsAgreementRepository termsAgreementRepository;

    private static final DateTimeFormatter BIRTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** 아이디 중복 확인 */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /** 로그인 */
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsernameAndPasswordPlain(username, password);
    }

    /** 회원가입 */
    public User registerUser(JoinForm form, String termsVersion) {
        LocalDate birthdate;
        try {
            birthdate = LocalDate.parse(form.getBirth(), BIRTH_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("생년월일 형식이 올바르지 않습니다. (예: 20051107)");
        }

        String genderCode = switch (form.getGender()) {
            case "남자" -> "M";
            case "여자" -> "F";
            default -> "N";
        };

        User user = new User();
        user.setUsername(form.getUserid());
        user.setPasswordPlain(form.getPassword());
        user.setName(form.getName());
        user.setBirthdate(birthdate);
        user.setGender(genderCode);
        user.setPhone(form.getPhone());
        user.setEmail(form.getEmail());
        user.setAddress(form.getAddress());
        user.setRole("User");

        User saved = userRepository.save(user);

        TermsAgreement agreement = new TermsAgreement();
        agreement.setUserId(saved.getUserId());
        agreement.setTermsVersion(termsVersion);
        termsAgreementRepository.save(agreement);

        return saved;
    }

    /** 아이디 찾기 */
    public Optional<User> findUserByNameAndBirth(String name, String birthStr) {
        LocalDate birth = LocalDate.parse(birthStr, BIRTH_FORMAT);
        return userRepository.findByNameAndBirth(name, birth);
    }

    /** 비밀번호 재설정 */
    public boolean resetPassword(String username, String name, String newPw, String confirmPw) {
        if (newPw == null || confirmPw == null || !newPw.equals(confirmPw)) return false;

        Optional<User> userOpt = userRepository.findByUsernameAndName(username, name);
        if (userOpt.isEmpty()) return false;

        userRepository.updatePasswordPlain(userOpt.get().getUserId(), newPw);
        return true;
    }
    /* 회원정보 수정 */
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public void updateProfile(Long userId, UserProfileForm form) {
        // 비밀번호 변경이 있을 때만
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            userRepository.updatePasswordPlain(userId, form.getNewPassword());
        }
        userRepository.updateProfileInfo(
                userId,
                form.getPhone(),
                form.getEmail(),
                form.getAddress(),
                form.getGender()
        );
    }
    public boolean checkCurrentPassword(Long userId, String currentPassword) {
        return userRepository.findById(userId)
                .map(user -> currentPassword != null &&
                        currentPassword.equals(user.getPasswordPlain()))
                .orElse(false);
    }
    public UserDto findUserDtoById(Long userId) {
        return userRepository.findById(userId)
                .map(u -> {
                    UserDto dto = new UserDto();
                    dto.setUserId(u.getUserId());
                    dto.setUsername(u.getUsername());
                    dto.setName(u.getName());
                    dto.setRole(u.getRole());
                    return dto;
                })
                .orElse(null);
    }
}
