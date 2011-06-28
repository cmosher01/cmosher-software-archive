<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="description" content="Mosher/Rapp Home Movies (1967-1978)" />
		<link rel="stylesheet" href="video.css" type="text/css" />

		<title>Mosher/Rapp Home Movies (1967-1978)</title>
	</head>
	<body>
<p>
The following is a chronological list of all the recovered films.
The videos are 640x480, 18 frames per second, progressive, color,
silent, compressed with x264 into H.264 format, and uploaded to YouTube.
Click on the image below to see the video, and a brief description.
</p>

<?php
$filmnum = array();
$filmdsc = array();
$filmyti = array();

$filmfile = fopen("films.txt","rt");
while ($lin = stream_get_line($filmfile,65536,"\n"))
{
	$filmnum[] = substr($lin,0,4);
	$filmyti[] = substr($lin,5,11);
	$filmdsc[] = substr($lin,17);
}
fclose($filmfile);

function vidlink($f)
{
	global $filmnum;
	global $filmdsc;
	global $filmyti;
	return
		"<a href=\"video.php?vidid={$filmyti[$f]}&filmnum={$filmnum[$f]}\"><img src=\"http://i3.ytimg.com/vi/{$filmyti[$f]}/default.jpg\" /></a>".
		"<br /><b>{$filmdsc[$f]}</b>";
}

?>
	<table class="film" cellspacing="16">
	<tbody>
		<tr>
			<td><?php echo vidlink(0); ?></td>
			<td><?php echo vidlink(1); ?></td>
			<td><?php echo vidlink(2); ?></td>
			<td><?php echo vidlink(3); ?></td>
			<td><?php echo vidlink(4); ?></td>
		</tr>
		<tr>
			<td><?php echo vidlink(5); ?></td>
			<td><?php echo vidlink(6); ?></td>
			<td><?php echo vidlink(7); ?></td>
			<td><?php echo vidlink(8); ?></td>
			<td><?php echo vidlink(9); ?></td>
		</tr>
		<tr>
			<td style="text-align:center;">(missing)</td>
			<td><?php echo vidlink(10); ?></td>
			<td><?php echo vidlink(11); ?></td>
			<td><?php echo vidlink(12); ?></td>
			<td><?php echo vidlink(13); ?></td>
		</tr>
		<tr>
			<td><?php echo vidlink(14); ?></td>
			<td><?php echo vidlink(15); ?></td>
			<td><?php echo vidlink(16); ?></td>
			<td><?php echo vidlink(17); ?></td>
			<td><?php echo vidlink(18); ?></td>
		</tr>
		<tr>
			<td><?php echo vidlink(19); ?></td>
			<td><?php echo vidlink(20); ?></td>
			<td><?php echo vidlink(21); ?></td>
			<td><?php echo vidlink(22); ?></td>
			<td><?php echo vidlink(23); ?></td>
		</tr>
		<tr>
			<td><?php echo vidlink(24); ?></td>
			<td><?php echo vidlink(25); ?></td>
			<td><?php echo vidlink(26); ?></td>
			<td><?php echo vidlink(27); ?></td>
			<td><?php echo vidlink(28); ?></td>
		</tr>
	</tbody>
	</table>



<!--
<object width="746" height="413"><param name="movie" value="http://www.youtube.com/cp/vjVQa1PpcFNbkiv-1QhxJRXgKX7Dz-_qB0St38_aIRM="></param>
<embed src="http://www.youtube.com/cp/vjVQa1PpcFNbkiv-1QhxJRXgKX7Dz-_qB0St38_aIRM=" type="application/x-shockwave-flash" width="746" height="413"></embed>
</object>
-->
	</body>
</html>
