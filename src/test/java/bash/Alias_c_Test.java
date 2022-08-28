package bash;

import org.junit.jupiter.api.Test;

import static bash.Alias_c.skipQuotes;
import static org.junit.jupiter.api.Assertions.*;

class AliasCTest {

    @Test
    void test_skipQuotes() {
        assertEquals(4, skipQuotes("'abc'".toCharArray(), 0));
        assertEquals(4, skipQuotes("'abc' def".toCharArray(), 0));
        assertEquals(4, skipQuotes("'a\\'' def".toCharArray(), 0));
        String unterminated = "'a\\' def";
        assertEquals(unterminated.length(), skipQuotes(unterminated.toCharArray(), 0));
    }

}
