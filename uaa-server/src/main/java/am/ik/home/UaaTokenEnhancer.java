package am.ik.home;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import am.ik.home.app.AppRepository;
import am.ik.home.member.Member;
import am.ik.home.member.MemberUserDetails;

@Component
public class UaaTokenEnhancer implements TokenEnhancer {
	private final String issuer;
	private final AppRepository appRepository;

	public UaaTokenEnhancer(
			@Value("${jwt.issuer:${spring.application.name}}") String issuer,
			AppRepository appRepository) {
		this.issuer = issuer;
		this.appRepository = appRepository;
	}

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
			OAuth2Authentication authentication) {
		if (authentication.getPrincipal() instanceof MemberUserDetails) {
			MemberUserDetails userDetails = (MemberUserDetails) authentication
					.getPrincipal();
			Member member = userDetails.getMember();
			Instant expiration = accessToken.getExpiration().toInstant();
			Map<String, Object> additionalInfo = new LinkedHashMap<>();
			additionalInfo.put("user_id", member.getMemberId());
			additionalInfo.put("email", member.getEmail());
			additionalInfo.put("family_name", member.getFamilyName());
			additionalInfo.put("given_name", member.getGivenName());
			additionalInfo.put("display_name",
					member.getFamilyName() + " " + member.getGivenName());
			additionalInfo.put("iss", issuer);
			additionalInfo.put("exp", expiration.getEpochSecond());
			// Retrieves client information
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String appId = auth.getName();
			appRepository.findByAppId(appId).ifPresent(app -> {
				Integer validitySeconds = app.getAccessTokenValiditySeconds();
				Instant iat = expiration.minusSeconds(validitySeconds);
				additionalInfo.put("iat", iat.getEpochSecond());
			});
			((DefaultOAuth2AccessToken) accessToken)
					.setAdditionalInformation(additionalInfo);
		}
		return accessToken;
	}
}
