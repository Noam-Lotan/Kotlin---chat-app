import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.example.*

class Test {
    @Test
    fun registerNewUser() {
        val result = Repository.register("Alice", "password123")
        assertTrue(result)
    }

    @Test
    fun registerExistingUser() {
        val result = Repository.register("user", "user")
        assertFalse(result)

    }

}