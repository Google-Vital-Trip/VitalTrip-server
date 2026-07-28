package com.vitaltrip.vitaltrip.application.admin;

import com.vitaltrip.vitaltrip.presentation.admin.dto.AdminUserResponse;
import com.vitaltrip.vitaltrip.presentation.admin.dto.UserPageResponse;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;

    public UserPageResponse getAllUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userPage = userRepository.findAll(pageable);

        Page<AdminUserResponse> responsePage = userPage.map(AdminUserResponse::from);

        log.info("사용자 목록 조회 완료 - 총 {}명, {}페이지 중 {}페이지",
            userPage.getTotalElements(), userPage.getTotalPages(), page + 1);

        return UserPageResponse.from(responsePage);
    }
}
