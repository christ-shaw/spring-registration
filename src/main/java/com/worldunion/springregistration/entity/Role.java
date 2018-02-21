package com.worldunion.springregistration.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    private Collection<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_privileges", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Collection<Privilege> privileges;

    private String name;

    public Role(String name) {
        this.name = name;
    }
}
