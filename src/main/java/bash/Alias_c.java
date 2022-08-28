package bash;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Alias_c {
    public static final int ALIAS_HASH_BUCKETS = 64;

    @Accessors(chain = true)
    @Data
    public static class Alias {
        String name;
        String value;
        byte flags;
    }

    /* Values for `flags' member of struct alias. */
    public static final int AL_EXPANDNEXT = 0x1;
    public static final int AL_BEINGEXPANDED = 0x2;

    /* The list of known aliases. */
    public static final HashMap<String, Alias> aliases = new HashMap<>();

    // here for completeness, not necessary
    public static void initializeAliases() {
    }


    /* Scan the list of aliases looking for one with NAME.  Return NULL
       if the alias doesn't exist, else a pointer to the alias. */
    public static Alias find_alias(String name) {
        return aliases.get(name);
    }

    /* Return the value of the alias for NAME, or NULL if there is none. */
    public static String get_alias_value(String name) {
        return Optional.ofNullable(find_alias(name)).map(Alias::getName).orElse(null);
    }

    /* Make a new alias from NAME and VALUE.  If NAME can be found,
       then replace its value. */
    public static void add_alias(String name, String value) {
        aliases.put(name,
                new Alias()
                        .setName(name)
                        .setValue(value)
                        .setFlags((byte) AL_EXPANDNEXT));
    }

    /* Remove the alias with name NAME from the alias list.  Returns
       the index of the removed alias, or -1 if the alias didn't exist.
       returns boolean in java version because no need for numeric indexes.
     */
    public static boolean remove_alias(String name) {
        return aliases.remove(name) != null;
    }

    /* Remove all aliases. */
    public static void delete_all_aliases() {
        aliases.clear();
    }

    /* Return an array of all defined aliases. */
    public static List<Alias> all_aliases() {
        return new ArrayList<>(aliases.values());
    }

    /* Expand a single word for aliases. */
    @SuppressWarnings("unused")
    private static String alias_expand_word(String name) {
        return get_alias_value(name);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static int command_word;

    /* Return a new line, with any aliases expanded. */
    public static String alias_expand(String string) {
        int i, j, start;
        StringBuilder line, token;
        int line_len, tl, real_start, expand_next, expand_this_token = 0;
        Alias alias;
        char[] charArray = string.toCharArray();

        line_len = string.length() + 1;
        line = new StringBuilder(line_len);
        token = new StringBuilder(line_len);

        i = 0;
        line.setCharAt(0, (char) 0);
        expand_next = 0;
        command_word = 1;  /* initialized to expand the first word on the line */


        /*
            Each time through the loop we find the next word in line.  If it
            has an alias, substitute the alias value.  If the value ends in ` ',
            then try again with the next word.  Else, if there is no value, or if
            the value does not end in space, we are done.
         */
        for (; ; ) {
            token.setCharAt(0, (char) 0);
            start = i;
            i = skip_ws(charArray, i);

            if (start == i && charArray.length == 0) {
                return line.toString();
            }

            // copy the just-skipped characters into the output string,
            // expanding it if there is not enough room.
            j = charArray.length;
            tl = i - start;	/* number of characters just skipped */
            // strncpy (line + j, string + start, tl);
            // line[j + tl] = '\0';

            real_start = i;

            // command_word = command_word || (command_separator (string[i]));
            // expand_this_token = (command_word || expand_next);
            expand_next = 0;
            /* Read the next token, and copy it into TOKEN. */
            start = i;
            // i = rd_token (string, start);

            tl = i - start;	/* token length */

            /*
                If tl == 0, but we're not at the end of the string, then we have a
                single-character token, probably a delimiter
            */
            if (tl == 0 && string.charAt(i) != '\0')
            {
                tl = 1;
                i++;		/* move past it */
            }

            // strncpy (token, string + start, tl);
            token.setLength(tl);

            /*
                If there is a backslash-escaped character quoted in TOKEN,
                then we don't do alias expansion.  This should check for all
                other quoting characters, too.
            */
            // if (mbschr(token, '\\'))
                expand_this_token = 0;

            /*
                If we should be expanding here, if we are expanding all words, or if
                we are in a location in the string where an expansion is supposed to
                take place, see if this word has a substitution.  If it does, then do
                the expansion.  Note that we defer the alias value lookup until we
                are sure we are expanding this token.
            */
            if (token.charAt(0) != 0 &&
                    // (expand_this_token != 0 || alias_expand_all) &&
                    (null != (alias = find_alias(token.toString()))))
            {
                char []v;
                int vlen, llen;

                v = alias.getValue().toCharArray();
                vlen = v.length;
                llen = line.length();

                /* +3 because we possibly add one more character below. */
                // RESIZE_MALLOCED_BUFFER (line, llen, (vlen + 3), line_len, (vlen + 50));

                // strcpy (line + llen, v);

                // if ((expand_this_token && vlen && whitespace (v[vlen - 1])) ||
                //         alias_expand_all)
                    expand_next = 1;
            }
            else
            {
                int llen, tlen;

                llen = line.length();
                tlen = i - real_start; /* tlen == strlen(token) */

                // RESIZE_MALLOCED_BUFFER (line, llen, (tlen + 1), line_len, (llen + tlen + 50));

                // strncpy (line + llen, string + real_start, tlen);
                line.setLength(llen + tlen);
                // line[llen + tlen] = '\0';
            }
            command_word = 0;
        }
    }

    static int skip_ws(char[] string, int start) {
        int i;
        boolean pass_next = false, backslash_quoted_word = false;
        char peekc;

        for (i = start; i < string.length; i++) {
            if (pass_next) {
                pass_next = false;
                continue;
            }
            if (Character.isWhitespace(string[i])) {
                // we are no longer in a backslash-quoted word
                backslash_quoted_word = false;
                continue;
            }

            if ('\\' == string[i]) {
                peekc = i + 1 >= string.length ? 0 : string[i + 1];
                if (peekc == 0) break;
                if (Character.isLetter(peekc)) {
                    // this is a backslash-quoted word
                    backslash_quoted_word = true;
                } else {
                    pass_next = true;
                }
            }

            /*
                This only handles single pairs of non-escaped quotes.  This
                overloads backslash_quoted_word to also mean that a word like
                ""f is being scanned, so that the quotes will inhibit any expansion
                of the word.
            */
            if ('\'' == string[i] || '"' == string[i]) {
                i = skipQuotes(string, i);
            }
        }
        return i;
    }

    /**
     * Consume a quoted string from STRING, starting at string[START] (so
     * string[START] is the opening quote character), and return the index
     * of the closing quote character matching the opening quote character.
     * This handles single matching pairs of unquoted quotes; it could afford
     * to be a little smarter... This skips words between balanced pairs of
     * quotes, words where the first character is quoted with a `\', and other
     * backslash-escaped characters.
     *
     * @param string input
     * @param i      position of opening quote
     * @return position of closing quote
     */
    static int skipQuotes(char[] string, int i) {
        char openingQuote = string[i];
        /*
            'i' starts at START + 1 because string[START] is the opening quote
            character.
        */
        i += 1;
        for (; i < string.length; i++) {
            if (string[i] == '\\') {
                i++;
                continue;
            }
            if (string[i] == openingQuote)
                return i;
        }
        return i;
    }

    private static boolean quote_char(char c) {
        return (((c) == '\'') || ((c) == '"'));
    }

    /* Helper definition for the parser */
    public static void clear_string_list_expander(Alias alias) {
        throw new UnsupportedOperationException("not yet implemented");
    }


}
