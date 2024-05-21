package com.example.demo.entity;

import jakarta.persistence.*;
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
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String token;

    @Column(name = "token_expried")
    private Date tokenExpried;

    @Column(name= "isRevoked")
    private boolean isRevoked;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", length = 255)  // Đảm bảo độ dài của cột đủ lớn
    private TokenType tokenType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_token_user"))
    private User user;

    public enum TokenType{
        ACTIVE,
        PASSWORD
    }

}
