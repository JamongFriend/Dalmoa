package Project.Dalmoa.domain.notification;

import Project.Dalmoa.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = AuditingEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String message;

    private boolean isRead;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Notification(Member member, String message) {
        this.member = member;
        this.message = message;
        this.isRead = false;
    }

    // 알림 읽음 처리 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}
