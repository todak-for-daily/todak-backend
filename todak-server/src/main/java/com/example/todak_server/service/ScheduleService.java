package com.example.todak_server.service;

import com.example.todak_server.entity.GeneralSchedule;
import com.example.todak_server.entity.Member;
import com.example.todak_server.entity.SourceType;
import com.example.todak_server.entity.WeeklySchedule;
import com.example.todak_server.repository.GeneralScheduleRepository;
import com.example.todak_server.repository.WeeklyScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class ScheduleService {

    private final WeeklyScheduleRepository weeklyRepo;
    private final GeneralScheduleRepository generalRepo;

    public ScheduleService(WeeklyScheduleRepository weeklyRepo, GeneralScheduleRepository generalRepo) {
        this.weeklyRepo = weeklyRepo;
        this.generalRepo = generalRepo;
    }

    // 1. 일주일 시간표 생성 + General 스냅샷 생성
    public WeeklySchedule createWeeklySchedule(WeeklySchedule weekly, LocalDate startDate, LocalDate endDate) {
        WeeklySchedule saved = weeklyRepo.save(weekly);

        // Weekly 기반으로 General 일정 여러 개 생성 (스냅샷)
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            if (date.getDayOfWeek().name().equals(weekly.getDayOfWeek().name())) {
                GeneralSchedule general = new GeneralSchedule();
                general.setMember(weekly.getMember());
                general.setDate(date);
                general.setStartTime(weekly.getStartTime());
                general.setEndTime(weekly.getEndTime());
                general.setTitle(weekly.getTitle());
                general.setColor(weekly.getColor());
                general.setSourceType(SourceType.WEEKLY);
                general.setSourceId(saved.getId());
                general.setLocation(weekly.getLocation());

                generalRepo.save(general);
            }
            date = date.plusDays(1);
        }
        return saved;
    }

    // 2. 특정 멤버의 일주일 시간표 조회
    public List<WeeklySchedule> getWeeklySchedule(Member member) {
        return weeklyRepo.findByMember(member);
    }

    // 3. 특정 사용자의 일정 중 특정 기간 조회 (ex. 먼슬리 캘린더 ..)
    public List<GeneralSchedule> getGeneralSchedule(Member member, LocalDate startDate, LocalDate endDate) {
        return generalRepo.findByMemberAndDateBetween(member,startDate,endDate);
    }

    // 4. General 일정 단일 생성
    public GeneralSchedule createGeneralSchedule(GeneralSchedule general) {
        general.setSourceType(SourceType.GENERAL);
        return generalRepo.save(general);
    }

    // 5. General 일정 수정
    public GeneralSchedule updateGeneralSchedule(Long id, GeneralSchedule updated) {
        GeneralSchedule general = generalRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("general 일정을 찾을 수 없습니다."));
        general.setTitle(updated.getTitle());
        general.setStartTime(updated.getStartTime());
        general.setEndTime(updated.getEndTime());
        general.setColor(updated.getColor());
        general.setLocation(updated.getLocation());
        return general;
    }

    // 6. General 일정 삭제 (soft delete)
    public void deleteGeneralSchedule(Long id) {
        GeneralSchedule general = generalRepo.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("general 일정을 찾을 수 없습니다."));
        general.setDeleted(true);
    }



    // Weekly 일정 수정
    public WeeklySchedule updateWeeklySchedule(Long id, WeeklySchedule updated) {
        WeeklySchedule weekly = weeklyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Weekly 일정을 찾을 수 없습니다."));

        weekly.setDayOfWeek(updated.getDayOfWeek());
        weekly.setStartTime(updated.getStartTime());
        weekly.setEndTime(updated.getEndTime());
        weekly.setTitle(updated.getTitle());
        weekly.setColor(updated.getColor());
        weekly.setLocation(updated.getLocation());

        // General 일정 반영: 과거는 그대로 두고 미래만 수정하는 로직 필요
        List<GeneralSchedule> generals = generalRepo.findBySourceId(id);
        for (GeneralSchedule g : generals) {
            if (g.getDate().isAfter(LocalDate.now())) { // 오늘 이후 일정만 수정
                g.setStartTime(updated.getStartTime());
                g.setEndTime(updated.getEndTime());
                g.setTitle(updated.getTitle());
                g.setColor(updated.getColor());
                g.setLocation(updated.getLocation());
            }
        }

        return weekly;
    }

    // Weekly 일정 삭제
    public void deleteWeeklySchedule(Long id) {
        WeeklySchedule weekly = weeklyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Weekly 일정을 찾을 수 없습니다."));

        weeklyRepo.delete(weekly);

        // General 일정 처리: 과거는 유지, 미래는 삭제
        List<GeneralSchedule> generals = generalRepo.findBySourceId(id);
        for (GeneralSchedule g : generals) {
            if (g.getDate().isAfter(LocalDate.now())) {
                generalRepo.delete(g);
            }
        }
    }

    /**
     * 매주 월요일 0시에 자동으로 General 일정 연장
     */
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void extendWeeklySchedules() {
        List<WeeklySchedule> allWeeklies = weeklyRepo.findAll();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusWeeks(8);

        for (WeeklySchedule weekly : allWeeklies) {
            // 이 Weekly 일정에 연결된 General 중 가장 마지막 날짜 확인
            List<GeneralSchedule> generals = generalRepo.findBySourceId(weekly.getId());
            LocalDate latest = generals.stream()
                    .map(GeneralSchedule::getDate)
                    .max(LocalDate::compareTo)
                    .orElse(today.minusDays(1)); // 없으면 과거 날짜로 설정

            // 마지막 스냅샷이 8주보다 짧으면 연장 필요
            if (ChronoUnit.WEEKS.between(today, latest) < 8) {
                LocalDate nextDate = latest.plusWeeks(1);

                // 다음 주차부터 limit(8주 후)까지 반복 생성
                while (!nextDate.isAfter(limit)) {
                    if (nextDate.getDayOfWeek().name().equals(weekly.getDayOfWeek().name())) {
                        GeneralSchedule g = new GeneralSchedule();
                        g.setMember(weekly.getMember());
                        g.setDate(nextDate);
                        g.setStartTime(weekly.getStartTime());
                        g.setEndTime(weekly.getEndTime());
                        g.setTitle(weekly.getTitle());
                        g.setColor(weekly.getColor());
                        g.setSourceType(SourceType.WEEKLY);
                        g.setSourceId(weekly.getId());
                        g.setLocation(weekly.getLocation());
                        generalRepo.save(g);
                    }
                    nextDate = nextDate.plusDays(1);
                }
            }
        }
    }

}
