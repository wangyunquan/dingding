package lingjia.wang.base.domain;



import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="base_authority")
public class Authority implements Serializable {

	@Id
	@GeneratedValue
	private Long authorityId;
	private String authName;
	private Boolean enable = Boolean.TRUE;
	@ManyToMany(mappedBy = "authorities")
	private Set<Role> roles = new HashSet<Role>();

	public Long getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(Long authorityId) {
		this.authorityId = authorityId;
	}

	public String getAuthName() {
		return authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
