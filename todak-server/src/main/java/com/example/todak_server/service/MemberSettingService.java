package com.example.todak_server.service;

import com.example.todak_server.dto.MemberSettingRequest;
import com.example.todak_server.dto.MemberSettingResponse;
import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.MemberSetting;
import com.example.todak_server.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberSettingService {

    private final MemberSettingRepository userSettingRepository;

    @Transactional(readOnly = true)
    public MemberSettingResponse getSetting(Member member) {
        MemberSetting setting = userSettingRepository.findByMember(member)
                .orElseGet(() -> userSettingRepository.save(
                        MemberSetting.builder().member(member).build()
                ));
        return MemberSettingResponse.from(setting);
    }

    @Transactional
    public MemberSettingResponse updateSetting(Member member, MemberSettingRequest req) {
        MemberSetting setting = userSettingRepository.findByMember(member)
                .orElseGet(() -> MemberSetting.builder().member(member).build());

        if (req.getEventAlarmMinutes() != null)
            setting.setEventAlarmMinutes(req.getEventAlarmMinutes());
        if (req.getEmotionIntervalMinutes() != null)
            setting.setEmotionIntervalMinutes(req.getEmotionIntervalMinutes());
        if (req.getEmotionActiveStart() != null)
            setting.setEmotionActiveStart(req.getEmotionActiveStart());
        if (req.getEmotionActiveEnd() != null)
            setting.setEmotionActiveEnd(req.getEmotionActiveEnd());

        userSettingRepository.save(setting);
        return MemberSettingResponse.from(setting);
    }
}
