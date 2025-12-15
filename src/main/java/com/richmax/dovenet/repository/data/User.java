package com.richmax.dovenet.repository.data;

import com.richmax.dovenet.types.SubscriptionType;
import com.richmax.dovenet.types.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    // --- Fields for Registration ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Pigeon> pigeons;

    // --- Fields for forgotten password ---
    @Column(unique = true)
    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    // --- Fields for email verification ---
    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    // --- Fields for UserSettings ---
    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    // Profile picture stored as URL
    private String profilePictureUrl;

    // Subscription type
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscription = SubscriptionType.FREE;

    // Roles
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    private String language = "en";

    // Stripe
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    @Column(name = "subscription_valid_until")
    private LocalDateTime subscriptionValidUntil;

}
