package org.learn.quarkus.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@Setter
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    public String name;

    @Column(unique = true)
    public String email;

    @JsonIgnore
    public String password;

    public String photo;

    public User() {
    }
}
