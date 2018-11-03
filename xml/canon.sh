#!/bin/bash



if [ "$#" -ne 1 ] ; then
    echo "Usage:"
    echo "    $0 x.xml"
    echo "Creates canonical xml in x.canon.xml"
    exit 1
fi

dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

if [ ! -r "$dir/canon.xsl" ] ; then
    echo "Error: file not found: $dir/canon.xsl"
    exit 1
fi

if echo "$1" | grep -q '\.xml$' ; then
    f="${1/.xml/.canon.xml}"
    echo "writing to file $f" >&2
else
    echo "file name must end with .xml" >&2
    exit 1
fi


#this will sort the elements, too, which can break some syntax:
#cat "$1" | xmlstarlet c14n --without-comments - | xsltproc "$dir/canon.xsl" - | sed '/^\s*$/d' | xmllint --format - >"$f"

cat "$1" | xmlstarlet c14n --without-comments - | xmllint --format - >"$f"
