Node Builder
=========

Web based Application that reads in application definitions from files in a drop folder. Users can then mix and match 
applications and configuration data to define a deployment of nodes.  The node definitions can then be downloaded or uploaded to 
a remote server and applied to a VM image or image set.


Version
-

0.1

Usage
-

```sh
grails run-app
```

Then point your favorite browser at http://localhost:8080/node-builder/


Setup
-

Currently the app looks in ~/.opendx/node-builder for json files with application definitions.  An example of
which can be found in src/java/resources and will be installed the first time the application is started.


License
-

Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0.html
