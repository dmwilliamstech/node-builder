package node.builder

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

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails extends User {
    def organizations = []

    CustomUserDetails(User details){
        super(details.username, details.password, details.enabled, details.accountNonExpired, details.credentialsNonExpired,
                details.accountNonLocked, details.authorities)
    }

    CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
    boolean credentialsNonExpired, boolean accountNonLocked,
                                           Collection<GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities)

    }

    def addOrganization(String organization){
        organizations << organization
    }

}
