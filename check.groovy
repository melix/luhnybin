/**
 * Implementation of "The Luhny Bin" coding challenge in Groovy.
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
    boolean right = false
    digits.reverse().inject(0) { sum, u ->
        int x = ((char) u)-48
        sum += (right?(x*2) % 10 + (x.intdiv(5)):x)
        right = !right

        sum
    } % 10 == 0
}.memoizeAtMost(10)

/**
 * Implements the filtering of a single line
 */
def filterLine = { line ->
    // shared stringbuilder instance for every potential match on a line
    def sb = new StringBuilder()
    println line.replaceAll(/([0-9 -]{14,})/) { full, match ->
        char x = 'X'
        sb.length = 0
        sb << match
        def digits = match.replaceAll(/[- ]/, '')
        int len = digits.length()
        int limit = len-14
        int offset = 0
        while (offset <= limit) {
            (16..14).each { k->
                // sublist operator
                def test = digits[offset..<Math.min(offset + k, len)]

                // a credit card number is found if contains enough digits
                // and passes the Luhn test
                int size = test.size()
                if (size >= 14 && checkLuhn(test)) {
                    // replace as many digits as found in the Luhn test
                    int rep = 0
                    int matchOffset = offset
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
System.in.eachLine('US-ASCII', filterLine)
