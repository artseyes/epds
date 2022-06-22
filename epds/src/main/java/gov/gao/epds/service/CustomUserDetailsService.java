package gov.gao.epds.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class CustomUserDetailsService implements
		AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

	@Autowired
	UserInfoService userInfoService;

	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
			throws UsernameNotFoundException {

		PreAuthenticatedAuthenticationToken tokenValue = (PreAuthenticatedAuthenticationToken) token.getPrincipal();

		if (tokenValue.getPrincipal() == null) {
			throw new AuthenticationCredentialsNotFoundException(
					"token not found");
		}
		
		return new User(tokenValue.getPrincipal().toString(), "",
				true, true, true, true, new ArrayList<GrantedAuthority>());

	}

}
