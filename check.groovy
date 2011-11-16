/**
 * Implementation of "The Luhny Bin" coding challenge in Groovy.
 *
 * This implementation is not meant to be optimal buth rather show
 * some nice features of the Groovy language.
 *
 * @author <a href="http://twitter.com/CedricChampeau">Cedric Champeau</a>
 */

/**
 * Implement the "putAt" method on StringBuilder so that the syntax
 * stringbuilder[index] = 'X' delegates to setCharAt
 */
StringBuilder.metaClass.putAt = { index, obj ->
    delegate.setCharAt(index, (char) obj)
}

/**
 * Converts a string consisting of digits into a list of integers
 */
def toIntList(str) { str.toCharArray().collect {it - 48} }

/**
 * Implements the Luhn test on a list of integers
 * @param digits
 * @return
 */
def checkLuhn(digits) {
    // the inject method is a "reduce" operation
    // we use a tuple as parameter which has as the first
    // element a boolean telling if we are on the second element of a pair, and second parameter the current sum
    digits.reverse().inject([false, 0]) { res, u ->
        def (right, sum) = res
        sum += (right?(2 * u) % 10 + ((int) u / 5):u)
        [!right, sum]
    }[1] % 10 == 0
}

/**
 * Implements the filtering of a single line
 */
void filterLine(line) {
    // shared stringbuilder instance for every potential match on a line
    def sb = new StringBuilder()
    println line.replaceAll(/([0-9 -]+)/) { full, match ->
        sb.setLength(0)
        sb.append(match)
        def cand = match.replaceAll(/[- ]/, '')
        int len = cand.length()
        def digits = toIntList(cand)
        int offset = 0
        while (offset <= len - 14) {
            boolean found = false
            for (int k = 16; k >= 14 && !found; k--) {
                int matchOffset = offset
                // sublist operator
                def test = digits[offset..<Math.min(offset + k, len)]

                // a credit card number is found if contains enough digits
                // and passes the Luhn test
                found = test.size() >= 14 && checkLuhn(test)
                if (found) {
                    // replace as many digits as found in the Luhn test
                    int rep = 0
                    while (rep < test.size()) {
                        char c = match[matchOffset]
                        if (c.isDigit()) {
                            rep++
                            sb[matchOffset] = 'X'
                        }
                        matchOffset++
                    }
                }
                offset++
            }
        }

        // return replacement, implicit call to toString()
        sb
    }
}

// main loop
System.in.getText('US-ASCII').split(/\n/).each {
    filterLine(it)
}
