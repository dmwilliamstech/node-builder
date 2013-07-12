package node.builder


class Utilities {
    def grailsApplication

    def isSerializable(object){
        try{
            if(object.class?.name =~ /javassist/ && object.class?.name =~ /node\.builder/)
                return false
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(object);
            return true
        }catch(e){
            e.printStackTrace()
            return false
        }
    }

    def serializeDomain(domain, level = 0){
        if(domain == null || level > 1)
            return null

        def domainClass = grailsApplication.getDomainClass(domain.class.name.replaceAll(/\_\$\$\_javassist.*/, ""))

        def map = new HashMap()
        if(domainClass == null)
            return map


        domainClass.persistentProperties.each { property ->
            if(domain[property.name] == null || isSerializable(domain[property.name])){
                map[property.name.toString()] = domain[property.name].toString()
            }else if(domain[property.name] instanceof java.util.LinkedHashSet || domain[property.name] instanceof org.hibernate.collection.PersistentSet){
                def list = []
                domain[property.name].each{ d ->
                    if(isSerializable(d)){
                        list.add(d)
                    }else{
                        def add = serializeDomain(d, level + 1)
                        list.add( add)
                    }
                    return list
                }
                map[property.name.toString()] = list
            }else{
                map[property.name.toString()] = serializeDomain(domain[property.name], level + 1)
            }
        }

        map.id = domain.id

        return map
    }
}
