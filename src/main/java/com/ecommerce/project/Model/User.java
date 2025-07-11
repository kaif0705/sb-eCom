package com.ecommerce.project.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table( name= "users",
        uniqueConstraints= {
        @UniqueConstraint(columnNames= "username"),
                @UniqueConstraint(columnNames= "email")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "user_id")
    private Long userId;

    @NotBlank
    @Size(max = 20)
    @Column(name= "username")
    private String userName;

    @NotBlank
    @Size(max = 120)
    @Column(name= "password")
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name= "email")
    private String email;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    //Joining User and Role table
    @ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable( name= "user_role",
            joinColumns= @JoinColumn(name= "user_id"),
            inverseJoinColumns= @JoinColumn(name= "role_id")
    )
    private Set<Role> roles= new HashSet<>();

    //Linking User(which is seller) with Product
    @ToString.Exclude
    @OneToMany(mappedBy= "user",
            cascade= {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval= true)
    private Set<Product> products;

    @ToString.Exclude
    @OneToOne(mappedBy= "user", cascade= {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval= true)
    @JoinColumn(name= "cart_id")
    private Cart cart;

    @OneToMany(mappedBy= "users", cascade= {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Address> addresses= new ArrayList<>();
}
