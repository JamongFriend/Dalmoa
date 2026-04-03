package Project.Dalmoa.domain.subscribe;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String money;

    private LocalDateTime date;

    @Enumerated
    private SubType subType;

    @Builder
    private Subscribe(Long id, String name, String money, LocalDateTime date, SubType subType) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.date = date;
        this.subType = subType;
    }

    public static Subscribe createSubscribe(String name, String money, LocalDateTime date, SubType subType) {
        return Subscribe.builder()
                .name(name)
                .money(money)
                .date(date)
                .subType(subType)
                .build();
    }
}
