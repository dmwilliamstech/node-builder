import grails.util.Environment
import node.builder.CustomUserDetailsContextMapper
import node.builder.CustomUserDetailsService

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


beans = {
    Environment.executeForCurrentEnvironment {
        production {
            ldapAuthenticator(org.springframework.security.ldap.authentication.BindAuthenticator, ref("contextSource")){
                userDnPatterns = ['cn={0},ou=users,dc=airgapit,dc=com','cn={0},dc=airgapit,dc=com']
            }
        }
    }

    ldapUserDetailsMapper(CustomUserDetailsContextMapper) {
    }

    if(Environment.currentEnvironment != Environment.PRODUCTION){
        userDetailsService(CustomUserDetailsService){
        }
    }
}