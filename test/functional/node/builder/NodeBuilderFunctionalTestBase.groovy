package node.builder

import geb.junit4.GebReportingTest

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
class NodeBuilderFunctionalTestBase extends GebReportingTest{

    void login(username = 'admin', password = 'admin'){
        go('login/auth')
        assert title == "Login"

        j_username = username
        j_password = password
        $('input#submit').click()
    }

    void logout(){
        $("a[href\$=\"logout/index\"]").click()
    }

    def createEmptyRepo(){
        def tempDir = new File("/tmp/dir")
        if(tempDir.exists())
            tempDir.deleteDir()
        tempDir.mkdir()

        def env = [""]
        def gitInit = "git init".execute([], tempDir)
        gitInit.waitForOrKill(1000)

        def touch = "touch test".execute([], tempDir)
        gitInit.waitForOrKill(1000)

        def gitAdd = "git add test".execute([], tempDir)
        gitInit.waitForOrKill(1000)

        def gitCommit = "git commit -m 'test' -a".execute([], tempDir)
        gitInit.waitForOrKill(1000)

        return tempDir
    }

    void deleteEmptyRepo(){
        def tempDir = new File("/tmp/dir")
        if(tempDir.exists())
            tempDir.deleteDir()
    }

    @Override
    def waitFor(Closure condition) {
        waitFor(null, condition)
    }

    @Override
    def waitFor(Double timeoutSecs, Closure condition) {
        waitFor(timeoutSecs, null, condition)
    }

    @Override
    def waitFor(Double timeoutSecs, Double intervalSecs, Closure condition) {
        timeoutSecs = timeoutSecs ?: 5
        intervalSecs = intervalSecs ?: 0.5
        def loops = Math.ceil(timeoutSecs / intervalSecs)

        def assertionError = doClosure(condition)
        def i = 0
        while (assertionError != null && i++ < loops) {
            sleep((intervalSecs * 1000) as long)
            assertionError = doClosure(condition)
        }
        if (assertionError != null) {
            throw assertionError
        }
        true
    }


    def doClosure(Closure closure){
        try{
            closure()
            return null
        } catch (Exception e){
            return e
        } catch (Error e){
            return e
        }
    }
}
