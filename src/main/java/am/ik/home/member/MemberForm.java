package am.ik.home.member;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.terasoluna.gfw.common.validator.constraints.Compare;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@Compare(left = "rawPassword", right = "passwordConfirm", operator = Compare.Operator.EQUAL,
        message = "'rawPassword' and 'passwordConfirm' must be same")
public class MemberForm implements Serializable {
    @NotEmpty
    @Size(max = 255)
    private String givenName;
    @NotEmpty
    @Size(max = 255)
    private String familyName;
    @NotEmpty
    @Size(min = 6, max = 50)
    private String rawPassword;
    @NotEmpty
    @Size(min = 6, max = 50)
    private String passwordConfirm;
    @NotEmpty
    @Size(max = 255)
    @Email
    private String email;
    private List<MemberRole> roles;
}
