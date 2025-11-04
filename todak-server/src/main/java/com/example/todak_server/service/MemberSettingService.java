package com.example.todak_server.service;

import com.example.todak_server.dto.UserSettingRequest;
import com.example.todak_server.dto.UserSettingResponse;
import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.UserSetting;
import com.example.todak_server.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingService {

    private final UserSettingRepository userSettingRepository;

    @Transactional(readOnly = true)
    public UserSettingResponse getSetting(Member member) {
        UserSetting setting = userSettingRepository.findByMember(member)
                .orElseGet(() -> userSettingRepository.save(
                        UserSetting.builder().member(member).build()
                ));
        return UserSettingResponse.from(setting);
    }

    @Transactional
    public UserSettingResponse updateSetting(Member member, UserSettingRequest req) {
        UserSetting setting = userSettingRepository.findByMember(member)
                .orElseGet(() -> UserSetting.builder().member(member).build());

        if (req.getEventAlarmMinutes() != null)
            setting.setEventAlarmMinutes(req.getEventAlarmMinutes());
        if (req.getEmotionIntervalMinutes() != null)
            setting.setEmotionIntervalMinutes(req.getEmotionIntervalMinutes());
        if (req.getEmotionActiveStart() != null)
            setting.setEmotionActiveStart(req.getEmotionActiveStart());
        if (req.getEmotionActiveEnd() != null)
            setting.setEmotionActiveEnd(req.getEmotionActiveEnd());

        userSettingRepository.save(setting);
        return UserSettingResponse.from(setting);
    }
}
