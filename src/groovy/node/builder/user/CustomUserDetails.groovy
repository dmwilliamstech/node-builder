package node.builder.user

import node.builder.Organization
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class CustomUserDetails extends User {
    def organizations = []
    def id

    CustomUserDetails(User details){
        super(details.username, details.password, details.enabled, details.accountNonExpired, details.credentialsNonExpired,
                details.accountNonLocked, details.authorities)

        id = details.username
    }

    CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
    boolean credentialsNonExpired, boolean accountNonLocked,
                                           Collection<GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities)

    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        if (!super.equals(o)) return false

        CustomUserDetails that = (CustomUserDetails) o

        if (id != that.id) return false
        if (organizations != that.organizations) return false

        return true
    }

    int hashCode() {
        return username.hashCode()
    }

    def addOrganization(Organization organization){
        organizations << organization
    }

}
