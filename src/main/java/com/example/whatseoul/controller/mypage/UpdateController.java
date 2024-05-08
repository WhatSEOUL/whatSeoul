package com.example.whatseoul.controller.mypage;

import com.example.whatseoul.entity.User;
import com.example.whatseoul.respository.user.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UpdateController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UpdateController(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @GetMapping("/api/user/update")
    public String getMyPage(Model model) {
        // 기존 로직 ...
        return"/update";
    }

//    @GetMapping("/api/user/update")
//    public String getMyUpdatePage(Model model, Authentication authentication) {
//
//        return "update";
//    }


    @PostMapping("/api/user/update")
    public String updateUserInfo(@RequestParam("email") String email,
                                 @RequestParam("name") String name,
                                 @RequestParam("password") String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setUserEmail(email);
        user.setUserName(name);
        user.setUserPassword(encoder.encode(password));        // 실제 애플리케이션에서는 패스워드를 암호화해야 합니다.
        userRepository.save(user);

        return "redirect:/api/user/me";
    }

    @PostMapping("/api/user/delete")
    public String deleteUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user); // 데이터베이스에서 사용자 정보 삭제
        return "redirect:/login"; // 로그인 페이지로 리디렉션
    }


}
