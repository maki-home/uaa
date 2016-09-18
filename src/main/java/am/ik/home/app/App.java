package am.ik.home.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class App implements Serializable {
    @Id
    @Column(columnDefinition = "varchar(36)")
    @Size(min = 36, max = 36)
    private String appId;
    @NotEmpty
    @Size(max = 255)
    private String appName;
    @URL
    @NotEmpty
    @Size(max = 255)
    private String appUrl;
    @Size(max = 255)
    private String appSecret;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @NotNull
    private Set<AppRole> roles;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @NotEmpty
    private Set<AppGrantType> grantTypes;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes;
    @ElementCollection(fetch = FetchType.EAGER)
    @NotNull
    private Set<String> redirectUrls;
    @Max(600000)
    @Min(0)
    @NotNull
    private Integer accessTokenValiditySeconds = (int) TimeUnit.HOURS.toSeconds(3);
    @Max(600000)
    @Min(0)
    @NotNull
    private Integer refreshTokenValiditySeconds = (int) TimeUnit.HOURS.toSeconds(12);
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> autoApproveScopes;

}
