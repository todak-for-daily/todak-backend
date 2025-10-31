package com.example.todak_server.service;

import com.example.todak_server.entity.Habit;
import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.HabitRepository;
import com.example.todak_server.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final MemberRepository memberRepository;

    public Habit createHabit(Long memberId, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Habit habit = new Habit();
        habit.setMember(member);
        habit.setContent(content);

        return habitRepository.save(habit);
    }

    public List<Habit> getHabits(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        return habitRepository.findByMember(member);
    }

    public void deleteHabit(Long memberId, Long habitId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Habit habit = habitRepository.findByIdAndMember(habitId, member)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found"));

        habitRepository.delete(habit);
    }


}
