<?php

# @todo externalize these into config file
define("BASE_URL", "http://localhost/CodeIgniter/index.php/Main");
define("RESPONSES_DIR", "responses");

define("MYSQL_DATABASE", "sxdb1");
define("MYSQL_USER", "sysview");
define("MYSQL_PASSWORD", "sysview");

class TestHelper
{
	public static function get_expected_response_body($requestUri)
	{
		return file_get_contents(RESPONSES_DIR . "/" . $requestUri . ".response");
	}

	// HTTP utilities

	/** @return	Object	response */
	public static function send_get_request($requestUri, $viewName = null)
	{
		return 	http_parse_message(http_get(self::get_url($requestUri, $viewName)));
	}

	/** @return	Object	response */
	public static function send_post_request($requestUri, $params, $viewName = null)
	{
		return 	http_parse_message(http_post_fields(self::get_url($requestUri, $viewName), $params));
	}

	public static function get_url($requestUri, $viewName)
	{
		$url = BASE_URL . "/" . $requestUri;
		if ($viewName)
		{
			$url .= "/" . $viewName;
		}
		return $url;
	}

	// Database utilities
	public static function query_as_xml($queryString)
	{
		$xml = "<?xml version=\"1.0\"?>\n<rows>\n";
		$link = mysql_connect("localhost", MYSQL_USER, MYSQL_PASSWORD);
		$result = mysql_db_query(MYSQL_DATABASE, $queryString);
		if ($result)
		{
			$numFields = mysql_num_fields($result);
			while ($row = mysql_fetch_assoc($result))
			{
				$xml .= "\t<row>\n";
				for ($i = 0; $i < $numFields; ++$i)
				{
					$name = mysql_field_name($result, $i);
					$xml .= "\t\t<" . $name . ">" . str_replace("-", "&#45;", $row[$name]) . "</" . $name . ">\n";
				}
				$xml .= "\t</row>\n";
			}
		}
		mysql_close($link);
		$xml .= "</rows>\n";
		return $xml;
	}
}
?>
