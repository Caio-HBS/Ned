package io.github.caiohbs.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long addressId;
    private String street;
    private String number;
    private String zipCode;
    private String city;
    private String state;
    private boolean mainAddress;
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Address(String street, String number, String zipCode, String city, String state, boolean mainAddress) {
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.mainAddress = mainAddress;
    }
}
