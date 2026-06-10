package com.gabriel.rentacar.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unused")
@Component
public class JwtTokenUtil {

	@SuppressWarnings("unused")
	@Value("${jwt.secret}")
	private String secret;

	@SuppressWarnings("unused")
	@Value("${jwt.expiration}")
	private long jwtExpiration;

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

	public String generateToken(String email, List<String> roles) {
		logger.debug("Generating token for user: {}", email);
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		return createToken(claims, email);
	}

	private String createToken(Map<String, Object> claims, String subject) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);
		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

		return Jwts.builder()
				.claims(claims)
				.subject(subject)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(key, Jwts.SIG.HS512)
				.compact();
	}

	public boolean validateToken(String token, String userEmail) {
		final String email = extractEmail(token);
		boolean isValid = email.equals(userEmail) && !isTokenExpired(token);
		logger.debug("Token validation for user: {} - Valid: {}", userEmail, isValid);
		return isValid;
	}

	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {
		try {
			Claims claims = extractAllClaims(token);
			List<String> roles = claims.get("roles", List.class);
			return roles != null ? roles : Collections.emptyList();
		} catch (Exception e) {
			logger.error("Error extracting roles from token", e);
			return Collections.emptyList();
		}
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private boolean isTokenExpired(String token) {
		final Date expiration = extractExpiration(token);
		boolean expired = expiration.before(new Date());
		if (expired) {
			logger.warn("Token has expired: {}...", token.substring(0, Math.min(10, token.length())));
		}
		return expired;
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
			return Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (Exception e) {
			logger.error("Failed to extract claims from token: {}...", token.substring(0, Math.min(10, token.length())), e);
			throw e;
		}
	}
}
