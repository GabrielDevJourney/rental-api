package com.gabriel.rentacar.security;

import com.gabriel.rentacar.entity.AccountEntity;
import com.gabriel.rentacar.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component("securityUtils")
public class SecurityUtils {

	private final AccountRepository accountRepository;

	public SecurityUtils(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Long getAuthenticatedAccountId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return null;
		}
		String email = auth.getName();
		return accountRepository.findByEmail(email)
				.map(AccountEntity::getId)
				.orElse(null);
	}

	public boolean isOwner(Long targetId) {
		Long authenticatedId = getAuthenticatedAccountId();
		return authenticatedId != null && authenticatedId.equals(targetId);
	}

	public boolean isOwnerOrAdmin(Long targetId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return false;
		}
		boolean isPrivileged = auth.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_MANAGER".equals(a.getAuthority()));
		return isPrivileged || isOwner(targetId);
	}
}
