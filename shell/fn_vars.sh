#!/bin/sh
bar=a
foo() {
    echo $bar
}
bar=b
foo
bar=c
