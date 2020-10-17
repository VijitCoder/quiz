package engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDto {
    @Pattern(regexp = ".+@.+\\..+")
    @JsonProperty
    private String email;

    @Size(min = 5)
    @JsonProperty
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
