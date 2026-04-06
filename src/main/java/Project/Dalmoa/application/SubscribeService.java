package Project.Dalmoa.application;

import Project.Dalmoa.domain.member.Member;
import Project.Dalmoa.domain.member.MemberRepository;
import Project.Dalmoa.domain.subscribe.SubType;
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

    @Transactional
    public Subscribe createSubscribe(SubscribeRequest dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원정보 없음"));

        Subscribe subscribe = Subscribe.createSubscribe(
                member,
                dto.name(),
                dto.price(),
                dto.date().atStartOfDay(),
                dto.type(),
                dto.customTypeTag()
        );
        return subscribeRepository.save(subscribe);
    }

    @Transactional
    public Subscribe editSubscribe(SubscribeRequest dto, Long subscribeId) {
        Subscribe subscribe = subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new IllegalArgumentException("구독 없음"));

        subscribe.editSubscribe(
                dto.name(),
                dto.price(),
                dto.date().atStartOfDay(),
                dto.type(),
                dto.customTypeTag()
        );

        return subscribe;
    }

    @Transactional
    public void deleteSubscribe(Long subscribeId, Long memberId) {
        Subscribe subscribe = subscribeRepository.findById(subscribeId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 구독 정보가 없습니다."));

        if (!subscribe.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        subscribeRepository.delete(subscribe);
    }

    public List<SubscribeListResponse> subscribeList(Long memberId) {
        return subscribeRepository.findAllByMemberId(memberId).stream()
                .map(SubscribeListResponse::from)
                .toList();
    }

    public DashboardResponse getDashboard(Long memberId) {
        List<Subscribe> subscribes = subscribeRepository.findAllByMemberId(memberId);
        double total = calculationService.totalAmount(subscribes);
        Map<SubType, Double> categorySums = calculationService.calculateGroupedAmount(subscribes);

        return new DashboardResponse(total, categorySums);
    }
}
