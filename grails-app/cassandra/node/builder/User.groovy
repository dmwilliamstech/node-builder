package node.builder

class User
{
    String username
    String emailAddress
    String firstName
    String lastName
    String address
    String city
    String state
    String zip
    String country
    String phone

    Date birthDate

//    static hasMany = [posts: Post]

    static cassandraMapping = [
            primaryKey: 'username',
            explicitIndexes: ["emailAddress", "phone", 'birthDay', ["country","state","city"]],
            secondaryIndexes: ["country", "state"],
            counters: [
                    [groupBy: ['birthDate']],
                    [groupBy: ['country','state','city']],
                    [findBy:  ['country','state'], groupBy:['city']]
            ]
    ]

    static transitents = ['birthDay']

    String getBirthDay()
    {
        birthDate?.format("MM-dd")
    }
}