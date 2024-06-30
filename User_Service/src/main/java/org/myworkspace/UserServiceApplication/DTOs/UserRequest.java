package org.myworkspace.UserServiceApplication.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.myworkspace.UserServiceApplication.Entities.Address;
import org.myworkspace.UserServiceApplication.Entities.UserIdentifier;
import org.myworkspace.UserServiceApplication.Entities.UserType;
import org.myworkspace.UserServiceApplication.Entities.Users;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
public class UserRequest {
    private String name;
    @NotBlank(message = "contact No. is mandatory")
    private String contactNo;
    @NotBlank(message = "email cannot be blank")
    private String email;
    private String dob;
    @NotNull(message = "user Id is mandatory")
    private UserIdentifier userIdentifier;
    @NotBlank(message = "user Id value cannot be blank")
    private String userIdentifierValue;
    private String address; //**!!
    @NotBlank(message = "password cannot be blank")
    private String password;

    public Users toUserEntity() {
        return Users.builder().name(this.name).contactNo(this.contactNo)
                .email(this.email).address(this.address).dob(this.dob)
                .identifier(this.userIdentifier).password(this.password)
                .userIdentifierValue(this.userIdentifierValue).enabled(true)
                .accountNonExpired(true).accountNonLocked(true)
                .credentialsNonExpired(true).type(UserType.USER).build();
    } //?! password ?!
}
