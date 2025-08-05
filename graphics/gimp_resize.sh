#!/bin/sh
gimp -d -f -i -c -b - <<EOF

(let*
    (
        (patternFilename "cropped/*.png")
        (outDir "web")

        (rFilename (cadr (file-glob patternFilename 1)))
    )
    (while rFilename
        (let*
            (
                (filename (car rFilename))

                (fileonly (car (last (strbreakup filename "/"))))
                (nameonly (car (butlast (strbreakup fileonly "."))))
                (outfile (string-append outDir "/" nameonly ".png"))

                (image (car (gimp-file-load 1 filename filename)))
                (drawable (car (gimp-image-get-active-layer image)))

                (w (car (gimp-image-width image)))
                (h (car (gimp-image-height image)))
            )

            (set! d (/ 300 h))
            (set! w (* w d))
            (gimp-message (string-append "file: " fileonly ", outfile: " outfile))
            (gimp-image-scale image w 300)

            (file-png-save-defaults 1 image drawable outfile outfile)

            (gimp-image-delete image)
        )
        (set! rFilename (cdr rFilename))
    )
)

(gimp-quit 0)

EOF
