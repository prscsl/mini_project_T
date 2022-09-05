package com.sparta.mini_project01.service;

import com.sparta.mini_project01.domain.Member;
import com.sparta.mini_project01.domain.UserDetailsImpl;
import com.sparta.mini_project01.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
    Optional<Member> member = memberRepository.findByNickname(nickname);
    return member
        .map(UserDetailsImpl::new)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));


//    Member member = memberRepository.findByNickname(nickname).orElseThrow(
//            () -> new RuntimeException("Not found Account")
//    );
//    UserDetailsImpl userDetails = new UserDetailsImpl();
//    userDetails.setMember(member);
//    return userDetails;

  }

}
