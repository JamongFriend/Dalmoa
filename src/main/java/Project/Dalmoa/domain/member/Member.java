package Project.Dalmoa.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDate;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private LocalDate birthDate;

    protected Member() {}

    public Member(String email, String name, String password, LocalDate birthDate) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.birthDate = birthDate;
    }

    public static Member create(String email, String name, String password, LocalDate birthDate) {
        return new Member(email, name, password, birthDate);
    }

    public void update(String name) {
        this.name = name;
    }
}
