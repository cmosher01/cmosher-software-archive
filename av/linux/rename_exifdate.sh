#!/bin/sh
exiftool '-FileName<CreateDate' -d %Y%m%d_%H%M%S%%-c.%%e .
