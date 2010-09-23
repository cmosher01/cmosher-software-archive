<?php
require_once 'PHPUnit/Framework.php';
require_once 'TestHelper.php';

class EnumTest extends PHPUnit_Framework_TestCase
{
	/** @test */
	public function enum_name_space()
	{
		$response = TestHelper::send_get_request("enum_name_space/sx_button_types");
		$this->assertEquals(200, $response->responseCode, "response code");
		$expected = TestHelper::query_as_xml("SELECT value, description 'label' FROM t_system_symbols where name_space = 'sx_button_types' order by label");
		$this->assertEquals($expected, $response->body);
	}
}
?>
