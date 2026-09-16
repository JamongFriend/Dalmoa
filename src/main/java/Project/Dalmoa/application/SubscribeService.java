package Project.Dalmoa.application;

import Project.Dalmoa.domain.member.Member;
import Project.Dalmoa.domain.member.MemberRepository;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.SubscribeRepository;
import Project.Dalmoa.presentation.dto.subscribe.response.DashboardResponse;
import Project.Dalmoa.presentation.dto.subscribe.request.SubscribeRequest;
import Project.Dalmoa.presentation.dto.subscribe.response.SubscribeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final MemberRepository memberRepository;

    private final CalculationService calculationService;

    // 구독 생성
    @Transactional
    public Subscribe createSubscribe(SubscribeRequest dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보 없음"));

        Subscribe subscribe = Subscribe.createSubscribe(
                member,
                dto.name(),
                dto.price(),
                dto.currency(),
                dto.date().atStartOfDay(),
                dto.subCategory(),
                dto.customCategoryTag(),
                dto.term()
        );
        return subscribeRepository.save(subscribe);
    }

    // 구독 수정
    @Transactional
    public Subscribe editSubscribe(SubscribeRequest dto, Long subscribeId, Long memberId) {
        Subscribe subscribe = subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new IllegalArgumentException("구독 없음"));

        if (!subscribe.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        subscribe.editSubscribe(
                dto.name(),
                dto.price(),
                dto.currency(),
                dto.date().atStartOfDay(),
                dto.subCategory(),
                dto.customCategoryTag(),
                dto.term()
        );

        return subscribe;
    }

    // 구독 삭제
    @Transactional
    public void deleteSubscribe(Long subscribeId, Long memberId) {
        Subscribe subscribe = subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 구독 정보가 없습니다."));

        if (!subscribe.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        subscribeRepository.delete(subscribe);
    }

    // 특정 달 기준, 회원의 구독 목록 조회 (해당 달에 아직 등록되지 않은 구독은 제외, 원화 환산 금액 포함)
    public List<SubscribeListResponse> subscribeList(Long memberId, YearMonth targetMonth) {
        return subscribeRepository.findAllByMemberId(memberId).stream()
                .filter(s -> calculationService.isActiveInMonth(s, targetMonth))
                .map(s -> SubscribeListResponse.from(
                        s,
                        calculationService.convertToKrw(s),
                        calculationService.monthlyKrwAmount(s, targetMonth)
                ))
                .toList();
    }

    // 특정 달 기준 대시보드 데이터 조회 (지출 합계, 전월 대비 증감, 카테고리별 합계)
    public DashboardResponse getDashboard(Long memberId, YearMonth targetMonth) {
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(memberId);
        double total = calculationService.totalAmount(subscribes, targetMonth);
        double previousTotal = calculationService.totalAmount(subscribes, targetMonth.minusMonths(1));
        Map<SubCategory, Double> categorySums = calculationService.calculateGroupedAmount(subscribes, targetMonth);

        double diffAmount = total - previousTotal;
        double diffPercent = (previousTotal == 0.0) ? 0.0 : (diffAmount / previousTotal) * 100.0;

        return new DashboardResponse(total, previousTotal, diffAmount, diffPercent, categorySums);
    }
}
