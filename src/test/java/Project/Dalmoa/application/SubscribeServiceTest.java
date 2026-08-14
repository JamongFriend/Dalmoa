package Project.Dalmoa.application;

import Project.Dalmoa.domain.member.Member;
import Project.Dalmoa.domain.member.MemberRepository;
import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.SubscribeRepository;
import Project.Dalmoa.domain.subscribe.Term;
import Project.Dalmoa.presentation.dto.subscribe.request.SubscribeRequest;
import Project.Dalmoa.presentation.dto.subscribe.response.DashboardResponse;
import Project.Dalmoa.presentation.dto.subscribe.response.SubscribeListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscribeServiceTest {

    private SubscribeService subscribeService;

    @Mock
    private SubscribeRepository subscribeRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        subscribeService = new SubscribeService(subscribeRepository, memberRepository, calculationService);
    }

    @Test
    void createSubscribe_성공() {
        // given
        Long memberId = 1L;
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));
        SubscribeRequest request = new SubscribeRequest(
                "넷플릭스", 17000.0, Currency.KRW, LocalDate.of(2026, 8, 1),
                SubCategory.OTT, null, Term.MONTH
        );
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(subscribeRepository.save(any(Subscribe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Subscribe result = subscribeService.createSubscribe(request, memberId);

        // then
        assertThat(result.getName()).isEqualTo("넷플릭스");
        assertThat(result.getTerm()).isEqualTo(Term.MONTH);
        verify(subscribeRepository).save(any(Subscribe.class));
    }

    @Test
    void createSubscribe_존재하지않는회원_예외() {
        // given
        Long memberId = 999L;
        SubscribeRequest request = new SubscribeRequest(
                "넷플릭스", 17000.0, Currency.KRW, LocalDate.of(2026, 8, 1),
                SubCategory.OTT, null, Term.MONTH
        );
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subscribeService.createSubscribe(request, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원정보 없음");
    }

    @Test
    void editSubscribe_성공() {
        // given
        Long subscribeId = 1L;
        Long memberId = 1L;
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));
        ReflectionTestUtils.setField(member, "id", memberId);
        Subscribe subscribe = Subscribe.createSubscribe(
                member, "넷플릭스", 17000.0, Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0), SubCategory.OTT, null, Term.MONTH
        );
        SubscribeRequest request = new SubscribeRequest(
                "디즈니플러스", 9900.0, Currency.KRW, LocalDate.of(2026, 8, 5),
                SubCategory.OTT, null, Term.MONTH
        );
        when(subscribeRepository.findById(subscribeId)).thenReturn(Optional.of(subscribe));

        // when
        Subscribe result = subscribeService.editSubscribe(request, subscribeId, memberId);

        // then
        assertThat(result.getName()).isEqualTo("디즈니플러스");
        assertThat(result.getPrice()).isEqualTo(9900.0);
    }

    @Test
    void editSubscribe_존재하지않는구독_예외() {
        // given
        Long subscribeId = 999L;
        Long memberId = 1L;
        SubscribeRequest request = new SubscribeRequest(
                "디즈니플러스", 9900.0, Currency.KRW, LocalDate.of(2026, 8, 5),
                SubCategory.OTT, null, Term.MONTH
        );
        when(subscribeRepository.findById(subscribeId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subscribeService.editSubscribe(request, subscribeId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구독 없음");
    }

    @Test
    void editSubscribe_권한없음_예외() {
        // given
        Long subscribeId = 1L;
        Long ownerId = 1L;
        Long requesterId = 2L;
        Member owner = Member.create("owner@test.com", "주인", "encodedPassword", LocalDate.of(1990, 1, 1));
        ReflectionTestUtils.setField(owner, "id", ownerId);
        Subscribe subscribe = Subscribe.createSubscribe(
                owner, "넷플릭스", 17000.0, Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0), SubCategory.OTT, null, Term.MONTH
        );
        SubscribeRequest request = new SubscribeRequest(
                "디즈니플러스", 9900.0, Currency.KRW, LocalDate.of(2026, 8, 5),
                SubCategory.OTT, null, Term.MONTH
        );
        when(subscribeRepository.findById(subscribeId)).thenReturn(Optional.of(subscribe));

        // when & then
        assertThatThrownBy(() -> subscribeService.editSubscribe(request, subscribeId, requesterId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("수정 권한이 없습니다.");
    }

    @Test
    void deleteSubscribe_성공() {
        // given
        Long subscribeId = 1L;
        Long memberId = 1L;
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));
        ReflectionTestUtils.setField(member, "id", memberId);
        Subscribe subscribe = Subscribe.createSubscribe(
                member, "넷플릭스", 17000.0, Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0), SubCategory.OTT, null, Term.MONTH
        );
        when(subscribeRepository.findById(subscribeId)).thenReturn(Optional.of(subscribe));

        // when
        subscribeService.deleteSubscribe(subscribeId, memberId);

        // then
        verify(subscribeRepository).delete(subscribe);
    }

    @Test
    void deleteSubscribe_존재하지않는구독_예외() {
        // given
        Long subscribeId = 999L;
        Long memberId = 1L;
        when(subscribeRepository.findById(subscribeId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> subscribeService.deleteSubscribe(subscribeId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제할 구독 정보가 없습니다.");
    }

    @Test
    void deleteSubscribe_권한없음_예외() {
        // given
        Long subscribeId = 1L;
        Long ownerId = 1L;
        Long requesterId = 2L;
        Member owner = Member.create("owner@test.com", "주인", "encodedPassword", LocalDate.of(1990, 1, 1));
        ReflectionTestUtils.setField(owner, "id", ownerId);
        Subscribe subscribe = Subscribe.createSubscribe(
                owner, "넷플릭스", 17000.0, Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0), SubCategory.OTT, null, Term.MONTH
        );
        when(subscribeRepository.findById(subscribeId)).thenReturn(Optional.of(subscribe));

        // when & then
        assertThatThrownBy(() -> subscribeService.deleteSubscribe(subscribeId, requesterId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("삭제 권한이 없습니다.");
    }

    @Test
    void subscribeList_회원의_구독목록을_원화환산금액과함께_반환() {
        // given
        Long memberId = 1L;
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));
        Subscribe subscribe = Subscribe.createSubscribe(
                member, "넷플릭스", 17000.0, Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0), SubCategory.OTT, null, Term.MONTH
        );
        when(subscribeRepository.findAllByMemberId(memberId)).thenReturn(List.of(subscribe));
        when(calculationService.convertToKrw(subscribe)).thenReturn(17000.0);

        // when
        List<SubscribeListResponse> result = subscribeService.subscribeList(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("넷플릭스");
        assertThat(result.get(0).convertedPriceKrw()).isEqualTo(17000.0);
    }

    @Test
    void getDashboard_전체합계와_카테고리별합계를_반환() {
        // given
        Long memberId = 1L;
        Member member = Member.create("test@test.com", "테스터", "encodedPassword", LocalDate.of(1990, 1, 1));
        Subscribe subscribe = Subscribe.createSubscribe(
                member, "넷플릭스", 17000.0, Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0), SubCategory.OTT, null, Term.MONTH
        );
        List<Subscribe> subscribes = List.of(subscribe);
        when(subscribeRepository.findAllByMemberId(memberId)).thenReturn(subscribes);
        when(calculationService.totalAmount(subscribes)).thenReturn(17000.0);
        when(calculationService.calculateGroupedAmount(subscribes)).thenReturn(Map.of(SubCategory.OTT, 17000.0));

        // when
        DashboardResponse result = subscribeService.getDashboard(memberId);

        // then
        assertThat(result.totalAmount()).isEqualTo(17000.0);
        assertThat(result.categorySums()).containsEntry(SubCategory.OTT, 17000.0);
    }
}
