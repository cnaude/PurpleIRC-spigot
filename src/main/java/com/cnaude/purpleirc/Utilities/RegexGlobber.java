package com.cnaude.purpleirc.Utilities;

/**
 * http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
 *
 * @author cnaude
 */
public class RegexGlobber {

    /**
     *
     * @param glob
     * @return
     */
    public String createRegexFromGlob(String glob) {
        String out = "^";
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*':
                    out += ".*";
                    break;
                case '?':
                    out += '.';
                    break;
                case '.':
                    out += "\\.";
                    break;
                case '\\':
                    out += "\\\\";
                    break;
                default:
                    out += c;
            }
        }
        out += '$';
        return out;
    }
}
