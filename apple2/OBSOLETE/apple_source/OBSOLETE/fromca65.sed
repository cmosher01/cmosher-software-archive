#!/usr/bin/sed -f
s/\.IF *\(.*\)/ifelse(eval(\1),1,`/
s/\.ELSEIF/.IFELSE!!!!!!!!!!!!!!!!!!!!!!!! NEED TO ADD AN EXTRA ENDIF !!!!!!!/
s/\.ELSE/',`/
s/\.ENDIF/')/
s/\.RES *\([^ ]*\)/ASM_RES(\1)/
s/\.BYTE *\([^ ]*\)/ASM_DATA(\1)/
s/\.WORD *\([^ ]*\)/ASM_DATA_W(\1)/
s/\.ADDR *\([^ ]*\)/ASM_ADDR(\1)/
s/;\(.*\)'\(.*\)/;\1\2/
