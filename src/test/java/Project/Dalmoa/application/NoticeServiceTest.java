package Project.Dalmoa.application;

import Project.Dalmoa.domain.notice.Notice;
import Project.Dalmoa.domain.notice.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @BeforeEach
    void setUp() {
        noticeService = new NoticeService(noticeRepository);
    }

    @Test
    void getNotices_최신순으로_목록을_반환() {
        // given
        Notice newer = Notice.builder().version("1.1.0").title("업데이트 안내").content("내용1").build();
        Notice older = Notice.builder().version(null).title("점검 안내").content("내용2").build();
        when(noticeRepository.findAllByOrderByIdDesc()).thenReturn(List.of(newer, older));

        // when
        List<Notice> result = noticeService.getNotices();

        // then
        assertThat(result).containsExactly(newer, older);
    }

    @Test
    void getNotice_존재하는ID_조회_성공() {
        // given
        Notice notice = Notice.builder().version("1.1.0").title("업데이트 안내").content("내용").build();
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        // when
        Notice result = noticeService.getNotice(1L);

        // then
        assertThat(result).isEqualTo(notice);
    }

    @Test
    void getNotice_존재하지않는ID_예외() {
        // given
        when(noticeRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> noticeService.getNotice(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 공지사항이 없습니다.");
    }
}
