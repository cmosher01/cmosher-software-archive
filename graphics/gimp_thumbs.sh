#!/bin/sh
gimp-2.2 -d -f -i -b - <<EOF

(let*
    (
        (patternFilename "append\\\\*.png")
        (outDir "thumbs")
        (fract 8)

        (rFilename (cadr (file-glob patternFilename 1)))
    )
    (while rFilename
        (let*
            (
                (filename (car rFilename))

                (fileonly (car (last (strbreakup filename "\\\\"))))
                (nameonly (car (butlast (strbreakup fileonly "."))))
                (outfile (string-append outDir "\\\\" nameonly ".png"))

                (image (car (gimp-file-load 1 filename filename)))
                (drawable (car (gimp-image-get-active-layer image)))

                (w (car (gimp-image-width image)))
                (h (car (gimp-image-height image)))
            )
            (gimp-message (string-append "file: " fileonly ", outfile: " outfile))

            (set! w (/ w fract))
            (set! h (/ h fract))
            (gimp-image-scale image w h)

            (gimp-convert-indexed image 1 0 256 0 0 "")

            (file-png-save-defaults 1 image drawable outfile outfile)

			(gimp-image-delete image)
        )
        (set! rFilename (cdr rFilename))
    )
)

(gimp-quit 0)

EOF
