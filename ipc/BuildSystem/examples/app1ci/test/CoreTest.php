<?php
require_once 'PHPUnit/Framework.php';
require_once 'TestHelper.php';

/** @todo	Dynamically created database fixtures for portability */

class CoreTest extends PHPUnit_Framework_TestCase
{
	const TEST_VIEW = "TraderButton";

	/** @test */
	public function select()
	{
		$params = array("trid" => 1, "__limit" => 10, "__offset" => 0);
		$response = TestHelper::send_post_request("select", $params, self::TEST_VIEW);
		$this->assertEquals(200, $response->responseCode, "response code");
		$expected = TestHelper::query_as_xml("SELECT b.trid, button_class, button_number, button_type, personal_label, extended_label, lac, site_id, key_sequence, incoming_action, ring_tone, speed_dial_type FROM t_trader_button b, t_trader_fav_entry f WHERE b.trid = f.trid AND b.button_class = f.class AND b.button_number = f.number AND b.trid = 1 ORDER BY b.trid, button_class, button_number LIMIT 10 OFFSET 0");
		$this->assertEquals($expected, $response->body);
	}
}
?>
