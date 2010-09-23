<?php
require_once 'PHPUnit/Framework.php';
require_once 'TestHelper.php';

class ViewConfigTest extends PHPUnit_Framework_TestCase
{
	const TEST_VIEW = "TraderButton";

	/** @test */
    public function config_valid()
    {
		$response = TestHelper::send_get_request("config", self::TEST_VIEW);
		$this->assertEquals(200, $response->responseCode, "response code");
		$this->assertEquals(TestHelper::get_expected_response_body("config"), $response->body);
    }

	/** @test */
    public function config_invalid()
    {
		$response = TestHelper::send_get_request("config", "not-found");
		$this->assertEquals(404, $response->responseCode, "response code");
    }
}
?>
