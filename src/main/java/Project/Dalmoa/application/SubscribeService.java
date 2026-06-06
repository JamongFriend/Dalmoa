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
                dto.customCategoryTag()
        );
        return subscribeRepository.save(subscribe);
    }

    // 구독 수정
    @Transactional
    public Subscribe editSubscribe(SubscribeRequest dto, Long subscribeId) {
        Subscribe subscribe = subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new IllegalArgumentException("구독 없음"));

        subscribe.editSubscribe(
                dto.name(),
                dto.price(),
                dto.currency(),
                dto.date().atStartOfDay(),
                dto.subCategory(),
                dto.customCategoryTag()
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

    // 회원의 전체 구독 목록 조회 (원화 환산 금액 포함)
    public List<SubscribeListResponse> subscribeList(Long memberId) {
        return subscribeRepository.findAllByMemberId(memberId).stream()
                .map(s -> SubscribeListResponse.from(s, calculationService.convertToKrw(s)))
                .toList();
    }

    // 대시보드 데이터 조회 (전체 지출 합계 및 카테고리별 합계)
    public DashboardResponse getDashboard(Long memberId) {
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(memberId);
        double total = calculationService.totalAmount(subscribes);
        Map<SubCategory, Double> categorySums = calculationService.calculateGroupedAmount(subscribes);

        return new DashboardResponse(total, categorySums);
    }
}
