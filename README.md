# Regex
Search text for regular expression patterns.

# Usage
Regex(String pattern)
PATTERN is a String representation of a regular expression pattern. Throws InvalidRegexException if
PATTERN has a syntax error.

Search(Regex r, String s)
S will be searched for the regular expression pattern contained in R.

boolean Search.find()
Returns true if a match is found, false otherwise.

String Search.getResult()
Returns the match that was found by the last invocation of FIND. If the last invocation of FIND failed
to find a match, returns null.

# Patterns
"abc" - matches "abc"
