import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.example.*

class RepositoryTest {
    @Test
    fun testDefaultUsersExist() {
        // Given - Repository starts with default users

        // When & Then - check default users can login
        assertTrue(Repository.validate("admin", "password"))
        assertTrue(Repository.validate("user", "user"))
    }

    @Test
    fun testRegisterNewUser() {
        // Given - a new username
        val username = "newuser"
        val password = "newpass"

        // When - register the user
        val success = Repository.register(username, password)

        // Then - registration should succeed and user should be able to login
        assertTrue(success)
        assertTrue(Repository.validate(username, password))
    }

    @Test
    fun testCannotRegisterSameUserTwice() {
        // Given - register a user first time
        val username = "testuser"
        Repository.register(username, "password1")

        // When - try to register same username again
        val success = Repository.register(username, "password2")

        // Then - second registration should fail
        assertFalse(success)

        // And - original password should still work
        assertTrue(Repository.validate(username, "password1"))
        assertFalse(Repository.validate(username, "password2"))
    }

    @Test
    fun testWrongPasswordFailsValidation() {
        // Given - a user exists
        Repository.register("testuser2", "correctpass")

        // When & Then - wrong password should fail
        assertFalse(Repository.validate("testuser2", "wrongpass"))
        assertFalse(Repository.validate("testuser2", ""))
    }

    @Test
    fun testNonexistentUserFailsValidation() {
        // When & Then - user that doesn't exist should fail validation
        assertFalse(Repository.validate("doesnotexist", "anypassword"))
    }

    @Test
    fun testValidLoginCredentials() {
        // Given - default users exist (admin/password and user/user)

        // When - try to validate with correct credentials
        val adminLoginSuccess = Repository.validate("admin", "password")
        val userLoginSuccess = Repository.validate("user", "user")

        // Then - both should succeed
        assertTrue(adminLoginSuccess)
        assertTrue(userLoginSuccess)
    }

    @Test
    fun testInvalidLoginCredentials() {
        // Given - we know admin exists with password "password"

        // When - try wrong passwords
        val wrongPassword = Repository.validate("admin", "wrongpassword")
        val emptyPassword = Repository.validate("admin", "")
        val wrongUser = Repository.validate("nonexistentuser", "password")

        // Then - all should fail
        assertFalse(wrongPassword)
        assertFalse(emptyPassword)
        assertFalse(wrongUser)
    }

    @Test
    fun testLoginAfterRegistration() {
        // Given - register a new user
        val username = "logintest"
        val password = "testpass123"
        val registrationSuccess = Repository.register(username, password)

        // When - try to login with the new user
        val loginSuccess = Repository.validate(username, password)

        // Then - registration should work and login should work
        assertTrue(registrationSuccess)
        assertTrue(loginSuccess)
    }
}