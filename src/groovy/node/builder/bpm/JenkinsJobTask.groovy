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
