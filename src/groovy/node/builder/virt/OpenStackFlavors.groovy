package node.builder.virt

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
public enum OpenStackFlavors {
    TINY("tiny","m1.tiny"),
    SMALL("small","m1.small"),
    MEDIUM("medium","m1.medium"),
    LARGE("large","m1.large"),
    XLARGE("xlarge","m1.xlarge")

    OpenStackFlavors(name, openstack){
        this.name = name
        this.openstack = openstack
    }

    private final String name
    private final String openstack

    public String nebula(){
        return openstack.replaceAll("m1", "n1")
    }

    static OpenStackFlavors flavorForName(String name){
        OpenStackFlavors.valueOf(name.replaceAll(/\w1\./, '').toUpperCase())
    }
}