package node.builder

import static org.junit.Assert.*
import org.junit.Test

/**
 * @author: kellyp
 */
class UserTests
{

    @Test
    void testGet()
    {
        def u = User.get("matt")
        assertTrue u instanceof User
        assertEquals "matt@everywhere.com", u.emailAddress

    }

    @Test
    void testList()
    {
        def list = User.list()
        assertEquals 3, list.size()
        assertNotNull list.find{it.username=='matt'}
        assertNotNull list.find{it.username=='katie'}
        assertNotNull list.find{it.username=='vicky'}
    }

    @Test
    void testFindByPhone()
    {
        def u = User.findByPhone("+34923444121")
        assertEquals "katie", u.username
    }




    @Test
    void testFindAllByCountryStateCity()
    {
        def u = User.findAllByCountryAndStateAndCity("USA","MD",["Ellicott City","Olney"])
        assertEquals 2, u.size()
    }


    @Test
    void testSaveMultipleTimes()
    {
        def u = User.get("matt")
        def birthDateCounts = User.getCountsGroupByBirthDate(grain: Calendar.DAY_OF_MONTH)

        def countryCounts = User.getCountsGroupByCountry()
        def stateCounts = User.getCountsGroupByState()
        def cityCounts = User.getCountsGroupByCity()
        def mdCounts = User.getCountsByCountryAndStateGroupByCity("USA","MD")
        def emailAddressSize = User.countByEmailAddress("katie@everywhere.com")
        def phoneSize = User.findAllByPhone("+13015551212").size()

        for (i in 1..5) {
            u.save()
        }

        assertEquals birthDateCounts, User.getCountsGroupByBirthDate(grain: Calendar.DAY_OF_MONTH)

        assertEquals countryCounts, User.getCountsGroupByCountry()
        assertEquals stateCounts, User.getCountsGroupByState()
        assertEquals cityCounts, User.getCountsGroupByCity()
        assertEquals mdCounts, User.getCountsByCountryAndStateGroupByCity("USA","MD")
        assertEquals emailAddressSize,User.countByEmailAddress("katie@everywhere.com")
        assertEquals phoneSize, User.findAllByPhone("+13015551212").size()
    }
}