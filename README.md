# Regex
Search text for regular expression patterns.

## Usage

### Regex(String pattern)
Constructor creates a Regex object where PATTERN is a String representation of a regular expression pattern. Throws InvalidRegexException if PATTERN has a syntax error.

### Search(Regex reg, String text)
Constructor creates a Search object where TEXT will be searched for the regular expression pattern contained in REG.

### boolean Search.find()
Returns true if a match is found, false otherwise.

### String Search.getResult()
Returns the match that was found by the last invocation of FIND. If the last invocation of FIND failed to find a match, returns null.

## Patterns
"abc" - matches "abc"

To match a special character, escape it with "\\"

"\0n" matches character with octal value 0n

"\0nn" matches character with octal value 0nn

"\0mnn" matches character with octal value 0mnn, where 0<= m <= 3

"\xhh" matches character with hexadecimal value 0xhh

"\x{h...h} matches character with hexadecimal value 0xh...h from 0x000000 to 0x10FFFF

"\uhhhh" matches character with hexadecimal value 0xhhhh

"\cx" matches the control character x where x is A-Z or a-z

"\t" matches tab

"\n" matches newline

"\r" matches carriage-return

"\f" matches form-feed

"\a" matches alert (bell)

"\e" matches escape

"\Q"..."\E" treats all enclosed characters as literals
