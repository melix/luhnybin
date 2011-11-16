/**
 * Implementation of "The Luhny Bin" coding challenge in Groovy.
 *
 * This implementation is not meant to be optimal buth rather show
 * some nice features of the Groovy language.
 *
 * @author <a href="http://twitter.com/CedricChampeau">Cedric Champeau</a>
 */

/**
 * Implements the Luhn test on a string of digits
 * @param digits
 * @return
 */
def checkLuhn = { digits ->
    // the inject method is a "reduce" operation
    // we use a tuple as parameter which has as the first
    // element a boolean telling if we are on the second element of a pair, and second parameter the current sum
    digits.reverse().inject([false, 0]) { res, u ->
        def (right, sum) = res
        u = ((char) u)-48
        sum += (right?(2 * u) % 10 + ((int) u / 5):u)
        [!right, sum]
    }[1] % 10 == 0
}.memoizeAtMost(10)

/**
 * Implements the filtering of a single line
 */
def filterLine = { line ->
    // shared stringbuilder instance for every potential match on a line
    def sb = new StringBuilder()
    println line.replaceAll(/([0-9 -]+)/) { full, match ->
        char x = 'X'
        sb.length = 0
        sb << match
        def digits = match.replaceAll(/[- ]/, '')
        int len = digits.length()
        int offset = 0
        boolean found
        while (offset <= len - 14) {
            found = false
            for (int k = 16; k >= 14 && !found; k--) {
                int matchOffset = offset
                // sublist operator
                def test = digits[offset..<Math.min(offset + k, len)]

                // a credit card number is found if contains enough digits
                // and passes the Luhn test
                int size = test.size()
                found = size >= 14 && checkLuhn(test)
                if (found) {
                    // replace as many digits as found in the Luhn test
                    int rep = 0
                    while (rep < size) {
                        char c = match.charAt(matchOffset)
                        if (c.isDigit()) {
                            rep++
                            sb.setCharAt(matchOffset, x)
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
System.in.eachLine('US-ASCII') {
    filterLine(it)
}
