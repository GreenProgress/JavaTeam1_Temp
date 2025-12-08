package JavaProject.Backend.service;

import JavaProject.Backend.domain.User;
import JavaProject.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import JavaProject.Backend.repository.AnalysisResultRepository;
import JavaProject.Backend.repository.UserResponseRepository;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AnalysisResultRepository analysisResultRepository;
    private final UserResponseRepository userResponseRepository;
    
    /**
     * 회원가입
     */
    public User register(String userId, String password, String nickname) {
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        
        String passwordHash = passwordEncoder.encode(password);
        
        User user = User.builder()
                .userId(userId)
                .passwordHash(passwordHash)
                .nickname(nickname)
                .roles(Arrays.asList("USER"))
                .active(true)
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * 로그인 인증
     */
    public Optional<User> authenticate(String userId, String password) {
        Optional<User> userOpt = userRepository.findByUserIdAndActiveTrue(userId);
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return Optional.of(user);
        }
        
        return Optional.empty();
    }
    
    /**
     * 회원탈퇴
     */
    public void withdraw(String userId) {
        analysisResultRepository.deleteByUserId(userId);
        userResponseRepository.deleteByUserId(userId);
        userRepository.findByUserId(userId).ifPresent(userRepository::delete);
    }
}