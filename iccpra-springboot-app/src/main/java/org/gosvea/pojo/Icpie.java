package org.gosvea.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Icpie {
    @NotNull
    private Integer id;
    private String firstname;
    private String lastname;
    private String state;
    private String city;
    @JsonIgnore
    private String password;
    private String icpiename;
    @NotEmpty
    @Email
    private String email;

    private String role;
}
