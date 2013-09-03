<table id="instances">
<g:each in="${instances}" status="i" var="instance">
    <tr>
        <td class="instanceName" style="width:125px">${instance.name}</td>
        <td class="instanceStatus" style="width:125px">
            ${ instance.image?.name?.split(/\+/)[0] }
        </td>

        <g:set var="ramCount" value="${instance.image?.minRam > 512? 2:1}" />
        <td class="instanceRam" style="width:${ramCount*60}px">
            <g:each in="${(1..ramCount)}" >
                <g:img file="Memory.png"/>
            </g:each >
        </td>
        <g:set var="diskCount" value="${instance.image?.minDisk > 20? 2:1}" />
        <td class="instanceDisk" style="width:${diskCount * 60}px">
            <g:each in="${(1..diskCount)}" >
                <g:img file="Disk.png"/>
            </g:each >
        </td>
        <g:set var="cpuCount" value="${((instance.flavor?.vcpus?: "1").toInteger())}"/>
        <td class="instanceCPUS" style="width:${60*cpuCount}px">
            <g:each in="${(1..cpuCount)}" >
                <g:img file="CPU.png"/>
            </g:each >
        </td>
        <td class="instanceStatus" style="width: 50px;"><g:img file="${instance.status == "ACTIVE"? "Green-Monster.png": "Orange-Monster.png"}"/></td>
    </tr>
</g:each>
</table>