package com.richmax.dovenet.service.data;

import com.richmax.dovenet.types.SubscriptionType;
import com.richmax.dovenet.types.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean emailVerified;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String profilePictureUrl;

    private SubscriptionType subscription;
    private UserRole role;

    private String language;
}
