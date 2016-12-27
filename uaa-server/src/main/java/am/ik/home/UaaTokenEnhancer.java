package am.ik.home;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import am.ik.home.member.Member;
import am.ik.home.member.MemberUserDetails;

@Component
public class UaaTokenEnhancer implements TokenEnhancer {
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
			OAuth2Authentication authentication) {
		MemberUserDetails userDetails = (MemberUserDetails) authentication.getPrincipal();
		Member member = userDetails.getMember();
		Map<String, Object> additionalInfo = new LinkedHashMap<>();
		additionalInfo.put("user_id", member.getMemberId().toString());
		additionalInfo.put("family_name", member.getFamilyName());
		additionalInfo.put("given_name", member.getGivenName());
		additionalInfo.put("display_name",
				member.getFamilyName() + " " + member.getGivenName());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		return accessToken;
	}
}
