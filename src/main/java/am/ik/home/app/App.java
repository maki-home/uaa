package am.ik.home.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class App implements Serializable {
    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @GeneratedValue(generator = "uuid")
    @Column(columnDefinition = "varchar(36)")
    @NotEmpty
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
    @NotEmpty
    private List<AppRole> roles;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @NotEmpty
    private List<AppGrantType> grantTypes;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<AppScope> scopes;
    @ElementCollection(fetch = FetchType.EAGER)
    @NotEmpty
    private List<String> redirectUrls;
    @Max(600000)
    @Min(0)
    @NotNull
    private Integer accessTokenValiditySeconds;
    @Max(600000)
    @Min(0)
    @NotNull
    private Integer refreshTokenValiditySeconds;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<AppScope> autoApproveScopes;

}
