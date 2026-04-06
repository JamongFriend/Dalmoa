package Project.Dalmoa.domain.subscribe;

import Project.Dalmoa.domain.member.Member;
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

    private Double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private SubType subType;

    private String customTypeTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Subscribe(Long id, Member member, String name, Double price, LocalDateTime date, SubType subType, String customTypeTag) {
        this.id = id;
        this.member = member;
        this.name = name;
        this.price = price;
        this.date = date;
        this.subType = subType;
        this.customTypeTag = (subType == SubType.ETC) ? customTypeTag : null;
    }
    public static Subscribe createSubscribe(Member member, String name, Double price, LocalDateTime date, SubType subType, String customTypeTag) {
        return Subscribe.builder()
                .member(member)
                .name(name)
                .price(price)
                .date(date)
                .subType(subType)
                .customTypeTag(customTypeTag)
                .build();
    }

    public void editSubscribe(String name, Double price, LocalDateTime date, SubType subType, String customTypeTag) {
        this.name = name;
        this.price = price;
        this.date = date;
        this.subType = subType;
        this.customTypeTag = (subType == SubType.ETC) ? customTypeTag : null;
    }
}
