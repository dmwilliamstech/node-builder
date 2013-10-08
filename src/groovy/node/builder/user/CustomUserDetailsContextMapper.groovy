package node.builder.user

import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper

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
class CustomUserDetailsContextMapper  implements UserDetailsContextMapper {
    private static final String DEFAULT_ORGANIZATION= "default"

    UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection authorities) {
        def user = new CustomUserDetails(username, "", true, true, true, true,
                authorities)

        def organizations = dirContextOperations.getAttributeSortedStringSet("o")
        if(organizations == null || organizations.empty)
            organizations = [DEFAULT_ORGANIZATION]
        organizations?.each{ organization ->
            user.addOrganization(organization)
        }

        return user
    }

    void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new IllegalStateException("Only retrieving data from LDAP is currently supported")
    }
}
