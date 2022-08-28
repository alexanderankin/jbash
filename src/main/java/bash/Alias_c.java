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
        int line_len, tl, real_start, expand_next, expand_this_token;
        Alias alias;

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
        for (;;) {
            token.setCharAt(0, (char) 0);
            start = i;
            // left off at https://github.com/alexanderankin/jbash/blob/3767be5e938366ae3182d59d8d9e76455361a96a/alias.c#L506
            throw new UnsupportedOperationException("not implemented yet");
        }

    }

    private static int skip_ws(String string, int start) {
        char[] charArray = string.toCharArray();
        int i;
        for (i = start; i < charArray.length; i++) {
            if (Character.isWhitespace(charArray[i]))
                return i;
        }
        return i;
    }

    /* Helper definition for the parser */
    public static void clear_string_list_expander(Alias alias) {
        throw new UnsupportedOperationException("not yet implemented");
    }


}
