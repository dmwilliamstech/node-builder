package node.builder.validator

import org.junit.Test


class GitLocationValidatorTests {

    @Test
    void shouldDetermineInValidLocation(){
        def validator = new GitLocationValidator()

        assert !validator.isValid("git@github.com:OpenDX/notreal.git")
        assert !validator.isValid("https://notreal.com/OpenDX/notreal.git")
        assert !validator.isValid("https://github.com/OpenDX/notreal.git")
    }

    @Test
    void shouldDetermineValidLocation(){
        def validator = new GitLocationValidator()

        assert validator.isValid("git@github.com:OpenDX/node-builder.git")
        assert validator.isValid("https://github.com/OpenDX/node-builder.git")
        assert validator.isValid("https://kellyp@github.com/OpenDX/node-builder")
    }
}
