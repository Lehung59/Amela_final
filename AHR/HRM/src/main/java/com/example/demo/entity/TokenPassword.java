package com.example.demo.entity;

import com.example.demo.rule.ValidEmail;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokenPassword")
public class TokenPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String token;

    @Column(name = "token_expried")
    private Date tokenExpried;

    @Column(name= "isRevoked")
    private boolean isRevoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_tokenpassword_user"))
    private User user;

}
