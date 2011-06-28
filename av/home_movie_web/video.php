<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
        <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>Mosher/Rapp Home Movies (1967-1978)</title>
		<link href="video.css" rel="stylesheet" type="text/css" />
        </head>
        <body>
<p>
<object width="660" height="525">
        <param name="movie" value="http://www.youtube-nocookie.com/v/<?php echo $_GET['vidid']; ?>&hl=en&fs=1&rel=0&color1=0x006699&color2=0x54abd6&border=1" />
        <param name="allowFullScreen" value="true" />
        <param name="allowscriptaccess" value="always" />
        <embed
                src="http://www.youtube-nocookie.com/v/<?php echo $_GET['vidid']; ?>&hl=en&fs=1&rel=0&color1=0x006699&color2=0x54abd6&border=1"
                type="application/x-shockwave-flash"
                allowscriptaccess="always"
                allowfullscreen="true"
                width="660"
                height="525"
        />
</object>
<br />
Click the movie to play it. You can drag the slider to skip around in the movie.
</p>



<table border="1" class="scene">
<caption>Scenes</caption>
<thead>
<tr><th>Date</th><th>Theme</th><th>People</th><th>Location</th></tr>
</thead>
<tbody>
<?php

define("STATE_START",0);
define("STATE_FILM",1);
define("STATE_END",2);

$chron = fopen("chron.txt","rt");
$state = STATE_START;
while (($lin = stream_get_line($chron,65536,"\n")) && $state != STATE_END)
{
	$lin = trim($lin);
	if ($lin[0] == '#')
	{
		continue;
	}

	if ($state == STATE_START)
	{
		if ($lin[0] == '*' && substr($lin,-4) == $_GET['filmnum'])
		{
			$state = STATE_FILM;
		}
	}
	else if ($state == STATE_FILM)
	{
		if ($lin[0] == '*')
			$state = STATE_END;
		else
		{
			ereg("([^,]*),([^,]*),([^,]*),?([^,]*)",$lin,$regs);

			if ($regs[3] == "WH")
				$regs[3] = "Windsor Hills";
			else if ($regs[3] == "NM")
				$regs[3] = "Normandy Manor";
			else if ($regs[3] == "Shelb" || $regs[3] == "Shelb.")
				$regs[3] = "Shelburne";

			echo "<tr><td>{$regs[4]}</td><td>{$regs[1]}</td><td>{$regs[2]}</td><td>{$regs[3]}</td></tr>\n";
		}
	}
}
fclose($chron);

?>
</tbody>
</table>




        </body>
</html>
