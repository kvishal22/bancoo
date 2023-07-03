package com.kanna.banco.confirmation;

import com.kanna.banco.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "confirmationTokenparttwo")
@Data
public class ConfirmationTokenDetail{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tokenId;

    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(nullable = false,name = "user_ids")
    private User user;
    public ConfirmationTokenDetail(User user) {
        this.user = user;
        createdAt = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }
}
