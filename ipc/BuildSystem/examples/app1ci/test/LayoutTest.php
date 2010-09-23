<?php
require_once 'PHPUnit/Framework.php';
require_once 'TestHelper.php';

class LayoutTest extends PHPUnit_Framework_TestCase
{
	const TEST_VIEW = "TraderButton";

	/** @test */
    public function layout_valid()
    {
		$response = TestHelper::send_get_request("layout", self::TEST_VIEW);
		$this->assertEquals(200, $response->responseCode, "response code");
		$this->assertEquals(TestHelper::get_expected_response_body("layout"), $response->body);
    }

	/** @test */
    public function layout_invalid()
    {
		$response = TestHelper::send_get_request("layout", "not-found");
		$this->assertEquals(404, $response->responseCode, "response code");
    }
}
?>
