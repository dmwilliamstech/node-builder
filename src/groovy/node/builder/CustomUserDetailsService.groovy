package node.builder

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService
import org.springframework.dao.DataAccessException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

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
class CustomUserDetailsService implements GrailsUserDetailsService{
    SpringSecurityService springSecurityService

    CustomUserDetailsService(){
        super()
    }


    @Override
    UserDetails loadUserByUsername(String s, boolean loadRoles) throws UsernameNotFoundException, DataAccessException {
        def user = SecUser.findByUsername(s)
        def authorities = new ArrayList<GrantedAuthority>()
        def secRoles = SecUserSecRole.findAllBySecUser(user)
        log.error secRoles

        secRoles.each {userRole ->
            log.error userRole
                authorities.addAll(AuthorityUtils.createAuthorityList(userRole.secRole.authority))
        }


        def details = new CustomUserDetails(
            user.username,
            user.password,
                user.enabled,
                !user.accountExpired,
                true,
                !user.accountLocked,
                authorities
        )

        return details
    }

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException, DataAccessException {
        loadUserByUsername(s, true)
    }
}
