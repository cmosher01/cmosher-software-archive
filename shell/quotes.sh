#!/bin/sh

my_function() {
    printf "'%s'\n" "$1"
    printf "'%s'\n" "$2"
}

x="a b"
y="c d"
z="$(my_function "$x" "$y")"
printf -- "-->%s<--\n" "$z"

p="$x"."$y"
printf -- "-->%s<--\n" "$p"

p="a"foo"b"
printf -- "-->%s<--\n" "$p"

p="a\"b"
printf -- "-->%s<--\n" "$p"

p='a"b'
printf -- "-->%s<--\n" "$p"

# won't work:
#p='a\'b'
#p='a'b'
p='a'"'"'b'
printf -- "-->%s<--\n" "$p"
