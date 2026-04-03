package Project.Dalmoa.domain.member;

import jakarta.persistence.*;
import lombok.Getter;

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

    protected Member() {}

    public Member(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static Member create(String email, String name, String password) {
        return new Member(email, name, password);
    }

    public void update(String name) {
        this.name = name;
    }
}
