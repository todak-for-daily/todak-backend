package com.example.todak_server.service;

import com.example.todak_server.dto.request.HabitRequest;
import com.example.todak_server.entity.Habit;
import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.HabitRepository;
import com.example.todak_server.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final MemberRepository memberRepository;

    public Habit createHabit(Long memberId, HabitRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Habit habit = new Habit();
        habit.setMember(member);

        habit.setType(request.type());
        habit.setSenseType(request.senseType());
        habit.setTime(request.time());
        habit.setPlace(request.place());
        habit.setTarget(request.target());
        habit.setDescription(request.description());
        habit.setSoothingAction(request.soothingAction());
        habit.setTrigger(request.trigger());

        return habitRepository.save(habit);
    }

    public Habit updateHabit(Long memberId,Long habitId, HabitRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found."));

        Habit habit = habitRepository.findByIdAndMember(habitId, member)
                .orElseThrow(() -> new EntityNotFoundException("Habit not found"));

        // 간단한 검증 (필요하면 더 추가)
        if (request.type() == null || request.description() == null) {
            throw new IllegalArgumentException("type과 description은 null일 수 없습니다.");
        }

        habit.setType(request.type());
        habit.setSenseType(request.senseType());
        habit.setTime(request.time());
        habit.setPlace(request.place());
        habit.setTarget(request.target());
        habit.setDescription(request.description());
        habit.setSoothingAction(request.soothingAction());
        habit.setTrigger(request.trigger());

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
