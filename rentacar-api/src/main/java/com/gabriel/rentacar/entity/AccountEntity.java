package com.gabriel.rentacar.entity;

import com.gabriel.rentacar.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unused", "NullableProblems"})
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class AccountEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private Long id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "active")
	private boolean active;

	@Column(name="email",unique = true)
	@NotNull
	private String email;

	@Column(name = "password", nullable = false, length = 72)
	private String password;

	@Column(name = "phone", unique = true)
	@NotNull
	private String phoneNumber;

	@Column(name = "age")
	@NotNull
	@Range(min = 18, max = 99)
	private Integer age;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false, length = 20)
	private UserRole userRole;

	//* USER DETAILS OVERRIDES

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

}
