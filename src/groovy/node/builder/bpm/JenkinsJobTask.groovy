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

package node.builder.bpm

abstract class JenkinsJobTask {

    Map getMapFromJob(job){
        Map map = [:]
        map.displayName = job.displayName
        if(!job.builds.empty){
            map.builds = getListFromBuilds(job.builds)
            map.lastBuild = getMapFromBuild(job.lastBuild)
            map.lastCompletedBuild = getMapFromBuild(job.lastCompletedBuild)
            map.lastFailedBuild = getMapFromBuild(job.lastFailedBuild)
            map.lastStableBuild = getMapFromBuild(job.lastStableBuild)
            map.lastSuccessfulBuild = getMapFromBuild(job.lastSuccessfulBuild)
            map.lastUnstableBuild = getMapFromBuild(job.lastUnstableBuild)
            map.lastUnsuccessfulBuild = getMapFromBuild(job.lastUnsuccessfulBuild)
            map.nextBuildNumber = job.nextBuildNumber
        }
        return map
    }

    def getListFromBuilds(builds){
        def list = []
        builds.each{ build ->
            list.add(getMapFromBuild(build))
        }
        return list
    }

    Map getMapFromBuild(build){
        Map map = [:]
		map.building = build.building
		map.duration = build.duration
		map.fullDisplayName = build.fullDisplayName
		map.id = build.id
		map.timestamp = build.timestamp
		map.result = build.result
        return map
    }

}
